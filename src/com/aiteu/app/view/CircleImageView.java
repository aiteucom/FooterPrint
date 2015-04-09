package com.aiteu.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

	private int mBorderThickness = 2;
	private int mBorderInsideColor = 0xFFFEFEFE;
	private int mBorderOutsideColor = mBorderInsideColor;
	private int mBorderWidth = 0;
	private int mBorderHeight = 0;
	private int defaultColor = 0xFFFFFFFF;
	private int RADIUS = 10;
	private boolean isDotDisplay = false;
	private Paint mPaint = null;

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		init(context);
	}

	public CircleImageView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(Color.RED);
	}

	public void setDotDisplay(boolean flagDisplay) {
		this.isDotDisplay = flagDisplay;
		this.invalidate();
	}

	public boolean isDotDisplay() {
		return isDotDisplay;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (drawable == null) {
			return;
		}
		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		if (drawable.getClass() == NinePatchDrawable.class) {
			return;
		}
		this.measure(0, 0);
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		if (mBorderWidth == 0) {
			mBorderWidth = getWidth();
		}
		if (mBorderHeight == 0) {
			mBorderHeight = getHeight();
		}
		// 半径
		int radius = 0;
		if (mBorderInsideColor != defaultColor
				&& mBorderOutsideColor != defaultColor) {
			radius = (mBorderWidth < mBorderHeight ? mBorderWidth
					: mBorderHeight) / 2 - 2 * mBorderThickness;
			// 画内圆
			drawCircleBorder(canvas, radius + mBorderThickness / 2,
					mBorderInsideColor);
			// 画外圆
			drawCircleBorder(canvas, radius + mBorderThickness
					+ mBorderThickness / 2, mBorderOutsideColor);
		} else if (mBorderInsideColor != defaultColor
				&& mBorderOutsideColor == defaultColor) {// 定义画一个边框
			radius = (mBorderWidth < mBorderHeight ? mBorderWidth
					: mBorderHeight) / 2 - mBorderThickness;
			drawCircleBorder(canvas, radius + mBorderThickness / 2,
					mBorderInsideColor);
		} else if (mBorderInsideColor == defaultColor
				&& mBorderOutsideColor != defaultColor) {// 定义画一个边框
			radius = (mBorderWidth < mBorderHeight ? mBorderWidth
					: mBorderHeight) / 2 - mBorderThickness;
			drawCircleBorder(canvas, radius + mBorderThickness / 2,
					mBorderOutsideColor);
		} else {// 没有边框
			radius = (mBorderWidth < mBorderHeight ? mBorderWidth
					: mBorderHeight) / 2;
		}

		// 画圆形图像
		Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
		canvas.drawBitmap(roundBitmap, mBorderWidth / 2 - radius, mBorderHeight
				/ 2 - radius, null);
		if(isDotDisplay){
			RADIUS = roundBitmap.getWidth()/8;
			int px = roundBitmap.getWidth() - RADIUS / 2;
			int py = RADIUS;
			canvas.drawCircle(px, py, RADIUS, mPaint);
		}
	}

	private void drawCircleBorder(Canvas canvas, int radius, int color) {
		// 新建画笔对象
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(mBorderThickness);
		canvas.drawCircle(mBorderWidth / 2, mBorderHeight / 2, radius, paint);
	}

	public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
		Bitmap scaledSrcBmp;
		int diameter = radius * 2;

		// 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		int squareWidth = 0, squareHeight = 0;
		int x = 0, y = 0;
		Bitmap squareBitmap;
		if (bmpHeight > bmpWidth) {// 高大于宽
			squareWidth = squareHeight = bmpWidth;
			x = 0;
			y = (bmpHeight - bmpWidth) / 2;
			// 截取正方形图片
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else if (bmpHeight < bmpWidth) {// 宽大于高
			squareWidth = squareHeight = bmpHeight;
			x = (bmpWidth - bmpHeight) / 2;
			y = 0;
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else {
			squareBitmap = bmp;
		}

		if (squareBitmap.getWidth() != diameter
				|| squareBitmap.getHeight() != diameter) {
			scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
					diameter, true);

		} else {
			scaledSrcBmp = squareBitmap;
		}
		Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
				scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
				paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
		bmp = null;
		squareBitmap = null;
		scaledSrcBmp = null;
		return output;
	}
}
