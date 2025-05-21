package core.interfaces;

import core.entities.Customer;
import java.util.List;

public interface ICustomer {

    List<Customer> getCustomers() throws Exception;

    Customer getCustomerById(String id) throws Exception;

    void addCustomer(Customer customer) throws Exception;

    void updateCustomer(Customer customer) throws Exception;

    void removeCustomer(Customer customer) throws Exception;

    void saveCustomersListToFile() throws Exception;
}
