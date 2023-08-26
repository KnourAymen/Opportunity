package com.example.opportunity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TraineeCustomAdapter extends RecyclerView.Adapter<TraineeCustomAdapter.MyViewHolder> {

    private Context context;
    private List<Users> dataList;
    List<Integer> colorList;

    public TraineeCustomAdapter(Context context, List<Users> dataList) {
        this.context = context;
        this.dataList = dataList;
        colorList = new ArrayList<>();
        colorList.add(Color.rgb(255, 0, 0));    // red
        colorList.add(Color.rgb(0, 0, 255));    // blue
        colorList.add(Color.rgb(0, 255, 0));    // green
        colorList.add(Color.rgb(255, 255, 0));  // yellow
        colorList.add(Color.rgb(128, 0, 128));  // purple
        colorList.add(Color.rgb(255, 192, 203));  // pink
        colorList.add(Color.rgb(255, 165, 0));  // orange
        Collections.shuffle(colorList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (position < dataList.size()) {
            holder.roundedBorder.setBackgroundTintList(ColorStateList.valueOf(colorList.get(position)));
        }
        holder.traineeFullName.setText(dataList.get(position).getUserFullName());
        holder.traineePhoneNumber.setText(dataList.get(position).getUserPhoneNumber());
        holder.traineeSpecialty.setText(dataList.get(position).getUserSpecialty());
        holder.traineeShortFullName.setText(dataList.get(position).getUserShortFullName());
        holder.traineeCardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("USER_KEY", dataList.get(holder.getAdapterPosition()).getUserKey());
                intent.putExtra("USER_ADDRESS", dataList.get(holder.getAdapterPosition()).getUserAddress());
                intent.putExtra("USER_BIRTH_DAY", dataList.get(holder.getAdapterPosition()).getUserBirthDay());
                intent.putExtra("USER_DREAMS", dataList.get(holder.getAdapterPosition()).getUserDreams());
                intent.putExtra("USER_EMAIL", dataList.get(holder.getAdapterPosition()).getUserEmail());
                intent.putExtra("USER_FILE_URL", dataList.get(holder.getAdapterPosition()).getUserFileUrl());
                intent.putExtra("USER_FULL_NAME", dataList.get(holder.getAdapterPosition()).getUserFullName());
                intent.putExtra("USER_ADVANTAGES_COMMENT", dataList.get(holder.getAdapterPosition()).getUserOfpptFirstComment());
                intent.putExtra("USER_DISADVANTAGES_COMMENT", dataList.get(holder.getAdapterPosition()).getUserOfpptSecondComment());
                intent.putExtra("USER_PHONE_NUMBER", dataList.get(holder.getAdapterPosition()).getUserPhoneNumber());
                intent.putExtra("USER_SPECIALTY", dataList.get(holder.getAdapterPosition()).getUserSpecialty());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<Users> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout roundedBorder;
        TextView traineeFullName, traineePhoneNumber, traineeSpecialty, traineeShortFullName;
        CardView traineeCardView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roundedBorder = itemView.findViewById(R.id.rounded_border);
            traineeCardView = itemView.findViewById(R.id.trainee_card_view);
            traineeFullName = itemView.findViewById(R.id.trainee_full_name);
            traineePhoneNumber = itemView.findViewById(R.id.trainee_phone_number);
            traineeSpecialty = itemView.findViewById(R.id.trainee_specialty);
            traineeShortFullName = itemView.findViewById(R.id.trainee_short_full_name);

        }
    }
}
