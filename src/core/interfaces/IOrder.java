package core.interfaces;

import core.entities.Order;
import java.time.LocalDate;
import java.util.List;

public interface IOrder {

    List<Order> getOrders() throws Exception;

    Order getOrderById(String id) throws Exception;

    void addOrder(Order order) throws Exception;

    boolean isDuplication(String customerCode, String feastCode, LocalDate date) throws Exception;

    void updateOrder(Order order) throws Exception;

    void saveOrdersListToFile() throws Exception;
}
