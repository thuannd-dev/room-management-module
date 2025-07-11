package core.entities;

import common.env.Constants;
import common.tools.DataValidate;

public final class Room {

    private String roomID;
    private String roomName;
    private String roomType;
    private double dailyRate;
    private int capacity;
    private String furnitureDescription;

    public Room(String roomID, String roomName, String roomType, double dailyRate, int capacity, String furnitureDescription) throws Exception {
        setRoomID(roomID);
        setRoomName(roomName);
        setRoomType(roomType);
        setDailyRate(dailyRate);
        setCapacity(capacity);
        setFurnitureDescription(furnitureDescription);
    }

    public String getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getFurnitureDescription() {
        return furnitureDescription;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setDailyRate(double dailyRate) throws Exception {
        if (!DataValidate.checkStringWithFormat(String.valueOf(dailyRate), Constants.POSITIVE_NUMBER_PATTERN)) {
            throw new Exception("Daily rate invalid.");
        }
        this.dailyRate = dailyRate;
    }

    public void setCapacity(int capacity) throws Exception {
        if (!DataValidate.checkStringWithFormat(String.valueOf(capacity), Constants.POSITIVE_NUMBER_PATTERN)) {
            throw new Exception("Capacity invalid.");
        }
        this.capacity = capacity;
    }

    public void setFurnitureDescription(String furnitureDescription) {
        this.furnitureDescription = furnitureDescription;
    }

    @Override
    public String toString() {
        return "Room [roomID=" + roomID + ", roomName=" + roomName + ", roomType=" + roomType + ", dailyRate=" + dailyRate
                + ", capacity=" + capacity + ", furnitureDescription=" + furnitureDescription + "]";
    }
}
