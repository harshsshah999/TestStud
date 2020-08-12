package com.app.hardik.studypdf;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.multilevelview.MultiLevelAdapter;
import com.multilevelview.MultiLevelRecyclerView;

import java.util.ArrayList;
import java.util.List;

//This PDF Adapter is for list view of pdfs in Pdflist and MyPdfFragment

public class Pdfadapter extends MultiLevelAdapter {

    private Holder mViewHolder;
    private Context mContext;
    private List<Item> mListItems = new ArrayList<>();
    private Item mItem;
    private MultiLevelRecyclerView mMultiLevelRecyclerView;

    Pdfadapter(Context mContext, List<Item> mListItems, MultiLevelRecyclerView mMultiLevelRecyclerView) {
        super(mListItems);
        this.mListItems = mListItems;
        this.mContext = mContext;
        this.mMultiLevelRecyclerView = mMultiLevelRecyclerView;
    }

    private void setExpandButton(ImageView expandButton) {
        // set the icon based on the current state
        expandButton.setImageResource(R.drawable.ic_download);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mViewHolder = (Holder) holder;
        mItem = mListItems.get(position);

        switch (getItemViewType(position)) {
            case 1:
                holder.itemView.setBackgroundColor(Color.parseColor("#efefef"));
                break;
            case 2:
                holder.itemView.setBackgroundColor(Color.parseColor("#dedede"));
                break;
            default:
                holder.itemView.setBackgroundResource(R.drawable.customshape);
                break;
        }
        mViewHolder.mTitle.setText(mItem.getText());
        mViewHolder.mSubtitle.setText(mItem.getSecondText());
        if(mItem.getSecondText().equals("Click to buy them")||mItem.getSecondText().equals("Click to Buy or Preview")||mItem.getSecondText().equals("Click to Open!")||mItem.getText().equals("NO PDFS FOUND") )
        {
        mViewHolder.mExpandButton.setVisibility(View.GONE);
        }
        else {
            mViewHolder.mExpandButton.setVisibility(View.VISIBLE);
            setExpandButton(mViewHolder.mExpandIcon);
        }
        Log.e("MuditLog",mItem.getLevel()+" "+mItem.getPosition()+" "+mItem.isExpanded()+"");

        // indent child items
        // Note: the parent item should start at zero to have no indentation
        // e.g. in populateFakeData(); the very first Item shold be instantiate like this: Item item = new Item(0);
        float density = mContext.getResources().getDisplayMetrics().density;
        ((ViewGroup.MarginLayoutParams) mViewHolder.mTextBox.getLayoutParams()).leftMargin = (int) ((getItemViewType(position) * 20) * density + 0.5f);
    }

    private class Holder extends RecyclerView.ViewHolder {

        TextView mTitle, mSubtitle;
        ImageView mExpandIcon;
        LinearLayout mTextBox, mExpandButton;

        Holder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mSubtitle = (TextView) itemView.findViewById(R.id.subtitle);
            mExpandIcon = (ImageView) itemView.findViewById(R.id.image_view);
            mTextBox = (LinearLayout) itemView.findViewById(R.id.text_box);
            mExpandButton = (LinearLayout) itemView.findViewById(R.id.expand_field);

            // The following code snippets are only necessary if you set multiLevelRecyclerView.removeItemClickListeners(); in MainActivity.java
            // this enables more than one click event on an item (e.g. Click Event on the item itself and click event on the expand button)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //set click event on item here
                    //Toast.makeText(mContext, String.format(Locale.ENGLISH, "Item at position %d was clicked!", getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }
            });

            //set click listener on LinearLayout because the click area is bigger than the ImageView
            mExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // set click event on expand button here
                    mMultiLevelRecyclerView.toggleItemsGroup(getAdapterPosition());
                    // rotate the icon based on the current state
                    // but only here because otherwise we'd see the animation on expanded items too while scrolling
                   //mExpandIcon.animate().rotation(mListItems.get(getAdapterPosition()).isExpanded() ? -180 : 0).start();

                   // Toast.makeText(mContext, String.format(Locale.ENGLISH, "Item at position %d is expanded: %s", getAdapterPosition(), mItem.isExpanded()), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
