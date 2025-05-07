import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class TakoyakiOrderingSystem {

    private static final Scanner scanner = new Scanner(System.in);

    // Constants for menu items and prices
    private static final String[] TAKOYAKI_FLAVORS = {
            "Classic 4pcs", "Octopus 4pcs", "Green Onion 4pcs",
            "Cheese 4pcs", "Bacon 4pcs", "Crab 4pcs"
    };

    private static final double[] TAKOYAKI_PRICES = {
            55.00, 65.00, 55.00, 55.00, 65.00, 65.00
    };

    private static final String[] DRINK_NAMES = {
            "Coke Original 325ml", "Coke Zero 325ml", "Sprite 325ml",
            "Royal 330ml", "Pepsi 320ml", "Mountain Dew 320ml"
    };

    private static final double[] DRINK_PRICES = {
            34.10, 34.50, 34.10, 31.10, 26.95, 26.95
    };

    // Class to represent order items
    private static class OrderItem {
        String name;
        int quantity;
        double unitPrice;
        double totalPrice;

        OrderItem(String name, int quantity, double unitPrice) {
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = quantity * unitPrice;
        }

        @Override
        public String toString() {
            return String.format("%d x %s (Php. %.2f each) = Php. %.2f",
                    quantity, name, unitPrice, totalPrice);
        }
    }

    // List to store order items
    private List<OrderItem> orderItems = new ArrayList<>();
    private double totalCost = 0.0;

    public void displayMenu() {
        System.out.println("\t\t\t\t+=======================================+");
        System.out.println("\t\t\t\t            GELO'S TAKOYAKI MENU         ");
        System.out.println("\t\t\t\t Flavors:                                ");

        for (int i = 0; i < TAKOYAKI_FLAVORS.length; i++) {
            System.out.printf("\t\t\t\t   %d. %-25s Php. %.2f%n",
                    i + 1, TAKOYAKI_FLAVORS[i], TAKOYAKI_PRICES[i]);
        }

        System.out.println("\t\t\t\t   " + (TAKOYAKI_FLAVORS.length + 1) + ". CANCEL");
        System.out.println("\t\t\t\t+=======================================+");
    }

    public void displayDrinks() {
        System.out.println("\t\t\t\t+=======================================+");
        System.out.println("\t\t\t\t                  Drinks                 ");
        System.out.println("\t\t\t\t Available canned:                       ");

        for (int i = 0; i < DRINK_NAMES.length; i++) {
            System.out.printf("\t\t\t\t   %d. %-25s Php. %.2f%n",
                    i + 1, DRINK_NAMES[i], DRINK_PRICES[i]);
        }

        System.out.println("\t\t\t\t   " + (DRINK_NAMES.length + 1) + ". CANCEL");
        System.out.println("\t\t\t\t+=======================================+");
    }

    public void orderTakoyaki(int flavorIndex) {
        String flavor = TAKOYAKI_FLAVORS[flavorIndex];
        double price = TAKOYAKI_PRICES[flavorIndex];

        System.out.println("You selected " + flavor + " Takoyaki.");
        System.out.println();
        System.out.print("Order Quantity: ");
        int orderQuantity = getIntInput(1, 100);

        // Create new order item and add to the list
        OrderItem item = new OrderItem(flavor, orderQuantity, price);
        orderItems.add(item);
        totalCost += item.totalPrice;

        System.out.printf("Total cost for %s: Php. %.2f Added to Order%n", flavor, item.totalPrice);
        System.out.printf("Overall order cost: Php. %.2f%n", totalCost);
        System.out.println();

        handleAdditionalItems();
    }

    private void handleAdditionalItems() {
        // Input validation for "Yes" or "No" choices
        String addItems = getYesNoInput("Would you like to add additional items? (Y/N): ");

        if (addItems.equals("Y")) {
            do {
                System.out.println();
                System.out.println("What item would you like to add?");
                System.out.println("1. Drink");
                System.out.println("2. Add New Takoyaki Order");
                System.out.println("3. View Current Order");
                System.out.println();
                System.out.print("Your choice: ");
                int choice = getIntInput(1, 3);

                switch (choice) {
                    case 1:
                        orderDrink();
                        break;
                    case 2:
                        displayMenu();
                        System.out.print("Your choice for the new takoyaki order (1-" +
                                TAKOYAKI_FLAVORS.length + ") or " +
                                (TAKOYAKI_FLAVORS.length + 1) + " to cancel: ");
                        int newOrderChoice = getIntInput(1, TAKOYAKI_FLAVORS.length + 1);

                        if (newOrderChoice <= TAKOYAKI_FLAVORS.length) {
                            addTakoyakiToOrder(newOrderChoice - 1);
                        } else {
                            System.out.println("Order canceled.");
                        }
                        break;
                    case 3:
                        displayCurrentOrder();
                        break;
                }

                // After adding items, ask if the user wants to add more
                System.out.println();
                System.out.printf("Overall order cost: Php. %.2f%n", totalCost);
                System.out.println();

                addItems = getYesNoInput("Would you like to add more items? (Y/N): ");

            } while (addItems.equals("Y"));
        }

        // Payment handling
        processPayment();
    }

    private void orderDrink() {
        displayDrinks();
        System.out.print("Select a drink (1-" + DRINK_NAMES.length + ") or " +
                (DRINK_NAMES.length + 1) + " to cancel: ");
        int drinkChoice = getIntInput(1, DRINK_NAMES.length + 1);

        if (drinkChoice <= DRINK_NAMES.length) {
            int drinkIndex = drinkChoice - 1;
            System.out.print("Drink Quantity: ");
            int drinkQuantity = getIntInput(1, 100);

            if (drinkQuantity > 0) {
                OrderItem drinkItem = new OrderItem(
                        DRINK_NAMES[drinkIndex],
                        drinkQuantity,
                        DRINK_PRICES[drinkIndex]);

                orderItems.add(drinkItem);
                totalCost += drinkItem.totalPrice;

                System.out.println();
                System.out.printf("You've added %d %s to your order for Php. %.2f%n",
                        drinkQuantity, DRINK_NAMES[drinkIndex], drinkItem.totalPrice);
            } else {
                System.out.println();
                System.out.println("Drink order canceled.");
            }
        } else {
            System.out.println("Drink order canceled.");
        }
    }

    // Added method to avoid recursion issues
    private void addTakoyakiToOrder(int flavorIndex) {
        String flavor = TAKOYAKI_FLAVORS[flavorIndex];
        double price = TAKOYAKI_PRICES[flavorIndex];

        System.out.println("You selected " + flavor + " Takoyaki.");
        System.out.println();
        System.out.print("Order Quantity: ");
        int orderQuantity = getIntInput(1, 100);

        OrderItem item = new OrderItem(flavor, orderQuantity, price);
        orderItems.add(item);
        totalCost += item.totalPrice;

        System.out.printf("Total cost for %s: Php. %.2f Added to Order%n",
                flavor, item.totalPrice);
    }

    private void displayCurrentOrder() {
        if (orderItems.isEmpty()) {
            System.out.println("Your order is empty.");
            return;
        }

        System.out.println("\n+=======================================+");
        System.out.println("           CURRENT ORDER SUMMARY          ");
        System.out.println("+=======================================+");

        for (int i = 0; i < orderItems.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, orderItems.get(i));
        }

        System.out.println("+=======================================+");
        System.out.printf("TOTAL COST: Php. %.2f%n", totalCost);
        System.out.println("+=======================================+");
    }

    private void processPayment() {
        if (orderItems.isEmpty()) {
            System.out.println("Your order is empty. Nothing to pay for.");
            return;
        }

        // Display the final order
        displayCurrentOrder();

        // Process payment
        System.out.println("\n--- PAYMENT ---");
        System.out.printf("Total Amount Due: Php. %.2f%n", totalCost);

        double payment = getDoubleInput("Enter payment amount: ", totalCost, 10000.0);
        double change = payment - totalCost;

        System.out.printf("Payment received: Php. %.2f%n", payment);
        System.out.printf("Change: Php. %.2f%n", change);

        // Print receipt
        printReceipt(payment, change);
    }

    private void printReceipt(double payment, double change) {
        System.out.println("\n\n+=======================================+");
        System.out.println("              GELO'S TAKOYAKI             ");
        System.out.println("                 RECEIPT                  ");
        System.out.println("+=======================================+");
        System.out.println("Date: May 7, 2025");
        System.out.println("+=======================================+");

        for (OrderItem item : orderItems) {
            System.out.printf("%s%n", item);
        }

        System.out.println("+=======================================+");
        System.out.printf("SUBTOTAL:           Php. %.2f%n", totalCost);
        System.out.println("+=======================================+");
        System.out.printf("TOTAL:              Php. %.2f%n", totalCost);
        System.out.printf("PAYMENT:            Php. %.2f%n", payment);
        System.out.printf("CHANGE:             Php. %.2f%n", change);
        System.out.println("+=======================================+");
        System.out.println("          Thank you for your order!      ");
        System.out.println("           Please come again soon!       ");
        System.out.println("+=======================================+");
    }

    // Helper method to get integer input with validation
    private int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // consume the newline
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max);
                    System.out.print("Try again: ");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // consume the invalid input
                System.out.print("Try again: ");
            }
        }
    }

    // Helper method for double input with validation
    private double getDoubleInput(String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                double input = scanner.nextDouble();
                scanner.nextLine(); // consume the newline

                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.printf("Please enter an amount between %.2f and %.2f%n", min, max);
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid amount.");
                scanner.nextLine(); // consume the invalid input
            }
        }
    }

    // Helper method to get Y/N input with validation
    private String getYesNoInput(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().toUpperCase();
            if (!input.equals("Y") && !input.equals("N")) {
                System.out.println("Invalid choice. Please enter 'Y' for Yes or 'N' for No.");
            }
        } while (!input.equals("Y") && !input.equals("N"));
        return input;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public static void main(String[] args) {
        System.out.println("\t\t\t\t+=======================================+");
        System.out.println("\t\t\t\t                              ");
        System.out.println("\t\t\t\t             GELO'S TAKOYAKI                ");
        System.out.println("\t\t\t\t                         ");
        System.out.println("\t\t\t\t            Press Enter to Order              ");
        System.out.println("\t\t\t\t          press Q to Quit Program           ");
        System.out.println("\t\t\t\t+=======================================+");

        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("q")) {
            System.out.println("Program terminated.");
        } else {
            TakoyakiOrderingSystem orderingSystem = new TakoyakiOrderingSystem();
            orderingSystem.displayMenu();

            System.out.print("Your choice (1-" + TAKOYAKI_FLAVORS.length + ") or " +
                    (TAKOYAKI_FLAVORS.length + 1) + " to cancel: ");
            int choice = orderingSystem.getIntInput(1, TAKOYAKI_FLAVORS.length + 1);

            if (choice <= TAKOYAKI_FLAVORS.length) {
                orderingSystem.orderTakoyaki(choice - 1);
            } else {
                System.out.println("Order canceled.");
            }
        }

        // Close the scanner only once at the end of the program
        scanner.close();
    }
}