package com.xtoee.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CardView extends ViewGroup {
	/** log tag. **/
	private static final String TAG = CardView.class.getSimpleName();
	/** 最小子View数量 **/
	private static final int MIN_CHILD_COUNT = 3;
	/** 隐藏top间隔 **/
	private static final int HIDE_TOP_MARGIN = CardConfig.HIDE_TOP_MARGIN;
	/** 屏幕高度 **/
	private int screenHeight;
	/** 卡片标题高度 **/
	private int itemTitleHeight;
	/** 卡片高度 **/
	private int itemHeight;
	/** 头部View高度 **/
	private int headerHeight;
	/** 尾部View高度 **/
	private int footerHeight;
	/** 卡片隐藏时top **/
	private int hideTop;

	/** 是否处于动画中 **/
	private boolean onAnima = false;
	/** 当前展开<卡片>的index **/
	private int currentExpandIndex = -1;

	/** 卡片改变事件 **/
	private OnCardChangeListener cardChangeListener;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public CardView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public CardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 初始化
	 * 
	 * @Description:
	 * @Author justlcw
	 * @Date 2014-4-23
	 */
	private void init(Context context) {
		screenHeight = context.getResources().getDisplayMetrics().heightPixels;// 屏幕高度

		itemTitleHeight = CardConfig.ITEM_TITLE_HEIGHT * screenHeight / 1280;// 卡片标题高度
		itemHeight = CardConfig.ITEM_HEIGHT * screenHeight / 1280;// 卡片高度
		headerHeight = CardConfig.HEADER_HEIGHT * screenHeight / 1280;// 头部View高度
		footerHeight = CardConfig.FOOTER_HEIGHT * screenHeight / 1280;// 尾部View高度
		hideTop = CardConfig.HIDE_TOP * screenHeight / 1280;// 卡片隐藏时top
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		// 当所有<子View>都添加的时候.
		int childCount = getChildCount();
		if (childCount < MIN_CHILD_COUNT) {
			Log.e(TAG,
					"error : this view need one header view, one footer view and one or more itemView at least.");
			return;
		}
		// 初始化<子View>的状态
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);

			CardItemStatus status;
			if (i == 0) {
				// 头部
				status = new CardItemStatus(0, 0, -headerHeight, headerHeight,
						i);
			} else if (i == childCount - 1) {
				// 尾部
				final int footerTop = getBottom() - footerHeight;
				status = new CardItemStatus(footerTop, footerTop, screenHeight,
						footerHeight, i);
			} else {
				// 卡片
				status = new CardItemStatus(0, headerHeight + (i - 1)
						* itemTitleHeight, hideTop, itemHeight, i);
				child.setOnClickListener(mOnClickListener);
			}
			child.setTag(status);
		}
	}

	// private View.di

	/** 卡片点击事件. **/
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			CardItemStatus status = (CardItemStatus) v.getTag();
			// 这里主要是用来扩大卡片恢复的热区,即:隐藏的卡片,无论点击那个卡片,都能够返回.
			if (currentExpandIndex != -1 && currentExpandIndex != status.index) {
				onCardItemClick(currentExpandIndex);
			}
		}
	};

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int childCount = getChildCount();
		if (childCount < MIN_CHILD_COUNT) {
			Log.e(TAG,
					"error : this view need one header view, one footer view and one or more itemView at least.");
			return;
		}
		for (int i = 0; i < childCount; i++) {
			// 既然<头><尾><卡片>的高度都被设定死了,这里只能往死里设定了
			final int eHeightMeasureSpec;
			View child = getChildAt(i);
			if (i == 0) {
				eHeightMeasureSpec = MeasureSpec.makeMeasureSpec(headerHeight,
						MeasureSpec.EXACTLY);
			} else if (i == childCount - 1) {
				eHeightMeasureSpec = MeasureSpec.makeMeasureSpec(footerHeight,
						MeasureSpec.EXACTLY);
			} else {
				eHeightMeasureSpec = MeasureSpec.makeMeasureSpec(itemHeight,
						MeasureSpec.EXACTLY);
			}
			child.measure(widthMeasureSpec, eHeightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childCount = getChildCount();
		if (childCount < MIN_CHILD_COUNT) {
			Log.e(TAG,
					"error : this view need one header view, one footer view and one or more itemView at least.");
			return;
		}
		final int w = r - l;

		// 根据<子View>自身的状态<布局>
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			CardItemStatus status = (CardItemStatus) child.getTag();
			child.layout(0, status.currentTop, w, status.currentTop
					+ status.height);
		}
	}

	/**
	 * 设置<卡片>改变事件.
	 * 
	 * @Description:
	 * @Author justlcw
	 */
	public void setOnCardChangeListener(OnCardChangeListener listener) {
		cardChangeListener = listener;
	}

	/**
	 * @return {@link #headerHeight}
	 * 
	 * @Description:
	 * @Author justlcw
	 */
	public int getHeaderHeight() {
		return headerHeight;
	}

	/**
	 * @return {@link #itemTitleHeight}
	 * @Description:
	 * @Author justlcw
	 */
	public int getCardItemTitleHeight() {
		return itemTitleHeight;
	}

	/**
	 * @return {@link #itemHeight}
	 * 
	 * @Description:
	 * @Author justlcw
	 */
	public int getCardItemHeight() {
		return itemHeight;
	}

	/**
	 * @return {@link #currentExpandIndex},-1:没有展开.
	 * @Description:
	 * @Author justlcw
	 */
	public int getCurrentExpandIndex() {
		return currentExpandIndex;
	}

	/**
	 * 当需要展示<卡片>的时候调用.
	 * @param index  位置.
	 * @Description:
	 * @Author justlcw
	 */
	public void onCardItemClick(int index) {
		int childCount = getChildCount();
		if (childCount < MIN_CHILD_COUNT || onAnima) {
			return;
		}
		// 目标<卡片>
		View target = null;
		// 从卡片中(不包括头,尾),寻找目标<卡片>
		for (int i = 1; i < childCount - 1; i++) {
			View child = getChildAt(i);
			CardItemStatus status = (CardItemStatus) child.getTag();
			if (status.status == CardItemStatus.STATUS_EXPAND) {
				// 如果发现有<卡片>处于<展开>状态,关闭这个<卡片>
				child.startAnimation(new CardClickAnimation(status));
				return;
			} else if (status.index == index) {
				// 如果 找到目标<卡片>,标记
				target = child;
			}
		}
		if (target != null) {
			// 如果找到了,开始展开
			CardItemStatus status = (CardItemStatus) target.getTag();
			target.startAnimation(new CardClickAnimation(status));
		}
	}

	/**
	 * 卡片点击动画
	 * @Title:
	 * @Description:
	 * @Author:justlcw
	 * @Version:
	 */
	class CardClickAnimation extends Animation {
		/** 动画时长. **/
		private static final long DURATION = CardConfig.DURATION;
		/** 加速器. **/
		private static final int INTERPOLATOR_ID = CardConfig.INTERPOLATOR_ID;

		/** 卡片状态 **/
		private CardItemStatus status;

		/**
		 * 构造方法
		 * @param status  {@link CardItemStatus}
		 */
		public CardClickAnimation(CardItemStatus status) {
			this.status = status;

			setDuration(DURATION);
			setInterpolator(getContext(), INTERPOLATOR_ID);
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			if (interpolatedTime == 0f && !hasStarted()) {
				status.onAnimationStart();
				onAnimationStart(status);
				Log.d(TAG, "animation start");
			} else if (interpolatedTime >= 1f && hasEnded()) {
				status.onAnimationEnd();
				onAnimationEnd(status);
				Log.d(TAG, "animation end");
			} else {
				if (status.status == CardItemStatus.STATUS_EXPANDING) {
					// 扩展阶段 (关闭状态top-展开状态top) * 当前运行时间 = 运行距离
					final float top = interpolatedTime
							* (float) (status.closeTop - status.expandTop);
					status.currentTop = status.closeTop - (int) top;// 关闭状态top -
																	// 运行距离 =
																	// 当前top
				} else {
					// 关闭阶段 (关闭状态top-展开状态top) * 当前运行时间 = 运行距离
					final float top = interpolatedTime
							* (float) (status.closeTop - status.expandTop);
					status.currentTop = (int) top;// 运行距离 = 当前top
				}
				// 处理其他view
				onAnimation(interpolatedTime, status);
			}
			// 重新布局
			requestLayout();
		}
	}

	/**
	 * 动画开始.
	 * @Description:
	 * @Author justlcw
	 */
	private void onAnimationStart(CardItemStatus status) {
		if (status.status == CardItemStatus.STATUS_CLOSING) {
			// 恢复时,隐藏top已经被设置好,不需要重新设置一遍.
			return;
		}
		int delTop = 0;// 递减值

		final int childCount = getChildCount();
		// <头><尾>全部隐藏,不计算在内
		for (int i = 1; i < childCount - 1; i++) {
			if (i != status.index) {
				CardItemStatus otherStatus = (CardItemStatus) getChildAt(i)
						.getTag();
				otherStatus.hideTop = hideTop + delTop;
				delTop += HIDE_TOP_MARGIN;
			}
		}
		// 上面我们把隐藏的top递归减少了一点,这样卡片看起来有层次感<不要问有没有必要,高保真是这么干的>
		// 按着道理说,我们后面应该复原隐藏top值,但是没有必要,因为只有两种场景
		// 1:假如卡片展开,用不到hideTop
		// 2:假如卡片隐藏,hideTop又会像上面一样被重置,所有没有必要
	}

	/**
	 * 动画关闭.
	 * @Description:
	 * @Author justlcw
	 */
	private void onAnimationEnd(CardItemStatus status) {
		for (int i = 0; i < getChildCount(); i++) {
			if (i != status.index) {
				CardItemStatus otherStatus = (CardItemStatus) getChildAt(i)
						.getTag();

				if (status.status == CardItemStatus.STATUS_EXPAND) {
					// 完成隐藏
					otherStatus.currentTop = otherStatus.hideTop;
				} else if (status.status == CardItemStatus.STATUS_CLOSE) {
					// 完成恢复
					otherStatus.currentTop = otherStatus.closeTop;
				}
			}
		}
	}

	/**
	 * 动画运行时, 处理其他<卡片>的状态
	 * @param interpolatedTime  当前动画运行时长
	 * @param status  当前显示<卡片>的状态
	 * @Description:
	 * @Author justlcw
	 */
	private void onAnimation(float interpolatedTime, CardItemStatus status) {
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			// 如果不是当前<卡片>
			if (i != status.index) {
				CardItemStatus otherStatus = (CardItemStatus) getChildAt(i)
						.getTag();

				if (status.status == CardItemStatus.STATUS_EXPANDING) {
					// 有个家伙展开了,那么我等就隐藏. (隐藏状态top-展开状态top) * 当前运行时间 = 运行距离
					final float top = interpolatedTime
							* (float) (otherStatus.hideTop - otherStatus.closeTop);
					otherStatus.currentTop = otherStatus.closeTop + (int) top;// 关闭状态top
																				// +
																				// 运行距离
																				// =
																				// 当前top
				} else {
					// 有个家伙关闭了,那么我等就恢复. (隐藏状态top-展开状态top) * 当前运行时间 = 运行距离
					final float top = interpolatedTime
							* (float) (otherStatus.hideTop - otherStatus.closeTop);
					otherStatus.currentTop = otherStatus.hideTop - (int) top;// 隐藏状态top
																				// -
																				// 运行距离
																				// =
																				// 当前top
				}
			}
		}
	}

	/**
	 * 卡片状态
	 * @Title:
	 * @Description:
	 * @Author:justlcw
	 * @Version:
	 */
	class CardItemStatus {
		/** 展开时top **/
		public int expandTop;
		/** 关闭时top **/
		public int closeTop;
		/** 隐藏时top **/
		public int hideTop;

		/** 当前top **/
		public int currentTop;

		/** 卡片高度 **/
		public int height;

		/** 卡片位置 **/
		private int index;

		/** 卡片运行状态 **/
		public int status = STATUS_CLOSE;
		/** 关闭 **/
		private static final int STATUS_CLOSE = 1;
		/** 展开 **/
		private static final int STATUS_EXPAND = 2;
		/** 正在关闭 **/
		private static final int STATUS_CLOSING = 3;
		/** 正在展开 **/
		private static final int STATUS_EXPANDING = 4;

		/**
		 * 构造方法
		 * @param expandTop 展开时top
		 * @param closeTop  关闭时top
		 * @param hideTop   隐藏时top
		 * @param height    卡片高度
		 * @param index     卡片位置
		 */
		public CardItemStatus(int expandTop, int closeTop, int hideTop,
				int height, int index) {
			this.expandTop = expandTop;
			this.closeTop = closeTop;
			this.hideTop = hideTop;

			this.height = height;
			this.index = index;

			// 初始化数据
			this.status = STATUS_CLOSE;
			this.currentTop = closeTop;
		}

		/**
		 * 动画开始的时候
		 * @Description:
		 * @Author justlcw
		 */
		public void onAnimationStart() {
			if (status == STATUS_CLOSE) {
				// 开始展开的时候
				status = STATUS_EXPANDING;
				currentTop = closeTop;

				// 事件调用
				if (cardChangeListener != null) {
					cardChangeListener.onChangeStart(index, true);
				}
			} else if (status == STATUS_EXPAND) {
				// 开始关闭的时候
				status = STATUS_CLOSING;
				currentTop = expandTop;

				// 事件调用
				if (cardChangeListener != null) {
					cardChangeListener.onChangeStart(index, false);
				}
			}
			onAnima = true;
		}

		/**
		 * 动画结束的时候
		 * @Description:
		 * @Author justlcw
		 */
		public void onAnimationEnd() {
			if (status == STATUS_CLOSING) {
				// 关闭完成的时候
				status = STATUS_CLOSE;
				currentTop = closeTop;

				// 事件调用
				if (cardChangeListener != null) {
					cardChangeListener.onChangeEnd(index, false);
				}
				currentExpandIndex = -1;
			} else if (status == STATUS_EXPANDING) {
				// 展开完成时
				status = STATUS_EXPAND;
				currentTop = expandTop;

				// 事件调用
				if (cardChangeListener != null) {
					cardChangeListener.onChangeEnd(index, true);
				}
				currentExpandIndex = index;
			}
			onAnima = false;
		}
	}

	/**
	 * <卡片>改变事件
	 * @Title:
	 * @Description:
	 * @Author:justlcw
	 * @Version:
	 */
	public interface OnCardChangeListener {
		/**
		 * 开始变动的时候
		 * @param index  当前<卡片>的index
		 * @param expand  true,开始展开;false,开始关闭
		 * @Description:
		 * @Author justlcw
		 */
		void onChangeStart(int index, boolean expand);

		/**
		 * 变动结束的时候
		 * @param index  当前<卡片>的index
		 * @param expand  true,展开结束;false,关闭结束
		 * @Description:
		 * @Author justlcw
		 */
		void onChangeEnd(int index, boolean expand);
	}
}
