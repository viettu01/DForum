package com.tuplv.dforum.interf;

import com.tuplv.dforum.model.Notify;

public interface OnNotifyClickListener {
    void goToDetailPostActivity(Notify notify);
    void goToListPostActivity(Notify notify);
    void onCheckNotify(Notify notify);
    void onDeleteNotify(Notify notify);
}
