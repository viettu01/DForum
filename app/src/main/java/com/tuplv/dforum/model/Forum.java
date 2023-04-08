package com.tuplv.dforum.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Serializable;

public class Forum extends ViewModel implements Serializable {
    private long forumId;
    private String name;
    private String description;

    private int totalPost;

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

    public int getTotalPost() {
        return totalPost;
    }

    public void setTotalPost(int totalPost) {
        this.totalPost = totalPost;
    }

    @Override
    public String toString() {
        return "Forum{" +
                "forumId=" + forumId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    private MutableLiveData<String> myData;

    public LiveData<String> getMyData() {
        if (myData == null) {
            myData = new MutableLiveData<String>();
        }
        return myData;
    }

    public void setMyData(String data) {
        if (myData != null) {
            myData.setValue(data);
        }
    }
}