package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.PICK_IMAGE_REQUEST;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Account;

import java.util.HashMap;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    TextView tvEditAvatar, tvEditNickName, tvLengthNickName, tvLengthStory;
    ImageView imvAvatar;
    EditText edtNickName;
    Toolbar tbEditProfile;

    //firebase authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //firebase
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        init();
        getDataAccount();

        setSupportActionBar(tbEditProfile);
        tbEditProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        tvEditAvatar = findViewById(R.id.tvEditAvatar);
        tvEditAvatar.setOnClickListener(this);

        tvEditNickName = findViewById(R.id.tvEditNickName);
        tvEditNickName.setOnClickListener(this);

        tvLengthNickName = findViewById(R.id.tvLengthNickName);
        imvAvatar = findViewById(R.id.imvAvatar);
        imvAvatar.setOnClickListener(this);

        edtNickName = findViewById(R.id.edtNickName);
        edtNickName.addTextChangedListener(this);

        tbEditProfile = findViewById(R.id.tbEditProfile);
    }

    private void getDataAccount() {
        Account account = (Account) getIntent().getSerializableExtra("account");
        if (account.getAvatarUri().equals("null")) {
            imvAvatar.setImageResource(R.drawable.no_avatar);
        } else
            Picasso.get().load(account.getAvatarUri()).into(imvAvatar);
        edtNickName.setText(account.getNickName());
    }

    private void updateProfile(String uri, String nickName) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;

        HashMap<String, Object> updateProfile = new HashMap<>();
        updateProfile.put("nickName", nickName);
        if (!uri.equals("null"))
            updateProfile.put("avatarUri", uri);
        reference.child(OBJ_ACCOUNT).child(user.getUid()).updateChildren(updateProfile);
        Toast.makeText(this, "Cập nhật thông tin thành công !", Toast.LENGTH_SHORT).show();
    }

    private String getFileNameExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void getAvatarUri(StorageReference imgRef) {
        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String AvatarUri = uri.toString();
                updateProfile(AvatarUri, edtNickName.getText().toString().trim());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xảy ra lỗi khi lấy đường dẫn
            }
        });
    }

    private void setImageToStorage(){
        StorageReference imgRef = storageRef.child("images/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "." + getFileNameExtension(uri));

        UploadTask uploadTask = imgRef.putFile(uri);
        getAvatarUri(imgRef);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditProfileActivity.this, "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EditProfileActivity.this, "Tải ảnh lên không thành công, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            Picasso.get().load(uri).into(imvAvatar);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.tvEditAvatar:
            case R.id.imvAvatar:
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_IMAGE_REQUEST);
                break;
            case R.id.tvEditNickName:
//                setImageToStorage();
                updateProfile("null", edtNickName.getText().toString().trim());
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
            tvLengthNickName.setText(lengthNickName + " / 30");
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}