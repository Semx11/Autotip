package me.semx11.autotip.util;

public class TimeUtil {

    private final static long ONE_SECOND = 1000;
    private final static long ONE_MINUTE = ONE_SECOND * 60;
    private final static long ONE_HOUR = ONE_MINUTE * 60;
    private final static long ONE_DAY = ONE_HOUR * 24;

    public static String formatMillis(long millis) {
        StringBuilder sb = new StringBuilder();
        long temp;
        if (millis >= ONE_SECOND) {
            temp = millis / ONE_DAY;
            if (temp > 0) {
                sb.append(temp).append(" day").append(temp > 1 ? "s" : "");
                return sb.toString() + " ago";
            }

            temp = millis / ONE_HOUR;
            if (temp > 0) {
                sb.append(temp).append(" hour").append(temp > 1 ? "s" : "");
                return sb.toString() + " ago";
            }

            temp = millis / ONE_MINUTE;
            if (temp > 0) {
                sb.append(temp).append(" minute").append(temp > 1 ? "s" : "");
                return sb.toString() + " ago";
            }

            temp = millis / ONE_SECOND;
            if (temp > 0) {
                sb.append(temp).append(" second").append(temp > 1 ? "s" : "");
            }
            return sb.toString() + " ago";
        } else {
            return "just now";
        }
    }
}
