package com.example.uploaddemo_android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;

import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private TextView progressTV = null;
    private ProgressBar progressBar = null;
    private ImageView imageView = null;
    private VideoView videoView = null;
    private Button selectBtn = null;
    private Button uploadBtn = null;
    private Button cancelBtn = null;

    private boolean isCancel = false;
    private String mediaPath = null;
    private QiniuUploader uploader = new QiniuUploader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI() {
        progressTV = findViewById(R.id.upload_progress_label);
        progressBar = findViewById(R.id.upload_progress);
        imageView = findViewById(R.id.upload_image);
        videoView = findViewById(R.id.upload_video);
        videoView.getBackground().setAlpha(1);
        selectBtn = findViewById(R.id.upload_btn_select);
        uploadBtn = findViewById(R.id.upload_btn_upload);
        cancelBtn = findViewById(R.id.upload_btn_cancel);

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

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelUpload();
            }
        });
    }


    private void selectImage() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofAll())
                .setImageEngine(GlideEngine.getInstance())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> mediaList) {
                        if (mediaList != null && mediaList.size() > 0) {
                            LocalMedia media = mediaList.get(0);
                            mediaPath = media.getPath();

                            if (mediaPath == null) {
                                return;
                            }

                            if (media.getMimeType().contains(PictureMimeType.MIME_TYPE_PREFIX_IMAGE)) {
                                showImage(mediaPath);
                            } else if (media.getMimeType().contains(PictureMimeType.MIME_TYPE_PREFIX_VIDEO)) {
                                showVideo(mediaPath);
                            }
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void cancelUpload() {
        isCancel = true;
    }

    private void uploadImage() {
        if (mediaPath == null) {
            mediaPath = "/sdcard/PLDroidShortVideo-master .zip";
            mediaPath = "/sdcard/thku.mp3";
            mediaPath = "/sdcard/shuishang.mp4";
            mediaPath = "/sdcard/doc.zip";
            mediaPath = "/sdcard/UploadResource_6M.zip";
        }
        isCancel = false;

        // 此处配置自己的token
        String token = Config.getToken();

        uploader.upload(mediaPath, "Android-Test", token,
                new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        progressBar.setProgress((int) (percent * 100));
                        String percentString = String.format("%.1f", percent * 100) + "%";
                        progressTV.setText(percentString);
                    }
                },
                new UpCancellationSignal() {
                    @Override
                    public boolean isCancelled() {
                        return isCancel;
                    }
                },
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        showMessage(info.toString());
                    }
                });
    }


    private void showImage(String imagePath) {
        if (imagePath == null || imagePath.length() == 0) {
            return;
        }

        imageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.INVISIBLE);
        videoView.clearFocus();
        videoView.pause();

        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bm);

    }

    private void showVideo(String videoPath) {
        if (videoPath == null || videoPath.length() == 0) {
            return;
        }

        imageView.setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.VISIBLE);
        videoView.requestFocus();
        videoView.pause();

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(videoPath);
        videoView.start();
    }

    private void showMessage(String message) {
        new AlertDialog.Builder(this)
                .setTitle("请求结果：")
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }
}
