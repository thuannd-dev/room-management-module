package common.tools;

public class DataUtils {

    public static String toTitleCase(String str) {
        str = str.toLowerCase().trim().replaceAll("\\s+", " ");
        String[] words = str.split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        }
        return String.join(" ", words).trim();
    }
}
