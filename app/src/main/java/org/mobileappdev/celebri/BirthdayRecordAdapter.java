package org.mobileappdev.celebri;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BirthdayRecordAdapter extends RecyclerView.Adapter<BirthdayRecordAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<String> names, ages, dates, favoriteItems;

    public BirthdayRecordAdapter(Activity activity, Context context, ArrayList<String> names, ArrayList<String> ages, ArrayList<String> dates, ArrayList<String> favoriteItems) {
        this.activity = activity;
        this.context = context;
        this.names = names;
        this.ages = ages;
        this.dates = dates;
        this.favoriteItems = favoriteItems;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.birthday_record_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nameText.setText(names.get(position) + ",");
        holder.ageText.setText("turns " + ages.get(position));
        holder.dateText.setText(dates.get(position));
        holder.favoriteItemText.setText("Favorite item: " + favoriteItems.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameText, ageText, dateText, favoriteItemText;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.name_text);
            ageText = itemView.findViewById(R.id.age_text);
            dateText = itemView.findViewById(R.id.date_text);
            favoriteItemText = itemView.findViewById(R.id.favorite_item_text);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}