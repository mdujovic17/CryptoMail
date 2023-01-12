package com.markod.cryptomail.util;

import org.jasypt.digest.PooledStringDigester;
import org.jasypt.encryption.pbe.PooledPBEByteEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;

public class Encryption {
    private static final int DEFAULT_POOL_SIZE = 4;
    private static final int DEFAULT_ITERATIONS = 512384;
    public enum DigestAlgorithm {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        SHA512("SHA-512");

        private final String algorithm;
        DigestAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }
        public String get() {
            return algorithm;
        }
    }

    public enum PBEAlgorithm {
        MD5_TRIPLE_DES("PBEWithMD5AndTripleDES"),
        SHA1_AES256("PBEWithHMacSHA1AndAES_256"),
        SHA256_AES256("PBEWithHMacSHA256AndAES_256"),
        SHA512_AES256("PBEWithHMacSHA512AndAES_256");
        private final String algorithm;
        PBEAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }
        public String get() {
            return algorithm;
        }
    }

    private static void setDigesterAlgorithm(PooledStringDigester digester, DigestAlgorithm algorithm) {
        digester.setAlgorithm(algorithm.get());
        digester.setIterations(DEFAULT_ITERATIONS);
        digester.setSaltSizeBytes(0);
        digester.setPoolSize(DEFAULT_POOL_SIZE);
    }

    private static void setStringPBEAlgorithm(PooledPBEStringEncryptor encryptor, PBEAlgorithm algorithm) {
        encryptor.setAlgorithm(algorithm.get());
        encryptor.setIvGenerator(new RandomIvGenerator());
        encryptor.setPoolSize(DEFAULT_POOL_SIZE);
    }

    private static void setBytePBEAlgorithm(PooledPBEByteEncryptor encryptor, PBEAlgorithm algorithm) {
        encryptor.setAlgorithm(algorithm.get());
        encryptor.setIvGenerator(new RandomIvGenerator());
        encryptor.setPoolSize(DEFAULT_POOL_SIZE);
    }

    public static String getHashFromPassword(String password, DigestAlgorithm algorithm) {
        PooledStringDigester digester = new PooledStringDigester();
        setDigesterAlgorithm(digester, algorithm);
        return digester.digest(password);
    }

    public static String encryptText(String text, String key, PBEAlgorithm algorithm) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        setStringPBEAlgorithm(encryptor, algorithm);
        encryptor.setPassword(key);

        return encryptor.encrypt(text);
    }

    public static String decryptText(String text, String key, PBEAlgorithm algorithm) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        setStringPBEAlgorithm(encryptor, algorithm);
        encryptor.setPassword(key);

        return encryptor.decrypt(text);
    }

    public static byte[] encryptBytes(byte[] bytes, String key, PBEAlgorithm algorithm) {
        PooledPBEByteEncryptor encryptor = new PooledPBEByteEncryptor();
        setBytePBEAlgorithm(encryptor, algorithm);
        encryptor.setPassword(key);

        return encryptor.encrypt(bytes);
    }

    public static byte[] decryptBytes(byte[] bytes, String key, PBEAlgorithm algorithm) {
        PooledPBEByteEncryptor encryptor = new PooledPBEByteEncryptor();
        setBytePBEAlgorithm(encryptor, algorithm);
        encryptor.setPassword(key);

        return encryptor.decrypt(bytes);
    }

    private Encryption() throws InstantiationException {
        throw new InstantiationException("Attempted to instantiate static class");
    }
}
