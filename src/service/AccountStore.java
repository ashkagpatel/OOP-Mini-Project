
package service;

import model.Account;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;

public class AccountStore {

    private HashMap<String, Account> accounts =
            new HashMap<>();

    private ArrayList<String> transactionHistory =
            new ArrayList<>();

    // Add account
    public void add(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    // Find account using account number
    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    // Get all accounts
    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    // Add transaction to history
    public void addTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    // Get transaction history
    public ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    // Sort accounts by balance
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> sorted = getAll();

        sorted.sort(Comparator.comparingLong(
                Account::getBalance
        ));

        return sorted;
    }
}
