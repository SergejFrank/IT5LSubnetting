package de.itech.netcalc;

/**
 * Provides global configuration.
 */
public class Config {
    /**
     * Describes how an IPAddress is notated.
     */
    public enum IPNotation {
        BINARY, DECIMAL, HEXADECEMAL
    }

    /**
     * Backing field for the IPv4Notation property.
     */
    private static IPNotation ipv4Notation = IPNotation.DECIMAL;

    /**
     * Backing field for the IPv6Notation property.
     */
    private static IPNotation ipv6Notation = IPNotation.HEXADECEMAL;

    /**
     * Gets the IPv4 notation setting.
     * @return the IPv4 notation
     */
    public static IPNotation getIpv4Notation() {
        return ipv4Notation;
    }

    /**
     * Sets the IPv4 notation settings.
     * @param notation the notation to set
     */
    public static void setIpv4Notation(IPNotation notation) {
        ipv4Notation = notation;
    }

    /**
     * Gets the IPv6 notation setting.
     * @return the IPv6 notation
     */
    public static IPNotation getIpv6Notation() {
        return ipv6Notation;
    }

    /**
     * Sets the IPv6 notation settings.
     * @param notation the notation to set
     */
    public static void setIpv6Notation(IPNotation notation) {
        ipv6Notation = notation;
    }
}
