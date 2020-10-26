package io.github.siaust.Model.VirtualCoin;

import java.io.Serializable;

public class Account implements Serializable, Comparable<Account> {

    private static final long serialVersionUID = 6346637387496540407L;

    private final String owner;
    private long balance;

    public Account(String owner, long balance) {
        this.owner = owner;
        this.balance = balance;
    }

    /** Deposit coins into the balance.
     * @return If successful, i.e a positive sum was passed as an arg, 1. Will not accept
     * zero or a negative value, returns -1. */
    public int deposit(long coins) {
        if (coins > 0) {
            this.balance += coins;
            return 1;
        }
        return -1;
    }

    /** Withdraw coins from balance. Will not allow balance to become overdrawn,
     * i.e balance less than 0
     * @return 1 is withdrawal was successful, -1 if withdrawal couldn't be completed
     * as it would have left account overdrawn. */
    public int withdraw(long coins) {
        if (this.balance - coins < 0) {
            return -1;
        }
        this.balance -= coins;
        return 1;
    }

    public long getBalance() { return this.balance; }

    public String getOwner() { return this.owner; }

    @Override
    public String toString() {
        return String.format("{\"account_holder\":\"%s\"," +
                "\"account_balance\":%d}", this.owner, this.balance);
    }

    @Override
    public int compareTo(Account o) {
        return Integer.compare((int) o.balance, (int) this.balance);
    }
}
