package edu.illinois.strollsafe;

/**
 * @author MichaelGoldstein
 */
public class GlobalConfig {

    public static final String[] SUPPORTED_ZIP_CODES = {"61820", "61801", "61821", "62401"}; // TODO remove effingham
    public static final String EMERGENCY_NUMBER = "tel:13017515134";
    public static final long RELEASE_TIMER_DURATION = 1000L;
    public static final long LOCK_TIMER_DURATION = 20000L;
    public static final boolean USE_SMS_IF_AVAILABLE = false;
    public static final String PREFS_NAME = "StrollSafePrefs";

}
