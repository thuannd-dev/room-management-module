package business;

import common.env.Constants;
import common.tools.DataInput;
import common.tools.DataUtils;
import core.entities.Guest;
import core.entities.Room;
import core.interfaces.IGuest;
import core.interfaces.IRoom;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import view.Menu;

public class RoomManagement {

    private final IRoom roomDAO;
    private final IGuest guestDAO;

    public RoomManagement(IRoom roomDAO, IGuest guestDAO) {
        this.roomDAO = roomDAO;
        this.guestDAO = guestDAO;
    }

    public void processMenu() {
        try {
            do {
                Menu.print("******Room Management Module******"
                        + "|1.Import Room Data from Text File"
                        + "|2.Display Available Room List"
                        + "|3.Enter Guest Information"
                        + "|4.Update Guest Stay Information"
                        + "|5.Search Guest by National ID"
                        + "|6.Delete Guest Reservation Before Arrival"
                        + "|7.List Vacant Rooms"
                        + "|8.Monthly Revenue Report"
                        + "|9.Revenue Report by Room Type"
                        + "|10.Save Guest Information"
                        + "|11.Exit |Select:");
                int choice = Menu.getUserChoice();
                switch (choice) {
                    case 1 ->
                        importRoomData();
                    case 2 ->
                        printRoomList(roomDAO.getRooms());
                    case 3 ->
                        addGuest();
                    case 4 ->
                        updateGuestStayInformation();
                    case 5 ->
                        searchGuestByNationalID();
                    case 6 ->
                        deleteGuestReservationBeforeArrival();
                    case 7 ->
                        printListVacantRooms();
                    case 8 ->
                        printMonthlyRevenueReport();
                    case 9 ->
                        printRevenueReportByRoomType();
                    case 10 ->
                        saveGuestInformation();
                    case 11 ->
                        handleExit();
                    default ->
                        System.out.println("This function is not available");
                }
            } while (true);
        } catch (Exception e) {
            //luu du lieu vao file truoc khi thoat va hien thi loi
            try {
                guestDAO.saveGuestsListToFile();
            } catch (Exception ex) {
                System.out.println("Error saving data: " + ex.getMessage());
            }
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
        System.out.println(Constants.SEPARATOR);
        System.out.format("%-6s | %-18s | %-10s | %-6s | %8s | %s%n",
                "RoomID", "RoomName", "Type", "Rate", "Capacity", "Furniture");
        System.out.println(Constants.SEPARATOR);
        roomList.forEach(room -> System.out.format(
                "%-6s | %-18s | %-10s | %6s | %8s | %s%n",
                room.getRoomID(), room.getRoomName(), room.getRoomType(),
                room.getDailyRate(), room.getCapacity(), room.getFurnitureDescription()
        ));
        System.out.println(Constants.SEPARATOR);
    }

    public boolean isRoomAvailable(String roomId, LocalDate startDate, int numberOfDays) throws Exception {
        return isRoomAvailable(roomId, startDate, numberOfDays, null);
    }

    public boolean isRoomAvailable(String roomId, LocalDate startDate, int numberOfDays, String excludeGuestId) throws Exception {
        Room room = roomDAO.getRoomById(roomId);
        if (room == null) {
            throw new Exception("Room not found!");
        }

        LocalDate endDate = startDate.plusDays(numberOfDays - 1);
        return guestDAO.getGuests().stream()
                .filter(guest -> guest.getDesiredRoomID().equalsIgnoreCase(roomId))
                .filter(guest -> excludeGuestId == null || !guest.getNationalID().equalsIgnoreCase(excludeGuestId))
                .noneMatch(guest -> {
                    LocalDate guestEndDate = guest.getStartDate().plusDays(guest.getNumberOfRentalDays() - 1);
                    return !(endDate.isBefore(guest.getStartDate()) || startDate.isAfter(guestEndDate));
                });
    }

    public Guest inputGuest() throws Exception {
        String guestID = DataInput.getString("Enter guest ID:", Constants.NATIONAL_ID_PATTERN);
        String fullName = DataInput.getString("Enter full name:", Constants.GUEST_NAME_PATTERN);
        LocalDate birthdate = DataInput.getDate("Enter birthdate (dd/mm/yyyy):");
        String gender = DataInput.getString("Enter gender (Male/Female):", Constants.GUEST_GENDER_PATTERN);
        String phoneNumber = DataInput.getString("Enter phone number:", Constants.GUEST_PHONE_PATTERN);
        String desiredRoomID = DataInput.getString("Enter desired room ID:", Constants.ROOM_ID_PATTERN);
        int numberOfRentalDays = DataInput.getIntegerNumber("Enter number of rental days:");
        LocalDate startDate = DataInput.getDate("Enter start date (dd/mm/yyyy):");
        String nameOfCoTenant = DataInput.getString("Enter name of co-tenant(Press enter if none):",
                Constants.GUEST_NAME_CO_TENANT_PATTERN);
        return new Guest(guestID, fullName, birthdate, gender, phoneNumber, desiredRoomID,
                numberOfRentalDays, startDate, nameOfCoTenant);
    }

    public void addGuest() throws Exception {
        Guest guest = inputGuest();
        if (guestDAO.getGuestById(guest.getNationalID()) != null) {
            throw new Exception("Guest already exists");
        }
        if (roomDAO.getRoomById(guest.getDesiredRoomID()) == null) {
            throw new Exception("Room not found!");
        }
        if (!isRoomAvailable(guest.getDesiredRoomID(), guest.getStartDate(), guest.getNumberOfRentalDays())) {
            throw new Exception("Room is not available for the requested dates");
        }
        guestDAO.addGuest(guest);
        System.out.println("Guest information added successfully");
    }

    public void printGuestInformationToUpdate(Guest guest) throws Exception {
        System.out.println(Constants.SEPARATOR);
        System.out.println("Guest information [National ID: " + guest.getNationalID() + "]");
        System.out.println(Constants.SEPARATOR);
        System.out.println("Full name        : " + guest.getFullName());
        System.out.println("Phone number     : " + guest.getPhoneNumber());
        System.out.println("Birth day        : " + guest.getBirthdate().format(Constants.DATE_FORMATTER));
        System.out.println("Gender           : " + guest.getGender());
        System.out.println(Constants.SEPARATOR);
        System.out.println("Rental room      : " + guest.getDesiredRoomID());
        System.out.println("Check in         : " + guest.getStartDate().format(Constants.DATE_FORMATTER));
        System.out.println("Rental days      : " + guest.getNumberOfRentalDays());
        System.out.println("Check out        : " + guest.getStartDate().plusDays(guest.getNumberOfRentalDays()).format(Constants.DATE_FORMATTER));
        System.out.println("Name of co-tenant: " + guest.getNameOfCoTenant());
        System.out.println(Constants.SEPARATOR);
    }

    public void updateGuestStayInformation() throws Exception {
        String guestID = DataInput.getString("Enter guest ID:", Constants.NATIONAL_ID_PATTERN);
        Guest guest = guestDAO.getGuestById(guestID);
        if (guest == null) {
            System.out.println(Constants.SEPARATOR);
            System.out.println("No guest found with the requested ID '" + guestID + "'");
            return;
        }
        printGuestInformationToUpdate(guest);
        System.out.println("Please enter new information!");

        String fullName = DataInput.getString("Enter full name:", Constants.GUEST_NAME_PATTERN);
        LocalDate birthdate = DataInput.getDate("Enter birthdate (dd/mm/yyyy):");
        String gender = DataInput.getString("Enter gender (Male/Female):", Constants.GUEST_GENDER_PATTERN);
        String phoneNumber = DataInput.getString("Enter phone number:", Constants.GUEST_PHONE_PATTERN);
        String desiredRoomID = DataInput.getString("Enter desired room ID:", Constants.ROOM_ID_PATTERN);
        int numberOfRentalDays = DataInput.getIntegerNumber("Enter number of rental days:");
        LocalDate startDate = DataInput.getDate("Enter start date (dd/mm/yyyy):");
        String nameOfCoTenant = DataInput.getString("Enter name of co-tenant(Press enter if none):",
                Constants.GUEST_NAME_CO_TENANT_PATTERN);

        if (roomDAO.getRoomById(desiredRoomID) == null) {
            throw new Exception("Room not found!");
        }
        if (!isRoomAvailable(desiredRoomID, startDate, numberOfRentalDays, guest.getNationalID())) {
            throw new Exception("Room is not available for the requested dates");
        }

        guest.setFullName(fullName);
        guest.setBirthdate(birthdate);
        guest.setGender(gender);
        guest.setPhoneNumber(phoneNumber);
        guest.setDesiredRoomID(desiredRoomID);
        guest.setNumberOfRentalDays(numberOfRentalDays);
        guest.setStartDate(startDate);
        guest.setNameOfCoTenant(nameOfCoTenant);
        guestDAO.updateGuest(guest);
        System.out.println("Guest information updated successfully");
    }

    public void printGuestInformation(Guest guest) throws Exception {
        if (guest == null) {
            throw new Exception("Printing guest information failed");
        }
        if (roomDAO.getRoomById(guest.getDesiredRoomID()) == null) {
            throw new Exception("Please load room data first!");
        }
        Room room = roomDAO.getRoomById(guest.getDesiredRoomID());

        System.out.println(Constants.SEPARATOR);
        System.out.println("Guest information [National ID: " + guest.getNationalID() + "]");
        System.out.println(Constants.SEPARATOR);
        System.out.println("Full name    : " + guest.getFullName());
        System.out.println("Phone number : " + guest.getPhoneNumber());
        System.out.println("Birth day    : " + guest.getBirthdate().format(Constants.DATE_FORMATTER));
        System.out.println("Gender       : " + guest.getGender());
        System.out.println(Constants.SEPARATOR);
        System.out.println("Rental room  : " + guest.getDesiredRoomID());
        System.out.println("Check in     : " + guest.getStartDate().format(Constants.DATE_FORMATTER));
        System.out.println("Rental days  : " + guest.getNumberOfRentalDays());
        System.out.println("Check out    : " + guest.getStartDate().plusDays(guest.getNumberOfRentalDays()).format(Constants.DATE_FORMATTER));
        System.out.println(Constants.SEPARATOR);
        System.out.println("Room information:");
        System.out.println("+ ID       : " + room.getRoomID());
        System.out.println("+ Room     : " + room.getRoomName());
        System.out.println("+ Type     : " + room.getRoomType());
        System.out.println("+ Daily rate: " + room.getDailyRate() + "$");
        System.out.println("+ Capacity : " + room.getCapacity());
        System.out.println("+ Furniture: " + room.getFurnitureDescription());
        System.out.println(Constants.SEPARATOR);
    }

    public void searchGuestByNationalID() throws Exception {
        String guestID = DataInput.getString("Enter guest ID:", Constants.NATIONAL_ID_PATTERN);
        Guest guest = guestDAO.getGuestById(guestID);
        if (guest == null) {
            System.out.println(Constants.SEPARATOR);
            System.out.println("No guest found with the requested ID '" + guestID + "'");
            return;
        }
        printGuestInformation(guest);
    }

    public void deleteGuestReservationBeforeArrival() throws Exception {
        String guestID = DataInput.getString("Enter guest ID:", Constants.NATIONAL_ID_PATTERN);
        Guest guest = guestDAO.getGuestById(guestID);
        if (guest == null) {
            System.out.println(Constants.SEPARATOR);
            System.out.println("Booking details for ID '" + guestID + "' could not be found.");
            return;
        }
        if (guest.getStartDate().isBefore(LocalDate.now()) || guest.getStartDate().isEqual(LocalDate.now())) {
            System.out.println(Constants.SEPARATOR);
            System.out.println("The room booking for this guest cannot be cancelled!");
            return;
        }
        printGuestInformation(guest);
        String confirm = DataInput.getString("Are you sure you want to cancel the booking for ID '"
                + guestID + "'? (Y/N):", Constants.CONFIRM_PATTERN);
        if (confirm.equalsIgnoreCase("Y")) {
            guestDAO.removeGuest(guest);
            System.out.println("... System message ...");
            System.out.println("Booking details for ID '" + guestID + "' have been successfully cancelled.");
        } else {
            System.out.println("Cancelled request");
        }
    }

    public void printListVacantRooms() throws Exception {
        List<Room> roomList = roomDAO.getRooms();
        if (roomList.isEmpty()) {
            System.out.println("Room list is currently empty, not loaded yet.");
            return;
        }
        List<Room> vacantRoomList = roomList.stream()
                .filter(room -> {
                    try {
                        return isRoomAvailable(room.getRoomID(), LocalDate.now(), 1);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        if (vacantRoomList.isEmpty()) {
            System.out.println("All rooms are currently rented out — no availability at the moment!");
        } else {
            System.out.println("Available Room List");
            printRoomList(vacantRoomList);
        }
    }

    public double calculateMonthlyRevenueOfRoom(Room room, LocalDate month) throws Exception {
        return guestDAO.getGuests().stream()
                .filter(guest -> guest.getDesiredRoomID().equalsIgnoreCase(room.getRoomID())
                && guest.getStartDate().getMonth().equals(month.getMonth())
                && guest.getStartDate().getYear() == month.getYear())
                .mapToDouble(guest -> guest.getNumberOfRentalDays() * room.getDailyRate())
                .sum();
    }

    public void printMonthlyRevenueReport() throws Exception {
        LocalDate month = DataInput.getMonth("Enter month (mm/yyyy):");
        List<Room> roomList = roomDAO.getRooms();
        List<Room> monthlyRevenueRoomList = roomList.stream()
                .filter(room -> {
                    try {
                        return calculateMonthlyRevenueOfRoom(room, month) > 0;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        if (monthlyRevenueRoomList.isEmpty()) {
            System.out.println("There is no data on guests who have rented rooms");
            return;
        }

        System.out.format("Monthly Revenue Report - %s%n", month.format(Constants.MONTH_FORMATTER));
        System.out.println(String.join("", Collections.nCopies(68, "-")));
        System.out.format("  %-6s | %-15s | %-10s | %10s | %10s%n",
                "RoomID", "Room Name", "Room type", "DailyRate", "Amount");
        System.out.println(String.join("", Collections.nCopies(68, "-")));

        monthlyRevenueRoomList.forEach(room -> {
            try {
                System.out.format("  %-6s | %-15s | %-10s | %10s | %10s%n",
                        room.getRoomID(), room.getRoomName(), room.getRoomType(),
                        room.getDailyRate(), calculateMonthlyRevenueOfRoom(room, month));
            } catch (Exception e) {
                // Handle exception silently
            }
        });
        System.out.println(String.join("", Collections.nCopies(68, "-")));
    }

    public double calculateTotalRevenueOfRoomType(String roomType) throws Exception {
        return guestDAO.getGuests().stream()
                .filter(guest -> {
                    try {
                        Room room = roomDAO.getRoomById(guest.getDesiredRoomID());
                        return room != null && room.getRoomType().equalsIgnoreCase(roomType);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(guest -> {
                    try {
                        Room room = roomDAO.getRoomById(guest.getDesiredRoomID());
                        return guest.getNumberOfRentalDays() * room.getDailyRate();
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .sum();
    }

    public void printRevenueReportByRoomType() throws Exception {
        String roomType = DataInput.getString("Enter room type:", Constants.ROOM_TYPE_PATTERN);
        roomType = DataUtils.toTitleCase(roomType);
        double totalRevenue = calculateTotalRevenueOfRoomType(roomType);

        System.out.format("     Revenue Report by Room Type\n");
        System.out.println(String.join("", Collections.nCopies(40, "-")));
        System.out.format("  %-15s | %12s%n", "Room type", "Amount");
        System.out.println(String.join("", Collections.nCopies(40, "-")));
        System.out.format("  %-15s | %15s%n", roomType, "$" + Constants.CURRENCY_FORMATTER.format(totalRevenue));
        System.out.println(String.join("", Collections.nCopies(40, "-")));
    }

    public void saveGuestInformation() throws Exception {
        guestDAO.saveGuestsListToFile();
        System.out.println("Guest information saved to file successfully");
    }

    private void handleExit() throws Exception {
        String confirm = DataInput.getString("Do you want to save changes before exiting? (Y/N): ", Constants.CONFIRM_PATTERN);
        if (confirm.equalsIgnoreCase("Y")) {
            saveGuestInformation();
        }
        System.out.println("Goodbye!");
        System.exit(0);
    }
}
