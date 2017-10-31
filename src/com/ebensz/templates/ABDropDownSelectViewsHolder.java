package com.ebensz.templates;

import java.util.ArrayList;

import com.ebensz.templates.data.actionbar.Utilities;
import com.ebensz.framework.common.Constants;
import com.ebensz.framework.common.MenuItem;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ABDropDownSelectViewsHolder extends ABSelectViewsHolder {

    private MenuItem[] mComboxMenus;

    private ViewGroup mDropDownView;
    private TextView mContentView;
    private ImageView mIndicatorView;
    private PopupMenuView mComboxView;
    private RelativeLayout mDropDownLayout;

    public ABDropDownSelectViewsHolder(Context context, ViewGroup root) {
        super(context, root);

    }

    public void setTotalCount(int count) {
        mTotalCount = count;
        final boolean isEnabled = count > 0;
        if (mDropDownLayout.isEnabled() ^ isEnabled) {
            mDropDownLayout.setEnabled(isEnabled);
        }
    }

    public void setSelectedCount(int count) {
        String title;
        int oldSelectCount = mSelectCount;

        mSelectCount = count;
        if (mSelectCount > 0) {
            title = mContext.getString(R.string.ctrl_actionbar_select_count, count);
            mContentView.setText(title);
            if (oldSelectCount <= 0) {
                setRightMenusEnabled(true);
            }
        } else {
            if (mTitle == null && count == 0) {
                title = mContext.getString(R.string.ctrl_actionbar_select_count, 0);
            } else {
                title = mTitle;
            }
            mContentView.setText(title);

            if (oldSelectCount > 0) {
                setRightMenusEnabled(false);
            }
        }
    }

    @Override
    public void initialize(int flags, int code, Object... args) {
        flags &= ~(ActionBarView.FLAG_TITLE_TEXT | ActionBarView.FLAG_TITLE_ICON);
        super.initialize(ActionBarView.TYPE_SELECT_MODE, flags, code, args);
    }

    @Override
    protected void updateContentViews(int flags, Object... args) {
        ArrayList<MenuItem> leftMenus = new ArrayList<MenuItem>();
        ArrayList<MenuItem> rightMenus = new ArrayList<MenuItem>();
        addMenusFromFlags(flags, leftMenus, rightMenus);
        if (args.length > 0) {
            mTitle = (String) args[0];
        }
        if (args.length > 1) {
            appendUserMenus((MenuItem[]) args[1], leftMenus);
        }

        if (args.length > 2) {
            appendUserMenus((MenuItem[]) args[2], rightMenus);
        }

        boolean hasBackButton = (flags & ActionBarView.FLAG_MENU_BACK) > 0;
        MenuItem[][] leftArray = getSplitedMenus(hasBackButton ? 1 : 0, leftMenus);
        setLeftMenus(leftArray[0]);

        MenuItem[][] rightArray = getSplitedMenus(1, rightMenus);
        setRightMenus(rightArray[1]);
        setRightMenusEnabled((flags & ActionBarView.FLAG_MODE_MULTI) <= 0);
        mIconView.setVisibility(View.GONE);
        mTitleView.setVisibility(View.GONE);
        initWithSelectMode(flags);
    }

    private void initWithSelectMode(int flags) {
        if (mDropDownView == null) {
            LayoutInflater inft = LayoutInflater.from(mContext);
            mDropDownView = (ViewGroup) inft.inflate(R.layout.ctrl_actionbar_dropdown, null);
            mContentView = (TextView) mDropDownView.findViewById(R.id.viewContent);
            mIndicatorView = (ImageView) mDropDownView.findViewById(R.id.viewIndicator);

            mDropDownLayout = (RelativeLayout) mDropDownView.findViewById(R.id.layoutDropDown);
            mDropDownLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showComboxView(v, true);
                }
            });
            mDropDownLayout.setEnabled(false);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.RIGHT_OF, R.id.rootLeftMenus);
            lp.addRule(RelativeLayout.LEFT_OF, R.id.rootRightMenus);
            mViewRoot.addView(mDropDownView, lp);
        }

        if ((flags & ActionBarView.FLAG_MODE_MULTI) == ActionBarView.FLAG_MODE_MULTI) {
            if (TextUtils.isEmpty(mTitle)) {
                mTitle = mContext.getString(R.string.ctrl_actionbar_select_count, 0);
            }
            mDropDownLayout.setClickable(true);
            mContentView.setText(mTitle);
            mIndicatorView.setVisibility(View.VISIBLE);
        } else {
            mDropDownLayout.setClickable(false);
            mContentView.setText(mTitle);
            mIndicatorView.setVisibility(View.GONE);
        }
    }

    @Override
    public void dismissPopupWindow() {
        super.dismissPopupWindow();
        if (mComboxView != null && mComboxView.isShowing()) {
            mComboxView.dismiss();
        }
    }

    private void showComboxView(View v, Boolean isLeftDropMenu) {
        Resources res = mContext.getResources();
        if (mComboxView != null && mComboxView.isShowing()) {
            return;
        }

        if (mComboxView == null) {
            mComboxView = new PopupMenuView(mContext, mUserCode);
            mComboxView.setNotifier(mNotifier);
        }

        for (MenuItem item : mComboxMenus) {
            if (item.getId() == Constants.BUTTON_SELECT_ALL && mSelectCount >= mTotalCount) {
                item.setText(res.getString(R.string.ctrl_actionbar_select_none));
                item.setId(Constants.BUTTON_SELECT_NONE);
            } else if (item.getId() == Constants.BUTTON_SELECT_NONE && mSelectCount < mTotalCount) {
                item.setText(res.getString(R.string.ctrl_actionbar_select_all));
                item.setId(Constants.BUTTON_SELECT_ALL);
            }
        }

        mComboxView.setItems(mComboxMenus);
        mComboxView.setFocusable(true);
        mComboxView.showAsDropDown(v, -78, -30, isLeftDropMenu);
    }
}
