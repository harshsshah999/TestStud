package com.app.hardik.studypdf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.multilevelview.MultiLevelAdapter;
import com.multilevelview.MultiLevelRecyclerView;

import java.util.ArrayList;
import java.util.List;


//This Adapter is for MultiLevelRecyclerview in UploadFragment.kt

//Missing comments can be found in ListAdapter.java file for common code

public class UploadAdapter extends MultiLevelAdapter {

    private Holder mViewHolder;
    private Context mContext;
    private List<Item> mListItems = new ArrayList<>();
    private Item mItem;
    private MultiLevelRecyclerView mMultiLevelRecyclerView;
    private UploadFragment uploadFragment;



    UploadAdapter(Context mContext, List<Item> mListItems, MultiLevelRecyclerView mMultiLevelRecyclerView,UploadFragment uploadFragment) {
        super(mListItems);
        this.mListItems = mListItems;
        this.mContext = mContext;
        this.mMultiLevelRecyclerView = mMultiLevelRecyclerView;
        this.uploadFragment = uploadFragment;
    }

    private void setExpandButton(ImageView expandButton, boolean isExpanded) {
        /* this statement doesnt work , so i have already implemented different logic let it be in comments
       expandButton.setImageResource(isExpanded ? R.drawable.ic_keyboard_arrow_down_black_24dp : R.drawable.ic_keyboard_arrow_up_black_24dp);
       */
        // set the icon based on the current state
        //expandButton.setImageResource(isExpanded ? R.drawable.ic_keyboard_arrow_down_black_24dp : R.drawable.ic_keyboard_arrow_up_black_24dp);
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
            case 0:
                holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 1:
                holder.itemView.setBackgroundColor(Color.parseColor("#efefef"));
                break;
            case 2:
                holder.itemView.setBackgroundColor(Color.parseColor("#dedede"));
                break;
            case 3:
                holder.itemView.setBackgroundColor(Color.parseColor("#cdcdcd"));
                break;
            default:
                holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
        }
        mViewHolder.mTitle.setText(mItem.getText());
        mViewHolder.mSubtitle.setText(mItem.getSecondText());

        if (mItem.hasChildren() && mItem.getChildren().size() > 0) {
            setExpandButton(mViewHolder.mExpandIcon, mItem.isExpanded());
            mViewHolder.mExpandButton.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.mExpandButton.setVisibility(View.GONE);
        }

        //Log.e("MuditLog",mItem.getLevel()+" "+mItem.getPosition()+" "+mItem.isExpanded()+"");

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

        Holder(final View itemView) {
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
                    if (activate.INSTANCE.isClickable() == 0){
                        return;
                    }
                    mExpandIcon.animate().rotation(mListItems.get(getAdapterPosition()).isExpanded() ? 0 : -180).start();
                    //set click event on item here
                    //Toast.makeText(mContext, String.format(Locale.ENGLISH, "Item at position %d was clicked!", getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(final View v) {

                    if (mListItems.get(getAdapterPosition()).getText().equals("Choose Category of File!")){
                        return false;
                    }
                    AlertDialog.Builder alert = new AlertDialog.Builder(v.getRootView().getContext());
                    int level = mListItems.get(getAdapterPosition()).getLevel();
                    final String currentName = mListItems.get(getAdapterPosition()).text;
                    if (activate.INSTANCE.isClickable() == 0)
                    {
                        if (mListItems.get(getAdapterPosition()).getSecondText().equals("Long Click Here to Cancel and select other subject"))
                        {
                           alert.setTitle("Deselect");
                           alert.setMessage("Are you sure to cancel the current selection?");
                           alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   mListItems.get(0).setText("Choose Category of File!");
                                   mListItems.get(0).setSecondText("Long Click on Subject to Select it. (You can Only Select Subject!)");
                                   Toast.makeText(v.getRootView().getContext(), "Given subject is Deselected. Choose new subject", Toast.LENGTH_LONG).show();
                                   activate.INSTANCE.setClickable(1);
                                   //auto refresh page after selecting node
                                   v.getRootView().findViewById(R.id.refresh).callOnClick();
                               }
                           });
                           alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {

                               }
                           });
                           alert.show();

                        }
                        return false;
                    }
                    String path = "",parentname = "";
                    Integer parent = 0;
                    final ListFragment listFragment = new ListFragment();

                    if (level == 0) {
                        path = "StreamList/" + currentName;
                    }
                    else {
                        for (int i=level ;i>0;i--)
                        {
                            parent = parentgive(i,1);
                            String a = parentname;
                            String b = mListItems.get(getAdapterPosition() - parent).text;
                            parentname = b+"/"+a;
                        }
                        path = "StreamList/"+parentname+currentName;
                        Log.i("genpath",path);
                    }
                    final String finalPath = path;

                    //restrict user to only select leaf nodes
                    if(!(mListItems.get(getAdapterPosition()).getSecondText().equals("__"))){
                        Toast.makeText(mContext,"You can select Subject (Last Node) Only",Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    else if((mListItems.get(getAdapterPosition()).getSecondText().equals("__"))) {
                        alert.setTitle("Upload");
                        alert.setMessage("Your Selected Subject is "+currentName+"\n"+"Full Path :- "+finalPath);
                        alert.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                itemView.setBackgroundColor(Color.parseColor("#87ceeb"));
                                activate.INSTANCE.setClickable(0);
                                activate.INSTANCE.setTitle(currentName);
                                activate.INSTANCE.setCurrentpath(finalPath);
                                mListItems.get(0).setText("You have Selected "+currentName+" .");
                                mListItems.get(0).setSecondText("Long Click Here to Cancel and select other subject");
                                Toast.makeText(v.getRootView().getContext(), currentName+" is Selected. Click Upload button to Upload!", Toast.LENGTH_LONG).show();
                                //auto refresh page after selecting node
                                v.getRootView().findViewById(R.id.refresh).callOnClick();
                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //NEGATIVE BUTTON
                            }
                        });
                        alert.show();
                    }
                    return false;
                }
            });

            //set click listener on LinearLayout because the click area is bigger than the ImageView
            mExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activate.INSTANCE.isClickable() == 0){
                        return;
                    }
                        // set click event on expand button here
                        mMultiLevelRecyclerView.toggleItemsGroup(getAdapterPosition());
                        // rotate the icon based on the current state
                        // but only here because otherwise we'd see the animation on expanded items too while scrolling
                        mExpandIcon.animate().rotation(mListItems.get(getAdapterPosition()).isExpanded() ? 0 : -180).start();

                        //Toast.makeText(mContext, String.format(Locale.ENGLISH, "Item at position %d is expanded: %s", getAdapterPosition(), mItem.isExpanded()), Toast.LENGTH_SHORT).show();


                }
            });
        }
        public Integer parentgive(int level,int x){

            int parentlevel = mListItems.get(getAdapterPosition() - x).getLevel();
            while (parentlevel >= level) {

                if (level == 0) {
                    break;
                }
                x++;
                parentlevel = mListItems.get(getAdapterPosition() - x).getLevel();

            }
            return x;
        }

    }

}
