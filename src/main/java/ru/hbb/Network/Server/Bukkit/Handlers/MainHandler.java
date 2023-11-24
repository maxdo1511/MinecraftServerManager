package ru.hbb.Network.Server.Bukkit.Handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.hbb.Console.Logger.SimpleLogger;
import ru.hbb.Network.*;
import ru.hbb.Network.Utils.BufferReader;

public class MainHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext context){
        SimpleLogger.log("New Bukkit connection: " + context.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg){
        try {
            String s_data = BufferReader.read(msg);
            System.out.println(s_data);
            String[] info = s_data.split("/-/", 2)[0].split(":");
            String[] data = s_data.split("/-/", 2)[1].split(":");
            MessageDist dist = MessageDist.valueOf(info[0]);
            switch (dist){
                case TO_SERVER:
                    //TODO make decrypting
                    if (info[1].equals(MessageCryptStatus.ENCRYPTED.toString())) {
                        return;
                    }
                    if (info[2].equals(MessageType.OBJECT.toString())) {
                        return;
                    }
                    if (info[2].equals(MessageType.STRING.toString())) {
                        StandardMessage standardMessage = StandardMessage.valueOf(data[0]);
                        switch (standardMessage) {
                            case NEW_BUKKIT_CONNECTION:
                                NetworkManager.getInstance().registerBukkitServer(data[1], context.channel());
                                break;
                            case APPLY_REGISTRATION:
                                NetworkManager.getInstance().applyRegisterNewClient(data[1], context.channel());
                                break;
                            case PLAYER_DISCONNECT:
                                NetworkManager.getInstance().unregisterClient(data[1]);
                                break;
                        }
                    }
                    return;
                case TO_MINECRAFT_CLIENT:
                    String uuid = info[3];
                    NetworkManager.getInstance().reselFullMessageToClient(uuid, s_data.split("/-/", 2)[1], info[2]);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        NetworkManager.getInstance().channelDisconnect(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause){
        NetworkManager.getInstance().channelDisconnect(context.channel());
    }

}
