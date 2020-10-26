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

    public void deserializeAccounts() {
        try {
            Accounts accounts = (Accounts) SerializationUtils.deserialize(FILEPATH);
            accountsList = accounts.getAccounts();
//            try {
//                accountsList.entrySet()
//                        .stream()
//                        .sorted(Map.Entry.comparingByKey())
//                        .forEach(e -> System.out.println(e.getValue())); // sort in alphabetical order
//            } catch (Exception e) {
////                e.printStackTrace();
//            }
            System.out.println("Accounts deserialized");
        } catch (IOException | ClassNotFoundException e) {
            /* No accounts exist so we create a Blockchain account with lots of coins */
            accountsList.put("Blockchain", new Account("Blockchain", 10_000));
            System.out.println("No accounts found");
//            e.printStackTrace();
        }
    }

    public void serializeAccounts() {
        try {
            SerializationUtils.serialize(this, FILEPATH);
            System.out.println("Accounts serialized");
            // fixme: causes ArrayOutOfBounds Exception on first serialization
//            accountsList.entrySet()
//                    .stream()
//                    .sorted(Map.Entry.comparingByKey())
//                    .forEach(e -> System.out.println(e.getValue())); // sort in alphabetical order
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Account getOrCreateAccount(String name) {
       if (accountsList.containsKey(name)) {
           return accountsList.get(name);
       }
       accountsList.put(name, new Account(name, 100)); // All new accounts credited with 100 coins
       return accountsList.get(name);
    }

    public Map<String, Account> getAccounts() {
        return this.accountsList;
    }
}
