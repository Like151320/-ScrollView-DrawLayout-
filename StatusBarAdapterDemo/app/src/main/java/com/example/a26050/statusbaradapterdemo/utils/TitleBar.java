package com.example.a26050.statusbaradapterdemo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.example.a26050.statusbaradapterdemo.R;

public class TitleBar extends FrameLayout {
    private TextView tvTitle;
    private TextView tvLeft;
    private TextView tvRight;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CharSequence titleText, leftText, rightText;
        int titleTextColor, leftTextColor, rightTextColor;
        float titleTextSize, leftTextSize, rightTextSize;
        Drawable leftTextDrawable, rightTextDrawable;
        int leftDrawablePadding, rightDrawablePadding;
        boolean titleVisible, leftVisible, rightVisible;

        Resources resources = getResources();

        //默认值
        //取值
        int defaultColor = ResourcesCompat.getColor(resources, R.color.colorAccent, null);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);

        titleText = typedArray.getText(R.styleable.TitleBar_title_text);
        titleTextColor = typedArray.getColor(R.styleable.TitleBar_title_text_color, defaultColor);
        titleTextSize = typedArray.getDimension(R.styleable.TitleBar_title_text_size, -1);
        titleVisible = typedArray.getBoolean(R.styleable.TitleBar_title_visible, true);

        leftText = typedArray.getString(R.styleable.TitleBar_left_text);
        leftTextColor = typedArray.getColor(R.styleable.TitleBar_left_text_color, defaultColor);
        leftTextSize = typedArray.getDimension(R.styleable.TitleBar_left_text_size, -1);
        leftDrawablePadding = typedArray.getDimensionPixelOffset(R.styleable.TitleBar_left_drawable_padding, 0);
        leftTextDrawable = typedArray.getDrawable(R.styleable.TitleBar_left_text_drawable);
        leftVisible = typedArray.getBoolean(R.styleable.TitleBar_left_text_visible, true);

        rightText = typedArray.getText(R.styleable.TitleBar_right_text);
        rightTextColor = typedArray.getColor(R.styleable.TitleBar_right_text_color, -1);
        rightTextSize = typedArray.getDimension(R.styleable.TitleBar_right_text_size, -1);
        rightDrawablePadding = typedArray.getDimensionPixelOffset(R.styleable.TitleBar_right_drawable_padding, 0);
        rightTextDrawable = typedArray.getDrawable(R.styleable.TitleBar_right_text_drawable);
        rightVisible = typedArray.getBoolean(R.styleable.TitleBar_right_text_visible, true);

        typedArray.recycle();

        //赋值
        LayoutInflater.from(context).inflate(R.layout.myview_title_bar, this);//直接改变布局，从而静态的使用子控件
        setBackgroundColor(Color.WHITE);//但是静态的使用子View并不能设置此View的背景色,导致在fit自动增加paddingTop时会颜色不同,所以手动设置
//        setMinimumHeight(getResources().getDimension(android.R.id.));
        tvTitle = (TextView) findViewById(R.id.tv_titleBar_title);
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvRight = (TextView) findViewById(R.id.tv_right);

        tvTitle.setText(titleText);
        tvTitle.setTextColor(titleTextColor);
        if (titleTextSize != -1)
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);//使用px做单位
        tvTitle.setVisibility(titleVisible ? VISIBLE : GONE);

        tvLeft.setText(leftText);
        tvLeft.setTextColor(leftTextColor);
        if (leftTextSize != -1)
            tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
        tvLeft.setCompoundDrawablePadding(leftDrawablePadding);//设置图片padding
        if (leftTextDrawable != null)
            tvLeft.setCompoundDrawablesWithIntrinsicBounds(leftTextDrawable, null, null, null);//设置图片,四个位置之一
        tvLeft.setVisibility(leftVisible ? VISIBLE : GONE);

        tvRight.setText(rightText);
        if (rightTextColor != -1)
            tvRight.setTextColor(rightTextColor);
        if (rightTextSize != -1)
            tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
        tvRight.setCompoundDrawablePadding(rightDrawablePadding);
        tvRight.setCompoundDrawablesWithIntrinsicBounds(null, null, rightTextDrawable, null);
        tvRight.setVisibility(rightVisible ? VISIBLE : GONE);

        tvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onLeftButtonClickListener(v);
                }
            }
        });

        tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRightButtonClickListener(v);
                }
            }
        });
    }

    public void setTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public void setLeftText(String left) {
        if (tvLeft != null) {
            tvLeft.setText(left);
        }
    }

    public void setRightText(String right) {
        if (tvRight != null) {
            tvRight.setText(right);
        }
    }

    public void setLeftVisibility(int visibility) {
        if (tvLeft != null) {
            tvLeft.setVisibility(visibility);
        }
    }

    public void setRightDrawable(Drawable rightTextDrawable) {
        if (tvRight != null) {
            tvRight.setCompoundDrawablesWithIntrinsicBounds(null, null, rightTextDrawable, null);
        }
    }

    public void setLeftDrawable(@Nullable Drawable leftTextDrawable) {
        if (tvLeft != null) {
            tvLeft.setCompoundDrawablesWithIntrinsicBounds(leftTextDrawable, null, null, null);
        }
    }

    public interface OnTitleBarClickListener {
        void onLeftButtonClickListener(View view);

        void onRightButtonClickListener(View view);
    }

    private OnTitleBarClickListener clickListener;

    public void setOnTitleBarClickListener(OnTitleBarClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnTitleBarClickListener(@Nullable final OnClickListener left, @Nullable final OnClickListener right) {
        this.clickListener = new OnTitleBarClickListener() {
            @Override
            public void onLeftButtonClickListener(View view) {
                if (left != null)
                    left.onClick(view);
            }

            @Override
            public void onRightButtonClickListener(View view) {
                if (right != null)
                    right.onClick(view);
            }
        };
    }
}