package com.tuplv.dforum.activity.account;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;
import static com.tuplv.dforum.until.Constant.ROLE_USER;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.main.AdminMainActivity;
import com.tuplv.dforum.activity.main.UserMainActivity;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvRegister, tvForgotPassword;
    Button btnLogin;
    EditText edtLoginEmail, edtLoginPassword;
    ImageView ic_back_arrow_login;
    ProgressDialog progressDialog;
    private String email, password;
    SharedPreferences sharedPreferences;
    //firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(this);

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(this);

        ic_back_arrow_login = findViewById(R.id.ic_back_arrow_login);
        ic_back_arrow_login.setOnClickListener(this);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        edtLoginEmail = findViewById(R.id.edtLoginEmail);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);

        progressDialog = new ProgressDialog(this);
        //progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setMessage("Đang đăng nhập");

        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
    }

    private void getText() {
        email = edtLoginEmail.getText().toString().trim();
        password = edtLoginPassword.getText().toString().trim();
    }

    private void login() {
        progressDialog.show();
        getText();
        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                changePasswordFirebaseRealtime(password);

                                // kiểm tra email đã xác minh hay chưa
                                if (user != null) {
                                    boolean emailVerified = user.isEmailVerified();
                                    if (emailVerified) {
                                        getAccountRole();
                                    } else
                                        Toast.makeText(LoginActivity.this, "Xác minh email của bạn trước", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        } else
            Toast.makeText(this, "Vui lòng nhập Email và mật khẩu để đăng nhập", Toast.LENGTH_SHORT).show();
    }

    //Đổi mật khẩu trên authentication
    private void changePasswordFirebaseAuth(String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Đổi mật khẩu cũ trên realtime
    private void changePasswordFirebaseRealtime(String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;

        HashMap<String, Object> updatePassword = new HashMap<>();
        updatePassword.put("password", newPassword);
        reference.child(OBJ_ACCOUNT).child(user.getUid()).updateChildren(updatePassword);
    }

    // Xử lý quên mật khẩu
    private void dialogForgotPassword() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forgot_password);

        // Không cho thoát khi bấm ra ngoài màn hình
        dialog.setCanceledOnTouchOutside(false);

        //ánh xạ
        Button btnForgotPassword = dialog.findViewById(R.id.btnForgotPassword);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        EditText edtEmailResetPassword = dialog.findViewById(R.id.edtEmailResetPassword);

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailResetPassword = edtEmailResetPassword.getText().toString().trim();
                if (!emailResetPassword.isEmpty()) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(emailResetPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Kiểm tra email để hoàn tất đặt lại mật khẩu mới", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
                } else
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập Email để tiếp tục", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void getAccountRole() {
        reference.child(OBJ_ACCOUNT).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String role = snapshot.getValue(String.class);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("role", role);
                            editor.apply();
                            if (role != null) {
                                if (role.equals(ROLE_ADMIN))
                                    startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
                                if (role.equals(ROLE_USER))
                                    startActivity(new Intent(LoginActivity.this, UserMainActivity.class));
                                finish();
                            }
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
            case R.id.tvRegister:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.tvForgotPassword:
                dialogForgotPassword();
                break;
            case R.id.ic_back_arrow_login:
                startActivity(new Intent(LoginActivity.this, UserMainActivity.class));
                break;
            case R.id.btnLogin:
                login();
                break;
        }
    }
}