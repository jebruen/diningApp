package com.example.diningapp.ui.main;

import  com.example.diningapp.util.LabelsUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diningapp.MainActivity;
import com.example.diningapp.R;
import com.example.diningapp.util.FoodItem;
import com.example.diningapp.util.RestClient;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class FoodItemCardAdapter extends RecyclerView.Adapter<FoodItemCardAdapter.ViewHolder>  {

    private final Context        context;
    private final List<FoodItem> foodItemArrayList;

    public enum FoodItemUpdateType {
        UPDATE_DISLIKE,
        UPDATE_LIKE,
        UPDATE_WAITING_LINE
    }

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
        holder.foodItemName  .setText(foodItem.getName());
        holder.thumbUpText   .setText(String.valueOf(foodItem.getThumbUpCount()));
        holder.thumbDownText .setText(String.valueOf(foodItem.getThumbDownCount()));
        holder.hourglassText .setText(String.valueOf(foodItem.getWaitingLine()));
        holder.detailView    .setTooltipText(foodItem.getDescription());
        holder.tableRow      .setTooltipText(foodItem.getLabel());

        TooltipCompat.setTooltipText(holder.detailView, foodItem.getDescription());

        List<String> labels =
                Arrays.stream(foodItem.getLabel().split(";")).distinct()
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toList());

        for (String label: labels) {
            addLabelToView(holder.tableRow, label);
        }
    }

    public void addLabelToView(TableRow tableRow, String label) {
        TableRow.LayoutParams layoutParams=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TextView textView1 = new TextView(tableRow.getContext());
        layoutParams.setMargins(10, 0, 10, 10);
        textView1.setText(label);
        textView1.setBackgroundColor(ContextCompat.getColor(tableRow.getRootView().getContext(), R.color.very_light_gray)); // hex color 0xAARRGGBB
        textView1.setPadding(20, 10, 20, 10);
        textView1.setLayoutParams(layoutParams);
        tableRow.addView(textView1, 0);
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return foodItemArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
        private final ImageView hourglassView;
        private final ImageView thumbDownView;
        private final ImageView thumbUpView;
        private final ImageView addLabelView;
        private final ImageView detailView;

        private final TableRow tableRow;

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
            thumbDownView = itemView.findViewById(R.id.thumb_down_view);
            thumbUpView   = itemView.findViewById(R.id.thumb_up_view);
            addLabelView  = itemView.findViewById(R.id.add_label);
            detailView    = itemView.findViewById(R.id.detail_view);
            tableRow      = itemView.findViewById(R.id.label_row);

            hourglassView.setOnClickListener(view -> {
                updateFoodItem(hourglassText, FoodItemUpdateType.UPDATE_WAITING_LINE);
            });

            thumbUpView.setOnClickListener(view -> {
                updateFoodItem(thumbUpText, FoodItemUpdateType.UPDATE_LIKE);
            });

            thumbDownView.setOnClickListener(view -> {
                updateFoodItem(thumbDownText, FoodItemUpdateType.UPDATE_DISLIKE);
            });

            addLabelView.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.setOnMenuItemClickListener(this);
                popup.inflate(R.menu.label_menu);
                popup.show();
            });

            detailView.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Detail");
                builder.setMessage(StringUtils.isNotBlank(detailView.getTooltipText()) ? detailView.getTooltipText() : "No Detailed Information.");
                builder.show();
            });
        }

        private void updateFoodItem(TextView textView, FoodItemUpdateType type) {
            int updatedData = Integer.parseInt(textView.getText().toString());
            updatedData++;
            Toast.makeText(textView.getContext().getApplicationContext(), LabelsUtils.UPDATE_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show();
            try {
                Optional<String> response;
                // Update remote database
                if (MainActivity.USE_REMOTE_DATA) {
                    switch (type) {
                        case UPDATE_WAITING_LINE:
                            response = client.updateFoodItemWaitingLine(foodItemName.getText().toString(), updatedData);
                            break;
                        case UPDATE_LIKE:
                            response = client.updateFoodItemThumbUp(foodItemName.getText().toString(), updatedData);
                            break;
                        case UPDATE_DISLIKE:
                            response = client.updateFoodItemThumbDown(foodItemName.getText().toString(), updatedData);
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }

                    if (response.isPresent()) {
                        // Update local data
                        textView.setText(String.valueOf(updatedData));
                        PlaceholderFragment.updateFoodItem(
                                foodItemName.getText().toString(),
                                type,
                                updatedData
                        );
                    }
                } else {
                    // Just update local data
                    textView.setText(String.valueOf(updatedData));
                    PlaceholderFragment.updateFoodItem(
                            foodItemName.getText().toString(),
                            type,
                            updatedData
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            try {
                String labelToAdd = menuItem.getTitle().toString();

                if (Objects.nonNull(tableRow.getTooltipText())) {
                    List<String> existingLabels =
                            Arrays.stream(tableRow.getTooltipText().toString().split(";"))
                                    .distinct()
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.toList());

                    if (existingLabels.contains(labelToAdd)) {
                        Toast.makeText(tableRow.getContext().getApplicationContext(), LabelsUtils.EXIST_LABEL_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                    else if (LabelsUtils.ifConflict(existingLabels, labelToAdd)) {
                        Toast.makeText(tableRow.getContext().getApplicationContext(), LabelsUtils.CONFLICT_LABEL_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        addFoodItemLabel(foodItemName.getText().toString(), menuItem.getTitle().toString(), this.itemView);
                        existingLabels.add(labelToAdd);
                        tableRow.setTooltipText(String.join(";", existingLabels));
                    }
                }
                else {
                    addFoodItemLabel(foodItemName.getText().toString(), menuItem.getTitle().toString(), this.itemView);
                    tableRow.setTooltipText(labelToAdd);
                }

            } catch (ExecutionException | TimeoutException | InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        private void addFoodItemLabel(String foodItem, String label, View view) throws ExecutionException, InterruptedException, TimeoutException {
            TableRow.LayoutParams layoutParams=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            TextView textView1 = new TextView(view.getContext());
            layoutParams.setMargins(10, 0, 10, 10);
            textView1.setText(label);
            textView1.setBackgroundColor(ContextCompat.getColor(this.itemView.getContext(), R.color.very_light_gray)); // hex color 0xAARRGGBB
            textView1.setPadding(20, 10, 20, 10);
            textView1.setLayoutParams(layoutParams);
            Optional<String> response;
            // Update remote database
            if (MainActivity.USE_REMOTE_DATA) {
                response = client.updateFoodItemLabels(foodItem, label);
                if (response.isPresent()) {
                    // Update local data
                    tableRow.addView(textView1, 0);
                    Toast.makeText(this.itemView.getRootView().getContext().getApplicationContext(), "Added Label: " + label, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this.itemView.getRootView().getContext().getApplicationContext(), "System went wrong. Try Later. " + label, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                client.updateFoodItemLabels(foodItem, label);
                tableRow.addView(textView1, 0);
                Toast.makeText(this.itemView.getRootView().getContext().getApplicationContext(), "Added Label: " + label, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}