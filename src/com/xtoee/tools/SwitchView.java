package com.xtoee.tools;

import com.xtoee.main.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * �Զ���witch
 * 
 *	void init() void setSwitchStatus(boolean isOpen) boolean
 *	onTouchEvent(MotionEvent event) void changeStatus() void
 *	changeOpenStatusWithAnim(boolean open) OnSwitchChangeListener
 *	getSwitchChangeListener() void
 *	setOnSwitchChangeListener(OnSwitchChangeListener
 *	switchChangeListener)
 */
public class SwitchView extends LinearLayout {
	private ImageView maskImage; // �����ڸ�ͼƬ
	private boolean open; // ���ص�ǰ״̬
	private boolean isAninFinish = true; // �����Ƿ����
	private float x; // ��¼ACTION_DOWNʱ��ĺ�����
	private boolean isChangedByTouch = false; // �Ƿ���һ���¼����Ѿ��л���״̬
	private OnSwitchChangeListener switchChangeListener; // ��ؿ���״̬
	private boolean isenable = true;
	private int duration = 300;

	long start = 0, end = 0;

	public interface OnSwitchChangeListener {
		void onSwitchChanged(boolean open);
	}

	public SwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SwitchView(Context context) {
		super(context);
		init();
	}

	public void setTheBackground(int i) {
		if (i == 1)
			setBackgroundResource(R.drawable.switch_bg_1);
		if (i == 2)
			setBackgroundResource(R.drawable.switch_bg_2);
		if (i == 3)
			setBackgroundResource(R.drawable.switch_bg_3);
	}

	private void init() {
		setBackgroundResource(R.drawable.switch_bg_2);
		maskImage = new ImageView(getContext());
		maskImage.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		maskImage.setImageResource(R.drawable.switch_mask);
		addView(maskImage);
	}

	public boolean getSwitchStatus() {
		return open;
	}

	public void setSwitchStatus(boolean isOpen) {
		this.open = isOpen;
		if (isOpen) {
			setGravity(Gravity.RIGHT);
		} else {
			setGravity(Gravity.LEFT);
		}
	}

	public void setEnabled(boolean isenable) {
		this.isenable = isenable;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isenable)
			return true;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			x = event.getX();
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			if (event.getX() - x > 5 && !open) { // ����
				changeStatus();
			} else if (event.getX() - x < -5 && open) { // ����
				changeStatus();
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			if (Math.abs(event.getX() - x) <= 5) {
				changeStatus();
			}
			isChangedByTouch = false;
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			isChangedByTouch = false;
			break;
		}
		}
		return true;
	}

	public void changeStatus() {
		if (isAninFinish && !isChangedByTouch) {
			isChangedByTouch = true;
			open = !open;
			isAninFinish = false;
			if (switchChangeListener != null) {
				switchChangeListener.onSwitchChanged(open);
			}
			changeOpenStatusWithAnim(open);
		}
	}

	private void changeOpenStatusWithAnim(boolean open) {
		if (open) {
			// ����
			Animation leftToRight = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE,
					getWidth() - maskImage.getWidth(),
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0);
			leftToRight.setDuration(duration);
			leftToRight.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					maskImage.clearAnimation();
					setGravity(Gravity.RIGHT);
					isAninFinish = true;
				}
			});
			maskImage.startAnimation(leftToRight);
		} else {
			// �ҵ���
			Animation rightToLeft = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE,
					maskImage.getWidth() - getWidth(),
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0);
			rightToLeft.setDuration(duration);
			rightToLeft.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					maskImage.clearAnimation();
					setGravity(Gravity.LEFT);
					isAninFinish = true;
				}
			});
			maskImage.startAnimation(rightToLeft);
		}
	}

	public OnSwitchChangeListener getSwitchChangeListener() {
		return switchChangeListener;
	}

	public void setOnSwitchChangeListener(
			OnSwitchChangeListener switchChangeListener) {
		this.switchChangeListener = switchChangeListener;
	}

}
