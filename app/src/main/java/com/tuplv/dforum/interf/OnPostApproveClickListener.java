package com.tuplv.dforum.interf;

import com.tuplv.dforum.model.Post;

public interface OnPostApproveClickListener {
    void goToActivityDetail(Post post);
    void approvePost(Post post);
    void noApprovePost(Post post);
}
