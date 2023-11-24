package ru.hbb.Console.Commands.ExecutableCommands;

import ru.hbb.Console.Commands.ExecutableCommand;
import ru.hbb.Main;

public class CommandStop {

    @ExecutableCommand
    public static void stopCommand(String[] args) {
        Main.stop();
    }

}
