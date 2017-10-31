package com.ebensz.templates;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ABSubTitleViewHolder extends ABDefaultViewsHolder
        implements IActionbarTitleHelper, IAcionbarSubTitleHelper {
    protected TextView mSubTitle;

    public ABSubTitleViewHolder(Context context, ViewGroup root) {
        super(context, root);
    }

    public void initialize(int flags, int code, Object... args) {
        super.initialize(ActionBarView.TYPE_SUBTITLE, flags, code, args);
    }

    @Override
    protected void createContentViews() {
        TypedArray a = mContext.obtainStyledAttributes(null, R.styleable.EActionbar, R.attr.EActionbarStyles,
                R.style.Actionbar_default_style);
        float textSize = a.getDimensionPixelSize(R.styleable.EActionbar_titleTextSize, 20 * 3);
        ColorStateList textColor = a.getColorStateList(R.styleable.EActionbar_titleColor);
        float subtitleSize = a.getDimension(R.styleable.EActionbar_subTitleSize, 12 * 3);
        ColorStateList subTextColor = a.getColorStateList(R.styleable.EActionbar_subTitleColor);
        int textAppearance = a.getResourceId(R.styleable.EActionbar_eActionbarTitleTextAppearance, R.style.TitleTextAppearance);
        a.recycle();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        LayoutInflater inft = LayoutInflater.from(mContext);
        View contentView = inft.inflate(R.layout.ctrl_actionbar_content_subtitle, null);
        mViewRoot.addView(contentView, lp);

        mIconView = (ImageView) contentView.findViewById(R.id.ctrl_actionbar_title_icon);
        mTitleView = (TextView) contentView.findViewById(R.id.ctrl_actionbar_title_text);
        mTitleView.setTextAppearance(textAppearance);
        mSubTitle = (TextView) contentView.findViewById(R.id.ctrl_actionbar_subtitle_text);
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        if (textColor != null) {
            mTitleView.setTextColor(textColor);
        }
        mSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, subtitleSize);
        if (subTextColor != null) {
            mSubTitle.setTextColor(subTextColor);
        }

    }

    protected void updateContentViews(int flags, Object... args) {
        if ((flags & ActionBarView.FLAG_TITLE_ICON) == ActionBarView.FLAG_TITLE_ICON) {
            if (args[0] instanceof Drawable) {
                mIconView.setImageDrawable((Drawable) args[0]);
            } else {
                mIconView.setImageResource((Integer) args[0]);
            }
            mTitleView.setVisibility(View.GONE);
            mSubTitle.setVisibility(View.GONE);
            mIconView.setVisibility(View.VISIBLE);
        } else if ((flags & ActionBarView.FLAG_TITLE_TEXT) == ActionBarView.FLAG_TITLE_TEXT) {
            mTitleView.setText((String) args[0]);
            mIconView.setVisibility(View.GONE);
            mTitleView.setVisibility(View.VISIBLE);
            mSubTitle.setVisibility(View.VISIBLE);
        }
        updateDefaultMenus(flags, args);
    }

    public void setSubTitleText(String text) {
        if (mSubTitle != null) {
            mSubTitle.setText(text);
        }
    }

    public void setSubTitleColor(int color) {
        if (mSubTitle != null) {
            mSubTitle.setTextColor(color);
        }
    }

}
