package view;

import business.CustomerManagement;
import common.tools.DataInput;
import core.interfaces.ICustomer;
import core.interfaces.IFeast;
import core.interfaces.IOrder;
import java.util.Arrays;
import java.util.List;

public class Menu {

    public static void print(String str) {
        List<String> menuList = Arrays.asList(str.split("\\|"));
        menuList.forEach(menuItem -> {
            if (menuItem.equalsIgnoreCase("Select")) {
                System.out.print(menuItem);
            } else {
                System.out.println(menuItem);
            }
        });
    }

    public static int getUserChoice() {
        int number = 0;
        try {
            number = DataInput.getIntegerNumber();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return number;
    }

    public static void manageStudent(ICustomer service, IFeast feastService, IOrder orderService) {
        CustomerManagement customerMenu = new CustomerManagement(service, feastService, orderService);
        customerMenu.processMenu();
    }
}
