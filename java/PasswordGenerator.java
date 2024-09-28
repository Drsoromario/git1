import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.swing.*;

public class PasswordGenerator {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "~!@#%^&*()+{}[]?=-";
    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SYMBOLS;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PasswordGeneratorGUI gui = new PasswordGeneratorGUI();
            gui.setVisible(true);
        });
    }

    public static String generatePassword(int length, boolean includeUppercase, boolean includeLowercase, boolean includeDigits, boolean includeSymbols) {
        if (length < 16 || length > 200) {
            throw new IllegalArgumentException("パスワードの長さは16文字以上200文字以下でなければなりません。");
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        StringBuilder characterPool = new StringBuilder();

        if (includeUppercase) {
            characterPool.append(UPPERCASE);
            password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        }
        if (includeLowercase) {
            characterPool.append(LOWERCASE);
            password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        }
        if (includeDigits) {
            characterPool.append(DIGITS);
            password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        if (includeSymbols) {
            characterPool.append(SYMBOLS);
            password.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
        }

        for (int i = password.length(); i < length; i++) {
            password.append(characterPool.charAt(random.nextInt(characterPool.length())));
        }

        return shuffleString(password.toString());
    }

    private static String shuffleString(String input) {
        List<Character> characters = input.chars()
                                          .mapToObj(c -> (char) c)
                                          .collect(Collectors.toList());
        Collections.shuffle(characters);
        StringBuilder shuffledString = new StringBuilder();
        for (char c : characters) {
            shuffledString.append(c);
        }
        return shuffledString.toString();
    }
}

class PasswordGeneratorGUI extends JFrame {
    private JTextField lengthField;
    private JCheckBox uppercaseCheckBox;
    private JCheckBox lowercaseCheckBox;
    private JCheckBox digitsCheckBox;
    private JCheckBox symbolsCheckBox;
    private JPanel outputPanel;

    public PasswordGeneratorGUI() {
        setTitle("Password Generator");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("Password Length (16-200):"));
        lengthField = new JTextField(10); // Smaller input field
        panel.add(lengthField);

        uppercaseCheckBox = new JCheckBox("Include Uppercase Letters");
        panel.add(uppercaseCheckBox);

        lowercaseCheckBox = new JCheckBox("Include Lowercase Letters");
        panel.add(lowercaseCheckBox);

        digitsCheckBox = new JCheckBox("Include Digits");
        panel.add(digitsCheckBox);

        symbolsCheckBox = new JCheckBox("Include Symbols");
        panel.add(symbolsCheckBox);

        JButton generateButton = new JButton("Generate Passwords");
        generateButton.addActionListener(e -> generatePasswords());
        panel.add(generateButton);

        outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
        panel.add(new JScrollPane(outputPanel));

        add(panel);
    }

    private void generatePasswords() {
        outputPanel.removeAll();
        try {
            int length = Integer.parseInt(lengthField.getText());
            boolean includeUppercase = uppercaseCheckBox.isSelected();
            boolean includeLowercase = lowercaseCheckBox.isSelected();
            boolean includeDigits = digitsCheckBox.isSelected();
            boolean includeSymbols = symbolsCheckBox.isSelected();

            for (int i = 0; i < 6; i++) {
                String password = PasswordGenerator.generatePassword(length, includeUppercase, includeLowercase, includeDigits, includeSymbols);
                JPanel passwordPanel = new JPanel();
                JTextField passwordField = new JTextField(password, 30);
                passwordField.setEditable(false);
                JButton copyButton = new JButton("Copy");
                copyButton.addActionListener(e -> copyToClipboard(password));
                passwordPanel.add(passwordField);
                passwordPanel.add(copyButton);
                outputPanel.add(passwordPanel);
            }
            outputPanel.revalidate();
            outputPanel.repaint();
        } catch (NumberFormatException e) {
            JTextField errorField = new JTextField("Invalid length. Please enter a number between 16 and 200.");
            errorField.setEditable(false);
            outputPanel.add(errorField);
        } catch (IllegalArgumentException e) {
            JTextField errorField = new JTextField(e.getMessage());
            errorField.setEditable(false);
            outputPanel.add(errorField);
        }
    }

    private void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
