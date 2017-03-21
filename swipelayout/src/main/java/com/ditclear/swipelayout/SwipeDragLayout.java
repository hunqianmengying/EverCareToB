package com.ditclear.swipelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by ditclear on 16/7/12.
 * 可滑动的layout extends FrameLayout
 */
public class SwipeDragLayout extends FrameLayout {
    private int downX;
    private int downY;
    private int mTouchSlop;
    private View contentView;
    private View menuView;
    private ViewDragHelper mDragHelper;
    private Point originPos = new Point();

    private boolean isOpen,clickToOpen,clickToClose;
    private float offset;
    private float needOffset = 0.1f;
    private int touchFlop;

    private SwipeListener mListener;

    public SwipeDragLayout(Context context) {
        this(context, null);
    }

    public SwipeDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.SwipeDragLayout);
        needOffset=array.getFloat(R.styleable.SwipeDragLayout_need_offset,0.1f);
        clickToOpen=array.getBoolean(R.styleable.SwipeDragLayout_click_to_open,false);
        clickToClose=array.getBoolean(R.styleable.SwipeDragLayout_click_to_close,false);
        touchFlop=ViewConfiguration.get(context).getScaledPagingTouchSlop();
        Log.d("needOffset",""+needOffset);
        init();
        array.recycle();
    }

    //初始化dragHelper，对拖动的view进行操作
    private void init() {

        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {


            @Override
            public boolean tryCaptureView(View child, int pointerId) {

                return child == contentView;
            }


            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                Log.d("releasedChild", "xvel:" + xvel + " yvel:" + yvel);
                if (releasedChild == contentView) {
                    if (isOpen()){
                        if (offset!=1&&offset>(1-needOffset)){
                            open();
                            if (mListener!=null){
                                mListener.onCancel(SwipeDragLayout.this);
                            }
                        }else if (offset==1&&!clickToClose){
                            open();
                        }else {
                            close();
                        }
                    }else {
                        if (offset!=0&&offset<needOffset){
                            close();
                            if (mListener!=null){
                                mListener.onCancel(SwipeDragLayout.this);
                            }
                        }else if (offset==0&&!clickToOpen){
                            if (mListener!=null){
                                mListener.onClick(SwipeDragLayout.this);
                            }
                        }else {
                            open();
                        }
                    }

                    invalidate();
                }
            }


            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                Log.d("DragLayout", "clampViewPositionHorizontal " + left + "," + dx);
                final int leftBound = getPaddingLeft() - menuView.getWidth();
                final int rightBound = getWidth() - child.getWidth();
                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
                return newLeft;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return contentView == child ? menuView.getWidth() : 0;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                final int childWidth = menuView.getWidth();
                offset = -(float) (left-getPaddingLeft()) / childWidth;
                //offset can callback here
                Log.d("offset", "" + offset+" dx"+dx);
                dispatchSwipeEvent(offset);
                Log.d("isOpen", "" + isOpen);
            }


        });

    }


    private void dispatchSwipeEvent(float offset) {
        if (mListener == null) return;

        if (offset == 0) {
            isOpen = false;
            mListener.onClosed(this);
        } else if (offset == 1) {
            isOpen = true;
            mListener.onOpened(this);
        }

        if (offset < 1 && offset > touchFlop) {
            if (isOpen && offset >= 1 - touchFlop) {
                mListener.onStartClose(this);
            } else if (!isOpen() && offset <= touchFlop) {
                mListener.onStartOpen(this);
            }else {
                mListener.onUpdate(this, offset);
            }
        }

    }

    public void setClickToClose(boolean clickToClose) {
        this.clickToClose = clickToClose;
    }

    public void setClickToOpen(boolean clickToOpen) {
        this.clickToOpen = clickToOpen;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void open() {
        mDragHelper.settleCapturedViewAt(originPos.x - menuView.getWidth(), originPos.y);
        isOpen=true;
    }


    public void smoothOpen(boolean smooth) {
        if (smooth) {
            mDragHelper.smoothSlideViewTo(contentView, originPos.x - menuView.getWidth(), originPos.y);
        }else {
            contentView.layout(originPos.x - menuView.getWidth(),originPos.y, menuView.getLeft(),menuView.getBottom());
        }
    }

    public void smoothClose(boolean smooth) {
        if (smooth){
            mDragHelper.smoothSlideViewTo(contentView, originPos.x, originPos.y);
        }else {
            contentView.layout(originPos.x, originPos.y, menuView.getRight(), menuView.getBottom());
        }
    }

    public void close() {
        mDragHelper.settleCapturedViewAt(originPos.x, originPos.y);
        isOpen=false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (isOpen()) {
//            return mDragHelper.shouldInterceptTouchEvent(ev);
//        } else {
//            return true;
//        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getRawX();
                downY = (int) ev.getRawY();
                return true;

            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getRawY();
                if (Math.abs(moveY - downY) > mTouchSlop) {
                    return mDragHelper.shouldInterceptTouchEvent(ev);
                }
        }
        return super.onInterceptTouchEvent(ev);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        originPos.x = contentView.getLeft();
        originPos.y = contentView.getTop();
    }


    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(1);
        menuView = getChildAt(0);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.RIGHT;
        menuView.setLayoutParams(params);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    public void addListener(SwipeListener listener) {
        mListener = listener;
    }

    public interface SwipeListener {

        void onStartOpen(SwipeDragLayout layout);

        void onStartClose(SwipeDragLayout layout);

        void onUpdate(SwipeDragLayout layout, float offset);

        void onOpened(SwipeDragLayout layout);

        void onClosed(SwipeDragLayout layout);

        void onCancel(SwipeDragLayout layout);

        void onClick(SwipeDragLayout layout);
    }


}
