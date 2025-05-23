package data;

import core.entities.Room;
import core.interfaces.IRoom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RoomDAO implements IRoom {

    private final List<Room> ROOM_LIST = new ArrayList<>();
    private final FileManager FILE_MANAGER;

    public RoomDAO(String fileName) throws Exception {
        this.FILE_MANAGER = new FileManager(fileName);
        initData();
    }

    public final void initData() throws Exception {
        String roomID, roomName, roomType, furnitureDescription;
        double dailyRate;
        int capacity;

        ROOM_LIST.clear();
        List<String> roomData = FILE_MANAGER.readDataFromFile();

        if (roomData.isEmpty()) {
            throw new Exception("File is empty");
        }

        for (int i = 0; i < roomData.size(); i++) {
            String e = roomData.get(i);
            try {
                List<String> roomS = Arrays.asList(e.split(";"));
                if (roomS.size() < 6) {
                    throw new IllegalArgumentException("Missing field data.");
                }

                roomID = roomS.get(0).trim();
                roomName = roomS.get(1).trim();
                roomType = roomS.get(2).trim();
                dailyRate = Double.parseDouble(roomS.get(3).trim());
                capacity = Integer.parseInt(roomS.get(4).trim());
                furnitureDescription = roomS.get(5).trim();

                // Check for duplicate room ID
                boolean isDuplicate = getRoomById(roomID) != null;
                if (isDuplicate) {
                    throw new IllegalArgumentException("Duplicate room ID found: " + roomID);
                }

                Room room = new Room(roomID, roomName, roomType, dailyRate, capacity, furnitureDescription);
                ROOM_LIST.add(room);
            } catch (Exception ex) {
            }
        }

        if (ROOM_LIST.isEmpty()) {
            throw new Exception("No valid room was loaded.");
        }

    }

    @Override
    public final void loadData() throws Exception {
        String roomID, roomName, roomType, furnitureDescription;
        double dailyRate;
        int capacity;

        ROOM_LIST.clear();
        List<String> roomData = FILE_MANAGER.readDataFromFile();

        if (roomData.isEmpty()) {
            throw new Exception("File is empty");
        }

        int successCount = 0;
        int errorCount = 0;
        for (int i = 0; i < roomData.size(); i++) {
            String e = roomData.get(i);
            try {
                List<String> roomS = Arrays.asList(e.split(";"));
                if (roomS.size() < 6) {
                    throw new IllegalArgumentException("Missing field data.");
                }

                roomID = roomS.get(0).trim();
                roomName = roomS.get(1).trim();
                roomType = roomS.get(2).trim();
                dailyRate = Double.parseDouble(roomS.get(3).trim());
                capacity = Integer.parseInt(roomS.get(4).trim());
                furnitureDescription = roomS.get(5).trim();

                // Check for duplicate room ID
                boolean isDuplicate = getRoomById(roomID) != null;
                if (isDuplicate) {
                    throw new IllegalArgumentException("Duplicate room ID found: " + roomID);
                }

                Room room = new Room(roomID, roomName, roomType, dailyRate, capacity, furnitureDescription);
                ROOM_LIST.add(room);
                successCount++;
            } catch (Exception ex) {
                errorCount++;
            }
        }

        System.out.println(successCount + " rooms successfully loaded.");
        System.out.println(errorCount + " entries failed.");
    }

    @Override
    public List<Room> getRooms() {
        Collections.sort(
                ROOM_LIST,
                (e1, e2) -> e1.getRoomID().compareTo(e2.getRoomID())
        );
        return ROOM_LIST;
    }

    @Override
    public Room getRoomById(String id) throws Exception {
        if (ROOM_LIST.isEmpty()) {
            getRooms();//return null
        }
        Room room = ROOM_LIST
                .stream()
                .filter(
                        e -> e.getRoomID().equals(id)
                )
                .findFirst()
                .orElse(null);
        return room;
    }

}
