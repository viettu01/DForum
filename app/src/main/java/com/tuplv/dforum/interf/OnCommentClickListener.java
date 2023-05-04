package com.tuplv.dforum.interf;

import android.net.Uri;

import com.tuplv.dforum.model.Comment;

public interface OnCommentClickListener {
    void goToActivityUpdate(Comment comment);
    void onDeleteClick(Comment comment);
    void goToActivityComment(Comment comment, String nameAuthorRepComment);
}
