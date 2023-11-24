package ru.hbb.Network;

public enum StandardMessage {

    NEW_PLAYER("NEW_PLAYER:%s:%s"), // name:mac
    TRY_APPLY_CONNECT("TRY_APPLY_CONNECT:%s:%s"), // UUID:name
    NEW_ENCRYPT_KEY("NEW_ENCRYPT_KEY:%s:%s:%s:%s"), // key:transformation:algorithm:key_len
    NEW_UUID("NEW_UUID:%s"), // UUID
    NEW_BUKKIT_CONNECTION("NEW_BUKKIT_CONNECTION:%s"), // ID
    APPLY_REGISTRATION("APPLY_REGISTRATION:%s"), // uuid
    KICK_PLAYER("KICK_PLAYER:%s"), // uuid
    PLAYER_DISCONNECT("PLAYER_DISCONNECT:%s"), // uuid
    SEND_NOTIFICATION("SEND_NOTIFICATION:%s:%s"), // type:message
    GET_MAC_ADDRESS("GET_MAC_ADDRESS"),
    DEATHWATCH_COMMAND("DEATHWATCH_COMMAND:%s"), // commands to bukkit
    USER_BAN("USER_BAN:%s"), // reason
    CHECK_USER_BANS("CHECK_USER_BANS:%s") // side {all, server_id}
    ;

    private final String message;

    StandardMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
