package com.miaosu.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xxb on 2017/5/13.
 */
public class DateUtil {
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static DateFormat normal = new SimpleDateFormat("yyyyMMddHHmmss");
    public static String formatDate(Date dt) {
        /*StringBuffer UTCTimeBuffer = new StringBuffer();
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance() ;
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));*/
        return df.format(dt);
    }

    public static Date parseDate(String dt) {
        if (StringUtils.isBlank(dt)) {
            return null;
        }
        try {
            return df.parse(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatNormalDate(Date dt) {
        return normal.format(dt);
    }


}
