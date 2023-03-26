package com.tuplv.dforum.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.tuplv.dforum.R;
import com.tuplv.dforum.service.AccountService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvRegister, tvForgotPassword;
    Button btnLogin;
    EditText edtLoginEmail, edtLoginPassword;
    ImageView ic_back_arrow_login;
    private String email, password;
    private AccountService accountService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        accountService = new AccountService(this);
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
    }

    private void getText() {
        email = edtLoginEmail.getText().toString().trim();
        password = edtLoginPassword.getText().toString().trim();
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

    // Kiểm tra trống và đăng nhập
    private void login() {
        getText();
        if (!email.isEmpty() || !password.isEmpty()) {
            accountService.login(email, password);
        } else
            Toast.makeText(this, "Vui lòng nhập Email và mật khẩu để đăng nhập", Toast.LENGTH_SHORT).show();
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
                this.finish();
                break;
            case R.id.btnLogin:
                login();
                break;
        }
    }
}