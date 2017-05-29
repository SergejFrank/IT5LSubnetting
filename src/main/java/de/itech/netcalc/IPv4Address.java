package de.itech.netcalc;

public class IPv4Address {

    private int oct1;
    private int oct2;
    private int oct3;
    private int oct4;

    IPv4Address(int oct1, int oct2, int oct3, int oct4) {
        setOct1(oct1);
        setOct2(oct2);
        setOct3(oct3);
        setOct4(oct4);
    }

    IPv4Address(int val){
        setOct1(val >> 24 & 0xFF);
        setOct2(val >> 16 & 0xFF);
        setOct3(val >> 8 & 0xFF);
        setOct4(val & 0xFF);
    }

    private int getOct1() {
        return oct1;
    }

    private void setOct1(int oct1) {
        checkRange(oct1);
        this.oct1 = oct1;
    }

    private int getOct2() {
        return oct2;
    }

    private void setOct2(int oct2) {
        checkRange(oct2);
        this.oct2 = oct2;
    }

    private int getOct3() {
        return oct3;
    }

    private void setOct3(int oct3) {
        checkRange(oct3);
        this.oct3 = oct3;
    }

    private int getOct4() {
        return oct4;
    }

    private void setOct4(int oct4) {
        checkRange(oct4);
        this.oct4 = oct4;
    }

    int getValue(){
        return (oct1 << 24) + (oct2 << 16) + (oct3 << 8) + oct4;
    }

    long getLValue() {
        return ((long)oct1 << 24) + ((long)oct2 << 16) + ((long)oct3 << 8) + ((long)oct4);
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

    boolean isGreaterThan(IPv4Address ipAddress) {
        return getLValue() > ipAddress.getLValue();
    }

    public String toBinary(String seperator){
        String[] elems = toBinary().split("(?<=\\G.{8})");
        return String.join(seperator, elems);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IPv4Address ipAddress = (IPv4Address) o;

        if (oct1 != ipAddress.oct1) return false;
        if (oct2 != ipAddress.oct2) return false;
        if (oct3 != ipAddress.oct3) return false;
        return oct4 == ipAddress.oct4;
    }

    @Override
    public int hashCode() {
        int result = oct1;
        result = 31 * result + oct2;
        result = 31 * result + oct3;
        result = 31 * result + oct4;
        return result;
    }




}
