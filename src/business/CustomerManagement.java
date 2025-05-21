package business;

import common.env.Constants;
import common.tools.DataInput;
import common.tools.DataUtils;
import core.entities.Customer;
import core.entities.Feast;
import core.entities.Order;
import core.interfaces.ICustomer;
import core.interfaces.IFeast;
import core.interfaces.IOrder;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import view.Menu;

public class CustomerManagement {

    ICustomer customerDAO;
    IFeast feastDAO;
    IOrder orderDAO;

    public CustomerManagement(ICustomer customerDAO, IFeast feastDAO, IOrder orderDAO) {
        this.customerDAO = customerDAO;
        this.feastDAO = feastDAO;
        this.orderDAO = orderDAO;
    }

    public void processMenu() {
        try {
            do {
                Menu.print("******Customer Management******"
                        + "|1.Add Customer"
                        + "|2.Update Customer"
                        + "|3.Search Customer By Name"
                        + "|4.Display Feast Menu"
                        + "|5.Place feast order"
                        + "|6.Update Order Info"
                        + "|7.Sava Data To File"
                        + "|8.Display Customer or Order Lists"
                        + "|9.Exit |Select:");
                int choice = Menu.getUserChoice();
                switch (choice) {
                    case 1 -> {
                        addNewCustomer();
                    }
                    case 2 -> {
                        updateCustomer();
                    }
                    case 3 -> {
                        searchByName();
                    }
                    case 4 -> {
                        printFeastList(feastDAO.getFeasts());
                    }
                    case 5 -> {
                        placeOrder();
                    }
                    case 6 -> {
                        updateOrder();
                    }
                    case 7 -> {
                        exportToFile();
                    }
                    case 8 -> {
                        System.out.println("Selection:");
                        System.out.println("1.Print all customers");
                        System.out.println("2.Print all orders");
                        System.out.println("3.Exit");
                        System.out.print("Enter your choice: ");
                        int subChoice = Menu.getUserChoice();
                        switch (subChoice) {
                            case 1 ->
                                printCustomerList(customerDAO.getCustomers());
                            case 2 ->
                                printOrderList(orderDAO.getOrders());
                            case 3 -> {
                            }
                            default ->
                                System.out.println("Invalid choice");
                        }
                    }
                    case 9 -> {
                        String confirm = DataInput.getString("Do you want to save changes before exiting? (Y/N): ").toUpperCase();
                        if (confirm.equalsIgnoreCase("Y")) {
                            System.out.println("Data saved to file successfully");
                            exportToFile();
                        }
                        System.out.println("Goodbye!");
                        System.exit(0);
                    }
                    default ->
                        System.out.println("This function is not available");
                }
            } while (true);
        } catch (Exception e) {
            //luu du lieu vao file truoc khi thoat va hien thi loi
            try {
                exportToFile();
            } catch (Exception ex) {
                System.out.println("Error saving data: " + ex.getMessage());
            }
            System.out.println(e.getMessage());
        }
    }

    public Customer inputCustomer() throws Exception {
        String customerCode = DataInput.getString("Enter customer code:", Constants.CUSTOMER_ID_PATTERN).toUpperCase();
        String customerName = DataInput.getString("Enter customer name:", Constants.NAME_PATTERN);
        String phoneNumber = DataInput.getString("Enter phone number:", Constants.PHONE_PATTERN);
        String email = DataInput.getString("Enter email:", Constants.EMAIL_PATTERN);
        customerName = DataUtils.toTitleCase(customerName);
        Customer customer = new Customer(customerCode, customerName, phoneNumber, email);
        return customer;
    }

    public void printCustomerList(List<Customer> customerList) throws Exception {
        if (customerList.isEmpty()) {
            System.out.println("Does not have any customer information.");
            return;
        }
        System.out.println(String.join("", Collections.nCopies(95, "-")));
        System.out.format("%-15s | %-20s | %-20s | %s%n", "Code", "Customer Name", "Phone", "Email");
        System.out.println(String.join("", Collections.nCopies(95, "-")));
        for (Customer e : customerList) {
            System.out.format(
                    "%-15s | %-20s | %-20s | %s%n",
                    e.getCustomerCode(), e.getCustomerName(), e.getPhoneNumber(), e.getEmail()
            );
        }
        System.out.println(String.join("", Collections.nCopies(95, "-")));
    }

    public void printFeastList(List<Feast> feastList) throws Exception {
        if (feastList.isEmpty()) {
            System.out.println("Does not have any feast information.");
            return;
        }
        System.out.println(String.join("", Collections.nCopies(95, "-")));
        System.out.println("List of Set Menus for ordering party:");
        System.out.println(String.join("", Collections.nCopies(95, "-")));
        for (Feast e : feastList) {
            System.out.format("%-13s: %s%n", "Code", e.getFeastCode());
            System.out.format("%-13s: %s%n", "Name", e.getFeastName());
            DecimalFormat formatter = new DecimalFormat("#,###");
            System.out.format("%-13s: %s%n", "Price", formatter.format(e.getPrice()) + " VND");
            System.out.format("%-13s:%n%s%n", "Ingredients", e.getIngredients().replaceAll("#", "\n").replaceAll("\"", ""));
            System.out.println(String.join("", Collections.nCopies(95, "-")));
        }
    }

    public void printOrderList(List<Order> orderList) throws Exception {
        if (orderList.isEmpty()) {
            System.out.println("Does not have any order information.");
            return;
        }
        Collections.sort(orderList, (e1, e2) -> e1.getDate().compareTo(e2.getDate()));
        System.out.println(String.join("", Collections.nCopies(95, "-")));
        System.out.format(
                "%-15s | %-20s | %-20s | %-15s | %-15s | %-15s | %s%n",
                "ID", "Event Date", "Customer ID", "Set Menu", "Price", "Tables", "Cost"
        );
        System.out.println(String.join("", Collections.nCopies(130, "-")));
        DecimalFormat formatter = new DecimalFormat("#,###");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Order e : orderList) {
            System.out.format(
                    "%-15s | %-20s | %-20s | %-15s | %-15s | %-15s | %s%n",
                    e.getOrderID(),
                    e.getDate().format(dateFormatter),
                    e.getCustomerCode(),
                    e.getFeastCode(),
                    formatter.format(e.getPrice()),
                    e.getNumberTable(),
                    formatter.format(e.getTotalPrice())
            );
        }
        System.out.println(String.join("", Collections.nCopies(130, "-")));
    }

    public void addNewCustomer() throws Exception {
        try {
            Customer customer = inputCustomer();
            if (customerDAO.getCustomerById(customer.getCustomerCode()) == null) {
                customerDAO.addCustomer(customer);
                System.out.println("Customer added successfully");
            } else {
                System.out.println("Customer already exists");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void updateCustomer() throws Exception {
        try {
            String customerCode = DataInput.getString("Enter customer code:", Constants.CUSTOMER_ID_PATTERN).toUpperCase();
            Customer customer = customerDAO.getCustomerById(customerCode);
            if (customer == null) {
                System.out.println("This customer does not exist.");
                return;
            }
            String customerName = DataInput.getStringUpdate("Enter customer name:", Constants.NAME_PATTERN);
            String phoneNumber = DataInput.getStringUpdate("Enter phone number:", Constants.PHONE_PATTERN);
            String email = DataInput.getStringUpdate("Enter email:", Constants.EMAIL_PATTERN);
            if ("".equals(customerName)) {
                customerName = customer.getCustomerName();
            }
            if ("".equals(phoneNumber)) {
                phoneNumber = customer.getPhoneNumber();
            }
            if ("".equals(email)) {
                email = customer.getEmail();
            }
            customerName = DataUtils.toTitleCase(customerName);
            customer.setCustomerName(customerName);
            customer.setPhoneNumber(phoneNumber);
            customer.setEmail(email);
            System.out.println("Customer updated successfully");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void searchByName() throws Exception {
        try {
            String name = DataInput.getString("Enter customer name:");
            List<Customer> customersList = customerDAO.getCustomers();
            List<Customer> result = new ArrayList<>();
            customersList.forEach(e -> {
                if (e.getCustomerName().toLowerCase().contains(name.toLowerCase())) {
                    result.add(e);
                }
            });
            if (result.isEmpty()) {
                System.out.println("No one matches the search criteria!");
            } else {
                printCustomerList(result);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void printOrder(Order order) throws Exception {
        Customer customer = customerDAO.getCustomerById(order.getCustomerCode());
        Feast feast = feastDAO.getFeastById(order.getFeastCode());
        System.out.println("Customer order information [Order ID: " + order.getOrderID() + "]");
        System.out.println(String.join("", Collections.nCopies(95, "-")));
        System.out.println("Customer code : " + order.getCustomerCode());
        System.out.println("Customer name : " + customer.getCustomerName());
        System.out.println("Phone number  : " + customer.getPhoneNumber());
        System.out.println("Email         : " + customer.getEmail());
        System.out.println(String.join("", Collections.nCopies(95, "-")));
        System.out.println("Code of Set Menu: " + order.getFeastCode());
        System.out.println("Set menu name   : " + feast.getFeastName());
        System.out.println("Event date      : " + order.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println("Number of tables: " + order.getNumberTable());
        DecimalFormat formatter = new DecimalFormat("#,###");
        System.out.println("Price           : " + formatter.format(order.getPrice()) + " VND");
        System.out.println("Ingredients     : \n" + feast.getIngredients().replaceAll("#", "\n").replaceAll("\"", ""));
        System.out.println(String.join("", Collections.nCopies(95, "-")));
        System.out.println("Total cost      : " + formatter.format(order.getTotalPrice()) + " VND");
        System.out.println(String.join("", Collections.nCopies(95, "-")));
    }

    public void placeOrder() throws Exception {
        String customerCode = DataInput.getString("Enter customer code:", Constants.CUSTOMER_ID_PATTERN).toUpperCase();
        Customer customer = customerDAO.getCustomerById(customerCode);
        if (customer == null) {
            throw new Exception("Customer not found");
        }
        String feastCode = DataInput.getString("Enter feast code:", Constants.FEAST_CODE_LIST).toUpperCase();
        Feast feast = feastDAO.getFeastById(feastCode);
        if (feast == null) {
            throw new Exception("Feast not found");
        }
        int numberTable = DataInput.getIntegerNumber("Enter number of table:");
        LocalDate date = DataInput.getDate("Enter event date:");
        if (date.isBefore(LocalDate.now())) {
            throw new Exception("Event date must be in the future");
        }
        if (orderDAO.isDuplication(customerCode, feastCode, date)) {
            throw new Exception("Order already exists");
        }
        Random random = new Random();
        String orderId = String.valueOf(random.nextInt(999) + 1);
        while (orderDAO.getOrderById(orderId) != null) {
            orderId = String.valueOf(random.nextInt(999) + 1);
        }
        Order order = new Order(orderId, customerCode, feastCode, feast.getPrice(), numberTable, date);
        orderDAO.addOrder(order);
        printOrder(order);
    }

    public void updateOrder() throws Exception {
        try {
            String orderId = DataInput.getString("Enter order id:").toUpperCase();
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                throw new Exception("This Order does not exist .");
            }
            String feastCode = DataInput.getStringUpdate("Enter feast code:", Constants.FEAST_CODE_LIST).toUpperCase();
            int numberTable = DataInput.getIntegerNumber("Enter number of table:");
            LocalDate date = DataInput.getDateUpdate("Enter event date:");
            if (date == null) {
                date = order.getDate();
            } else if (date.isBefore(LocalDate.now())) {
                throw new Exception("Event date must be in the future");
            }
            if ("".equals(feastCode)) {
                feastCode = order.getFeastCode();
            }
            if (numberTable == 0) {
                numberTable = order.getNumberTable();
            }

            order.setFeastCode(feastCode);
            order.setNumberTable(numberTable);
            order.setDate(date);
            System.out.println("Order updated successfully");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void exportToFile() throws Exception {
        customerDAO.saveCustomersListToFile();
        orderDAO.saveOrdersListToFile();
    }

}
