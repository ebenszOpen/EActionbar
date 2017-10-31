package com.ebensz.templates;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ABDefaultViewsHolder extends ABBaseViewsHolder implements View.OnClickListener, IActionbarTitleHelper {
    protected ImageView mIconView;
    protected TextView mTitleView;

    public ABDefaultViewsHolder(Context context, ViewGroup root) {
        super(context, root);
    }

    public void initialize(int flags, int code, Object... args) {
        super.initialize(ActionBarView.TYPE_DEFAULT, flags, code, args);
    }

    @Override
    protected void createContentViews() {
        TypedArray a = mContext.obtainStyledAttributes(null, R.styleable.EActionbar, R.attr.EActionbarStyles,
                R.style.Actionbar_default_style);
        float textSize = a.getDimension(R.styleable.EActionbar_titleTextSize, 20 * 3);
        ColorStateList textColor = a.getColorStateList(R.styleable.EActionbar_titleColor);
        int textAppearance = a.getResourceId(R.styleable.EActionbar_eActionbarTitleTextAppearance, R.style.TitleTextAppearance);
        a.recycle();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        LayoutInflater inft = LayoutInflater.from(mContext);
        View contentView = inft.inflate(R.layout.ctrl_actionbar_content_default, null);
        mViewRoot.addView(contentView, lp);
        mIconView = (ImageView) contentView.findViewById(R.id.viewIcon);
        mTitleView = (TextView) contentView.findViewById(R.id.viewTitle);
        mTitleView.setTextAppearance(textAppearance);
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mTitleView.setTextColor(textColor != null ? textColor : mContext.getColorStateList(R.color.std_actionbar_title));

        Resources res = mContext.getResources();
        if (isSecure()) {
            LinearGradient mLinearGradient = new LinearGradient(0, 0, 0, mTitleView.getPaint().getTextSize(),
                    Color.WHITE, res.getColor(R.color.ctrl_actionbar_title_secure_color1), TileMode.CLAMP);
            mTitleView.getPaint().setShader(mLinearGradient);
            mTitleView.setActivated(true);
        } else {
//            mTitleView.setShadowLayer(3, 0, 2, res.getColor(R.color.ctrl_actionbar_title_shadow_color1));
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
            mIconView.setVisibility(View.VISIBLE);
        } else if ((flags & ActionBarView.FLAG_TITLE_TEXT) == ActionBarView.FLAG_TITLE_TEXT) {
            mTitleView.setText((String) args[0]);
            mIconView.setVisibility(View.GONE);
            mTitleView.setVisibility(View.VISIBLE);
        }
        updateDefaultMenus(flags, args);
    }

    public void setTitleIcon(int resId) {
        if (mIconView != null) {
            mIconView.setImageResource(resId);
        }
    }

    public void setTitleText(String text) {
        if (mTitleView != null) {
            mTitleView.setText(text != null ? text : "");
        }
    }

    public void setTitleColor(int color) {
        if (mTitleView != null) {
            mTitleView.setTextColor(color);
        }
    }
}