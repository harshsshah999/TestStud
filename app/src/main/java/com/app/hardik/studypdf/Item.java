package com.app.hardik.studypdf;

import com.multilevelview.models.RecyclerViewItem;

//This is Model Class for each item of MultiLevelRecyclerView

public class Item extends RecyclerViewItem {

    String text="";

    String secondText = "";

    public String getSecondText() {
        return secondText;
    }

    public void setSecondText(String secondText) {
        this.secondText = secondText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    protected Item(int level) {
        super(level);
    }
}
