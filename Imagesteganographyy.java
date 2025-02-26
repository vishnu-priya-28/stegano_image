import java.awt.image.BufferedImage;
import java.io.File;
import java.security.MessageDigest;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Imagesteganographyy {
    private static SecretKey secretKey;
    
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            BufferedImage img = ImageIO.read(new File("vishnu.jpg"));

            System.out.print("Enter secret message: ");
            String msg = scanner.nextLine();
            System.out.print("Enter a passcode: ");
            String password = scanner.nextLine();
            
            // Generate encryption key
            generateKey(password);
            String encryptedMsg = encrypt(msg);

            int width = img.getWidth();
            int height = img.getHeight();
            Random random = new Random(password.hashCode());
            
            for (int i = 0; i < encryptedMsg.length(); i++) {
                int ascii = (int) encryptedMsg.charAt(i);
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int rgb = (img.getRGB(x, y) & 0xFFFFFF00) | (ascii & 0xFF);
                img.setRGB(x, y, rgb);
            }
            
            ImageIO.write(img, "png", new File("encryptedImage.png"));
            System.out.println("Message encrypted successfully!");

            System.out.print("Enter passcode for decryption: ");
            String pas = scanner.nextLine();
            
            if (password.equals(pas)) {
                StringBuilder decryptedMessage = new StringBuilder();
                random = new Random(password.hashCode());
                for (int i = 0; i < encryptedMsg.length(); i++) {
                    int x = random.nextInt(width);
                    int y = random.nextInt(height);
                    int ascii = img.getRGB(x, y) & 0xFF;
                    decryptedMessage.append((char) ascii);
                }
                System.out.println("Decryption message: " + decrypt(decryptedMessage.toString()));
            } else {
                System.out.println("YOU ARE NOT AUTHORIZED");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void generateKey(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(password.getBytes("UTF-8"));
        secretKey = new SecretKeySpec(key, 0, 16, "AES");
    }
    
    private static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }
    
    private static String decrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
    }
}
