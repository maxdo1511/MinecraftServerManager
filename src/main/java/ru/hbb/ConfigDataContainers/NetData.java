package ru.hbb.ConfigDataContainers;

public class NetData {

    private String connection_id;
    private String ip;
    private int port;
    private int connection_threads;
    private int handler_threads;

    public NetData() {
    }

    public NetData(String connection_id, String ip, int port, int connection_threads, int handler_threads) {
        this.connection_id = connection_id;
        this.ip = ip;
        this.port = port;
        this.connection_threads = connection_threads;
        this.handler_threads = handler_threads;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getConnection_id() {
        return connection_id;
    }

    public int getConnection_threads() {
        return connection_threads;
    }

    public int getHandler_threads() {
        return handler_threads;
    }
}
