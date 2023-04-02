package com.tuplv.dforum.fragment;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.EditProfileActivity;
import com.tuplv.dforum.activity.LoginActivity;
import com.tuplv.dforum.model.Account;

import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    Button btnLogout, btnEditProfile;
    TextView tvNickName, tvStory;

    ImageView imvAvatar;

    //firebase authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    Account account;
    //firebase
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
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
        tvStory = view.findViewById(R.id.tvStory);
        imvAvatar = view.findViewById(R.id.imvAvatar);
    }

    private void getProfile() {
        reference.child(OBJ_ACCOUNT).child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            account = snapshot.getValue(Account.class);
                            if (account != null) {
                                if (account.getAvatarUri().equals("null")) {
                                    imvAvatar.setImageResource(R.drawable.no_avatar);
                                } else
                                    Picasso.get().load(account.getAvatarUri()).into(imvAvatar);
                                tvNickName.setText(account.getNickName());
                                tvStory.setText(account.getStory());

                                if(tvStory.getText().toString().trim().equals("null"))
                                    tvStory.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Có lỗi xảy ra vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void putDataAccount(){
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        intent.putExtra("account", account);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getProfile();
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                mAuth.signOut();
                requireContext().deleteSharedPreferences("account");
                requireContext().startActivity(new Intent(getContext(), LoginActivity.class));
                requireActivity().finish();
                break;
            case R.id.btnEditProfile:
                putDataAccount();
                break;
        }
    }
}