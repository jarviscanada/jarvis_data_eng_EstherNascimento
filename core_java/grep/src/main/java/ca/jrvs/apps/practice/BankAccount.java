package ca.jrvs.apps.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

public class BankAccount {


    private String accountNumber;
    private String ownerName;
    private double balance;
    private List<Double> transactions;


    public final static int THRESHOLD = 1000;


    public BankAccount(String accountNumber, String ownerName, double startingBalance) {
        if (startingBalance < 0) {
            throw new IllegalArgumentException("Starting balance cannot be negative. Received: " + startingBalance);
        }
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = startingBalance;
        this.transactions = new ArrayList<>();
    }


    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero. Received: " + amount);
        }

        this.balance += amount;
        this.transactions.add(amount);
    }


    public void withdraw(double amount) throws InsufficientFundsException {

        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero. Received: " + amount);
        }


        if (amount > this.balance) {
            throw new InsufficientFundsException("Insufficient funds. Current balance: " + String.format("%.2f", balance) + ", Requested: " + amount);
        }

        this.balance -= amount;
        this.transactions.add(-amount);
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountInfo() {
        return "Account: " + accountNumber + "\n" +
                "Owner: " + ownerName + "\n" +
                "Balance: " + String.format("%.2f", balance);
    }



    public double getTotalDeposited() {
        return transactions.stream()
                .filter(t -> t > 0)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public double getTotalWithdrawn() {

        return Math.abs(transactions.stream()
                .filter(t -> t < 0)
                .mapToDouble(Double::doubleValue)
                .sum());
    }

    public List<Double> getAllDeposits() {
        return transactions.stream()
                .filter(t -> t > 0)
                .collect(Collectors.toList());
    }

    // Step 5: Main Method for testing
    public static void main(String[] args) {
        BankAccount myAccount = new BankAccount("12345", "Alice", 500.0);

        try {

            myAccount.deposit(150.0);
            myAccount.withdraw(45.0);


            System.out.println("--- Account Info ---");
            System.out.println(myAccount.getAccountInfo());

            System.out.println("\n--- Transaction Statistics ---");
            System.out.println("Total Deposited: " + myAccount.getTotalDeposited());
            System.out.println("Total Withdrawn: " + myAccount.getTotalWithdrawn());
            System.out.println("All Deposit Amounts: " + myAccount.getAllDeposits());



        } catch (InsufficientFundsException e) {
            System.err.println("BANK ERROR: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("INPUT ERROR: " + e.getMessage());
        }
    }
}