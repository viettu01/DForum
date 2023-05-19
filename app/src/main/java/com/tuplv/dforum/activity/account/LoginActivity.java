package com.tuplv.dforum.activity.account;

import static com.tuplv.dforum.until.Constant.LOCK_DURATION_MS;
import static com.tuplv.dforum.until.Constant.MAX_LOGIN_ATTEMPTS;
import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;
import static com.tuplv.dforum.until.Constant.ROLE_USER;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.main.AdminMainActivity;
import com.tuplv.dforum.activity.main.UserMainActivity;
import com.tuplv.dforum.model.Account;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvRegister, tvForgotPassword;
    Button btnLogin;
    EditText edtLoginEmail, edtLoginPassword;
    ImageView ic_back_arrow_login;
    ProgressDialog progressDialog;
    private String email, password, message;
    int count = 0;
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
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Đang đăng nhập");

        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
    }

    private void getText() {
        email = edtLoginEmail.getText().toString().trim();
        password = edtLoginPassword.getText().toString().trim();
    }

    @SuppressLint("SimpleDateFormat")
    private void login(String accountId, String status, String role, long countLoginFail, long lockedDate) {
        if (new Date().getTime() >= (lockedDate + LOCK_DURATION_MS))
            updateStatus(accountId, STATUS_ENABLE);
        else if (status.equals(STATUS_DISABLE)) {
            Date date = new Date(lockedDate + LOCK_DURATION_MS);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy");
            String unlockDate = simpleDateFormat.format(date);
            alertNotify("Tài khoản của bạn đã bị khóa trong " + LOCK_DURATION_MS / (60000) + " phút đến " + unlockDate);

            progressDialog.dismiss();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                checkRole(role);
                                updateCountLoginFailAndLockedDate("countLoginFail", accountId, 0);
                            } else {
                                Toast.makeText(LoginActivity.this, "Xác minh email của bạn trước !", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException) exception).getErrorCode();
                                if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
                                    lockAccountFiveMinute(countLoginFail, accountId, new Date().getTime());
                                    alertNotify(message);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Đã xảy ra lỗi không xác định thử lại sau !", Toast.LENGTH_SHORT).show();
                                }
                            } else if (exception instanceof FirebaseTooManyRequestsException) {
                                lockAccountFiveMinute(countLoginFail, accountId, new Date().getTime());
                                alertNotify(message);
                            } else {
                                Toast.makeText(LoginActivity.this, "lỗi khác!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }


    @SuppressLint("SimpleDateFormat")
    private void lockAccountFiveMinute(long countLoginFail, String accountId, long lockedDate) {
        if (countLoginFail + 1 >= MAX_LOGIN_ATTEMPTS) {

            Date date = new Date(lockedDate + LOCK_DURATION_MS);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy");
            String unlockDate = simpleDateFormat.format(date);

            message = "Bạn đã nhập sai " + (countLoginFail + 1) + " lần liên tiếp. Tài khoản của bạn đã bị khóa trong " + (LOCK_DURATION_MS / 60000) + " phút đến " + unlockDate;
            updateCountLoginFailAndLockedDate("lockedDate", accountId, new Date().getTime());

            updateCountLoginFailAndLockedDate("countLoginFail", accountId, 0);
            updateStatus(accountId, STATUS_DISABLE);
        } else {
            message = "Bạn đã nhập sai " + (countLoginFail + 1) + " lần liên tiếp. Đến lần thứ " + MAX_LOGIN_ATTEMPTS + " tài khoản của bạn sẽ bị khóa.";
            updateCountLoginFailAndLockedDate("countLoginFail", accountId, countLoginFail + 1);
        }
    }

    private void getInfoLockAccount(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            reference.child(OBJ_ACCOUNT).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                @Override
                public void onDataChange(@NonNull DataSnapshot dsPost) {
                    for (DataSnapshot dataSnapshot : dsPost.getChildren()) {
                        Account account = dataSnapshot.getValue(Account.class);
                        if (account != null && account.getEmail().equals(email)) {
                            String accountId = account.getAccountId();
                            String role = account.getRole();
                            String status = account.getStatus();
                            long countLoginFail = account.getCountLoginFail();
                            long lockedDate = account.getLockedDate();

                            count++;

                            login(accountId, status, role, countLoginFail, lockedDate);
                            break;
                        }
                    }
                    if (count == 0) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Tài khoản email này không tồn tại !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Không được để trống Email và mật khẩu !", Toast.LENGTH_SHORT).show();
        }
    }

    public void alertNotify(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cảnh báo!");
        builder.setIcon(R.drawable.ic_round_warning_yellow_24);
        builder.setMessage(message);
        builder.setNegativeButton("Đóng", (dialog, which) -> {});
        builder.show();
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

    // Lấy quyền và trạng thái tài khoản muốn đăng nhập để kiểm tra
    private void checkRole(String role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("role", role);
        editor.apply();

        if (role.equals(ROLE_ADMIN))
            startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
        if (role.equals(ROLE_USER))
            startActivity(new Intent(LoginActivity.this, UserMainActivity.class));
        finish();
    }

    // Đóng bàn phím
    private void closeKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView == null) {
            inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        } else
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    // cập nhật trạng thái đang đăng nhập và trạng thái tài khoản
    private void updateStatus(String accountId, String value) {
        HashMap<String, Object> updateStatus = new HashMap<>();
        updateStatus.put("status", value);
        reference.child(OBJ_ACCOUNT).child(accountId).updateChildren(updateStatus);
    }

    // cập nhật số lần đăng nhập sai và thời gian bị khóa tài khoản
    private void updateCountLoginFailAndLockedDate(String attribute, String accountId, long value) {
        HashMap<String, Object> updateCountLoginFail = new HashMap<>();
        updateCountLoginFail.put(attribute, value);
        reference.child(OBJ_ACCOUNT).child(accountId).updateChildren(updateCountLoginFail);
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
                closeKeyBoard();
                progressDialog.show();
                getText();
                getInfoLockAccount(email, password);
                break;
        }
    }
}