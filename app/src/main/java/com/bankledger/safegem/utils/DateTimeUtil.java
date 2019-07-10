/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bankledger.safegem.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {


    public static final String SHORT_DATE_TIME_FORMAT = "MM-dd HH:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT_NO_SECOND = "yyyy-MM-dd HH:mm";
    private static final String DATE_TIME_FORMAT_DCIM_FilENAME = "yyyyMMdd_HHmmss";
    private static final String SHORT_DATE_TIME_FORMAT0 = "yyyy-MM";
    private static final String DEFAULT_TIMEZONE = "GMT+0";

    public static final String getNameForDcim(long time) {
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                DATE_TIME_FORMAT_DCIM_FilENAME);
        return dateFormat.format(date);
    }


    public static final String getNameForFile(long time) {
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                DATE_TIME_FORMAT_DCIM_FilENAME);
        return dateFormat.format(date);
    }

    public static final String getDateTimeString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
        String result = df.format(date);
        return result;
    }

    public static final String getDateTimeString(long date) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
        String result = df.format(date);
        return result;
    }

    public static final String getShortDateTimeString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(SHORT_DATE_TIME_FORMAT);
        return df.format(date);

    }

    public static final Date getDateTimeForTimeZone(String str)
            throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
        return df.parse(str);
    }

    public static final Date getDateTimeForTimeZone(Long time) {
        Long sourceRelativelyGMT = time
                - TimeZone.getTimeZone(DEFAULT_TIMEZONE).getRawOffset();
        Long targetTime = sourceRelativelyGMT
                + TimeZone.getDefault().getRawOffset();
        Date date = new Date(targetTime);
        return date;
    }

    public static int getDayOfWeek(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getHour(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static final String getShortDateTimeString0(Long date) {
        SimpleDateFormat df = new SimpleDateFormat(SHORT_DATE_TIME_FORMAT0);
        return df.format(date);
    }

    public static final String getMontnDayHM(Long date) {
        SimpleDateFormat df = new SimpleDateFormat(SHORT_DATE_TIME_FORMAT);
        return df.format(date);
    }

    public static final String formatDate(String str) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
        Date date;
        try {
            date = df.parse(str);
        } catch (ParseException e) {
            return str;
        }
        df = new SimpleDateFormat(DATE_TIME_FORMAT_NO_SECOND);
        return df.format(date);
    }

    public static String transformTime(String mTime) {
        Date date;
        SimpleDateFormat mFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        try {
            date = mFormat.parse(mTime);
        } catch (ParseException e) {
            return mTime;
        }
        Date finalDate = null;
        TimeZone oldZone = TimeZone.getTimeZone("GMT+0");
        TimeZone newZone = TimeZone.getDefault();
        if (date != null) {
            int timeOffset = oldZone.getOffset(date.getTime())
                    - newZone.getOffset(date.getTime());
            finalDate = new Date(date.getTime() - timeOffset);
        }
        return mFormat.format(finalDate);
    }

    public static String timeText(long time) {
        SimpleDateFormat format = new SimpleDateFormat();
        // 获取今天零点的时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Date date = null;
        Date yearDate = null;
        try {
            format.applyPattern("yyyy-MM-dd");
            date = format.parse(year + "-" + month + "-" + day);

            format.applyPattern("yyyy");
            yearDate = format.parse(year + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (yearDate.getTime() > time) {
            format.applyPattern("yyyy年MM月dd日 HH:mm");
            return format.format(time);
        }

        // 两个时间相减
        long timeInterval = time - date.getTime();
        // 这里有一个Get："yyyy-MM-dd hh:mm:ss" hh写成HH就是24小时制，hh是12小时制
        if (timeInterval > 0) {
            // 凌晨 （00-06）
            final int lincheng = 21600000;
            // 早上 （06-12）
            final int morning = 43200000;
            // 中午 （12-14.30）
            final int noon = 52200000;
            // 下午 （14：30-18）
            final int afternoon = 64800000;
            // 傍晚 （18-23）
            final int bangwan = 82800000;
            // 深夜 (23 - 24)
            final int shengye = 86400000;
            if (timeInterval <= lincheng) {
                format.applyPattern("凌晨hh:mm");
            } else if (timeInterval <= morning) {
                format.applyPattern("早上hh:mm");
            } else if (timeInterval <= noon) {
                format.applyPattern("中午hh:mm");
            } else if (timeInterval <= afternoon) {
                format.applyPattern("下午hh:mm");
            } else if (timeInterval <= bangwan) {
                format.applyPattern("傍晚hh:mm");
            } else if (timeInterval <= shengye) {
                format.applyPattern("深夜hh:mm");
            }
        } else if (timeInterval > -86400000) {
            long absTimeInterval = Math.abs(timeInterval);
            // 这里顺序和上面相比倒过来了
            // 昨天 深夜 (23 - 24) 3,600,000
            final int shengye = 3600000;
            // 昨天 傍晚 （18-23）21,600,000
            final int bangwan = 21600000;
            // 昨天 下午 （14：30-18）34,200,000
            final int afternoon = 34200000;
            // 昨天 中午 （12-14.30）43,200,000
            final int noon = 43200000;
            // 昨天 早上 （06-12）64,800,000
            final int morning = 64800000;
            // 昨天 凌晨 （00-06）
            final int lincheng = 86400000;

            if (absTimeInterval <= shengye) {
                format.applyPattern("昨天 深夜hh:mm");
            } else if (absTimeInterval <= bangwan) {
                format.applyPattern("昨天 傍晚hh:mm");
            } else if (absTimeInterval <= afternoon) {
                format.applyPattern("昨天 下午hh:mm");
            } else if (absTimeInterval <= noon) {
                format.applyPattern("昨天 中午hh:mm");
            } else if (absTimeInterval <= morning) {
                format.applyPattern("昨天 早上hh:mm");
            } else if (absTimeInterval <= lincheng) {
                format.applyPattern("昨天 凌晨hh:mm");
            }
        } else if (timeInterval > -86400000 * 2) {
            long absTimeInterval = Math.abs(timeInterval);
            // 昨天 深夜 (23 - 24) 3,600,000
            final int shengye = 3600000 * 2;
            // 昨天 傍晚 （18-23）21,600,000
            final int bangwan = 21600000 * 2;
            // 昨天 下午 （14：30-18）34,200,000
            final int afternoon = 34200000 * 2;
            // 昨天 中午 （12-14.30）43,200,000
            final int noon = 43200000 * 2;
            // 昨天 早上 （06-12）64,800,000
            final int morning = 64800000 * 2;
            // 昨天 凌晨 （00-06）
            final int lincheng = 86400000 * 2;

            if (absTimeInterval <= shengye) {
                format.applyPattern("E 深夜hh:mm");
            } else if (absTimeInterval <= bangwan) {
                format.applyPattern("E 傍晚hh:mm");
            } else if (absTimeInterval <= afternoon) {
                format.applyPattern("E 下午hh:mm");
            } else if (absTimeInterval <= noon) {
                format.applyPattern("E 中午hh:mm");
            } else if (absTimeInterval <= morning) {
                format.applyPattern("E 早上hh:mm");
            } else if (absTimeInterval <= lincheng) {
                format.applyPattern("E 凌晨hh:mm");
            }
        } else {
            format.applyPattern("MM月dd日 HH:mm");
        }

        return format.format(time);
    }

}
