package view;

import business.RoomManagement;
import common.tools.DataInput;
import core.interfaces.IGuest;
import core.interfaces.IRoom;
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

    public static void manageRoom(IRoom service, IGuest guestService) {
        RoomManagement roomMenu = new RoomManagement(service, guestService);
        roomMenu.processMenu();
    }
}
