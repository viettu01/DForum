package com.tuplv.dforum.service;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostService {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // khai báo firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    Context context;

    public PostService(Context context) {
        this.context = context;
    }


}
