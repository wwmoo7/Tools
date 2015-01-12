package com.tpv.androidtool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;

public class BitmapUtils {

	public static final int FLAG_HUE = 0;
	public static final int FLAG_SATURATION = 1;
	public static final int FLAG_LUM = 2;

	public Bitmap handleImage(Bitmap bm, int flag, float mHueValue,
			float mSaturationValue, float mLumValue) {
		Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		ColorMatrix mAllMatrix = new ColorMatrix();
		ColorMatrix mLightnessMatrix = new ColorMatrix();
		ColorMatrix mSaturationMatrix = new ColorMatrix();
		ColorMatrix mHueMatrix = new ColorMatrix();

		switch (flag) {
		case FLAG_HUE:
			mHueMatrix.reset();
			mHueMatrix.setScale(mHueValue, mHueValue, mHueValue, 1);
			break;
		case FLAG_SATURATION:
			mSaturationMatrix.reset();
			mSaturationMatrix.setSaturation(mSaturationValue);
			break;
		case FLAG_LUM:
			mLightnessMatrix.reset();
			mLightnessMatrix.setRotate(0, mLumValue);
			mLightnessMatrix.setRotate(1, mLumValue);
			mLightnessMatrix.setRotate(2, mLumValue);
			break;
		}
		mAllMatrix.reset();
		mAllMatrix.postConcat(mHueMatrix); // add effect
		mAllMatrix.postConcat(mSaturationMatrix); // add effect
		mAllMatrix.postConcat(mLightnessMatrix); // add effect

		paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));
		canvas.drawBitmap(bm, 0, 0, paint);
		return bmp;
	}

	public static Paint addEffectBrightness(float brightness) {
		ColorMatrix mCm = new ColorMatrix();
		mCm.set(new float[] { 1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness,
				0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });
		Paint brightnessPaint = new Paint();
		brightnessPaint.setColorFilter(new ColorMatrixColorFilter(mCm));
		mCm = null;
		return brightnessPaint;
	}

	/**
	 * galuss blur bmp
	 * 
	 * @param bm
	 * @param c
	 * @return
	 */
	public static Bitmap rsbmp(Bitmap bm, Context c, int radio) {
		if (bm == null)
			return null;
		// long start = System.currentTimeMillis();
		Bitmap outputBitmap = Bitmap.createBitmap(bm.getWidth(),
				bm.getHeight(), Config.ARGB_8888);

		RenderScript rs = RenderScript.create(c);
		ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs,
				Element.U8_4(rs));
		Allocation tmpIn = Allocation.createFromBitmap(rs, bm);
		Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
		theIntrinsic.setRadius(radio);
		theIntrinsic.setInput(tmpIn);
		theIntrinsic.forEach(tmpOut);
		tmpOut.copyTo(outputBitmap);
		rs.destroy();
		// long end = System.currentTimeMillis();
		// long usetime = end - start;
		return outputBitmap;
	}

	private Bitmap blur(Bitmap bkg, int radius, float scaleFactor) {
		//long startMs = System.currentTimeMillis();
		int w = bkg.getWidth();
		int h = bkg.getHeight();
		Bitmap overlay = Bitmap.createBitmap((int) (w / scaleFactor),
				(int) (h / scaleFactor), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		canvas.scale(1 / scaleFactor, 1 / scaleFactor);
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(bkg, 0, 0, paint);
		overlay = FastBlur.doBlur(overlay, radius, true);
		//Log.v("mmm", "blur cost = " + (System.currentTimeMillis() - startMs));
		return overlay;
	}

	private Bitmap fastrsblur(Bitmap bkg, int radius, float scaleFactor,
			Context c) {
		//long startMs = System.currentTimeMillis();
		int w = bkg.getWidth();
		int h = bkg.getHeight();
		Bitmap overlay = Bitmap.createBitmap((int) (w / scaleFactor),
				(int) (h / scaleFactor), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		canvas.scale(1 / scaleFactor, 1 / scaleFactor);
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(bkg, 0, 0, paint);
		// overlay = FastBlur.doBlur(overlay, radius, true);
		overlay = rsbmp(overlay, c, radius);
/*		Log.v("mmm", "fastrsblur cost = "
				+ (System.currentTimeMillis() - startMs));*/
		return overlay;
	}

	/**
	 * convert RGB to Gray Bitmap
	 * 
	 * @param img
	 * @return
	 */
	public static Bitmap RGB2Gray(Bitmap img) {
		int width = img.getWidth();
		int height = img.getHeight();
		int[] pixels = new int[width * height];
		img.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];

				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
		result.setPixels(pixels, 0, width, 0, 0, width, height);
		return result;
	}

	public static Drawable RGB2Gray(Context context, int id) {
		Drawable mDrawable = context.getResources().getDrawable(id);
		mDrawable.mutate();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
		mDrawable.setColorFilter(cf);
		return mDrawable;

	}

	/**
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {

		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}

	/**
	 * 
	 * @param sourceImg
	 * @param alpha
	 *            0~255
	 * @return
	 */
	public static Bitmap setAlpha(Bitmap sourceImg, int alpha) {
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
				sourceImg.getWidth(), sourceImg.getHeight());
		for (int i = 0; i < argb.length; i++) {
			argb[i] = (alpha << 24) | (argb[i] & 0x00FFFFFF);
		}
		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
				sourceImg.getHeight(), Config.ARGB_8888);
		return sourceImg;
	}

	public static Bitmap getViewBitmap(View v) {
		v.clearFocus();
		v.setPressed(false);
		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);
		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {

			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);
		return bitmap;
	}

	public static class ImagePiece {
		public int index = 0;
		public Bitmap bitmap = null;
	}

	public static List<ImagePiece> split(Bitmap bitmap, int xPiece, int yPiece) {
		List<ImagePiece> pieces = new ArrayList<ImagePiece>(xPiece * yPiece);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int pieceWidth = width / 3;
		int pieceHeight = height / 3;
		for (int i = 0; i < yPiece; i++) {
			for (int j = 0; j < xPiece; j++) {
				ImagePiece piece = new ImagePiece();
				piece.index = j + i * xPiece;
				int xValue = j * pieceWidth;
				int yValue = i * pieceHeight;
				piece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue,
						pieceWidth, pieceHeight);
				pieces.add(piece);
			}
		}
		return pieces;
	}

	public static InputStream bitmap2is(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		return isBm;
	}

	public static byte[] bitmap2byte(Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 90, stream);
		byte[] bitmapdata = stream.toByteArray();
		return bitmapdata;
	}

	public static Bitmap drawabelToBmp(Drawable d) {
		return ((BitmapDrawable) d).getBitmap();
	}

	public static Drawable bmpToDrawabel(Bitmap bmp) {
		return new FastBitmapDrawable(bmp);
	}

	public static String savebmpFile(Bitmap bitmap, String path,
			String filename, boolean replace) {
		if (bitmap == null) {
			return null;
		}
		File folder = new File(path);
		if (!folder.exists() && !folder.mkdir()) {
			return null;
		}

		File file = new File(path, filename);
		if (file.exists()) {
			if (replace) {
				file.delete();
				file = new File(path, filename);
			} else {
				return file.getAbsolutePath();
			}

		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.close();
			return file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap readBitMap(Context context, int resId, int sample) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inSampleSize = sample;
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 
	 * @param bitmap
	 *            which want to change to round corner
	 * @param corner
	 *            round corner size
	 * @return RoundedCornerBitmap
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int corner) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		paint.setAntiAlias(true);

		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, corner, corner, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap loadBitmapFromUrl(String url) {
		Bitmap bitmap = null;
		try {
			URL fileUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) fileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(3000);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}
}
