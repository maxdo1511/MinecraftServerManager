package ru.hbb.Console.Commands.ExecutableCommands;

import ru.hbb.Console.Commands.ConsoleListener;
import ru.hbb.Console.Commands.ExecutableCommand;
import ru.hbb.Console.Logger.SimpleLogger;
import ru.hbb.Network.NetworkManager;
import ru.hbb.Network.StandardMessage;

public class CommandBukkit {

    @ExecutableCommand
    public static void execute(String[] args) {
        try {
            if (args.length < 2) {
                ConsoleListener.execute("help");
                return;
            }
            if (args[1].equalsIgnoreCase("kick")) {
                String uuid = NetworkManager.getInstance().getPlayerUUID(args[2]);
                NetworkManager.getInstance().sendMessageFromServerToBukkit(args[0], String.format(StandardMessage.KICK_PLAYER.getMessage(), uuid));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
