package kitchen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ListIterator;
import java.util.InputMismatchException;

import adt.AbstractPriorityQueue;
import adt.Entry;
import adt.HeapAdaptablePriorityQueue;
import adt.ListInterface;
import adt.MapInterface;
import adt.HeapAdaptablePriorityQueue.AdaptablePQEntry;
import checkout.DeliveryDatePage;
import checkout.Order;
import checkout.OrderRepository;
import main.Input;
import ordermenu.AlaCarte;
import ordermenu.Cart;
import ordermenu.CartItem;
import ordermenu.SetMeal;
import utility.CalendarUtils;
import utility.Font;
import utility.Screen;

public class Kitchen {
    private static AbstractPriorityQueue<String, Order> orderQueue = new HeapAdaptablePriorityQueue<String, Order>();

    // method for initialising the queue
    public static void initialise() {
        // get all processing order from database
        MapInterface<Integer, Order> ordersMap = OrderRepository.findAllProcessingOrders();
        
        for (Order order : ordersMap.values()) {
            orderQueue.insert(getKey(order), order);
        }
    }

    // ----------- MANAGE ORDERS (Kitchen Menu) -----------
    public static void manageOrders() {
        Screen.clear();
        Font.print(Font.ANSI_CYAN, "           <<< Loading Kitchen... >>>");
        initialise(); 
        int choice = 0;
        do {
            displayKitchenMenu();
            choice = Input.getMenuChoice(4);
            switch (choice) {
            case 1:
                // Order Queue View
                if (orderQueue.isEmpty()) {
                    displayEmptyKitchenMessage();
                    break;
                }
                orderQueueView();
                break;
            case 2:
                // Order Details View
                if (orderQueue.isEmpty()) {
                    displayEmptyKitchenMessage();
                    break;
                }
                orderDetailsView();
                break;
            case 3:
                // Change Delivery Dates of Orders
                if (orderQueue.isEmpty()) {
                    displayEmptyKitchenMessage();
                    break;
                }
                changeDeliveryDates();
                break;
            case 4:
                // Go Back
                break;
            default:
                break;
            }
        } while (choice != 4);

        // reset order queue 
        orderQueue = null;
    }

    private static void displayKitchenMenu() {
        Screen.clear();
        System.out.printf("\n%47s\n", "+-------------------------------+");
        System.out.printf("%47s\n", "|          Manage Orders        |");
        System.out.printf("%47s\n", "|-------------------------------|");
        System.out.printf("%47s\n", "| 1. Order Queue View           |");
        System.out.printf("%47s\n", "| 2. Order Details View         |");
        System.out.printf("%47s\n", "| 3. Change Delivery Dates      |");
        System.out.printf("%47s\n", "|-------------------------------|");
        System.out.printf("%47s\n", "| 4. Back                       |");
        System.out.printf("%47s\n", "+-------------------------------+");
    }

    // ----------- END OF MANAGE ORDERS (Kitchen Menu) -----------

    // ----------- ORDER QUEUE VIEW -----------
    private static void orderQueueView() {
        int choice = 0;
        Order minOrder;
        do {
            if (orderQueue.isEmpty()) {
                displayEmptyKitchenMessage();
                break;
            }
            displayOrderQueue();
            System.out.printf("\n%47s\n", "+-------------------------------+");
            System.out.printf("%47s\n", "| 1. Prepare Next Order         |");
            System.out.printf("%47s\n", "| 2. Back                       |");
            System.out.printf("%47s\n", "+-------------------------------+");
            choice = Input.getMenuChoice(2);
            switch (choice) {
            case 1:
                // Prepare First Order
                minOrder = orderQueue.removeMin().getValue();
                minOrder.setStatus("DELIVERED");
                // update database status
                OrderRepository.updateStatus(minOrder);

                // print out successful message
                System.out.println();
                Font.print(Font.ANSI_GREEN, "<<< Order has been prepared and delivered! >>>");
                System.out.printf("%s","Press enter to continue...");
                Input.SCANNER.nextLine();
                break;
            case 2:
                // Go Back
                break;
            default:
                break;
            }
        } while (choice != 2);
    }

    // ----------- END OF ORDER QUEUE VIEW -----------

    // ----------- ORDER DETAILS VIEW -----------
    private static Entry<String, Order> orderEntry;
    private static ListIterator<Entry<String,Order>> orderQueueIterator;

    private static void orderDetailsView() {

        initialiseOrderQueueIterator();
        int choice = 0;
        
        do {
            if (orderQueue.isEmpty()) {
                displayEmptyKitchenMessage();
                break;
            }
            displayCurrentOrderDetails((AdaptablePQEntry<String, Order>) orderEntry);

            choice = orderDetailsViewOption();
            switch (choice) {
            case 0:
                // Next Order
                if (orderQueueIterator.hasNext()) {
                    orderEntry = orderQueueIterator.next();
                } else {
                    System.out.printf("\n%s","This is the last Order. Press enter to continue...");
                    Input.SCANNER.nextLine();
                }
                break;
            case 1:
                // Previous Order
                if (orderQueueIterator.hasPrevious()) {
                    orderEntry = orderQueueIterator.previous();
                } else {
                    System.out.printf("\n%s","This is the first Order. Press enter to continue...");
                    Input.SCANNER.nextLine();
                }
                break;
            case 2:
                // prepare this order
                orderQueueIterator.remove();
                // set status
                orderEntry.getValue().setStatus("DELIVERED");
                // update database status
                OrderRepository.updateStatus(orderEntry.getValue());

                // print out successful message
                System.out.println();
                Font.print(Font.ANSI_GREEN, "<<< Order has been prepared and delivered! >>>");
                System.out.printf("%s","Press enter to continue...");
                Input.SCANNER.nextLine();

                //reload queue iterator
                initialiseOrderQueueIterator();
                
                break;
            case 3:
                // Go Back
                break;
            default:
                break;
            }
        } while (choice != 3);
    }

    private static void initialiseOrderQueueIterator() {
        orderQueueIterator =  (ListIterator<Entry<String,Order>>) orderQueue.entries().iterator();
        orderEntry = orderQueueIterator.next();

    }

    private static int orderDetailsViewOption() {
        int choice = 0;
        boolean validChoice = false;
        System.out.printf("\n%47s\n", "+-------------------------------+");
        if (orderQueueIterator.hasNext()) {
        System.out.printf("%47s\n", "| 0. Next Order                 |");
        }
        if (orderQueueIterator.hasPrevious()) {
        System.out.printf("%47s\n", "| 1. Previous Order             |");
        }
        System.out.printf("%47s\n", "| 2. Prepare this Order         |");
        System.out.printf("%47s\n", "| 3. Back                       |");
        System.out.printf("%47s\n", "+-------------------------------+");
        do {
            try {
                System.out.printf("\n%47s", "> Please enter your choice (0-3): ");
                choice = Input.SCANNER.nextInt();
                if (choice < 0 || choice > 3) {
                throw new Input.InvalidMenuChoiceException(2);
                } else {
                validChoice = true;
                }
            } catch (Input.InvalidMenuChoiceException invalidMenuChoiceException) {
                System.out.printf(Font.getStr(Font.ANSI_RED, 
                "<<< Invalid choice, only 0 to 3 are accepted. >>>"));
            } catch (InputMismatchException inputMismatchException) {
                System.out.printf("%65s", Font.getStr(Font.ANSI_RED, "<<< Invalid choice, please enter integers only. >>>"));
            }
            Input.SCANNER.nextLine();
        } while (!validChoice);
        return choice;
    }
    
    // ----------- END OF ORDER DETAILS VIEW -----------

    // ----------- CHANGE DELIVERY DATES OF ORDERS -----------
    private static void changeDeliveryDates() {
        int choice = 0;
        Entry<String, Order> modifiedOrderEntry;
        Order modifiedOrder; 
        do {
            if (orderQueue.isEmpty()) {
                displayEmptyKitchenMessage();
                break;
            }
            displayOrderQueue();
            System.out.printf("\n%47s\n", "+-------------------------------+");
            System.out.printf("%47s\n", "| 1. Postpone/Advance an order  |");
            System.out.printf("%47s\n", "| 2. Back                       |");
            System.out.printf("%47s\n", "+-------------------------------+");
            choice = Input.getMenuChoice(2);
            switch (choice) {
            case 1:
                // Change an order's delivery date

                // get queue id of the order
                modifiedOrderEntry = ((HeapAdaptablePriorityQueue<String,Order>)
                orderQueue).getEntry(modifiedOrderIDchoice());
                modifiedOrder = modifiedOrderEntry.getValue();

                // specify new delivery date
                displayCurrentOrderDetails((AdaptablePQEntry<String, Order>)modifiedOrderEntry);
                // Update order's delivery date
                modifiedOrder.setDeliveryDate(getNewDeliveryDate());

                // update key of the queue entry (delivery date + order time)
                ((HeapAdaptablePriorityQueue<String, Order>)
                orderQueue).replaceKey(modifiedOrderEntry, getKey(modifiedOrder));
                // update database delivery date
                OrderRepository.updateDeliveryDate(modifiedOrder);

                // remove and re-insert to the queue to reshuffle
                ((HeapAdaptablePriorityQueue<String, Order>)
                orderQueue).removeEntry(modifiedOrderEntry);
                orderQueue.insert(modifiedOrderEntry.getKey(), modifiedOrderEntry.getValue());

                // print out successful message
                System.out.println();
                Font.print(Font.ANSI_GREEN, "<<< Order's delivery date has been successfully changed! >>>");
                System.out.printf("%s","Press enter to continue...");
                Input.SCANNER.nextLine();
                break;
            case 2:
                // Go Back
                break;
            default:
                break;
            }
        } while (choice != 2);
    }

    private static int modifiedOrderIDchoice() {
        int queueID = 0;
        boolean validQueueID = false;
        int lastQueueID = orderQueue.size() - 1;
        do {
            try {
                System.out.printf("\n%30s", "> Please enter order to change its delivery date (0-"+lastQueueID+"): ");
                queueID = Input.SCANNER.nextInt();
                if (queueID < 0 || queueID > lastQueueID) {
                throw new Input.InvalidMenuChoiceException(lastQueueID);
                } else {
                validQueueID = true;
                }
            } catch (Input.InvalidMenuChoiceException invalidMenuChoiceException) {
                System.out.printf(Font.getStr(Font.ANSI_RED, 
                "<<< Invalid ID, only 0 to "+ lastQueueID + "are accepted. >>>"));
            } catch (InputMismatchException inputMismatchException) {
                System.out.printf("%65s", Font.getStr(Font.ANSI_RED, "<<< Invalid ID, please enter integers only. >>>"));
            }
            Input.SCANNER.nextLine();
        } while (!validQueueID);
        return queueID;
    }

    private static LocalDate getNewDeliveryDate() {
        System.out.println("==================================================");
        System.out.println("|            Choose New Delivery Date            |");
        System.out.println("==================================================");

        System.out.println("\n<<< Please enter the new delivery date below >>>");

        LocalDate deliveryDate = DeliveryDatePage.getDeliveryDate();

        return deliveryDate;
    }

    // ----------- END OF CHANGE DELIVERY DATES OF ORDERS -----------

    // ------------------ UTILITY FUNCTIONS -------------------

    private static String getKey(Order order){
        // key (comparable) = deliveryDate + orderTime
        String key;

        // String formatter for deliveryDate and orderTime
        DateTimeFormatter dateTimeformatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate deliveryDate; LocalDateTime orderTime;

        // get deliveryDate and orderTime from order instance
        deliveryDate = order.getDeliveryDate();
        orderTime = order.getOrderTime();

        // concate deliveryDate and orderTime
        key = deliveryDate.format(dateFormatter) + orderTime.format(dateTimeformatter);
        return key;
    }

    private static void displayEmptyKitchenMessage() {
        Screen.clear();
        System.out.printf("\n%47s\n", "+-------------------------------+");
        System.out.printf("%47s\n", "|  No Orders in Queue, Hurray!  |");
        System.out.printf("%47s\n", "|-------------------------------|");
        System.out.print("\nPress enter to continue...");
        Input.SCANNER.nextLine();
    }

    private static void displayCurrentOrderDetails(AdaptablePQEntry<String, Order> orderEntry) {
        Order order = orderEntry.getValue();
        Screen.clear();
        //TODO : ADD ORDER ID
        System.out.println("===============================================================");
        System.out.println("|                        Order Details                        |");
        System.out.println("===============================================================");
        System.out.printf("| Queue No : %-13s | Delivery Date : %-16s | \n", orderEntry.getIndex(),
            order.getDeliveryDate().format(CalendarUtils.DATE_FORMATTER));
        System.out.println("|-------------------------------------------------------------|");
        System.out.printf("| Order ID         : %-40s | \n", order.getId());
        System.out.printf("| Status           : %-40s | \n", order.getStatus());
        System.out.printf("| Order Time       : %-40s | \n", order.getOrderTime().format(CalendarUtils.DATE_TIME_FORMATTER));
        System.out.printf("| Delivery Address : %-40s | \n", order.getDeliveryAddress().getAddressLine());
        System.out.printf("|                    %-5s %-34s | \n", order.getDeliveryAddress().getPostcode(),
            order.getDeliveryAddress().getCity());
        System.out.printf("|                    %-40s | \n", order.getDeliveryAddress().getState());
        System.out.println("|-------------------------------------------------------------|");
        System.out.printf("|                            | Amount Paid      : RM%-9s | \n",
            String.format("%.2f", order.getFinalPrice()));
        System.out.println("|-------------------------------------------------------------|");
        ListInterface<CartItem> cartItems = order.getItems();
        Cart.displayCartItems(cartItems);
        System.out.println("===============================================================");
    }

    private static void displayOrderQueue() {
        Font.print(Font.ANSI_CYAN, "           <<< Loading order queue... >>>");

        Screen.clear();
        System.out.println(
        "    +----------------------------------------------------+");
        System.out.println(Font.getStr(Font.ANSI_CYAN, 
        "                          Orders Queue                   "));
        System.out.println(
        "    |----------------------------------------------------|");
        System.out.println(
        "    | Queue ID | Status     | Order Date | Delivery Date |");
        System.out.println(
        "    |----------------------------------------------------|");


        for (Entry<String, Order> orderEntry : orderQueue.entries()) {
        Order order = orderEntry.getValue();
        ListInterface<CartItem> cartItems = order.getItems();
        int queueID = ((AdaptablePQEntry<String, Order>) orderEntry).getIndex();
        System.out.println(
            String.format("    | %-8s | %-10s | %-10s | %-13s |", 
            queueID, order.getStatus(),
            order.getOrderTime().toLocalDate(), order.getDeliveryDate()));
            if (queueID == 0) {
                for (CartItem cartItem : cartItems) {
                    System.out.println(
                 "    |----------------------------------------------------|");
                    if (cartItem instanceof AlaCarte) {
                        System.out.println(String.format("    | %-21s | %-22s%1s%3s |", "", 
                        ((AlaCarte)cartItem).getAlaCarteFoodItem().getFoodName(),
                        "x", cartItem.getQuantity()));
                    } else if (cartItem instanceof SetMeal) {
                        System.out.println(String.format("    | %-21s | %-22s%1s%3s |", 
                    "", ((SetMeal)cartItem).getMainDishFoodItem().getFoodName(),
                        "x", cartItem.getQuantity()));
                        System.out.println(String.format("    | %-21s | %-22s%1s%3s |", 
                        "", ((SetMeal)cartItem).getSideDishFoodItem().getFoodName(),
                        "x", cartItem.getQuantity()));
                        System.out.println(String.format("    | %-21s | %-22s%1s%3s |", 
                        "", ((SetMeal)cartItem).getDrinkFoodItem().getFoodName(),
                        "x", cartItem.getQuantity()));
                    }
                    System.out.println(
                 "    |----------------------------------------------------|");
                }
                
            }
        }

        System.out.println(
        "    +----------------------------------------------------+");
    }

    // --------------- END OF UTILITY FUNCTIONS ---------------
}
