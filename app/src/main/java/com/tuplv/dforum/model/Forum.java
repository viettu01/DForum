package com.tuplv.dforum.model;

public class Forum {
    private long forumId;
    private String name;
    private String description;

    public Forum() {
    }

    public Forum(long forumId, String name, String description) {
        this.forumId = forumId;
        this.name = name;
        this.description = description;
    }

    public long getForumId() {
        return forumId;
    }

    public void setForumId(long forumId) {
        this.forumId = forumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
