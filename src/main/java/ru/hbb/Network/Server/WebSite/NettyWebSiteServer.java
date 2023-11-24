package ru.hbb.Network.Server.WebSite;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import ru.hbb.Console.Logger.SimpleLogger;
import ru.hbb.Network.NetworkManager;
import ru.hbb.Network.Server.Server;
import ru.hbb.Network.Server.WebSite.Handlers.MainHandler;

import java.net.InetSocketAddress;

public final class NettyWebSiteServer implements Server {

    public static NettyWebSiteServer instance;
    private EventLoopGroup connect_group;
    private EventLoopGroup handlers;
    private SocketChannel channel;
    private String name;
    private String ip;
    private int port, connection_threads, handler_threads;

    public NettyWebSiteServer(String name){
        instance = this;
        this.name = name;
    }

    @Override
    public boolean start(String ip, int port, int connection_threads, int handler_threads) {
        this.ip = ip;
        this.port = port;
        this.connection_threads = connection_threads;
        this.handler_threads = handler_threads;
        try {
            new Thread(() -> {
                run(ip, port, connection_threads, handler_threads);
            }).start();
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean stop(){
        try {
            connect_group.shutdownGracefully();
            handlers.shutdownGracefully();
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean restart() {
        if (!stop()) {
            return false;
        }
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            run(ip, port, connection_threads, handler_threads);
        }).start();
        return true;
    }

    @Override
    public void run(String ip, int port, int connection_threads, int handler_threads) {
        connect_group = new NioEventLoopGroup(connection_threads);
        handlers = new NioEventLoopGroup(handler_threads);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(connect_group, handlers).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    getInstance().channel = ch;
                    ByteBuf byteBuf = Unpooled.copiedBuffer(NetworkManager.DELIMITER.getBytes());
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, byteBuf));
                    ch.pipeline().addLast(new MainHandler(), new StringEncoder(CharsetUtil.UTF_8), new StringDecoder(CharsetUtil.UTF_8));
                }
            });
            ChannelFuture future = b.bind(new InetSocketAddress(ip, port)).sync();
            SimpleLogger.log("Website server start! Using ip:" + future.channel().localAddress());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            SimpleLogger.error("Error to start server!");
            connect_group.shutdownGracefully();
            handlers.shutdownGracefully();
        }
    }

    public static NettyWebSiteServer getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return name;
    }
}
