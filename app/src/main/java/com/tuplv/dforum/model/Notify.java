package com.tuplv.dforum.model;

public class Notify {
    private long notifyId;
    private String accountId;
    private long postId;
    private String status;
    private String typeNotify;

    public long getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(long notifyId) {
        this.notifyId = notifyId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypeNotify() {
        return typeNotify;
    }

    public void setTypeNotify(String typeNotify) {
        this.typeNotify = typeNotify;
    }
}
