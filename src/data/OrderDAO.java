package data;

import core.entities.Order;
import core.interfaces.IOrder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderDAO implements IOrder {

    private final List<Order> ORDERS_LIST = new ArrayList<>();
    private final FileManager FILE_MANAGER;

    public OrderDAO(String fileName) throws Exception {
        this.FILE_MANAGER = new FileManager(fileName);
        loadData();
    }

    public final void loadData() throws Exception {
        String orderID, customerCode, feastCode;
        LocalDate date;
        double price;
        int numberTable;
        try {
            ORDERS_LIST.clear();
            List<String> orderData = FILE_MANAGER.readDataFromFile();
            for (String e : orderData) {
                List<String> fieldsOfOrder = Arrays.asList(e.split(","));
                orderID = fieldsOfOrder.get(0).trim();
                customerCode = fieldsOfOrder.get(1).trim();
                feastCode = fieldsOfOrder.get(2).trim();
                price = Double.parseDouble(fieldsOfOrder.get(3).trim());
                numberTable = Integer.parseInt(fieldsOfOrder.get(4).trim());
                date = LocalDate.parse(fieldsOfOrder.get(5).trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                Order order = new Order(orderID, customerCode, feastCode, price, numberTable, date);
                ORDERS_LIST.add(order);
                if (ORDERS_LIST.isEmpty()) {
                    throw new Exception("Order list is empty.");
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public List<Order> getOrders() throws Exception {
        return ORDERS_LIST;
    }

    @Override
    public void addOrder(Order order) throws Exception {
        ORDERS_LIST.add(order);
    }

    @Override
    public Order getOrderById(String id) throws Exception {
        Order order = ORDERS_LIST.stream()
                .filter(e -> e.getOrderID()
                .equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
        return order;
    }

    @Override
    public void updateOrder(Order order) throws Exception {
        Order orderO = getOrderById(order.getOrderID());
        if (orderO == null) {
            throw new Exception("Order not found");
        }
        orderO.setCustomerCode(order.getCustomerCode());
        orderO.setFeastCode(order.getFeastCode());
        orderO.setPrice(order.getPrice());
        orderO.setNumberTable(order.getNumberTable());
        orderO.setDate(order.getDate());
        orderO.setTotalPrice(order.getTotalPrice());
    }

    @Override
    public boolean isDuplication(String customerCode, String feastCode, LocalDate date) throws Exception {
        Order order = ORDERS_LIST.stream()
                .filter(
                        e -> e.getCustomerCode().equalsIgnoreCase(customerCode)
                        && e.getFeastCode().equalsIgnoreCase(feastCode)
                        && e.getDate().equals(date)
                )
                .findFirst()
                .orElse(null);
        return order != null;
    }

    @Override
    public void saveOrdersListToFile() throws Exception {
        List<String> stringObject = ORDERS_LIST.stream()
                .map(String::valueOf).toList();
        String data = String.join("\n", stringObject);
        FILE_MANAGER.saveDataToFile(data);
    }
}
