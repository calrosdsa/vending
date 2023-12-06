package com.tcn.vending.springdemo.presentation.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;

import com.tcn.vending.springdemo.R;
import com.tcn.vending.springdemo.data.models.Celda;

import java.util.List;

// The adapter class which
// extends RecyclerView Adapter
public class CeldaAdapter  extends RecyclerView.Adapter<CeldaAdapter.MyView> {

    // List with String type
    private List<Celda> list;

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
    public CeldaAdapter(List<Celda> horizontalList)
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
        Celda celda = list.get(position);
        if(celda.mCanShow){
        holder.textView.setText(String.valueOf(celda.mSlotNumber));
        holder.itemView.findViewById(R.id.cardview).setMinimumWidth(130);
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
