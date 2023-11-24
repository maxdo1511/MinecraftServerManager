package ru.hbb.ConfigDataContainers;

public class CryptData {

    private String transformation;
    private String algorithm;
    private int key_len;

    public CryptData(String transformation, String algorithm, int key_len) {
        this.transformation = transformation;
        this.algorithm = algorithm;
        this.key_len = key_len;
    }

    public CryptData() {
    }

    public String getTransformation() {
        return transformation;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public int getKey_len() {
        return key_len;
    }
}
