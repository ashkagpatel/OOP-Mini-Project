
package service;

import model.Account;

public class DeadLockDemo {


    public static void transferSafely(Account a, Account b, long amount) {
        Account first;
        Account second;

        if (a.getAccountNumber().compareTo(b.getAccountNumber()) < 0) {
            first = a;
            second = b;
        } else {
            first = b;
            second = a;
        }

        synchronized (first) {
            synchronized (second) {
                try {
                    a.withdraw(amount);
                    b.deposit(amount);

                    System.out.println(
                            Thread.currentThread().getName()
                                    + ": Transfer successful.");
                } catch (Exception e) {
                    System.out.println(
                            "Transfer failed: " + e.getMessage());
                }
            }
        }
    }

}
