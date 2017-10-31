package com.ebensz.templates;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.ebensz.templates.data.actionbar.Utilities;
import com.ebensz.framework.common.Constants;

public class ABPairbtnViewsHolder extends ABBaseViewsHolder implements View.OnClickListener {
    private RadioGroup mRadioTabView;
    private RadioButton mRadioButtonLeft;
    private RadioButton mRadioButtonRight;

    public ABPairbtnViewsHolder(Context context, ViewGroup root) {
        super(context, root);
    }

    public void initialize(int flags, int code, Object... args) {
        super.initialize(ActionBarView.TYPE_PAIRBTN, flags, code, args);
    }

    @Override
    protected void createContentViews() {

        LayoutInflater inft = LayoutInflater.from(mContext);
        View contentView = inft.inflate(R.layout.ctrl_actionbar_content_pairbtn, mViewRoot, true);

        mRadioTabView = (RadioGroup) contentView.findViewById(R.id.radioGroup);
        mRadioButtonLeft = (RadioButton) contentView.findViewById(R.id.btnLeft);
        mRadioButtonRight = (RadioButton) contentView.findViewById(R.id.btnRight);

        ColorStateList res = null;
        if (mContext.getUserId() == 0) {
            res = mContext.getColorStateList(R.color.ctrl_actionbar_title_ripple_tab_wp_text_selector);
            mRadioButtonLeft.setTextColor(res);
            mRadioButtonRight.setTextColor(res);
            mRadioTabView.setBackgroundResource(R.drawable.ctrl_actionbar_background_tab_bg_normal);
            mRadioButtonLeft.setBackgroundResource(R.drawable.ctrl_actionbar_tab_left_selector_normal);
            mRadioButtonRight.setBackgroundResource(R.drawable.ctrl_actionbar_tab_right_selector_normal);
        } else {
            res = mContext.getColorStateList(R.color.ctrl_actionbar_title_ripple_tab_wp_text_selector_private);
            mRadioButtonLeft.setTextColor(res);
            mRadioButtonRight.setTextColor(res);
            mRadioTabView.setBackgroundResource(R.drawable.ctrl_actionbar_background_tab_bg_security);
            mRadioButtonLeft.setBackgroundResource(R.drawable.ctrl_actionbar_tab_left_selector_security);
            mRadioButtonRight.setBackgroundResource(R.drawable.ctrl_actionbar_tab_right_selector_security);
        }

        mRadioTabView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onTabCheckedChanged(group, checkedId);
            }
        });
    }

    protected void updateContentViews(int flags, Object... args) {
        String[] btnLables = null;
        int[] btnState = null;

        if (args[0] != null) {
            btnLables = (String[]) args[0];
        }
        if (args.length >= 4) {
            if (args[3] != null) {
                btnState = (int[]) args[3];
            }
        }

        if (btnLables != null && btnLables.length == 2) {
            mRadioButtonLeft.setText(btnLables[0]);
            mRadioButtonRight.setText(btnLables[1]);
        }
        if (btnState != null && btnState.length == 2) {
            mRadioButtonLeft.setChecked(btnState[0] == 1);
            mRadioButtonRight.setChecked(btnState[1] == 1);
        } else {
            mRadioButtonLeft.setChecked(true);
        }

        updateDefaultMenus(flags, args);
    }

    private void onTabCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.btnLeft) {
            Utilities.sendNotifyToCaller(mNotifier, 0, Constants.PAIRBTN_LEFT, null);
        } else if (checkedId == R.id.btnRight) {
            Utilities.sendNotifyToCaller(mNotifier, 0, Constants.PAIRBTN_RIGHT, null);
        }
    }
}