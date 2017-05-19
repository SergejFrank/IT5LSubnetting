package de.itech.netcalc;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class IPv6Address {
    private static final String validV6Regex =
            "(::|(([a-fA-F0-9]{1,4}):){7}(([a-fA-F0-9]{1,4}))|(:(:([a-fA-F0-9]{1,4})){1,6})|((([a-fA-F0-9]{1,4}):){1,6}:)|((([a-fA-F0-9]{1,4}):)(:([a-fA-F0-9]{1,4})){1,6})|((([a-fA-F0-9]{1,4}):){2}(:([a-fA-F0-9]{1,4})){1,5})|((([a-fA-F0-9]{1,4}):){3}(:([a-fA-F0-9]{1,4})){1,4})|((([a-fA-F0-9]{1,4}):){4}(:([a-fA-F0-9]{1,4})){1,3})|((([a-fA-F0-9]{1,4}):){5}(:([a-fA-F0-9]{1,4})){1,2}))";

    public int[] segments;

    public int[] getSegments(){
        return segments.clone();
    }

    public IPv6Address(int[] segments){
        if(segments.length != 8) throw new IllegalArgumentException("Wrong number of segments");
        if(Arrays.stream(segments).anyMatch(s -> s < 0 || s > 65535)) throw new IllegalArgumentException("Invalid segment");
        this.segments = segments;
    }

    public static Boolean isValid(String value) {
        return value != null && value.matches(validV6Regex);
    }

    public static IPv6Address parse(String value) {
        try {

            if (!isValid(value))
                throw new IllegalArgumentException("'value' is not a valid IPv6 Address.");
            ArrayList<Integer> parsedSegments = new ArrayList<>();

            //String[] splitted = value.split("([.:])");
            String[] splitted = value.split("((?<=[:.])|(?=[:.]))");
            int missingSegments = 0;
            if (splitted.length != 15)
                missingSegments = (15 - splitted.length - 1) / 2;

            String currSegment;
            for (int i = 0; i < splitted.length; i++) {
                currSegment = splitted[i];
                if (isDelimiter(currSegment) && i != (splitted.length - 1) && isDelimiter(splitted[i+1])) { //Evtl nächstes Zeichen noch ein Trennzeichen?
                    i++;
                    for (int j = 0; j <= missingSegments; j++) {
                        parsedSegments.add(0);
                    }
                } else if (isDelimiter(currSegment) && i == (splitted.length - 1)) //Spezialfall letzter Block ist Trennzeichen
                    parsedSegments.add(0);
                else if(!isDelimiter(currSegment)) //normales Segment
                {
                    parsedSegments.add(Integer.parseInt(currSegment, 16));
                }
            }
            return new IPv6Address(parsedSegments.stream().mapToInt(i -> i).toArray());
        }
        catch(Exception e) {
            System.out.println("Can not parse IPv6Address (" + e.toString() + ")");
            throw new IllegalArgumentException("'value' is not a valid IPv6 Address.");
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int s : segments) {
            builder.append(Integer.toString(s, 16));
        }
        return builder.toString();
    }

    private static boolean isDelimiter(String value) {
        return Objects.equals(value, ":") || Objects.equals(value, ".");
    }
}