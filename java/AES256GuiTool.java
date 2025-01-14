import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AES256GuiTool {

    public static String encrypt(String key, String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(adjustKeyLength(key).getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = generateIv();
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        String iv = Base64.getEncoder().encodeToString(ivSpec.getIV());
        String encryptedData = Base64.getEncoder().encodeToString(encrypted);
        return iv + ":" + encryptedData;
    }

    public static String decrypt(String key, String encryptedData) throws Exception {
        String[] parts = encryptedData.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted data format.");
        }
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] encrypted = Base64.getDecoder().decode(parts[1]);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(adjustKeyLength(key).getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] original = cipher.doFinal(encrypted);
        return new String(original, StandardCharsets.UTF_8);
    }

    private static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private static String adjustKeyLength(String key) {
        if (key.length() < 6 || key.length() > 100) {
            throw new IllegalArgumentException("Key length must be between 6 and 100 characters.");
        }
        StringBuilder adjustedKey = new StringBuilder(key);
        while (adjustedKey.length() < 32) {
            adjustedKey.append("0");
        }
        return adjustedKey.substring(0, 32);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("AES256 Encryption/Decryption Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new GridLayout(8, 1));

        JLabel actionLabel = new JLabel("Select Action:");
        panel.add(actionLabel);

        JRadioButton encryptButton = new JRadioButton("Encrypt", true);
        JRadioButton decryptButton = new JRadioButton("Decrypt");
        ButtonGroup group = new ButtonGroup();
        group.add(encryptButton);
        group.add(decryptButton);

        panel.add(encryptButton);
        panel.add(decryptButton);

        JLabel keyLabel = new JLabel("Enter Key (6-100 characters):");
        panel.add(keyLabel);
        JTextField keyField = new JTextField();
        panel.add(keyField);

        JLabel dataLabel = new JLabel("Enter Data:");
        panel.add(dataLabel);
        JTextArea dataArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(dataArea);
        panel.add(scrollPane);

        JButton processButton = new JButton("Execute");
        panel.add(processButton);

        JLabel resultLabel = new JLabel("Result:");
        JTextArea resultArea = new JTextArea(5, 20);
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(resultScroll, BorderLayout.CENTER);

        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String action = encryptButton.isSelected() ? "encrypt" : "decrypt";
                String key = keyField.getText();
                String data = dataArea.getText();

                if (key.length() < 6 || key.length() > 100) {
                    JOptionPane.showMessageDialog(frame, "Key must be between 6 and 100 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    if (action.equals("encrypt")) {
                        String result = encrypt(key, data);
                        resultLabel.setText("Encryption Result:");
                        resultArea.setText(result);
                    } else {
                        String result = decrypt(key, data);
                        resultLabel.setText("Decryption Result:");
                        resultArea.setText(result);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }
}
