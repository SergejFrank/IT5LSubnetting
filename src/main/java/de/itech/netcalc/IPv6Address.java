package de.itech.netcalc;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class IPv6Address {
    public int[] segments;

    public int[] getSegments(){
        return segments.clone();
    }

    public IPv6Address(int[] segments){
        if(segments.length != 8) throw new IllegalArgumentException("Wrong number of segments");
        if(Arrays.stream(segments).anyMatch(s -> s < 0 || s > 65535)) throw new IllegalArgumentException("Invalid segment");
        this.segments = segments.clone();
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

    @Override
    protected IPv6Address clone() {
        return new IPv6Address(segments);
    }
}