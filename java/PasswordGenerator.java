import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Scanner;

public class PasswordGenerator {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "@#$%&()^+*?<>";
    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SYMBOLS;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int length;
        do{
            System.out.print("The length of password:");
            length = scan.nextInt();
        }while(length < 11 || length > 50);
        String password = generatePassword(length);
        System.out.println("Generated Password: " + password);
    }

    public static String generatePassword(int length) {
        if (length < 11 || length > 50) {
            throw new IllegalArgumentException("パスワードの長さは11文字以上50文字以下でなければなりません。");
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        // 各カテゴリから最低1文字を追加
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));

        // 残りの文字をランダムに追加
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        // パスワードをシャッフル
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
