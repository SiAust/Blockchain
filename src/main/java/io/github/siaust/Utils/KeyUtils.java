package io.github.siaust.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

public class KeyUtils {

    private static final String publicKeyPath = "src/main/resources/publicKey";

    // Method for signature verification that initializes with the Public Key,
    // updates the data to be verified and then verifies them using the signature
    public static boolean verifySignature(List<byte[]> list) throws Exception {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(getPublic());
        sig.update(list.get(0));

        return sig.verify(list.get(1));
    }

    // Method to retrieve the Public Key from a file
    private static PublicKey getPublic() throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static void writeToFile(byte[] key) throws IOException {
        File f = new File(publicKeyPath);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }
}
