import java.util.Hashtable;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class EasyAES {

    private static byte[] hex2byte(String inputString) {
        if (inputString == null || inputString.length() < 2) {
            return new byte[0];
        }
        inputString = inputString.toLowerCase();
        int l = inputString.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = inputString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }

    public static SecretKeySpec createKey(byte[] password) {
        return new SecretKeySpec(password, "AES");
    }

    public static byte[] decrypt(byte[] content, byte[] password) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;

        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;

    }

    public static byte[] encrypt(byte[] content, byte[] password) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;

        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;

    }

    public static void main(String[] args) {
        String cleartext = "Verschluesselung";
        String hexKeyString = "be393d39ca4e18f41fa9d88a9d47a574";

        byte[] hexKey = hex2byte(hexKeyString);
        byte[] cleartextBytes = cleartext.getBytes();

        Hashtable<byte[], byte[]> table = new Hashtable<byte[], byte[]>();

        for (int firstBytePosition = 0; firstBytePosition < 16; firstBytePosition++) {
            for (int secondBytePosition = 0; secondBytePosition < 16; secondBytePosition++) {
                if (secondBytePosition > firstBytePosition) {
                    for (int byteValue1 = 0; byteValue1 < 256; byteValue1++) {
                        for (int byteValue2 = 0; byteValue2 < 256; byteValue2++) {
                            byte[] keyValue = new byte[16];
                            keyValue[firstBytePosition] = (byte) byteValue1;
                            keyValue[secondBytePosition] = (byte) byteValue2;

                            table.put(
                                encrypt(cleartextBytes, keyValue),
                                keyValue
                            );
                        }

                    }

                }
            }
        }

        for (int firstBytePosition = 0; firstBytePosition < 16; firstBytePosition++) {
            for (int secondBytePosition = 0; secondBytePosition < 16; secondBytePosition++) {
                if (secondBytePosition > firstBytePosition) {
                    for (int byteValue1 = 0; byteValue1 < 256; byteValue1++) {
                        for (int byteValue2 = 0; byteValue2 < 256; byteValue2++) {
                            byte[] keyValue = new byte[16];
                            keyValue[firstBytePosition] = (byte) byteValue1;
                            keyValue[secondBytePosition] = (byte) byteValue2;

                            byte[] decryptedValue = decrypt(hexKey, keyValue);

                            byte[] correspondingKey1 = table.get(
                                decryptedValue
                            );

                            if (correspondingKey1 != null) {
                                System.out.println("Found keys: ");
                                System.out.println(keyValue);
                                System.out.println(correspondingKey1);
                                System.exit(0);
                            }
                        }

                    }

                }
            }
        }


    }

}
