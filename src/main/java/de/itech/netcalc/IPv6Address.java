package de.itech.netcalc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class IPv6Address extends IPAddress {
    private int[] segments;

    public int[] getSegments(){
        return segments.clone();
    }

    public IPv6Address(int[] segments){
        if(segments.length != 8) throw new IllegalArgumentException("Wrong number of segments");
        if(Arrays.stream(segments).anyMatch(s -> s < 0 || s > 65535)) throw new IllegalArgumentException("Invalid segment");
        this.segments = segments.clone();
    }

    public long getInterfaceId() {
        return ((long)segments[4] << 24) + ((long)segments[5] << 16) + ((long)segments[6] << 8) + ((long)segments[7]);
    }

    public long getNetworkId() {
        return ((long)segments[0] << 24) + ((long)segments[1] << 16) + ((long)segments[2] << 8) + ((long)segments[3]);
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(Boolean shorthand)
    {
        if(shorthand) {
            ArrayList<String> values = new ArrayList<>();
            boolean shorted = false;
            for(int s : segments) {
                if(s == 0 && !shorted)
                {
                    values.add("");
                    shorted = true;
                }
                else
                    values.add(Integer.toString(s, 16));
            }
            return String.join(":",values).toLowerCase();
        }
        else{
            ArrayList<String> values = new ArrayList<>();
            for(int s : segments) {
                //values.add(Integer.toString(s, 16));
                values.add(String.format("%04X", s & 0xFFFF));
            }
            return String.join(":",values).toLowerCase();
        }
    }

    public static IPv6Address getRandomInterfaceAddress(){
        Random r = new Random();
        int[] newSegments = new int[8];

        newSegments[4] = r.nextInt(65025);
        newSegments[5] = r.nextInt(65025) | 0x0F;
        newSegments[6] = r.nextInt(255) | 0xE0;
        newSegments[7] = r.nextInt(65025);

        return new IPv6Address(newSegments);
    }

    @Override
    protected IPv6Address clone() {
        return new IPv6Address(segments);
    }
}