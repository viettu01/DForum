package com.tuplv.dforum.model;

import java.util.List;

public class Posts {
    private long postId;
    private long accountId;
    private long accountName;
    private String categoryName;
    private String forumName;
    private String title;
    private String content;
    private long approvalDate; //Ngày duyệt
    private long createdDate; //Ngày tạo
    private long view;
    private List<Comments> comments;
    private String status;

    public Posts() {
    }

    public Posts(long postId, long accountId, long accountName, String categoryName, String forumName, String title, String content, long approvalDate, long createdDate, long view, List<Comments> comments, String status) {
        this.postId = postId;
        this.accountId = accountId;
        this.accountName = accountName;
        this.categoryName = categoryName;
        this.forumName = forumName;
        this.title = title;
        this.content = content;
        this.approvalDate = approvalDate;
        this.createdDate = createdDate;
        this.view = view;
        this.comments = comments;
        this.status = status;
    }
}
