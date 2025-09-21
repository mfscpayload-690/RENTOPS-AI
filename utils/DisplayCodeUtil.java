package utils;

public class DisplayCodeUtil {

    /**
     * Generates a stable 4-character code from a name: first two alphabetic
     * characters uppercased + two digits derived from hash. If name has fewer
     * than two letters, pads with 'X'.
     */
    public static String codeFromName(String name) {
        if (name == null) {
            name = "";
        }
        String lettersOnly = name.replaceAll("[^A-Za-z]", "");
        String prefix;
        if (lettersOnly.length() >= 2) {
            prefix = lettersOnly.substring(0, 2).toUpperCase();
        } else if (lettersOnly.length() == 1) {
            prefix = (lettersOnly.substring(0, 1) + "X").toUpperCase();
        } else {
            prefix = "XX";
        }
        int twoDigits = Math.abs(name.hashCode()) % 90 + 10; // 10..99 stable per name
        return prefix + twoDigits;
    }
}
