package ru.hbb;

import ru.hbb.Console.Commands.ConsoleListener;
import ru.hbb.Console.Logger.SimpleLogger;
import ru.hbb.Network.NetworkManager;

public final class Main {

    public static void main(String[] args) {
        new ConsoleListener();
        new NetworkManager();
        new PlayerManager();

        NetworkManager.getInstance().startServers();
    }

    public static void stop() {
        SimpleLogger.log("Stopping!");
        PlayerManager.getInstance().save();
        NetworkManager.getInstance().stopAll();
        System.exit(0);
    }

}