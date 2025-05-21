package data;

import core.entities.Customer;
import core.interfaces.ICustomer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDAO implements ICustomer {

    private final List<Customer> CUSTOMER_LIST = new ArrayList<>();
    private final FileManager FILE_MANAGER;

    public CustomerDAO(String fileName) throws Exception {
        this.FILE_MANAGER = new FileManager(fileName);
        loadData();
    }

    public final void loadData() throws Exception {
        String customerCode, customerName, email, phoneNumber;
        try {
            CUSTOMER_LIST.clear();
            List<String> customersData = FILE_MANAGER.readDataFromFile();
            for (String e : customersData) {
                List<String> fieldsOfCustomer = Arrays.asList(e.split(","));
                customerCode = fieldsOfCustomer.get(0).trim();
                customerName = fieldsOfCustomer.get(1).trim();
                email = fieldsOfCustomer.get(2).trim();
                phoneNumber = fieldsOfCustomer.get(3).trim();
                Customer customer = new Customer(customerCode, customerName, phoneNumber, email);
                CUSTOMER_LIST.add(customer);
                if (CUSTOMER_LIST.isEmpty()) {
                    throw new Exception("Customer list is empty.");
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public List<Customer> getCustomers() throws Exception {
        CUSTOMER_LIST.sort((e1, e2) -> e1.getCustomerName().compareToIgnoreCase(e2.getCustomerName()));
        return CUSTOMER_LIST;
    }

    @Override
    public Customer getCustomerById(String id) throws Exception {
        Customer customer = CUSTOMER_LIST.stream()
                .filter(e -> e.getCustomerCode()
                .equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
        return customer;
    }

    @Override
    public void addCustomer(Customer customer) throws Exception {
        CUSTOMER_LIST.add(customer);
    }

    @Override
    public void updateCustomer(Customer customer) throws Exception {
        Customer cus = getCustomerById(customer.getCustomerCode());
        if (cus != null) {
            cus.setCustomerName(cus.getCustomerName());
            cus.setEmail(cus.getEmail());
            cus.setPhoneNumber(cus.getPhoneNumber());
        }
    }

    @Override
    public void removeCustomer(Customer customer) throws Exception {
        Customer cus = getCustomerById(customer.getCustomerCode());
        if (cus != null) {
            CUSTOMER_LIST.remove(cus);
        }
    }

    @Override
    public void saveCustomersListToFile() throws Exception {
        List<String> stringObject = CUSTOMER_LIST.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        String data = String.join("\n", stringObject);
        FILE_MANAGER.saveDataToFile(data);
    }

}
