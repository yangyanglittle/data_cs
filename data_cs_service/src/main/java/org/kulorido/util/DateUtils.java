package org.kulorido.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import java.util.Date;

@Slf4j
public class DateUtils {

    public static String formatDate(Date date, String pattern) {
        return DateFormatUtils.format(org.apache.commons.lang3.time.DateUtils.addHours(date, 8), pattern);
    }
}
