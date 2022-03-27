package com.example.protimer.ui.ui;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.protimer.R;

import java.util.ArrayList;

public class myAdapter extends RecyclerView.Adapter<myAdapter.MyViewHolder> {

    Context context;
    ArrayList<history> historyArrayList;


    public myAdapter(Context context, ArrayList<history> historyArrayList) {
        this.context = context;
        this.historyArrayList = historyArrayList;
    }

    @NonNull
    @Override
    public myAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myAdapter.MyViewHolder holder, int position) {
        history history = historyArrayList.get(position);
        holder.history.setText(history.history);
        holder.date.setText("Date: "+history.date);
    }

    @Override
    public int getItemCount() {
        return historyArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        TextView history;
        TextView date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            history = itemView.findViewById(R.id.history11);
            date = itemView.findViewById(R.id.history10);
        }
    }
}
