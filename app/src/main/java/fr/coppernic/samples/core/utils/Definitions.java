package fr.coppernic.samples.core.utils;

import fr.coppernic.sdk.utils.helpers.OsHelper;

/**
 * <p>Created on 15/11/17
 *
 * @author bastien
 */
public final class Definitions {
    public static final String KEY_PERIPHERAL = "peripheral";

    public static String getDeviceName() {
        if (OsHelper.isConeV1()) {
            return "C-One";
        } else if (OsHelper.isConeV2()) {
            return "C-One²";
        } else if (OsHelper.isC5()) {
            return "C-five";
        } else if (OsHelper.isC8()) {
            return "C-eight";
        } else if (OsHelper.isIdPlatform()) {
            return "ID Platform";
        } else if (OsHelper.isTomTomBridge()) {
            return "Tom Tom ";
        } else {
            return "device";
        }
    }

    private Definitions() {
    }
}
