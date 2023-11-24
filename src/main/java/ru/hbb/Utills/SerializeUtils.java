package ru.hbb.Utills;

import ru.hbb.Console.Logger.SimpleLogger;
import ru.hbb.Network.PacketSystem.CustomPacket;
import ru.hbb.Network.PacketSystem.SerializeField;

import java.io.*;
import java.lang.reflect.Field;

public class SerializeUtils {

    public static byte[] serialize(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] yourBytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            yourBytes = bos.toByteArray();
        } catch (IOException e) {
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return yourBytes;
    }

    public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        Object out = null;
        try {
            in = new ObjectInputStream(bis);
            out = in.readObject();
        }catch (Exception e) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return out;
    }

}
