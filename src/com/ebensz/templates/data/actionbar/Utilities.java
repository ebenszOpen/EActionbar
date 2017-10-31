package com.ebensz.templates.data.actionbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ebensz.framework.common.Constants;
import com.ebensz.framework.common.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;

public class Utilities {
    public static final int MENUID_BASE = -100;

    private static final int DIMEN_MENU_MARGIN_INTERVAL = 1;
    private static final int DIMEN_MENU_MARGIN_TERMIAL = 2;

    public static int[] loadDimensFromResource(Resources res, int[] resIds) {
        if (resIds == null || resIds.length <= 0) {
            return null;
        }

        int[] values = new int[resIds.length];
        for (int i = 0; i < resIds.length; i++) {
            values[i] = res.getDimensionPixelSize(resIds[i]);
        }
        return values;
    }

    public static MenuItem[][] splitMenus(int index, MenuItem[] menus) {
        MenuItem[] part1 = null;
        MenuItem[] part2 = null;
        ArrayList<MenuItem> part1ArrayList = new ArrayList<MenuItem>();
        ArrayList<MenuItem> part2ArrayList = new ArrayList<MenuItem>();

        if (menus != null && menus.length > 0) {
            if (index <= 0) {
                for (MenuItem menuItem : menus) {
                    if ((menuItem.getFlags() & MenuItem.FLAG_ON_ACTIONBAR_SHOW) > 0) {
                        part1ArrayList.add(menuItem);
                    } else {
                        part2ArrayList.add(menuItem);
                    }
                }
                if (part1ArrayList.size() == 0) {
                    part2 = menus;
                } else {
                    part1 = new MenuItem[part1ArrayList.size()];
                    part2 = new MenuItem[part2ArrayList.size()];
                    part1 = part1ArrayList.toArray(part1);
                    part2 = part2ArrayList.toArray(part2);
                }

            } else {
                int part1Size = Math.min(index, menus.length);
                part1 = new MenuItem[part1Size];
                for (int i = 0; i < part1Size; i++) {
                    part1[i] = menus[i];
                }

                if (part1Size < menus.length) {
                    int part2Size = menus.length - part1Size;
                    part2 = new MenuItem[part2Size];
                    for (int i = part1Size; i < menus.length; i++) {
                        part2[i - part1Size] = menus[i];
                    }
                }
            }
        }

        return new MenuItem[][] { part1, part2 };
    }

    public static MenuItem[][] splitMenusWithPopup(Context context, int index, MenuItem[] menus, int moreResId,
            boolean isRight) {
        MenuItem[][] splitMenus = splitMenus(index, menus);
        MenuItem[] part1 = splitMenus[0];
        MenuItem[] part2 = splitMenus[1];

        if (part2 != null && part2.length > 0) {
            int menuId = isRight ? Constants.BUTTON_POPUP : Constants.BUTTON_POPUP_LEFT;
            MenuItem menuPopup = new MenuItem(menuId, moreResId, null);
            menuPopup.setFlags(MenuItem.FLAG_ALWAYS_ENABLED);
            int size = 1;
            if (splitMenus[0] != null) {
                size = splitMenus[0].length + 1;
                part1 = new MenuItem[size];
                for (int i = 0; i < splitMenus[0].length; i++) {
                    part1[i] = splitMenus[0][i];
                }
            } else {
                part1 = new MenuItem[size];
            }

            part1[size - 1] = menuPopup;
        }

        return new MenuItem[][] { part1, part2 };
    }

    public static MenuItem[][] splitMenusWithPopup(Context context, int index, MenuItem[] menus, int moreResId) {
        return splitMenusWithPopup(context, index, menus, moreResId, true);
    }

    public static void setLeftMenus(Context context, ViewGroup menusRoot, MenuItem[] menus, final int[] dimens,
            int cellResId, View.OnClickListener listener, boolean isSecure,HashMap<String,Object> resIds) {
        int menuCount = menus != null ? menus.length : 0;
        int childCount = menusRoot.getChildCount();
        if (childCount > menuCount) {
            menusRoot.removeViews(menuCount, (childCount - menuCount));
        } else if (childCount < menuCount) {
            for (int i = childCount; i < menuCount; i++) {
                View view = LayoutInflater.from(context).inflate(cellResId, menusRoot, false);
                if (view instanceof TextView) {
                    TextView menu = (TextView) view;
                    TypedArray a = context.obtainStyledAttributes(null, (int[]) resIds.get("EActionbar"),(int)resIds.get("EActionbarStyles"),
                            (int)resIds.get("Actionbar_default_style"));
                    int size = a.getDimensionPixelSize((int)resIds.get("EActionbar_menuTextSize"), 16 * 3);
                    ColorStateList colors = a.getColorStateList((int)resIds.get("EActionbar_menuTextColor"));
                    a.recycle();
                    menu.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
                    if (colors != null) {
                        menu.setTextColor(colors);
                    }
                }
                menusRoot.addView(view);
            }
        }

        for (int i = 0; i < menuCount; i++) {
            TextView view = (TextView) menusRoot.getChildAt(i);
            view.setOnClickListener(listener);
            Utilities.updateChildMenus(menus[i], view, true, (i == 0), dimens, isSecure);
        }
    }

    public static void setLeftMenus(Context context, ViewGroup menuRoot, int commandId, Boolean enabled) {
        int childCount = menuRoot.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView view = (TextView) menuRoot.getChildAt(i);
            MenuItem menu = (MenuItem) view.getTag();
            if (commandId == menu.getId()) {
                view.setEnabled(enabled);
            }
        }
    }

    public static boolean setRightMenus(Context context, ViewGroup menuRoot, int commandId, Boolean enabled) {
        boolean result = false;

        int childCount = menuRoot.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView view = (TextView) menuRoot.getChildAt(i);
            MenuItem menu = (MenuItem) view.getTag();
            if (commandId == menu.getId()) {
                view.setEnabled(enabled);
                result = true;
            }
        }

        return result;
    }

    public static void setRightMenus(Context context, ViewGroup menusRoot, MenuItem[] menus, final int[] dimens,
            int cellResId, View.OnClickListener listener, boolean isSecure,HashMap<String,Object> resIds) {
        int menuCount = menus != null ? menus.length : 0;
        int childCount = menusRoot.getChildCount();
        if (childCount > menuCount) {
            menusRoot.removeViews(menuCount, (childCount - menuCount));
        } else if (childCount < menuCount) {
            for (int i = childCount; i < menuCount; i++) {
                View view = LayoutInflater.from(context).inflate(cellResId, menusRoot, false);
                if (view instanceof TextView) {
                    TextView menu = (TextView) view;
                    TypedArray a = context.obtainStyledAttributes(null, (int[]) resIds.get("EActionbar"),(int)resIds.get("EActionbarStyles"),
                            (int)resIds.get("Actionbar_default_style"));
                    int size = a.getDimensionPixelSize((int)resIds.get("EActionbar_menuTextSize"), 16 * 3);
                    ColorStateList colors = a.getColorStateList((int)resIds.get("EActionbar_menuTextColor"));
                    a.recycle();
                    menu.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
                    if (colors != null) {
                        menu.setTextColor(colors);
                    }
                }
                menusRoot.addView(view);
            }
        }

        for (int i = 0; i < menuCount; i++) {
            TextView view = (TextView) menusRoot.getChildAt(i);
            view.setOnClickListener(listener);
            Utilities.updateChildMenus(menus[i], view, false, (i == menuCount - 1), dimens, isSecure);
        }
    }

    private static void updateChildMenus(MenuItem menuItem, TextView menuView, boolean leftMargin, boolean isTernimal,
            final int[] dimens, boolean isSecure) {
        String text = menuItem.getText();
        int iconResid = menuItem.getIconResId();

        if (iconResid > 0 && text != null) {

            menuView.setText(text);
            menuView.setCompoundDrawablesWithIntrinsicBounds(iconResid, 0, 0, 0);
        } else if (iconResid > 0) {

            menuView.setText("");
            menuView.setCompoundDrawablesWithIntrinsicBounds(iconResid, 0, 0, 0);
        } else {

            menuView.setText(text != null ? text : "");
            menuView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        menuView.setTag(menuItem);
        menuView.setAlpha(1.0f);
        menuView.setEnabled(menuItem.isEnabled());

        menuView.setActivated(isSecure);
    }

    public static void setRightMenusEnabled(ViewGroup menusRoot, boolean enabled) {
        int childCount = menusRoot.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView view = (TextView) menusRoot.getChildAt(i);
            MenuItem menu = (MenuItem) view.getTag();
            if ((menu.getFlags() & menu.FLAG_ALWAYS_ENABLED) == 0) {
                view.setEnabled(enabled);
            }
        }
    }

    public static void sendNotifyToCaller(Handler notifier, int code, int commandId, Bundle userdata) {
        if (notifier == null) {
            return;
        }

        Message message = notifier.obtainMessage(Constants.COMMAND_ACTIONBAR_CLICK, code, commandId);
        if (userdata != null) {
            message.setData(userdata);
        }

        notifier.sendMessage(message);
    }
}
