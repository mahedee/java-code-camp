package com.malkeith.pgpUtils;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Objects;

public class PgpEncryptionUtil {

    private int compressionAlgorithm;
    private int symmetricKeyAlgorithm;
    private boolean armor;
    private boolean withIntegrityCheck;
    private int bufferSize;

    // Static block to initialize Bouncy Castle provider
    static {
        if (Objects.isNull(Security.getProvider(BouncyCastleProvider.PROVIDER_NAME))) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // Constructor with default values
    public PgpEncryptionUtil() {
        this.compressionAlgorithm = CompressionAlgorithmTags.ZIP;
        this.symmetricKeyAlgorithm = SymmetricKeyAlgorithmTags.AES_128;
        this.armor = true;
        this.withIntegrityCheck = true;
        this.bufferSize = 1 << 16;
    }

    // Constructor to initialize with specific values
    public PgpEncryptionUtil(int compressionAlgorithm, int symmetricKeyAlgorithm, boolean armor, boolean withIntegrityCheck, int bufferSize) {
        this.compressionAlgorithm = compressionAlgorithm;
        this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
        this.armor = armor;
        this.withIntegrityCheck = withIntegrityCheck;
        this.bufferSize = bufferSize;
    }

    // Getter methods
    public int getCompressionAlgorithm() {
        return compressionAlgorithm;
    }

    public int getSymmetricKeyAlgorithm() {
        return symmetricKeyAlgorithm;
    }

    public boolean isArmor() {
        return armor;
    }

    public boolean isWithIntegrityCheck() {
        return withIntegrityCheck;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    // Encrypt method
    public void encrypt(OutputStream encryptOut, InputStream clearIn, long length, InputStream publicKeyIn)
            throws IOException, PGPException {
        PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(compressionAlgorithm);
        PGPEncryptedDataGenerator pgpEncryptedDataGenerator = new PGPEncryptedDataGenerator(
                new JcePGPDataEncryptorBuilder(symmetricKeyAlgorithm)
                        .setWithIntegrityPacket(withIntegrityCheck)
                        .setSecureRandom(new SecureRandom())
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME)
        );

        // Add public key
        pgpEncryptedDataGenerator.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(
                CommonUtils.getPublicKey(publicKeyIn)));

        // Wrap the output stream with ArmoredOutputStream if armor is true
        if (armor) {
            encryptOut = new ArmoredOutputStream(encryptOut);
        }

        OutputStream cipherOutStream = pgpEncryptedDataGenerator.open(encryptOut, new byte[bufferSize]);
        CommonUtils.copyAsLiteralData(compressedDataGenerator.open(cipherOutStream), clearIn, length, bufferSize);

        // Closing all streams
        compressedDataGenerator.close();
        cipherOutStream.close();
        encryptOut.close();
    }

    // Overload method for encrypting byte array
    public byte[] encrypt(byte[] clearData, InputStream publicKeyIn) throws PGPException, IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(clearData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encrypt(outputStream, inputStream, clearData.length, publicKeyIn);
        return outputStream.toByteArray();
    }

    // Overload method for encrypting InputStream
    public InputStream encrypt(InputStream clearIn, long length, InputStream publicKeyIn) throws IOException, PGPException {
        File tempFile = File.createTempFile("pgp-", "-encrypted");
        encrypt(Files.newOutputStream(tempFile.toPath()), clearIn, length, publicKeyIn);
        return Files.newInputStream(tempFile.toPath());
    }

    // Overload method for encrypting byte array with public key as String
    public byte[] encrypt(byte[] clearData, String publicKeyStr) throws PGPException, IOException {
        return encrypt(clearData, IOUtils.toInputStream(publicKeyStr, Charset.defaultCharset()));
    }

    // Overload method for encrypting InputStream with public key as String
    public InputStream encrypt(InputStream clearIn, long length, String publicKeyStr) throws IOException, PGPException {
        return encrypt(clearIn, length, IOUtils.toInputStream(publicKeyStr, Charset.defaultCharset()));
    }

    // Custom builder class to replicate @Builder functionality of Lombok
    public static class Builder {
        private int compressionAlgorithm = CompressionAlgorithmTags.ZIP;
        private int symmetricKeyAlgorithm = SymmetricKeyAlgorithmTags.AES_128;
        private boolean armor = true;
        private boolean withIntegrityCheck = true;
        private int bufferSize = 1 << 16;

        public Builder compressionAlgorithm(int compressionAlgorithm) {
            this.compressionAlgorithm = compressionAlgorithm;
            return this;
        }

        public Builder symmetricKeyAlgorithm(int symmetricKeyAlgorithm) {
            this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
            return this;
        }

        public Builder armor(boolean armor) {
            this.armor = armor;
            return this;
        }

        public Builder withIntegrityCheck(boolean withIntegrityCheck) {
            this.withIntegrityCheck = withIntegrityCheck;
            return this;
        }

        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public PgpEncryptionUtil build() {
            return new PgpEncryptionUtil(compressionAlgorithm, symmetricKeyAlgorithm, armor, withIntegrityCheck, bufferSize);
        }
    }

    // Static builder method to initiate the builder
    public static Builder builder() {
        return new Builder();
    }
}
