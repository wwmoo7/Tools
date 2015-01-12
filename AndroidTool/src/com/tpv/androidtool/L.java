package com.tpv.androidtool;

import android.util.Log;

public class L {

	public enum LOG_LEVEL {
		DISABLE, ERROR, WARNING, INFO, DEBUG, VERBO,
	};

	public static String TAG = "mylog";
	public static final boolean isDebug = true;

	private static LOG_LEVEL sLogLevel = LOG_LEVEL.VERBO;

	public static void setLogLevel(LOG_LEVEL level) {
		sLogLevel = level;
	}

	public static void setDefaultTag(String tag) {
		TAG = tag;
	}

	public static void V(String tag, String msg) {
		if (sLogLevel.ordinal() >= LOG_LEVEL.VERBO.ordinal()) {
			Log.v(tag, msg);
		}
	}

	public static void D(String tag, String msg) {
		if (sLogLevel.ordinal() >= LOG_LEVEL.DEBUG.ordinal()) {
			Log.v(tag, msg);
		}
	}

	public static void I(String tag, String msg) {
		if (sLogLevel.ordinal() >= LOG_LEVEL.INFO.ordinal()) {
			Log.i(tag, msg);
		}
	}

	public static void W(String tag, String msg) {
		if (sLogLevel.ordinal() >= LOG_LEVEL.WARNING.ordinal()) {
			Log.w(tag, msg);
		}
	}

	public static void E(String tag, String msg) {
		if (sLogLevel.ordinal() >= LOG_LEVEL.ERROR.ordinal()) {
			Log.e(tag, msg);
		}
	}

	public static void V(String msg) {
		if (sLogLevel.ordinal() >= LOG_LEVEL.VERBO.ordinal()) {
			Log.v(TAG, msg);
		}
	}

	public static void D(String msg) {
		if (sLogLevel.ordinal() >= LOG_LEVEL.DEBUG.ordinal()) {
			Log.v(TAG, msg);
		}
	}

	public static void I(String msg) {
		if (sLogLevel.ordinal() >= LOG_LEVEL.INFO.ordinal()) {
			Log.i(TAG, msg);
		}
	}

	public static void W(String msg) {
		if (sLogLevel.ordinal() >= LOG_LEVEL.WARNING.ordinal()) {
			Log.w(TAG, msg);
		}
	}

	public static void E(String msg) {
		if (sLogLevel.ordinal() >= LOG_LEVEL.ERROR.ordinal()) {
			Log.e(TAG, msg);
		}
	}

}
