package com.xtoee.main;

import com.xtoee.tools.CardConfig;
import com.xtoee.tools.CardView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class TheMainActivity extends Activity {
	private CardView mCardView;
	private boolean isopen = false;
	private int child = 1;
	private float startx = 0.0f, starty = 0.0f, offsetx = 0.0f, offsety = 0.0f;
	private int headerHeight;
	/** 头部View高度 **/
	private int itemTitleHeight;

	/** 卡片标题高度 **/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home);

		mCardView = (CardView) findViewById(R.id.epay_card_view);
		getViewHigh();// 计算当前屏幕的各种View的高度

	}

	/**
	 * 保持竖屏
	 */
	@Override
	protected void onResume() {
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
	}

	/**
	 * 点击事件
	 * @param v   被点击的View
	 */
	public void onClick(View v) {
		Intent intent;// 一下为各种跳转切换，先申明一个intent为后续所用
		switch (v.getId()) {
		case R.id.title1:
			mCardView.onCardItemClick(1);
			isopen = !isopen;
			child = 1;
			// startActivity(new Intent(this, TestActivity.class));
			break;

		case R.id.title2:
			mCardView.onCardItemClick(2);
			isopen = !isopen;
			child = 2;
			break;

		case R.id.title3:
			mCardView.onCardItemClick(3);
			isopen = !isopen;
			child = 3;
			break;

		case R.id.home_cpc:
			intent = new Intent(TheMainActivity.this, RunActivity.class);
			intent.putExtra("no", 0);
			startActivity(intent);
			// TheMainActivity.this.finish();
			break;

		case R.id.home_cl:
			intent = new Intent(TheMainActivity.this, RunActivity.class);
			intent.putExtra("no", 1);
			startActivity(intent);
			// TheMainActivity.this.finish();
			break;

		case R.id.home_cpr:
			intent = new Intent(TheMainActivity.this, RunActivity.class);
			intent.putExtra("no", 2);
			startActivity(intent);
			// TheMainActivity.this.finish();
			break;

		case R.id.home_subcl:
			intent = new Intent(TheMainActivity.this, RunActivity.class);
			intent.putExtra("no", 3);
			startActivity(intent);
			// TheMainActivity.this.finish();
			break;

		case R.id.home_procont:
			intent = new Intent(TheMainActivity.this, ControlActivity.class);
			intent.putExtra("no", 0);
			startActivity(intent);
			// TheMainActivity.this.finish();
			break;

		case R.id.home_scene:
			intent = new Intent(TheMainActivity.this, ControlActivity.class);
			intent.putExtra("no", 1);
			startActivity(intent);
			// TheMainActivity.this.finish();
			break;

		case R.id.home_adjust:
			intent = new Intent(TheMainActivity.this, ControlActivity.class);
			intent.putExtra("no", 2);
			startActivity(intent);
			// TheMainActivity.this.finish();
			break;

		case R.id.home_offon:
			intent = new Intent(TheMainActivity.this, ControlActivity.class);
			intent.putExtra("no", 3);
			startActivity(intent);
			// TheMainActivity.this.finish();
			break;

		case R.id.home_common:
			intent = new Intent(TheMainActivity.this, SystemActivity.class);
			intent.putExtra("no", 0);
			startActivity(intent);
			// TheMainActivity.this.finish();
			break;

		case R.id.home_manage:
			intent = new Intent(TheMainActivity.this, SystemActivity.class);
			intent.putExtra("no", 1);
			startActivity(intent);
			// TheMainActivity.this.finish();
			break;

		case R.id.home_set:
			intent = new Intent(TheMainActivity.this, SystemActivity.class);
			intent.putExtra("no", 2);
			startActivity(intent);
			// TheMainActivity.this.finish();
			break;

		case R.id.home_record:
			intent = new Intent(TheMainActivity.this, SystemActivity.class);
			intent.putExtra("no", 3);
			startActivity(intent);
			// TheMainActivity.this.finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 屏幕触摸事件
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		// 按下，记录此时的位置
		case MotionEvent.ACTION_DOWN:
			startx = event.getX();
			starty = event.getY();
			break;

		// 抬起，算出滑动的横纵偏移量
		case MotionEvent.ACTION_UP:
			offsetx = event.getX() - startx;
			offsety = event.getY() - starty;
			offsetx = Math.abs(offsetx);
			offsety = Math.abs(offsety);
			if (isopen) {// 菜单已经展开
			// if((offsety>100)&&(offsety/offsetx > 3)){
				if (offsety > 100) {
					// 如果纵向偏移足够大则关闭菜单
					mCardView.onCardItemClick(child);
					isopen = !isopen;
				}
			} else {// 菜单已经关闭
			// if((offsety>100)&&(offsety/offsetx > 3)){
				if (offsety > 100) {
					// 如果纵向偏移足够大则关闭菜单
					int num = getChildNum(starty);// 根据起始位置计算点击\滑动的菜单编号
					if (num != 0) {
						mCardView.onCardItemClick(num);
						isopen = !isopen;
						child = num;
					}
				}
			}
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	/**
	 * 计算当前屏幕的各种View的高度
	 */
	private void getViewHigh() {
		int screenHeight = TheMainActivity.this.getResources()
				.getDisplayMetrics().heightPixels;// 屏幕高度

		itemTitleHeight = CardConfig.ITEM_TITLE_HEIGHT * screenHeight / 1280;// 卡片标题高度
		headerHeight = CardConfig.HEADER_HEIGHT * screenHeight / 1280;// 头部View高度
	}

	/**
	 * 根据起始位置计算点击\滑动的菜单编号
	 * @param starty   起始位置
	 * @return     菜单编号
	 */
	private int getChildNum(float starty) {
		int num = 0;
		if (starty < headerHeight) {
			num = 0;
		} else if (starty < headerHeight + itemTitleHeight) {
			num = 1;
		} else if (starty < headerHeight + itemTitleHeight * 2) {
			num = 2;
		} else if (starty < headerHeight + itemTitleHeight * 3) {
			num = 3;
		}
		return num;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isopen) {
				mCardView.onCardItemClick(child);
				isopen = !isopen;
			} else {
				System.exit(0);
			}
		}
		return false;
	}

}
