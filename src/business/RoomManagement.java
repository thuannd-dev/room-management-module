package business;

import common.env.Constants;
import common.tools.DataInput;
import common.tools.DataUtils;
import core.entities.Guest;
import core.entities.Room;
import core.interfaces.IGuest;
import core.interfaces.IRoom;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import view.Menu;

public class RoomManagement {

    IRoom roomDAO;
    IGuest guestDAO;

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
                        + "|6.Delete Guest Reservation Before Arrival "
                        + "|7.List Vacant Rooms"
                        + "|8.Monthly Revenue Report"
                        + "|9.Revenue Report by Room Type"
                        + "|10.Save Guest Information"
                        + "|11.Exit |Select:");
                int choice = Menu.getUserChoice();
                switch (choice) {
                    case 1 -> {
                        importRoomData();
                    }
                    case 2 -> {
                        printRoomList(roomDAO.getRooms());
                    }
                    case 3 -> {
                        addGuest();
                    }
                    case 4 -> {
                        updateGuestStayInformation();
                    }
                    case 5 -> {
                        searchGuestByNationalID();
                    }
                    case 6 -> {
                        deleteGuestReservationBeforeArrival();
                    }
                    case 7 -> {
                        printListVacantRooms();
                    }
                    case 8 -> {
                        printMonthlyRevenueReport();
                    }
                    case 9 -> {
                        printRevenueReportByRoomType();
                    }
                    case 10 -> {
                        saveGuestInformation();
                    }
                    case 11 -> {
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

    public boolean isRoomAvailable(String roomId, LocalDate startDate, int numberOfDays) throws Exception {
        // Check if room exists
        Room room = roomDAO.getRoomById(roomId);
        if (room == null) {
            throw new Exception("Room not found!");
        }

        // Get all guests
        List<Guest> guests = guestDAO.getGuests();

        // Calculate end date for the requested stay
        LocalDate endDate = startDate.plusDays(numberOfDays - 1);

        // Check if any guest has overlapping stay
        for (Guest guest : guests) {
            if (guest.getDesiredRoomID().equals(roomId)) {
                LocalDate guestEndDate = guest.getStartDate().plusDays(guest.getNumberOfRentalDays() - 1);

                // Check for date overlap
                if (!(endDate.isBefore(guest.getStartDate()) || startDate.isAfter(guestEndDate))) {
                    return false; // Room is not available - there is an overlap
                }
            }
        }

        return true; // Room is available
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
        String nameOfCoTenant = DataInput.getString("Enter name of co-tenant(Press enter if none):", Constants.GUEST_NAME_CO_TENANT_PATTERN);
        return new Guest(guestID, fullName, birthdate, gender, phoneNumber, desiredRoomID, numberOfRentalDays, startDate, nameOfCoTenant);
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

    public void saveGuestInformation() throws Exception {
        guestDAO.saveGuestsListToFile();
        System.out.println("Guest information saved to file successfully");
    }

    public void printGuestInformation(Guest guest) throws Exception {
        if (guest == null) {
            throw new Exception("Printing guest information failed");
        }
        if (roomDAO.getRoomById(guest.getDesiredRoomID()) == null) {
            throw new Exception("Please load room data first!");
        }
        System.out.println(String.join("", Collections.nCopies(115, "-")));
        System.out.println("Guest information [National ID: " + guest.getNationalID() + "]");
        System.out.println(String.join("", Collections.nCopies(115, "-")));
        System.out.println("Full name    : " + guest.getFullName());
        System.out.println("Phone number : " + guest.getPhoneNumber());
        System.out.println("Birth day    : " + guest.getBirthdate());
        System.out.println("Gender       : " + guest.getGender());
        System.out.println(String.join("", Collections.nCopies(115, "-")));
        System.out.println("Rental room  : " + guest.getDesiredRoomID());
        System.out.println("Check in     : " + guest.getStartDate());
        System.out.println("Rental days  : " + guest.getNumberOfRentalDays());
        System.out.println("Check out    : " + guest.getStartDate().plusDays(guest.getNumberOfRentalDays()));
        System.out.println(String.join("", Collections.nCopies(115, "-")));
        System.out.println("Room inforamtion:");
        System.out.println("+ ID       : " + guest.getDesiredRoomID());
        System.out.println("+ Room     : " + roomDAO.getRoomById(guest.getDesiredRoomID()).getRoomName());
        System.out.println("+ Type     : " + roomDAO.getRoomById(guest.getDesiredRoomID()).getRoomType());
        System.out.println("+ Daly rate: " + roomDAO.getRoomById(guest.getDesiredRoomID()).getDailyRate());
        System.out.println("+ Capacity : " + roomDAO.getRoomById(guest.getDesiredRoomID()).getCapacity());
        System.out.println("+ Funiture : " + roomDAO.getRoomById(guest.getDesiredRoomID()).getFurnitureDescription());
        System.out.println(String.join("", Collections.nCopies(115, "-")));
    }

    public void printGuestInformationToUpdate(Guest guest) throws Exception {
        System.out.println(String.join("", Collections.nCopies(115, "-")));
        System.out.println("Guest information [National ID: " + guest.getNationalID() + "]");
        System.out.println(String.join("", Collections.nCopies(115, "-")));
        System.out.println("Full name        : " + guest.getFullName());
        System.out.println("Phone number     : " + guest.getPhoneNumber());
        System.out.println("Birth day        : " + guest.getBirthdate());
        System.out.println("Gender           : " + guest.getGender());
        System.out.println(String.join("", Collections.nCopies(115, "-")));
        System.out.println("Rental room      : " + guest.getDesiredRoomID());
        System.out.println("Check in         : " + guest.getStartDate());
        System.out.println("Rental days      : " + guest.getNumberOfRentalDays());
        System.out.println("Check out        : " + guest.getStartDate().plusDays(guest.getNumberOfRentalDays()));
        System.out.println("Name of co-tenant: " + guest.getNameOfCoTenant());
        System.out.println(String.join("", Collections.nCopies(115, "-")));
    }

    public void searchGuestByNationalID() throws Exception {
        String guestID = DataInput.getString("Enter guest ID:", Constants.NATIONAL_ID_PATTERN);
        Guest guest = guestDAO.getGuestById(guestID);
        if (guest == null) {
            System.out.println(String.join("", Collections.nCopies(115, "-")));
            System.out.println("No guest found with the requested ID '" + guestID + "'");
            return;
        }
        printGuestInformation(guest);
    }

    public void updateGuestStayInformation() throws Exception {
        String guestID = DataInput.getString("Enter guest ID:", Constants.NATIONAL_ID_PATTERN);
        Guest guest = guestDAO.getGuestById(guestID);
        if (guest == null) {
            System.out.println(String.join("", Collections.nCopies(115, "-")));
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
        String nameOfCoTenant = DataInput.getString("Enter name of co-tenant(Press enter if none):", Constants.GUEST_NAME_CO_TENANT_PATTERN);

        if (!desiredRoomID.equals(guest.getDesiredRoomID()) && roomDAO.getRoomById(desiredRoomID) == null) {
            throw new Exception("Room not found!");
        }
        if (!isRoomAvailable(desiredRoomID, startDate, numberOfRentalDays)) {
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

    public void deleteGuestReservationBeforeArrival() throws Exception {
        String guestID = DataInput.getString("Enter guest ID:", Constants.NATIONAL_ID_PATTERN);
        Guest guest = guestDAO.getGuestById(guestID);
        if (guest == null) {
            System.out.println(String.join("", Collections.nCopies(115, "-")));
            System.out.println("Booking details for ID '" + guestID + "' could not be found.");
            return;
        }
        if (guest.getStartDate().isBefore(LocalDate.now()) || guest.getStartDate().isEqual(LocalDate.now())) {
            System.out.println(String.join("", Collections.nCopies(115, "-")));
            System.out.println("The room booking for this guest cannot be cancelled!");
            return;
        }
        printGuestInformation(guest);
        System.out.println("Are you sure you want to cancel the booking for ID '" + guestID + "'? (Y/N):");
        String confirm = DataInput.getString("Enter your choice:", Constants.CONFIRM_PATTERN);
        if (confirm.equalsIgnoreCase("Y")) {
            guestDAO.removeGuest(guest);
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
        List<Room> vacantRoomList = new ArrayList<>();
        for (Room room : roomList) {
            if (isRoomAvailable(room.getRoomID(), LocalDate.now(), 1)) {
                vacantRoomList.add(room);
            }
        }
        if (vacantRoomList.isEmpty()) {
            System.out.println("All rooms are currently rented out — no availability at the moment !.");
        } else {
            System.out.println("Available Room List");
            printRoomList(vacantRoomList);
        }

    }

    public double calculateTotalRevenueOfRoomType(String roomType) throws Exception {
        List<Guest> guestList = guestDAO.getGuests();
        double totalRevenue = 0;
        for (Guest guest : guestList) {
            Room room = roomDAO.getRoomById(guest.getDesiredRoomID());
            if (room != null && room.getRoomType().equalsIgnoreCase(roomType)) {
                totalRevenue += guest.getNumberOfRentalDays() * room.getDailyRate();
            }
        }
        return totalRevenue;
    }

    public double calculateMonthlyRevenueOfRoom(Room room, LocalDate month) throws Exception {
        List<Guest> guestList = guestDAO.getGuests();
        double totalRevenue = 0;
        for (Guest guest : guestList) {
            if (guest.getDesiredRoomID().equalsIgnoreCase(room.getRoomID()) && guest.getStartDate().getMonth().equals(month.getMonth()) && guest.getStartDate().getYear() == month.getYear()) {
                totalRevenue += guest.getNumberOfRentalDays() * room.getDailyRate();
            }
        }
        return totalRevenue;
    }

    public void printRevenueReportByRoomType() throws Exception {
        String roomType = DataInput.getString("Enter room type:", Constants.ROOM_TYPE_PATTERN);
        roomType = DataUtils.toTitleCase(roomType);
        double totalRevenue = calculateTotalRevenueOfRoomType(roomType);
        // Revenue Report by Room Type
        // ----------------------------
        //     Room type | Amount
        // ----------------------------
        //     Deluxe   | $450,050
        //    Standard | $90,000
        //   Suite    | $1,200,000
        //  Superior| $650,500
        DecimalFormat formatter = new DecimalFormat("#,###");
        System.out.format("     Revenue Report by Room Type\n");
        System.out.println(String.join("", Collections.nCopies(40, "-")));
        System.out.format("  %-15s | %12s%n", "Room type", "Amount");
        System.out.println(String.join("", Collections.nCopies(40, "-")));
        System.out.format("  %-15s | %15s%n", roomType, "$" + formatter.format(totalRevenue));
        System.out.println(String.join("", Collections.nCopies(40, "-")));
    }

    public void printMonthlyRevenueReport() throws Exception {

        LocalDate month = DataInput.getMonth("Enter month (mm/yyyy):");
        List<Room> roomList = roomDAO.getRooms();
        List<Room> monthlyRevenueRoomList = new ArrayList<>();
        for (Room room : roomList) {
            double totalRevenue = calculateMonthlyRevenueOfRoom(room, month);
            if (totalRevenue > 0) {
                monthlyRevenueRoomList.add(room);
            }
        }
        if (monthlyRevenueRoomList.isEmpty()) {
            System.out.println("There is no data on guests who have rented rooms");
            return;
        }
        System.out.format("Monthly Revenue Report - %s%n", month.format(DateTimeFormatter.ofPattern("MM/yyyy")));
        System.out.println(String.join("", Collections.nCopies(68, "-")));
        System.out.format("  %-6s | %-15s | %-10s | %10s | %10s%n", "RoomID", "Room Name", "Room type", "DailyRate", "Amount");
        System.out.println(String.join("", Collections.nCopies(68, "-")));
        for (Room room : monthlyRevenueRoomList) {
            System.out.format("  %-6s | %-15s | %-10s | %10s | %10s%n", room.getRoomID(), room.getRoomName(), room.getRoomType(), room.getDailyRate(), calculateMonthlyRevenueOfRoom(room, month));
        }
        System.out.println(String.join("", Collections.nCopies(68, "-")));
    }
}
