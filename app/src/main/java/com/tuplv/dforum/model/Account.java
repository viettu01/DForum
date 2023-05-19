package com.tuplv.dforum.model;

import java.io.Serializable;

public class Account implements Serializable {
    private String accountId;
    private String nickName;
    private String avatarUri;
    private String email;
    private String role;
    private String status;
    private long createdDate;
    private long countLoginFail;
    private long lockedDate;

    public Account() {
    }

    public long getCountLoginFail() {
        return countLoginFail;
    }

    public void setCountLoginFail(long countLoginFail) {
        this.countLoginFail = countLoginFail;
    }

    public long getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(long lockedDate) {
        this.lockedDate = lockedDate;
    }

    public Account(String accountId, String nickName, String avatarUri, String email, String role, String status, long createdDate, long countLoginFail, long lockedDate) {
        this.accountId = accountId;
        this.nickName = nickName;
        this.avatarUri = avatarUri;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdDate = createdDate;
        this.countLoginFail = countLoginFail;
        this.lockedDate = lockedDate;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }
}
