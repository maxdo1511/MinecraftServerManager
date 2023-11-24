package ru.hbb;

import ru.hbb.ConfigDataContainers.UserInfo;
import ru.hbb.Console.Logger.SimpleLogger;
import ru.hbb.Utills.FileName;
import ru.hbb.Utills.FileUtils;

import java.util.*;

public final class PlayerManager {

    public static PlayerManager instance;
    private ArrayList<UserInfo> userInfoArrayList = new ArrayList<>();
    private Map<String, UserInfo> userInfoFromUUID = new HashMap<>();
    private Map<String, UserInfo> userInfoFromName = new HashMap<>();
    private Map<String, UserInfo> userInfoFromIp = new HashMap<>();
    private Map<String, UserInfo> userInfoFromMac = new HashMap<>();

    public PlayerManager() {
        instance = this;
        load();
    }

    public UserInfo getDataFromName(String name, boolean createIfNull) {
        if (!userInfoFromName.containsKey(name)) {
            return createUserInfo(name);
        }
        return userInfoFromName.get(name);
    }

    public UserInfo getDataFromUUID(String uuid) {
        return userInfoFromUUID.getOrDefault(uuid, null);
    }

    public Map<String, UserInfo> getPlayerDataMap() {
        return userInfoFromUUID;
    }

    public static PlayerManager getInstance() {
        return instance;
    }

    public boolean hasBan(String name) {
        UserInfo info = userInfoFromName.get(name);
        if (info != null) {
            if (userInfoFromName.get(name).isHas_ban()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBan(String name, String uuid, String ip, String mac) {
        UserInfo info = userInfoFromName.get(name);
        if (info != null) {
            if (userInfoFromName.get(name).isHas_ban()) {
                return true;
            }
        }

        info = userInfoFromUUID.get(uuid);
        if (info != null) {
            if (info.isHas_ban()) {
                return true;
            }
        }

        info = userInfoFromIp.get(ip);
        if (info != null) {
            if (info.isHas_ban()) {
                return true;
            }
        }

        info = userInfoFromMac.get(mac);
        if (info != null) {
            if (info.isHas_ban()) {
                return true;
            }
        }

        return false;
    }

    public void save() {
        FileUtils fileUtils = new FileUtils(FileName.USER_DATA.getName());
        fileUtils.write(userInfoArrayList);
    }

    public void banPlayer(UserInfo userInfo) {
        String name = userInfo.getName();
        String uuid = userInfo.getUuid();
        String ip = userInfo.getIp();
        String mac = userInfo.getMac();
        if (name != null) {

        }
        if (uuid != null) {

        }
        if (ip != null) {

        }
        if (mac != null) {

        }
    }

    private void load() {
        try {
            FileUtils fileUtils = new FileUtils(FileName.USER_DATA.getName());
            List<UserInfo> userInfos = (List<UserInfo>) fileUtils.read();
            for (UserInfo info : userInfos) {
                try {
                    initUserInfo(info);
                }catch (RuntimeException e) {
                    SimpleLogger.error("Error to load player info: " + e.getMessage());
                }
            }
            SimpleLogger.log("Player data load successfully");
        }catch (Exception e) {
            SimpleLogger.error("Error player info NULL!");
        }
    }

    private void initUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            throw new RuntimeException("User data null");
        }
        String name = userInfo.getName();
        String uuid = userInfo.getUuid();
        String ip = userInfo.getIp();
        String mac = userInfo.getMac();
        if (name != null) {
            userInfoFromName.put(name, userInfo);
        }
        if (uuid != null) {
            userInfoFromUUID.put(uuid, userInfo);
        }
        if (ip != null) {
            userInfoFromIp.put(ip, userInfo);
        }
        if (mac != null) {
            userInfoFromMac.put(mac, userInfo);
        }
        userInfoArrayList.add(userInfo);
    }

    private UserInfo createUserInfo(String name) {
        UserInfo userInfo = new UserInfo();
        String uuid = UUID.randomUUID().toString();
        userInfo.setName(name);
        userInfo.setUuid(uuid);

        // add to list
        initUserInfo(userInfo);
        save();
        return userInfo;
    }
}
