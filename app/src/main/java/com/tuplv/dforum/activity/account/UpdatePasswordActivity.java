package com.tuplv.dforum.activity.account;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tuplv.dforum.R;

import java.util.Objects;
import java.util.regex.Pattern;

public class UpdatePasswordActivity extends AppCompatActivity implements TextWatcher {

    EditText edtOldPassword, edtNewPassword, edtConfirmNewPassword;
    TextView tvErrorOldPassword, tvErrorNewPassword, tvErrorConfirmNewPassword;
    Button btnCancel, btnUpdatePassword;
    Toolbar tbUpdatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        init();
        tbUpdatePassword.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
    }

    private void init() {
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtOldPassword.addTextChangedListener(this);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtNewPassword.addTextChangedListener(this);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);
        edtConfirmNewPassword.addTextChangedListener(this);

        tvErrorOldPassword = findViewById(R.id.tvErrorOldPassword);
        tvErrorNewPassword = findViewById(R.id.tvErrorNewPassword);
        tvErrorConfirmNewPassword = findViewById(R.id.tvErrorConfirmNewPassword);

        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        btnCancel = findViewById(R.id.btnCancel);

        tbUpdatePassword = findViewById(R.id.tbUpdatePassword);
        setSupportActionBar(tbUpdatePassword);
    }

    private void changePassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (checkEmptyUpdatePassword()) {
            if (checkNewPasswordStrength() && checkConfirmNewPassword()) {
                // Kiểm tra mật khẩu cũ
                assert user != null;
                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), edtOldPassword.getText().toString().trim());

                // Yêu cầu người dùng xác thực lại danh tính bằng mật khẩu hiện tại của họ
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Người dùng đã xác thực lại danh tính thành công
                                    // Cập nhật mật khẩu mới của người dùng
                                    user.updatePassword(edtNewPassword.getText().toString().trim())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Mật khẩu đã được cập nhật thành công
                                                        Toast.makeText(UpdatePasswordActivity.this, "Mật khẩu đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        // Có lỗi xảy ra khi cập nhật mật khẩu
                                                        Toast.makeText(UpdatePasswordActivity.this, "Có lỗi xảy ra khi cập nhật mật khẩu", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // Có lỗi xảy ra khi xác thực lại danh tính
                                    tvErrorOldPassword.setText("Mật khẩu cũ không đúng");
                                    tvErrorOldPassword.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private boolean checkNewPasswordStrength() {
        String newPassword = edtNewPassword.getText().toString().trim();
        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern lowercase = Pattern.compile("[a-z]");
        Pattern number = Pattern.compile("[0-9]");
        Pattern specialCharacter = Pattern.compile("[,.!@+#$&]");

        if (newPassword.length() < 8
                || !specialCharacter.matcher(newPassword).find()
                || !uppercase.matcher(newPassword).find()
                || !lowercase.matcher(newPassword).find()
                || !number.matcher(newPassword).find()) {
            tvErrorNewPassword.setText("Mật khẩu phải có 8 ký tự đủ chữ hoa, chữ thường, chữ số và ký tự đặc biệt !");
            tvErrorNewPassword.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvErrorNewPassword.setVisibility(View.GONE);
            return true;
        }
    }

    @SuppressLint("SetTextI18n")
    private boolean checkConfirmNewPassword() {
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmNewPassword = edtConfirmNewPassword.getText().toString().trim();
        if (!newPassword.equals(confirmNewPassword)) {
            tvErrorConfirmNewPassword.setText("Mật khẩu không trùng khớp !");
            tvErrorConfirmNewPassword.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvErrorConfirmNewPassword.setVisibility(View.GONE);
            return true;
        }
    }

    // Kiểm tra trống các trường
    @SuppressLint("SetTextI18n")
    private boolean checkEmptyUpdatePassword() {
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmNewPassword = edtConfirmNewPassword.getText().toString().trim();
        if (oldPassword.isEmpty()) {
            tvErrorOldPassword.setText("Không được để trống");
            tvErrorOldPassword.setVisibility(View.VISIBLE);
        }
        if (newPassword.isEmpty()) {
            tvErrorNewPassword.setText("Không được để trống");
            tvErrorNewPassword.setVisibility(View.VISIBLE);
        }
        if (confirmNewPassword.isEmpty()) {
            tvErrorConfirmNewPassword.setText("Không được để trống");
            tvErrorConfirmNewPassword.setVisibility(View.VISIBLE);
        }
        return !oldPassword.isEmpty() && !newPassword.isEmpty() && !confirmNewPassword.isEmpty();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String oldPassword = edtOldPassword.getText().toString().trim();
        String confirmNewPassword = edtConfirmNewPassword.getText().toString().trim();
        if (charSequence == edtOldPassword.getText()) {
            if (!oldPassword.isEmpty())
                tvErrorOldPassword.setVisibility(View.GONE);
        }
        if (charSequence == edtNewPassword.getText()) {
            checkNewPasswordStrength();
            if (!confirmNewPassword.isEmpty())
                checkConfirmNewPassword();
        }
        if (charSequence == edtConfirmNewPassword.getText()) {
            checkConfirmNewPassword();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}