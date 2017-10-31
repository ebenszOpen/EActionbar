package com.ebensz.templates;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ebensz.framework.common.MenuItem;

public class ActionBarView extends FrameLayout {
    public static final int COMMAND_BASEID = 0;

    public static final int TYPE_DEFAULT = 1;
    public static final int TYPE_SEARCH = 2;
    public static final int TYPE_PAIRBTN = 3;
    public static final int TYPE_SELECT_MODE = 4;
    public static final int TYPE_SUBTITLE = 5;

    public static final int FLAG_TITLE_ICON = 0x00000001;
    public static final int FLAG_TITLE_TEXT = 0x00000002;
    public static final int FLAG_MENU_OK = 0x00000008;
    public static final int FLAG_MENU_BACK = 0x00000010;
    public static final int FLAG_MENU_CANCEL = 0x00000020;
    public static final int FLAG_MENU_SELECT = 0x00000040;
    public static final int FLAG_MODE_MULTI = 0x00000400;
    public static final int FLAG_MODE_SINGLE = 0x00000800;
    public static final int FLAG_BACKGROUND_USER = 0x00001000;
    public static final int FLAG_MODE_POPUP_MENU = 0x00002000;

    private Handler mNotifier;
    private ViewGroup mMainViewsRoot;
    private ViewGroup mSelectViewsRoot;

    private Animation mMainShowAnimation;
    private Animation mMainHideAnimation;
    private Animation mSelectShowAnimation;
    private Animation mSelectHideAnimation;

    public ActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setNotifier(Handler notifier) {
        mNotifier = notifier;

        ABBaseViewsHolder mainHolder = getMainHolder();
        if (mainHolder != null) {
            mainHolder.setNotifier(notifier);
        }

        ABBaseViewsHolder selHolder = getSelectHolder();
        if (selHolder != null) {
            selHolder.setNotifier(mNotifier);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mMainViewsRoot = (ViewGroup) findViewById(R.id.ctrl_actionbar_main_root);
        mSelectViewsRoot = (ViewGroup) findViewById(R.id.ctrl_actionbar_select_root);
//        setBackground(mBackground);

        mMainShowAnimation = AnimationUtils.loadAnimation(mContext, R.anim.actionbar_main_show_animtion);
        mMainHideAnimation = AnimationUtils.loadAnimation(mContext, R.anim.actionbar_main_hide_animtion);
        mSelectShowAnimation = AnimationUtils.loadAnimation(mContext, R.anim.actionbar_select_show_animtion);
        mSelectHideAnimation = AnimationUtils.loadAnimation(mContext, R.anim.actionbar_select_hide_animtion);
    }

    public void initialize(int type, int flags, int code, Object... args) {
        switch (type) {
        case TYPE_DEFAULT: {
            ABSelectViewsHolder selHolder = getSelectViewsHolder(mContext, mSelectViewsRoot, flags);
            selHolder.setNotifier(mNotifier);
            mSelectViewsRoot.setTag(selHolder);

            ABDefaultViewsHolder mainHolder = new ABDefaultViewsHolder(mContext, mMainViewsRoot);
            mainHolder.setNotifier(mNotifier);
            mMainViewsRoot.setTag(mainHolder);

            mSelectViewsRoot.setVisibility(View.GONE);
            mMainViewsRoot.setVisibility(View.VISIBLE);
            mainHolder.initialize(flags, code, args);
            break;
        }
        case TYPE_SUBTITLE: {
            ABSubTtitleSelectViewHolder selHolder = new ABSubTtitleSelectViewHolder(mContext, mSelectViewsRoot);
            selHolder.setNotifier(mNotifier);
            mSelectViewsRoot.setTag(selHolder);

            ABSubTitleViewHolder mainHolder = new ABSubTitleViewHolder(mContext, mMainViewsRoot);
            mainHolder.setNotifier(mNotifier);
            mMainViewsRoot.setTag(mainHolder);

            mSelectViewsRoot.setVisibility(View.GONE);
            mMainViewsRoot.setVisibility(View.VISIBLE);
            mainHolder.initialize(flags, code, args);
            break;
        }
        case TYPE_SEARCH: {
            ABSelectViewsHolder selHolder = getSelectViewsHolder(mContext, mSelectViewsRoot, flags);
            selHolder.setNotifier(mNotifier);
            mSelectViewsRoot.setTag(selHolder);

            ABSearchViewsHolder mainHolder = new ABSearchViewsHolder(mContext, mMainViewsRoot);
            mainHolder.setNotifier(mNotifier);
            mMainViewsRoot.setTag(mainHolder);

            mSelectViewsRoot.setVisibility(View.GONE);
            mMainViewsRoot.setVisibility(View.VISIBLE);
            mainHolder.initialize(flags, code, args);
            break;
        }
        case TYPE_PAIRBTN: {
            ABSelectViewsHolder selHolder = getSelectViewsHolder(mContext, mSelectViewsRoot, flags);
            selHolder.setNotifier(mNotifier);
            mSelectViewsRoot.setTag(selHolder);

            ABPairbtnViewsHolder mainHolder = new ABPairbtnViewsHolder(mContext, mMainViewsRoot);
            mainHolder.setNotifier(mNotifier);
            mMainViewsRoot.setTag(mainHolder);

            mSelectViewsRoot.setVisibility(View.GONE);
            mMainViewsRoot.setVisibility(View.VISIBLE);
            mainHolder.initialize(flags, code, args);
            break;
        }
        case TYPE_SELECT_MODE: {
            ABSelectViewsHolder selHolder = getSelectViewsHolder(mContext, mSelectViewsRoot, flags);
            selHolder.setNotifier(mNotifier);
            mSelectViewsRoot.setTag(selHolder);

            ABDefaultViewsHolder mainHolder = new ABDefaultViewsHolder(mContext, mMainViewsRoot);
            mainHolder.setNotifier(mNotifier);
            mMainViewsRoot.setTag(mainHolder);

            mMainViewsRoot.setVisibility(View.GONE);
            mSelectViewsRoot.setVisibility(View.VISIBLE);
            selHolder.initialize(flags, code, args);
            break;
        }
        }
    }

    public boolean isInSelectMode() {
        return (mSelectViewsRoot.getVisibility() == View.VISIBLE);
    }

    public void showSelectMode(int flags) {
        if (isInSelectMode()) {
            return;
        }

        mMainViewsRoot.setVisibility(View.GONE);
        mSelectViewsRoot.setVisibility(View.VISIBLE);

        ABSelectViewsHolder holder = getSelectHolder();
        if (holder != null) {
            holder.initialize(flags, 0);
        }
    }

    public void showSelectMode(int flags, String title, MenuItem[] leftMenus, MenuItem[] rightMenus) {
        if (isInSelectMode()) {
            return;
        }

        mMainViewsRoot.setVisibility(View.GONE);
        mSelectViewsRoot.setVisibility(View.VISIBLE);

        ABSelectViewsHolder holder = getSelectHolder();
        if (holder != null) {
            holder.initialize(flags, 0, title, leftMenus, rightMenus);
        }
    }

    public void showSelectDirectly() {
        mMainViewsRoot.setVisibility(GONE);
        mSelectViewsRoot.setVisibility(VISIBLE);
    }

    public void hideSelectDirectly() {
        mMainViewsRoot.setVisibility(VISIBLE);
        mSelectViewsRoot.setVisibility(GONE);
    }

    public void showSelectAnim() {
        mMainViewsRoot.startAnimation(mMainHideAnimation);
        mSelectViewsRoot.startAnimation(mSelectShowAnimation);
    }

    public void hideSelectAnim() {
        mMainViewsRoot.startAnimation(mMainShowAnimation);
        mSelectViewsRoot.startAnimation(mSelectHideAnimation);
    }

    public void hideSelectMode() {
        if (!isInSelectMode()) {
            return;
        }

        ABSelectViewsHolder holder = getSelectHolder();
        if (holder != null) {
            holder.dismissPopupWindow();
        }

        mSelectViewsRoot.setVisibility(View.GONE);
        mMainViewsRoot.setVisibility(View.VISIBLE);
    }

    private ABBaseViewsHolder getMainHolder() {
        Object tag = mMainViewsRoot.getTag();
        if (tag != null) {
            return (ABBaseViewsHolder) tag;
        }
        return null;
    }

    private ABSelectViewsHolder getSelectHolder() {
        Object tag = mSelectViewsRoot.getTag();
        if (tag != null) {
            return (ABSelectViewsHolder) tag;
        }
        return null;
    }

    private ABPairbtnViewsHolder getPairbtnHolder() {
        Object tag = mMainViewsRoot.getTag();
        if (tag != null) {
            return (ABPairbtnViewsHolder) tag;
        }
        return null;
    }

    public void setTitleIcon(int resId) {
        if (isInSelectMode()) {
            return;
        }

        ABBaseViewsHolder holder = getMainHolder();
        if (holder != null && holder instanceof IActionbarTitleHelper) {
            ((IActionbarTitleHelper) holder).setTitleIcon(resId);
        }
    }

    public void setTitleText(String text) {
        if (isInSelectMode()) {
            return;
        }

        ABBaseViewsHolder holder = getMainHolder();
        if (holder != null && holder instanceof IActionbarTitleHelper) {
            ((IActionbarTitleHelper) holder).setTitleText(text);
        }
    }

    public void setTitleColor(int color) {
        ABBaseViewsHolder holder = null;

        if (isInSelectMode()) {
            holder = getSelectHolder();
        } else {
            holder = getMainHolder();
        }
        if (holder != null && holder instanceof IActionbarTitleHelper) {
            ((IActionbarTitleHelper) holder).setTitleColor(color);
        }
    }

    public void setSubTitleText(String text) {

        ABBaseViewsHolder holder = null;

        if (isInSelectMode()) {
            holder = getSelectHolder();
        } else {
            holder = getMainHolder();
        }

        if (holder != null && holder instanceof IAcionbarSubTitleHelper) {
            ((IAcionbarSubTitleHelper) holder).setSubTitleText(text);
        }

    }

    public void setSubTitleColor(int color) {
        ABBaseViewsHolder holder = null;
        if (isInSelectMode()) {
            holder = getSelectHolder();
        } else {
            holder = getMainHolder();
        }

        if (holder != null && holder instanceof IAcionbarSubTitleHelper) {
            ((IAcionbarSubTitleHelper) holder).setSubTitleColor(color);
        }
    }

    public void setBackground(Drawable background) {
        mMainViewsRoot.setBackground(background);
        mSelectViewsRoot.setBackground(background);
    }

    public void setLeftMenus(final MenuItem[] menus) {
        if (isInSelectMode()) {
            return;
        }

        ABBaseViewsHolder holder = getMainHolder();
        if (holder != null) {
            holder.setLeftMenus(menus);
        }
    }

    public void setRightMenus(final MenuItem[] menus) {
        if (isInSelectMode()) {
            return;
        }

        ABBaseViewsHolder holder = getMainHolder();
        if (holder != null) {
            holder.setRightMenus(menus);
        }
    }

    public void setLeftRightMenus(final MenuItem[] leftMenus, final MenuItem[] rightMenus) {
        if (isInSelectMode()) {
            return;
        }

        ABBaseViewsHolder holder = getMainHolder();
        if (holder != null) {
            holder.setLeftMenus(leftMenus);
            holder.setRightMenus(rightMenus);
        }
    }

    public void setLeftMenuEnabled(int comandId, boolean enabled) {
        ABBaseViewsHolder holder = isInSelectMode() ? getSelectHolder() : getMainHolder();
        if (holder != null) {
            holder.setLeftMenuEnabled(comandId, enabled);
        }
    }

    public void setRightMenuEnabled(int comandId, boolean enabled) {
        ABBaseViewsHolder holder = isInSelectMode() ? getSelectHolder() : getMainHolder();
        if (holder != null) {
            holder.setRightMenuEnabled(comandId, enabled);
        }
    }

    public void setTotalCount(int count) {
        if (!isInSelectMode()) {
            return;
        }

        ABSelectViewsHolder holder = getSelectHolder();
        if (holder != null) {
            holder.setTotalCount(count);
        }
    }

    public void setSelectedCount(int count) {
        if (!isInSelectMode()) {
            return;
        }

        ABSelectViewsHolder holder = getSelectHolder();
        if (holder != null) {
            holder.setSelectedCount(count);
        }
    }

    public TextView findChildMenuView(int position, boolean byIndex, boolean isLeftMenus) {
        ABBaseViewsHolder holder = null;
        if (isInSelectMode()) {
            holder = getSelectHolder();
        } else {
            holder = getMainHolder();
        }
        TextView textView = null;
        if (holder != null) {
            textView = holder.findChildMenuView(position, byIndex, isLeftMenus);
        }
        return textView;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility != View.VISIBLE) {
            ABBaseViewsHolder mainHolder = getMainHolder();
            if (mainHolder != null) {
                mainHolder.dismissPopupWindow();
            }

            ABBaseViewsHolder selHolder = getSelectHolder();
            if (selHolder != null) {
                selHolder.dismissPopupWindow();
            }
        }
    }

    protected ABSelectViewsHolder getSelectViewsHolder(Context context, ViewGroup root, int flags) {
        ABSelectViewsHolder selectViewsHolder = null;
        if ((flags & FLAG_MODE_POPUP_MENU) > 0) {
            selectViewsHolder = new ABDropDownSelectViewsHolder(context, root);
        } else {
            selectViewsHolder = new ABSelectViewsHolder(context, root);
        }
        return selectViewsHolder;
    }
}
