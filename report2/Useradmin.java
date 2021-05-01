import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.concurrent.ThreadLocalRandom;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Useradmin {
    public void addUser(String username, char[] password) {
        try {
            FileWriter writer = new FileWriter("passwords.txt");

        } catch (IOException e) {
            e.printStackTrace();

        }

        int random_number = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        String password_salt = username + random_number;

        String password_hash = this.generateSaltedPasswordHash(String.valueOf(password), password_salt);

        System.out.println(password_hash);
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

        return new String(bytes);

    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("wrong format");
            System.exit(1);
        }

        String command = args[0];
        String param = args[1];

        Useradmin useradmin = new Useradmin();

        System.out.println(command);
        System.out.println(command=="addUser\0");
        System.out.println(param);

        if (command == "addUser") {
            System.out.println("New password:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            try {
                String password = reader.readLine();
                useradmin.addUser(param, password.toCharArray());

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}
