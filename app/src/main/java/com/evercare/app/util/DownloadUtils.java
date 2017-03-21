package com.evercare.app.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.text.TextUtils;

import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * 下载工具类
 *
 * @author DengBin
 */
public class DownloadUtils {
    public static final String REGULAR_EXPRESSION = "§";
    public static final String SAVEPATH = "/linjie/";
    public static String SAVENAME = "";
    public static final int DOWNLOAD_STATUS_CHANGE = 0x12f;

    /**
     * 获取本地然间版本信息 返回格式为 versionCode + "&" + versionName
     *
     * @return
     */
    public static String getLocalVersions(Context context) {
        PackageManager manager = context.getPackageManager();
        StringBuilder sb = new StringBuilder();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(context), 0);
            // 版本号
            sb.append(info.versionCode);
            sb.append(REGULAR_EXPRESSION);
            // 版本名
            sb.append(info.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        context = null;
        return sb.toString();
    }

    public static boolean canUpdateToNewVersion(Context context) {
        return true;
//        String currentVersion = getLocalVersions(context);
//        String newVersion = getServerVersion();
//        if (!currentVersion.equalsIgnoreCase(newVersion)) {
//            return true;
//        } else {
//            return false;
//        }
    }

    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(30*1000);
            connection.setRequestProperty("Accept-Encoding", "identity");

            pd.setMax(connection.getContentLength());

            InputStream inputStream = connection.getInputStream();

            File fff = Environment.getExternalStorageDirectory();
            if(!fff.exists()){
                fff.mkdir();
            }

            File file = new File(fff, "upData.apk");
            FileOutputStream outputStream = new FileOutputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            byte[] buffer = new byte[2048];
            int len;
            int total = 0;
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                total += len;
                pd.setProgress(total);
            }
            outputStream.close();
            bufferedInputStream.close();
            inputStream.close();
            return file;
        } else {
            return null;
        }
    }

    public static String getServerVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        StringBuilder sb = new StringBuilder();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(context), 0);
            // 版本名
            sb.append(info.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static String getNewApkName(String softMessage) {
        try {
            return softMessage.split(REGULAR_EXPRESSION)[1];
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 通过url判断文件是否存在
     *
     * @param url
     */
    public static boolean isFileExistForUrl(String url) {
        String name = getFileName(url);
        return isFileExist(name);
    }

    /**
     * 通过文件名判断文件是否存在
     *
     * @param name
     */
    private static boolean isFileExist(String name) {
        // 访问本地文件
        String LocalDir = DownloadUtils.createPath() + "/" + name;
        // 创建URI
        File file = new File(LocalDir);
        return file.exists();
    }

    /**
     * 创建软件保存路径
     *
     * @return
     */
    public static File createPath() {
        return new File(Environment.getExternalStorageDirectory() + DownloadUtils.SAVEPATH);
    }


    public static void unBindDownloadFileService(Context context, ServiceConnection serviceConnection) {
        if (serviceConnection != null) {
            context.unbindService(serviceConnection);
        }
        context = null;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return "";
        }
        String filename = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
        filename = filename.split("[?]")[0];
        DownloadUtils.SAVENAME = filename;
        return filename;
    }

}
