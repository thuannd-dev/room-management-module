package core.interfaces;

import core.entities.Guest;
import java.util.List;

public interface IGuest {

    List<Guest> getGuests() throws Exception;

    Guest getGuestById(String id) throws Exception;

    void addGuest(Guest guest) throws Exception;

    void updateGuest(Guest guest) throws Exception;

    void removeGuest(Guest guest) throws Exception;

    void saveGuestsListToFile() throws Exception;
}
