package io.github.siaust.Model.VirtualCoin;

import io.github.siaust.Utils.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Accounts implements Serializable {

    private static final long serialVersionUID = -5417481895764118780L;
    private static final String FILEPATH = ".\\accounts.data";

    private Map<String, Account> accountsList = new HashMap<>();

    public Accounts() {
        deserializeAccounts();
    }

    public boolean deserializeAccounts() {
        try {
            Accounts accounts = (Accounts) SerializationUtils.deserialize(FILEPATH);
            accountsList = accounts.getAccounts();
            accountsList.forEach((k, v) -> System.out.println(v));
            System.out.println("Accounts deserialized");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            /* No accounts exist so we create a Blockchain account with lots of coins */
            accountsList.put("Blockchain", new Account("Blockchain", Long.MAX_VALUE));
            System.out.println("No accounts.data found");
//            e.printStackTrace();
        }
        return false;
    }

    public boolean serializeAccounts() {
        try {
            SerializationUtils.serialize(this, FILEPATH);
            System.out.println("Accounts serialized");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Account getOrCreateAccount(String name) {
       if (accountsList.containsKey(name)) {
           return accountsList.get(name);
       }
       accountsList.put(name, new Account(name, 100L));
       return accountsList.get(name);
    }

    private Map<String, Account> getAccounts() {
        return this.accountsList;
    }
}
