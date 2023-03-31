package com.tuplv.dforum.model;

import java.util.List;

public class Post {
    private long postId;
    private long accountId;
    private String categoryName;
    private String forumName;
    private String title;
    private String content;
    private long approvalDate; //Ngày duyệt
    private long createdDate; //Ngày tạo
    private long view;
    private List<Comment> comments;
    private String status;

    public Post() {
    }

    public Post(long postId, long accountId, String categoryName, String forumName, String title, String content, long approvalDate, long createdDate, long view, List<Comment> comments, String status) {
        this.postId = postId;
        this.accountId = accountId;
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

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
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
                ", forumName='" + forumName + '\'' +
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
