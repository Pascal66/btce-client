/*copy from
 * http://stackoverflow.com/questions/9272384/how-add-textview-in-middle-of-seekbar-thumb
 * thanks
 */
package com.googlecode.BtceClient;

import java.text.DecimalFormat;

import android.content.Context;
//import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class SeekBarWithText extends SeekBar {

	int btn_regin = 0;
	int buttonsize = 50;// dp

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub"
		int step = 0;
		if (event.getX() < buttonsize)
			step = -1;
		else if (event.getX() > this.getWidth() - buttonsize)
			step = 1;
		if (0 != step) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				btn_regin = step;
				return true;
			case MotionEvent.ACTION_UP:
				if (btn_regin == step) {
					this.setProgress(this.getProgress() + btn_regin);
					btn_regin = 0;
					return true;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (btn_regin == step) {
					return true;
				}
				break;
			}
		} else
			btn_regin = 0;
		return super.onTouchEvent(event);
	}

	private static final int textMargin = 6;
	private static final int leftPlusRightTextMargins = textMargin + textMargin;
	// private static final int maxFontSize = 18;
	// private static final int minFontSize = 10;

	protected String overlayText = "";
	protected Paint textPaint;
	protected DecimalFormat formatter2 = new DecimalFormat();
	protected double max_value = 0;
	protected double base_value = 0;
	protected double ratio = 1;

	public SeekBarWithText(Context context) {
		super(context);
		initData();
	}

	public SeekBarWithText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData();
	}

	public void setMaxValue(double vl) {
		max_value = vl;
	}

	public void setBaseValue(double vl) {
		base_value = vl;
	}

	public void setRatio(double r) {
		ratio = r;
	}

	public double getProgressValue() {
		int progress = this.getProgress();
		int maxProgress = this.getMax();
		double percentProgress = (double) progress / (double) maxProgress;
		return base_value + percentProgress * (max_value - base_value);
	}

	public void setProgressValue(double vl) {
		int maxProgress = this.getMax();
		this.setProgress((int) ((vl - base_value) / (max_value - base_value) * this
				.getMax()));
	}

	protected void initData() {
		// Resources resources = getResources();
		// Set up drawn text attributes here
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);
		textPaint.setTextAlign(Align.LEFT);
		formatter2.setMaximumFractionDigits(2);
		formatter2.setGroupingUsed(false);
		textPaint.setTextSize(18);
		buttonsize = (int) (buttonsize
				* this.getContext().getResources().getDisplayMetrics().density + 0.5);
	}

	// This attempts to ensure that the text fits inside your SeekBar on a
	// resize
	// @Override
	// protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	// super.onSizeChanged(w, h, oldw, oldh);
	// // setFontSmallEnoughToFit(w - leftPlusRightTextMargins)));
	// }

	// Finds the largest text size that will fit
	// protected void setFontSmallEnoughToFit(int width) {
	// int textSize = maxTextSize;
	// textPaint.setTextSize(textSize);
	// while ((textPaint.measureText(sampleText) > width)
	// && (textSize > minTextSize)) {
	// textSize--;
	// textPaint.setTextSize(textSize);
	// }
	// }

	// Clients use this to change the displayed text
	public void setOverlayText(String text) {
		// %p:percent
		// %P:progress
		// %M:max
		// %m:max_value
		// %v:progress_value
		this.overlayText = text;
		invalidate();
	}

	// Draws the text onto the SeekBar
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// Draw everything else (i.e., the usual SeekBar) first
		super.onDraw(canvas);

		// No text, no problem
		if (overlayText.length() == 0) {
			return;
		}

		canvas.save();

		// Here are a few parameters that could be useful in calculating where
		// to put the text
		int width = this.getWidth() - leftPlusRightTextMargins;
		int height = this.getHeight();

		// A somewhat fat finger takes up about seven digits of space
		// on each side of the thumb; YFMV
		// int fatFingerThumbHangover = (int) textPaint.measureText("1234567");

		int progress = this.getProgress();
		int maxProgress = this.getMax();
		double percentProgress = (double) progress / (double) maxProgress;

		String info = overlayText.replace("%M", "" + maxProgress);
		info = info.replace("%P", "" + progress);
		info = info.replace(
				"%p",
				formatter2.format(ratio
						* 100
						* (base_value + percentProgress
								* (max_value - base_value)) / max_value)
						+ "%");
		info = info.replace("%m", formatter2.format(max_value));
		info = info.replace(
				"%v",
				formatter2.format(base_value + percentProgress
						* (max_value - base_value)));

		int textHeight = (int) (Math.abs(textPaint.ascent())
				+ textPaint.descent() + 1);
		float textWidth = textPaint.measureText(info);
		// int thumbOffset = this.getThumbOffset();
		//
		// // These are measured from the point textMargin in from the left of
		// the
		// // SeekBarWithText view.
		// int middleOfThumbControl = (int) ((double) width * percentProgress);
		// int spaceToLeftOfFatFinger = middleOfThumbControl
		// - fatFingerThumbHangover;
		// int spaceToRightOfFatFinger = (width - middleOfThumbControl)
		// - fatFingerThumbHangover;
		//
		// int spaceToLeftOfThumbControl = middleOfThumbControl - thumbOffset;
		// int spaceToRightOfThumbControl = (width - middleOfThumbControl)
		// - thumbOffset;
		//
		// int bottomPadding = this.getPaddingBottom();
		// int topPadding = this.getPaddingTop();

		// Here you will use the above and possibly other information to decide
		// where you would
		// like to draw the text. One policy might be to draw it on the extreme
		// right when the thumb
		// is left of center, and on the extreme left when the thumb is right of
		// center. These
		// methods will receive any parameters from the above calculations that
		// you need to
		// implement your own policy.
		float x = (width - textWidth) / 2;
		float y = (float) ((height + textHeight / 2.5) / 2);

		// Finally, just draw the text on top of the SeekBar
		canvas.drawText(info, x, y, textPaint);

		canvas.restore();
	}
}