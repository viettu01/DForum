package com.tuplv.dforum.service;

import static com.tuplv.dforum.constant.Constant.TABLE_FORUM;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.model.Forum;

import java.util.List;

public class ForumService {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    //Khởi tạo DatabaseReference
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Context context;

    public ForumService(Context context) {
        this.context = context;
        FirebaseApp.initializeApp(context);
    }

    public void create(Forum forum) {
        reference.child(TABLE_FORUM).child(String.valueOf(forum.getForumId())).setValue(forum)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(context, "Thêm forum thành công", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Thêm forum thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void findAll(List<Forum> forums) {
        reference.child(TABLE_FORUM).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Forum forum = dataSnapshot.getValue(Forum.class);
                    forums.add(forum);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
