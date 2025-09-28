package com.amouri_dev.talksy.security;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

public class KeyUtils {

    private KeyUtils() {}

    public static PrivateKey loadPrivateKey(final String pemPath) throws Exception {
        if (pemPath == null || pemPath.trim().isEmpty()) {
            throw new IllegalArgumentException("PEM path cannot be null or empty");
        }
        final String key = readKeyFromFile(pemPath)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Private key content is empty or invalid: " + pemPath);
        }
        final byte[] decoded = Base64.getDecoder().decode(key);
        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    public static PublicKey loadPublicKey(String pemPath) throws Exception {
        if (pemPath == null || pemPath.trim().isEmpty()) {
            throw new IllegalArgumentException("PEM path cannot be null or empty");
        }
        final String key = readKeyFromFile(pemPath)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Private key content is empty or invalid: " + pemPath);
        }
        System.out.println("Cleaned public key: " + key);
        byte[] decodedKey = Base64.getDecoder().decode(key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static String readKeyFromFile(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        try (InputStream inputStream = KeyUtils.class.getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Classpath resource not found: " + path);
            }
            Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
            scanner.useDelimiter("\\A");
            String content = scanner.hasNext() ? scanner.next() : "";
            if (content.isEmpty()) {
                throw new IllegalArgumentException("Key file is empty: " + path);
            }
            return content;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read key from classpath: " + path, e);
        }
    }
}