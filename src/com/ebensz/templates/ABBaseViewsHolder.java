package com.ebensz.templates;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ebensz.templates.data.actionbar.Utilities;
import com.ebensz.framework.common.Constants;
import com.ebensz.framework.common.MenuItem;
import com.ebensz.framework.utils.CommonUtils;

import java.util.HashMap;

abstract public class ABBaseViewsHolder implements View.OnClickListener {
    protected int mType = -1;
    protected int mFlags;
    protected int mUserCode;
    private int[] mMenuDimens;
    protected Context mContext;
    protected Handler mNotifier;

    protected ViewGroup mViewRoot;
    private ViewGroup mLeftMenusRoot;
    private ViewGroup mRightMenusRoot;
    protected MenuItem[] mRightPopupMenus;
    protected MenuItem[] mLeftPopupMenus;
    protected PopupMenuView mPopupView;
    protected int mLeftMoreIcon;
    protected int mRightMoreIcon;
    protected int mBackIcon;

    abstract protected void createContentViews();

    abstract protected void updateContentViews(int flags, Object... args);

    public ABBaseViewsHolder(Context context, ViewGroup root) {
        mContext = context;
        mViewRoot = root;
        TypedArray a = context.obtainStyledAttributes(null, R.styleable.EActionbar, R.attr.EActionbarStyles, R.style.Actionbar_default_style);
        int abBackground = a.getResourceId(R.styleable.EActionbar_eActionbarBackground, R.drawable.std_actionbar_bg_selector);
        mBackIcon = a.getResourceId(R.styleable.EActionbar_menuBackIcon, R.drawable.ctrl_actionbar_back);
        mLeftMoreIcon = a.getResourceId(R.styleable.EActionbar_menuMoreIconLeft, R.drawable.std_icon_more_left_sel);
        mRightMoreIcon = a.getResourceId(R.styleable.EActionbar_menuMoreIconRight, R.drawable.std_icon_more_v_sel);
        a.recycle();
        root.setBackgroundResource(abBackground);
        findChildViewsInernal(mViewRoot);

        int[] resIds = new int[] { R.dimen.ctrl_actionbar_menu_size, R.dimen.ctrl_actionbar_menu_margin_interval,
                R.dimen.ctrl_actionbar_menu_margin_terminal };
        mMenuDimens = Utilities.loadDimensFromResource(mContext.getResources(), resIds);
    }

    public void setNotifier(Handler notifier) {
        mNotifier = notifier;
        if (mPopupView != null) {
            mPopupView.setNotifier(notifier);
        }
    }

    protected boolean isPopupMenuEnabled() {
        return true;
    }

    public void dismissPopupWindow() {
        if (mPopupView != null && mPopupView.isShowing()) {
            mPopupView.dismiss();
        }
    }

    protected void initialize(int type, int flags, int code, Object... args) {
        mType = type;
        mFlags = flags;
        mUserCode = code;

        removeContentViews();
        createContentViews();

        updateBackground();
        updateContentViews(flags, args);
    }

    private void findChildViewsInernal(ViewGroup root) {
        mLeftMenusRoot = (ViewGroup) root.findViewById(R.id.rootLeftMenus);
        mRightMenusRoot = (ViewGroup) root.findViewById(R.id.rootRightMenus);
    }

    private void removeContentViews() {
        int count = mViewRoot.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View view = mViewRoot.getChildAt(i);
            int id = view.getId();
            if (id != R.id.rootLeftMenus && id != R.id.rootRightMenus) {
                mViewRoot.removeView(view);
            }
        }
    }

    protected void updateBackground() {
        mViewRoot.setActivated(isSecure());
    }

    protected boolean isSecure() {
        if ((mFlags & ActionBarView.FLAG_BACKGROUND_USER) > 0) {
            if (CommonUtils.isPrivateSystem()) {
                return true;
            }
        } else {
            if (mContext.getUserId() > 0) {
                return true;
            }
        }
        return false;
    }

    protected void updateDefaultMenus(int flags, Object... args) {
        MenuItem[] leftMenus = (MenuItem[]) args[1];
        MenuItem[] rightMenus = (MenuItem[]) args[2];

        if ((flags & ActionBarView.FLAG_MENU_BACK) == ActionBarView.FLAG_MENU_BACK) {
            MenuItem menu = new MenuItem(Constants.BUTTON_BACK, mBackIcon, null);
            leftMenus = new MenuItem[] { menu };
        }

        setLeftMenus(leftMenus);
        setRightMenus(rightMenus);
    }

    public int getType() {
        return mType;
    }

    public void setLeftMenus(MenuItem[] menus) {
        if (menus != null && menus.length > 1) {
            MenuItem[][] splitMenus = Utilities.splitMenusWithPopup(mContext, 0, menus,
                    mLeftMoreIcon, false);
            mLeftPopupMenus = splitMenus[1];
            menus = splitMenus[0];
        }
        HashMap<String, Object> resIds = new HashMap<>();
        resIds.put("EActionbar", R.styleable.EActionbar);
        resIds.put("EActionbarStyles", R.attr.EActionbarStyles);
        resIds.put("Actionbar_default_style", R.style.Actionbar_default_style);
        resIds.put("EActionbar_menuTextSize", R.styleable.EActionbar_menuTextSize);
        resIds.put("EActionbar_menuTextColor", R.styleable.EActionbar_menuTextColor);

        Utilities.setLeftMenus(mContext, mLeftMenusRoot, menus, mMenuDimens, R.layout.ctrl_actionbar_cell_menu, this,
                isSecure(), resIds);
    }

    public void setRightMenus(MenuItem[] menus) {
        if (menus != null && menus.length > 1) {
            MenuItem[][] splitMenus = Utilities.splitMenusWithPopup(mContext, 0, menus, mRightMoreIcon,
                    true);
            mRightPopupMenus = splitMenus[1];
            menus = splitMenus[0];
        }
        HashMap<String, Object> resIds = new HashMap<>();
        resIds.put("EActionbar", R.styleable.EActionbar);
        resIds.put("EActionbarStyles", R.attr.EActionbarStyles);
        resIds.put("Actionbar_default_style", R.style.Actionbar_default_style);
        resIds.put("EActionbar_menuTextSize", R.styleable.EActionbar_menuTextSize);
        resIds.put("EActionbar_menuTextColor", R.styleable.EActionbar_menuTextColor);
        Utilities.setRightMenus(mContext, mRightMenusRoot, menus, mMenuDimens, R.layout.ctrl_actionbar_cell_menu, this,
                isSecure(), resIds);
    }

    protected void setRightMenusEnabled(boolean enabled) {
        Utilities.setRightMenusEnabled(mRightMenusRoot, enabled);
    }

    private void setMenuEnabled(MenuItem[] popupMenus, int commandId, boolean enabled) {
        if (popupMenus != null) {
            for (MenuItem item : popupMenus) {
                if (item.getId() == commandId) {
                    item.setEnabled(enabled);
                }
            }
        }

        if (mPopupView != null) {
            mPopupView.setItemEnabled(commandId, enabled);
        }
    }

    public void setLeftMenuEnabled(int commandId, boolean enabled) {
        if (Utilities.setRightMenus(mContext, mLeftMenusRoot, commandId, enabled)) {
            return;
        }
        setMenuEnabled(mLeftPopupMenus, commandId, enabled);
    }

    public void setRightMenuEnabled(int commandId, boolean enabled) {
        if (Utilities.setRightMenus(mContext, mRightMenusRoot, commandId, enabled)) {
            return;
        }
        setMenuEnabled(mRightPopupMenus, commandId, enabled);

    }

    @Override
    public void onClick(View view) {
        MenuItem menu = (MenuItem) view.getTag();
        if (menu.getId() == Constants.BUTTON_POPUP) {
            showPopupMenus(view, false, true);
        } else if (menu.getId() == Constants.BUTTON_POPUP_LEFT) {
            showPopupMenus(view, false, false);
        } else {
            sendNotifyToCaller(mUserCode, menu.getId(), menu.getUserData());
        }
    }

    private void showPopupMenus(View v, Boolean isLeftDropMenu, boolean isRight) {
        if (mPopupView != null && mPopupView.isShowing()) {
            return;
        }

        if (mPopupView == null) {
            mPopupView = new PopupMenuView(mContext, mUserCode);
            mPopupView.setNotifier(mNotifier);
        }
        if (isRight) {
            mPopupView.setItems(mRightPopupMenus);
        } else {
            mPopupView.setItems(mLeftPopupMenus);
        }
        mPopupView.setFocusable(true);
        mPopupView.setEnabled(isPopupMenuEnabled());
        mPopupView.showAsDropDown(v, -v.getWidth() / 2 - 10, -12 * 3, isLeftDropMenu);
    }

    public TextView findChildMenuView(int position, boolean byIndex, boolean isLeftMenus) {
        ViewGroup parent = isLeftMenus ? mLeftMenusRoot : mRightMenusRoot;
        if (byIndex) {
            return findMenuViewByIndex(parent, position);
        } else {
            return findMenuViewById(parent, position);
        }
    }

    private TextView findMenuViewByIndex(ViewGroup parent, int index) {
        int count = parent.getChildCount();
        if (index >= 0 && index < count) {
            return (TextView) parent.getChildAt(index);
        }
        return null;
    }

    private TextView findMenuViewById(ViewGroup parent, int id) {
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            MenuItem menu = (MenuItem) view.getTag();
            if (menu.getId() == id) {
                return (TextView) view;
            }
        }
        return null;
    }

    protected void sendNotifyToCaller(int code, int commandId, Bundle userdata) {
        Utilities.sendNotifyToCaller(mNotifier, code, commandId, userdata);
    }
}