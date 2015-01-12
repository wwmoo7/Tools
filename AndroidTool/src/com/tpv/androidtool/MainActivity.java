package com.tpv.androidtool;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sonymobile.tools.xappdbg.XAppDbgServer;
import com.sonymobile.tools.xappdbg.properties.XAppDbgPropertiesModule;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration builder = new ImageLoaderConfiguration.Builder(
				context)
				.defaultDisplayImageOptions(options)
				.discCacheSize(50 * 1024 * 1024).threadPoolSize(2)
				.discCache(new UnlimitedDiscCache(context.getCacheDir()))
				.denyCacheImageMultipleSizesInMemory().build();
		ImageLoader.getInstance().init(builder);
	}
	
	public static DisplayImageOptions options = new DisplayImageOptions.Builder()
	.delayBeforeLoading(30).cacheOnDisc(true)
	.build();
	
	public static class Consts{
		public static int PARM1=1;
	}
	
	public static void startParaControlTools(){
		XAppDbgServer mServer = new XAppDbgServer();
	    mServer.addModule(new XAppDbgPropertiesModule(Consts.class));	  
	    mServer.start();
	}
}
