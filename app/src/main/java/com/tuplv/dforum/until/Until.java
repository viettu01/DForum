package com.tuplv.dforum.until;

import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_ADD_COMMENT;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_ADD_POST;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_APPROVE;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_REPLY_COMMENT;

import android.annotation.SuppressLint;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Until {
    @SuppressLint("DefaultLocale")
    public static String formatNumber(long number) {
        if (number >= 1000 && number < 1000000) {
            return String.format("%.1f", number / 1000.0) + " K";
        } else if (number >= 1000000 && number < 1000000000) {
            return String.format("%.1f", number / 1000000.0) + " Tr";
        } else if (number >= 1000000000) {
            return String.format("%.1f", number / 1000000000.0) + " B";
        } else {
            return String.valueOf(number);
        }
    }

    public static String formatTime(long time){
        Date commentDate = new Date(time);
        long diffInMillis = new Date().getTime() - commentDate.getTime();
        long secondsAgo = TimeUnit.SECONDS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        if (secondsAgo < 60) {
            return "Vừa xong";
        } else if (secondsAgo < 3600) {
            long minutesAgo = TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS);
            return minutesAgo + " phút";
        } else if (secondsAgo < 86400) {
            long hoursAgo = TimeUnit.HOURS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            return hoursAgo + " giờ";
        } else if (secondsAgo < 604800) {
            long daysAgo = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            return daysAgo + " ngày";
        } else if (secondsAgo < 31536000) {
            long diffInWeeks = diffInMillis / (7 * 24 * 60 * 60 * 1000);
            return diffInWeeks + " tuần";
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(commentDate);

            int commentMonth = calendar.get(Calendar.MONTH);
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

            int commentDate2 = calendar.get(Calendar.DATE);
            int currentDate = Calendar.getInstance().get(Calendar.DATE);

            int diffInYears = Calendar.getInstance().get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
            if (currentMonth < commentMonth || (currentMonth == commentMonth && currentDate < commentDate2)) {
                diffInYears--;
            }
            return diffInYears + " năm";
        }
    }

    public static String formatNotify(String typeNotify) {
        String message = "";

        if (typeNotify.equals(TYPE_NOTIFY_APPROVE))
            message = " đã phê duyệt bài vết của bạn: ";

        if (typeNotify.equals(TYPE_NOTIFY_ADD_POST))
            message = " đã thêm một bài viết mới: ";

        if (typeNotify.equals(TYPE_NOTIFY_ADD_COMMENT))
            message = " đã bình luận bài vết của bạn: ";

        if (typeNotify.equals(TYPE_NOTIFY_REPLY_COMMENT))
            message = " đã trả lời bình luận của bạn: ";

        return message;
    }
}
