package com.tuplv.dforum.model;

import java.io.Serializable;

public class Account implements Serializable {
    private long accountId;
    private String nickName;
    private String story;
    private String avatarUri;
    private String email;
    private String password;
    private String role;
    private String status;

    public Account() {
    }

    public Account(long accountId, String nickName, String story, String avatarUri, String email, String password, String role, String status) {
        this.accountId = accountId;
        this.nickName = nickName;
        this.story = story;
        this.avatarUri = avatarUri;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUrl) {
        this.avatarUri = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
