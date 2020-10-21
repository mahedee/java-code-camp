import java.util.Scanner;

public class Fibonacci {
    public static void main(String[] args) {

         /* This reads the input provided by user
         * using keyboard
         */
        Scanner scan = new Scanner(System.in);

        // This method reads the number provided using keyboard
        System.out.println("Please enter a number. ");
        int n = scan.nextInt();

        // Closing Scanner after the use
        scan.close();

        int fib1 = 0;
        int fib2 = 1;
        int sum = 0;

        for(int i = 1; i <= n; i++)
        {

            System.out.print(fib1 + "  ");
            
            sum = fib1 + fib2;
            
            fib1 = fib2;
            fib2 = sum;
        }

    }
}
