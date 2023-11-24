package ru.hbb.Network.PacketSystem.CustomPackets;

import ru.hbb.Network.PacketSystem.CustomPacket;
import ru.hbb.Network.PacketSystem.SerializeField;

public class PlayerData implements CustomPacket {

    @SerializeField()
    public int coins;

    public PlayerData() {
    }

    public PlayerData(int coins) {
        this.coins = coins;
    }

    public int getCoins() {
        return coins;
    }
}
