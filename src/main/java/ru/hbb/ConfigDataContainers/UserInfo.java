package ru.hbb.ConfigDataContainers;

public class UserInfo {

    private String name;
    private String uuid;
    private String ip;
    private String mac;
    private boolean has_ban;
    private String active_ban;

    public UserInfo() {
    }

    public UserInfo(String name, String uuid, String ip, String mac, boolean has_ban, String active_ban) {
        this.name = name;
        this.uuid = uuid;
        this.ip = ip;
        this.mac = mac;
        this.has_ban = has_ban;
        this.active_ban = active_ban;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setHas_ban(boolean has_ban) {
        this.has_ban = has_ban;
    }

    public void setActive_ban(String active_ban) {
        this.active_ban = active_ban;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public boolean isHas_ban() {
        return has_ban;
    }

    public String getActive_ban() {
        return active_ban;
    }
}
