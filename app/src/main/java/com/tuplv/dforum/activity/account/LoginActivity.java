package com.tuplv.dforum.activity.account;

import static com.tuplv.dforum.until.Constant.IS_LOGIN_FALSE;
import static com.tuplv.dforum.until.Constant.IS_LOGIN_TRUE;
import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;
import static com.tuplv.dforum.until.Constant.ROLE_USER;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
        //progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setMessage("Đang đăng nhập");

        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
    }

    private void getText() {
        email = edtLoginEmail.getText().toString().trim();
        password = edtLoginPassword.getText().toString().trim();
    }

    private void login(String isLogin, long countLoginFail, String accountId, String status) {
        if (!email.isEmpty() && !password.isEmpty()) {
            if (status.equals(STATUS_ENABLE)) {
                if (isLogin.equals(IS_LOGIN_FALSE)) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        // kiểm tra email đã xác minh hay chưa
                                        if (user != null) {
                                            boolean emailVerified = user.isEmailVerified();
                                            if (emailVerified) {
                                                getAccountRoleAndStatus(user);
                                                updateCountLoginFailAndLockedDate("countLoginFail", accountId, 0);
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "Xác minh email của bạn trước !", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    } else {
                                        Exception exception = task.getException();
                                        int countLock = 5;
                                        if (exception instanceof FirebaseAuthException) {
                                            String errorCode = ((FirebaseAuthException) exception).getErrorCode();
                                            if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
                                                updateCountLoginFailAndLockedDate("countLoginFail", accountId, countLoginFail + 1);
                                                message = "Bạn đã nhập sai " + (countLoginFail + 1) + " lần liên tiếp. Đến lần thứ "+countLock+" tài khoản của bạn sẽ bị khóa.";
                                                alertNotify(message);
                                                if (countLoginFail + 1 >= countLock) {
                                                    message = "Tài khoản của bạn đã bị khóa. Bạn có thể sử dụng tính năng Quên mật khẩu để mở khóa tài khoản";
                                                    alertNotify(message);
                                                    updateCountLoginFailAndLockedDate("countLoginFail", accountId, 0);
                                                    updateIsLoginAndStatus("status", accountId, STATUS_DISABLE);
                                                }
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Đã xảy ra lỗi không xác định thử lại sau !", Toast.LENGTH_SHORT).show();
                                            }
                                        } else if (exception instanceof FirebaseTooManyRequestsException) {
                                            message = "Bạn đã nhập sai " + (countLoginFail + 1) + " lần liên tiếp. Tài khoản của bạn đã bị khóa.";
                                            //alertNotify(message);
                                            Toast.makeText(LoginActivity.this, "Tài khoản đã bị khóa tạm thời", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // xử lý các trường hợp lỗi khác
                                            Toast.makeText(LoginActivity.this, "lỗi khác!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Tài khoản đang đăng nhập ở nơi khác !", Toast.LENGTH_SHORT).show();
                }
            } else {
                progressDialog.dismiss();
                message = "Tài khoản của bạn đã bị khóa. Bạn có thể sử dụng tính năng Quên mật khẩu để mở khóa tài khoản";
                alertNotify(message);
            }

        } else
            Toast.makeText(this, "Vui lòng nhập Email và mật khẩu để đăng nhập", Toast.LENGTH_SHORT).show();
    }

    private void getInfoLockAccount(String email) {
        reference.child(OBJ_ACCOUNT).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dsPost) {
                for (DataSnapshot dataSnapshot : dsPost.getChildren()) {
                    Account account = dataSnapshot.getValue(Account.class);
                    if (account != null && account.getEmail().equals(email)) {
                        String accountId = account.getAccountId();
                        String isLogin = account.getIsLogin();
                        String status = account.getStatus();
                        long countLoginFail = account.getCountLoginFail();
                        long lockedDate = account.getLockedDate();

                        count++;

                        login(isLogin, countLoginFail, accountId, status);
                        break;
                    }
                }
                if(count == 0){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Tài khoản email này không tồn tại !", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void alertNotify(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cảnh báo!");
        builder.setIcon(R.drawable.ic_round_warning_yellow_24);
        builder.setMessage(message);
        builder.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
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
    private void getAccountRoleAndStatus(FirebaseUser user) {
        reference.child(OBJ_ACCOUNT).child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String role = snapshot.child("role").getValue(String.class);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("role", role);
                            editor.apply();

                            if (role != null) {
                                if (role.equals(ROLE_ADMIN)) {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
                                }
                                if (role.equals(ROLE_USER)) {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(LoginActivity.this, UserMainActivity.class));
                                }
                                updateIsLoginAndStatus("isLogin", user.getUid(), IS_LOGIN_TRUE);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
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
    private void updateIsLoginAndStatus(String attribute, String accountId, String value) {
        HashMap<String, Object> updateIsLoginTrue = new HashMap<>();
        updateIsLoginTrue.put(attribute, value);
        reference.child(OBJ_ACCOUNT).child(accountId).updateChildren(updateIsLoginTrue);
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
                getInfoLockAccount(email);
                break;
        }
    }
}