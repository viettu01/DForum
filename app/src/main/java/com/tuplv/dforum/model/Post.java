package com.tuplv.dforum.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Post implements Serializable {
    private long postId;
    private String accountId;
    private String categoryName;
    private long forumId;
    private String title;
    private String content;
    private long approveDate; //Ngày duyệt
    private long createdDate; //Ngày tạo
    private long view;
    private String status;
    private String statusNotify;

    public Post() {
    }

    public String getStatusNotify() {
        return statusNotify;
    }

    public void setStatusNotify(String statusNotify) {
        this.statusNotify = statusNotify;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getForumId() {
        return forumId;
    }

    public void setForumId(long forumId) {
        this.forumId = forumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(long approveDate) {
        this.approveDate = approveDate;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getView() {
        return view;
    }

    public void setView(long view) {
        this.view = view;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "Posts{" +
                "postId=" + postId +
                ", accountId=" + accountId +
                ", categoryName='" + categoryName + '\'' +
                ", forumName='" + forumId + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", approvalDate=" + approveDate +
                ", createdDate=" + createdDate +
                ", view=" + view +
                ", status='" + status + '\'' +
                '}';
    }
}
