package controllers;

import common.env.Constants;
import core.interfaces.IRoom;
import data.RoomDAO;
import view.Menu;

public class Program {

    public static void main(String[] args) {
        try {
            while (true) {
                IRoom roomService = new RoomDAO(Constants.ACTIVE_ROOM_FILE);
                Menu.manageRoom(roomService);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
