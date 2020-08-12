package com.app.hardik.studypdf;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//this adapter for horizontal cardview in admin home page that shows statistics

public class HotizontalAdapter extends RecyclerView.Adapter<HotizontalAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Stats> imageModelArrayList;

    public HotizontalAdapter(Context ctx, ArrayList<Stats> imageModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
    }

    @Override
    public HotizontalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.horizontalcard, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(HotizontalAdapter.MyViewHolder holder, int position) {


        holder.iv.setImageResource(imageModelArrayList.get(position).getImage_drawable());
        holder.time.setText(imageModelArrayList.get(position).getName());
        switch (position){
            case 0:
                holder.time.setTextColor(Color.parseColor("#6200EE"));
                break;
            case 1:
                holder.time.setTextColor(Color.parseColor("#ff1b76"));
                break;
            case 2:
                holder.time.setTextColor(Color.parseColor("#ffa000"));
                break;
            default:
        }
    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView time;
        ImageView iv;

        public MyViewHolder(View itemView) {
            super(itemView);

            time = (TextView) itemView.findViewById(R.id.tv);
            iv = (ImageView) itemView.findViewById(R.id.iv);


        }

    }



}
