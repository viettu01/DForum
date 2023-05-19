package com.tuplv.dforum.activity.forum;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_FORUM;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;
import static com.tuplv.dforum.until.Until.sendNotifyAllAccount;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Forum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddAndUpdateForumActivity extends AppCompatActivity {

    EditText edtNameForum, edtDesForum;
    Button btnAddForum;
    ProgressBar pbForum;
    Toolbar tbAddNewForum;
    Forum forum;
    Account currentAccountLogin;
    List<Account> accounts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_forum);

        init();

        tbAddNewForum.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAddForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtNameForum.getText().toString().isEmpty()) {
                    Toast.makeText(AddAndUpdateForumActivity.this, "Vui lòng nhập tên diễn đàn!", Toast.LENGTH_SHORT).show();
                    return;
                }
                pbForum.setVisibility(View.VISIBLE);
                if (forum != null)
                    updateForum();
                else
                    addForum();
                finish();
            }
        });

        getAllAccount();
        checkUpdate();
    }

    public void init() {
        edtNameForum = findViewById(R.id.edtNameForum);
        edtDesForum = findViewById(R.id.edtDesForum);
        btnAddForum = findViewById(R.id.btnShowListForum);
        tbAddNewForum = findViewById(R.id.tbAddNewForum);
        setSupportActionBar(tbAddNewForum);
        pbForum = findViewById(R.id.pbForum);
        accounts = new ArrayList<>();
    }

    public void addForum() {
        forum = new Forum();
        forum.setForumId(new Date().getTime());
        forum.setName(edtNameForum.getText().toString().trim());
        forum.setDescription(edtDesForum.getText().toString().trim());
        FirebaseDatabase.getInstance().getReference(OBJ_FORUM).child(String.valueOf(forum.getForumId())).setValue(forum)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(AddAndUpdateForumActivity.this, "Thêm diễn đàn thành công", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(AddAndUpdateForumActivity.this, "Thêm diễn đàn thất bại", Toast.LENGTH_SHORT).show();
                        pbForum.setVisibility(View.GONE);
                    }
                });

        sendNotifyAllAccount(ROLE_ADMIN, forum, null, accounts, " (Admin) đã thêm diễn đàn mới \"" + forum.getName() + "\"");
    }

    @SuppressLint("SetTextI18n")
    private void checkUpdate() {
        forum = (Forum) getIntent().getSerializableExtra("forum");
        if (forum != null) {
            tbAddNewForum.setTitle("Cập nhât diễn đàn");
            btnAddForum.setText("Cập nhật");
            edtNameForum.setText(forum.getName());
            edtDesForum.setText(forum.getDescription());
        }
    }

    public void updateForum() {
        String oldName = forum.getName();
        forum.setName(edtNameForum.getText().toString().trim());
        forum.setDescription(edtDesForum.getText().toString().trim());
        FirebaseDatabase.getInstance().getReference(OBJ_FORUM).child(String.valueOf(forum.getForumId())).setValue(forum)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(AddAndUpdateForumActivity.this, "Cập nhật diễn đàn thành công", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(AddAndUpdateForumActivity.this, "Cập nhật diễn đàn thất bại", Toast.LENGTH_SHORT).show();
                        pbForum.setVisibility(View.GONE);
                    }
                });

        sendNotifyAllAccount(ROLE_ADMIN, forum, null, accounts, " (Admin) đã đổi tên diễn đàn từ \"" + oldName + "\" thành \"" + forum.getName() + "\"");
    }

    // Lấy danh sách tài khoản và lấy thông tin tài khoản đang đăng nhập
    private void getAllAccount() {
        FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Account account = ds.getValue(Account.class);
                            if (Objects.requireNonNull(account).getAccountId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                currentAccountLogin = account;
                            accounts.add(account);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}