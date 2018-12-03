package ui;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UtilityInputFromUser {
    public static boolean getBooleanChoiceFromUser(Scanner sc, String message) {
        String userInput;
        boolean userChoice = false, validAnswer = false;

        while(!validAnswer) {
            System.out.println(message);
            userInput = sc.nextLine();
            userInput = userInput.toUpperCase();
            if(userInput.equals("Y")) {
                validAnswer = true;
                userChoice = true;
            }
            else if(userInput.equals("N")) {
                validAnswer = true;
                userChoice = false;
            }
            else {
                System.out.println("System didn't get valid answer: 'Y' or 'N'");
            }
        }

        return userChoice;
    }

    public static boolean getBooleanChoiceFromUser(Scanner sc) {
        return getBooleanChoiceFromUser(sc, "Would you like to try again? Please press(Y/N)");
    }

    public static int getFromUserNumber(Scanner scanner, String message, int minNumber, int maxNumber) {
        boolean validInput = false;
        int userInput = -1;

        while(!validInput) {
            System.out.println(message);
            try {
                userInput = scanner.nextInt();
                if(userInput < minNumber || userInput > maxNumber) {
                    System.out.println("User entered number that isn't in the right range.");
                }
                else {
                    validInput = true;
                }
            }
            catch(InputMismatchException e) {
                System.out.println("User entered non integer input, Please try again to enter number in range.");
                scanner.nextLine();
            }
        }

        scanner.nextLine();
        return userInput;
    }
}
