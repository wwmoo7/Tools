package com.tpv.androidtool.Views;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

public class PicGallery extends ViewGroup {

	public class C {
		public static final int distanc = 10;
		public static final int baseline = 100;
		public static final int duration = 100;
		public static final int itemdelay = 100;
	}

	private int col = 4;

	public PicGallery(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public PicGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public PicGallery(Context context) {
		super(context);
		initView();
	}

	private View genView() {
		ImageView img = new ImageView(getContext());
		img.setBackgroundColor((int) (0xff000000 + Math.random() * 0xffffff));
		return img;
	}

	private void initView() {
		for (int i = 0; i < 16; i++) {

			addView(genView());
		}
		setFocusable(true);
		LayoutTransition transition = new LayoutTransition();
		transition.enableTransitionType(LayoutTransition.CHANGING);
		setLayoutTransition(transition);
		setChildrenDrawingOrderEnabled(true);
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		return childCount - i - 1;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
			colup();
			requestLayout();
			break;
		case KeyEvent.KEYCODE_2:
			coldown();
			requestLayout();
			break;
		case KeyEvent.KEYCODE_3:
			tobottom();
			break;
		case KeyEvent.KEYCODE_4:
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				child.animate().translationX(0).translationY(0).rotationX(0)
						.setStartDelay(i * C.itemdelay);
			}
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void coldown() {
		if (col > 2) {
			col--;
			int delview = getChildCount() - col * col;
			removeViews(getChildCount() - delview, delview);
		}

	}

	private void colup() {
		if (col < 11) {
			col++;
			int addview = col * col - getChildCount();
			for (int i = 0; i < addview; i++) {
				addView(genView());
			}
		}

	}

	private void tobottom() {
		int w = getWidth();
		int h = getHeight();
		int itemh = h / col;
		int itemw = w / col;
		int dl = (w - itemw) / 2;
		int dt = h - itemh;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			int l = child.getLeft();
			int t = child.getTop();
			int ty = dt - t + i * C.distanc + C.baseline;
			// int ty = dt - t;
			child.animate().translationX(dl - l).translationY(ty).rotationX(70)
					.setStartDelay((getChildCount() - i) * C.itemdelay).setDuration(C.duration)
					.setInterpolator(new AccelerateDecelerateInterpolator());
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int w = getWidth();
		int h = getHeight();
		int itemh = h / col;
		int itemw = w / col;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			int inCol = i % col;
			int inRow = i / col;
			int left = inCol * itemw;
			int top = inRow * itemh;
			child.layout(left, top, left + itemw, top + itemh);
		}
	}
}
