import java.util.InputMismatchException;
import java.util.Scanner;

public class new1 {

    private static final String[] TAKOYAKI_MENU = {
            "Classic 4pcs", "Octopus 4pcs", "Green Onion 4pcs", "Cheese 4pcs", "Bacon 4pcs", "Crab 4pcs"
    };
    private static final double[] TAKOYAKI_PRICES = { 55.00, 65.00, 55.00, 55.00, 65.00, 65.00 };
    private static final String[] DRINK_MENU = {
            "Coke Original 325ml", "Coke Zero 325ml", "Sprite 325ml", "Royal 330ml", "Pepsi 320ml", "Mountain Dew 320ml"
    };
    private static final double[] DRINK_PRICES = { 34.10, 34.50, 34.10, 31.10, 26.95, 26.95 };

    private double totalCost = 0.0;

    public void displayMenu() {
        System.out.println("\t\t\t\t+=======================================+");
        System.out.println("\t\t\t\t            GELO'S TAKOYAKI MENU         ");
        for (int i = 0; i < TAKOYAKI_MENU.length; i++) {
            System.out.printf("\t\t\t\t   %d. %s             Php. %.2f%n", i + 1, TAKOYAKI_MENU[i], TAKOYAKI_PRICES[i]);
        }
        System.out.println("\t\t\t\t   7. CANCEL");
        System.out.println("\t\t\t\t+=======================================+");
    }

    public void displayDrinks() {
        System.out.println("\t\t\t\t+=======================================+");
        System.out.println("\t\t\t\t                  Drinks                 ");
        for (int i = 0; i < DRINK_MENU.length; i++) {
            System.out.printf("\t\t\t\t   %d. %s      Php. %.2f%n", i + 1, DRINK_MENU[i], DRINK_PRICES[i]);
        }
        System.out.println("\t\t\t\t   7. CANCEL");
        System.out.println("\t\t\t\t+=======================================+");
    }

    public void orderTakoyaki(int choice) {
        Scanner scan = new Scanner(System.in);
        String flavor = TAKOYAKI_MENU[choice - 1];
        double price = TAKOYAKI_PRICES[choice - 1];

        System.out.println("You selected " + flavor + " Takoyaki.");
        System.out.print("Order Quantity: ");
        int orderQuantity = scan.nextInt();

        double orderCost = price * orderQuantity;
        totalCost += orderCost;

        System.out.println("Total cost for " + flavor + " Takoyaki: Php. " + orderCost + " Added to Order");
        System.out.println("Overall selected items cost: " + totalCost);

        if (askYesNo("Would you like to add additional items? (Y/N): ")) {
            addAdditionalItems();
        }

        System.out.println("Total cost: Php. " + totalCost);
    }

    private void addAdditionalItems() {
        Scanner scan = new Scanner(System.in);
        boolean addMoreItems;
        do {
            System.out.println("What item would you like to add?");
            System.out.println("1. Drink");
            System.out.println("2. Add New Takoyaki Order");
            System.out.print("Your choice: ");
            int choice = scan.nextInt();

            switch (choice) {
                case 1:
                    displayDrinks();
                    System.out.print("Select a drink (1-6) or 7 to cancel: ");
                    int drinkChoice = scan.nextInt();
                    if (drinkChoice >= 1 && drinkChoice <= 6) {
                        System.out.print("Drink Quantity: ");
                        int drinkQuantity = scan.nextInt();
                        if (drinkQuantity > 0) {
                            totalCost += DRINK_PRICES[drinkChoice - 1] * drinkQuantity;
                            System.out.println("You've added " + drinkQuantity + " " + DRINK_MENU[drinkChoice - 1]
                                    + " to your order.");
                        } else {
                            System.out.println("Drink order canceled.");
                        }
                    } else {
                        System.out.println("Drink order canceled.");
                    }
                    break;
                case 2:
                    displayMenu();
                    System.out.print("Your choice for the new takoyaki order: ");
                    int newOrderChoice = scan.nextInt();
                    if (newOrderChoice >= 1 && newOrderChoice <= 6) {
                        orderTakoyaki(newOrderChoice);
                    } else {
                        System.out.println("Order canceled.");
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }

            addMoreItems = askYesNo("Would you like to add more items? (Y/N): ");
        } while (addMoreItems);
    }

    private boolean askYesNo(String prompt) {
        Scanner scan = new Scanner(System.in);
        String input;
        do {
            System.out.print(prompt);
            input = scan.nextLine().trim().toUpperCase();
        } while (!input.equals("Y") && !input.equals("N"));
        return input.equals("Y");
    }

    public double getTotalCost() {
        return totalCost;
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("\t\t\t\t+=======================================+");
        System.out.println("\t\t\t\t             GELO'S TAKOYAKI             ");
        System.out.println("\t\t\t\t            Press Enter to Order         ");
        System.out.println("\t\t\t\t          press Q to Quit Program        ");
        System.out.println("\t\t\t\t+=======================================+");

        String input = scan.nextLine();

        if (input.equalsIgnoreCase("q")) {
            System.out.println("Program terminated.");
        } else {
            TakoyakiOrderingSystem orderingSystem = new TakoyakiOrderingSystem();
            orderingSystem.displayMenu();

            System.out.print("Your choice: ");
            int choice = scan.nextInt();

            if (choice >= 1 && choice <= 6) {
                orderingSystem.orderTakoyaki(choice);
            } else {
                System.out.println("Order canceled.");
            }
        }

        scan.close();
    }
}