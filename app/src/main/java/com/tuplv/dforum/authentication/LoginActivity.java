package com.tuplv.dforum.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tuplv.dforum.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvRegister;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getViews();

        // khởi tạo xác thực firebase
        mAuth = FirebaseAuth.getInstance();
    }
    private void getViews(){
        tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(this);
    }
    public void login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRegister:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                this.finish();
                startActivity(intent);
                break;
//            case R.id.btnLogin:
//                email = txtEmail.getText().toString();
//                password = txtPassword.getText().toString();
//                if (!email.isEmpty() || !password.isEmpty()) {
//                    signInID(email, password);
//                }
//                break;
        }
    }
}