package de.itech.netcalc;

public class IPv4Address extends IPAddress {
    private int value;

    IPv4Address(int oct1, int oct2, int oct3, int oct4) {
        if(oct1 < 0 || oct1 > 255) throw new IllegalArgumentException("Value '" + oct1 + "' for oct1 is out of range.");
        if(oct2 < 0 || oct2 > 255) throw new IllegalArgumentException("Value '" + oct2 + "' for oct2 is out of range.");
        if(oct3 < 0 || oct3 > 255) throw new IllegalArgumentException("Value '" + oct3 + "' for oct3 is out of range.");
        if(oct4 < 0 || oct4 > 255) throw new IllegalArgumentException("Value '" + oct4 + "' for oct4 is out of range.");
        value =  (oct1 << 24) + (oct2 << 16) + (oct3 << 8) + oct4;
    }

    IPv4Address(int val){
        value = val;
    }

    private int getOct1() {
        return (value & 0xFF000000) >>> 24;
    }

    private int getOct2() {
        return (value & 0x00FF0000) >> 16;
    }

    private int getOct3() {
        return (value & 0x0000FF00) >> 8;
    }

    private int getOct4() {
        return value & 0x000000FF;
    }

    public int getValue() {
        return value;
    }

    public long getLValue() {
        return Integer.toUnsignedLong(value);
    }

    String toBinary(){
        String bin = "";
        String zeroPad = "00000000";
        String oct1Bin = Integer.toBinaryString(getOct1());
        String oct2Bin = Integer.toBinaryString(getOct2());
        String oct3Bin = Integer.toBinaryString(getOct3());
        String oct4Bin = Integer.toBinaryString(getOct4());

        bin += zeroPad.substring(oct1Bin.length()) + oct1Bin;
        bin += zeroPad.substring(oct2Bin.length()) + oct2Bin;
        bin += zeroPad.substring(oct3Bin.length()) + oct3Bin;
        bin += zeroPad.substring(oct4Bin.length()) + oct4Bin; 

        return bin;
    }

    public boolean isGreaterThan(IPv4Address ipAddress) {
        return getLValue() > ipAddress.getLValue();
    }

    public String toBinary(String seperator){
        String[] elements = toBinary().split("(?<=\\G.{8})");
        return String.join(seperator, elements);
    }

    private void checkRange(int val){
        if(val < 0 || val > 255)
            throw new IllegalArgumentException("Out of range: "+val);
    }

    @Override
    public String toString(){
        return getOct1()+"."+getOct2()+"."+getOct3()+"."+getOct4();
    }

    @Override
    protected IPv4Address clone() {
        return new IPv4Address(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IPv4Address that = (IPv4Address) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return value;
    }
}
