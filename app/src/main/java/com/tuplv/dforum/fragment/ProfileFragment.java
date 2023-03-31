package com.tuplv.dforum.fragment;

import static android.app.Activity.RESULT_OK;
import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.LoginActivity;
import com.tuplv.dforum.model.Account;

import java.util.UUID;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    Button btnLogout, btnEditProfile;
    TextView tvNickName;

    ImageView imvAvatar;

    //firebase authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    //firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);

        if (user != null)
            getProfile();
        return view;
    }

    private void init(View view) {
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(this);

        tvNickName = view.findViewById(R.id.tvNickName);

        imvAvatar = view.findViewById(R.id.imvAvatar);
    }

    public void getProfile() {
        reference.child(OBJ_ACCOUNT).child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Account account = snapshot.getValue(Account.class);
                            if (account != null) {
                                tvNickName.setText(account.getNickName());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Có lỗi xảy ra vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 113 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            StorageReference imageRef = storageRef.child("images/" + mAuth.getCurrentUser().getUid());

            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Đường dẫn của tập tin
                    String url = uri.toString();
                    System.out.println("đường dẫn ảnh: "+url);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Xảy ra lỗi khi lấy đường dẫn
                }
            });

            UploadTask uploadTask = imageRef.putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
                    String uri = taskSnapshot.getStorage().getDownloadUrl().toString();
                    Toast.makeText(getContext(), "uri: " + uri, Toast.LENGTH_SHORT).show();
                    System.out.println(uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getContext(), "Tải ảnh lên không thành công", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                mAuth.signOut();
                requireContext().startActivity(new Intent(getContext(), LoginActivity.class));
                requireActivity().finish();
                break;
            case R.id.btnEditProfile:

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 113);


                //requireContext().startActivity(new Intent(getContext(), EditProfileActivity.class));
                //requireActivity().finish();
                break;
        }
    }
}