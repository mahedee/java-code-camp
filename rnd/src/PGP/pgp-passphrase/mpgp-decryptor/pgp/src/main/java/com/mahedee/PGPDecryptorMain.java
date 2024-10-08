package com.mahedee;

import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;

public class PGPDecryptorMain {

    private static final Logger logger = Logger.getLogger(PGPDecryptorMain.class);

    public static void main(String[] args) {
        logger.info("PGP Decryption started at: "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // Default directory values
        String defaultSourcePath = "files/encrypted-files";
        String defaultDestPath = "files/decrypted-files";
        String defaultPvtKeyPath = "pgp-keys/private_key.pgp";
        String defaultPassphrase = "mySecurePassphrase";  // Default passphrase
        String defaultPathForBadFiles = "files/bad-files";

        // Override the defaults with provided arguments
        String sourcePath = (args.length > 0 && !args[0].isEmpty()) ? args[0] : defaultSourcePath;
        String destPath = (args.length > 1 && !args[1].isEmpty()) ? args[1] : defaultDestPath;
        String pvtKeyPath = (args.length > 2 && !args[2].isEmpty()) ? args[2] : defaultPvtKeyPath;
        String passphrase = (args.length > 3 && !args[3].isEmpty()) ? args[3] : defaultPassphrase;
        String pathForBadFiles = (args.length > 4 && !args[4].isEmpty()) ? args[4] : defaultPathForBadFiles;

        // Log the directories being used
        logger.info("Source Directory: " + sourcePath);
        logger.info("Destination Directory: " + destPath);
        logger.info("Private Key File: " + pvtKeyPath);
        logger.info("Passphrase: " + passphrase);  // Log the passphrase for debugging purposes (be cautious in production!)
        logger.info("Path for bad files: " + pathForBadFiles);

        // Get the private key input stream
        try (InputStream privateKeyStream = new FileInputStream(pvtKeyPath)) {

            // Initialize the PgpDecryptionUtil with the private key and passphrase
            PGPDecryptor decryptionUtil = new PGPDecryptor(privateKeyStream, passphrase);

            // List all files in the source directory
            File sourceDir = new File(sourcePath);
            File[] encryptedFiles = sourceDir.listFiles();

            if (encryptedFiles == null || encryptedFiles.length == 0) {
                logger.info("No files found in the source directory.");
                return;
            }

            // Iterate over each file in the source directory
            for (File encryptedFile : encryptedFiles) {
                if (!encryptedFile.isFile()) {
                    continue;
                }

                // Prepare input stream for the encrypted file
                InputStream encryptedFileStream = new FileInputStream(encryptedFile);

                // Prepare output stream for the decrypted file
                String decryptedFilePath = destPath + File.separator + encryptedFile.getName();
                OutputStream decryptedOutputStream = new FileOutputStream(decryptedFilePath);

                try {
                    // Perform decryption
                    decryptionUtil.decrypt(encryptedFileStream, decryptedOutputStream);

                    // Close streams after processing
                    encryptedFileStream.close();
                    decryptedOutputStream.close();

                    // Delete the original file from sourcePath after decryption
                    Files.delete(encryptedFile.toPath());
                    logger.info("Decrypted and removed: " + encryptedFile.getName());

                } catch (Exception e) {
                    logger.error("Failed to decrypt file: " + encryptedFile.getName(), e);
                    e.printStackTrace();
                    logger.warn("Moving failed file to bad files folder.");

                    try {
                        moveFileToBadFiles(encryptedFile, pathForBadFiles, decryptedFilePath);
                        logger.warn("Moved the file to bad files folder: " + encryptedFile.getName());
                    } catch (IOException ioException) {
                        logger.error("Failed to move the file to bad files folder: " + encryptedFile.getName(), ioException);
                        ioException.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An error occurred during decryption", e);
            System.out.println("An error occurred during decryption: " + e.getMessage());
        }
    }

    // Method to move a file to the bad files directory in case of decryption failure
    private static void moveFileToBadFiles(File encryptedFile, String pathForBadFiles, String decryptedFilePath) throws IOException {
        // Move the file to pathForBadFiles if decryption fails
        File badFile = new File(pathForBadFiles + File.separator + encryptedFile.getName());

        // Move the file from source to destination
        Files.move(encryptedFile.toPath(), badFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Delete the temporary file created for decryption in the decrypted directory
        Path pathToDelete = Paths.get(decryptedFilePath);
        Files.delete(pathToDelete);
    }
}
