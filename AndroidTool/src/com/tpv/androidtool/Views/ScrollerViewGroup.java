package com.tpv.androidtool.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Scroller;

public abstract class ScrollerViewGroup extends ViewGroup {

	protected Scroller mScroller;

	public ScrollerViewGroup(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public ScrollerViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public ScrollerViewGroup(Context context) {
		super(context);
		initView();
	}

	public void smoothScrollTo(int toX, int toY) {
		mScroller.startScroll(getScrollX(), getScrollY(), toX - getScrollX(),
				toY - getScrollY());
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int curX = mScroller.getCurrX();
			int curY = mScroller.getCurrY();
			scrollTo(curX, curY);
			postInvalidate();
		}
	}

	private void initView() {
		mScroller = new Scroller(getContext());
	}

}
