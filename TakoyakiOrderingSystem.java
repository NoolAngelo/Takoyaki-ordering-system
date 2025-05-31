import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

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

    // Combo meal options
    private static final String[] COMBO_NAMES = {
            "Classic Combo (4pcs + Drink)", "Premium Combo (4pcs + Drink)", "Family Pack (8pcs + 2 Drinks)"
    };

    private static final double[] COMBO_PRICES = {
            85.00, 95.00, 165.00
    };

    // Simple inventory management
    private static final int[] TAKOYAKI_STOCK = { 50, 50, 50, 50, 50, 50 };
    private static final int[] DRINK_STOCK = { 30, 30, 30, 30, 30, 30 };
    private static final int[] COMBO_STOCK = { 20, 20, 15 };

    // Magic numbers as constants
    private static final int MAX_QUANTITY = 100;
    private static final double MAX_PAYMENT = 10000.0;

    // Customer information
    private static class Customer {
        String name;
        String phoneNumber;
        boolean isFirstTime;

        Customer(String name, String phoneNumber, boolean isFirstTime) {
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.isFirstTime = isFirstTime;
        }
    }

    private Customer currentCustomer = null;

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
    private double discountAmount = 0.0;
    private String discountCode = "";

    // Discount codes and their values
    private static final String[] VALID_DISCOUNT_CODES = { "STUDENT10", "SENIOR15", "FIRST20" };
    private static final double[] DISCOUNT_PERCENTAGES = { 0.10, 0.15, 0.20 };

    public void displayMenu() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("                                    GELO'S TAKOYAKI - PROFESSIONAL POS SYSTEM");
        System.out.println("=".repeat(100));

        // Display Takoyaki Section
        System.out.println("                                              *** TAKOYAKI MENU ***");
        System.out.println("-".repeat(100));
        for (int i = 0; i < TAKOYAKI_FLAVORS.length; i++) {
            String stockStatus = TAKOYAKI_STOCK[i] <= 0 ? " [OUT OF STOCK]"
                    : TAKOYAKI_STOCK[i] <= 5 ? " [LOW STOCK: " + TAKOYAKI_STOCK[i] + "]" : "";
            System.out.printf("  %2d. %-35s Php %8.2f    Stock: %3d%s%n",
                    i + 1, TAKOYAKI_FLAVORS[i], TAKOYAKI_PRICES[i], TAKOYAKI_STOCK[i], stockStatus);
        }

        // Display Combo Meals Section
        System.out.println("\n                                              *** COMBO MEALS ***");
        System.out.println("-".repeat(100));
        int comboStartIndex = TAKOYAKI_FLAVORS.length;
        for (int i = 0; i < COMBO_NAMES.length; i++) {
            String stockStatus = COMBO_STOCK[i] <= 0 ? " [OUT OF STOCK]"
                    : COMBO_STOCK[i] <= 5 ? " [LOW STOCK: " + COMBO_STOCK[i] + "]" : "";
            System.out.printf("  %2d. %-35s Php %8.2f    Stock: %3d%s%n",
                    comboStartIndex + i + 1, COMBO_NAMES[i], COMBO_PRICES[i], COMBO_STOCK[i], stockStatus);
        }

        // Display Drinks Section
        System.out.println("\n                                               *** BEVERAGES ***");
        System.out.println("-".repeat(100));
        int drinkStartIndex = TAKOYAKI_FLAVORS.length + COMBO_NAMES.length;
        for (int i = 0; i < DRINK_NAMES.length; i++) {
            String stockStatus = DRINK_STOCK[i] <= 0 ? " [OUT OF STOCK]"
                    : DRINK_STOCK[i] <= 5 ? " [LOW STOCK: " + DRINK_STOCK[i] + "]" : "";
            System.out.printf("  %2d. %-35s Php %8.2f    Stock: %3d%s%n",
                    drinkStartIndex + i + 1, DRINK_NAMES[i], DRINK_PRICES[i], DRINK_STOCK[i], stockStatus);
        }

        System.out.println("\n" + "-".repeat(100));
        System.out.println("                                         *** SPECIAL OPTIONS ***");
        System.out.println("-".repeat(100));
        int totalItems = TAKOYAKI_FLAVORS.length + COMBO_NAMES.length + DRINK_NAMES.length;
        System.out.printf("  %2d. View Order Summary & Checkout%n", totalItems + 1);
        System.out.printf("  %2d. Modify Existing Order%n", totalItems + 2);
        System.out.printf("  %2d. Apply Discount Code%n", totalItems + 3);
        System.out.printf("  %2d. View Stock Levels%n", totalItems + 4);
        System.out.printf("  %2d. CANCEL ORDER%n", totalItems + 5);
        System.out.println("=".repeat(100));

        // Show current order summary if items exist
        if (!orderItems.isEmpty()) {
            System.out.println("\n                                           *** CURRENT ORDER ***");
            System.out.println("-".repeat(100));
            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem item = orderItems.get(i);
                System.out.printf("  %d. %dx %-30s @ Php %.2f each = Php %.2f%n",
                        i + 1, item.quantity, item.name, item.unitPrice, item.totalPrice);
            }
            System.out.printf("%nCurrent Total: Php %.2f", totalCost);
            if (discountAmount > 0) {
                System.out.printf(" (Discount: -Php %.2f)", discountAmount);
            }
            System.out.println("\n" + "-".repeat(100));
        }
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

    public void displayComboMeals() {
        System.out.println("\t\t\t\t+=======================================+");
        System.out.println("\t\t\t\t               COMBO MEALS               ");
        System.out.println("\t\t\t\t Great Value Deals:                      ");

        for (int i = 0; i < COMBO_NAMES.length; i++) {
            System.out.printf("\t\t\t\t   %d. %-25s Php. %.2f%n",
                    i + 1, COMBO_NAMES[i], COMBO_PRICES[i]);
        }

        System.out.println("\t\t\t\t   " + (COMBO_NAMES.length + 1) + ". CANCEL");
        System.out.println("\t\t\t\t+=======================================+");
    }

    // Generalized method for ordering items (Takoyaki or Drink)
    private void orderItem(String[] names, double[] prices, String itemType) {
        System.out.printf("Select a %s (1-%d) or %d to cancel: ", itemType, names.length, names.length + 1);
        int choice = getIntInput(1, names.length + 1);
        if (choice <= names.length) {
            int index = choice - 1;

            // Check stock availability
            int[] stockArray = getStockArray(itemType);
            if (stockArray[index] <= 0) {
                System.out.println("Sorry! " + names[index] + " is currently out of stock.");
                return;
            }

            System.out.printf("%s Quantity (Available: %d): ", itemType, stockArray[index]);
            int maxAvailable = Math.min(MAX_QUANTITY, stockArray[index]);
            int quantity = getIntInput(1, maxAvailable);

            if (quantity > 0) {
                OrderItem item = new OrderItem(names[index], quantity, prices[index]);
                orderItems.add(item);
                totalCost += item.totalPrice;

                // Update stock
                stockArray[index] -= quantity;

                System.out.printf("You've added %d %s to your order for Php. %.2f\n", quantity, names[index],
                        item.totalPrice);

                // Low stock warning
                if (stockArray[index] <= 5 && stockArray[index] > 0) {
                    System.out.println("⚠️ Only " + stockArray[index] + " " + names[index] + " remaining!");
                }
            } else {
                System.out.println(itemType + " order canceled.");
            }
        } else {
            System.out.println(itemType + " order canceled.");
        }
    }

    private int[] getStockArray(String itemType) {
        switch (itemType.toLowerCase()) {
            case "takoyaki":
                return TAKOYAKI_STOCK;
            case "drink":
                return DRINK_STOCK;
            case "combo meal":
            case "combo":
                return COMBO_STOCK;
            default:
                return new int[0];
        }
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
        System.out.printf("SUBTOTAL: Php. %.2f%n", totalCost);

        if (discountAmount > 0) {
            System.out.printf("DISCOUNT (%s): -Php. %.2f%n", discountCode, discountAmount);
            System.out.printf("TOTAL COST: Php. %.2f%n", totalCost - discountAmount);
        } else {
            System.out.printf("TOTAL COST: Php. %.2f%n", totalCost);
        }
        System.out.println("+=======================================+");
    }

    private void processPayment() {
        if (orderItems.isEmpty()) {
            System.out.println("Your order is empty. Nothing to pay for.");
            return;
        }

        // Collect customer information before payment
        collectCustomerInfo();

        displayCurrentOrder();
        System.out.println("\n--- PAYMENT ---");

        double finalTotal = totalCost - discountAmount;
        System.out.printf("Total Amount Due: Php. %.2f\n", finalTotal);
        double payment = getDoubleInput("Enter payment amount: ", finalTotal, MAX_PAYMENT);
        double change = payment - finalTotal;
        System.out.printf("Payment received: Php. %.2f\n", payment);
        System.out.printf("Change: Php. %.2f\n", change);
        printReceipt(payment, change);
        // Ask if user wants to order again
        String again = getYesNoInput("Would you like to place another order? (Y/N): ");
        if (again.equals("Y")) {
            orderItems.clear();
            totalCost = 0.0;
            discountAmount = 0.0;
            discountCode = "";
            currentCustomer = null;
            startOrderingProcess(); // Restart the ordering process
        } else {
            System.out.println("Thank you for using GELO'S TAKOYAKI Ordering System!");
        }
    }

    private void printReceipt(double payment, double change) {
        System.out.println("\n\n+=======================================+");
        System.out.println("              GELO'S TAKOYAKI             ");
        System.out.println("                 RECEIPT                  ");
        System.out.println("+=======================================+");
        // Use current date and time
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        String time = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("Date: " + date + " | Time: " + time);
        System.out.println("Order #: " + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        if (currentCustomer != null) {
            System.out.println("Customer: " + currentCustomer.name);
            System.out.println("Phone: " + currentCustomer.phoneNumber);
        }

        System.out.println("+=======================================+");

        for (OrderItem item : orderItems) {
            System.out.printf("%s\n", item);
        }

        System.out.println("+=======================================+");
        System.out.printf("SUBTOTAL:           Php. %.2f\n", totalCost);

        if (discountAmount > 0) {
            System.out.printf("DISCOUNT (%s):      -Php. %.2f\n", discountCode, discountAmount);
        }

        double finalTotal = totalCost - discountAmount;
        System.out.println("+=======================================+");
        System.out.printf("TOTAL:              Php. %.2f\n", finalTotal);
        System.out.printf("PAYMENT:            Php. %.2f\n", payment);
        System.out.printf("CHANGE:             Php. %.2f\n", change);
        System.out.println("+=======================================+");
        System.out.println("          Thank you for your order!      ");
        System.out.println("           Please come again soon!       ");

        if (discountAmount > 0) {
            System.out.printf("       You saved Php. %.2f today!        \n", discountAmount);
        }

        if (currentCustomer != null && currentCustomer.isFirstTime) {
            System.out.println("    Welcome to the Gelo's Takoyaki family!  ");
        }

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

    // Method to display order statistics
    public void displayOrderStatistics() {
        if (orderItems.isEmpty()) {
            System.out.println("No items ordered yet.");
            return;
        }

        System.out.println("\n+=======================================+");
        System.out.println("           ORDER STATISTICS               ");
        System.out.println("+=======================================+");

        int totalItems = orderItems.stream().mapToInt(item -> item.quantity).sum();
        double averageItemPrice = totalCost / totalItems;

        System.out.printf("Total Items Ordered: %d\n", totalItems);
        System.out.printf("Average Price per Item: Php. %.2f\n", averageItemPrice);
        System.out.printf("Most Expensive Item: %s\n", getMostExpensiveItem());
        System.out.printf("Total Savings: Php. %.2f\n", discountAmount);

        System.out.println("+=======================================+");
    }

    private String getMostExpensiveItem() {
        return orderItems.stream()
                .max((item1, item2) -> Double.compare(item1.unitPrice, item2.unitPrice))
                .map(item -> item.name)
                .orElse("None");
    }

    private void modifyOrder() {
        if (orderItems.isEmpty()) {
            System.out.println("Your order is empty. Nothing to modify.");
            return;
        }

        displayCurrentOrder();
        System.out.println("\nOrder Modification Options:");
        System.out.println("1. Remove an item");
        System.out.println("2. Change item quantity");
        System.out.println("3. Cancel modification");

        int choice = getIntInput(1, 3);

        switch (choice) {
            case 1:
                removeItem();
                break;
            case 2:
                changeQuantity();
                break;
            case 3:
                System.out.println("Modification canceled.");
                break;
        }
    }

    private void removeItem() {
        System.out.print("Enter item number to remove (1-" + orderItems.size() + "): ");
        int itemIndex = getIntInput(1, orderItems.size()) - 1;

        OrderItem removedItem = orderItems.get(itemIndex);
        orderItems.remove(itemIndex);
        totalCost -= removedItem.totalPrice;

        System.out.println("Removed: " + removedItem.name + " from your order.");
        recalculateTotal();
    }

    private void changeQuantity() {
        System.out.print("Enter item number to modify quantity (1-" + orderItems.size() + "): ");
        int itemIndex = getIntInput(1, orderItems.size()) - 1;

        OrderItem item = orderItems.get(itemIndex);
        System.out.println("Current quantity for " + item.name + ": " + item.quantity);
        System.out.print("Enter new quantity (0 to remove item): ");
        int newQuantity = getIntInput(0, MAX_QUANTITY);

        if (newQuantity == 0) {
            orderItems.remove(itemIndex);
            totalCost -= item.totalPrice;
            System.out.println("Item removed from order.");
        } else {
            totalCost -= item.totalPrice; // Remove old total
            item.quantity = newQuantity;
            item.totalPrice = newQuantity * item.unitPrice;
            totalCost += item.totalPrice; // Add new total
            System.out.println("Quantity updated to " + newQuantity);
        }
        recalculateTotal();
    }

    private void applyDiscount() {
        if (discountAmount > 0) {
            System.out.println("Discount already applied: " + discountCode);
            return;
        }

        System.out.println("\nAvailable Discount Codes:");
        System.out.println("STUDENT10 - 10% off for students");
        System.out.println("SENIOR15 - 15% off for senior citizens");
        System.out.println("FIRST20 - 20% off for first-time customers");

        System.out.print("Enter discount code: ");
        String code = scanner.nextLine().toUpperCase();

        for (int i = 0; i < VALID_DISCOUNT_CODES.length; i++) {
            if (VALID_DISCOUNT_CODES[i].equals(code)) {
                discountCode = code;
                discountAmount = totalCost * DISCOUNT_PERCENTAGES[i];
                System.out.printf("Discount applied! You saved Php. %.2f\n", discountAmount);
                return;
            }
        }

        System.out.println("Invalid discount code. Please try again.");
    }

    private void collectCustomerInfo() {
        System.out.println("\n--- CUSTOMER INFORMATION ---");
        String wantToProvide = getYesNoInput("Would you like to provide your information for better service? (Y/N): ");

        if (wantToProvide.equals("Y")) {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter your phone number: ");
            String phone = scanner.nextLine().trim();

            String isFirstTime = getYesNoInput("Is this your first time ordering from Gelo's Takoyaki? (Y/N): ");

            currentCustomer = new Customer(name, phone, isFirstTime.equals("Y"));

            System.out.println("Thank you, " + name + "! Your information has been saved.");

            // Suggest first-time discount
            if (currentCustomer.isFirstTime && discountAmount == 0) {
                System.out.println("🎉 Great news! As a first-time customer, you can use code 'FIRST20' for 20% off!");
                String useDiscount = getYesNoInput("Would you like to apply this discount now? (Y/N): ");
                if (useDiscount.equals("Y")) {
                    discountCode = "FIRST20";
                    discountAmount = totalCost * 0.20;
                    System.out.printf("Discount applied! You saved Php. %.2f\n", discountAmount);
                }
            }
        }
    }

    private void recalculateTotal() {
        totalCost = 0.0;
        for (OrderItem item : orderItems) {
            totalCost += item.totalPrice;
        }
        // Reapply discount if it was previously applied
        if (discountAmount > 0 && !discountCode.isEmpty()) {
            for (int i = 0; i < VALID_DISCOUNT_CODES.length; i++) {
                if (VALID_DISCOUNT_CODES[i].equals(discountCode)) {
                    discountAmount = totalCost * DISCOUNT_PERCENTAGES[i];
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(100));
        System.out.println("                                    WELCOME TO GELO'S TAKOYAKI");
        System.out.println("                                   Professional POS System v2.0");
        System.out.println("=".repeat(100));
        System.out.println("                              Press Enter to start ordering or Q to quit");
        System.out.println("=".repeat(100));

        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("q")) {
            System.out.println("Thank you for visiting Gelo's Takoyaki! Have a great day!");
        } else {
            TakoyakiOrderingSystem orderingSystem = new TakoyakiOrderingSystem();
            orderingSystem.startOrderingProcess();
        }

        scanner.close();
    }

    private void startOrderingProcess() {
        collectCustomerInfo();

        boolean ordering = true;
        while (ordering) {
            displayMenu();

            int totalItems = TAKOYAKI_FLAVORS.length + COMBO_NAMES.length + DRINK_NAMES.length;
            int maxChoice = totalItems + 5;

            System.out.printf("\nEnter your choice (1-%d): ", maxChoice);
            int choice = getIntInput(1, maxChoice);

            if (choice <= TAKOYAKI_FLAVORS.length) {
                // Takoyaki selection
                orderSingleItem(TAKOYAKI_FLAVORS, TAKOYAKI_PRICES, TAKOYAKI_STOCK, "Takoyaki", choice - 1);
            } else if (choice <= TAKOYAKI_FLAVORS.length + COMBO_NAMES.length) {
                // Combo meal selection
                int comboIndex = choice - TAKOYAKI_FLAVORS.length - 1;
                orderSingleItem(COMBO_NAMES, COMBO_PRICES, COMBO_STOCK, "Combo", comboIndex);
            } else if (choice <= totalItems) {
                // Drink selection
                int drinkIndex = choice - TAKOYAKI_FLAVORS.length - COMBO_NAMES.length - 1;
                orderSingleItem(DRINK_NAMES, DRINK_PRICES, DRINK_STOCK, "Drink", drinkIndex);
            } else {
                // Special options
                int specialOption = choice - totalItems;
                switch (specialOption) {
                    case 1:
                        viewOrderSummaryAndCheckout();
                        ordering = false;
                        break;
                    case 2:
                        modifyOrder();
                        break;
                    case 3:
                        applyDiscount();
                        break;
                    case 4:
                        displayStockLevels();
                        break;
                    case 5:
                        System.out.println("Order canceled. Thank you for visiting!");
                        ordering = false;
                        break;
                }
            }
        }
    }

    private void orderSingleItem(String[] names, double[] prices, int[] stock, String category, int index) {
        if (stock[index] <= 0) {
            System.out.println("Sorry, " + names[index] + " is currently out of stock.");
            return;
        }

        System.out.printf("\nSelected: %s - Php %.2f\n", names[index], prices[index]);
        System.out.printf("Available stock: %d\n", stock[index]);

        int maxQuantity = Math.min(stock[index], MAX_QUANTITY);
        System.out.printf("How many would you like? (1-%d): ", maxQuantity);
        int quantity = getIntInput(1, maxQuantity);

        // Add to order
        OrderItem newItem = new OrderItem(names[index], quantity, prices[index]);

        // Check if item already exists in order
        boolean found = false;
        for (OrderItem item : orderItems) {
            if (item.name.equals(names[index])) {
                item.quantity += quantity;
                found = true;
                break;
            }
        }

        if (!found) {
            orderItems.add(newItem);
        }

        // Update stock and total cost
        stock[index] -= quantity;
        totalCost += prices[index] * quantity;

        System.out.printf("Added %d x %s to your order.\n", quantity, names[index]);
        System.out.printf("Current order total: Php %.2f\n", totalCost);

        if (stock[index] <= 5 && stock[index] > 0) {
            System.out.printf("Warning: Only %d %s remaining in stock!\n", stock[index], names[index]);
        }
    }

    private void viewOrderSummaryAndCheckout() {
        if (orderItems.isEmpty()) {
            System.out.println("Your order is empty. Please add items before checkout.");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    ORDER SUMMARY");
        System.out.println("=".repeat(60));

        double subtotal = 0;
        for (OrderItem item : orderItems) {
            double itemTotal = item.totalPrice;
            subtotal += itemTotal;
            System.out.printf("%dx %-30s Php %8.2f\n", item.quantity, item.name, itemTotal);
        }

        System.out.println("-".repeat(60));
        System.out.printf("Subtotal:%41s Php %8.2f\n", "", subtotal);

        if (discountAmount > 0) {
            System.out.printf("Discount (%s):%30s -Php %8.2f\n", discountCode, "", discountAmount);
        }

        System.out.printf("TOTAL:%44s Php %8.2f\n", "", totalCost);
        System.out.println("=".repeat(60));

        String confirm = getYesNoInput("Proceed to payment? (Y/N): ");
        if (confirm.equals("Y")) {
            processPayment();
        }
    }

    private void displayStockLevels() {
        System.out.println("\n+=======================================+");
        System.out.println("           CURRENT STOCK LEVELS          ");
        System.out.println("+=======================================+");

        System.out.println("TAKOYAKI:");
        for (int i = 0; i < TAKOYAKI_FLAVORS.length; i++) {
            String status = TAKOYAKI_STOCK[i] <= 0 ? "OUT OF STOCK" : TAKOYAKI_STOCK[i] <= 5 ? "LOW STOCK" : "IN STOCK";
            System.out.printf("  %-25s: %d (%s)\n", TAKOYAKI_FLAVORS[i], TAKOYAKI_STOCK[i], status);
        }

        System.out.println("\nDRINKS:");
        for (int i = 0; i < DRINK_NAMES.length; i++) {
            String status = DRINK_STOCK[i] <= 0 ? "OUT OF STOCK" : DRINK_STOCK[i] <= 5 ? "LOW STOCK" : "IN STOCK";
            System.out.printf("  %-25s: %d (%s)\n", DRINK_NAMES[i], DRINK_STOCK[i], status);
        }

        System.out.println("\nCOMBO MEALS:");
        for (int i = 0; i < COMBO_NAMES.length; i++) {
            String status = COMBO_STOCK[i] <= 0 ? "OUT OF STOCK" : COMBO_STOCK[i] <= 5 ? "LOW STOCK" : "IN STOCK";
            System.out.printf("  %-25s: %d (%s)\n", COMBO_NAMES[i], COMBO_STOCK[i], status);
        }

        System.out.println("+=======================================+");
    }
}