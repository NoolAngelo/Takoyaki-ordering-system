import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class TakoyakiOrderingSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private double totalCost = 0.0;
    private List<OrderItem> orderItems = new ArrayList<>();

    private static class OrderItem {
        String name;
        int quantity;
        double price;

        OrderItem(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }
    }

    private static class MenuItem {
        String name;
        double price;

        MenuItem(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }

    private static final MenuItem[] TAKOYAKI_MENU = {
            new MenuItem("Classic", 55.00),
            new MenuItem("Octopus", 65.00),
            new MenuItem("Green Onion", 55.00),
            new MenuItem("Cheese", 55.00),
            new MenuItem("Bacon", 65.00),
            new MenuItem("Crab", 65.00)
    };

    private static final MenuItem[] DRINKS_MENU = {
            new MenuItem("Coke Original 325ml", 34.10),
            new MenuItem("Coke Zero 325ml", 34.50),
            new MenuItem("Sprite 325ml", 34.10),
            new MenuItem("Royal 330ml", 31.10),
            new MenuItem("Pepsi 320ml", 26.95),
            new MenuItem("Mountain Dew 320ml", 26.95)
    };

    public void displayMenu(MenuItem[] menu, String title) {
        System.out.println("\n\t\t\t\t+=======================================+");
        System.out.println("\t\t\t\t            " + title);
        System.out.println("\t\t\t\t+=======================================+");
        for (int i = 0; i < menu.length; i++) {
            System.out.printf("\t\t\t\t %d. %-25s Php. %.2f%n", i + 1, menu[i].name, menu[i].price);
        }
        System.out.println("\t\t\t\t " + (menu.length + 1) + ". CANCEL");
        System.out.println("\t\t\t\t+=======================================+");
    }

    public void orderItem(MenuItem[] menu, String type) {
        displayMenu(menu, type + " MENU");
        int choice = getIntInput(
                "Select a " + type.toLowerCase() + " (1-" + menu.length + ") or " + (menu.length + 1) + " to cancel: ",
                1, menu.length + 1);

        if (choice == menu.length + 1) {
            System.out.println(type + " order canceled.");
            return;
        }

        MenuItem selectedItem = menu[choice - 1];
        int quantity = getIntInput("Enter quantity: ", 1, 100);

        double itemCost = selectedItem.price * quantity;
        totalCost += itemCost;
        orderItems.add(new OrderItem(selectedItem.name, quantity, itemCost));

        System.out.printf("Added %d %s to your order. Cost: Php. %.2f%n", quantity, selectedItem.name, itemCost);
        System.out.printf("Current total: Php. %.2f%n", totalCost);
    }

    public void startOrdering() {
        while (true) {
            System.out.println("\nWhat would you like to order?");
            System.out.println("1. Takoyaki");
            System.out.println("2. Drinks");
            System.out.println("3. View Current Order");
            System.out.println("4. Finish and Pay");
            int choice = getIntInput("Your choice: ", 1, 4);

            switch (choice) {
                case 1:
                    orderItem(TAKOYAKI_MENU, "TAKOYAKI");
                    break;
                case 2:
                    orderItem(DRINKS_MENU, "DRINKS");
                    break;
                case 3:
                    viewCurrentOrder();
                    break;
                case 4:
                    finishAndPay();
                    return;
            }
        }
    }

    private void viewCurrentOrder() {
        if (orderItems.isEmpty()) {
            System.out.println("Your order is empty.");
            return;
        }
        System.out.println("\nCurrent Order:");
        for (OrderItem item : orderItems) {
            System.out.printf("%d x %s - Php. %.2f%n", item.quantity, item.name, item.price);
        }
        System.out.printf("Total: Php. %.2f%n", totalCost);
    }

    private void finishAndPay() {
        if (orderItems.isEmpty()) {
            System.out.println("Your order is empty. Nothing to pay.");
            return;
        }
        viewCurrentOrder();
        double payment = getDoubleInput("Enter payment amount: ", totalCost, 10000);
        double change = payment - totalCost;
        System.out.printf("Payment received: Php. %.2f%n", payment);
        System.out.printf("Change: Php. %.2f%n", change);
        System.out.println("Thank you for your order!");
    }

    private int getIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = scanner.nextInt();
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
            }
        }
    }

    private double getDoubleInput(String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                double input = scanner.nextDouble();
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.printf("Please enter a number between %.2f and %.2f.%n", min, max);
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("\t\t\t\t+=======================================+");
        System.out.println("\t\t\t\t                                         ");
        System.out.println("\t\t\t\t             GELO'S TAKOYAKI             ");
        System.out.println("\t\t\t\t                                         ");
        System.out.println("\t\t\t\t            Press Enter to Order         ");
        System.out.println("\t\t\t\t          Press Q to Quit Program        ");
        System.out.println("\t\t\t\t+=======================================+");

        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("q")) {
            System.out.println("Program terminated.");
        } else {
            TakoyakiOrderingSystem orderingSystem = new TakoyakiOrderingSystem();
            orderingSystem.startOrdering();
        }

        scanner.close();
    }
}