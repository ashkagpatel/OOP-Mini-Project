package service;

import model.Account;
import model.CurrentAccount;
import model.Customer;
import model.SavingsAccount;
import model.FixedDepositAccount;

public class AccountDriver {

    public static void main(String[] args) {

        Account a1 = new SavingsAccount("Asha", 5000, 2000);
        Account a2 = new CurrentAccount("Riya", 3000, 5000);

        System.out.println("Accounts:");
        System.out.println(a1);
        System.out.println(a2);

        System.out.println();

        System.out.println(
                "a1 equals a2: " + a1.equals(a2)
        );

        System.out.println();

        if (a1 instanceof Account) {
            System.out.println("a1 is an model.Account");
        }

        Customer customer =
                new Customer(
                        "Ashka",
                        "ashka@gmail.com",
                        "9876543210"
                );

        Customer.Address address =
                new Customer.Address(
                        "Main Road",
                        "Anand",
                        "388001"
                );

        customer.setAddress(address);

        System.out.println();
        System.out.println("model.Customer ID: "
                + customer.getCustomerId());

        System.out.println("City: "
                + customer.getAddress().getCity());

        Customer copy = customer.clone();

        System.out.println("Cloned model.Customer ID: "
                + copy.getCustomerId());

        System.out.println();

        if (copy instanceof Customer) {
            System.out.println("copy is a model.Customer");
        }
    }
}