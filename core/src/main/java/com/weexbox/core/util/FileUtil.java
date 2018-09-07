package com.weexbox.core.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class FileUtil {

    private FileUtil() {
    }

    public static String getSystemGalleryPath() {
        String fileDir = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
        return fileDir;
    }

    public static boolean checkAvailableSpaceOnDisk(long size) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            StatFs fs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            @SuppressWarnings("deprecation")
            long availableBlocks = (long) fs.getAvailableBlocks() * fs.getBlockSize();
            return availableBlocks >= size;
        } else {
            return false;
        }
    }

    /**
     * 根据资源文件名获取资源文件的id
     *
     * @param context
     * @param name
     * @return
     */
    public static int getDrawableIdByName(Context context, String name) {
        int resid = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return resid;
    }

    /**
     * 简单的拷贝
     */
    public static void copyFile(String fromFilePath, String toFilePath) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            FileInputStream inputStream = new FileInputStream(new File(fromFilePath));
            final byte[] tmp = new byte[4096];
            int l = 0;
            while ((l = inputStream.read(tmp)) != -1) {
                out.write(tmp, 0, l);
            }
            out.flush();
            inputStream.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(new File(toFilePath));
            outputStream.write(out.toByteArray());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将zip解压缩
     *
     * @param context
     * @throws IOException
     */
    public static void copyZip2DataDirectory(Context context) throws IOException {
        FileOutputStream outputStream = null;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("test.zip");
        ZipInputStream zis = new ZipInputStream(inputStream);
        ZipEntry entry = null;
        while ((entry = zis.getNextEntry()) != null) {
            outputStream = context.openFileOutput(entry.getName(), Context.MODE_PRIVATE);
            byte[] buffer = new byte[2 * 1024];
            int len = -1;
            while ((len = zis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
        }
        zis.close();
        inputStream.close();
    }

    /**
     * @param context
     * @param dirName Only the folder name, not full path.
     * @return app_cache_path/dirName
     */
    public static String getDiskCacheDir(Context context, String dirName) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getAbsolutePath();
            }
        }
        if (cachePath == null) {
            cachePath = context.getCacheDir().getAbsolutePath();
        }

        return cachePath + File.separator + dirName;
    }

    public static String getExternalStorageDirectory(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            showToast(context, "请检查存储空间");
        }
        return null;
    }

    public static File saveImage(final Bitmap bitmap, final String path) {
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public static boolean saveFile(Context context, String dirName, final String fileName, final String content, final boolean
            override) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            final String dir = getExternalStorageDirectory(context) + File.separator + dirName;
            File file = new File(dir);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(dir, fileName);
            if (file.exists()) {
                if (override) {
                    final boolean result = file.delete();
                    if (!result && file.exists()) {
                        file.delete();
                    }
                    file = new File(dir, fileName);
                } else {
                    showToast(context, "保存失败，已经有相同的文件");
                    return false;
                }
            }
            FileOutputStream outputStream = null;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                outputStream.write(content.getBytes("UTF-8"));
                outputStream.close();
//                showToast(context, "保存成功");
                return true;
            } catch (Exception e) {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                showToast(context, "保存失败");
                return false;
            }
        } else {
            showToast(context, "请检查存储空间");
            return false;
        }
    }

    public static List<String> getFilesName(Context context, String dirName) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            final String dir = getExternalStorageDirectory(context) + File.separator + dirName;
            final File file = new File(dir);
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length != 0) {
                    final List<String> list = new ArrayList<>();
                    for (File temp : files) {
                        list.add(temp.getName());
                    }
                    return list;
                } else {
                    return null;
                }
            } else {
//                showToast(context, "路径错误");
                return null;
            }
        } else {
            showToast(context, "请检查存储空间");
            return null;
        }
    }

    public static String getFileContent(final String path) {
        final File file = new File(path);
        if (file.exists()) {
            FileInputStream inputStream = null;
            ByteArrayOutputStream baos = null;
            try {
                inputStream = new FileInputStream(file);
                byte[] b = new byte[1024];
                int len = 0;
                baos = new ByteArrayOutputStream();
                while ((len = inputStream.read(b)) != -1) {
                    baos.write(b, 0, len);
                }
                byte[] data = baos.toByteArray();
                baos.close();
                inputStream.close();
                return new String(data, "UTF-8");
            } catch (Exception e) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public static String getFileContent(final Context context, final String dirName, final String fileName) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            final String dir = getExternalStorageDirectory(context) + File.separator + dirName;
            final File file = new File(dir, fileName);
            if (file.exists()) {
                FileInputStream inputStream = null;
                ByteArrayOutputStream baos = null;
                try {
                    inputStream = new FileInputStream(file);
                    byte[] b = new byte[1024];
                    int len = 0;
                    baos = new ByteArrayOutputStream();
                    while ((len = inputStream.read(b)) != -1) {
                        baos.write(b, 0, len);
                    }
                    byte[] data = baos.toByteArray();
                    baos.close();
                    inputStream.close();
                    return new String(data, "UTF-8");
                } catch (Exception e) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (baos != null) {
                        try {
                            baos.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
//                    showToast(context, "读取出错");
                    return null;
                }

            } else {
//                showToast(context, "路径错误");
                return null;
            }
        } else {
            showToast(context, "请检查存储空间");
            return null;
        }
    }

    private static void showToast(final Context context, final String text) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            ToastUtil.showLongToast(context, text);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showLongToast(context, text);
                }
            });
        }
    }
}
