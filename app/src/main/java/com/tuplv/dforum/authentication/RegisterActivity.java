package com.tuplv.dforum.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuplv.dforum.R;

public class RegisterActivity extends AppCompatActivity{

    TextView tvErrorEmail, tvErrorPassword, tvErrorPasswordSecond;
    EditText edtRegisterEmail, edtRegisterPassword, edtRegisterPasswordSecond;
    Button btnRegister;
    private FirebaseAuth mAuth;
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
        tvErrorPasswordSecond.setVisibility(View.GONE);

        // bắt sự kiện click button đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAccount(edtRegisterEmail.getText().toString().trim(), edtRegisterPassword.getText().toString().trim());
            }
        });
    }

    private void getViews(){
        tvErrorEmail = findViewById(R.id.tvErrorEmail);
        tvErrorPassword = findViewById(R.id.tvErrorPassword);
        tvErrorPasswordSecond = findViewById(R.id.tvErrorPasswordSecond);

        edtRegisterEmail = findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword);
        edtRegisterPasswordSecond = findViewById(R.id.edtRegisterPasswordSecond);

        btnRegister = findViewById(R.id.btnRegister);
    }

    // Đăng ký tài khoản với firebase
    public void registerAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
////                            updateUI(null);
                        }
                    }
                });
    }
}