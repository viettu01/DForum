package com.tuplv.dforum.model;

public class Accounts {
    private String accountId;
    private String nickName;
    private String story;
    private String avatarUrl;
    private String email;
    private String password;
    private String role;
    private String status;
    private String startDate;

    public Accounts() {
    }

    public Accounts(String accountId, String nickName, String story, String avatarUrl, String email, String password, String role, String status, String startDate) {
        this.accountId = accountId;
        this.nickName = nickName;
        this.story = story;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.startDate = startDate;
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

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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
    public String getStartDate() {return startDate;}

    public void setStartDate(String startDate) {this.startDate = startDate;}
}
