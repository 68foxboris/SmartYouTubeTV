package com.liskovsoft.smartyoutubetv.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;
import com.liskovsoft.smartyoutubetv.common.R;

import java.util.ArrayList;
import java.util.Map;

public class GenericSelectorDialog implements OnClickListener {
    private final Context mActivity;
    private AlertDialog alertDialog;
    private ArrayList<CheckedTextView> mDialogItems;
    private final DataSource mDataSource;

    public interface DataSource {
        /**
         * Your data
         * @return pairs that consist of item text and tag
         */
        Map<String, String> getDialogItems();
        /**
         * Get selected tag
         * @return selected tag
         */
        String getSelected();
        void setSelected(String tag);
        String getTitle();
    }

    public static void create(Context ctx, DataSource dataSource) {
        GenericSelectorDialog dialog = new GenericSelectorDialog(ctx, dataSource);
        dialog.run();
    }

    public GenericSelectorDialog(Context activity, DataSource dataSource) {
        mActivity = activity;
        mDataSource = dataSource;
    }

    public void run() {
        showDialog();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AppDialog);
        View title = createCustomTitle(builder.getContext());
        alertDialog = builder.setCustomTitle(title).setView(buildView(builder.getContext())).create();
        alertDialog.show();
    }

    private View createCustomTitle(Context context) {
        String title = mDataSource.getTitle();
        LayoutInflater inflater = LayoutInflater.from(context);
        View titleView = inflater.inflate(R.layout.dialog_custom_title, null);
        TextView textView = titleView.findViewById(R.id.title);
        textView.setText(title);
        return titleView;
    }

    @SuppressLint("InflateParams")
    private View buildView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.generic_selection_dialog, null);
        ViewGroup root = view.findViewById(R.id.root);

        TypedArray attributeArray = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        int selectableItemBackgroundResourceId = attributeArray.getResourceId(0, 0);
        attributeArray.recycle();

        mDialogItems = new ArrayList<>();

        for (Map.Entry<String, String> entry : mDataSource.getDialogItems().entrySet()) {
            CheckedTextView dialogItem = (CheckedTextView) inflater.inflate(R.layout.dialog_check_item_single, root, false);
            dialogItem.setBackgroundResource(selectableItemBackgroundResourceId);
            dialogItem.setText(entry.getKey());

            dialogItem.setFocusable(true);
            dialogItem.setTag(entry.getValue());
            dialogItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimension(R.dimen.dialog_text_size));
            dialogItem.setOnClickListener(this);
            mDialogItems.add(dialogItem);
            root.addView(dialogItem);
        }

        updateViews();

        return view;
    }

    private void updateViews() {
        for (CheckedTextView view : mDialogItems) {
            if (view.getTag().equals(mDataSource.getSelected())) {
                view.setChecked(true);
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        String tag = (String) view.getTag();
        if (!tag.equals(mDataSource.getSelected())) {
            mDataSource.setSelected(tag);

            // close dialog
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
}
