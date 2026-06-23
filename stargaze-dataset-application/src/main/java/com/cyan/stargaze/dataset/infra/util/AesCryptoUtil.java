package com.cyan.stargaze.dataset.infra.util;

import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.infra.config.DatasetCryptoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * AES-256-GCM 加解密工具(用于数据源连接密码/配置加密)。
 * <p>
 * 密钥由 KMS/环境变量注入,密文以 base64 存储;IV 随机生成并前置于密文。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class AesCryptoUtil {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ALGORITHM = "AES";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;
    private static final int KEY_LENGTH = 32;

    private final DatasetCryptoProperties properties;

    /**
     * 加密
     */
    public String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        try {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey(), new GCMParameterSpec(TAG_LENGTH, iv));
            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] result = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(cipherText, 0, result, iv.length, cipherText.length);
            return Base64Utils.encodeToString(result);
        } catch (Exception e) {
            throw new SilentException("数据源配置加密失败: " + e.getMessage());
        }
    }

    /**
     * 解密
     */
    public String decrypt(String ciphertext) {
        if (ciphertext == null) {
            return null;
        }
        try {
            byte[] data = Base64Utils.decodeFromString(ciphertext);
            byte[] iv = Arrays.copyOfRange(data, 0, IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(data, IV_LENGTH, data.length);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey(), new GCMParameterSpec(TAG_LENGTH, iv));
            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SilentException("数据源配置解密失败: " + e.getMessage());
        }
    }

    /**
     * 由配置密钥派生 256bit AES 密钥
     */
    private SecretKeySpec secretKey() throws Exception {
        String raw = properties.getAesKey();
        if (raw == null || raw.isBlank()) {
            throw new SilentException("未配置数据源加密密钥");
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] key = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(Arrays.copyOf(key, KEY_LENGTH), ALGORITHM);
    }
}
