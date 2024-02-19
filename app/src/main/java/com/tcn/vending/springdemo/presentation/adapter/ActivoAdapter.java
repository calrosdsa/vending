package com.tcn.vending.springdemo.presentation.adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcn.vending.springdemo.R;
import com.tcn.vending.springdemo.data.models.Activo;

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
        TextView activoName;


        // parameterised constructor for View Holder class
        // which takes the view as a parameter
        public MyView(View view)
        {
            super(view);

            // initialise TextView with id
            textView = (TextView)view
                    .findViewById(R.id.textview);
            activoName = (TextView)view
                    .findViewById(R.id.activoName);
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
        holder.textView.setText(activo.celdaName);
        holder.activoName.setText(activo.activoName);
        int defaultWidth = 95;
//        int defaultWidth = 127;
        int paddingWidth = 8;
//        TextView activoName = holder.item
        CardView cardView = holder.itemView.findViewById(R.id.cardview);
        double result = (activo.celdas * defaultWidth)+ (paddingWidth * (activo.celdas-1));
        cardView.setMinimumWidth((int) Math.ceil(result));
        Context context = cardView.getContext();
        holder.itemView.findViewById(R.id.activo_layout).setMinimumWidth((int) Math.ceil(result));
//        cardView.setBackground(context.getDrawable(R.drawable.card));
        if(activo.enabled){
            cardView.setCardBackgroundColor(context.getColor(R.color.primary));
            holder.textView.setTextColor(context.getColor(R.color.ui_base_white));
            holder.activoName.setTextColor(context.getColor(R.color.ui_base_white));
        }else {
            cardView.setAlpha(0.35f);
            cardView.setCardBackgroundColor(context.getColor(R.color.primary));
            holder.textView.setTextColor(context.getColor(R.color.ui_base_white));
            holder.activoName.setTextColor(context.getColor(R.color.ui_base_white));
//            cardView.setCardBackgroundColor(context.getColor(R.color.ui_base_white));
//            holder.textView.setTextColor(context.getColor(R.color.primary));
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
