package org.example.springproject.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * PBKDF2 密码加密工具类
 * 使用 PBKDF2WithHmacSHA256 算法进行密码哈希
 */
public class PasswordUtil {

    /**
     * 默认迭代次数
     */
    private static final int DEFAULT_ITERATIONS = 100000;

    /**
     * 盐值长度（字节）
     */
    private static final int SALT_LENGTH = 32;

    /**
     * 哈希值长度（位）
     */
    private static final int HASH_LENGTH = 256;

    /**
     * 算法名称
     */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * 生成随机盐值
     *
     * @return Base64 编码的盐值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 使用 PBKDF2 算法对密码进行哈希
     *
     * @param password   明文密码
     * @param salt       Base64 编码的盐值
     * @param iterations 迭代次数
     * @return Base64 编码的密码哈希
     * @throws NoSuchAlgorithmException 算法不存在异常
     * @throws InvalidKeySpecException  密钥规范异常
     */
    public static String hashPassword(String password, String salt, int iterations)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (salt == null || salt.isEmpty()) {
            throw new IllegalArgumentException("盐值不能为空");
        }
        if (iterations <= 0) {
            throw new IllegalArgumentException("迭代次数必须大于0");
        }

        // 将 Base64 编码的盐值解码为字节数组
        byte[] saltBytes = Base64.getDecoder().decode(salt);

        // 创建 PBEKeySpec 对象
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                saltBytes,
                iterations,
                HASH_LENGTH
        );

        // 获取 SecretKeyFactory 实例
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

        // 生成哈希
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // 将哈希值编码为 Base64 字符串
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * 使用默认迭代次数对密码进行哈希
     *
     * @param password 明文密码
     * @param salt     Base64 编码的盐值
     * @return Base64 编码的密码哈希
     * @throws NoSuchAlgorithmException 算法不存在异常
     * @throws InvalidKeySpecException  密钥规范异常
     */
    public static String hashPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return hashPassword(password, salt, DEFAULT_ITERATIONS);
    }

    /**
     * 验证密码是否正确
     *
     * @param password     用户输入的明文密码
     * @param salt         Base64 编码的盐值
     * @param iterations   迭代次数
     * @param expectedHash 存储的密码哈希（Base64 编码）
     * @return 密码是否匹配
     */
    public static boolean verifyPassword(String password, String salt, int iterations, String expectedHash) {
        try {
            // 使用相同的参数计算哈希
            String actualHash = hashPassword(password, salt, iterations);
            // 比较哈希值
            return actualHash.equals(expectedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 使用默认迭代次数验证密码
     *
     * @param password     用户输入的明文密码
     * @param salt         Base64 编码的盐值
     * @param expectedHash 存储的密码哈希（Base64 编码）
     * @return 密码是否匹配
     */
    public static boolean verifyPassword(String password, String salt, String expectedHash) {
        return verifyPassword(password, salt, DEFAULT_ITERATIONS, expectedHash);
    }

    /**
     * 获取默认迭代次数
     *
     * @return 默认迭代次数
     */
    public static int getDefaultIterations() {
        return DEFAULT_ITERATIONS;
    }

    /**
     * 生成完整的密码哈希信息（用于测试）
     *
     * @param password 明文密码
     * @return 包含盐值、迭代次数和哈希的对象
     */
    public static PasswordHashInfo generatePasswordHash(String password) {
        try {
            String salt = generateSalt();
            int iterations = DEFAULT_ITERATIONS;
            String hash = hashPassword(password, salt, iterations);
            return new PasswordHashInfo(salt, iterations, hash);
        } catch (Exception e) {
            throw new RuntimeException("密码哈希生成失败", e);
        }
    }

    /**
     * 密码哈希信息类
     */
    public static class PasswordHashInfo {
        private final String salt;
        private final int iterations;
        private final String hash;

        public PasswordHashInfo(String salt, int iterations, String hash) {
            this.salt = salt;
            this.iterations = iterations;
            this.hash = hash;
        }

        public String getSalt() {
            return salt;
        }

        public int getIterations() {
            return iterations;
        }

        public String getHash() {
            return hash;
        }

        @Override
        public String toString() {
            return "PasswordHashInfo{" +
                    "salt='" + salt + '\'' +
                    ", iterations=" + iterations +
                    ", hash='" + hash + '\'' +
                    '}';
        }
    }
}
