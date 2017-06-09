package de.itech.netcalc;

import de.itech.netcalc.net.Format;

/**
 * Provides global configuration.
 */
public class Config {
    /**
     * Backing field for the IPv4Notation property.
     */
    private static Format.IPv4Format ipv4Notation = Format.IPv4Format.DECIMAL;

    /**
     * Backing field for the IPv6Notation property.
     */
    private static Format.IPv6Format ipv6Notation = Format.IPv6Format.SHORTHAND;

    /**
     * Gets the IPv4 notation setting.
     * @return the IPv4 notation
     */
    public static Format.IPv4Format getIpv4Notation() {
        return ipv4Notation;
    }

    /**
     * Sets the IPv4 notation settings.
     * @param notation the notation to set
     */
    public static void setIpv4Notation(Format.IPv4Format notation) {
        ipv4Notation = notation;
    }

    /**
     * Gets the IPv6 notation setting.
     * @return the IPv6 notation
     */
    public static Format.IPv6Format getIpv6Notation() {
        return ipv6Notation;
    }

    /**
     * Sets the IPv6 notation settings.
     * @param notation the notation to set
     */
    public static void setIpv6Notation(Format.IPv6Format notation) {
        ipv6Notation = notation;
    }
}
