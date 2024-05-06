import java.util.Scanner;

public class exerciase {
    public static void main(String[] args) {
        double tempC;
        Scanner scan = new Scanner(System.in);
        /*
         * Write a program that converts a temperature from Celsius to Fahrenheit.
         * It should (1) prompt the user for input,
         * (2) read a double value from the keyboard, (3) calculate the result, and (4)
         * format the output to one decimal place. When it’s finished, it should work
         * like this:
         * Enter a temperature in Celsius: 24
         * 24.0 C = 75.2 F
         * Here is the formula to do the conversion:
         * 
         * F = C ×
         * 9
         * 5
         * + 32
         * Hint: Be careful not to use integer division!
         */

        System.out.println("Enter a temperature in celcius (input a decimal): ");
        tempC = scan.nextDouble();
        double tempF;

        tempF = tempC * (9 / 5) + 32;
        System.out.printf("Your input temperature in Celcius: %f C, Your temperature in Fahrenheit: %f F.", tempC,
                tempF);

    }
}