import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.ThreadLocalRandom;


public interface Useradministration {
    public void addUser(String username, char[] password);
    public boolean checkUser(String username, char[] password);
}


public class Useradmin implements Useradministration {
    public void addUser(String username, char[] password) {
        FileWriter writer = new FileWriter("passwords.txt");

        int random_number = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        String password_salt = username + random_number;

        String password_hash = this.generateSaltedPasswordHash(String.valueOf(password), password_salt);

        System.out.println(password_hash);
    }

    private String generateSaltedPasswordHash(String password, String salt) {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt.getBytes(StandardCharsets.UTF_8));
        md.update(password.getBytes(StandardCharsets.UTF_8));
        byte[] bytes = md.digest();

        return new String(bytes);
    }

    public static void main(String[] args) {
        System.out.println(args[0]);
        System.out.println(args[1]);
    }
}
