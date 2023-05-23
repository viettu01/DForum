package com.tuplv.dforum.activity.account;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.ROLE_USER;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Account;

import java.util.Date;
import java.util.regex.Pattern;

@SuppressLint("SetTextI18n")
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    TextView tvErrorEmail, tvErrorPassword, tvErrorConfirmPassword;
    EditText edtRegisterEmail, edtRegisterPassword, edtRegisterConfirmPassword;
    Button btnRegister;
    ImageView ic_back_arrow_register;
    private String email, password, confirmPassword;

    //firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
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

    // Lấy giá trị người dùng nhập trên view
    private void getText() {
        email = edtRegisterEmail.getText().toString().trim();
        password = edtRegisterPassword.getText().toString().trim();
        confirmPassword = edtRegisterConfirmPassword.getText().toString().trim();
    }

    // Gửi email xác minh tài khoản về email đã đăng ký
    private void sendEmailVerification(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Kiểm tra email của bạn để xác minh tài khoản", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // kiểm tra độ mạnh mật khẩu
    private boolean checkPasswordStrength() {
        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern lowercase = Pattern.compile("[a-z]");
        Pattern number = Pattern.compile("[0-9]");
        Pattern specialCharacter = Pattern.compile("[,.!@+#$&]");

        if (password.length() < 8
                || !specialCharacter.matcher(password).find()
                || !uppercase.matcher(password).find()
                || !lowercase.matcher(password).find()
                || !number.matcher(password).find()) {
            tvErrorPassword.setText("Mật khẩu phải có 8 ký tự đủ chữ hoa, chữ thường, chữ số và ký tự đặc biệt !");
            tvErrorPassword.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvErrorPassword.setVisibility(View.GONE);
            return true;
        }
    }

    // kiểm tra định dạng email
    private boolean checkEmail() {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvErrorEmail.setText("Không phải địa chỉ email !");
            tvErrorEmail.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvErrorEmail.setVisibility(View.GONE);
            return true;
        }
    }

    // kiểm tra mật khẩu và xác nhận mật khẩu
    private boolean checkConfirmPassword() {
        if (!password.equals(confirmPassword)) {
            tvErrorConfirmPassword.setText("Mật khẩu không trùng khớp !");
            tvErrorConfirmPassword.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvErrorConfirmPassword.setVisibility(View.GONE);
            return true;
        }
    }

    // Kiểm tra trống các trường
    private boolean checkEmptyRegister() {
        if (email.isEmpty()) {
            tvErrorEmail.setText("Không được để trống");
            tvErrorEmail.setVisibility(View.VISIBLE);
        }
        if (password.isEmpty()) {
            tvErrorPassword.setText("Không được để trống");
            tvErrorPassword.setVisibility(View.VISIBLE);
        }
        if (confirmPassword.isEmpty()) {
            tvErrorConfirmPassword.setText("Không được để trống");
            tvErrorConfirmPassword.setVisibility(View.VISIBLE);
        }
        return !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty();
    }

    // Đăng ký tài khoản với firebase
    private void register() {
        getText();
        if (checkEmptyRegister()) {
           // if (checkEmail() && checkPasswordStrength() && checkConfirmPassword()) {
            if (checkEmail() && checkConfirmPassword()) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    sendEmailVerification(user);

                                    assert user != null;
                                    long createdDate = new Date().getTime();
                                    //khởi tạo một đối tượng account
                                    Account account = new Account(user.getUid(), "user" + createdDate, "null", email, ROLE_USER, STATUS_ENABLE, createdDate, 0, 0);

                                    // gọi hàm thêm dữ liệu vào firebase
                                    reference.child(OBJ_ACCOUNT).child(user.getUid()).setValue(account);

                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Email đã được sử dụng!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                register();
                break;
            case R.id.ic_back_arrow_register:
                finish();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        getText();

        if (charSequence == edtRegisterEmail.getText()) {
            checkEmail();
        }
        if (charSequence == edtRegisterPassword.getText()) {
            checkPasswordStrength();
            if (!confirmPassword.isEmpty())
                checkConfirmPassword();
        }
        if (charSequence == edtRegisterConfirmPassword.getText()) {
            checkConfirmPassword();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}