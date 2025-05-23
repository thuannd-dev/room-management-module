package controllers;

import common.env.Constants;
import core.interfaces.IGuest;
import core.interfaces.IRoom;
import data.GuestDAO;
import data.RoomDAO;
import view.Menu;

public class Program {

    public static void main(String[] args) {
        try {
            while (true) {
                IRoom roomService = new RoomDAO(Constants.ACTIVE_ROOM_FILE);
                IGuest guestService = new GuestDAO(Constants.GUEST_FILE);
                Menu.manageRoom(roomService, guestService);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
