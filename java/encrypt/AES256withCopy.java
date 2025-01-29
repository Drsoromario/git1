import javax.crypto.*;
import javax.crypto.spec.*;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class AES256withCopy extends JFrame {
    private JRadioButton encryptRadio, decryptRadio;
    private JPasswordField passwordField;
    private JTextArea inputArea, outputArea;
    private ButtonGroup modeGroup;
    private JButton toggleVisibilityBtn;
    private boolean isPasswordVisible = false;

    private static final int ITERATIONS = 100000;
    private static final int KEY_LENGTH = 256;

    public AES256withCopy() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("AES256 暗号化/復号化ツール");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // モード選択パネル
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        encryptRadio = new JRadioButton("暗号化", true);
        decryptRadio = new JRadioButton("復号化");
        modeGroup = new ButtonGroup();
        modeGroup.add(encryptRadio);
        modeGroup.add(decryptRadio);
        modePanel.add(new JLabel("モード選択:"));
        modePanel.add(encryptRadio);
        modePanel.add(decryptRadio);

        // 共通鍵入力パネル
        JPanel keyPanel = new JPanel(new BorderLayout(5, 5));

        passwordField = new JPasswordField();
        passwordField.setEchoChar('●'); // エコーキャラクターを黒丸に設定

        // 鍵の表示/非表示ボタン
        toggleVisibilityBtn = new JButton("鍵を表示");
        toggleVisibilityBtn.addActionListener(e -> togglePasswordVisibility());

        keyPanel.add(new JLabel("共通鍵 (7-200文字):"), BorderLayout.NORTH);
        keyPanel.add(passwordField, BorderLayout.CENTER);
        keyPanel.add(toggleVisibilityBtn, BorderLayout.EAST);

        // 入力と出力エリア
        inputArea = new JTextArea(8, 20);
        outputArea = new JTextArea(8, 20);
        outputArea.setEditable(false);

        JScrollPane inputScroll = new JScrollPane(inputArea);
        JScrollPane outputScroll = new JScrollPane(outputArea);

        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton processBtn = new JButton("処理実行");
        JButton copyBtn = new JButton("コピー");

        processBtn.addActionListener(e -> processData());
        copyBtn.addActionListener(e -> copyToClipboard());

        buttonPanel.add(processBtn);
        buttonPanel.add(copyBtn);

        // メインパネルにコンポーネントを配置
        mainPanel.add(modePanel, BorderLayout.NORTH);
        mainPanel.add(keyPanel, BorderLayout.CENTER);
        mainPanel.add(inputScroll, BorderLayout.WEST);
        mainPanel.add(outputScroll, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordField.setEchoChar('●'); // パスワードを隠す
            toggleVisibilityBtn.setText("鍵を表示");
        } else {
            passwordField.setEchoChar((char) 0); // パスワードを表示
            toggleVisibilityBtn.setText("鍵を非表示");
        }
        isPasswordVisible = !isPasswordVisible;
    }

    private void processData() {
        try {
            String password = new String(passwordField.getPassword());

            if (password.length() < 7) {
                JOptionPane.showMessageDialog(this, "共通鍵は7文字以上必要です", "エラー", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String input = inputArea.getText();
            if (encryptRadio.isSelected()) {
                outputArea.setText(encrypt(input, password));
            } else {
                outputArea.setText(decrypt(input, password));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "処理中にエラーが発生しました: " + ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String encrypt(String plaintext, String password) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
        byte[] combined = new byte[salt.length + iv.length + encrypted.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(iv, 0, combined, salt.length, iv.length);
        System.arraycopy(encrypted, 0, combined, salt.length + iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    private String decrypt(String ciphertext, String password) throws Exception {
        byte[] combined = Base64.getDecoder().decode(ciphertext);
        byte[] salt = new byte[16];
        byte[] iv = new byte[16];
        byte[] encrypted = new byte[combined.length - 32];

        System.arraycopy(combined, 0, salt, 0, salt.length);
        System.arraycopy(combined, salt.length, iv, 0, iv.length);
        System.arraycopy(combined, salt.length + iv.length, encrypted, 0, encrypted.length);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, "UTF-8");
    }

    private void copyToClipboard() {
        String output = outputArea.getText();
        if (!output.isEmpty()) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(output), null);
            JOptionPane.showMessageDialog(this, "クリップボードにコピーしました", "情報", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            AES256withCopy app = new AES256withCopy();
            app.setVisible(true);
        });
    }
}
