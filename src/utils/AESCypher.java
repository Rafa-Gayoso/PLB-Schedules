package utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESCypher {
    private SecretKey key;
    private int KEY_SIZE = 256;
    private int T_LEN = 128;
    private byte[] IV;

    public AESCypher(){
        initFromStrings("xMNA6kqyTk4bnc/qPPyJOLdSmhnjao8IW8RUIYmFGsc=","5VbKdt+c1rA1h6G3");
    }

    /**
     * Generate key and IV
     * @throws Exception
     */
    public void init() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        key = generator.generateKey();
    }

    public void initFromStrings(String secretKey, String IV){
        key = new SecretKeySpec(decode(secretKey),"AES");
        this.IV = decode(IV);
    }

    /**
     * Use to deisplay new key and IV
     * @param message
     * @return
     * @throws Exception
     */
    public String encryptOld(String message) throws Exception {
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        IV = encryptionCipher.getIV();
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    public String encrypt(String message) throws Exception {
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN,IV);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key,spec);
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    public String decrypt(String encryptedMessage) throws Exception {
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes);
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    private void exportKeys(){
        System.err.println("SecretKey : "+encode(key.getEncoded()));
        System.err.println("IV : "+encode(IV));
    }

    /*public static void main(String[] args) {
        try {
            AESCypher aes = new AESCypher();
            aes.initFromStrings("xMNA6kqyTk4bnc/qPPyJOLdSmhnjao8IW8RUIYmFGsc=","5VbKdt+c1rA1h6G3");
            String encryptedMessage = aes.encrypt("TheXCoders_2");
            String decryptedMessage = aes.decrypt(encryptedMessage);

            System.err.println("Encrypted Message : " + encryptedMessage);
            System.err.println("Decrypted Message : " + decryptedMessage);
            //aes.exportKeys();
        } catch (Exception ignored) {
        }
    }*/
}
