package com.game.sdk.floatwindow;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.game.sdk.domain.GoagalInfo;
import com.game.sdk.ui.MainActivity;
import com.game.sdk.utils.Constants;
import com.game.sdk.utils.DimensionUtil;
import com.game.sdk.utils.EmulatorCheckUtil;
import com.game.sdk.utils.Logger;
import com.game.sdk.utils.MResource;
import com.game.sdk.utils.Util;

public class FloatViewImpl {
    public static int speed = 1;
    protected static final String TAG = "FloatViewImpl";
    private static FloatViewImpl instance = null;

    // 定义浮动窗口布局
    private RelativeLayout mFloatLayout;

    private static WindowManager.LayoutParams wmParams;
    private static WindowManager.LayoutParams wmParams2;
    // 创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    private ImageView mFloatView;
    private LayoutInflater inflater;

    private Context mContext;
    boolean isOne = true;
    private boolean isClick = true;
    private static int xfor = 0; // 悬浮按扭方向 0 左 1 右

    public static Bitmap dragBitmap;

    public Bitmap leftBitmap;

    public Bitmap rightBitmap;

    private FloatViewImpl(Context context) {
        init(context.getApplicationContext());
    }

    public synchronized static FloatViewImpl getInstance(Context context) {
        GoagalInfo.isEmulator = EmulatorCheckUtil.isEmulator();

        if (instance == null) {
            instance = new FloatViewImpl(context);
        }
        return instance;
    }

    protected void init(Context context) {
        this.mContext = context;

        try {
            dragBitmap = Util.getLogoFileBitmap(context, Constants.DRAG_IMAGE);

            if (dragBitmap == null) {
                dragBitmap = BitmapFactory.decodeResource(mContext.getResources(), MResource.getIdByName(mContext, "drawable", "float_drag"));
            }

            leftBitmap = Util.getLogoFileBitmap(context, Constants.DRAG_LEFT_IMAGE);
            if (leftBitmap == null) {
                leftBitmap = BitmapFactory.decodeResource(mContext.getResources(), MResource.getIdByName(mContext, "drawable", "float_holder"));
            }

            rightBitmap = Util.getLogoFileBitmap(context, Constants.DRAG_RIGHT_IMAGE);
            if (rightBitmap == null) {
                rightBitmap = BitmapFactory.decodeResource(mContext.getResources(), MResource.getIdByName(mContext, "drawable", "float_holder2"));
            }

        } catch (Exception e) {
            Logger.msg("FloatViewImpl image error --- ");
        }

        createFloatView();
    }

    private void createFloatView() {

        if (wmParams == null) {
            xfor = 0;
            wmParams = new WindowManager.LayoutParams();
            wmParams.type = GoagalInfo.isEmulator ? LayoutParams.TYPE_PHONE : LayoutParams.TYPE_TOAST;
            // 设置图片格式，效果为背景透明
            wmParams.format = PixelFormat.RGBA_8888;
            // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
            wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
            // 调整悬浮窗显示的停靠位置为左侧置顶

            wmParams.gravity = Gravity.LEFT | Gravity.TOP;

            // 以屏幕左上角为原点，设置x、y初始值，相对于gravity

            // 设置悬浮窗口长宽数据
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.x = 0;
            wmParams.y = DimensionUtil.getHeight(mContext) / 2 - 180;
        }

        if (wmParams2 == null) {
            wmParams2 = new WindowManager.LayoutParams();
            wmParams2.type = GoagalInfo.isEmulator ? LayoutParams.TYPE_PHONE : LayoutParams.TYPE_TOAST;
            // 设置图片格式，效果为背景透明
            wmParams2.format = PixelFormat.RGBA_8888;
            // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
            wmParams2.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
            // 调整悬浮窗显示的停靠位置为左侧置顶
            wmParams2.gravity = Gravity.LEFT | Gravity.TOP;

            // 以屏幕左上角为原点，设置x、y初始值，相对于gravity

            // 设置悬浮窗口长宽数据
            wmParams2.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams2.height = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams2.x = Util.dip2px(mContext, 45);
            wmParams2.y = DimensionUtil.getHeight(mContext) / 2 - 180;
        }

        // 获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        inflater = LayoutInflater.from(mContext);
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 获取浮动窗口视图所在布局
        mFloatLayout = (RelativeLayout) inflater
                .inflate(MResource.getIdByName(mContext, Constants.Resouce.LAYOUT, "float_layout"), null);

        // 添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        init();
    }

    private int checkXY(int y) {
        int height = Util.getHeight(mContext);
        if (height - wmParams.y < Util.dip2px(mContext, 180)) {
            y = height - Util.dip2px(mContext, 180);
        }

        if (wmParams.y < Util.dip2px(mContext, 50)) {
            y = Util.dip2px(mContext, 50);
        }
        Logger.msg("checkXY-->" + y);
        return y;
    }

    @SuppressLint("NewApi")
    private void init() {
        // 浮动窗口按钮
        mFloatView = (ImageView) mFloatLayout.findViewById(MResource.getIdByName(mContext, "id", "iv_float"));
        mFloatView.setBackground(new BitmapDrawable(dragBitmap));

        // 设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.alpha = 10;
                int x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;

                // 减25为状态栏的高度
                int y = (int) event.getRawY() - mFloatView.getMeasuredHeight() / 2 - 25;

                // 刷新
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        Log.e("ontouch---", "ACTION_DOWN---X---" + wmParams.x + "---Y--" + wmParams.y);

                        if (!isClick) {
                            isClick = true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isClick) {
                            Log.e("ontouch---", "ACTION_UP---X---" + wmParams.x + "---Y--" + wmParams.y);
                            int tempy = y;
                            int tempx = x;
                            float swidth = mWindowManager.getDefaultDisplay().getWidth();
                            if (wmParams.x + mFloatView.getMeasuredWidth() / 2 <= swidth / 2) {
                                tempx = 0;
                                wmParams2.x = Util.dip2px(mContext, 45);
                                xfor = 0;
                                tempy = checkXY(y);
                                wmParams2.y = tempy + Util.dip2px(mContext, 8);

                            } else if (wmParams.x + mFloatView.getMeasuredWidth() / 2 > swidth / 2) {
                                tempx = (int) swidth - mFloatView.getMeasuredWidth() / 2;
                                xfor = 1;
                                wmParams2.x = (int) swidth - Util.dip2px(mContext, 245);
                                tempy = checkXY(y);
                                wmParams2.y = tempy + Util.dip2px(mContext, 8);
                            }
                            animate(x, tempx, y, tempy);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:

                        isHolder = false;
                        if (!isClick || (Math.abs(wmParams.x - x) > Util.dip2px(mContext, 30)
                                || Math.abs(wmParams.y - y) > Util.dip2px(mContext, 30))) {
                            isClick = false;

                            setBackground("float_drag");

                            wmParams.x = x;
                            wmParams.y = y;
                            mWindowManager.updateViewLayout(mFloatLayout, wmParams);

                            Log.e("ontouch---", "ACTION_MOVE---X---" + wmParams.x + "---Y--" + wmParams.y);
                        }

                        break;
                }
                return false; // 此处必须返回false，否则OnClickListener获取不到监听
            }
        });
        mFloatView.setOnClickListener(onclick);
    }

    private void animate(int x1, int x2, int y1, int y2) {

        PropertyValuesHolder mPropertyValuesX = PropertyValuesHolder.ofInt("x", x1, x2);
        PropertyValuesHolder mPropertyValuesY = PropertyValuesHolder.ofInt("y", y1, y2);
        ValueAnimator mAnimator = ValueAnimator.ofPropertyValuesHolder(mPropertyValuesX,
                mPropertyValuesY);
        mAnimator.setInterpolator(new BounceInterpolator());//使用线性插值器
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue("x");
                int y = (int) animation.getAnimatedValue("y");
                wmParams.x = x;
                wmParams.y = y;
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
            }
        });
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setHolder();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.setDuration(1000);
        mAnimator.setTarget(mWindowManager);
        mAnimator.start();
    }

    @SuppressLint("NewApi")
    private void setBackground(String name) {
        /*
         * mFloatView.setImageDrawable(
		 * mContext.getResources().getDrawable(MResource.getIdByName(mContext,
		 * "drawable", name)));
		 */
        if ("float_holder".equals(name)) {
            mFloatView.setBackground(new BitmapDrawable(leftBitmap));
        }
        if ("float_holder2".equals(name)) {
            mFloatView.setBackground(new BitmapDrawable(rightBitmap));
        }
        if ("float_drag".equals(name)) {
            mFloatView.setBackground(new BitmapDrawable(dragBitmap));
        }
    }

    @SuppressLint("NewApi")
    private void setBackground2(String name) {
        // item_lay.setBackground(mContext.getResources().getDrawable(MResource.getIdByName(mContext,
        // "drawable", name)));
    }

    private boolean isHolder = false;
    private void setHolder() {
        isHolder = true;
        if (xfor == 0) {
            Util.postDelayed(1000, new Runnable() {
                @Override
                public void run() {
                    if(isHolder) {
                        isHolder = false;
                        setBackground("float_holder");
                    }
                }
            });
        } else {
            Util.postDelayed(1000, new Runnable() {
                @Override
                public void run() {
                    if(isHolder) {
                        isHolder = false;
                        setBackground("float_holder2");
                    }
                }
            });
        }
    }

    private void setMenuOut() {
        // if (xfor == 0) {
        // setBackground("float_menu_out");
        // setBackground2("float_bg_left_up");
        // } else {
        // setBackground("float_menu_out2");
        // setBackground2("float_bg_right_up");
        // }
    }

    private OnClickListener onclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == mFloatView.getId()) {
                //Speed.getImp().update(mContext, speed++);
                Logger.msg("current speed ->" + speed);
                if (isClick && GoagalInfo.isLogin && GoagalInfo.userInfo != null) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    mContext.startActivity(intent);
                    removeFloat();
                }
                return;
            }

        }
    };

    // 隐藏悬浮窗口
    private void hideFloat() {
        mFloatLayout.setVisibility(View.GONE);
        // removeMenuLayout();
    }

    // 移除悬浮窗口
    public void removeFloat() {
        try {
            mWindowManager.removeView(mFloatLayout);
            // removeMenuLayout();
        } catch (Exception e) {
        }
        instance = null;
    }

    // 显示悬浮窗口
    public void ShowFloat() {
        mFloatLayout.setVisibility(View.VISIBLE);
        setHolder();
    }

}
