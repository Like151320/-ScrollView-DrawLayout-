package com.example.a26050.statusbaradapterdemo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by leon on 10/31/15.
 * 状态栏便捷设置工具类
 */
/* 使用实例     —要饿汉式使用，否则可能出现顶部太高bug。
StatusBarUtils.from(this)
                //白底黑字状态栏
                .setLightStatusBar(true)
                //透明状态栏
                .setTransparentStatusbar(true)
                //设置此view对齐状态栏 或 设置此View的高度为状态栏高度。前者为TitleBar添加paddingTop ,后者由于约束布局中PaddingTop设定无效 而使用view做状态栏背景方案
                .setActionbarView(titleBar) 或 .setStatusbarBg(statusBg)
                //完成,开始执行
                .process();

沉浸原理:
api 19-23:
1、为window添加 “状态栏透明+不占空间” 属性
2、将主体内容下移状态栏高度
api 23及以上:
1、为window添加 “内容占全屏 + 内容稳定显示” 属性
2、将主体内容下移状态栏高度

BUG:
与软键盘遮挡冲突: 若设置了 沉浸状态栏 + 窗口调整adjustResize 那么弹出软键盘时，页面失去了调整功能，窗口底部会被软键盘遮挡，
简单解决 使用fitsSystemWindows=true做titleBar的下移,页面就根据软键盘可以正常调整了

fitsSystemWindows=true ：19以上时,系统设置view的paddingTop为状态栏高度
 */
public final class StatusBarUtils {

    private final boolean lightStatusBar;
    //透明且背景不占用控件的statusbar，这里估且叫做沉浸
    private final boolean transparentStatusBar;
    private final boolean transparentNavigationbar;
    private final Window window;
    private final View actionBarView;
    private final int current = Build.VERSION.SDK_INT;
    private static int defaultFlag = 0;//记录了默认Flag，和颜色有关

    private StatusBarUtils(Window window, boolean lightStatusBar, boolean transparentStatusBar,
                           boolean transparentNavigationbar, View actionBarView) {
        this.lightStatusBar = lightStatusBar;
        this.transparentStatusBar = transparentStatusBar;
        this.window = window;
        this.transparentNavigationbar = transparentNavigationbar;
        this.actionBarView = actionBarView;
    }

    public static boolean isLessKitkat() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }

    public static Builder from(Activity activity) {
        return new Builder().setWindow(activity);
    }

    public static Builder from(Dialog dialog) {
        return new Builder().setWindow(dialog);
    }

    public static Builder from(Window window) {
        return new Builder().setWindow(window);
    }

    /**
     * Default status dp = 24 or 25
     * mhdpi = dp * 1
     * hdpi = dp * 1.5
     * xhdpi = dp * 2
     * xxhdpi = dp * 3
     * eg : 1920x1080, xxhdpi, => status/all = 25/640(dp) = 75/1080(px)
     * <p>
     * don't forget toolbar's dp = 48
     *
     * @return px
     */
    @IntRange(from = 0, to = 75)
    public static int getStatusBarOffsetPx(Context context) {
        if (isLessKitkat()) {
            return 0;
        }
        Context appContext = context.getApplicationContext();
        int result = 0;
        int resourceId =
                appContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = appContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @IntRange(from = 0, to = 75)
    public static int getNavigationBarOffsetPx(Context context) {
        if (isLessKitkat()) {
            return 0;
        }
        Context appContext = context.getApplicationContext();
        int result = 0;
        int resourceId =
                appContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = appContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 为View添加paddingTop
     */
    private void processActionBar(final View v) {
        if (v == null || !transparentStatusBar || isLessKitkat()) {
            return;
        }
//        v.postEvent(new Runnable() {//延时可能导致界面显示时TitleBar还未移动，之后瞬间移动距离，效果不好。
//            @Override
//            public void run() {
        v.setPadding(v.getPaddingLeft(), /*v.getPaddingTop() + */getStatusBarOffsetPx(v.getContext()),//改为设置paddingTop，缺点是值只能为0，优点是可以重复使用
                v.getPaddingRight(), v.getPaddingBottom());
//                v.getLayoutParams().height += getStatusBarOffsetPx(v.getContext());//加上就会导致view消失。
//            }
//        });
    }

    /**
     * 调用私有API处理颜色 (小米或魅族手机的颜色改变)
     */
    private void processPrivateAPI() {
        try {
            processFlyMe(lightStatusBar);
        } catch (Exception e) {
            try {
                processMIUI(lightStatusBar);
            } catch (Exception e2) {
                //
            }
        }
    }

    private void process() {
        //调用私有API处理颜色
        processPrivateAPI();
        processActionBar(actionBarView);

        //处理4.4~5.0沉浸
        if (current >= Build.VERSION_CODES.KITKAT && current < Build.VERSION_CODES.M) {
            processKitkat();
        } else if (current >= Build.VERSION_CODES.M) {
            processM();
        }
    }

    /**
     * 透明状态栏
     */
    @Deprecated
    private void transparentStatusBar() {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    /**
     * 显示状态栏
     */
    @Deprecated
    private void unTransparentStatusBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    /**
     * 改变小米的状态栏字体颜色为黑色, 要求MIUI6以上
     * Tested on: MIUIV7 5.0 Redmi-Note3
     */
    private void processMIUI(boolean lightStatusBar) throws Exception {
        Class<? extends Window> clazz = window.getClass();
        int darkModeFlag;
        Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
        Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
        darkModeFlag = field.getInt(layoutParams);
        Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
        extraFlagField.invoke(window, lightStatusBar ? darkModeFlag : 0, darkModeFlag);
    }

    /**
     * 改变魅族的状态栏字体为黑色，要求FlyMe4以上
     */
    private void processFlyMe(boolean isLightStatusBar) throws Exception {
        WindowManager.LayoutParams lp = window.getAttributes();
        Class<?> instance = Class.forName("android.view.WindowManager$LayoutParams");
        int value = instance.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON").getInt(lp);
        Field field = instance.getDeclaredField("meizuFlags");
        field.setAccessible(true);
        int origin = field.getInt(lp);
        if (isLightStatusBar) {
            field.set(lp, origin | value);
        } else {
            field.set(lp, (~value) & origin);
        }
    }

    /**
     * 处理4.4沉浸
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void processKitkat() {
        WindowManager.LayoutParams winParams = window.getAttributes();//1、断点得知softInputMode并没有改变
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;//透明_状态
        if (transparentStatusBar) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        window.setAttributes(winParams);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void processM() {
        if (current < Build.VERSION_CODES.M) {
            return;
        }
        int flag = window.getDecorView().getSystemUiVisibility();
        if (lightStatusBar) {
            /**
             * 改变字体颜色
             * see {@link <a href="https://developer.android.com/reference/android/R.attr.html#windowLightStatusBar"></a>}
             */
            flag |= (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        if (transparentStatusBar) {//隐藏状态栏
            flag |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        if (transparentNavigationbar) {//隐藏导航栏
            flag |= (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        if (defaultFlag == 0) {
            defaultFlag = flag;
        }
        if (lightStatusBar)//白色状态栏图标 -> 使用默认的
            window.getDecorView().setSystemUiVisibility(flag);
        else
            window.getDecorView().setSystemUiVisibility(defaultFlag);
        Log.i("Li_ke", "改变了状态栏文字颜色:" + (lightStatusBar ? "亮色" : "默认色"));
    }

    final public static class Builder {
        private Window window;
        private boolean lightStatusBar = false;
        private boolean transparentStatusbar = false;
        private boolean transparentNavigationbar = false;
        private View actionBarView;

        /**
         * 防止该view被状态栏遮挡
         * {@link Deprecated 兼容性不好，改为修改标题栏占位背景色 或直接 设置fits属性}
         */
        @Deprecated
        public Builder setActionbarView(@Nullable View actionbarView) {
            this.actionBarView = actionbarView;
            return this;
        }

        public Builder setStatusBarBg(@NonNull View statusBarBgView) {
            StatusBarUtils.setStatusBarBg(statusBarBgView);
            return this;
        }

        private Builder setWindow(@NonNull Window Window) {
            this.window = Window;
            return this;
        }

        private Builder setWindow(@NonNull Activity activity) {
            this.window = activity.getWindow();
            return this;
        }

        private Builder setWindow(@NonNull Dialog dialog) {
            this.window = dialog.getWindow();
            return this;
        }

        public Builder setLightStatusBar(boolean lightStatusBar) {
            this.lightStatusBar = lightStatusBar;
            return this;
        }

        public Builder setTransparentStatusbar(boolean transparentStatusbar) {
            this.transparentStatusbar = transparentStatusbar;
            return this;
        }

        public Builder setTransparentNavigationbar(boolean transparentNavigationbar) {
            this.transparentNavigationbar = transparentNavigationbar;
            return this;
        }

        public void process() {
            new StatusBarUtils(window, lightStatusBar, transparentStatusbar, transparentNavigationbar,
                    actionBarView).process();
        }
    }

    /**
     * 将一个view设为与状态栏等高。其他控件需要从此view底部开始排版。
     */
    public static void setStatusBarBg(View v) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.height = getStatusBarOffsetPx(v.getContext());
        v.setLayoutParams(layoutParams);
    }
}