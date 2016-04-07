package se3a04.twentyonequestions.Controller.Database.Cryptography;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * MCrypt
 *      This class is a java implementation of the php MCrypt module
 *      It is used to encrypt data between the database and the client
 *      Based off of http://stackoverflow.com/questions/20929105/php-android-xml-encryption-decryption
 *      Accessed on April 1st, 2016
 */
public class MCrypt {

    private String iv = "afrosamuraicool1";
    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;

    private String SecretKey = "diodebjtmosfet12";

    /**
     * Constructor
     *      Initializes the iv and key specs, sets the cipher to AES
     */
    public MCrypt()
    {
        ivspec = new IvParameterSpec(iv.getBytes());

        keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * encrypt
     *      Encrypts a string object to an array of bytes
     * @param text: The text string to encrypt before sending to the database
     * @return: byte array of encrypted data
     * @throws Exception: indicating the encryption failed
     */
    public byte[] encrypt(String text) throws Exception
    {
        if(text == null || text.length() == 0)
            throw new Exception("Empty string");

        byte[] encrypted = null;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            encrypted = cipher.doFinal(padString(text).getBytes());
        } catch (Exception e)
        {
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    /**
     * decrypt:
     *      Decrypts a string and converts to an array of bytes
     * @param code: the string to decrypt
     * @return: byte array of decrypted string
     * @throws Exception: if decryption failed
     */
    public byte[] decrypt(String code) throws Exception
    {
        if(code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted = null;

        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            decrypted = cipher.doFinal(hexToBytes(code));
        } catch (Exception e)
        {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }


    /**
     * bytesToHex:
     *      Converts an array of bytes to a string object
     * @param data: byte array of data to convert
     * @return: String representation of data
     */
    public static String bytesToHex(byte[] data)
    {
        if (data==null)
        {
            return null;
        }

        int len = data.length;
        String str = "";
        for (int i=0; i<len; i++) {
            if ((data[i]&0xFF)<16)
                str = str + "0" + java.lang.Integer.toHexString(data[i]&0xFF);
            else
                str = str + java.lang.Integer.toHexString(data[i]&0xFF);
        }
        return str;
    }

    /**
     * hexToBytes
     *      Converts a String object to a byte array representation
     * @param str: string to convert to byte[]
     * @return: byte array representation of str
     */
    public static byte[] hexToBytes(String str) {
        if (str==null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i=0; i<len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i*2,i*2+2),16);
            }
            return buffer;
        }
    }


    /**
     * padString
     *      Pads a string to ensure it is the correct size
     * @param source: string of wrong size
     * @return: String of correct size
     */
    private static String padString(String source)
    {
        char paddingChar = ' ';
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        for (int i = 0; i < padLength; i++)
        {
            source += paddingChar;
        }

        return source;
    }
}