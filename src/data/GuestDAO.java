package data;

import core.entities.Guest;
import core.interfaces.IGuest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuestDAO implements IGuest {

    private final List<Guest> GUEST_LIST = new ArrayList<>();
    private final FileManager FILE_MANAGER;

    public GuestDAO(String fileName) throws Exception {
        this.FILE_MANAGER = new FileManager(fileName);
        loadData();
    }

    public final void loadData() throws Exception {
        String guestID, fullName, gender, phoneNumber, desiredRoomID, nameOfCoTenant;
        LocalDate birthdate, startDate;
        int numberOfRentalDays;
        try {
            GUEST_LIST.clear();
            List<String> guestsData = FILE_MANAGER.readDataFromFile();
            for (String e : guestsData) {
                List<String> fieldsOfGuest = Arrays.asList(e.split(","));
                guestID = fieldsOfGuest.get(0).trim();
                fullName = fieldsOfGuest.get(1).trim();
                birthdate = LocalDate.parse(fieldsOfGuest.get(2).trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                gender = fieldsOfGuest.get(3).trim();
                phoneNumber = fieldsOfGuest.get(4).trim();
                desiredRoomID = fieldsOfGuest.get(5).trim();
                numberOfRentalDays = Integer.parseInt(fieldsOfGuest.get(6).trim());
                startDate = LocalDate.parse(fieldsOfGuest.get(7).trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                nameOfCoTenant = fieldsOfGuest.get(8).trim();
                Guest guest = new Guest(guestID, fullName, birthdate, gender, phoneNumber, desiredRoomID, numberOfRentalDays, startDate, nameOfCoTenant);
                GUEST_LIST.add(guest);
                if (GUEST_LIST.isEmpty()) {
                    throw new Exception("Guest list is empty.");
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public List<Guest> getGuests() throws Exception {
        GUEST_LIST.sort((e1, e2) -> e1.getFullName().compareToIgnoreCase(e2.getFullName()));
        return GUEST_LIST;
    }

    @Override
    public Guest getGuestById(String id) throws Exception {
        Guest guest = GUEST_LIST.stream()
                .filter(e -> e.getNationalID()
                .equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
        return guest;
    }

    @Override
    public void addGuest(Guest guest) throws Exception {
        GUEST_LIST.add(guest);
    }

    @Override
    public void updateGuest(Guest guest) throws Exception {
        Guest gus = getGuestById(guest.getNationalID());
        if (gus != null) {
            gus.setFullName(gus.getFullName());
            gus.setBirthdate(gus.getBirthdate());
            gus.setGender(gus.getGender());
            gus.setPhoneNumber(gus.getPhoneNumber());
            gus.setDesiredRoomID(gus.getDesiredRoomID());
            gus.setNumberOfRentalDays(gus.getNumberOfRentalDays());
            gus.setStartDate(gus.getStartDate());
            gus.setNameOfCoTenant(gus.getNameOfCoTenant());
        }
    }

    @Override
    public void removeGuest(Guest guest) throws Exception {
        Guest gus = getGuestById(guest.getNationalID());
        if (gus != null) {
            GUEST_LIST.remove(gus);
        }
    }

    @Override
    public void saveGuestsListToFile() throws Exception {
        List<String> stringObject = GUEST_LIST.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        String data = String.join("\n", stringObject);
        FILE_MANAGER.saveDataToFile(data);
    }

}
