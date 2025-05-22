package core.interfaces;

import core.entities.Room;
import java.util.List;

public interface IRoom {

    public void loadData() throws Exception;

    public List<Room> getRooms();

    public Room getRoomById(String id) throws Exception;

}
