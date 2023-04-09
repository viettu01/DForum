package com.tuplv.dforum.interf;

import com.tuplv.dforum.model.Post;

public interface OnPostApproveClickListener {
    void goToActivityDetail(Post post);
    void postApprove (Post post);
    void noPostApprove(Post post);
}
