import BuilderPattern.Order;
import FactoryMethodPattern.MenuItem;
import FactoryMethodPattern.MenuItemFactory;
import ObserverPattern.KitchenStaff;
import SingletonPattern.OrderManager;
import StrategyPattern.CashPayment;
import StrategyPattern.CreditCardPayment;
import StrategyPattern.PaymentContext;
import StrategyPattern.PaymentStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RestaurantApp {
    private static List<MenuItem> menu=new ArrayList<>();

    public static void main(String[] args){
        initializeMenu();
        OrderManager orderManager=OrderManager.getInstance();

        KitchenStaff kitchen1=new KitchenStaff("Alice");
        KitchenStaff kitchen2=new KitchenStaff("Bob");
        orderManager.registerObserver(kitchen1);
        orderManager.registerObserver(kitchen2);

        Scanner scanner=new Scanner(System.in);
        boolean running=true;

        while (running) {
            System.out.println("\nWelcome to the Restaurant Ordering System");
            System.out.println("1. Display Menu");
            System.out.println("2. Place Order");
            System.out.println("3. Exit");
            System.out.println("Please choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    Utils.displayMenu(menu);
                    break;
                case "2":
                    placeOrder(scanner, orderManager);
                    break;
                case "3":
                    System.out.print("Thank you for visiting!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private static void initializeMenu(){
        menu.add(MenuItemFactory.createMenuItem("appetizer","Spring Rolls", 4.99));
        menu.add(MenuItemFactory.createMenuItem("appetizer","Garlic Bread", 3.99));
        menu.add(MenuItemFactory.createMenuItem("maincourse","Grilled Salmon", 13.99));
        menu.add(MenuItemFactory.createMenuItem("maincourse","Steak", 15.99));
        menu.add(MenuItemFactory.createMenuItem("dessert","Cheesecake", 5.99));
        menu.add(MenuItemFactory.createMenuItem("dessert","Ice Cream", 3.99));
    }

    private static void placeOrder(Scanner scanner,OrderManager orderManager){
        Order.Builder orderBuilder=new Order.Builder();
        boolean ordering=true;

        while (ordering){
            Utils.displayMenu(menu);
            System.out.print("Enter the number of the items to add to your order (or 'done' to finish): ");
            String input=scanner.nextLine();

            if(input.equalsIgnoreCase("done")){
                ordering=false;
                break;
            }

            try{
                int itemNumber=Integer.parseInt(input);
                if(itemNumber<1||itemNumber>menu.size()){
                    System.out.println("Invalid item number. Please try again. ");
                } else {
                    MenuItem selectedItem=menu.get(itemNumber-1);
                    orderBuilder.addItem(selectedItem);
                    System.out.println(selectedItem.getName()+" added to your order");
                }
            }catch (NumberFormatException e){
                System.out.println("Invalid input. Please enter a valid number or 'done'.");
            }
        }

        Order order=orderBuilder.build();
        order.displayOrder();
        orderManager.placeOrder(order);

        PaymentContext paymentContext=new PaymentContext();
        boolean paymentCompleted=false;

        while (!paymentCompleted){
            System.out.println("\nChoose Payment Method:");
            System.out.println("1. Credit Card");
            System.out.println("2. Cash");
            System.out.println("Enter choice: ");
            String paymentChoice=scanner.nextLine();

            switch (paymentChoice){
                case "1":
                    System.out.print("Enter Card Number: ");
                    String cardNumber=scanner.nextLine();
                    System.out.print("Enter Card Holder Name: ");
                    String cardHolder=scanner.nextLine();
                    System.out.print("Enter CVV: ");
                    String cvv=scanner.nextLine();
                    PaymentStrategy creditCard=new CreditCardPayment(cardNumber,cardHolder,cvv);
                    paymentContext.setPaymentStrategy(creditCard);
                    paymentContext.executePayment(order.getTotalPrice());
                    paymentCompleted=true;
                    break;
                case "2":
                    PaymentStrategy cash = new CashPayment();
                    paymentContext.setPaymentStrategy(cash);
                    paymentContext.executePayment(order.getTotalPrice());
                    paymentCompleted = true;
                    break;
                default:
                    System.out.println("Invalid payment method. Please try again.");
            }
        }
        System.out.println("Order placed successfully!\n");
    }
}
