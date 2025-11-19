package server;

import shared.Account;

public class InterestThread implements Runnable {
    private volatile double rate;
    private Server server;

    public InterestThread(Server server, double rate) {
        this.server = server;
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public void run() {
        System.out.println("Applying " + (rate * 100) + "% interest to all accounts...");
        for (Account account : server.getAccounts().values()) {
            server.addInterest(account, rate);
        }
        System.out.println("Interest applied successfully");
    }
}
