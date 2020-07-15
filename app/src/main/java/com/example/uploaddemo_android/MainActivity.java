package com.example.uploaddemo_android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.wildma.pictureselector.PictureSelector;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar = null;
    private ImageView imageView = null;
    private Button selectBtn = null;
    private Button uploadBtn = null;

    private String imagePath = null;

    private UploadManager uploadManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initUI();
    }

    private void initUI(){
        progressBar = findViewById(R.id.upload_progress);
        imageView = findViewById(R.id.upload_image);
        selectBtn = findViewById(R.id.upload_btn_select);
        uploadBtn = findViewById(R.id.upload_btn_upload);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }

    private void initData(){
        uploadManager = new UploadManager();
    }


    private void selectImage(){
        PictureSelector
                .create(MainActivity.this, PictureSelector.SELECT_REQUEST_CODE)
                .selectPicture();
    }

    private void uploadImage(){
        String token = "your token";
        token = "jH983zIUFIP1OVumiBVGeAfiLYJvwrF45S-t22eu:DtOhccYARFhzC4cpxtPaclI5sPU=:eyJzY29wZSI6InpvbmUwLXNwYWNlIiwiZGVhZGxpbmUiOjE1OTczOTMzOTYsICJyZXR1cm5Cb2R5Ijoie1wiZm9vXCI6JCh4OmZvbyksIFwiYmFyXCI6JCh4OmJhciksIFwibWltZVR5cGVcIjokKG1pbWVUeXBlKSwgXCJoYXNoXCI6JChldGFnKSwgXCJrZXlcIjokKGtleSksIFwiZm5hbWVcIjokKGZuYW1lKX0ifQ==";
        Map<String, String> params = new HashMap<String, String>();
        params.put("x:foo", "foo");
        params.put("x:bar", "bar");

        final UploadOptions options = new UploadOptions(params, null, true, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                progressBar.setProgress((int)(percent * 100));
            }
        }, null);

        uploadManager.put(imagePath, null, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                showMessage(info.toString());
            }
        }, options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PictureSelector.SELECT_REQUEST_CODE){
            if (data != null){
                String imagePath = data.getStringExtra(PictureSelector.PICTURE_PATH);
                Bitmap bm = BitmapFactory.decodeFile(imagePath);
                imageView.setImageBitmap(bm);

                this.imagePath = imagePath;
            }
        }
    }

    private void showMessage(String message){
        new AlertDialog.Builder(this)
                .setTitle("请求结果：")
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }
}
