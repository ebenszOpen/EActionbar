package com.ebensz.templates;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ebensz.templates.data.actionbar.Utilities;
import com.ebensz.framework.common.Constants;
import com.ebensz.framework.common.MenuItem;

import java.util.ArrayList;

public class ABSelectViewsHolder extends ABDefaultViewsHolder {
    protected String mTitle;
    protected int mTotalCount;
    protected int mSelectCount;
    protected ViewGroup mViewRoot;
    private MenuItem[] mRightMenusSelectAll;
    private MenuItem[] mRightMenusSelectNone;

    public ABSelectViewsHolder(Context context, ViewGroup root) {
        super(context, root);
        mViewRoot = root;
        mTotalCount = 0;
        mSelectCount = 0;
        Resources res = context.getResources();
        String textSelectAll = res.getString(R.string.ctrl_actionbar_select_all);
        String textSelectNone = res.getString(R.string.ctrl_actionbar_select_none);
        mRightMenusSelectAll = new MenuItem[] { new MenuItem(Constants.BUTTON_SELECT_ALL, 0, textSelectAll) };
        mRightMenusSelectNone = new MenuItem[] { new MenuItem(Constants.BUTTON_SELECT_NONE, 0, textSelectNone) };
    }

    public void setTotalCount(int count) {
        mTotalCount = count;
    }

    public void setSelectedCount(int count) {
        String title;
        int oldSelectCount = mSelectCount;

        mSelectCount = count;
        if (mSelectCount > 0) {
            title = mContext.getString(R.string.ctrl_actionbar_select_count, count);
            mTitleView.setText(title);
        } else {
            if (mTitle == null && mSelectCount == 0) {
                title = mContext.getString(R.string.ctrl_actionbar_select_count, 0);
            } else {
                title = mTitle;
            }
            mTitleView.setText(title);
        }

        boolean isSelectAll = (mTotalCount <= mSelectCount) && (mTotalCount > 0);
        TextView textView = findChildMenuView(0, true, false);
        if (textView != null) {
            MenuItem menuItem = (MenuItem) textView.getTag();
            if (menuItem.getId() != Constants.BUTTON_OK) {
                if ((isSelectAll ^ (menuItem.getId() == Constants.BUTTON_SELECT_NONE))) {
                    setRightMenus(isSelectAll ? mRightMenusSelectNone : mRightMenusSelectAll);
                }
            }
        }
    }

    @Override
    public void initialize(int flags, int code, Object... args) {
        mTotalCount = 0;
        super.initialize(ActionBarView.TYPE_SELECT_MODE, flags, code, args);
    }

    @Override
    protected boolean isPopupMenuEnabled() {
        boolean isEnabled = true;
        if ((mFlags & ActionBarView.FLAG_MODE_MULTI) > 0 && mSelectCount <= 0) {
            isEnabled = false;
        }
        return isEnabled;
    }

    @Override
    protected void updateContentViews(int flags, Object... args) {
        ArrayList<MenuItem> leftMenus = new ArrayList<MenuItem>();
        ArrayList<MenuItem> rightMenus = new ArrayList<MenuItem>();
        addMenusFromFlags(flags, leftMenus, rightMenus);
        mTitleView.setVisibility(View.VISIBLE);
        if (args.length > 0) {
            mTitle = (String) args[0];
            mTitleView.setText(mTitle);
        }
        // if (args.length > 1) {
        // appendUserMenus((MenuItem[]) args[1], leftMenus);
        // }
        //
        // if (args.length > 2) {
        // appendUserMenus((MenuItem[]) args[2], rightMenus);
        // }

        MenuItem[][] leftArray = getSplitedMenus(1, leftMenus);
        MenuItem[][] rightArray = getSplitedMenus(1, rightMenus);
        setLeftMenus(leftArray[0]);
        setRightMenus(rightArray[0]);
    }

    protected void addMenusFromFlags(int flags, ArrayList<MenuItem> leftMenus, ArrayList<MenuItem> rightMenus) {
        Resources res = mContext.getResources();

        if ((flags & ActionBarView.FLAG_MENU_CANCEL) == ActionBarView.FLAG_MENU_CANCEL) {
            String text = res.getString(R.string.ctrl_actionbar_cancel);
            leftMenus.add(new MenuItem(Constants.BUTTON_CANCEL, 0, text));
        } else if ((flags & ActionBarView.FLAG_MENU_BACK) == ActionBarView.FLAG_MENU_BACK) {
            leftMenus.add(new MenuItem(Constants.BUTTON_BACK, R.drawable.ctrl_actionbar_back, null));
        }

        if ((flags & ActionBarView.FLAG_MENU_OK) == ActionBarView.FLAG_MENU_OK) {
            String text = res.getString(R.string.ctrl_actionbar_ok);
            rightMenus.add(new MenuItem(Constants.BUTTON_OK, 0, text));
        } else if ((flags & ActionBarView.FLAG_MODE_MULTI) == ActionBarView.FLAG_MODE_MULTI) {
            rightMenus.add(mRightMenusSelectAll[0]);
        }
    }

    protected void appendUserMenus(final MenuItem[] array, ArrayList<MenuItem> list) {
        if (array != null && list != null) {
            for (MenuItem item : array) {
                list.add(item);
            }
        }
    }

    protected MenuItem[][] getSplitedMenus(int index, ArrayList<MenuItem> menus) {
        MenuItem[] array = null;

        final int size = menus.size();
        if (size > 0) {
            array = menus.toArray(new MenuItem[size]);
        }

        return Utilities.splitMenus(index, array);
    }

    @Override
    public void dismissPopupWindow() {
        super.dismissPopupWindow();
    }

    @Override
    protected void sendNotifyToCaller(int code, int commandId, Bundle userdata) {

        super.sendNotifyToCaller(code, commandId, userdata);
    }

}
