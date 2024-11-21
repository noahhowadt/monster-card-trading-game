package at.technikum.application.mctg.util;


import java.util.Arrays;

public class Utils {
    public static String[] getPathArray(String path) {
        return Arrays.stream(path.split("/"))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }
}
