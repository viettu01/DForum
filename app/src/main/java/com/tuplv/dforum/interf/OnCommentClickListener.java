package com.tuplv.dforum.interf;

import com.tuplv.dforum.model.Comment;

public interface OnCommentClickListener {
    void goToActivityUpdate(Comment comment);
    void onDeleteClick(Comment comment);
}
