https://www.tutorialspoint.com/javamail_api/javamail_api_checking_emails.htm
https://gist.github.com/jmiguelr/880b92f0ac647eb70448

Compile:
javac -cp javax.mail-1.6.2.jar .\EmailEngine.java

Run: 
java -cp javax.mail-1.6.2.jar EmailEngine

// direct output
java -cp javax.mail-1.6.2.jar .\EmailEngine.java

// compile
javac -cp javax.mail-1.6.2.jar .\EmailEngine.java

Run
java EmailEngine
java -cp javax.mail-1.6.2.jar EmailEngine


PS D:\Projects\Github\java-code-camp\rnd\src\raw-email> javac -cp ".;javax.mail-1.6.2.jar;jakarta.activation-2.0.1.jar" EmailEngine.java
PS D:\Projects\Github\java-code-camp\rnd\src\raw-email> java -cp ".;javax.mail-1.6.2.jar;jakarta.activation-2.0.1.jar" EmailEngine  


--- Final and working
 javac -cp ".;javax.mail-1.6.2.jar;jakarta.activation-1.2.2.jar" EmailEngine.java
 java -cp ".;javax.mail-1.6.2.jar;jakarta.activation-1.2.2.jar" EmailEngine

