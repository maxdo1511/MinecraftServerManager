package ru.hbb.Network;

public enum MessageDist {

    TO_MINECRAFT_CLIENT(0),
    FROM_MINECRAFT_CLIENT(0),
    TO_SERVER(-1),
    FROM_SERVER(-1),
    TO_BUKKIT_SERVER(1),
    FROM_BUKKIT_SERVER(1);

    public final int id;

    MessageDist(int id) {
        this.id = id;
    }
}
