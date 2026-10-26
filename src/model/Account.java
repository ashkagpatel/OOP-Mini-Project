package model;

import java.io.Serializable;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;


import model.annotation.Id;
import model.annotation.Positive;
import java.util.Objects;

public abstract class Account implements Transactable, InterestBearing ,Serializable, Comparable<Account>{
    private static final long serialVersionUID = 1L;

    @Id
    private final String accountNumber;

    private String ownerName;

    @Positive
    private long balance;

    private boolean active;

    private static long accountCounter = 0;

    private static String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    public Account(String ownerName, long openingBalance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than 0"
            );
        }

        balance = balance + amount;
    }

    @Override
    public synchronized boolean withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be greater than 0"
            );
        }

        if (!canWithdraw(amount)) {

            long shortfall =
                    amount - getBalance();

            throw new InsufficientFundsException(
                    "Insufficient funds. Shortfall: " + shortfall,
                    shortfall
            );
        }

        balance = balance - amount;

        return true;
    }

    public void transfer(Account to, long amount)
            throws BankException {

        try {

            this.withdraw(amount);

            to.deposit(amount);

        } catch (BankException e) {

            throw e;

        } finally {

            System.out.println("Transfer operation completed.");
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    @Override
    public String toString() {
        return "model.Account Number: " + accountNumber
                + ", Owner: " + ownerName
                + ", Balance: " + balance;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Account)) {
            return false;
        }

        Account other = (Account) o;

        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public int compareTo(Account other) {
        return this.getAccountNumber()
                .compareTo(other.getAccountNumber());
    }
}