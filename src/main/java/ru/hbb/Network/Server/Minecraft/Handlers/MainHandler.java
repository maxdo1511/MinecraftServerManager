package ru.hbb.Network.Server.Minecraft.Handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.hbb.Console.Logger.SimpleLogger;
import ru.hbb.Network.MessageCryptStatus;
import ru.hbb.Network.NetworkManager;
import ru.hbb.Network.StandardMessage;
import ru.hbb.Network.Utils.BufferReader;
import ru.hbb.Network.MessageDist;
import ru.hbb.PlayerManager;

public class MainHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext context){
        SimpleLogger.log("New Client connection: " + context.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg){
        String s_data = BufferReader.read(msg);
        System.out.println(s_data);
        String[] info = s_data.split("/-/", 2)[0].split(":");
        String[] data = s_data.split("/-/", 2)[1].split(":");
        MessageDist messageDist = MessageDist.valueOf(info[0]);
        switch (messageDist) {
            case TO_SERVER:
                StandardMessage messageType = StandardMessage.valueOf(data[0]);
                String name = "";
                switch (messageType){
                    case NEW_PLAYER:
                        name = data[1];
                        String mac = data[2];
                        NetworkManager.getInstance().
                                tryRegisterNewClient(context.channel(), name, mac);
                        break;
                    case CHECK_USER_BANS:
                        name = data[1];
                        if (PlayerManager.getInstance().hasBan(name)) {
                            NetworkManager.getInstance().sendMessageFromServer(context.channel(), String.format(StandardMessage.USER_BAN.getMessage(), "You banned!"), MessageDist.TO_MINECRAFT_CLIENT);
                        }
                        break;
                }

                break;
            case TO_BUKKIT_SERVER:
                NetworkManager.getInstance().reselDecryptedMessageToBukkit(context.channel(), s_data, MessageCryptStatus.valueOf(info[1]));
                break;
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
