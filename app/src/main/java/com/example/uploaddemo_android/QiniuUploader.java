package com.example.uploaddemo_android;

import android.content.ContentResolver;
import android.net.Uri;

import com.qiniu.android.common.AutoZone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.FileRecorder;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class QiniuUploader {

    private UploadManager uploadManager;
    private UploadOptions options;

    public QiniuUploader() {
        Recorder recorder = null;
        try {
            recorder = new FileRecorder(Utils.sdkDirectory() + "/recorder");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 断点续传信息保存时使用的 key 的生成器，根据 key 可以获取相应文件的上传信息
        // 需要确保每个文件的 key 是唯一的，下面为默认值
        KeyGenerator keyGenerator = new KeyGenerator() {
            @Override
            public String gen(String key, File file) {
                return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
            }

            @Override
            public String gen(String key, String sourceId) {
                if (sourceId == null) {
                    sourceId = "";
                }
                return key + "_._" + sourceId;
            }
        };


        Configuration configuration = new Configuration.Builder()
                // 使用分片 V2
                .resumeUploadVersion(Configuration.RESUME_UPLOAD_VERSION_V2)
                // 开启分片上传
                .useConcurrentResumeUpload(true)
                // 上传文件大于 4M 采用分片上传
                .putThreshold(4 * 1024 * 1024)
                // 文件使用分片上传时，此文件并发上传的块数量
                .concurrentTaskCount(3)
                // 根据 token 中的 bucket 信息自动获取相应的区域
                .zone(new AutoZone())
                // 请求连接超时
                .connectTimeout(15)
                // 请求读超时
                .responseTimeout(20)
                // 请求写超时
                .writeTimeout(40)
                // 文件分片上传时断点续传信息保存
                .recorder(recorder, keyGenerator)
                .build();

        uploadManager = new UploadManager(configuration);
    }

    public void upload(Uri uri,
                       ContentResolver resolver,
                       String key,
                       String token,
                       UpProgressHandler progressHandler,
                       UpCancellationSignal cancellationHandler,
                       UpCompletionHandler completionHandler) {
        UploadOptions options = new UploadOptions(null, null, true,
                progressHandler, cancellationHandler);
        uploadManager.put(uri, resolver, key, token, completionHandler, options);
    }

    public void upload(String filePath,
                       String key,
                       String token,
                       UpProgressHandler progressHandler,
                       UpCancellationSignal cancellationHandler,
                       UpCompletionHandler completionHandler) {
        UploadOptions options = new UploadOptions(null, null, true,
                progressHandler, cancellationHandler);
        uploadManager.put(filePath, key, token, completionHandler, options);
    }

    public void upload(File file,
                       String key,
                       String token,
                       UpProgressHandler progressHandler,
                       UpCancellationSignal cancellationHandler,
                       UpCompletionHandler completionHandler) {
        UploadOptions options = new UploadOptions(null, null, true,
                progressHandler, cancellationHandler);
        uploadManager.put(file, key, token, completionHandler, options);
    }

    public void upload(byte[] data,
                       String key,
                       String token,
                       UpProgressHandler progressHandler,
                       UpCancellationSignal cancellationHandler,
                       UpCompletionHandler completionHandler) {
        UploadOptions options = new UploadOptions(null, null, true,
                progressHandler, cancellationHandler);
        uploadManager.put(data, key, token, completionHandler, options);
    }

    public void upload(InputStream stream,
                       long size,
                       String sourceId,
                       String key,
                       String fileName,
                       String token,
                       UpProgressHandler progressHandler,
                       UpCancellationSignal cancellationHandler,
                       UpCompletionHandler completionHandler) {
        UploadOptions options = new UploadOptions(null, null, true,
                progressHandler, cancellationHandler);
        uploadManager.put(stream, sourceId, size, fileName, key, token, completionHandler, options);
    }
}
