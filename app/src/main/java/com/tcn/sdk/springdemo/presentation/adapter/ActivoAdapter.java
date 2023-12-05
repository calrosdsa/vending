package com.tcn.sdk.springdemo.presentation.adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.data.models.Activo;

import java.util.List;

// The adapter class which
// extends RecyclerView Adapter
public class ActivoAdapter extends RecyclerView.Adapter<ActivoAdapter.MyView> {

    // List with String type
    private List<Activo> list;

    // View Holder class which
    // extends RecyclerView.ViewHolder
    public class MyView
            extends RecyclerView.ViewHolder {

        // Text View
        TextView textView;

        // parameterised constructor for View Holder class
        // which takes the view as a parameter
        public MyView(View view)
        {
            super(view);

            // initialise TextView with id
            textView = (TextView)view
                    .findViewById(R.id.textview);
        }
    }

    // Constructor for adapter class
    // which takes a list of String type
    public ActivoAdapter(List<Activo> horizontalList)
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
                .inflate(R.layout.item,
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
        Activo activo = list.get(position);
        holder.textView.setText(activo.name);
        int defaultWidth = 98;
        int paddingWidth = 8;
        CardView cardView = holder.itemView.findViewById(R.id.cardview);
        cardView.setMinimumWidth((activo.celdas * defaultWidth)+ (paddingWidth * (activo.celdas-1)));
        Context context = cardView.getContext();
        if(activo.enabled){
            cardView.setCardBackgroundColor(context.getColor(R.color.primary));
            holder.textView.setTextColor(context.getColor(R.color.ui_base_white));
        }else {
            cardView.setCardBackgroundColor(context.getColor(R.color.ui_base_whitesmoke));
            holder.textView.setTextColor(context.getColor(R.color.primary));
        }
    }

    // Override getItemCount which Returns
    // the length of the RecyclerView.
    @Override
    public int getItemCount()
    {
        return list.size();
    }

}
