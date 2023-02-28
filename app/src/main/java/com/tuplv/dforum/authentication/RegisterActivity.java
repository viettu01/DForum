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

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvErrorEmail, tvErrorPassword, tvErrorConfirmPassword;
    EditText edtRegisterEmail, edtRegisterPassword, edtRegisterConfirmPassword;
    Button btnRegister;
    ImageView ic_back_arrow_register;
    private FirebaseAuth mAuth;

    // khai báo firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ánh xạ
        getViews();

        // khởi tạo xác thực firebase
        mAuth = FirebaseAuth.getInstance();

        // ẩn textview báo lỗi
        tvErrorEmail.setVisibility(View.GONE);
        tvErrorPassword.setVisibility(View.GONE);
        tvErrorConfirmPassword.setVisibility(View.GONE);

        edtRegisterEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String emailRegister = edtRegisterEmail.getText().toString().trim();
                checkEmail(emailRegister);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // bắt sự kiện thay đổi mật khẩu để kiểm tra
        edtRegisterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String passwordRegister = edtRegisterPassword.getText().toString().trim();
                checkPasswordStrong(passwordRegister);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtRegisterConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String confirmPasswordRegister = edtRegisterConfirmPassword.getText().toString().trim();
                String passwordRegister = edtRegisterPassword.getText().toString().trim();
                checkConfirmPassword(passwordRegister, confirmPasswordRegister);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getViews() {
        tvErrorEmail = findViewById(R.id.tvErrorEmail);
        tvErrorPassword = findViewById(R.id.tvErrorPassword);
        tvErrorConfirmPassword = findViewById(R.id.tvErrorConfirmPassword);

        edtRegisterEmail = findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword);
        edtRegisterConfirmPassword = findViewById(R.id.edtRegisterConfirmPassword);

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
                            assert user != null;
                            String accountId = user.getUid();

                            // thông báo đăng ký tài khỏa thành công
                            Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thành công !",
                                    Toast.LENGTH_SHORT).show();

                            // tạo mới đối tượng account
                            Accounts accounts = new Accounts(accountId, "nickName", "story", "avatarUrl", email, password, "user", "enable", "abc");

                            // gọi hàm thêm dữ liệu vào firebase
                            insertAccountToFirebase(accounts, accountId);

                            // chuyển đến trang chủ
                            Intent intentMain = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intentMain);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thất bại, vui lòng thử lại sau !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // hàm lưu dữ liệu vào firebase
    private void insertAccountToFirebase(Accounts accounts, String accountId) {
        reference.child("Accounts").child(accountId).setValue(accounts);
    }

    @SuppressLint("SetTextI18n")
    private boolean checkPasswordStrong(String password) {
        Pattern chuHoa = Pattern.compile("[A-Z]");
        Pattern chuThuong = Pattern.compile("[a-z]");
        Pattern chuSo = Pattern.compile("[0-9]");
        Pattern kyTu = Pattern.compile("[,.!@+#$&]");

        if (password.length() <= 8 || !kyTu.matcher(password).find() || !chuHoa.matcher(password).find() || !chuThuong.matcher(password).find() || !chuSo.matcher(password).find()) {
            tvErrorPassword.setText("Mật khẩu phải có 8 ký tự đủ chữ hoa, chữ thường, chữ số và ký tự đặc biệt!");
            tvErrorPassword.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvErrorPassword.setVisibility(View.GONE);
            return true;
        }
    }

    @SuppressLint("SetTextI18n")
    private boolean checkEmail(String emailRegister) {
        if (!Patterns.EMAIL_ADDRESS.matcher(emailRegister).matches()) {
            tvErrorEmail.setText("Không phải địa chỉ email");
            tvErrorEmail.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvErrorEmail.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean checkConfirmPassword(String password, String confirmPassword) {
        return !password.equals(confirmPassword);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                // lấy giá trị người dùng nhập để kiểm tra
                String email = edtRegisterEmail.getText().toString().trim();
                String password = edtRegisterPassword.getText().toString().trim();
                String confirmPassword = edtRegisterConfirmPassword.getText().toString().trim();

                // thỏa mãn đồng thời 3 điều kiện thì cho phép tạo tài khoản
                if (checkEmail(email) && checkPasswordStrong(password) && checkConfirmPassword(password, confirmPassword)) {
                    registerAccount();
                }
                break;
            case R.id.ic_back_arrow_register:
                this.finish();
                break;
        }
    }
}