package service;

import service.StatePersister;
import service.TransactionLog;
import service.ReportGenerator;
import service.BankService;

import service.Repository;
import service.AccountStore;
import model.Account;
import model.SavingsAccount;

import java.nio.file.Path;
import java.io.IOException;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;

import service.DeadLockDemo;
import service.TransactionProcessor;
import model.Account;
import model.SavingsAccount;
import util.WithdrawRule;

import java.util.Scanner;


import static java.lang.System.out;

public class MiniBank {

    record BankInfo(String name,String branch){}
    enum MenuOption{
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        EXIT
    }



    public static void main(String[] args) {

        Account account =
                new SavingsAccount("Asha", 5000, 2000);

        String[] errors =
                AnnotationValidator.validate(account);

        for (String error : errors) {
            out.println(error);
        }

        try {

            account.deposit(1000);
            out.println("Deposit successful.");

            account.withdraw(2000);
            out.println("Withdrawal successful.");

        } catch (InsufficientFundsException e) {

            out.println("Insufficient funds.");
            out.println("Shortfall: " + e.getShortfall());

        } catch (InvalidAmountException e) {

            out.println("Invalid amount: " + e.getMessage());

        } catch (BankException e) {

            out.println("Bank error: " + e.getMessage());

        } finally {

            out.println("Bank operation completed.");
        }

        try {

            Account receiver =
                    new SavingsAccount("Riya", 2000, 1000);

            account.transfer(receiver, 1000);

            out.println("Transfer successful.");

        } catch (BankException e) {

            out.println("Transfer failed: "
                    + e.getMessage());

        } finally {

            out.println("Transfer process completed.");
        }

        try (BankResource resource = new BankResource()) {

            out.println("Using bank resource.");

        }

        out.println("Balance: "
                + account.getBalance());

        out.println("Yearly Interest: "
                + account.yearlyInterest());

        WithdrawRule rule1 = new WithdrawRule() {

            @Override
            public boolean allow(Account account, long amount) {
                return account.canWithdraw(amount);
            }
        };

        out.println("Anonymous class withdrawal: "
                + rule1.allow(account, 2000));


        WithdrawRule rule2 =
                (acc, amount) -> acc.canWithdraw(amount);

        out.println("Lambda withdrawal: "
                + rule2.allow(account, 4000));

        BankInfo bank=new BankInfo("SBI","surat");
        out.println("        " + bank.name());
        out.println("        " + bank.branch());

        // Practical 9: Multithreading test

        Account sharedAccount =
                new SavingsAccount("ThreadTest", 0, 0);

        int threadCount = 10;
        int times = 1000;
        long amount = 1;

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {

            AccountWorker worker =
                    new AccountWorker(sharedAccount, times, amount);

            threads[i] = new Thread(worker, "BankThread-" + (i + 1));

            threads[i].start();
        }

        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Main thread interrupted.");
                return;
            }
        }

        System.out.println("Final balance: "
                + sharedAccount.getBalance());



        // Practical 10: Transaction Processing

        Account transactionAccount =
                new SavingsAccount("TransactionTest", 0, 0);

        TransactionProcessor processor =
                new TransactionProcessor();

        // Deposit tasks
        for (int i = 0; i < 10; i++) {
            processor.submit(() -> {
                try {
                    transactionAccount.deposit(100);
                    System.out.println(
                            Thread.currentThread().getName()
                                    + " deposited 100");
                } catch (Exception e) {
                    System.out.println(
                            "Deposit failed: " + e.getMessage());
                }
            });
        }

        // Withdrawal tasks
        for (int i = 0; i < 5; i++) {
            processor.submit(() -> {
                try {
                    transactionAccount.withdraw(50);
                    System.out.println(
                            Thread.currentThread().getName()
                                    + " withdrew 50");
                } catch (Exception e) {
                    System.out.println(
                            "Withdrawal failed: " + e.getMessage());
                }
            });
        }

        // Wait for all submitted tasks
        processor.stop();

        System.out.println("Transaction processing completed.");
        System.out.println("Transaction account balance: "
                + transactionAccount.getBalance());


        // Practical 10: Producer-Consumer

        TransactionBuffer buffer = new TransactionBuffer();

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    buffer.put(i * 100);
                }
                System.out.println("Producer finished.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    int transaction = buffer.take();
                    System.out.println(
                            "Consumer processed: " + transaction);
                }
                System.out.println("Consumer finished.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Main thread interrupted.");
            return;
        }

        System.out.println("Producer-Consumer completed.");


        // Practical 10: Safe Transfer Demo

        Account accountA =
                new SavingsAccount("1001", 1000, 0);

        Account accountB =
                new SavingsAccount("1002", 1000, 0);

        Thread transfer1 = new Thread(() -> {
            DeadLockDemo.transferSafely(accountA, accountB,100);
        }, "Transfer-A-to-B");

        Thread transfer2 = new Thread(() -> {
            DeadLockDemo.transferSafely(accountB, accountA,100);
        }, "Transfer-B-to-A");

        transfer1.start();
        transfer2.start();

        try {
            transfer1.join();
            transfer2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Main thread interrupted.");
            return;
        }

        System.out.println("Safe transfer demo completed.");


        // Practical 11: Persistence and Reporting

        try {
            Path dataFile = Path.of("data", "accounts.dat");
            Path logsFolder = Path.of("logs");
            Path logFile = logsFolder.resolve("transactions.txt");
            Path reportFile = Path.of("reports", "daily-report.txt");

            // Create accounts to save
            Account[] accountsToSave = {
                    new SavingsAccount("2001", 1000, 0),
                    new SavingsAccount("2002", 2000, 0)
            };

            // Save accounts
            StatePersister.save(accountsToSave, dataFile);
            System.out.println("Accounts saved.");

            // Reload accounts from file
            Account[] loadedAccounts = StatePersister.load(dataFile);

            System.out.println("Reloaded accounts:");
            for (Account loadAccount : loadedAccounts) {
                System.out.println(loadAccount);
            }

            // Write sample transaction log entries
            TransactionLog.append(logFile, "DEPOSIT 100");
            TransactionLog.append(logFile, "WITHDRAW 50");
            TransactionLog.append(logFile, "DEPOSIT 500");

            // Generate end-of-day report
            ReportGenerator.generate(logsFolder, reportFile);
            System.out.println("Report saved to: " + reportFile);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Persistence/report error: "
                    + e.getMessage());
        }


        // Practical 12: Collections Framework
        AccountStore store = new AccountStore();

        Account account1 = new SavingsAccount("3001", 3000, 0);
        Account account2 = new SavingsAccount("3002", 1000, 0);
        Account account3 = new SavingsAccount("3003", 2000, 0);

        store.add(account1);
        store.add(account2);
        store.add(account3);

        System.out.println("\nAccounts sorted by balance:");

        for (Account sortedAccount : store.sortedByBalance()) {
            System.out.println(sortedAccount);
        }

        // Practical 13: Generic Repository
        Repository<Account> repository = new Repository<>();

        Account repoAccount1 = new SavingsAccount("4001", 3000, 0);
        Account repoAccount2 = new SavingsAccount("4002", 1000, 0);
        Account repoAccount3 = new SavingsAccount("4003", 2000, 0);

        repository.saveAll(repoAccount1, repoAccount2, repoAccount3);

        System.out.println("\nAccount found by ID:");
        System.out.println(repository.findById(repoAccount1.getAccountNumber()));

        System.out.println("\nAll repository accounts:");
        for (Account savedAccount : repository.findAll()) {
            System.out.println(savedAccount);
        }

        Scanner sc = new Scanner(System.in);

        int choice;

        BankService bankService = new BankService(store);

        do {
            System.out.println("\n----- MiniBank Menu -----");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Account Statement");
            System.out.println("6. List All Accounts");
            System.out.println("7. Transaction History");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter owner name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter opening balance: ");
                        long balance = sc.nextLong();
                        sc.nextLine();

                        Account acc=
                                bankService.openAccount(name, balance);

                        System.out.println("Account opened successfully!");
                        System.out.println(acc);
                    }

                    case 2 -> {
                        System.out.print("Enter account number: ");
                        String number = sc.nextLine();

                        System.out.print("Enter deposit amount: ");
                        long transferAmount = sc.nextLong();
                        sc.nextLine();

                        bankService.deposit(number, transferAmount);
                        System.out.println("Deposit successful!");
                    }

                    case 3 -> {
                        System.out.print("Enter account number: ");
                        String number = sc.nextLine();

                        System.out.print("Enter withdrawal amount: ");
                        long transferAmount = sc.nextLong();
                        sc.nextLine();

                        bankService.withdraw(number, transferAmount);
                        System.out.println("Withdrawal successful!");
                    }

                    case 4 -> {
                        System.out.print("Enter sender account number: ");
                        String sender = sc.nextLine();

                        System.out.print("Enter receiver account number: ");
                        String receiver = sc.nextLine();

                        System.out.print("Enter transfer amount: ");
                        long transferAmount = sc.nextLong();
                        sc.nextLine();

                        bankService.transfer(sender, receiver, transferAmount);
                        System.out.println("Transfer successful!");
                    }

                    case 5 -> {
                        System.out.print("Enter account number: ");
                        String number = sc.nextLine();

                        System.out.println(bankService.statement(number));
                    }

                    case 6 -> {
                        for (Account acc : bankService.listAccounts()) {
                            System.out.println(acc);
                        }
                    }

                    case 7 -> {
                        for (String transaction : bankService.getTransactionHistory()) {
                            System.out.println(transaction);
                        }
                    }

                    case 8 -> System.out.println("Exiting MiniBank...");

                    default -> System.out.println("Invalid choice!");
                }
            } catch (exception.BankException e) {
                System.out.println("Bank error: " + e.getMessage());
            }

        } while (choice != 8);

        out.println("Thank you for using MiniBank!");
        out.println("Goodbye!");

        sc.close();
    }
    static class BankResource implements AutoCloseable {

        @Override
        public void close() {
            out.println("Bank resource closed.");
        }
    }
}
