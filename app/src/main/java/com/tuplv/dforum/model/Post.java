package com.tuplv.dforum.model;

import java.io.Serializable;
import java.util.List;

public class Post implements Serializable {
    private long postId;
    private String accountId;
    private String categoryName;
    private long forumId;
    private String title;
    private String content;
    private long approvalDate; //Ngày duyệt
    private long createdDate; //Ngày tạo
    private long view;
    private List<Comment> comments;
    private String status;

    public Post() {
    }

    public Post(long postId, String accountId, String categoryName, long forumId, String title, String content, long approvalDate, long createdDate, long view, List<Comment> comments, String status) {
        this.postId = postId;
        this.accountId = accountId;
        this.categoryName = categoryName;
        this.forumId = forumId;
        this.title = title;
        this.content = content;
        this.approvalDate = approvalDate;
        this.createdDate = createdDate;
        this.view = view;
        this.comments = comments;
        this.status = status;
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

    public long getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(long approvalDate) {
        this.approvalDate = approvalDate;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Posts{" +
                "postId=" + postId +
                ", accountId=" + accountId +
                ", categoryName='" + categoryName + '\'' +
                ", forumName='" + forumId + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", approvalDate=" + approvalDate +
                ", createdDate=" + createdDate +
                ", view=" + view +
                ", comments=" + comments +
                ", status='" + status + '\'' +
                '}';
    }
}
