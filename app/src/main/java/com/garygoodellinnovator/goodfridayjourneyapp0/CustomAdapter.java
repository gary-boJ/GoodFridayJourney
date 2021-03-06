package com.garygoodellinnovator.goodfridayjourneyapp0;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.garygoodellinnovator.goodfridayjourneyapp0.ui.slideshow.SlideshowFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class CustomAdapter
        extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<DataModel> dataSet;

    public CustomAdapter(ArrayList<DataModel> data) {
        this.dataSet = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        view.setOnClickListener(SlideshowFragment.myOnClickListener());

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int listPosition) {
        TextView tvName = holder.tvName;
        TextView tvTimeOfDay = holder.tvTimeOfDay;
        ImageView ivIcon = holder.ivIcon;
        ImageView ivPicture = holder.ivPicture;
        TextView tvHeading = holder.tvHeading;
        TextView tvScripture = holder.tvScripture;
        TextView tvPrayer = holder.tvPrayer;

        tvName.setText(dataSet.get(listPosition).getName());
        tvTimeOfDay.setText(dataSet.get(listPosition).getTimeOfDay());
        ivIcon.setImageResource(dataSet.get(listPosition).getIcon());
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Easter Egg " + listPosition,
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ivPicture.setImageResource(dataSet.get(listPosition).getPicture());
        tvHeading.setText(dataSet.get(listPosition).getHeading());

        Spanned spScripture = HtmlCompat.fromHtml( dataSet.get(listPosition).getScripture() ,
                HtmlCompat.FROM_HTML_MODE_COMPACT|HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS);
        tvScripture.setText(spScripture);

        Spanned spPrayer = HtmlCompat.fromHtml( dataSet.get(listPosition).getPrayer(),
                HtmlCompat.FROM_HTML_MODE_COMPACT|HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS);
        tvPrayer.setText(spPrayer);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class MyViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName, tvTimeOfDay, tvHeading, tvScripture, tvPrayer;
        ImageView ivIcon, ivPicture;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = this.itemView.findViewById(R.id.tvName);
            tvTimeOfDay = this.itemView.findViewById(R.id.tvTimeOfDay);
            ivIcon = this.itemView.findViewById(R.id.ivIcon);
            ivPicture = this.itemView.findViewById(R.id.ivPicture);
            tvHeading = this.itemView.findViewById(R.id.tvHeading);
            tvScripture = this.itemView.findViewById(R.id.tvScripture);
            tvPrayer = this.itemView.findViewById(R.id.tvPrayer);


        }
    }
}
