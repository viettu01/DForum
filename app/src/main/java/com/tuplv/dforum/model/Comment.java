package com.tuplv.dforum.model;

import androidx.annotation.NonNull;

public class Comment {
    private long commentId;
    private String accountId;
    private String content;

    public Comment() {
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @NonNull
    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", accountId=" + accountId +
                ", content='" + content + '\'' +
                '}';
    }
}
