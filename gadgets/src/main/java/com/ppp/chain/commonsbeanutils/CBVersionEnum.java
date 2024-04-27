package com.ppp.chain.commonsbeanutils;

import com.ppp.Printer;

/**
 * @author Whoopsunix
 */
public enum CBVersionEnum {
    // -2044202215314119608
    Default("default", 0L),
    V_1_8_3("1.8.3", -3490850999041592962L),
    V_1_6("1.6", 2573799559215537819L),
    V_1_5("1.5", 5123381023979609048L),
    ;

    private final String version;
    private final long serialVersionUID;

    CBVersionEnum(String version, long serialVersionUID) {
        this.version = version;
        this.serialVersionUID = serialVersionUID;
    }

    public String getVersion() {
        return version;
    }

    public long getSerialVersionUID() {
        return serialVersionUID;
    }

    public static CBVersionEnum getCBVersion(String version) {
        for (CBVersionEnum cbVersionEnum : CBVersionEnum.values()) {
            if (cbVersionEnum.getVersion().equals(version)) {
                return cbVersionEnum;
            }
        }
        Printer.warn("Use default cb version");
        return Default;
    }
}
