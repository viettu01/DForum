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
}
