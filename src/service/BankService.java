
package service;

import model.Account;
import model.SavingsAccount;
import java.util.ArrayList;

public class BankService {

    private AccountStore accountStore;

    public BankService(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    public ArrayList<Account> listAccounts() {
        return accountStore.getAll();
    }

    public Account findAccount(String accountNumber) {
        return accountStore.get(accountNumber);
    }


    public void deposit(String accountNumber, long amount)
            throws exception.BankException {

        Account account = findAccount(accountNumber);

        if (account == null) {
            throw new exception.BankException("Account not found");
        }

        account.deposit(amount);
        accountStore.addTransaction(
                "Deposit: " + amount + " to " + accountNumber
        );
    }

    public void withdraw(String accountNumber, long amount)
            throws exception.BankException {

        Account account = findAccount(accountNumber);

        if (account == null) {
            throw new exception.BankException("Account not found");
        }

        account.withdraw(amount);
        accountStore.addTransaction(
                "Withdrawal: " + amount + " from " + accountNumber
        );
    }

    public void transfer(String fromAccount, String toAccount, long amount)
            throws exception.BankException {

        Account sender = findAccount(fromAccount);
        Account receiver = findAccount(toAccount);

        if (sender == null || receiver == null) {
            throw new exception.BankException("Account not found");
        }

        sender.transfer(receiver, amount);

        accountStore.addTransaction(
                "Transfer: " + amount + " from "
                        + fromAccount + " to " + toAccount
        );
    }

    public Account openAccount(String ownerName, long openingBalance) {
        Account account = new SavingsAccount(ownerName, openingBalance, 0);
        accountStore.add(account);

        accountStore.addTransaction(
                "Account opened: " + account.getAccountNumber()
        );

        return account;
    }

    public String statement(String accountNumber) {
        Account account = findAccount(accountNumber);

        if (account == null) {
            return "Account not found.";
        }

        return "Account Number: " + account.getAccountNumber()
                + "\nOwner Name: " + account.getOwnerName()
                + "\nBalance: " + account.getBalance();
    }


    public ArrayList<String> getTransactionHistory() {
        return accountStore.getTransactionHistory();
    }

}
