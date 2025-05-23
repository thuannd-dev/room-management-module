package common.env;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final String NATIONAL_ID_PATTERN = "^\\d{12}$";
    public static final String GUEST_NAME_PATTERN = "^[A-Za-z|\\s]{2,25}$";
    public static final String GUEST_PHONE_PATTERN = "^0(3[2-9]|5[689]|7[06789]|8[0-689]|9[0-46-9])[0-9]{7}$";
    public static final String GUEST_EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String ROOM_ID_PATTERN = "^[A-Za-z][0-9]{0,4}$";
    public static final String ROOM_NAME_PATTERN = "^[A-Za-z0-9\\s]{2,25}$";
    public static final String ROOM_TYPE_PATTERN = "^(Single|Double|Family)$";
    public static final String FURNITURE_DESCRIPTION_PATTERN = "^[A-Za-z0-9\\s]{2,25}$";
    public static final String POSITIVE_NUMBER_PATTERN = "^[+]?([1-9][0-9]*(?:\\.[0-9]*)?|0*\\.0*[1-9][0-9]*)(?:[eE][+-][0-9]+)?$";
    public static final String ACTIVE_ROOM_FILE = "./src/files/Active_Room_List.txt";
    public static final List<String> FEAST_CODE_LIST = new ArrayList<>();
}
