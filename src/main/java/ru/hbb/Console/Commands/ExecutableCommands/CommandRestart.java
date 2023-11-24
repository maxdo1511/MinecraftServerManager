package ru.hbb.Console.Commands.ExecutableCommands;

import ru.hbb.Console.Commands.ExecutableCommand;
import ru.hbb.Network.NetworkManager;

public class CommandRestart {

    @ExecutableCommand
    public static void restartCommand(String[] args) {
        if (args.length == 0) {
            NetworkManager.getInstance().stopAll();
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    NetworkManager.getInstance().startServers();
                }catch (InterruptedException e){

                }
            }).start();
        }
        if (args.length == 1) {
            NetworkManager.getInstance().restartServer(args[0]);
        }
    }

}
