package com.tuplv.dforum.fragment;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;

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
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.account.LoginActivity;
import com.tuplv.dforum.activity.account.ProfileActivity;
import com.tuplv.dforum.activity.account.RegisterActivity;
import com.tuplv.dforum.activity.account.UpdatePasswordActivity;
import com.tuplv.dforum.activity.main.DarkModeActivity;
import com.tuplv.dforum.activity.main.StartActivity;
import com.tuplv.dforum.activity.post.PostApproveActivity;
import com.tuplv.dforum.model.Account;

import java.util.Objects;

public class AccountFragment extends Fragment implements View.OnClickListener {
    Button btnCallLogin, btnCallRegister;
    TextView tvNickName;
    ImageView imvAvatar;
    LinearLayout llLogout, llNotLogin, llProfile, llAdmin, llPostApprove, llListUser, llDarkMode, llChangePassword, llReportProblem, llHelp, llPolicy;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    SharedPreferences sharedPreferences;
    Account account;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        init(view);

        sharedPreferences = requireContext().getSharedPreferences("account", Context.MODE_PRIVATE);

        if (user != null) {
            getProfile();
            llLogout.setVisibility(View.VISIBLE);
            llProfile.setVisibility(View.VISIBLE);
            llNotLogin.setVisibility(View.GONE);
            llAdmin.setVisibility(sharedPreferences.getString("role", "").equals(ROLE_ADMIN) ? View.VISIBLE : View.GONE);
        } else {
            llLogout.setVisibility(View.GONE);
            llProfile.setVisibility(View.GONE);
            llNotLogin.setVisibility(View.VISIBLE);
            llAdmin.setVisibility(View.GONE);
        }

        return view;
    }

    private void init(View view) {
        llPostApprove = view.findViewById(R.id.llPostApprove);
        llLogout = view.findViewById(R.id.llLogout);
        llNotLogin = view.findViewById(R.id.llNotLogin);
        llProfile = view.findViewById(R.id.llProfile);
        llAdmin = view.findViewById(R.id.llAdmin);
        llListUser = view.findViewById(R.id.llListUser);
        llDarkMode = view.findViewById(R.id.llDarkMode);
        llChangePassword = view.findViewById(R.id.llChangePassword);

        llReportProblem = view.findViewById(R.id.llReportProblem);
        llHelp = view.findViewById(R.id.llHelp);
        llPolicy = view.findViewById(R.id.llPolicy);

        btnCallLogin = view.findViewById(R.id.btnCallLogin);
        btnCallRegister = view.findViewById(R.id.btnCallRegister);
        tvNickName = view.findViewById(R.id.tvNickName);
        imvAvatar = view.findViewById(R.id.imvAvatar);

        llPostApprove.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llProfile.setOnClickListener(this);
        llDarkMode.setOnClickListener(this);
        llListUser.setOnClickListener(this);
        llChangePassword.setOnClickListener(this);

        llReportProblem.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llPolicy.setOnClickListener(this);

        btnCallLogin.setOnClickListener(this);
        btnCallRegister.setOnClickListener(this);
        imvAvatar.setOnClickListener(this);
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
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Có lỗi xảy ra, thử lại sau", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llLogout:
                mAuth.signOut();
                requireContext().deleteSharedPreferences("account");
                requireContext().startActivity(new Intent(getContext(), StartActivity.class));
                requireActivity().finish();
                break;
            case R.id.imvAvatar:
            case R.id.llProfile:
                if (user != null) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("userId", account.getAccountId());
                    startActivity(intent);
                }
                break;
            case R.id.llPostApprove:
                startActivity(new Intent(getActivity(), PostApproveActivity.class));
                break;
            case R.id.llChangePassword:
                if (user != null) {
                    startActivity(new Intent(getActivity(), UpdatePasswordActivity.class));
                } else
                    Toast.makeText(getContext(), "Đăng nhập để sử dụng chức năng này!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnCallLogin:
                startActivity(new Intent(getContext(), LoginActivity.class));
                break;
            case R.id.btnCallRegister:
                startActivity(new Intent(getContext(), RegisterActivity.class));
                break;
            case R.id.llDarkMode:
                startActivity(new Intent(getContext(), DarkModeActivity.class));
                break;
            case R.id.llReportProblem:
            case R.id.llListUser:
            case R.id.llHelp:
            case R.id.llPolicy:
                Toast.makeText(getContext(), "Tính năng đang được phát triển!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}