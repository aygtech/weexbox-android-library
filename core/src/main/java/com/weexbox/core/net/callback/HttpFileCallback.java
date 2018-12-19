package com.weexbox.core.net.callback;

import com.weexbox.core.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by freeson on 16/7/29.
 */
public abstract class HttpFileCallback extends HttpCallback<File> {

    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;


    public HttpFileCallback(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    @Override
    public void inProgress(float progress, long total, int id) {
        super.inProgress(progress, total, id);
    }

    @Override
    protected boolean useInputStream() {
        return true;
    }

    @Override
    protected File decodeInputStream(InputStream inputStream, long length, int requestId) throws IOException {
        return saveFile(inputStream, length, requestId);
    }

    public File saveFile(InputStream inputStream, long length, final int requestId) throws IOException {
        InputStream is = inputStream;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            final long total = length;

            long sum = 0;

            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                    @Override
                    public void run() {

                        inProgress(finalSum * 1.0f / total, total, requestId);
                    }
                });
            }
            fos.flush();

            return file;

        } finally {
            try {
                inputStream.close();
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }

        }
    }
}
