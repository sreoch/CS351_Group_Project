package server;

import shared.Account;
import java.util.ArrayList;
import java.util.Collection;

public class InterestThread implements Runnable {
    private volatile Collection<Account> accounts;
    private volatile double rate;
    private Server server;

    public InterestThread(Server server, double rate, int period) {
        this.accounts = server.getAccounts().values();
        this.server = server;
        this.rate = rate;
    }

    public void updateAccounts(Collection<Account> accounts) {
        this.accounts = accounts;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public void run() {
        System.out.println("Adding interest");
        for (Account account : this.accounts) {
            server.addInterest(account, rate);
        }
    }
}
