package com.tpv.androidtool;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;

public class AndTools {

	public class FPSCounter {
		long startTime;
		int frames = 0;
		long useTime;
		float fps = 0;

		public void start() {
			startTime = System.currentTimeMillis();
			frames = 0;
		}

		public void end() {
			useTime = System.currentTimeMillis() - startTime;
			fps = frames * 1000 / useTime;
		}

		public void logFrame() {
			frames++;
			if (System.currentTimeMillis() - startTime >= 1000) {
				Log.d("mmm", "fps: " + frames);
				frames = 0;
				startTime = System.currentTimeMillis();
			}
		}

		public void updateFrame() {
			frames++;
		}
	}

	public static Bitmap takeScreenshotpic() {
		float[] dims = { 1920, 1080 };
		try {
			Class<?> sc = Class.forName("android.view.SurfaceControl");
			Method method = sc.getMethod("screenshot", new Class[] { int.class,
					int.class });
			Object o = method.invoke(sc, new Object[] { (int) dims[0],
					(int) dims[1] });
			Log.v("mmm", "test");
			return (Bitmap) o;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<ResolveInfo> getInstallApps(PackageManager packageManager) {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> apps = packageManager.queryIntentActivities(
				mainIntent, 0);
		return apps;
	}

	public static String getVersion(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getVersionCode(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static boolean setWallpaper(Context ctx, Bitmap bitmap) {
		WallpaperManager wallpaperManager = WallpaperManager.getInstance(ctx);
		try {
			wallpaperManager.setBitmap(bitmap);
			return true;
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return false;
	}

	public static Resources getResources(Context context, String apkPath)
			throws Exception {
		String PATH_AssetManager = "android.content.res.AssetManager";
		Class<?> assetMagCls = Class.forName(PATH_AssetManager);
		Constructor<?> assetMagCt = assetMagCls.getConstructor((Class[]) null);
		Object assetMag = assetMagCt.newInstance((Object[]) null);
		Class<?>[] typeArgs = new Class[1];
		typeArgs[0] = String.class;
		Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
				"addAssetPath", typeArgs);
		Object[] valueArgs = new Object[1];
		valueArgs[0] = apkPath;
		assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
		Resources res = context.getResources();
		typeArgs = new Class[3];
		typeArgs[0] = assetMag.getClass();
		typeArgs[1] = res.getDisplayMetrics().getClass();
		typeArgs[2] = res.getConfiguration().getClass();
		Constructor<?> resCt = Resources.class.getConstructor(typeArgs);
		valueArgs = new Object[3];
		valueArgs[0] = assetMag;
		valueArgs[1] = res.getDisplayMetrics();
		valueArgs[2] = res.getConfiguration();
		res = (Resources) resCt.newInstance(valueArgs);
		return res;
	}

	public static Drawable getUninstallAPKIcon(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		Resources res = null;
		try {
			res = getResources(context, apkPath);
		} catch (Exception e) {
			return null;
		}
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			return res.getDrawable(appInfo.icon);
		}

		return null;
	}

	public static boolean installApk(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = "application/vnd.android.package-archive";
		intent.setDataAndType(Uri.fromFile(file), type);
		context.startActivity(intent);
		return true;
	}

	public static boolean unInstall(Context c, String UninstallPkg) {
		try {
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
					Uri.parse("package:" + UninstallPkg));
			c.startActivity(uninstallIntent);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/*-- hide api need framwork
	 public static Boolean cleanUserdate(Context c, String pkg) {
	 boolean result = false;
	 try {
	 PackageManager pm = c.getPackageManager();
	 pm.clearApplicationUserData(pkg, null);
	 result = true;
	 return result;
	 } catch (Exception e) {
	 e.printStackTrace();
	 return result;
	 }
	 }
	 public static Boolean uninstallHide(Context c, String pkg) {
	 boolean result = false;
	 try {
	 PackageManager pm = c.getPackageManager();
	 IPackageDeleteObserver ob = new IPackageDeleteObserver.Stub() {

	 @Override
	 public void packageDeleted(String arg0, int arg1)
	 throws RemoteException {
	 L.D("uninstall " + arg0 + " flag=" + arg1);
	 }
	 };
	 pm.deletePackage(pkg, ob, 0);
	 result = true;
	 return result;
	 } catch (Exception e) {
	 e.printStackTrace();
	 return result;
	 }
	 }
	 private void changelan(Locale locale) {
	 try {
	 IActivityManager iActMag = ActivityManagerNative.getDefault();
	 Configuration config = iActMag.getConfiguration();
	 config.locale = locale;
	 iActMag.updateConfiguration(config);
	 } catch (Exception e) {
	 e.printStackTrace();
	 }
	 }

	 -*/
	public static boolean updateLanguage(Locale locale) {
		try {
			Object objIActMag;
			Class<?> clzIActMag = Class.forName("android.app.IActivityManager");
			Class<?> clzActMagNative = Class
					.forName("android.app.ActivityManagerNative");
			Method mtdActMagNative$getDefault = clzActMagNative
					.getDeclaredMethod("getDefault");
			objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
			Method mtdIActMag$getConfiguration = clzIActMag
					.getDeclaredMethod("getConfiguration");
			Configuration config = (Configuration) mtdIActMag$getConfiguration
					.invoke(objIActMag);
			config.locale = locale;
			Class<?>[] clzParams = { Configuration.class };
			Method mtdIActMag$updateConfiguration = clzIActMag
					.getDeclaredMethod("updateConfiguration", clzParams);
			mtdIActMag$updateConfiguration.invoke(objIActMag, config);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void resizeIconDrawable(Drawable icon, int w, int h) {
		icon.setBounds(0, 0, w, h);
	}

	public static void disableComp(PackageManager pm, ComponentName comp) {
		pm.setComponentEnabledSetting(comp,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 1);
	}

	public static void enableComp(PackageManager pm, ComponentName comp) {
		pm.setComponentEnabledSetting(comp,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 1);
	}

	public static void enableApp(PackageManager pm, String pkg) {
		pm.setApplicationEnabledSetting(pkg,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 1);
	}

	public static void disableApp(PackageManager pm, String pkg) {
		pm.setApplicationEnabledSetting(pkg,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 1);
	}
}
