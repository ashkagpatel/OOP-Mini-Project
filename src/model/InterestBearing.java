package model;

public interface InterestBearing {

    default double yearlyInterest() {

        return interestRate() / 100
                * getBalance();
    }

    double interestRate();

    long getBalance();
}