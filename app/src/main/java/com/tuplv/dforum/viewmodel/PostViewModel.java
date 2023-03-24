package com.tuplv.dforum.viewmodel;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostViewModel {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // khai báo firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    Context context;

    public PostViewModel(Context context) {
        this.context = context;
    }


}
