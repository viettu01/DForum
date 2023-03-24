package com.tuplv.dforum.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuplv.dforum.R;
import com.tuplv.dforum.viewmodel.AccountViewModel;

import java.util.regex.Pattern;

@SuppressLint("SetTextI18n")
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    TextView tvErrorEmail, tvErrorPassword, tvErrorConfirmPassword;
    EditText edtRegisterEmail, edtRegisterPassword, edtRegisterConfirmPassword;
    Button btnRegister;
    ImageView ic_back_arrow_register;
    private String email, password, confirmPassword;
    private AccountViewModel accountViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // ánh xạ
        init();
        // khai báo account service
        accountViewModel = new AccountViewModel(this);
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
    private void getText(){
        email = edtRegisterEmail.getText().toString().trim();
        password = edtRegisterPassword.getText().toString().trim();
        confirmPassword = edtRegisterConfirmPassword.getText().toString().trim();
    }

    // kiểm tra độ mạnh mật khẩu
    private boolean checkPasswordStrength() {
        Pattern chuHoa = Pattern.compile("[A-Z]");
        Pattern chuThuong = Pattern.compile("[a-z]");
        Pattern chuSo = Pattern.compile("[0-9]");
        Pattern kyTu = Pattern.compile("[,.!@+#$&]");

        if (password.length() <= 8
                || !kyTu.matcher(password).find()
                || !chuHoa.matcher(password).find()
                || !chuThuong.matcher(password).find()
                || !chuSo.matcher(password).find()) {
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

    private void register(){
        getText();
        if (checkEmptyRegister()) {
            if (checkEmail() && checkPasswordStrength() && checkConfirmPassword()) {
                accountViewModel.registerAccount(email,password);
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
                this.finish();
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