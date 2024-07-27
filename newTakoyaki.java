import java.util.*;

public class TakoyakiOrderingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OrderManager orderManager = new OrderManager();
        UI ui = new UI(scanner, orderManager);
        ui.start();
        scanner.close();
    }
}

class UI {
    private final Scanner scanner;
    private final OrderManager orderManager;

    public UI(Scanner scanner, OrderManager orderManager) {
        this.scanner = scanner;
        this.orderManager = orderManager;
    }

    public void start() {
        while (true) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ", 1, 3);
            switch (choice) {
                case 1:
                    orderTakoyaki();
                    break;
                case 2:
                    orderDrink();
                    break;
                case 3:
                    checkout();
                    return;
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n===== GELO'S TAKOYAKI =====");
        System.out.println("1. Order Takoyaki");
        System.out.println("2. Order Drink");
        System.out.println("3. Checkout");
    }

    private void orderTakoyaki() {
        displayTakoyakiMenu();
        int choice = getIntInput("Select a flavor (1-6): ", 1, 6);
        TakoyakiFlavor flavor = TakoyakiFlavor.values()[choice - 1];
        int quantity = getIntInput("Enter quantity: ", 1, 100);
        orderManager.addTakoyaki(flavor, quantity);
        System.out.println("Added to order: " + quantity + " " + flavor.getName());
    }

    private void orderDrink() {
        displayDrinkMenu();
        int choice = getIntInput("Select a drink (1-6): ", 1, 6);
        Drink drink = Drink.values()[choice - 1];
        int quantity = getIntInput("Enter quantity: ", 1, 100);
        orderManager.addDrink(drink, quantity);
        System.out.println("Added to order: " + quantity + " " + drink.getName());
    }

    private void checkout() {
        System.out.println("\n===== Your Order =====");
        orderManager.displayOrder();
        System.out.printf("Total: Php %.2f\n", orderManager.getTotal());
        System.out.println("Thank you for your order!");
    }

    private void displayTakoyakiMenu() {
        System.out.println("\n===== TAKOYAKI MENU =====");
        for (TakoyakiFlavor flavor : TakoyakiFlavor.values()) {
            System.out.printf("%d. %-20s Php %.2f\n", flavor.ordinal() + 1, flavor.getName(), flavor.getPrice());
        }
    }

    private void displayDrinkMenu() {
        System.out.println("\n===== DRINKS MENU =====");
        for (Drink drink : Drink.values()) {
            System.out.printf("%d. %-20s Php %.2f\n", drink.ordinal() + 1, drink.getName(), drink.getPrice());
        }
    }

    private int getIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}

class OrderManager {
    private final List<OrderItem> orderItems = new ArrayList<>();

    public void addTakoyaki(TakoyakiFlavor flavor, int quantity) {
        orderItems.add(new OrderItem(flavor.getName(), flavor.getPrice(), quantity));
    }

    public void addDrink(Drink drink, int quantity) {
        orderItems.add(new OrderItem(drink.getName(), drink.getPrice(), quantity));
    }

    public void displayOrder() {
        for (OrderItem item : orderItems) {
            System.out.printf("%-20s x%d  Php %.2f\n", item.getName(), item.getQuantity(), item.getTotal());
        }
    }

    public double getTotal() {
        return orderItems.stream().mapToDouble(OrderItem::getTotal).sum();
    }
}

class OrderItem {
    private final String name;
    private final double price;
    private final int quantity;

    public OrderItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return price * quantity;
    }
}

enum TakoyakiFlavor {
    CLASSIC("Classic", 55.00),
    OCTOPUS("Octopus", 65.00),
    GREEN_ONION("Green Onion", 55.00),
    CHEESE("Cheese", 55.00),
    BACON("Bacon", 65.00),
    CRAB("Crab", 65.00);

    private final String name;
    private final double price;

    TakoyakiFlavor(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

enum Drink {
    COKE_ORIGINAL("Coke Original 325ml", 34.10),
    COKE_ZERO("Coke Zero 325ml", 34.50),
    SPRITE("Sprite 325ml", 34.10),
    ROYAL("Royal 330ml", 31.10),
    PEPSI("Pepsi 320ml", 26.95),
    MOUNTAIN_DEW("Mountain Dew 320ml", 26.95);

    private final String name;
    private final double price;

    Drink(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}