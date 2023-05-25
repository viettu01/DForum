package com.tuplv.dforum.until;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_NOTIFY;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;
import static com.tuplv.dforum.until.Constant.ROLE_USER;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_ADD_COMMENT;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_ADMIN_ADD_POST;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_APPROVE_POST;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_NEW_POST_NEED_APPROVE;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_NOT_APPROVE_POST;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_REPLY_COMMENT;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.model.Notify;
import com.tuplv.dforum.model.Post;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

    public static String formatTime(long time) {
        Date commentDate = new Date(time);
        long diffInMillis = new Date().getTime() - commentDate.getTime();
        long secondsAgo = TimeUnit.SECONDS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        if (secondsAgo < 60) {
            return "Vừa xong";
        } else if (secondsAgo < 3600) {
            long minutesAgo = TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS);
            return minutesAgo + " phút trước";
        } else if (secondsAgo < 86400) {
            long hoursAgo = TimeUnit.HOURS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            return hoursAgo + " giờ trước";
        } else if (secondsAgo < 604800) {
            long daysAgo = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            return daysAgo + " ngày trước";
        } else if (secondsAgo < 31536000) {
            long diffInWeeks = diffInMillis / (7 * 24 * 60 * 60 * 1000);
            return diffInWeeks + " tuần trước";
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
            return diffInYears + " năm trước";
        }
    }

    public static String formatNotify(String typeNotify) {
        String message = "";

        if (typeNotify.equals(TYPE_NOTIFY_NEW_POST_NEED_APPROVE))
            message = " đã thêm một bài viết mới cần phê duyệt: ";

        if (typeNotify.equals(TYPE_NOTIFY_APPROVE_POST))
            message = " đã phê duyệt bài viết của bạn: ";

        if (typeNotify.equals(TYPE_NOTIFY_NOT_APPROVE_POST))
            message = " bài viết của bạn không được phê duyệt: ";

        if (typeNotify.equals(TYPE_NOTIFY_ADMIN_ADD_POST))
            message = " (Admin) đã thêm một bài viết mới: ";

        if (typeNotify.equals(TYPE_NOTIFY_ADD_COMMENT))
            message = " đã bình luận bài viết của bạn: ";

        if (typeNotify.equals(TYPE_NOTIFY_REPLY_COMMENT))
            message = " đã trả lời bình luận của bạn về bài viết: ";

        if (message.equals(""))
            message = typeNotify;

        return message;
    }

    // Gửi thông báo cho chủ bài viết và chủ bình luận
    public static void sendNotifyToAuthor(Post post, String typeNotify, String accountIdComment) {
        if (!post.getAccountId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
            Notify notify = new Notify();
            notify.setNotifyId(new Date().getTime());
            notify.setPostId(post.getPostId());
            notify.setAccountId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            notify.setStatus(STATUS_DISABLE);
            notify.setTypeNotify(typeNotify);

            if (post.getStatusNotify() == null || post.getStatusNotify().equals(STATUS_ENABLE)) {
                FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(post.getAccountId())
                        .child(OBJ_NOTIFY).child(String.valueOf(notify.getNotifyId())).setValue(notify)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                task.isSuccessful();
                            }
                        });
            }

            if (accountIdComment != null && !accountIdComment.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                notify.setTypeNotify(TYPE_NOTIFY_REPLY_COMMENT);
                FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(accountIdComment)
                        .child(OBJ_NOTIFY).child(String.valueOf(notify.getNotifyId())).setValue(notify)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                task.isSuccessful();
                            }
                        });
            }
        }
    }

    // Thông báo cho tất cả người dùng
    public static void sendNotifyAllAccount(String role, Forum forum, Post post, List<Account> accounts, String typeNotify) {
        Notify notify = new Notify();
        notify.setNotifyId(new Date().getTime());

        if (post != null)
            notify.setPostId(post.getPostId());

        if (forum != null)
            notify.setForumId(forum.getForumId());

        notify.setAccountId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        notify.setStatus(STATUS_DISABLE);
        if (role.equals(ROLE_ADMIN))
            notify.setTypeNotify(typeNotify);

        if (role.equals(ROLE_USER))
            notify.setTypeNotify(TYPE_NOTIFY_NEW_POST_NEED_APPROVE);

        for (Account account : accounts) {
            if (!account.getAccountId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                if (role.equals(ROLE_ADMIN))
                    FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(account.getAccountId())
                            .child(OBJ_NOTIFY).child(String.valueOf(notify.getNotifyId())).setValue(notify);
                if (role.equals(ROLE_USER) && account.getRole().equals(ROLE_ADMIN))
                    FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(account.getAccountId())
                            .child(OBJ_NOTIFY).child(String.valueOf(notify.getNotifyId())).setValue(notify);
            }
        }
    }
}
