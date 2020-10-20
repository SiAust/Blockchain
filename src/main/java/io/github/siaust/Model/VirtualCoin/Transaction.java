package io.github.siaust.Model.VirtualCoin;

import java.io.Serializable;

public class Transaction implements Serializable {

    private static final long serialVersionUID = -2451521382313591788L;
    private final Account from;
    private final Account to;
    private final long coins;
    private final int messageID;

    public Transaction(Account from, Account to, long coins,  int messageID) {
        this.from = from;
        this.to = to;
        this.coins = coins;
        this.messageID = messageID;
    }

    public Account getFrom() {
        return from;
    }

    public Account getTo() {
        return to;
    }

    public long getCoins() {
        return coins;
    }

    public int getMessageID() {
        return messageID;
    }

    public boolean completeTransaction() {
        if (from.withdraw(coins) == 1) {
            if (to.deposit(coins) == 1) {
                return true;
            } else { // the amount couldn't be deposited, refund from account
                from.deposit(coins);
//                return false;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s sent %d VC to %s", from.getOwner(), coins, to.getOwner());
    }
}
