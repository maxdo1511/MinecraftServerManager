package ru.hbb.Console.Commands;

import ru.hbb.Console.Commands.ExecutableCommands.*;
import ru.hbb.Console.Logger.SimpleLogger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleListener {

    public ConsoleListener() {
        createNewListener();
        registerAll();
    }
    private static Map<String, Class> registeredCommands = new HashMap<>();

    private void createNewListener() {
        new Thread(() -> {
            while (true) {
                Scanner in = new Scanner(System.in);
                String line = in.nextLine();
                execute(line);
            }
        }).start();
    }

    public static void execute(String command) {
        try {
            String[] args = command.split(" ");
            Class c = registeredCommands.get(args[0].toLowerCase());
            for (Method method : c.getDeclaredMethods()) {
                if (method.isAnnotationPresent(ExecutableCommand.class)) {
                    String[] nArgs = new String[]{};
                    if (args.length > 1) {
                        nArgs = Arrays.copyOfRange(args, 1, args.length);
                    }
                    try {
                        method.invoke(c, (Object) nArgs);
                    } catch (Exception e) {
                        e.printStackTrace();
                        SimpleLogger.error("Error to execute command " + args[0]);
                    }
                }
            }
        }catch (Exception e) {
            CommandHelp.execute(new String[]{});
        }
    }

    public void register(String id, Class clazz) {
        registeredCommands.put(id, clazz);
    }

    private void registerAll() {
        register("stop", CommandStop.class);
        register("restart", CommandRestart.class);
        register("help", CommandHelp.class);
        register("bukkit", CommandBukkit.class);
        register("client", CommandClient.class);
    }
}
