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
	/** ��С��View���� **/
	private static final int MIN_CHILD_COUNT = 3;
	/** ����top��� **/
	private static final int HIDE_TOP_MARGIN = CardConfig.HIDE_TOP_MARGIN;
	/** ��Ļ�߶� **/
	private int screenHeight;
	/** ��Ƭ����߶� **/
	private int itemTitleHeight;
	/** ��Ƭ�߶� **/
	private int itemHeight;
	/** ͷ��View�߶� **/
	private int headerHeight;
	/** β��View�߶� **/
	private int footerHeight;
	/** ��Ƭ����ʱtop **/
	private int hideTop;

	/** �Ƿ��ڶ����� **/
	private boolean onAnima = false;
	/** ��ǰչ��<��Ƭ>��index **/
	private int currentExpandIndex = -1;

	/** ��Ƭ�ı��¼� **/
	private OnCardChangeListener cardChangeListener;

	/**
	 * ���췽��
	 * 
	 * @param context
	 */
	public CardView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * ���췽��
	 * 
	 * @param context
	 * @param attrs
	 */
	public CardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * ��ʼ��
	 * 
	 * @Description:
	 * @Author justlcw
	 * @Date 2014-4-23
	 */
	private void init(Context context) {
		screenHeight = context.getResources().getDisplayMetrics().heightPixels;// ��Ļ�߶�

		itemTitleHeight = CardConfig.ITEM_TITLE_HEIGHT * screenHeight / 1280;// ��Ƭ����߶�
		itemHeight = CardConfig.ITEM_HEIGHT * screenHeight / 1280;// ��Ƭ�߶�
		headerHeight = CardConfig.HEADER_HEIGHT * screenHeight / 1280;// ͷ��View�߶�
		footerHeight = CardConfig.FOOTER_HEIGHT * screenHeight / 1280;// β��View�߶�
		hideTop = CardConfig.HIDE_TOP * screenHeight / 1280;// ��Ƭ����ʱtop
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		// ������<��View>����ӵ�ʱ��.
		int childCount = getChildCount();
		if (childCount < MIN_CHILD_COUNT) {
			Log.e(TAG,
					"error : this view need one header view, one footer view and one or more itemView at least.");
			return;
		}
		// ��ʼ��<��View>��״̬
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);

			CardItemStatus status;
			if (i == 0) {
				// ͷ��
				status = new CardItemStatus(0, 0, -headerHeight, headerHeight,
						i);
			} else if (i == childCount - 1) {
				// β��
				final int footerTop = getBottom() - footerHeight;
				status = new CardItemStatus(footerTop, footerTop, screenHeight,
						footerHeight, i);
			} else {
				// ��Ƭ
				status = new CardItemStatus(0, headerHeight + (i - 1)
						* itemTitleHeight, hideTop, itemHeight, i);
				child.setOnClickListener(mOnClickListener);
			}
			child.setTag(status);
		}
	}

	// private View.di

	/** ��Ƭ����¼�. **/
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			CardItemStatus status = (CardItemStatus) v.getTag();
			// ������Ҫ����������Ƭ�ָ�������,��:���صĿ�Ƭ,���۵���Ǹ���Ƭ,���ܹ�����.
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
			// ��Ȼ<ͷ><β><��Ƭ>�ĸ߶ȶ����趨����,����ֻ���������趨��
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

		// ����<��View>�����״̬<����>
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			CardItemStatus status = (CardItemStatus) child.getTag();
			child.layout(0, status.currentTop, w, status.currentTop
					+ status.height);
		}
	}

	/**
	 * ����<��Ƭ>�ı��¼�.
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
	 * @return {@link #currentExpandIndex},-1:û��չ��.
	 * @Description:
	 * @Author justlcw
	 */
	public int getCurrentExpandIndex() {
		return currentExpandIndex;
	}

	/**
	 * ����Ҫչʾ<��Ƭ>��ʱ�����.
	 * @param index  λ��.
	 * @Description:
	 * @Author justlcw
	 */
	public void onCardItemClick(int index) {
		int childCount = getChildCount();
		if (childCount < MIN_CHILD_COUNT || onAnima) {
			return;
		}
		// Ŀ��<��Ƭ>
		View target = null;
		// �ӿ�Ƭ��(������ͷ,β),Ѱ��Ŀ��<��Ƭ>
		for (int i = 1; i < childCount - 1; i++) {
			View child = getChildAt(i);
			CardItemStatus status = (CardItemStatus) child.getTag();
			if (status.status == CardItemStatus.STATUS_EXPAND) {
				// ���������<��Ƭ>����<չ��>״̬,�ر����<��Ƭ>
				child.startAnimation(new CardClickAnimation(status));
				return;
			} else if (status.index == index) {
				// ��� �ҵ�Ŀ��<��Ƭ>,���
				target = child;
			}
		}
		if (target != null) {
			// ����ҵ���,��ʼչ��
			CardItemStatus status = (CardItemStatus) target.getTag();
			target.startAnimation(new CardClickAnimation(status));
		}
	}

	/**
	 * ��Ƭ�������
	 * @Title:
	 * @Description:
	 * @Author:justlcw
	 * @Version:
	 */
	class CardClickAnimation extends Animation {
		/** ����ʱ��. **/
		private static final long DURATION = CardConfig.DURATION;
		/** ������. **/
		private static final int INTERPOLATOR_ID = CardConfig.INTERPOLATOR_ID;

		/** ��Ƭ״̬ **/
		private CardItemStatus status;

		/**
		 * ���췽��
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
					// ��չ�׶� (�ر�״̬top-չ��״̬top) * ��ǰ����ʱ�� = ���о���
					final float top = interpolatedTime
							* (float) (status.closeTop - status.expandTop);
					status.currentTop = status.closeTop - (int) top;// �ر�״̬top -
																	// ���о��� =
																	// ��ǰtop
				} else {
					// �رս׶� (�ر�״̬top-չ��״̬top) * ��ǰ����ʱ�� = ���о���
					final float top = interpolatedTime
							* (float) (status.closeTop - status.expandTop);
					status.currentTop = (int) top;// ���о��� = ��ǰtop
				}
				// ��������view
				onAnimation(interpolatedTime, status);
			}
			// ���²���
			requestLayout();
		}
	}

	/**
	 * ������ʼ.
	 * @Description:
	 * @Author justlcw
	 */
	private void onAnimationStart(CardItemStatus status) {
		if (status.status == CardItemStatus.STATUS_CLOSING) {
			// �ָ�ʱ,����top�Ѿ������ú�,����Ҫ��������һ��.
			return;
		}
		int delTop = 0;// �ݼ�ֵ

		final int childCount = getChildCount();
		// <ͷ><β>ȫ������,����������
		for (int i = 1; i < childCount - 1; i++) {
			if (i != status.index) {
				CardItemStatus otherStatus = (CardItemStatus) getChildAt(i)
						.getTag();
				otherStatus.hideTop = hideTop + delTop;
				delTop += HIDE_TOP_MARGIN;
			}
		}
		// �������ǰ����ص�top�ݹ������һ��,������Ƭ�������в�θ�<��Ҫ����û�б�Ҫ,�߱�������ô�ɵ�>
		// ���ŵ���˵,���Ǻ���Ӧ�ø�ԭ����topֵ,����û�б�Ҫ,��Ϊֻ�����ֳ���
		// 1:���翨Ƭչ��,�ò���hideTop
		// 2:���翨Ƭ����,hideTop�ֻ�������һ��������,����û�б�Ҫ
	}

	/**
	 * �����ر�.
	 * @Description:
	 * @Author justlcw
	 */
	private void onAnimationEnd(CardItemStatus status) {
		for (int i = 0; i < getChildCount(); i++) {
			if (i != status.index) {
				CardItemStatus otherStatus = (CardItemStatus) getChildAt(i)
						.getTag();

				if (status.status == CardItemStatus.STATUS_EXPAND) {
					// �������
					otherStatus.currentTop = otherStatus.hideTop;
				} else if (status.status == CardItemStatus.STATUS_CLOSE) {
					// ��ɻָ�
					otherStatus.currentTop = otherStatus.closeTop;
				}
			}
		}
	}

	/**
	 * ��������ʱ, ��������<��Ƭ>��״̬
	 * @param interpolatedTime  ��ǰ��������ʱ��
	 * @param status  ��ǰ��ʾ<��Ƭ>��״̬
	 * @Description:
	 * @Author justlcw
	 */
	private void onAnimation(float interpolatedTime, CardItemStatus status) {
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			// ������ǵ�ǰ<��Ƭ>
			if (i != status.index) {
				CardItemStatus otherStatus = (CardItemStatus) getChildAt(i)
						.getTag();

				if (status.status == CardItemStatus.STATUS_EXPANDING) {
					// �и��һ�չ����,��ô�ҵȾ�����. (����״̬top-չ��״̬top) * ��ǰ����ʱ�� = ���о���
					final float top = interpolatedTime
							* (float) (otherStatus.hideTop - otherStatus.closeTop);
					otherStatus.currentTop = otherStatus.closeTop + (int) top;// �ر�״̬top
																				// +
																				// ���о���
																				// =
																				// ��ǰtop
				} else {
					// �и��һ�ر���,��ô�ҵȾͻָ�. (����״̬top-չ��״̬top) * ��ǰ����ʱ�� = ���о���
					final float top = interpolatedTime
							* (float) (otherStatus.hideTop - otherStatus.closeTop);
					otherStatus.currentTop = otherStatus.hideTop - (int) top;// ����״̬top
																				// -
																				// ���о���
																				// =
																				// ��ǰtop
				}
			}
		}
	}

	/**
	 * ��Ƭ״̬
	 * @Title:
	 * @Description:
	 * @Author:justlcw
	 * @Version:
	 */
	class CardItemStatus {
		/** չ��ʱtop **/
		public int expandTop;
		/** �ر�ʱtop **/
		public int closeTop;
		/** ����ʱtop **/
		public int hideTop;

		/** ��ǰtop **/
		public int currentTop;

		/** ��Ƭ�߶� **/
		public int height;

		/** ��Ƭλ�� **/
		private int index;

		/** ��Ƭ����״̬ **/
		public int status = STATUS_CLOSE;
		/** �ر� **/
		private static final int STATUS_CLOSE = 1;
		/** չ�� **/
		private static final int STATUS_EXPAND = 2;
		/** ���ڹر� **/
		private static final int STATUS_CLOSING = 3;
		/** ����չ�� **/
		private static final int STATUS_EXPANDING = 4;

		/**
		 * ���췽��
		 * @param expandTop չ��ʱtop
		 * @param closeTop  �ر�ʱtop
		 * @param hideTop   ����ʱtop
		 * @param height    ��Ƭ�߶�
		 * @param index     ��Ƭλ��
		 */
		public CardItemStatus(int expandTop, int closeTop, int hideTop,
				int height, int index) {
			this.expandTop = expandTop;
			this.closeTop = closeTop;
			this.hideTop = hideTop;

			this.height = height;
			this.index = index;

			// ��ʼ������
			this.status = STATUS_CLOSE;
			this.currentTop = closeTop;
		}

		/**
		 * ������ʼ��ʱ��
		 * @Description:
		 * @Author justlcw
		 */
		public void onAnimationStart() {
			if (status == STATUS_CLOSE) {
				// ��ʼչ����ʱ��
				status = STATUS_EXPANDING;
				currentTop = closeTop;

				// �¼�����
				if (cardChangeListener != null) {
					cardChangeListener.onChangeStart(index, true);
				}
			} else if (status == STATUS_EXPAND) {
				// ��ʼ�رյ�ʱ��
				status = STATUS_CLOSING;
				currentTop = expandTop;

				// �¼�����
				if (cardChangeListener != null) {
					cardChangeListener.onChangeStart(index, false);
				}
			}
			onAnima = true;
		}

		/**
		 * ����������ʱ��
		 * @Description:
		 * @Author justlcw
		 */
		public void onAnimationEnd() {
			if (status == STATUS_CLOSING) {
				// �ر���ɵ�ʱ��
				status = STATUS_CLOSE;
				currentTop = closeTop;

				// �¼�����
				if (cardChangeListener != null) {
					cardChangeListener.onChangeEnd(index, false);
				}
				currentExpandIndex = -1;
			} else if (status == STATUS_EXPANDING) {
				// չ�����ʱ
				status = STATUS_EXPAND;
				currentTop = expandTop;

				// �¼�����
				if (cardChangeListener != null) {
					cardChangeListener.onChangeEnd(index, true);
				}
				currentExpandIndex = index;
			}
			onAnima = false;
		}
	}

	/**
	 * <��Ƭ>�ı��¼�
	 * @Title:
	 * @Description:
	 * @Author:justlcw
	 * @Version:
	 */
	public interface OnCardChangeListener {
		/**
		 * ��ʼ�䶯��ʱ��
		 * @param index  ��ǰ<��Ƭ>��index
		 * @param expand  true,��ʼչ��;false,��ʼ�ر�
		 * @Description:
		 * @Author justlcw
		 */
		void onChangeStart(int index, boolean expand);

		/**
		 * �䶯������ʱ��
		 * @param index  ��ǰ<��Ƭ>��index
		 * @param expand  true,չ������;false,�رս���
		 * @Description:
		 * @Author justlcw
		 */
		void onChangeEnd(int index, boolean expand);
	}
}
