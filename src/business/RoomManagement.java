package business;

import common.tools.DataInput;
import core.entities.Room;
import core.interfaces.IRoom;
import java.util.Collections;
import java.util.List;
import view.Menu;

public class RoomManagement {

    IRoom roomDAO;

    public RoomManagement(IRoom roomDAO) {
        this.roomDAO = roomDAO;
    }

    public void processMenu() {
        try {
            do {
                Menu.print("******Room Management Module******"
                        + "|1.Import Room Data from Text File"
                        + "|2.Display Available Room List"
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
                        importRoomData();
                    }
                    case 2 -> {
                        printRoomList(roomDAO.getRooms());
                    }
                    case 3 -> {
                        // searchByName();
                    }
                    case 4 -> {
                        // printFeastList(feastDAO.getFeasts());
                    }
                    case 5 -> {
                        // placeOrder();
                    }
                    case 6 -> {
                        // updateOrder();
                    }
                    case 7 -> {
                        // exportToFile();
                    }
                    case 8 -> {
                        // System.out.println("Selection:");
                        // System.out.println("1.Print all customers");
                        // System.out.println("2.Print all orders");
                        // System.out.println("3.Exit");
                        // System.out.print("Enter your choice: ");
                        // int subChoice = Menu.getUserChoice();
                        // switch (subChoice) {
                        //     case 1 ->
                        //         printCustomerList(customerDAO.getCustomers());
                        //     case 2 ->
                        //         printOrderList(orderDAO.getOrders());
                        //     case 3 -> {
                        //     }
                        //     default ->
                        //         System.out.println("Invalid choice");
                        // }
                    }
                    case 9 -> {
                        String confirm = DataInput.getString("Do you want to save changes before exiting? (Y/N): ").toUpperCase();
                        if (confirm.equalsIgnoreCase("Y")) {
                            System.out.println("Data saved to file successfully");
                            // exportToFile();
                        }
                        System.out.println("Goodbye!");
                        System.exit(0);
                    }
                    default ->
                        System.out.println("This function is not available");
                }
            } while (true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void importRoomData() {
        try {
            roomDAO.loadData();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void printRoomList(List<Room> roomList) throws Exception {
        if (roomList.isEmpty()) {
            System.out.println("Room list is currently empty, not loaded yet.");
            return;
        }
        System.out.println(String.join("", Collections.nCopies(115, "-")));
        System.out.format("%-6s | %-18s | %-10s | %-6s | %8s | %s%n", "RoomID", "RoomName", "Type", "Rate", "Capacity", "Furniture");
        System.out.println(String.join("", Collections.nCopies(115, "-")));
        for (Room e : roomList) {
            System.out.format(
                    "%-6s | %-18s | %-10s | %6s | %8s | %s%n",
                    e.getRoomID(), e.getRoomName(), e.getRoomType(), e.getDailyRate(), e.getCapacity(), e.getFurnitureDescription()
            );
        }
        System.out.println(String.join("", Collections.nCopies(115, "-")));
    }

}
