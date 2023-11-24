package ru.hbb.Utills;

public enum FileName {

    NET_CONFIG("net_config.json"),
    CRYPT_CONFIG("crypt_config.json"),
    USER_DATA("user_data.json");

    public final String name;

    FileName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        return name.split("\\.")[0];
    }
}
