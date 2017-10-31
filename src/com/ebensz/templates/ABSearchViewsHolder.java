package com.ebensz.templates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ABSearchViewsHolder extends ABBaseViewsHolder implements View.OnClickListener {
    private EditText mEditView;
    private ImageView mBtnDelete;

    public ABSearchViewsHolder(Context context, ViewGroup root) {
        super(context, root);
    }

    public void initialize(int flags, int code, Object... args) {
        super.initialize(ActionBarView.TYPE_SEARCH, flags, code, args);
    }

    @Override
    protected void createContentViews() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        lp.addRule(RelativeLayout.RIGHT_OF, R.id.rootLeftMenus);
        lp.addRule(RelativeLayout.LEFT_OF, R.id.rootRightMenus);

        LayoutInflater inft = LayoutInflater.from(mContext);
        View contentView = inft.inflate(R.layout.ctrl_actionbar_content_search, null);
        mViewRoot.addView(contentView, lp);

        mEditView = (EditText) contentView.findViewById(R.id.editView);
        mBtnDelete = (ImageView) contentView.findViewById(R.id.btnDelete);
    }

    protected void updateContentViews(int flags, Object... args) {
        String suggest = (String) args[0];
        mEditView.setText(suggest != null ? suggest : "");

        updateDefaultMenus(flags, args);
    }
}