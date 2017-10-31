package com.ebensz.templates;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ebensz.templates.data.actionbar.Utilities;
import com.ebensz.framework.common.MenuItem;

public class PopupMenuView extends PopupWindow implements AbsListView.OnItemClickListener {
    private Context mContext;
    private Handler mNotifier;
    private MyAdapter mAdapter;

    private ImageView mViewPoint;
    private ListView mListView;
    private int mItemSize = 18 * 3;
    private ColorStateList mItemTextColor = null;
    private int mUserCode;

    public PopupMenuView(Context context, int code) {
        super(context);
        mUserCode = code;
        mContext = context;
    }

    public void setNotifier(Handler notifier) {
        mNotifier = notifier;
    }

    public void setItems(MenuItem[] items) {
        if (mAdapter == null) {
            mAdapter = new MyAdapter(mContext);
        }
        mAdapter.swap(items);
    }

    public void setItemEnabled(int commandId, boolean enabled) {
        if (mAdapter != null) {
            mAdapter.setItemEnabled(commandId, enabled);
        }
    }

    public void setEnabled(boolean enabled) {
        if (mAdapter != null) {
            mAdapter.setEnabled(enabled);
        }
    }

    public void showAsDropDown(View parent, int x, int y, Boolean mIsLeftDropMenu) {
        addList(mIsLeftDropMenu);
        showAsDropDown(parent, x, y);
    }

    public void addList(Boolean mIsLeftDropMenu) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ctrl_actionbar_more_list, null);
        TypedArray a = mContext.obtainStyledAttributes(null, R.styleable.EActionbar, R.attr.EActionbarStyles,
                R.style.Actionbar_default_style);
        Drawable d = a.getDrawable(R.styleable.EActionbar_popupBackground);
        Drawable point = a.getDrawable(R.styleable.EActionbar_popupPointIcon);
        Drawable divider = a.getDrawable(R.styleable.EActionbar_popupListDivider);
        mItemSize = a.getDimensionPixelSize(R.styleable.EActionbar_popupTextSize, mItemSize);
        mItemTextColor = a.getColorStateList(R.styleable.EActionbar_popupTextColor);
        a.recycle();
        mListView = (ListView) view.findViewById(R.id.viewList);

        if (d != null) {
            mListView.setBackground(d);
        }
        mViewPoint = (ImageView) view.findViewById(R.id.viewPoint);
        if (mIsLeftDropMenu == true) {
            mViewPoint.setVisibility(View.GONE);
        } else {
            mViewPoint.setVisibility(View.VISIBLE);
        }
        if (point != null) {
            mViewPoint.setImageDrawable(point);
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        if (divider != null) {
            mListView.setDivider(divider);
            mListView.setDividerHeight(2);
        }
        setContentView(view);
        setFocusable(true);
        this.setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        this.setOutsideTouchable(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MenuItem item = (MenuItem) parent.getItemAtPosition(position);
        if (item == null) {
            return;
        }
        Utilities.sendNotifyToCaller(mNotifier, mUserCode, item.getId(), item.getUserData());
        dismiss();
    }

    private class MyAdapter extends BaseAdapter {
        private Context mContext;
        private MenuItem[] mItems;
        private boolean mIsEnabled;

        public MyAdapter(Context context) {
            mContext = context;
            mIsEnabled = true;
        }

        public void setEnabled(boolean enabled) {
            mIsEnabled = enabled;
            notifyDataSetChanged();
        }

        public void swap(MenuItem[] items) {
            mItems = items;
            notifyDataSetChanged();
        }

        public void setItemEnabled(int commandId, boolean enabled) {
            if (mItems == null) {
                return;
            }

            for (MenuItem item : mItems) {
                if (commandId < 0) {
                    item.setEnabled(enabled);
                } else if (item.getId() == commandId && item.isEnabled() ^ enabled) {
                    item.setEnabled(enabled);
                }
            }

            notifyDataSetChanged();
        }

        @Override
        public boolean isEnabled(int position) {
            boolean isEnabled = false;

            if (mIsEnabled && mItems != null && position >= 0 && position < mItems.length) {
                int flags = mItems[position].getFlags();
                if ((flags & MenuItem.FLAG_ALWAYS_ENABLED) > 0) {
                    isEnabled = true;
                } else {
                    isEnabled = mItems[position].isEnabled();
                }
            }

            return isEnabled;
        }

        @Override
        public int getCount() {
            return mItems != null ? mItems.length : 0;
        }

        @Override
        public Object getItem(int position) {
            if (mItems != null && position >= 0 && position < mItems.length) {
                return mItems[position];
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.ctrl_actionbar_more_item, parent, false);
                holder.viewMenuIcon = (ImageView) convertView.findViewById(R.id.viewMenuIcon);
                holder.viewMenu = (TextView) convertView.findViewById(R.id.viewMenu);
                holder.viewMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, mItemSize);
                if (mItemTextColor != null) {
                    holder.viewMenu.setTextColor(mItemTextColor);
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (isEnabled(position)) {
                holder.viewMenu.setActivated(false);
                holder.viewMenuIcon.setActivated(true);
            } else {
                holder.viewMenu.setActivated(true);
                holder.viewMenuIcon.setActivated(false);
            }

            if (mItems[position].getIcon() == null && mItems[position].getIconResId() == 0) {
                holder.viewMenuIcon.setVisibility(View.GONE);
            } else {
                holder.viewMenuIcon.setVisibility(View.VISIBLE);
                if (mItems[position].getIcon() != null) {
                    holder.viewMenuIcon.setImageDrawable(mItems[position].getIcon());
                } else {
                    holder.viewMenuIcon.setImageResource(mItems[position].getIconResId());
                }
            }

            if (TextUtils.isEmpty(mItems[position].getText())) {
                holder.viewMenu.setVisibility(View.GONE);
            } else {
                holder.viewMenu.setVisibility(View.VISIBLE);
                holder.viewMenu.setText(mItems[position].getText());
                if (mItems[position].getIcon() != null) {
                    holder.viewMenu.setPadding(48, 0, 0, 0);
                    holder.viewMenu.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                } else {
                    holder.viewMenu.setPadding(0, 0, 0, 0);
                    holder.viewMenu.setGravity(Gravity.CENTER);
                }
            }

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return super.getDropDownView(position, convertView, parent);
        }
    }

    class ViewHolder {
        ImageView viewMenuIcon;
        TextView viewMenu;
    }
}
