package com.evercare.app.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 
 * <pre>
 * 业务名:
 * 功能说明: 获取手机相关信息 工具类
 * 编写日期:	2015年6月4日
 * 作者:	 xlren
 * 
 * 历史记录
 * 1、修改日期：
 *    修改人：
 *    修改内容：
 * </pre>
 */
public class DeviceTool {
	private static long exitTime = 0;

	private DeviceTool() {
	}

	private static DeviceTool instance = null;
	private static DisplayMetrics dm;

	public synchronized static DeviceTool getInstance() {

		if (instance == null) {
			instance = new DeviceTool();

		}
		return instance;
	}

	/**
	 * 判断sd卡是否存在，并且可读
	 * 
	 * @return
	 */
	public static boolean isHasSDCard() {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 查看SD卡的剩余空间
	 * 
	 * @return
	 */
	public static double getSDFreeSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		double blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		double freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		// return freeBlocks * blockSize; //单位Byte
		// return (freeBlocks * blockSize)/1024; //单位KB
		return (freeBlocks * blockSize) / 1024 / 1024 / 1024; // 单位MB
	}

	/**
	 * 查看SD卡总容量
	 * 
	 * @return
	 */
	public static double getSDAllSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		double blockSize = sf.getBlockSize();
		// 获取所有数据块数
		double allBlocks = sf.getBlockCount();
		// 返回SD卡大小
		// return allBlocks * blockSize; //单位Byte
		// return (allBlocks * blockSize)/1024; //单位KB
		return (allBlocks * blockSize) / 1024 / 1024 / 1024; // 单位MB
	}

	/**
	 * 获取设备Id
	 * 
	 * @return deviceId
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		if (imei != null) {
			return imei;
		}
		return "";
	}

	public static String getSubscriberId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telephonyManager.getSubscriberId();
		if (imsi != null) {
			return imsi;
		}
		return "";
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @param context
	 * @return 如果可用，返回true
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info == null) {
				return false;
			} else {
				if (info.isAvailable()) {
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetworkTypeName(Context context) {
		String typeName = "";
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null) {
			typeName = cm.getActiveNetworkInfo().getTypeName();
		}
		return typeName;
	}

	/**
	 * 获取渠道商号
	 * 
	 * @param context
	 * @return
	 */
	public static String getChannelCode(Context context) {
		String code = getMetaData(context);
		if (!TextUtils.isEmpty(code)) {
			return code;
		}
		return "default";
	}

	/**
	 * 通过Key获取渠道商号码
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	private static String getMetaData(Context context) {
		// 第一版打包工具
		// try {
		// ApplicationInfo ai = context.getPackageManager()
		// .getApplicationInfo(context.getPackageName(),
		// PackageManager.GET_META_DATA);
		// Object value = ai.metaData.get(key);
		// if (value != null) {
		// return value.toString();
		// }
		// } catch (Exception e) {
		// //
		// }
		// return null;
		// 第二版打包工具来自于美团
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;
		String ret = "";
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(sourceDir);
			Enumeration<?> entries = zipfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				String entryName = entry.getName();
				if (entryName.startsWith("META-INF/cpchannel")) {
					ret = entryName;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zipfile != null) {
				try {
					zipfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String[] split = ret.split("_");
		if (split != null && split.length >= 2) {
			return ret.substring(split[0].length() + 1);

		} else {
			return "";
		}

	}

	public static String getChannel(Context context) {
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;
		String ret = "";
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(sourceDir);
			Enumeration<?> entries = zipfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				String entryName = entry.getName();
				if (entryName.startsWith("mtchannel")) {
					ret = entryName;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zipfile != null) {
				try {
					zipfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String[] split = ret.split("_");
		if (split != null && split.length >= 2) {
			return ret.substring(split[0].length() + 1);

		} else {
			return "";
		}
	}

	/**
	 * 查询Apn名字
	 * 
	 * @param context
	 * @return
	 */
	public static String getApnName(Context context) {
		// 设置默认APN URI
		Uri preferApnUri = Uri.parse("content://telephony/carriers/preferapn");
		String apnName = "";
		try {
			// 查询默认APN
			Cursor c = context.getContentResolver().query(preferApnUri, null,
					null, null, null);
			if (c != null) {
				c.moveToFirst();
				String proxyStr = c.getString(c.getColumnIndex("proxy"));
				if (proxyStr.equals("10.0.0.172")) {
					apnName = "cmwap";
				} else if (proxyStr.equals("10.0.0.200")) {
					apnName = "ctwap";
				}
				c.close();
			}
		} catch (Exception e) {
			return "";
		}
		apnName = apnName.toLowerCase();
		return apnName;
	}

	/**
	 * 注： density 大于1的情况下，需要设置targetSdkVersion在4-9之间，例如 <uses-sdk
	 * android:minSdkVersion="3" android:targetSdkVersion="10" />
	 * 
	 * @param context
	 */
	public static Dispaly getDisplay(Context context) {
		dm = new DisplayMetrics();
		Dispaly dis = new Dispaly();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		dis.density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		dis.densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
		dis.xdpi = dm.xdpi;
		dis.ydpi = dm.ydpi;

		dis.screenWidthDip = dm.widthPixels; // 屏幕宽（dip，如：320dip）
		dis.screenHeightDip = dm.heightPixels; // 屏幕高（dip，如：533dip）

		dis.screenWidth = (int) (dm.widthPixels * dis.density + 0.5f); // 屏幕宽（px，如：480px）
		dis.screenHeight = (int) (dm.heightPixels * dis.density + 0.5f); // 屏幕高（px，如：800px）
		return dis;
	}

	public static class Dispaly {

		public int screenHeight;
		public int screenWidth;
		public int screenHeightDip;
		public int screenWidthDip;
		public float ydpi;
		public float xdpi;
		public int densityDPI;
		public float density;

	}

	public static int getMeasuredHeight(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
		return child.getMeasuredHeight();
	}

	public static String getUniqueId(Context context) {
		String PREFS_FILE = "device_id";
		String PREFS_DEVICE_ID = "device_id";
		final SharedPreferences prefs = context.getSharedPreferences(
				PREFS_FILE, 0);
		String uniqueId = prefs.getString(PREFS_DEVICE_ID, null);
		if (uniqueId == null) {
			try {
				uniqueId = DeviceTool.getDeviceId(context);
				if ("".equals(uniqueId)) {
					String mac = getMac();
					uniqueId = "".equals(mac) ? mac : Secure.getString(
							context.getContentResolver(), Secure.ANDROID_ID);
				}
			} catch (Exception e) {

			}
		}
		prefs.edit().putString(PREFS_DEVICE_ID, uniqueId).commit();
		return uniqueId;
	}

	public static String getMac() {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;
	}

	// 获取手机号码
	public static String phoneId(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String phone = tm.getLine1Number();
		if (phone != null && phone.contains("+86")) {
			phone = phone.substring(3, 14);
		}
		return phone;
	}

	/**
	 * 退出程序
	 */
	public static void exitApp(Context context) {
		// 判断2次点击事件时间
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			RWTool.exitClient(context);
		}
	}

	public static boolean isFastDoubleClick() {
		// 判断2次点击事件时间
		long time = System.currentTimeMillis();
		if ( time - exitTime < 2000) {
			return true;
		}
		exitTime = time;
		return false;
	}
}
