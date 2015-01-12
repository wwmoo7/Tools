package com.tpv.androidtool.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundImageView extends ImageView {
	private Path mPath;
	private static final int RADIO = 8;

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public RoundImageView(Context context) {
		super(context);
		initView();
	}

	private void initView() {

	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		if (mPath == null) {
			mPath = new Path();
			RectF rect = new RectF(getPaddingLeft(), getPaddingTop(),
					this.getWidth() - getPaddingRight(), this.getHeight()
							- getPaddingBottom());
			mPath.addRoundRect(rect, RADIO, RADIO, Path.Direction.CCW);
		}
		canvas.clipPath(mPath);
		super.onDraw(canvas);
		canvas.restore();
	}
}
