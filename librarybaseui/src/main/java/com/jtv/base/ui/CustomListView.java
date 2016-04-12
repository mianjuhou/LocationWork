package com.jtv.base.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.plutus.libraryui.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * ListView下拉刷新和加载更多
 * 
 */
public class CustomListView extends ListView implements OnScrollListener {

	/** 显示格式化日期模板 */
	private final static String DATE_FORMAT_STR = "yyyy年MM月dd日 HH:mm";

	/** 实际的padding的距离与界面上偏移距离的比例 */
	private final static int RATIO = 3;

	private final static int RELEASE_TO_REFRESH = 0;
	private final static int PULL_TO_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	/** 加载中 */
	private final static int ENDINT_LOADING = 1;
	/** 手动完成刷新 */
	private final static int ENDINT_MANUAL_LOAD_DONE = 2;
	/** 自动完成刷新 */
	private final static int ENDINT_AUTO_LOAD_DONE = 3;
	private int mHeadState;
	private int mEndState;

	// ================================= 功能设置Flag
	// ================================

	/** 可以加载更多？ */
	private boolean mCanLoadMore = false;
	/** 可以下拉刷新？ */
	private boolean mCanRefresh = false;
	/** 可以自动加载更多吗？（注意，先判断是否有加载更多，如果没有，这个flag也没有意义） */
	private boolean mIsAutoLoadMore = true;
	/** 下拉刷新后是否显示第一条Item */
	private boolean mIsMoveToFirstItemAfterRefresh = false;
	private TextView load_more_nodata;

	public boolean isCanLoadMore() {
		return mCanLoadMore;
	}

	public void setCanLoadMore(boolean pCanLoadMore) {
		mCanLoadMore = pCanLoadMore;
		if (mCanLoadMore && getFooterViewsCount() == 0) {
			addFooterView();
		}
	}

	public boolean isCanRefresh() {
		return mCanRefresh;
	}

	public void setCanRefresh(boolean pCanRefresh) {
		mCanRefresh = pCanRefresh;
	}

	public boolean isAutoLoadMore() {
		return mIsAutoLoadMore;
	}

	public void setAutoLoadMore(boolean pIsAutoLoadMore) {
		mIsAutoLoadMore = pIsAutoLoadMore;
	}

	public boolean isMoveToFirstItemAfterRefresh() {
		return mIsMoveToFirstItemAfterRefresh;
	}

	public void setMoveToFirstItemAfterRefresh(
			boolean pIsMoveToFirstItemAfterRefresh) {
		mIsMoveToFirstItemAfterRefresh = pIsMoveToFirstItemAfterRefresh;
	}

	private LayoutInflater mInflater;

	private LinearLayout mHeadView;
	private TextView mTipsTextView;
	private TextView mLastUpdatedTextView;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;

	private View mEndRootView;
	private ProgressBar mEndLoadProgressBar;
	private TextView mEndLoadTipsTextView;

	/** headView动画 */
	private RotateAnimation mArrowAnim;
	/** headView反转动画 */
	private RotateAnimation mArrowReverseAnim;

	/** 用于保证startY的值在一个完整的touch事件中只被记录一次 */
	private boolean mIsRecored;

	private int mHeadViewWidth;
	private int mHeadViewHeight;

	private int mStartY;
	private boolean mIsBack;

	private int mFirstItemIndex;
	private int mLastItemIndex;
	private int mCount;
	private boolean mEnoughCount;// 足够数量充满屏幕？

	private OnRefreshListener mRefreshListener;
	private OnLoadMoreListener mLoadMoreListener;

	public CustomListView(Context pContext, AttributeSet pAttrs) {
		super(pContext, pAttrs);
		init(pContext);
	}

	public CustomListView(Context pContext) {
		super(pContext);
		init(pContext);
	}

	public CustomListView(Context pContext, AttributeSet pAttrs, int pDefStyle) {
		super(pContext, pAttrs, pDefStyle);
		init(pContext);
	}

	/**
	 * 初始化操作
	 */
	private void init(Context pContext) {
		setCacheColorHint(pContext.getResources().getColor(
				R.color.about_darkgrey));
		mInflater = LayoutInflater.from(pContext);

		addHeadView();

		setOnScrollListener(this);

		initPullImageAnimation(0);
	}

	/**
	 * 添加下拉刷新的HeadView
	 */
	private void addHeadView() {
		mHeadView = (LinearLayout) mInflater.inflate(
				R.layout.base_pulllistview_head, null);

		mArrowImageView = (ImageView) mHeadView
				.findViewById(R.id.head_arrowImageView);
		mArrowImageView.setMinimumWidth(70);
		mArrowImageView.setMinimumHeight(50);
		mProgressBar = (ProgressBar) mHeadView
				.findViewById(R.id.head_progressBar);
		mTipsTextView = (TextView) mHeadView
				.findViewById(R.id.head_tipsTextView);
		mLastUpdatedTextView = (TextView) mHeadView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(mHeadView);
		mHeadViewHeight = mHeadView.getMeasuredHeight();
		mHeadViewWidth = mHeadView.getMeasuredWidth();

		mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
		mHeadView.invalidate();

		addHeaderView(mHeadView, null, false);

		mHeadState = DONE;
	}

	/**
	 * 添加加载更多FootView
	 */
	private void addFooterView() {
		mEndRootView = mInflater.inflate(R.layout.base_pulllistfooter_more,
				null);
		mEndRootView.setVisibility(View.VISIBLE);
		mEndLoadProgressBar = (ProgressBar) mEndRootView
				.findViewById(R.id.pull_to_refresh_progress);
		mEndLoadTipsTextView = (TextView) mEndRootView
				.findViewById(R.id.load_more);
		load_more_nodata = (TextView) mEndRootView
				.findViewById(R.id.load_more_nodata);
		mEndRootView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCanLoadMore) {
					if (mCanRefresh) {
						// 当可以下拉刷新时，如果FootView没有正在加载，并且HeadView没有正在刷新，才可以点击加载更多。
						if (mEndState != ENDINT_LOADING
								&& mHeadState != REFRESHING) {
							mEndState = ENDINT_LOADING;
							onLoadMore();
						}
					} else if (mEndState != ENDINT_LOADING) {
						// 当不能下拉刷新时，FootView不正在加载时，才可以点击加载更多。
						mEndState = ENDINT_LOADING;
						onLoadMore();
					}
				}
			}
		});

		addFooterView(mEndRootView);

		if (mIsAutoLoadMore) {
			mEndState = ENDINT_AUTO_LOAD_DONE;
		} else {
			mEndState = ENDINT_MANUAL_LOAD_DONE;
		}
	}

	/**
	 * 实例化下拉刷新的箭头的动画效果
	 */
	private void initPullImageAnimation(final int pAnimDuration) {

		int _Duration;

		if (pAnimDuration > 0) {
			_Duration = pAnimDuration;
		} else {
			_Duration = 250;
		}

		Interpolator _Interpolator = new LinearInterpolator();

		mArrowAnim = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mArrowAnim.setInterpolator(_Interpolator);
		mArrowAnim.setDuration(_Duration);
		mArrowAnim.setFillAfter(true);

		mArrowReverseAnim = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mArrowReverseAnim.setInterpolator(_Interpolator);
		mArrowReverseAnim.setDuration(_Duration);
		mArrowReverseAnim.setFillAfter(true);
	}

	/**
	 * 测量HeadView宽高(注意：此方法仅适用于LinearLayout，请读者自己测试验证。)
	 */
	private void measureView(View pChild) {
		ViewGroup.LayoutParams p = pChild.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;

		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		pChild.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * 为了判断滑动到ListView底部没
	 */
	@Override
	public void onScroll(AbsListView pView, int pFirstVisibleItem,
			int pVisibleItemCount, int pTotalItemCount) {
		mFirstItemIndex = pFirstVisibleItem;
		mLastItemIndex = pFirstVisibleItem + pVisibleItemCount - 2;
		mCount = pTotalItemCount - 2;
		if (pTotalItemCount > pVisibleItemCount) {
			mEnoughCount = true;
			// endingView.setVisibility(View.VISIBLE);
		} else {
			mEnoughCount = false;
		}
	}

	/**
	 * 这个方法，可能有点乱，大家多读几遍就明白了。
	 */
	@Override
	public void onScrollStateChanged(AbsListView pView, int pScrollState) {
		if (mCanLoadMore) {// 存在加载更多功能
			if (mLastItemIndex == mCount && pScrollState == SCROLL_STATE_IDLE) {
				// SCROLL_STATE_IDLE=0，滑动停止
				if (mEndState != ENDINT_LOADING) {
					if (mIsAutoLoadMore) {// 自动加载更多，我们让FootView显示 “更 多”
						if (mCanRefresh) {
							// 存在下拉刷新并且HeadView没有正在刷新时，FootView可以自动加载更多。
							if (mHeadState != REFRESHING) {
								// FootView显示 : 更 多 ---> 加载中...
								mEndState = ENDINT_LOADING;
								onLoadMore();
								changeEndViewByState();
							}
						} else {// 没有下拉刷新，我们直接进行加载更多。
								// FootView显示 : 更 多 ---> 加载中...
							mEndState = ENDINT_LOADING;
							onLoadMore();
							changeEndViewByState();
						}
					} else {// 不是自动加载更多，我们让FootView显示 “点击加载”
							// FootView显示 : 点击加载 ---> 加载中...
						mEndState = ENDINT_MANUAL_LOAD_DONE;
						changeEndViewByState();
					}
				}
			}
		} else if (mEndRootView != null
				&& mEndRootView.getVisibility() == VISIBLE) {
			// 突然关闭加载更多功能之后，我们要移除FootView。
			mEndRootView.setVisibility(View.GONE);
			this.removeFooterView(mEndRootView);
		}
	}

	/**
	 * 改变加载更多状态
	 */
	private void changeEndViewByState() {
		if (mCanLoadMore) {
			load_more_nodata.setVisibility(View.GONE);
			// 允许加载更多
			switch (mEndState) {
			case ENDINT_LOADING:// 刷新中

				// 加载中...
				if (mEndLoadTipsTextView.getText().equals("加载中…")) {
					break;
				}
				mEndLoadTipsTextView.setText("加载中…");
				mEndLoadTipsTextView.setVisibility(View.VISIBLE);
				mEndLoadProgressBar.setVisibility(View.VISIBLE);
				break;
			case ENDINT_MANUAL_LOAD_DONE:// 手动刷新完成

				// 点击加载
				mEndLoadTipsTextView.setText("点击加载");
				mEndLoadTipsTextView.setVisibility(View.VISIBLE);
				mEndLoadProgressBar.setVisibility(View.GONE);

				mEndRootView.setVisibility(View.VISIBLE);
				break;
			case ENDINT_AUTO_LOAD_DONE:// 自动刷新完成

				// 更 多
				mEndLoadTipsTextView.setText("更 多");
				mEndLoadTipsTextView.setVisibility(View.GONE);//
				mEndLoadProgressBar.setVisibility(View.GONE);

				mEndRootView.setVisibility(View.VISIBLE);
				break;
			default:
				// 原来的代码是为了： 当所有item的高度小于ListView本身的高度时，
				// 要隐藏掉FootView，大家自己去原作者的代码参考。

				// if (enoughCount) {
				// endRootView.setVisibility(View.VISIBLE);
				// } else {
				// endRootView.setVisibility(View.GONE);
				// }
				break;
			}
		}
	}

	/**
	 * 原作者的，我没改动，请读者自行优化。
	 */
	public boolean onTouchEvent(MotionEvent event) {

		if (mCanRefresh) {
			if (mCanLoadMore && mEndState == ENDINT_LOADING) {
				// 如果存在加载更多功能，并且当前正在加载更多，默认不允许下拉刷新，必须加载完毕后才能使用。
				return super.onTouchEvent(event);
			}

			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				if (mFirstItemIndex == 0 && !mIsRecored) {
					mIsRecored = true;
					mStartY = (int) event.getY();
				}
				break;

			case MotionEvent.ACTION_UP:

				if (mHeadState != REFRESHING && mHeadState != LOADING) {
					if (mHeadState == DONE) {

					}
					if (mHeadState == PULL_TO_REFRESH) {
						mHeadState = DONE;
						changeHeaderViewByState();
					}
					if (mHeadState == RELEASE_TO_REFRESH) {
						mHeadState = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
					}
				}

				mIsRecored = false;
				mIsBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();

				if (!mIsRecored && mFirstItemIndex == 0) {
					mIsRecored = true;
					mStartY = tempY;
				}

				if (mHeadState != REFRESHING && mIsRecored
						&& mHeadState != LOADING) {

					// 保证在设置padding的过程中，当前的位置一直是在head，
					// 否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
					// 可以松手去刷新了
					if (mHeadState == RELEASE_TO_REFRESH) {

						setSelection(0);

						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - mStartY) / RATIO < mHeadViewHeight)
								&& (tempY - mStartY) > 0) {
							mHeadState = PULL_TO_REFRESH;
							changeHeaderViewByState();
						}
						// 一下子推到顶了
						else if (tempY - mStartY <= 0) {
							mHeadState = DONE;
							changeHeaderViewByState();
						}
						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (mHeadState == PULL_TO_REFRESH) {

						setSelection(0);

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - mStartY) / RATIO >= mHeadViewHeight) {
							mHeadState = RELEASE_TO_REFRESH;
							mIsBack = true;
							changeHeaderViewByState();
						} else if (tempY - mStartY <= 0) {
							mHeadState = DONE;
							changeHeaderViewByState();
						}
					}

					if (mHeadState == DONE) {
						if (tempY - mStartY > 0) {
							mHeadState = PULL_TO_REFRESH;
							changeHeaderViewByState();
						}
					}

					if (mHeadState == PULL_TO_REFRESH) {
						mHeadView.setPadding(0, -1 * mHeadViewHeight
								+ (tempY - mStartY) / RATIO, 0, 0);

					}

					if (mHeadState == RELEASE_TO_REFRESH) {
						mHeadView.setPadding(0, (tempY - mStartY) / RATIO
								- mHeadViewHeight, 0, 0);
					}
				}
				break;
			}
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 当HeadView状态改变时候，调用该方法，以更新界面
	 */
	private void changeHeaderViewByState() {
		switch (mHeadState) {
		case RELEASE_TO_REFRESH:
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
			mTipsTextView.setVisibility(View.VISIBLE);
			// mLastUpdatedTextView.setVisibility(View.VISIBLE);

			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(mArrowAnim);
			// 松开刷新
			mTipsTextView.setText("松开刷新");

			break;
		case PULL_TO_REFRESH:
			mProgressBar.setVisibility(View.GONE);
			mTipsTextView.setVisibility(View.VISIBLE);
			// mLastUpdatedTextView.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (mIsBack) {
				mIsBack = false;
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mArrowReverseAnim);
				// 下拉刷新
				mTipsTextView.setText("下拉刷新");
			} else {
				// 下拉刷新
				mTipsTextView.setText("下拉刷新");
			}
			break;

		case REFRESHING:
			mHeadView.setPadding(0, 0, 0, 0);

			mProgressBar.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.GONE);
			// 正在刷新...
			mTipsTextView.setText("正在刷新...");
			// mLastUpdatedTextView.setVisibility(View.VISIBLE);

			break;
		case DONE:
			mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);

			// 此处可以改进，同上所述。

			mProgressBar.setVisibility(View.GONE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setImageResource(R.drawable.ic_listview_arrow);
			// 下拉刷新
			mTipsTextView.setText("下拉刷新");
			// mLastUpdatedTextView.setVisibility(View.VISIBLE);

			break;
		}
	}

	/**
	 * 下拉刷新监听接口
	 */
	public interface OnRefreshListener {
		public void onRefresh();
	}

	/**
	 * 加载更多监听接口
	 */
	public interface OnLoadMoreListener {
		public void onLoadMore();
	}

	public void setOnRefreshListener(OnRefreshListener pRefreshListener) {
		if (pRefreshListener != null) {
			mRefreshListener = pRefreshListener;
			mCanRefresh = true;
		}
	}

	public void setOnLoadListener(OnLoadMoreListener pLoadMoreListener) {
		if (pLoadMoreListener != null) {
			mLoadMoreListener = pLoadMoreListener;
			mCanLoadMore = true;
			if (mCanLoadMore && getFooterViewsCount() == 0) {
				addFooterView();
			}
		}
	}

	/**
	 * 正在下拉刷新
	 */
	private void onRefresh() {
		if (mRefreshListener != null) {
			mRefreshListener.onRefresh();
		}
	}

	/**
	 * 下拉刷新完成
	 */
	public void onRefreshComplete() {
		// 下拉刷新后是否显示第一条Item
		if (mIsMoveToFirstItemAfterRefresh)
			setSelection(0);

		mHeadState = DONE;
		// 最近更新: Time
		mLastUpdatedTextView.setText("最近更新："
				+ new SimpleDateFormat(DATE_FORMAT_STR, Locale.CHINA)
						.format(new Date()));
		changeHeaderViewByState();
	}

	/**
	 * 正在加载更多，FootView显示 ： 加载中...
	 */
	private void onLoadMore() {
		if (mLoadMoreListener != null) {
			// 加载中...
			mEndLoadTipsTextView.setText("加载中...");
			mEndLoadTipsTextView.setVisibility(View.VISIBLE);
			mEndLoadProgressBar.setVisibility(View.VISIBLE);

			mLoadMoreListener.onLoadMore();
		}
	}

	/**
	 * 加载更多完成
	 */
	public void onLoadMoreComplete() {
		if (mIsAutoLoadMore) {
			mEndState = ENDINT_AUTO_LOAD_DONE;
		} else {
			mEndState = ENDINT_MANUAL_LOAD_DONE;
		}
		changeEndViewByState();
	}

	/**
	 * 加载更多完成
	 */
	public void onLoadMorNodata() {
		load_more_nodata.setVisibility(View.VISIBLE);
		mEndLoadTipsTextView.setVisibility(View.GONE);
		mEndLoadProgressBar.setVisibility(View.GONE);
		mEndRootView.setVisibility(View.VISIBLE);
	}

	/**
	 * 主要更新一下刷新时间啦！
	 */
	public void setAdapter(BaseAdapter adapter) {
		// 最近更新: Time
		mLastUpdatedTextView.setText("最近更新"
				+ new SimpleDateFormat(DATE_FORMAT_STR, Locale.CHINA)
						.format(new Date()));
		super.setAdapter(adapter);
	}

}
