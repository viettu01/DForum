package com.tuplv.dforum.activity.account;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_COMMENT;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.OBJ_REP_COMMENT;
import static com.tuplv.dforum.until.Constant.PICK_IMAGE_REQUEST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.post.DetailPostActivity;
import com.tuplv.dforum.adapter.PostAdapter;
import com.tuplv.dforum.interf.OnPostClickListener;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Comment;
import com.tuplv.dforum.model.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements OnPostClickListener {

    TextView tvNickName, tvEmail, tvCreatedDate, tvTotalPost, tvTotalComment, tvNoPost;
    ImageView imvAvatar, imvUpdateName;
    RelativeLayout rlAvatar;
    ProgressDialog progressDialog;
    RecyclerView rvMyPost;
    Toolbar tbProfileUser;
    List<Post> myPost;
    PostAdapter myPostAdapter;

    //firebase authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Account account;
    String userId;
    //firebase
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

        if (userId != null) {
            getProfile();
            getMyPost();
            getTotalComment();
        } else
            Toast.makeText(this, "Có lỗi xảy ra thử lại sau !", Toast.LENGTH_SHORT).show();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != null && !userId.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                    Intent intent = new Intent(ProfileActivity.this, ShowAvatarActivity.class);
                    intent.putExtra("avatarUri", account.getAvatarUri());
                    startActivity(intent);
                } else if (userId != null && userId.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                    showBottomSheetDialogAvatar();
            }
        };
        imvAvatar.setOnClickListener(listener);
        rlAvatar.setOnClickListener(listener);

        imvUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, UpdateNameActivity.class);
                intent.putExtra("name", account.getNickName());
                startActivity(intent);
            }
        });

        tbProfileUser.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        tvEmail = findViewById(R.id.tvEmail);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        imvUpdateName = findViewById(R.id.imvUpdateName);
        tvNickName = findViewById(R.id.tvNickName);
        imvAvatar = findViewById(R.id.imvAvatar);
        rlAvatar = findViewById(R.id.rlAvatar);

        rvMyPost = findViewById(R.id.rvMyPost);
        tvTotalPost = findViewById(R.id.tvTotalPost);
        tvTotalComment = findViewById(R.id.tvTotalComment);
        tvNoPost = findViewById(R.id.tvNoPost);

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Đang tải ảnh lên");

        tbProfileUser = findViewById(R.id.tbProfileUser);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        if (userId != null && !userId.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
            rlAvatar.setVisibility(View.GONE);
            imvUpdateName.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void getProfile() {
        reference.child(OBJ_ACCOUNT).child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            account = snapshot.getValue(Account.class);
                            if (account != null) {
                                if (account.getAvatarUri().equals("null"))
                                    imvAvatar.setImageResource(R.drawable.no_avatar);
                                else
                                    Picasso.get().load(account.getAvatarUri()).into(imvAvatar);
                                Log.d("aaa", account.getAvatarUri());
                                tvNickName.setText(account.getNickName());

                                tvEmail.setText(account.getEmail());

                                Date date = new Date(account.getCreatedDate());
                                SimpleDateFormat fMonth = new SimpleDateFormat("M");
                                SimpleDateFormat fYear = new SimpleDateFormat("yyyy");
                                String month = fMonth.format(date);
                                String year = fYear.format(date);

                                tvCreatedDate.setText("Tham gia vào tháng " + month + " năm " + year);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileActivity.this, "Có lỗi xảy ra, thử lại sau", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("InflateParams")
    private void showBottomSheetDialogAvatar() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet_avatar, null);
        bottomSheetDialog.setContentView(bottomSheetView);


        LinearLayout llChooseAvatar = bottomSheetDialog.findViewById(R.id.llChooseAvatar);
        LinearLayout llShowAvatar = bottomSheetDialog.findViewById(R.id.llShowAvatar);
        LinearLayout llRemoveAvatar = bottomSheetDialog.findViewById(R.id.llRemoveAvatar);

        Objects.requireNonNull(llChooseAvatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_IMAGE_REQUEST);
                bottomSheetDialog.dismiss();
            }
        });

        Objects.requireNonNull(llShowAvatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ShowAvatarActivity.class);
                intent.putExtra("avatarUri", account.getAvatarUri());
                Log.d("aaaStartIntent", account.getAvatarUri());
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        Objects.requireNonNull(llRemoveAvatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                reference.child(OBJ_ACCOUNT).child(user.getUid()).child("avatarUri").setValue("null");
                Toast.makeText(ProfileActivity.this, "Cập nhật ảnh dại diện thành công !", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });
        //bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();
    }

    // Cập nhật ảnh lên realtime
    private void updateAvatarUriRealtime(String uri) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;

        HashMap<String, Object> updateAvatarUri = new HashMap<>();
        if (!uri.equals("null")) {
            updateAvatarUri.put("avatarUri", uri);
            reference.child(OBJ_ACCOUNT).child(user.getUid()).updateChildren(updateAvatarUri);
        }
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
                String avatarUri = uri.toString();
                account.setAvatarUri(avatarUri);
                updateAvatarUriRealtime(avatarUri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xảy ra lỗi khi lấy đường dẫn
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            progressDialog.show();
            StorageReference imgRef = storageRef.child("images/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "." + getFileNameExtension(uri));

            UploadTask uploadTask = imgRef.putFile(uri);
            getAvatarUri(imgRef);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Picasso.get().load(uri).into(imvAvatar);
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(ProfileActivity.this, "Tải ảnh lên không thành công, thử lại sau", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getTotalComment() {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference(OBJ_POST);
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int commentCount = 0;
                int repCommentCount = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot commentSnapshot : postSnapshot.child(OBJ_COMMENT).getChildren()) {
                        Comment comment = commentSnapshot.getValue(Comment.class);
                        assert comment != null;
                        if (String.valueOf(comment.getAccountId()).equals(userId)) {
                            commentCount++;
                            for (DataSnapshot repCommentSnapshot : commentSnapshot.child(OBJ_REP_COMMENT).getChildren()) {
                                Comment repComment = repCommentSnapshot.getValue(Comment.class);
                                assert repComment != null;
                                if (String.valueOf(repComment.getAccountId()).equals(userId)) {
                                    repCommentCount++;
                                }
                            }
                        }
                    }
                }
                tvTotalComment.setText(String.valueOf(commentCount + repCommentCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // xử lý khi có lỗi xảy ra
            }
        });
    }


    private void getMyPost() {
        myPost = new ArrayList<>();
        myPostAdapter = new PostAdapter(this, R.layout.item_post, myPost, this);
        rvMyPost.setAdapter(myPostAdapter);
        rvMyPost.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        FirebaseDatabase.getInstance().getReference(OBJ_POST).orderByChild("status").equalTo(STATUS_ENABLE)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myPost.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (Objects.requireNonNull(post).getAccountId().equals(userId)) {
                                myPost.add(post);
                            }
                        }
                        tvTotalPost.setText(String.valueOf(myPost.size()));
                        myPostAdapter.notifyDataSetChanged();

                        if (myPost.size() == 0)
                            tvNoPost.setVisibility(View.VISIBLE);
                        else
                            tvNoPost.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void goToActivityDetail(Post post) {
        HashMap<String, Object> updateView = new HashMap<>();
        updateView.put("view", post.getView() + 1);
        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                .updateChildren(updateView);

        Intent intent = new Intent(ProfileActivity.this, DetailPostActivity.class);
        intent.putExtra("post", post);

        startActivity(intent);
    }
}