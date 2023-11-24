package ru.hbb.Console.Commands.ExecutableCommands;

import ru.hbb.ConfigDataContainers.UserInfo;
import ru.hbb.Console.Commands.ConsoleListener;
import ru.hbb.Console.Commands.ExecutableCommand;
import ru.hbb.Console.Logger.SimpleLogger;
import ru.hbb.Network.MessageDist;
import ru.hbb.Network.MessageType;
import ru.hbb.Network.NetworkManager;
import ru.hbb.Network.StandardMessage;
import ru.hbb.PlayerManager;

import java.util.Arrays;

public class CommandClient {

    @ExecutableCommand
    public static void execute(String[] args) {
        if (args.length < 2) {
            ConsoleListener.execute("help");
            return;
        }
        if (args[0].startsWith("send")) {
            if (args.length < 4) {
                ConsoleListener.execute("help");
                return;
            }
            String msg = "";
            boolean flag = true;
            for (String l : Arrays.copyOfRange(args, 3, args.length)) {
                if (flag) {
                    msg = l;
                    flag = false;
                }else {
                    msg = msg + " " + l;
                }
            }
            if (args[1].equals("all")) {
                for (String uuid : NetworkManager.getInstance().getChannels().keySet()) {
                    NetworkManager.getInstance().sendCryptMessageFromServer(
                            uuid,
                            String.format(StandardMessage.SEND_NOTIFICATION.getMessage(), args[2], msg),
                            MessageDist.TO_MINECRAFT_CLIENT,
                            MessageType.STRING
                    );
                }
            }else {
                String uuid = NetworkManager.instance.getPlayerUUID(args[1]);
                NetworkManager.getInstance().sendCryptMessageFromServer(
                        uuid,
                        String.format(StandardMessage.SEND_NOTIFICATION.getMessage(), args[2], msg),
                        MessageDist.TO_MINECRAFT_CLIENT,
                        MessageType.STRING
                );
            }
            return;
        }
        if (args[0].equalsIgnoreCase("banhw")) {
            UserInfo userInfo = PlayerManager.getInstance().getDataFromName(args[1], false);
            if (userInfo == null) {
                SimpleLogger.log("No player with this name");
                return;
            }
            String reason = "No reason";
            if (args.length > 2) {
                for (int i = 2; i < args.length; i++) {
                    reason = reason + args[i] + " ";
                }
            }
            PlayerManager.getInstance().banPlayer(userInfo);

            String uuid = NetworkManager.getInstance().getPlayerUUID(args[1]);
            if (uuid != null) {
                NetworkManager.getInstance().sendMessageFromServer(uuid, String.format(StandardMessage.USER_BAN.getMessage(), reason), MessageDist.TO_MINECRAFT_CLIENT);
            }
        }
    }

}
