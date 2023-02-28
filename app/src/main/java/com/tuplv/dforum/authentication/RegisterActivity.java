package com.tuplv.dforum.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuplv.dforum.MainActivity;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Accounts;
import com.tuplv.dforum.service.AccountService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    TextView tvErrorEmail, tvErrorPassword, tvErrorConfirmPassword;
    EditText edtRegisterEmail, edtRegisterPassword, edtRegisterConfirmPassword;
    Button btnRegister;
    ImageView ic_back_arrow_register;
    private FirebaseAuth mAuth;
    private AccountService accountService;

    // khai báo firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ánh xạ
        init();
        // khai báo account service
        accountService = new AccountService();

        // khởi tạo xác thực firebase
        mAuth = FirebaseAuth.getInstance();
    }

    private void init() {
        tvErrorEmail = findViewById(R.id.tvErrorEmail);
        tvErrorPassword = findViewById(R.id.tvErrorPassword);
        tvErrorConfirmPassword = findViewById(R.id.tvErrorConfirmPassword);

        edtRegisterEmail = findViewById(R.id.edtRegisterEmail);
        edtRegisterEmail.addTextChangedListener(this);

        edtRegisterPassword = findViewById(R.id.edtRegisterPassword);
        edtRegisterPassword.addTextChangedListener(this);

        edtRegisterConfirmPassword = findViewById(R.id.edtRegisterConfirmPassword);
        edtRegisterConfirmPassword.addTextChangedListener(this);


        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        ic_back_arrow_register = findViewById(R.id.ic_back_arrow_register);
        ic_back_arrow_register.setOnClickListener(this);
    }

    // Đăng ký tài khoản với firebase
    public void registerAccount() {

        String email = edtRegisterEmail.getText().toString().trim();
        String password = edtRegisterPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            Date startDate = new Date();
                            long accountId = startDate.getTime();

                            System.out.println(startDate);

                            // thông báo đăng ký tài khỏa thành công
                            Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thành công !",
                                    Toast.LENGTH_SHORT).show();

                            // tạo một đối tượng account
                            Accounts accounts = new Accounts(accountId, "user"+ accountId, "story", "avatarUrl", email, password, "user", "enable");

                            // gọi hàm thêm dữ liệu vào firebase
                            assert user != null;
                            reference.child("Accounts").child(user.getUid()).setValue(accounts);

                            // chuyển đến trang chủ
                            Intent intentMain = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intentMain);
                            finish();

                        } else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thất bại, vui lòng thử lại sau !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:


                registerAccount();


                // lấy giá trị người dùng nhập để kiểm tra
                String email = edtRegisterEmail.getText().toString().trim();
                String password = edtRegisterPassword.getText().toString().trim();
                String confirmPassword = edtRegisterConfirmPassword.getText().toString().trim();

                if (accountService.checkEmptyRegister(tvErrorEmail, tvErrorPassword, tvErrorConfirmPassword, email, password, confirmPassword)) {
                    // thỏa mãn đồng thời 3 điều kiện thì cho phép tạo tài khoản
                    if (accountService.checkEmail(tvErrorEmail, email)
                            && accountService.checkPasswordStrong(tvErrorPassword, password)
                            && accountService.checkConfirmPassword(tvErrorConfirmPassword, password, confirmPassword)) {
//                        registerAccount();
                    }
                }
                break;
            case R.id.ic_back_arrow_register:
                this.finish();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        String passwordRegister = edtRegisterPassword.getText().toString().trim();
        String confirmPasswordRegister = edtRegisterConfirmPassword.getText().toString().trim();

        if (charSequence == edtRegisterEmail.getText()) {
            String emailRegister = edtRegisterEmail.getText().toString().trim();
            accountService.checkEmail(tvErrorEmail, emailRegister);
        }
        if (charSequence == edtRegisterPassword.getText()) {
            accountService.checkPasswordStrong(tvErrorPassword, passwordRegister);
            if (!confirmPasswordRegister.isEmpty())
                accountService.checkConfirmPassword(tvErrorConfirmPassword, passwordRegister, confirmPasswordRegister);
        }
        if (charSequence == edtRegisterConfirmPassword.getText()) {
            accountService.checkConfirmPassword(tvErrorConfirmPassword, passwordRegister, confirmPasswordRegister);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}