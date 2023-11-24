package ru.hbb.Console.Commands.ExecutableCommands;

import ru.hbb.Console.Commands.ExecutableCommand;

public class CommandHelp {

    @ExecutableCommand
    public static void execute(String[] args) {
        System.out.println("========== HELP ===========");
        System.out.println("help - to help");
        System.out.println("stop - to stop server");
        System.out.println("restart {server} - to restart connection (if no args, restart all servers)");
        System.out.println("bukkit {server} {command/restart/stop/kick/kick-all} {args} - some action on server bukkit");
        System.out.println("client {ban/banhw/banip/kick/kick-all/send} {name} {send: notification/chat/post} {args}");
        System.out.println("===========================");
    }

}
