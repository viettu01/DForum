package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.PICK_IMAGE_REQUEST;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tuplv.dforum.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class ShowImageActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ic_back_arrow_show_image, download_image, image_full_size;
    RelativeLayout show_menu;
    boolean show = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        init();

        String avatarUri = String.valueOf(getIntent().getSerializableExtra("avatarUri"));
        if (avatarUri.equals("null")) {
            image_full_size.setImageResource(R.drawable.no_avatar);
        } else
            Picasso.get().load(avatarUri).into(image_full_size);
    }

    private void init() {
        ic_back_arrow_show_image = findViewById(R.id.ic_back_arrow_show_image);
        ic_back_arrow_show_image.setOnClickListener(this);

        download_image = findViewById(R.id.download_image);
        download_image.setOnClickListener(this);

        image_full_size = findViewById(R.id.image_full_size);
        image_full_size.setOnClickListener(this);

        show_menu =findViewById(R.id.show_menu);
    }

    private void saveImageToGallery(Bitmap bitmap) {
        String filename = "image_" + System.currentTimeMillis() + ".jpg"; // Tạo tên file
        OutputStream fos; // Khai báo OutputStream để ghi ảnh xuống
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Kiểm tra phiên bản Android có hỗ trợ Storage Access Framework hay không
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "DForum");
            Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                fos = getContentResolver().openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                Toast.makeText(this, "Saved to gallery", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        } else { // Nếu không hỗ trợ Storage Access Framework, sử dụng phương thức truyền thống để lưu ảnh xuống bộ nhớ
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File myDir = new File(root + File.separator + "DForum");
            myDir.mkdirs();
            File file = new File(myDir, filename);
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                Toast.makeText(this, "Saved to gallery", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadImage(String uri){
        Picasso.get().load(uri).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // Lưu ảnh xuống bộ nhớ
                saveImageToGallery(bitmap);
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_back_arrow_show_image:
                finish();
                break;
            case R.id.download_image:
                downloadImage(String.valueOf(getIntent().getSerializableExtra("avatarUri")));
                break;
            case R.id.image_full_size:
                if(!show){
                    show_menu.setVisibility(View.GONE);
                    show = true;
                }
                else if(show){
                    show_menu.setVisibility(View.VISIBLE);
                    show = false;
                }
                break;
        }
    }
}