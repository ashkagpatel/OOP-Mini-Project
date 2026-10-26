package service;

import model.Account;
import model.SavingsAccount;
import util.Command;
import util.CommandParser;
import util.StatementFormater;
import util.Validator;

public class ValidationDriver {

    public static void main(String[] args) {

        // Mobile
        System.out.println(
                "Mobile 9876543210: "
                        + Validator.isValidMobile("9876543210")
        );

        System.out.println(
                "Mobile 12345: "
                        + Validator.isValidMobile("12345")
        );

        // Email
        System.out.println(
                "Email asha@gmail.com: "
                        + Validator.isValidEmail("asha@gmail.com")
        );

        System.out.println(
                "Email asha@gmail: "
                        + Validator.isValidEmail("asha@gmail")
        );

        // PAN
        System.out.println(
                "PAN ABCDE1234F: "
                        + Validator.isValidPan("ABCDE1234F")
        );

        System.out.println(
                "PAN ABC123: "
                        + Validator.isValidPan("ABC123")
        );

        // IFSC
        System.out.println(
                "IFSC SBIN0001234: "
                        + Validator.isValidIfsc("SBIN0001234")
        );

        System.out.println(
                "IFSC SBIN123: "
                        + Validator.isValidIfsc("SBIN123")
        );

        // util.Command Parser
        System.out.println();
        System.out.println("===== util.Command =====");

        Command command =
                CommandParser.parse(
                        "DEPOSIT AC0001 500"
                );

        System.out.println("Type: "
                + command.type());

        System.out.println("model.Account: "
                + command.accountNumber());

        System.out.println("Amount: "
                + command.amount());

        // Statement
        System.out.println();
        System.out.println(
                "===== Statement ====="
        );

        Account account =
                new SavingsAccount("Asha", 5000, 2000);

        System.out.println(
                StatementFormater.buildStatement(account)
        );
    }
}