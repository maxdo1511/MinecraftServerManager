package ru.hbb.Network.PacketSystem;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class PacketManager {

    public static Map<String, Class> registered = new HashMap<>();

    public PacketManager() {
        // register here packets or put them to CustomPackets Packet!
        registerAll();
    }

    public static void register(Class customPacketClass) {
        registered.put(customPacketClass.getSimpleName(), customPacketClass);
        System.out.println("Successfully registered " + customPacketClass.getSimpleName() + " packet!");
    }

    public static String serializeToString(Object o, int deep) {
        String res = o.getClass().getSimpleName();
        Class c = o.getClass();
        for (Field field : c.getDeclaredFields()) {
            if (field.isAnnotationPresent(SerializeField.class)) {
                String s_obj = getStringFromField(field, o, deep);
                res = res + ":" + s_obj;
            }
        }
        return res;
    }

    /**
     * @param deep use 1
     */
    public static Object deserializeFromString(String str, Field lastField, int deep) {
        try {
            Object[] data = getObjects(str, deep);
            String[] fields = data[0].toString().split(":");
            String obj_name = fields[0];
            // проверка на особые типы данных
            if (obj_name.contains("List")) {
                return deserializeList(fields, lastField);
            }
            if (obj_name.contains("Map")) {
                return deserializeMap(fields, lastField);
            }
            //
            Class c = registered.get(obj_name);
            CustomPacket customPacket = (CustomPacket) c.newInstance();
            for (int i = 1; i < fields.length; i = i + 2) {
                // Проверка на объект
                Field field = c.getField(fields[i]);
                Object obj = null;
                String s_field_type = field.getType().getSimpleName();
                if (!isDefaultVar(s_field_type)) {
                    obj = deserializeFromString((String) ((Object[]) data[1])[Integer.parseInt(fields[i + 1])], field, deep + 1);
                    obj = field.getType().cast(obj);
                } else {
                    String field_type = field.getType().getTypeName();
                    String s_object = fields[i + 1];
                    obj = getObjectWithTrueType(s_object, field_type);
                }
                field.set(customPacket, obj);
            }
            return customPacket;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object getObjectWithTrueType(String obj, Class field_type) {
        if (field_type.getSimpleName().contains("int") || field_type.getSimpleName().contains("Int")) {
            return Integer.parseInt(obj);
        }
        if (field_type.getSimpleName().equalsIgnoreCase("double")) {
            return Double.parseDouble(obj);
        }
        if (field_type.getSimpleName().contains("bool") || field_type.getSimpleName().contains("Bool")) {
            return Boolean.getBoolean(obj);
        }
        return obj;
    }

    private static Object getObjectWithTrueType(String obj, String field_type) {
        if (field_type.contains("int") || field_type.contains("Int")) {
            return Integer.parseInt(obj);
        }
        if (field_type.equalsIgnoreCase("double")) {
            return Double.parseDouble(obj);
        }
        return obj;
    }

    /**
     * @param data - fields list
     */
    private static List<Object> deserializeList(String[] data, Field field) {
        Class clazz = field.getAnnotation(SerializeField.class).list_type();
        List<Object> list = new ArrayList<>();
        for (int i = 1; i < data.length; i++) {
            list.add(getObjectWithTrueType(data[i], clazz));
        }
        return list;
    }

    /**
     * @param data - fields map
     */
    private static Map<Object, Object> deserializeMap(String[] data, Field field) {
        Class key = field.getAnnotation(SerializeField.class).map_key_type();
        Class val = field.getAnnotation(SerializeField.class).map_val_type();
        Map<Object, Object> map = new HashMap<>();
        for (int i = 1; i < data.length; i = i + 2) {
            map.put(getObjectWithTrueType(data[i], key), getObjectWithTrueType(data[i + 1], val));
        }
        return map;
    }


    //TODO add ability to serialize list with not default Objects
    private static String serializeList(Object obj) {
        List<Object> list = (List<Object>) obj;
        String res = "";
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                res = res + ":" + list.get(i).toString();
            }else {
                res = list.get(i).toString();
            }
        }
        return res;
    }

    private static String serializeMap(Object obj) {
        Map<Object, Object> map = (Map<Object, Object>) obj;
        String res = "";
        boolean flag = true;
        for (Object key: map.keySet()) {
            Object value = map.get(key);
            if (flag) {
                res = key.toString() + ":" + value.toString();
                flag = false;
            } else {
                res = res + ":" + key.toString() + ":" + value.toString();
            }
        }
        return res;
    }

    private static String getStringFromField(Field field, Object obj, int deep) {
        try {
            String[] full_field_name = field.getName().split("\\.");
            String field_name = full_field_name[full_field_name.length - 1];
            String field_type = field.getType().getSimpleName();
            String delimiter = "<" + createStringFromRepeatingString("!", deep) + ">";
            if (isDefaultVar(field_type)) {
                Object o = field.get(obj);
                return field_name + ":" + o.toString();
            }
            if (field_type.contains("List")) {
                return field_name + ":" + delimiter + "List:" + serializeList(field.get(obj)) + delimiter;
            }
            if (field_type.contains("Map")) {
                return field_name + ":" + delimiter + "Map:" + serializeMap(field.get(obj)) + delimiter;
            }
            return field_name + ":" + delimiter + serializeToString(field.get(obj), deep + 1) + delimiter;
        }catch (IllegalAccessException e) {
            return "";
        }
    }

    private static Object[] getObjects(String s, int deep) {
        String[] data = s.split("<" + createStringFromRepeatingString("!", deep) + ">");
        String new_message = "";
        String[] memb_objects = new String[data.length / 2];
        for (int i = 0; i < data.length; i++) {
            if (i % 2 == 1) {
                memb_objects[i / 2] = data[i];
                new_message = new_message + "" + (i / 2);
            }else {
                new_message = new_message + data[i];
            }
        }
        return new Object[]{new_message, memb_objects};
    }

    private static boolean isDefaultVar(String field_type) {
        return field_type.equalsIgnoreCase("int") || field_type.equalsIgnoreCase("string") || field_type.equalsIgnoreCase("double");
    }

    private void registerAll() {
        try {
            for (Class c : Reflection.getClasses(this.getClass().getPackage().getName() + ".CustomPackets")) {
                for (Class i : c.getInterfaces()) {
                    if (i.getName().equals(CustomPacket.class.getName())) {
                        register(c);
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String createStringFromRepeatingString(String s, int amount) {
        String res = "";
        for (int i = 0; i < amount; i++) {
            res = res + s;
        }
        return res;
    }

}

