package ru.hbb.Network.Server.WebSite.Handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.hbb.Console.Logger.SimpleLogger;
import ru.hbb.Network.Utils.BufferReader;

public class MainHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext context){
        SimpleLogger.log("New Site connection: " + context.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg){
        String data = BufferReader.read(msg);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        ctx.fireChannelUnregistered();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause){
        cause.printStackTrace();
    }

}
