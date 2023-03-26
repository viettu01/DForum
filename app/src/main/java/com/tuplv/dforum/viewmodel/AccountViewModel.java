package com.tuplv.dforum.viewmodel;

import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuplv.dforum.model.Accounts;
import com.tuplv.dforum.view.activity.LoginActivity;
import com.tuplv.dforum.view.activity.MainActivity;

import java.util.Date;
import java.util.HashMap;

public class AccountViewModel extends ViewModel {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference reference = database.getReference();

    Context context;

    public AccountViewModel(Context context) {
        this.context = context;
    }

    // Gửi email xác minh tài khoản
    public void sendEmailVerification(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Kiểm tra email của bạn để xác minh tài khoản", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    // Đăng ký tài khoản với firebase
    public void registerAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendEmailVerification(user);

                            Date startDate = new Date();
                            long accountId = startDate.getTime();

                            // tạo một đối tượng account
                            Accounts accounts = new Accounts(accountId, "user" + accountId, "null", "null", email, password, "user", STATUS_ENABLE);

                            // gọi hàm thêm dữ liệu vào firebase
                            assert user != null;
                            reference.child("Accounts").child(user.getUid()).setValue(accounts);

                            // Chuyển trang
                            context.startActivity(new Intent(context, LoginActivity.class));
                            ((Activity) context).finish();

                        } else {
                            Toast.makeText(context, "Email đã được sử dụng !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            // kiểm tra email đã xác minh hay chưa
                            if (user != null) {
                                boolean emailVerified = user.isEmailVerified();
                                if (emailVerified) {
                                    context.startActivity(new Intent(context, MainActivity.class));
                                    ((Activity) context).finish();
                                } else
                                    Toast.makeText(context, "Để đăng nhập hãy xác minh Email trước", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void changePasswordToFirebaseAuthentication(String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Đổi mật khẩu
    public void changePasswordToFirebaseRealtime(String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        // Tạo một HashMap chứa thuộc tính muốn cập nhật và giá trị mới
        HashMap<String, Object> updatePassword = new HashMap<>();
        updatePassword.put("password", newPassword);

        // Sử dụng phương thức updateChildren() để cập nhật thuộc tính cảu Account
        reference.child("Accounts").child(uid).updateChildren(updatePassword);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show();
        context.startActivity(new Intent(context, LoginActivity.class));
    }

}
