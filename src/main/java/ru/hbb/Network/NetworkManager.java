package ru.hbb.Network;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import ru.hbb.ConfigDataContainers.CryptData;
import ru.hbb.ConfigDataContainers.NetData;
import ru.hbb.ConfigDataContainers.UserInfo;
import ru.hbb.Console.Logger.SimpleLogger;
import ru.hbb.Network.PacketSystem.CustomPacket;
import ru.hbb.Network.PacketSystem.PacketManager;
import ru.hbb.Network.Server.Bukkit.NettyBukkitServer;
import ru.hbb.Network.Server.Minecraft.NettyMinecraftServer;
import ru.hbb.Network.Server.WebSite.NettyWebSiteServer;
import ru.hbb.Network.Utils.*;
import ru.hbb.PlayerManager;
import ru.hbb.Utills.FileName;
import ru.hbb.Utills.FileUtils;

import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.util.*;

public final class NetworkManager {

    public static NetworkManager instance;
    /**
     * form UUID
     * index 0: minecraft client
     * index 1: bukkit server
     */
    private Map<String, String> playersAndUUIDs = new HashMap<>();
    private Map<String, Channel[]> channels = new HashMap<>();
    private Map<Channel, String> connectedClients = new HashMap<>();
    private Map<String, SecretKeySpec> encryptSecretKeys = new HashMap<>();
    private Map<String, Channel> connectedServersChannel = new HashMap<>();
    private Map<Channel, String> connectedServersId = new HashMap<>();
    private Cryptographer cryptographer;
    public static final String DELIMITER = "$_";

    public NetworkManager() {
        instance = this;

        // init cryptographer
        try {
            CryptData cryptData = (CryptData) new FileUtils(FileName.CRYPT_CONFIG.getName()).read();
            cryptographer = new Cryptographer(cryptData.getTransformation(), cryptData.getAlgorithm(), cryptData.getKey_len());
            SimpleLogger.log("cryptographer init successfully!");
        }catch (Exception e) {
            e.printStackTrace();
            SimpleLogger.error("crypt init error!");
        }
    }

    public static NetworkManager getInstance() {
        return instance;
    }

    public String getPlayerUUID(String player_name) {
        return playersAndUUIDs.get(player_name);
    }

    public String getPlayerServerId(String player_id) {
        Channel channel = channels.get(player_id)[1];
        return connectedServersId.get(channel);
    }

    /*
    Bukkit register
     */

    public void registerBukkitServer(String id, Channel channel) {
        connectedServersId.put(channel, id);
        connectedServersChannel.put(id, channel);
        sendMessageFromServerToBukkit(id, "HELLO " + id + " you connected!");
        SimpleLogger.log("Server " + id + " registered!");
    }

    /*
    Server working system
     */

    public void startServers() {
        try {
            List<NetData> netDatas = (List<NetData>) new FileUtils("net_config.json").read();
            for (NetData netData : netDatas) {
                if (!startServer(netData)) {
                    throw new RuntimeException("start server error: " + netData.getConnection_id());
                }else {
                    SimpleLogger.log("Server " + netData.getConnection_id() + " starting!");
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            SimpleLogger.error("start servers error!");
        }
    }

    public boolean startServer(NetData data) {
        if (data.getConnection_id().equals("minecraft")) {
            new NettyMinecraftServer(data.getConnection_id());
            return NettyMinecraftServer.getInstance().start(data.getIp(), data.getPort(), data.getConnection_threads(), data.getHandler_threads());
        }
        if (data.getConnection_id().equals("website")) {
            new NettyWebSiteServer(data.getConnection_id());
            return NettyWebSiteServer.getInstance().start(data.getIp(), data.getPort(), data.getConnection_threads(), data.getHandler_threads());
        }
        if (data.getConnection_id().equals("bukkit")) {
            new NettyBukkitServer(data.getConnection_id());
            return NettyBukkitServer.getInstance().start(data.getIp(), data.getPort(), data.getConnection_threads(), data.getHandler_threads());
        }
        return false;
    }

    public void stopAll() {
        NettyMinecraftServer.getInstance().stop();
        SimpleLogger.log("Server minectaft stopped!");
        NettyWebSiteServer.getInstance().stop();
        SimpleLogger.log("Server website stopped!");
        NettyBukkitServer.getInstance().stop();
        SimpleLogger.log("Server bukkit stopped!");
    }

    public void restartServer(String id) {
        SimpleLogger.log("Restarting server " + id);
        if (id.equals("minecraft")) {
            NettyMinecraftServer.getInstance().restart();
            return;
        }
        if (id.equals("website")) {
           NettyWebSiteServer.getInstance().restart();
           return;
        }
        if (id.equals("bukkit")) {
            NettyBukkitServer.getInstance().restart();
        }
    }

    /*
    Registration
     */

    public void tryRegisterNewClient(Channel channel, String name, String mac) {
        try {
            UserInfo userInfo = PlayerManager.getInstance().getDataFromName(name, true);
            String uuid = userInfo.getUuid();
            if (PlayerManager.getInstance().hasBan(name, uuid, channel.localAddress().toString(), mac)) {
                sendMessageFromServer(channel, String.format(StandardMessage.USER_BAN.getMessage(), "ban"), MessageDist.TO_MINECRAFT_CLIENT);
                return;
            }
            userInfo.setIp(channel.localAddress().toString());
            userInfo.setMac(mac);

            channels.put(uuid.toString(), new Channel[]{channel, null});
            playersAndUUIDs.put(name, uuid.toString());
            for (String id : connectedServersId.values()) {
                sendMessageFromServerToBukkit(id, String.format(StandardMessage.TRY_APPLY_CONNECT.getMessage(), uuid.toString(), name));
            }
        }catch (Exception e) {
            SimpleLogger.error("Fail to start register " + channel.id().asShortText());
            e.printStackTrace();
        }
    }

    public void applyRegisterNewClient(String id, Channel channel) {
        try {
            channels.get(id)[1] = channel;
            connectedClients.put(channels.get(id)[0], id);
            SecretKeySpec key = cryptographer.createSecretKey();
            encryptSecretKeys.put(id, key);
            sendMessageFromServer(id, String.format(StandardMessage.NEW_ENCRYPT_KEY.getMessage(),
                    cryptographer.getKeyString(key),
                    cryptographer.transformation,
                    cryptographer.algorithm,
                    cryptographer.key_len), MessageDist.TO_MINECRAFT_CLIENT);
            sendMessageFromServer(id, String.format(StandardMessage.NEW_UUID.getMessage(), id), MessageDist.TO_MINECRAFT_CLIENT);
            SimpleLogger.log("Player " + id + " successfully registered!");
        }catch (Exception e) {
            SimpleLogger.error("Fail to apply register " + channel.id().asShortText());
        }
    }

    public void unregisterClient(String id) {
        try {
            Channel channel = channels.get(id)[0];
            channel.disconnect();
            clearDisconnectedChannel(id);
            SimpleLogger.log("Client " + id + " unregistered!");
        } catch (Exception e) {
            SimpleLogger.error("Fail to unregister " + id);
        }
    }

    public void channelDisconnect(Channel channel) {
        String id = connectedClients.get(channel);
        clearDisconnectedChannel(id);
        SimpleLogger.log("Client " + id + " disconnect!");
    }

    private void clearDisconnectedChannel(String id) {
        channels.remove(id);
        encryptSecretKeys.remove(id);
        for (String name : playersAndUUIDs.keySet()) {
            if (playersAndUUIDs.get(name).equals(id)) {
                playersAndUUIDs.remove(name);
                break;
            }
        }
    }

    /*
    Messages sender

    Message format: MessageDist:MessageCryptStatus:MessageType-{message}
     */

    public void sendMessageFromServerToBukkit(String server_id, String msg) {
        String info = String.format("%s:%s:%s/-/", MessageDist.FROM_SERVER.toString(), MessageCryptStatus.NOTENCRYPTED.toString(), MessageType.STRING);
        Channel channel = connectedServersChannel.get(server_id);
        sendMessageTo(channel, info + "" + msg);
    }

    public void sendCryptMessageFromServer(String id, String msg, MessageDist to) {
        String info = String.format("%s:%s:%s/-/", MessageDist.FROM_SERVER.toString(), MessageCryptStatus.ENCRYPTED.toString(), MessageType.STRING);
        Channel channel = channels.get(id)[to.id];
        byte[] message = cryptographer.encrypt(encryptSecretKeys.get(id), msg.getBytes());
        sendMessageTo(channel, info, message);
    }

    public void sendCryptMessageFromServer(String id, String msg, MessageDist to, MessageType type) {
        String info = String.format("%s:%s:%s/-/", MessageDist.FROM_SERVER.toString(), MessageCryptStatus.ENCRYPTED.toString(), type);
        Channel channel = channels.get(id)[to.id];
        byte[] message = cryptographer.encrypt(encryptSecretKeys.get(id), msg.getBytes());
        sendMessageTo(channel, info, message);
    }


    /*
    public void sendCryptMessageFromServer(String id, String msg, MessageDist to) {
        String info = String.format("%s:%s:%s-", MessageDist.FROM_SERVER.toString(), MessageCryptStatus.ENCRYPTED.toString(), MessageType.STRING);
        Channel channel = channels.get(id)[to.id];
        String message = BufferReader.read(Unpooled.copiedBuffer(cryptographer.encrypt(encryptSecretKeys.get(id), msg.getBytes())).copy());
        sendMessageTo(channel, info + "" + message);
     */

    public void sendMessageFromServer(String id, String msg, MessageDist to) {
        String info = String.format("%s:%s:%s/-/", MessageDist.FROM_SERVER.toString(), MessageCryptStatus.NOTENCRYPTED.toString(), MessageType.STRING);
        Channel channel = channels.get(id)[to.id];
        sendMessageTo(channel, info + "" + msg);
    }

    public void sendMessageFromServer(Channel channel, String msg, MessageDist to) {
        String info = String.format("%s:%s:%s/-/", MessageDist.FROM_SERVER.toString(), MessageCryptStatus.NOTENCRYPTED.toString(), MessageType.STRING);
        sendMessageTo(channel, info + "" + msg);
    }

    public void sendSerializedMessageFromServer(String id, Object obj, MessageDist to, MessageCryptStatus encrypt) {
        String info = String.format("%s:%s:%s/-/", MessageDist.FROM_SERVER.toString(), encrypt.toString(), MessageType.OBJECT);
        Channel channel = channels.get(id)[to.id];

        String msg = PacketManager.serializeToString((CustomPacket) obj, 1);
        if (encrypt == MessageCryptStatus.ENCRYPTED) {
            sendMessageTo(channel, info, cryptographer.encrypt(encryptSecretKeys.get(id), msg.getBytes()));
            return;
        }
        sendMessageTo(channel, info + "" + msg);
    }

    public void reselFullMessageToClient(String id, String msg, String type) {
        sendCryptMessageFromServer(id, msg, MessageDist.TO_MINECRAFT_CLIENT, MessageType.valueOf(type));
    }

    public void reselDecryptedMessageToBukkit(Channel channel, String msg, MessageCryptStatus status) {
        String server = connectedServersId.get(channels.get(connectedClients.get(channel))[1]);
        if (status == MessageCryptStatus.NOTENCRYPTED) {
            sendMessageFromServerToBukkit(server, msg);
        }else {
            String uuid = connectedClients.get(channel);
            SecretKeySpec key = encryptSecretKeys.get(uuid);
            String s_obj = BufferReader.read(Unpooled.copiedBuffer(getCryptographer().decrypt(key, msg.split("/-/", 2)[1].getBytes())));
            sendMessageFromServerToBukkit(server, s_obj);
        }
    }

    @Deprecated
    private void sendMessageTo(Channel channel, String info, byte[] msg) {
        sendMessageTo(channel, info, msg, 0);
    }

    @Deprecated
    private void sendMessageTo(Channel channel, String info, byte[] msg, int i){
        new Thread(() -> {
            if (i >= 10) {
                return;
            }
            try {
                channel.write(Unpooled.copiedBuffer(info.getBytes()));
                channel.write(Unpooled.copiedBuffer(msg));
                channel.write(Unpooled.copiedBuffer(DELIMITER.getBytes()));
                channel.flush();
            } catch (Exception e) {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                }
                sendMessageTo(channel, info, msg, i + 1);
            }
        }).start();
    }

    @Deprecated
    private void sendMessageTo(Channel channel, String msg) {
        sendMessageTo(channel, msg, 0);
    }

    @Deprecated
    private void sendMessageTo(Channel channel, String msg, int i){
        new Thread(() -> {
            if (i >= 10) {
                return;
            }
            try {
                channel.writeAndFlush(Unpooled.copiedBuffer((msg + "" + DELIMITER).getBytes()));
            } catch (Exception e) {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                }
                sendMessageTo(channel, msg, i + 1);
            }
        }).start();
    }

    private byte[] joinByteArrays(byte[]... arrays) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (byte[] arr : arrays) {
            try {
                output.write(arr);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return output.toByteArray();

        /*
        int len = 0;
        for (byte[] arr : arrays) {
            len += arr.length;
        }
        byte[] result = new byte[len];
        int i = 0;
        for (byte[] arr : arrays) {
            for (byte b : arr) {
                result[i] = b;
                i++;
            }
        }
        return result;

         */
    }

    /*
    Working with messages
     */


    /*
    Debug
     */

    @Deprecated
    public Map<String, Channel[]> getChannels() {
        return channels;
    }

    @Deprecated
    public Cryptographer getCryptographer() {
        return cryptographer;
    }
}
