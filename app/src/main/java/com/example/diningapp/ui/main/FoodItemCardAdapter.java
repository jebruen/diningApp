package com.example.diningapp.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diningapp.MainActivity;
import com.example.diningapp.R;
import com.example.diningapp.util.FoodItem;
import com.example.diningapp.util.RestClient;

import java.util.List;
import java.util.Optional;

public class FoodItemCardAdapter extends RecyclerView.Adapter<FoodItemCardAdapter.ViewHolder> {

    private final Context        context;
    private final List<FoodItem> foodItemArrayList;

    // Constructor
    public FoodItemCardAdapter(Context context, List<FoodItem> foodItemArrayList) {
        this.context = context;
        this.foodItemArrayList = foodItemArrayList;
    }

    @NonNull
    @Override
    public FoodItemCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemCardAdapter.ViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        FoodItem foodItem = foodItemArrayList.get(position);
        holder.foodItemName .setText(foodItem.getName());
        holder.thumbUpText  .setText(String.valueOf(foodItem.getThumbUpCount()));
        holder.thumbDownText.setText(String.valueOf(foodItem.getThumbDownCount()));
        holder.hourglassText.setText(String.valueOf(foodItem.getWaitingLine()));
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return foodItemArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView hourglassView;
        private final ImageView thumbDownView;
        private final ImageView thumbUpView;

        private final TextView hourglassText;
        private final TextView foodItemName;
        private final TextView thumbUpText;
        private final TextView thumbDownText;

        final RestClient client = new RestClient();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            hourglassView = itemView.findViewById(R.id.hourglass_view);
            hourglassText = itemView.findViewById(R.id.hourglass_text);
            foodItemName  = itemView.findViewById(R.id.food_item_name);
            thumbUpText   = itemView.findViewById(R.id.thumb_up_text);
            thumbDownText = itemView.findViewById(R.id.thumb_down_text);
            thumbDownView = itemView.findViewById(R.id.thumb_up_view);
            thumbUpView   = itemView.findViewById(R.id.thumb_down_view);

            hourglassView.setOnClickListener(view -> {
                int oldData = Integer.parseInt(hourglassText.getText().toString());
                oldData++;
                try {
                    // Update remote database
                    Optional<String> response = client.request("http://10.0.2.2:8080/FoodItemsUpdateWaitingLine?" +"foodName="
                            + foodItemName.getText().toString()
                            + "&waitingLine="+ oldData);
                    if (response.isPresent()) {
                        // Update local data
                        hourglassText.setText(String.valueOf(oldData));
                        PlaceholderFragment.updateFoodItem(
                                foodItemName.getText().toString(),
                                "waitingLine",
                                oldData
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thumbUpView.setOnClickListener(view -> {
                int oldData = Integer.parseInt(thumbUpText.getText().toString());
                oldData++;
                try {
                    // Update remote database
                    if (MainActivity.USE_REMOTE_DATA) {
                        Optional<String> response = client.request("http://10.0.2.2:8080/FoodItemsUpdateThumbUp?" +"foodName="
                                + foodItemName.getText().toString()
                                + "&thumbUpCount="+ oldData);
                        if (response.isPresent()) {
                            // Update local data
                            thumbUpText.setText(String.valueOf(oldData));
                            PlaceholderFragment.updateFoodItem(
                                    foodItemName.getText().toString(),
                                    "thumbUp",
                                    oldData
                            );
                        }
                    }
                    else {
                        thumbUpText.setText(String.valueOf(oldData));
                        PlaceholderFragment.updateFoodItem(
                                foodItemName.getText().toString(),
                                "thumbUp",
                                oldData
                        );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thumbDownView.setOnClickListener(view -> {
                int oldData = Integer.parseInt(thumbUpText.getText().toString());
                oldData++;
                try {
                    // Update remote database
                    Optional<String> response = client.request("http://10.0.2.2:8080/FoodItemsUpdateWaitingLine?" +"foodName="
                            + foodItemName.getText().toString()
                            + "&thumbDownCount="+ oldData);
                    if (response.isPresent()) {
                        // Update local data
                        thumbUpText.setText(String.valueOf(oldData));
                        hourglassText.setText(String.valueOf(oldData));
                        PlaceholderFragment.updateFoodItem(
                                foodItemName.getText().toString(),
                                "thumbDown",
                                oldData
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }

}
