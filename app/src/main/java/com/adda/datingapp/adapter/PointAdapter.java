/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 *  if your need any help knock this number +8801776254584 whatsapp
 */

package com.adda.datingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adda.datingapp.R;
import com.adda.datingapp.activity.InComingVdActivity;
import com.adda.datingapp.activity.OrderActivity;
import com.adda.datingapp.databinding.PointDemoBinding;
import com.adda.datingapp.model.PointModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PointAdapter extends RecyclerView.Adapter<PointAdapter.PointViewHolder> {

    ArrayList<PointModel> list;
    Context context;

    public PointAdapter(ArrayList<PointModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public PointViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.point_demo, parent, false);
        return new PointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PointAdapter.PointViewHolder holder, int position) {
        PointModel pointModel=list.get(position);
        holder.binding.point.setText(pointModel.getPoint());
        holder.binding.taka.setText(pointModel.getTaka());
        holder.binding.point10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(), OrderActivity.class);
                intent.putExtra("object",pointModel);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PointViewHolder extends RecyclerView.ViewHolder{
        PointDemoBinding binding;

        public PointViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding=PointDemoBinding.bind(itemView);
        }
    }
}
