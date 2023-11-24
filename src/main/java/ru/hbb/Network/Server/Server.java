package ru.hbb.Network.Server;

public interface Server {

    boolean start(String ip, int port, int connection_threads, int handler_threads);
    boolean stop();
    boolean restart();
    void run(String ip, int port, int connection_threads, int handler_threads);
    String getName();

}
