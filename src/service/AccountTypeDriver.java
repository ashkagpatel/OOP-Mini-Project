package service;

import model.Account;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;

public class AccountTypeDriver {

    public static void main(String[] args) {

        Account[] accounts = new Account[3];

        accounts[0] =
                new SavingsAccount("Asha", 5000, 2000);

        accounts[1] =
                new CurrentAccount("Riya", 1000, 5000);

        accounts[2] =
                new FixedDepositAccount("Neha", 10000);

        for (Account account : accounts) {

            System.out.println("model.Account: "
                    + account.getAccountNumber());

            System.out.println("Owner: "
                    + account.getOwnerName());

            System.out.println("Balance: "
                    + account.getBalance());

            System.out.println("Interest Rate: "
                    + account.interestRate() + "%");

            System.out.println();
        }

        if (accounts[0] instanceof SavingsAccount savings) {

            System.out.println(
                    "Savings account detected."
            );
        }
    }
}