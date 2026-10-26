package util;

import model.Account;

public class StatementFormater {

    public static String buildStatement(Account account) {

        StringBuilder statement = new StringBuilder();

        statement.append("===== model.Account Statement =====\n");
        statement.append("model.Account Number: ");
        statement.append(account.getAccountNumber());
        statement.append("\n");

        statement.append("Owner Name: ");
        statement.append(account.getOwnerName());
        statement.append("\n");

        statement.append("Balance: ");
        statement.append(account.getBalance());
        statement.append("\n");

        statement.append("Status: ");
        statement.append(account.isActive());

        return statement.toString();
    }
}