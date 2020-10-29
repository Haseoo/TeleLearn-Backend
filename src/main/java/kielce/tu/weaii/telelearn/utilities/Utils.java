package kielce.tu.weaii.telelearn.utilities;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {
    public static boolean isStringNullOrEmpty(String string) {
        return string == null || string.equals("");
    }
}
