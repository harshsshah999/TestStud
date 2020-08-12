package com.app.hardik.studypdf;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
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

//This Adapter is for MultiLevelRecyclerView of UserHomeFragment.kt

public class UserAdapter extends MultiLevelAdapter {
    private Holder mViewHolder;
    private Context mContext;
    private List<Item> mListItems = new ArrayList<>();
    private Item mItem;
    private MultiLevelRecyclerView mMultiLevelRecyclerView;

    UserAdapter(Context mContext, List<Item> mListItems, MultiLevelRecyclerView mMultiLevelRecyclerView) {
        super(mListItems);
        this.mListItems = mListItems;
        this.mContext = mContext;
        this.mMultiLevelRecyclerView = mMultiLevelRecyclerView;
    }

    private void setExpandButton(ImageView expandButton, boolean isExpanded) {
      /* this statement doesnt work , so i have already implemented different logic let it be in comments
       expandButton.setImageResource(isExpanded ? R.drawable.ic_keyboard_arrow_down_black_24dp : R.drawable.ic_keyboard_arrow_up_black_24dp);
       */
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mViewHolder = (Holder) holder;
        mItem = mListItems.get(position);

        switch (getItemViewType(position)%4) {
            case 1:
                holder.itemView.setBackgroundResource(R.drawable.customshape2);
               // ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
              //  params.leftMargin = 100; params.rightMargin = 100;
                break;
            case 2:
                holder.itemView.setBackgroundResource(R.drawable.customshape3);
             //   ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
             //   params2.leftMargin = 130; params2.rightMargin = 130;
                break;
            case 3:
                holder.itemView.setBackgroundResource(R.drawable.customshape4);
              //  ViewGroup.MarginLayoutParams params3 = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
              //  params3.leftMargin = 165; params3.rightMargin = 165;
                break;
            default:
                holder.itemView.setBackgroundResource(R.drawable.customshape);
                break;
        }
        mViewHolder.mTitle.setText(mItem.getText());
        mViewHolder.mTitle.setGravity(Gravity.CENTER_VERTICAL);
        if (mItem.getText().substring(0,2).equals("->")){
            mViewHolder.mSubtitle.setText(mItem.getSecondText());
            Log.i("LeafNode",mItem.getText().substring(0,2)+mItem.getText());
        }
        else{
            mViewHolder.mSubtitle.setText("");
        }
        //   mViewHolder.mSubtitle.setText(mItem.getSecondText());

        if (mItem.hasChildren() && mItem.getChildren().size() > 0) {
            setExpandButton(mViewHolder.mExpandIcon, mItem.isExpanded());
            mViewHolder.mExpandButton.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.mExpandButton.setVisibility(View.GONE);
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
                    //  Toast.makeText(mContext, String.format(Locale.ENGLISH, "Item at position %d was clicked!", getAdapterPosition()), Toast.LENGTH_SHORT).show();
                    mExpandIcon.animate().rotation(mListItems.get(getAdapterPosition()).isExpanded() ? 0 : -180).start();
                   // setExpandButton(mViewHolder.mExpandIcon, mItem.isExpanded());
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
                    mExpandIcon.animate().rotation(mListItems.get(getAdapterPosition()).isExpanded() ? 0 : -180).start();
                   // setExpandButton(mViewHolder.mExpandIcon, mItem.isExpanded());
                    //Toast.makeText(mContext, String.format(Locale.ENGLISH, "Item at position %d is expanded: %s", getAdapterPosition(), mItem.isExpanded()), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
