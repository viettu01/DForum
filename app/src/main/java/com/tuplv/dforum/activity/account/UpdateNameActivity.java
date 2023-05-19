package com.tuplv.dforum.activity.account;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.PICK_IMAGE_REQUEST;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Post;

import java.util.HashMap;
import java.util.Objects;

public class UpdateNameActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    TextView tvLengthNickName, tvErrorName;
    EditText edtNickName;
    Toolbar tbUpdateName;
    Button btnUpdateName, btnCancel;
    String name;

    //firebase authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //firebase realtime
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_name);

        init();

        tbUpdateName.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        btnUpdateName = findViewById(R.id.btnUpdateName);
        btnUpdateName.setOnClickListener(this);

        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        tvErrorName = findViewById(R.id.tvErrorName);

        tvLengthNickName = findViewById(R.id.tvLengthNickName);

        edtNickName = findViewById(R.id.edtNickName);
        edtNickName.addTextChangedListener(this);

        tbUpdateName = findViewById(R.id.tbUpdateProfile);
        setSupportActionBar(tbUpdateName);

        name = getIntent().getStringExtra("name");
        edtNickName.setText(name);
    }

    private void updateName(String nickName) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;

        HashMap<String, Object> updateName = new HashMap<>();
        updateName.put("nickName", nickName);
        reference.child(OBJ_ACCOUNT).child(user.getUid()).updateChildren(updateName);
        Toast.makeText(this, "Cập nhật tên thành công !", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void checkSameName() {
        final String newName = edtNickName.getText().toString().trim();
        if (TextUtils.isEmpty(newName)) {
            Toast.makeText(UpdateNameActivity.this, "Vui lòng nhập tên mới của bạn !", Toast.LENGTH_SHORT).show();
            return;
        }
        // kiểm tra tên trùng
        if (name.equals(newName)) {
            Toast.makeText(this, "Cập nhật tên thành công !", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT);
        Query query = databaseRef.orderByChild("nickName").equalTo(newName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvErrorName.setVisibility(View.VISIBLE);
                } else {
                    updateName(newName);
                    tvErrorName.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnUpdateName:
                checkSameName();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence == edtNickName.getText()) {
            int lengthNickName = edtNickName.getText().toString().trim().length();
            tvLengthNickName.setText(lengthNickName + " / 25");
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}