import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.*;
import java.security.Security;
import java.util.Iterator;

public class PGPFileDecryptor {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void decryptFile(String inputFilePath, String outputFilePath, String privateKeyPath, String passphrase) throws Exception {
        // Read the private key from the file
        PGPPrivateKey privateKey = readPrivateKey(privateKeyPath, passphrase);

        // Decrypt the file
        try (InputStream fileInput = new BufferedInputStream(new FileInputStream(inputFilePath));
             InputStream decoderStream = PGPUtil.getDecoderStream(fileInput);
             FileOutputStream fos = new FileOutputStream(outputFilePath)) {

            PGPObjectFactory pgpFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());
            Object object = pgpFactory.nextObject();

            if (object instanceof PGPEncryptedDataList) {
                PGPEncryptedDataList encryptedDataList = (PGPEncryptedDataList) object;
                decryptData(encryptedDataList, privateKey, fos);
            } else {
                throw new PGPException("Invalid input file format.");
            }
        }

        System.out.println("File decrypted successfully!");
    }

    private static PGPPrivateKey readPrivateKey(String privateKeyFilePath, String passphrase) throws IOException, PGPException {
        try (InputStream keyIn = new BufferedInputStream(new FileInputStream(privateKeyFilePath))) {
            InputStream decoderStream = PGPUtil.getDecoderStream(keyIn);
            PGPObjectFactory pgpFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());
            Object object = pgpFactory.nextObject();

            if (object instanceof PGPSecretKeyRingCollection) {
                PGPSecretKeyRingCollection secretKeyRingCollection = (PGPSecretKeyRingCollection) object;

                // Iterate over key rings to find the private key
                for (Iterator<PGPSecretKeyRing> keyRingIterator = secretKeyRingCollection.getKeyRings(); keyRingIterator.hasNext(); ) {
                    PGPSecretKeyRing keyRing = keyRingIterator.next();
                    PGPSecretKey secretKey = keyRing.getSecretKey();
                    return decryptPrivateKey(secretKey, passphrase);
                }
            } else if (object instanceof PGPSecretKeyRing) {
                PGPSecretKeyRing keyRing = (PGPSecretKeyRing) object;
                PGPSecretKey secretKey = keyRing.getSecretKey();
                return decryptPrivateKey(secretKey, passphrase);
            }

            throw new IllegalArgumentException("No private key found in the private key ring.");
        }
    }

    private static PGPPrivateKey decryptPrivateKey(PGPSecretKey secretKey, String passphrase) throws PGPException {
        PGPPrivateKey privateKey = null;
        if (secretKey != null) {
            PGPPrivateKey key = secretKey.extractPrivateKey(
                    new JcePBEDataDecryptorFactoryBuilder().setProvider("BC").build(passphrase.toCharArray())
            );
            privateKey = key;
        }
        return privateKey;
    }

    private static void decryptData(PGPEncryptedDataList encryptedDataList, PGPPrivateKey privateKey, OutputStream outStream) throws IOException, PGPException {
        PGPPublicKeyEncryptedData encryptedData = null;

        // Find the encrypted data for the correct private key
        for (Iterator<PGPPublicKeyEncryptedData> it = encryptedDataList.getEncryptedDataObjects(); it.hasNext(); ) {
            encryptedData = it.next();
            if (encryptedData.getKeyID() == privateKey.getKeyID()) {
                break;
            }
        }

        if (encryptedData == null) {
            throw new IllegalArgumentException("No encrypted data found for the provided private key.");
        }

        // Decrypt the file
        InputStream clearData = encryptedData.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(privateKey));
        PGPObjectFactory plainFactory = new PGPObjectFactory(clearData, new JcaKeyFingerprintCalculator());
        Object message = plainFactory.nextObject();

        if (message instanceof PGPCompressedData) {
            PGPCompressedData compressedData = (PGPCompressedData) message;
            plainFactory = new PGPObjectFactory(compressedData.getDataStream(), new JcaKeyFingerprintCalculator());
            message = plainFactory.nextObject();
        }

        if (message instanceof PGPLiteralData) {
            PGPLiteralData literalData = (PGPLiteralData) message;
            try (InputStream literalInput = literalData.getInputStream()) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = literalInput.read(buffer)) > 0) {
                    outStream.write(buffer, 0, len);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String inputFilePath = "encrypted_sample.txt";
        String outputFilePath = "decrypted_sample.txt";
        String privateKeyPath = "private_key.asc";
        String passphrase = "strong-passphrase";

        decryptFile(inputFilePath, outputFilePath, privateKeyPath, passphrase);
    }
}
