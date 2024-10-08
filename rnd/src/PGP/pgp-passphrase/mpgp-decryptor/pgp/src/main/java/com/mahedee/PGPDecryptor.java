package com.mahedee;

import org.apache.log4j.Logger;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.Security;
import java.util.Iterator;
import java.util.Objects;

public class PGPDecryptor {

    private static final Logger logger = Logger.getLogger(PGPDecryptor.class);

    static {
        // Add BouncyCastle to JVM
        if (Objects.isNull(Security.getProvider(BouncyCastleProvider.PROVIDER_NAME))) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private final PGPSecretKeyRingCollection pgpSecretKeyRingCollection;
    private final String passphrase;

    // Constructor with passphrase
    public PGPDecryptor(InputStream privateKeyIn, String passphrase) throws IOException, PGPException {
        this.pgpSecretKeyRingCollection = new PGPSecretKeyRingCollection(
                PGPUtil.getDecoderStream(privateKeyIn),
                new JcaKeyFingerprintCalculator()
        );
        this.passphrase = passphrase;
        logger.info("Initialized PGPDecryptor with private key and passphrase");
    }

    // Overloaded constructor to support key string input
    public PGPDecryptor(String privateKeyStr, String passphrase) throws IOException, PGPException {
        this(IOUtils.toInputStream(privateKeyStr, Charset.defaultCharset()), passphrase);
    }

    // Method to find the secret key using the passphrase
    private PGPPrivateKey findSecretKey(long keyID) throws PGPException {
        PGPSecretKey pgpSecretKey = pgpSecretKeyRingCollection.getSecretKey(keyID);
        if (pgpSecretKey == null) {
            return null;
        }
        // Decrypt the private key using the passphrase
        return pgpSecretKey.extractPrivateKey(
                new JcePBESecretKeyDecryptorBuilder()
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                        .build(passphrase.toCharArray())
        );
    }

    // Decrypt method
    public void decrypt(InputStream encryptedIn, OutputStream clearOut)
            throws PGPException, IOException {
        encryptedIn = PGPUtil.getDecoderStream(encryptedIn);
        JcaPGPObjectFactory pgpObjectFactory = new JcaPGPObjectFactory(encryptedIn);

        Object obj = pgpObjectFactory.nextObject();
        PGPEncryptedDataList pgpEncryptedDataList = (obj instanceof PGPEncryptedDataList)
                ? (PGPEncryptedDataList) obj
                : (PGPEncryptedDataList) pgpObjectFactory.nextObject();

        PGPPrivateKey pgpPrivateKey = null;
        PGPPublicKeyEncryptedData publicKeyEncryptedData = null;

        Iterator<PGPEncryptedData> encryptedDataItr = pgpEncryptedDataList.getEncryptedDataObjects();
        while (pgpPrivateKey == null && encryptedDataItr.hasNext()) {
            publicKeyEncryptedData = (PGPPublicKeyEncryptedData) encryptedDataItr.next();
            pgpPrivateKey = findSecretKey(publicKeyEncryptedData.getKeyID());
        }

        if (Objects.isNull(publicKeyEncryptedData)) {
            throw new PGPException("Could not generate PGPPublicKeyEncryptedData object");
        }

        if (pgpPrivateKey == null) {
            throw new PGPException("Could not extract private key using the provided passphrase");
        }

        CommonUtils.decrypt(clearOut, pgpPrivateKey, publicKeyEncryptedData);
    }

    // Overloaded decrypt method to handle byte arrays
    public byte[] decrypt(byte[] encryptedBytes) throws PGPException, IOException {
        ByteArrayInputStream encryptedIn = new ByteArrayInputStream(encryptedBytes);
        ByteArrayOutputStream clearOut = new ByteArrayOutputStream();
        decrypt(encryptedIn, clearOut);
        return clearOut.toByteArray();
    }
}
