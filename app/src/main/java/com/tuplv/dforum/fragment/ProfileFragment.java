package com.tuplv.dforum.fragment;

import static android.app.Activity.RESULT_OK;
import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_COMMENT;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.PICK_IMAGE_REQUEST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.tuplv.dforum.activity.profile.EditProfileActivity;
import com.tuplv.dforum.activity.account.LoginActivity;
import com.tuplv.dforum.activity.profile.ShowImageActivity;
import com.tuplv.dforum.activity.post.ViewPostsActivity;
import com.tuplv.dforum.adapter.PostsAdapter;
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

public class ProfileFragment extends Fragment implements View.OnClickListener, OnPostClickListener {
    Button btnLogout, btnEditProfile;
    TextView tvNickName, tvEmail, tvCreatedDate, tvTotalPost, tvTotalComment;
    ImageView imvAvatar;
    RecyclerView rvMyPost;
    List<Post> myPost;
    PostsAdapter myPostAdapter;
    //firebase authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    Account account;
    //firebase
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        tvEmail = view.findViewById(R.id.tvEmail);
        tvCreatedDate = view.findViewById(R.id.tvCreatedDate);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(this);

        tvNickName = view.findViewById(R.id.tvNickName);

        imvAvatar = view.findViewById(R.id.imvAvatar);
        imvAvatar.setOnClickListener(this);

        rvMyPost = view.findViewById(R.id.rvMyPost);
        tvTotalPost = view.findViewById(R.id.tvTotalPost);
        tvTotalComment = view.findViewById(R.id.tvTotalComment);
    }

    @SuppressLint("SimpleDateFormat")
    private void getProfile() {
        reference.child(OBJ_ACCOUNT).child(user.getUid())
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
                        Toast.makeText(getContext(), "Có lỗi xảy ra vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("InflateParams")
    private void dialogAvatar() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_bottom_sheet_menu, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        LinearLayout linear_choose_avatar = bottomSheetDialog.findViewById(R.id.linear_choose_avatar);
        LinearLayout linear_view_avatar = bottomSheetDialog.findViewById(R.id.linear_view_avatar);
        LinearLayout linear_remove_avatar = bottomSheetDialog.findViewById(R.id.linear_remove_avatar);

        Objects.requireNonNull(linear_choose_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_IMAGE_REQUEST);
                bottomSheetDialog.dismiss();
            }
        });

        Objects.requireNonNull(linear_view_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowImageActivity.class);
                intent.putExtra("avatarUri", account.getAvatarUri());
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        Objects.requireNonNull(linear_remove_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                reference.child(OBJ_ACCOUNT).child(user.getUid()).child("avatarUri").setValue("null");
                Toast.makeText(getContext(), "Cập nhật ảnh dại diện thành công !", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.show();
    }

    private void updateAvatar(String uri) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;

        HashMap<String, Object> updateProfile = new HashMap<>();
        if (!uri.equals("null")) {
            updateProfile.put("avatarUri", uri);
            reference.child(OBJ_ACCOUNT).child(user.getUid()).updateChildren(updateProfile);
            Toast.makeText(getContext(), "Cập nhật thông tin thành công !", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileNameExtension(Uri uri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void getAvatarUri(StorageReference imgRef) {
        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String avatarUri = uri.toString();
                updateAvatar(avatarUri);
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

            StorageReference imgRef = storageRef.child("images/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "." + getFileNameExtension(uri));

            UploadTask uploadTask = imgRef.putFile(uri);
            getAvatarUri(imgRef);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Picasso.get().load(uri).into(imvAvatar);
                    Toast.makeText(getContext(), "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getContext(), "Tải ảnh lên không thành công, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (user != null)
            getProfile();
        getMyPost();
        getTotalComment();
    }

    private void getTotalComment(){
        List<Comment> comments = new ArrayList<>();
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference(OBJ_POST);
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot commentSnapshot : postSnapshot.child(OBJ_COMMENT).getChildren()) {
                        Comment comment = commentSnapshot.getValue(Comment.class);
                        assert comment != null;
                        if (String.valueOf(comment.getAccountId()).equals(user.getUid())){
                            comments.add(comment);
                        }
                    }
                    tvTotalComment.setText(String.valueOf(comments.size()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // xử lý khi có lỗi xảy ra
            }
        });

    }

    private void getMyPost(){
        myPost = new ArrayList<>();
        myPostAdapter = new PostsAdapter(getActivity(), R.layout.item_posts, myPost, this);
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
                            if (Objects.requireNonNull(post).getAccountId().equals(user.getUid())) {
                                myPost.add(post);
                            }
                        }
                        tvTotalPost.setText(String.valueOf(myPost.size()));
                        myPostAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void goToActivityDetail(Post post) {
        HashMap<String, Object> updateView = new HashMap<>();
        updateView.put("view", post.getView() + 1);
        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                .updateChildren(updateView);

        Intent intent = new Intent(getActivity(), ViewPostsActivity.class);
        intent.putExtra("post", post);

        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                mAuth.signOut();
                requireContext().deleteSharedPreferences("account");
                requireContext().startActivity(new Intent(getContext(), LoginActivity.class));
                break;
            case R.id.btnEditProfile:
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("account", account);
                startActivity(intent);
                break;
            case R.id.imvAvatar:
                dialogAvatar();
                break;
        }
    }
}