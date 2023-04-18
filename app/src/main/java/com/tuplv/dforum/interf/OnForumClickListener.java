package com.tuplv.dforum.interf;

import com.tuplv.dforum.model.Forum;

public interface OnForumClickListener {
    void goToActivityUpdate(Forum forum);
    void onDeleteClick(Forum forum);
    void goToListPostOfForum(Forum forum);
}
