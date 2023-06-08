package com.example.camera2_1;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class SimpleEncode {
    private static final String keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz~!@#$%^&*()_+-={}[]:;<,>.?/|";
    private static final int keyLength = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz~!@#$%^&*()_+-={}[]:;<,>.?/|".length();
    private static int encryptionA = 17;
    private static int encryptionB = 8;
    private static int preCountMax = 15;
    private static int postCount = 5;
    private static final String randomChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnop";
    private static final int randomCharLength = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnop".length();

    private static char[] ALPHABET = "ABCDEFGHIJKLMN0123456789OPQRSTUVWXYZ+/abcdefghijklmnopqrstuvwxyz".toCharArray();

    private static String base64Encode(String sourceStr)
            throws UnsupportedEncodingException
    {
        byte[] bs = sourceStr.getBytes("utf-8");
        return base64Encode(bs);
    }

    private static String base64Encode(byte[] buf)
    {
        int size = buf.length;
        char[] ar = new char[(size + 2) / 3 * 4];
        int a = 0;
        int i = 0;
        while (i < size) {
            byte b0 = buf[(i++)];
            byte b1 = i < size ? buf[(i++)] : 0;
            byte b2 = i < size ? buf[(i++)] : 0;

            int mask = 63;
            ar[(a++)] = ALPHABET[(b0 >> 2 & mask)];
            ar[(a++)] = ALPHABET[((b0 << 4 | (b1 & 0xFF) >> 4) & mask)];
            ar[(a++)] = ALPHABET[((b1 << 2 | (b2 & 0xFF) >> 6) & mask)];
            ar[(a++)] = ALPHABET[(b2 & mask)];
        }
        switch (size % 3) { case 1:
            a--; ar[a] = '=';
            case 2:
                a--; ar[a] = '=';
        }
        return new String(ar);
    }

    public static String encrypt(String s)
            throws UnsupportedEncodingException
    {
        int srcLength = s.length();
        Random random = new Random();

        int addCharCount = 0;
        if (srcLength < preCountMax) {
            addCharCount = random.nextInt(preCountMax) + 1;
        }

        StringBuilder sb = new StringBuilder(new StringBuilder().append(addCharCount).append("|").toString());
        for (int i = 0; i < addCharCount; i++) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnop".charAt(random.nextInt(randomCharLength)));
        }
        sb.append(s);

        for (int i = 0; i < postCount; i++) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnop".charAt(random.nextInt(randomCharLength)));
        }

        String base64Str = base64Encode(sb.toString());
        char[] srcArray = base64Str.toCharArray();

        char[] destArray = new char[srcArray.length];
        for (int i = 0; i < srcArray.length; i++) {
            char srcChar = srcArray[i];

            int position = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz~!@#$%^&*()_+-={}[]:;<,>.?/|".indexOf(srcChar);

            int y = (encryptionA * position + encryptionB) % keyLength;

            char replaceChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz~!@#$%^&*()_+-={}[]:;<,>.?/|".charAt(y);

            destArray[i] = replaceChar;
        }

        return new String(destArray);
    }
}
