package com.tcn.sdk.springdemo.presentation.adapter;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcn.sdk.springdemo.R;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

// The adapter class which
// extends RecyclerView Adapter
public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.MyView> {
    // List with String type
    private final List<File> list;

    // View Holder class which
    // extends RecyclerView.ViewHolder
    public static class MyView
            extends RecyclerView.ViewHolder {

        // Text View
        TextView textView;
        TextView textPosition;

        // parameterised constructor for View Holder class
        // which takes the view as a parameter
        public MyView(View view)
        {
            super(view);

            // initialise TextView with id
            textView = (TextView)view
                    .findViewById(R.id.log_name);
            textPosition = (TextView)view
                    .findViewById(R.id.log_position);
        }
    }

    // Constructor for adapter class
    // which takes a list of String type
    public LogsAdapter(List<File> horizontalList)
    {
        this.list = horizontalList;
    }

    // Override onCreateViewHolder which deals
    // with the inflation of the card layout
    // as an item for the RecyclerView.
    @Override
    public MyView onCreateViewHolder(ViewGroup parent,
                                     int viewType)
    {

        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.log,
                        parent,
                        false);

        // return itemView
        return new MyView(itemView);
    }

    // Override onBindViewHolder which deals
    // with the setting of different data
    // and methods related to clicks on
    // particular items of the RecyclerView.
    @Override
    public void onBindViewHolder(final MyView holder,
                                 final int position)
    {

        // Set the text of each item of
        // Recycler view with the list items
        File file = list.get(position);
        holder.textView.setText(file.getName());
        holder.textPosition.setText(String.format("%d.-",position +1));
//        int defaultWidth = 130;
//        int paddingWidth = 8;
//        CardView cardView = holder.itemView.findViewById(R.id.cardview);
//        cardView.setMinimumWidth((activo.celdas * defaultWidth)+ (paddingWidth * (activo.celdas-1)));

    }

    // Override getItemCount which Returns
    // the length of the RecyclerView.
    @Override
    public int getItemCount()
    {
        return list.size();
    }

}
