package com.mahedee;

public class PGPDecryptor {
    public static void main( String[] args )
    {
        // Default directory values
        String defaultSourcePath = "/opt/app/source";
        String defaultDestPath = "/opt/app/dest";
        String defaultPvtKeyPath = "/opt/app/pgp";
        String defaultPassPhrase = "strong-passphrase";

        // If arguments are provided, override the defaults
        String sourcePath = (args.length > 0 && !args[0].isEmpty()) ? args[0] : defaultSourcePath;
        String destPath = (args.length > 1 && !args[1].isEmpty()) ? args[1] : defaultDestPath;
        String pvtKeyPath = (args.length > 2 && !args[2].isEmpty()) ? args[2] : defaultPvtKeyPath;
        String passPhrase = (args.length > 3 && !args[3].isEmpty()) ? args[3] : defaultPassPhrase;

        // Print the directories
        System.out.println("Source Directory: " + sourcePath);
        System.out.println("Destination Directory: " + destPath);
        System.out.println("Private key File Directory: " + pvtKeyPath);
        System.out.println("Passphrase: " + passPhrase);
    }
}
