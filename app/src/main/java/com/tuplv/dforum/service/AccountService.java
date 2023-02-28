package com.tuplv.dforum.service;

import android.annotation.SuppressLint;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Pattern;

public class AccountService {

    // kiểm tra độ mạnh mật khẩu
    @SuppressLint("SetTextI18n")
    public boolean checkPasswordStrong(TextView tvErrorPassword, String password){
        Pattern chuHoa =Pattern.compile("[A-Z]");
        Pattern chuThuong =Pattern.compile("[a-z]");
        Pattern chuSo =Pattern.compile("[0-9]");
        Pattern kyTu =Pattern.compile("[,.!@+#$&]");

        if(password.length()<=8 || !kyTu.matcher(password).find() || !chuHoa.matcher(password).find() || !chuThuong.matcher(password).find() || !chuSo.matcher(password).find()){
            tvErrorPassword.setText("Mật khẩu phải có 8 ký tự đủ chữ hoa, chữ thường, chữ số và ký tự đặc biệt !");
            tvErrorPassword.setVisibility(View.VISIBLE);
            return false;
        }else {
            tvErrorPassword.setVisibility(View.GONE);
            return true;
        }
    }

    // kiểm tra định dạng email
    @SuppressLint("SetTextI18n")
    public boolean checkEmail(TextView tvErrorEmail, String emailRegister){
        if(!Patterns.EMAIL_ADDRESS.matcher(emailRegister).matches()){
            tvErrorEmail.setText("Không phải địa chỉ email !");
            tvErrorEmail.setVisibility(View.VISIBLE);
            return false;
        }else{
            tvErrorEmail.setVisibility(View.GONE);
            return true;
        }
    }

    // kiểm tra mật khẩu và xác nhận mật khẩu
    @SuppressLint("SetTextI18n")
    public boolean checkConfirmPassword(TextView tvErrorConfirmPassword, String password, String confirmPassword){
        if(!password.equals(confirmPassword)){
            tvErrorConfirmPassword.setText("Mật khẩu không trùng khớp !");
            tvErrorConfirmPassword.setVisibility(View.VISIBLE);
            return false;
        }else{
            tvErrorConfirmPassword.setVisibility(View.GONE);
            return true;
        }
    }

    @SuppressLint("SetTextI18n")
    public boolean checkEmptyRegister(TextView tvEmail, TextView tvPassword, TextView tvConfirmPassword, String email, String password, String confirmPassword){
        if(email.isEmpty()){
            tvEmail.setText("Không được để trống");
            tvEmail.setVisibility(View.VISIBLE);
        }
        if(password.isEmpty()){
            tvPassword.setText("Không được để trống");
            tvPassword.setVisibility(View.VISIBLE);
        }
        if(confirmPassword.isEmpty()){
            tvConfirmPassword.setText("Không được để trống");
            tvConfirmPassword.setVisibility(View.VISIBLE);
        }
        return !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty();
    }
}
