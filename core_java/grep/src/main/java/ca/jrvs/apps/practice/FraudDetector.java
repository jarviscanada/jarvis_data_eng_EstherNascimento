package ca.jrvs.apps.practice;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FraudDetector {

    public static List<Integer> detectFraud(List<Integer> transactions, int threshold) {
        List<Integer> suspicious = new ArrayList<>();

        for (Integer amount : transactions) {
            if (amount > threshold) {
                suspicious.add(amount);
            }
        }

        return suspicious;
    }

    public static void main(String[] args) {
        List<Integer> transactions = Arrays.asList(20, 40, 5000, 30);
        int threshold = 1000; //should be constant value

        List<Integer> result = detectFraud(transactions, threshold);
        System.out.println("Suspicious Transactions: " + result);
    }
}