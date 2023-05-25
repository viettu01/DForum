package com.tuplv.dforum.activity.account;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tuplv.dforum.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ShowAvatarActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imvBack, imvDownload;
    PhotoView imvAvatarFullSize;
    RelativeLayout rlShowMenu;
    boolean show = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_avatar);

        init();


    }

    @Override
    protected void onResume() {
        super.onResume();
        String avatarUri = String.valueOf(getIntent().getSerializableExtra("avatarUri"));

        if (avatarUri.equals("null")) {
            imvAvatarFullSize.setImageResource(R.drawable.no_avatar);
        } else {
            Glide.with(this)
                    .load(avatarUri)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.no_avatar) // Ảnh hiển thị mặc định trong quá trình tải
                            .error(R.drawable.no_avatar)) // Ảnh hiển thị khi xảy ra lỗi
                    .into(imvAvatarFullSize);
//            Picasso.get().load(avatarUri).into(imvAvatarFullSize);
        }

    }

    private void init() {
        imvBack = findViewById(R.id.imvBack);
        imvBack.setOnClickListener(this);

        imvDownload = findViewById(R.id.imvDownload);
        imvDownload.setOnClickListener(this);

        imvAvatarFullSize = findViewById(R.id.imvAvatarFullSize);
        imvAvatarFullSize.setOnClickListener(this);

        rlShowMenu = findViewById(R.id.rlShowMenu);
    }

    // Lưu ảnh về máy
    private void saveAvatarToGallery(Bitmap bitmap) {
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
                Toast.makeText(this, "Đã lưu ảnh", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Đã lưu ảnh", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadAvatar(String uri) {
        Picasso.get().load(uri).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // Lưu ảnh xuống bộ nhớ
                saveAvatarToGallery(bitmap);
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
            case R.id.imvBack:
                finish();
                break;
            case R.id.imvDownload:
                downloadAvatar(String.valueOf(getIntent().getSerializableExtra("avatarUri")));
                break;
            case R.id.imvAvatarFullSize:
                if (show)
                    rlShowMenu.setVisibility(View.VISIBLE);
                else
                    rlShowMenu.setVisibility(View.GONE);
                show = !show;
                break;
        }
    }
}