package com.tuplv.dforum.until;

public class Constant {
    public static final String STATUS_ENABLE = "ENABLE";
    public static final String STATUS_DISABLE = "DISABLE";

    public static final String IS_LOGIN_TRUE = "TRUE";
    public static final String IS_LOGIN_FALSE = "FALSE";

    public static final String OBJ_ACCOUNT = "ACCOUNTS";
    public static final String OBJ_FORUM = "FORUMS";
    public static final String OBJ_POST = "POSTS";
    public static final String OBJ_COMMENT = "COMMENTS";
    public static final String OBJ_REP_COMMENT = "REP_COMMENTS";
    public static final String OBJ_NOTIFY = "NOTIFY";

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    public static final String CHIA_SE_KIEN_THUC = "Chia sẻ kiến thức";
    public static final String HOI_DAP = "Hỏi đáp";
    public static final String SORT_EARLIEST = "Mới nhất"; // Bài viết mới nhất đầu tiên
    public static final String SORT_OLDEST = "Cũ nhất"; // Bài viết cữ nhất đầu tiên
    public static final String SORT_DECREASE_VIEWS = "Lượt xem giảm dần"; // Sắp xếp bài viết có lượt xem giảm dần
    public static final String SORT_INCREASE_VIEWS = "Lượt xem tăng dần"; // Sắp xếp bài viết có lượt xem tăng dần
    public static final int PICK_IMAGE_REQUEST = 111;
    public static final int ONE_MINUTE = 65000;

    public static final int MAX_LOGIN_ATTEMPTS = 6;
    public static final long LOCK_DURATION_MS = 70 * 1000; // 70 giây

    public static final String TYPE_NOTIFY_ADMIN_ADD_POST = "ADMIN_ADD_POST";
    public static final String TYPE_NOTIFY_ADD_COMMENT = "ADD_COMMENT";
    public static final String TYPE_NOTIFY_REPLY_COMMENT = "REPLY_COMMENT";
    public static final String TYPE_NOTIFY_NEW_POST_NEED_APPROVE = "NEW_POST_NEED_APPROVE";
    public static final String TYPE_NOTIFY_APPROVE_POST = "APPROVE_POST";
    public static final String TYPE_NOTIFY_NOT_APPROVE_POST = "NOT_APPROVE_POST";

    public static final String TYPE_UPDATE_COMMENT = "UPDATE_COMMENT";
    public static final String TYPE_UPDATE_REP_COMMENT = "UPDATE_REP_COMMENT";
}
