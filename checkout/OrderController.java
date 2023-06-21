package checkout;

import account.Customer;
import adt.MapInterface;
import utility.CalendarUtils;
import utility.Email;

public class OrderController {
  public static void generateOrder(Order order, Customer customer) {
    OrderRepository.create(order, customer.getEmail());

    // Send an order placed email
    generateOrderPlacedEmail(customer);

    MapInterface<Integer,Order> ordersMap = OrderRepository.findAllByCustomerEmail(customer.getEmail());
    customer.setOrders(ordersMap);
  }

  // Send a customized order placed email for customer user
  private static boolean generateOrderPlacedEmail(Customer customer) {
    String toEmail = customer.getEmail();
    String subject = "Your Order is Placed Successfully!";
    String text = "Hi " + customer.getName().getFullName() + ", your order is placed successfully"
        + " and we will delivery your food on the date specified.\n\n"
        + "Your email: " + customer.getEmail() + "\n"
        + "Payment method: " + Order.getCurrentOrder().getPaymentMethod() + "\n"
        + String.format("Amount paid: RM%.2f", Order.getCurrentOrder().getFinalPrice()) + "\n"
        + "Order time: " + Order.getCurrentOrder().getOrderTime().format(CalendarUtils.DATE_TIME_FORMATTER);
    return Email.sendEmail(toEmail, subject, text);
  }
}
