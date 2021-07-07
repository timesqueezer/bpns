package socket;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.concurrent.ThreadLocalRandom;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Console;


public class Useradmin {
    // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    private static String line_format = "%s:%s:%s\n";

    public boolean checkUser(String username, char[] password) {
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("passwords.txt"))) {
                String user_string;
                while ((user_string = br.readLine()) != null) {
                    String[] user_salt_hash = user_string.split(":");
                    if (user_salt_hash[0].equals(username)) {
                        String password_salt = user_salt_hash[1];
                        String password_hash = this.generateSaltedPasswordHash(
                            String.valueOf(password),
                            password_salt
                        );
                        String saved_password_hash = user_salt_hash[2];

                        if (saved_password_hash.equals(password_hash)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUser(String username, char[] password) {
        int random_number = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        String password_salt = username + random_number;

        String password_hash = this.generateSaltedPasswordHash(
            String.valueOf(password),
            password_salt
        );

        StringBuilder output_content = new StringBuilder();
        boolean found_user = false;

        try (BufferedReader br = new BufferedReader(new FileReader("passwords.txt"))) {
            String user_string;
            while ((user_string = br.readLine()) != null) {
                if (user_string.split(":")[0].equals(username)) {
                    String s = String.format(
                        Useradmin.line_format,
                        username,
                        password_salt,
                        password_hash
                    );
                    output_content.append(s);
                    found_user = true;
                } else {
                    output_content.append((user_string + "\n"));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!found_user) {
            String s = String.format(
                Useradmin.line_format,
                username,
                password_salt,
                password_hash
            );
            output_content.append(s);
        }

        try {
            FileWriter writer = new FileWriter("passwords.txt");
            writer.write(output_content.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateSaltedPasswordHash(String password, String salt) {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            md.update(password.getBytes(StandardCharsets.UTF_8));
            bytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new String(Useradmin.bytesToHex(bytes));
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("wrong format");
            System.exit(1);
        }
        String command = args[0];
        String param = args[1];
        Useradmin useradmin = new Useradmin();
        if (command.equals("addUser")) {
            Console console = System.console();
            char[] password = console.readPassword("New password:");
            useradmin.addUser(param, password);
        } else if (command.equals("checkUser")) {
            Console console = System.console();
            char[] password = console.readPassword("Password:");
            boolean authenticated = useradmin.checkUser(param, password);
            if (authenticated) {
                System.out.println("Access Granted");
            } else {
                System.out.println("Access Denied");
            }
        }
    }
}
