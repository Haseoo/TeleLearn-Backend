package kielce.tu.weaii.telelearn.utilities;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class Constants {
    public static final DateTimeFormatter DATE_FORMATTER_FOR_MAP_KEY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final int WEEK_END_RANGE_DAYS = 8;
}
