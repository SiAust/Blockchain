package model;

import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

public class Message {

    List<byte[]> list;

    private String name;
    private String msgContent;

    public Message() {}

    public Message(String name, String msgContent, String keyFile) {
        this.list = new ArrayList<>();

        this.name = name;
        this.msgContent = msgContent;

        this.list.add(this.toString().getBytes()); // our message as a byte[]
        try {
            this.list.add(sign(this.toString(), keyFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //The method that signs the data using the private key that is stored in keyFile path
    public byte[] sign(String data, String keyFile) throws InvalidKeyException, Exception{
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(getPrivate(keyFile));
        rsa.update(data.getBytes());
        return rsa.sign();
    }

    //Method to retrieve the Private Key from a file
    public PrivateKey getPrivate(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    //Method to write the List of byte[] to a file
    public void writeToFile(String filename) throws FileNotFoundException, IOException {
        File f = new File(filename);
        f.getParentFile().mkdirs();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
        out.writeObject(list);
        out.close();
        System.out.println("Your file is ready.");
    }

    public List<byte[]> getList() {
        return list;
    }

    @Override
    public String toString() {
        return name + ": " + msgContent;

    }
}
