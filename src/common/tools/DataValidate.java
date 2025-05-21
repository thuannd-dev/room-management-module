package common.tools;

import java.util.List;

public class DataValidate {

    public static boolean checkStringEmpty(String value) {
        return value.isEmpty();
    }

    public static boolean checkStringWithFormat(String value, String pattern) {
        return value.matches(pattern);
    }

    public static boolean checkMatchCode(String value, List<String> pattern) {
        return pattern.contains(value);
    }
}
