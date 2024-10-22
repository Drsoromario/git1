import java.util.Arrays;

public class SortNumbers {
    public static void main(String[] args) {
        // 入力した数列
        int[] numbers = {99389,99391,106866,106868,211459,428967,1687009,2258452,2258454,2258459,2258461,2537447,2836183,2837582,2837584,2841730,2841732,2850061,2850063,2850065,504933};

        // 昇順に並び替え
        Arrays.sort(numbers);

        // 結果を表示
        System.out.println("昇順に並び替えた数列:");
        for (int number : numbers) {
            System.out.println(number);
        }
    }
}