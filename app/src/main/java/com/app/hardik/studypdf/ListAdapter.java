package com.app.hardik.studypdf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.multilevelview.MultiLevelAdapter;
import com.multilevelview.MultiLevelRecyclerView;

import java.util.ArrayList;
import java.util.List;

//This is Adapter for MultiLevelRecyclerView of ListFragment

public class ListAdapter extends MultiLevelAdapter {
    private Holder mViewHolder;
    private Context mContext;
    private List<Item> mListItems = new ArrayList<>();
    private Item mItem;
    private MultiLevelRecyclerView mMultiLevelRecyclerView;
    private FirebaseDatabase db;
    private DatabaseReference dbrefer;



    ListAdapter(Context mContext, List<Item> mListItems, MultiLevelRecyclerView mMultiLevelRecyclerView) {
        super(mListItems);
        this.mListItems = mListItems;
        this.mContext = mContext;
        this.mMultiLevelRecyclerView = mMultiLevelRecyclerView;
    }

    private void setExpandButton(ImageView expandButton, boolean isExpanded) {
        // set the icon based on the current state
        Log.i("Expanded",String.valueOf(isExpanded));
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

        //Changing Color range of all items according to the mode
        if(currentMode.INSTANCE.getUpdateClicked() == 1) {
            //ADD Mode :- Blue color range
            switch (getItemViewType(position)%4) {
                case 0:
                    holder.itemView.setBackgroundColor(Color.parseColor("#dbf3fa"));
                    break;
                case 1:
                    holder.itemView.setBackgroundColor(Color.parseColor("#b7e9f7"));
                    break;
                case 2:
                    holder.itemView.setBackgroundColor(Color.parseColor("#93dff3"));
                    break;
                case 3:
                    holder.itemView.setBackgroundColor(Color.parseColor("#7AD7F0"));
                    break;
                default:
                    holder.itemView.setBackgroundColor(Color.parseColor("#f5fcff"));
                    break;
            }

        }
        else if (currentMode.INSTANCE.getUpdateClicked() == 0){
            //Default mode
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
        }
        else if (currentMode.INSTANCE.getUpdateClicked() == 2){
            //DELETE Mode :- Red color range
            switch (getItemViewType(position)%4) {
                case 0:
                    holder.itemView.setBackgroundColor(Color.parseColor("#F6BDC0"));
                    break;
                case 1:
                    holder.itemView.setBackgroundColor(Color.parseColor("#F1959B"));
                    break;
                case 2:
                    holder.itemView.setBackgroundColor(Color.parseColor("#F07470"));
                    break;
                case 3:
                    holder.itemView.setBackgroundColor(Color.parseColor("#EA4C46"));
                    break;
                default:
                    holder.itemView.setBackgroundColor(Color.parseColor("#F6BDC0"));
                    break;
            }
        }
        else if(currentMode.INSTANCE.getUpdateClicked() == 3){
            //RENAME Mode :- Yellow color range
            switch (getItemViewType(position)%4) {
                case 0:
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFFFB7"));
                    break;
                case 1:
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFF192"));
                    break;
                case 2:
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFEA61"));
                    break;
                case 3:
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFDD3C"));
                    break;
                default:
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFFFB7"));
                    break;
            }
        }

        mViewHolder.mTitle.setText(mItem.getText());
        if(currentMode.INSTANCE.getUpdateClicked() == 1){
            mViewHolder.mSubtitle.setText("Long Click here to add New Element");
        }
        else if (currentMode.INSTANCE.getUpdateClicked() == 2){
            if(mItem.text.equals("All Available Categories")){

            }
            else{
            mViewHolder.mSubtitle.setText("Long Click here to delete this Element");
            }
        }
        else if(currentMode.INSTANCE.getUpdateClicked() == 3){
            if(mItem.text.equals("All Available Categories")){

            }
           else {
               mViewHolder.mSubtitle.setText("Long Click here to Rename this Element");
            }
        }
        else {
            // mViewHolder.mSubtitle.setText(mItem.getSecondText());
        }

        if (mItem.hasChildren() && mItem.getChildren().size() > 0) {
            setExpandButton(mViewHolder.mExpandIcon, mItem.isExpanded());
            mViewHolder.mExpandButton.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.mExpandButton.setVisibility(View.GONE);
        }

        Log.e("MuditLog",mItem.getLevel()+" "+mItem.getPosition()+" "+mItem.isExpanded()+"");

        // indent child items
        // Note: the parent item should start at zero to have no indentation
        float density = mContext.getResources().getDisplayMetrics().density;
        ((ViewGroup.MarginLayoutParams) mViewHolder.mTextBox.getLayoutParams()).leftMargin = (int) ((getItemViewType(position) * 20) * density + 0.5f);
        //((ViewGroup.MarginLayoutParams) mViewHolder.itemView.getLayoutParams()).leftMargin = (int) ((getItemViewType(position) * 20) * density + 0.5f);
    }

    private class Holder extends RecyclerView.ViewHolder {

        TextView mTitle, mSubtitle;
        ImageView mExpandIcon;
        LinearLayout mTextBox, mExpandButton;

        Holder(final View itemView) {
            super(itemView);
            mTitle =  itemView.findViewById(R.id.title);
            mSubtitle =  itemView.findViewById(R.id.subtitle);
            mExpandIcon =  itemView.findViewById(R.id.image_view);
            mTextBox =  itemView.findViewById(R.id.text_box);
            mExpandButton =  itemView.findViewById(R.id.expand_field);




            // The following code snippets are only necessary if you set multiLevelRecyclerView.removeItemClickListeners(); in MainActivity.java
            // this enables more than one click event on an item (e.g. Click Event on the item itself and click event on the expand button)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExpandIcon.animate().rotation(mListItems.get(getAdapterPosition()).isExpanded() ? 0 : -180).start();
                    //set click event on item here
                   // Toast.makeText(mContext, String.format(Locale.ENGLISH, "Item at position %d was clicked!", getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }
            });
            //Events to occur after long clicking any item of our listview
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (mListItems.get(getAdapterPosition()).getText().equals("All Available Categories")){
                        return false;
                    }

                    //Initializing AlertDialog
                    AlertDialog.Builder alert = new AlertDialog.Builder(v.getRootView().getContext());
                    int level = mListItems.get(getAdapterPosition()).getLevel();
                    final String currentName = mListItems.get(getAdapterPosition()).secondText;
                    String path = "",parentpath="" , parentname = "";
                    //String[] childlist = {};
                    Integer parent = 0;

                  /*
                  IGNORE IGNORE IGNORE
                  OLD method to get path of current selected item
                  if (level == 3) {
                        parent2 = parentgive(level, 1);
                        parent2Name = mListItems.get(getAdapterPosition() - parent2).text;
                        parent1 = parentgive(2, parent2);
                        parent1Name = mListItems.get(getAdapterPosition() - parent1).text;
                        parent0 = parentgive(1, parent1);
                        parent0Name = mListItems.get(getAdapterPosition() - parent0).text;
                        path = "StreamList/" + parent0Name + "/" + parent1Name + "/" + parent2Name + "/" + currentName;

                    } else if (level == 2) {
                        parent1 = parentgive(level, 1);
                        parent1Name = mListItems.get(getAdapterPosition() - parent1).text;
                        parent0 = parentgive(1, parent1);
                        parent0Name = mListItems.get(getAdapterPosition() - parent0).text;
                        path = "StreamList/" + parent0Name + "/" + parent1Name + "/" + currentName;
                    } else if (level == 1) {
                        parent0 = parentgive(level, 1);
                        parent0Name = mListItems.get(getAdapterPosition() - parent0).text;
                        path = "StreamList/" + parent0Name + "/" + currentName;
                    } else
                    IGNORE IGNORE IGNORE
                    */


                  //Method to get path of current selected item
                    if(mListItems.get(getAdapterPosition()).text.equals("New +")){
                        path = "StreamList";
                        parentpath = "StreamList";
                    }
                    else if (level == 0) {
                        path = "StreamList/" + currentName;
                        parentpath = "StreamList/";
                    }
                    else {
                        for (int i=level ;i>0;i--)
                        {
                            parent = parentgive(i,1);
                            String a = parentname;
                            String b = mListItems.get(getAdapterPosition() - parent).secondText;
                            parentname = b+"/"+a;
                        }
                        path = "StreamList/"+parentname+currentName;
                        parentpath = "StreamList/"+parentname;
                        Log.i("genpath",path);
                    }

                    final String finalPath = path;
                    final String finalparentPath = parentpath;

                    //DELETE Mode
                    if(currentMode.INSTANCE.getUpdateClicked() == 2) {
                        //Toast.makeText(mContext, String.format(Locale.ENGLISH, "Item at position %d was LONG clicked!", getAdapterPosition()), Toast.LENGTH_LONG).show();

                        //setting alert dilogue for delete mode
                        alert.setTitle("Do you want to Delete?");
                        alert.setMessage("Deleting this will also delete its descendants!");
                        Log.i("path", path);

                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                db = FirebaseDatabase.getInstance();
                                dbrefer = db.getReference();
                                Log.i("finall delete",finalPath);
                                //deleting item from database
                                dbrefer.child(finalPath).setValue(null);
                                dbrefer = db.getReference("SubjectPath");
                                dbrefer.child(currentName).setValue(null);
                                Toast.makeText(mContext,"Element Deleted successfully,Swipe Down to Refresh",Toast.LENGTH_LONG).show();
                             //  mlistFragment.reload();
                            }
                        });
                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });
                        alert.show();


                    }

                    //ADD Mode
                    else if (currentMode.INSTANCE.getUpdateClicked() == 1){
                        final String finalPath2 = path+"/";

                       //alert dilogue for adding element in database
                        alert.setTitle("Adding new element...");
                        alert.setMessage("New element will be added under selected element!");

                        //edittext input in alert dilogue with pre written previous path
                        final EditText input = new EditText(v.getRootView().getContext());
                        alert.setView(input);
                        input.setText(finalPath2);
                        //method to fix the pre written path text on edittext so user cant remove it
                        Selection.setSelection(input.getText(),input.getText().length());
                        input.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if(!s.toString().startsWith(finalPath2)){
                                    input.setText(finalPath2);
                                    Selection.setSelection(input.getText(),input.getText().length());

                                }

                            }
                        });

                        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                db = FirebaseDatabase.getInstance();
                                dbrefer = db.getReference();
                                /*store input from user along with its parent elements in inputText variable
                                for ex:- User adds child under "SEM 4" element and names it as "OOPM"
                                 hence the value of inputText will be "StreamList/Engineering/Computer Science/SEM 4/OOPM"
                                 */
                                String inputText = input.getText().toString().trim();

                                /*since our edittext already contains its parent elements (for getting the path)
                                  so we split the inputText variable and store actual input given by user in subname variable
                                   hence the value of subname will be "OOPM"
                                  */
                                String subname = inputText.substring(inputText.lastIndexOf("/") + 1);
                                Log.i("subname",subname);

                                if(inputText.equals(finalPath2)){
                                    Toast.makeText(mContext, "Error: Field can't be blank", Toast.LENGTH_SHORT).show();
                                    Log.i("exitt","eee");
                                    return;
                                }
                                if(subname.equals("name")){
                                    Toast.makeText(mContext, "Error: Invalid name", Toast.LENGTH_SHORT).show();
                                    Log.i("exitt","eee");
                                    return;
                                }

                                Log.i("finall add",inputText);
                                try{
                                if(finalPath == finalparentPath){
                                    dbrefer.child(inputText).setValue(inputText);
                                }
                                else{
                                    dbrefer.child(inputText).setValue(inputText);
                                    dbrefer.child(finalPath).child("name").setValue(currentName);
                                }
                                dbrefer.child("SubjectPath").child(subname).setValue(inputText);
                                dbrefer.child("SubjectPath").child(currentName).setValue(null);
                                }
                                catch (Exception e){
                                    Toast.makeText(mContext,String.valueOf(e),Toast.LENGTH_LONG).show();
                                }
                                Toast.makeText(mContext,"New Element added successfully,Swipe Down to Refresh",Toast.LENGTH_LONG).show();

                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });
                        alert.show();
                    }

                    //RENAME Mode
                    else if (currentMode.INSTANCE.getUpdateClicked() == 3){
                        //setting alert dilogue for rename mode
                        alert.setTitle("Do you want to Rename this element?");
                        alert.setMessage("Enter New Name for this element");
                        final EditText input = new EditText(v.getRootView().getContext());
                        alert.setView(input);
                        alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db = FirebaseDatabase.getInstance();
                                dbrefer = db.getReference();
                                final String inputText = input.getText().toString().trim();
                                if(inputText.isEmpty()){
                                    Toast.makeText(mContext, "Error: Field can't be blank", Toast.LENGTH_SHORT).show();
                                    Log.i("exitt","eee");
                                    return;
                                }
                                if(inputText.equals("name")){
                                    Toast.makeText(mContext, "Error: Invalid Name", Toast.LENGTH_SHORT).show();
                                    Log.i("exitt","eee");
                                    return;
                                }
                                try{
                                    dbrefer.child(finalPath).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild("name")){
                                                dbrefer.child(finalPath).child("name").setValue(inputText);
                                            }
                                            else {
                                                dbrefer.child(finalparentPath).child(inputText).setValue(finalparentPath+"/"+currentName);
                                                dbrefer.child(finalPath).setValue(null);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                catch (Exception e){
                                    Toast.makeText(mContext,String.valueOf(e),Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //on cancel
                            }
                        });
                        alert.show();
                    }
                    else {
                        Toast.makeText(mContext,"To Add,Delete or Rename an item , Please click on the buttons above",Toast.LENGTH_LONG).show();
                    }
                    return true;
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
                    mExpandIcon.animate().rotation(mListItems.get(getAdapterPosition()).isExpanded() ? -180 : 0).start();

                    //Toast.makeText(mContext, String.format(Locale.ENGLISH, "Item at position %d is expanded: %s", getAdapterPosition(), mItem.isExpanded()), Toast.LENGTH_SHORT).show();
                }
            });


        }
        //a function which gives location of parent node of selected node
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
