package com.example.diningapp.ui.main;

import static com.example.diningapp.ui.main.FoodItemCardAdapter.FoodItemStatus.NEUTRAL;

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
import com.example.diningapp.util.LabelsUtils;
import com.example.diningapp.util.RestClient;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class FoodItemCardAdapter extends RecyclerView.Adapter<FoodItemCardAdapter.ViewHolder>  {

    private final Context        context;
    private final List<FoodItem> foodItemArrayList;

    // Constructor
    public FoodItemCardAdapter(Context context, List<FoodItem> foodItemArrayList) {
        this.context = context;
        this.foodItemArrayList = foodItemArrayList;
    }

    public enum FoodItemUpdateType {
        UPDATE_DISLIKE,
        UPDATE_LIKE,
        UPDATE_WAITING_LINE
    }

    public enum FoodItemStatus {
        LIKE    ("LIKE"),
        DISLIKE ("DISLIKE"),
        NEUTRAL ("NEUTRAL");

        public final String status;

        FoodItemStatus(String status) { this.status = status;}

        public static FoodItemStatus getByStatus(String status) {
            for (FoodItemStatus status1 :values()) {
                if (status1.status.equals(status)) {
                    return status1;
                }
            }
            return null;
        }
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
        holder.detailView    .setTooltipText(
                "Description: " +
                        StringUtils.defaultIfBlank(foodItem.getDescription(), "No detailed information.")   + "\n" +
                        "Amount: "      +
                        StringUtils.defaultIfBlank(foodItem.getAmount(), "No detailed information.")        + "\n"

        );
        holder.tableRow      .setTooltipText(foodItem.getLabel());

        TooltipCompat.setTooltipText(holder.detailView,
                "Description: " +
                        StringUtils.defaultIfBlank(foodItem.getDescription(), "No detailed information.")   + "\n" +
                        "Amount: "      +
                        StringUtils.defaultIfBlank(foodItem.getAmount(), "No detailed information.")        + "\n"
        );

        Set<String> labels =
                Arrays.stream(foodItem.getLabel().split(";"))
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toSet());

        for (String label: labels) {
            addLabelToView(holder.tableRow, label);
        }
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

            foodItemName.setTooltipText(NEUTRAL.status);

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
                    Toast.makeText(this.itemView.getRootView().getContext().getApplicationContext(), "System went wrong. Try Later. ", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                tableRow.addView(textView1, 0);
                Toast.makeText(this.itemView.getRootView().getContext().getApplicationContext(), "Added Label: " + label, Toast.LENGTH_SHORT).show();
            }
        }

        private void updateFoodItem(TextView textView, FoodItemUpdateType type) {
            int updatedData = Integer.parseInt(textView.getText().toString());
            String foodItemNameValue = foodItemName.getText().toString();

            //
            try {
                Optional<String> response;
                updatedData++;
                // Update remote database
                if (MainActivity.USE_REMOTE_DATA) {
                    switch (type) {
                        case UPDATE_WAITING_LINE:
                            response = client.updateFoodItemWaitingLine(foodItemNameValue, updatedData);
                            break;
                        case UPDATE_LIKE:
                            response = client.updateFoodItemThumbUp(foodItemNameValue, updatedData);
                            break;
                        case UPDATE_DISLIKE:
                            response = client.updateFoodItemThumbDown(foodItemNameValue, updatedData);
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }

                    if (response.isPresent()) {
                        updateLocalData(type, updatedData, textView);
                    }
                } else {
                    updateLocalData(type, updatedData, textView);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void updateLocalData(FoodItemUpdateType type, int updatedData, TextView textView) {
            switch (type) {
                case UPDATE_WAITING_LINE:
                    textView.setText(String.valueOf(updatedData));
                    PlaceholderFragment.updateFoodItem(
                            foodItemName.getText().toString(),
                            type,
                            updatedData
                    );
                    Toast.makeText(textView.getContext().getApplicationContext(), LabelsUtils.UPDATE_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_LIKE:
                    String likeStatus = foodItemName.getTooltipText().toString();
                    switch (Objects.requireNonNull(FoodItemStatus.getByStatus(likeStatus))) {
                        case NEUTRAL:
                            addLike();
                            Toast.makeText(textView.getContext().getApplicationContext(), LabelsUtils.UPDATE_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show();
                            break;
                        case LIKE:
                            cancelLike();
                            Toast.makeText(textView.getContext().getApplicationContext(), LabelsUtils.UPDATE_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show();
                            break;
                        case DISLIKE:
                            cancelDisLike();
                            addLike();
                            Toast.makeText(textView.getContext().getApplicationContext(), LabelsUtils.UPDATE_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                    break;
                case UPDATE_DISLIKE:
                    String dislikeStatus = foodItemName.getTooltipText().toString();
                    switch (Objects.requireNonNull(FoodItemStatus.getByStatus(dislikeStatus))) {
                        case NEUTRAL:
                            addDisLike();
                            Toast.makeText(textView.getContext().getApplicationContext(), LabelsUtils.UPDATE_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show();
                            break;
                        case LIKE:
                            cancelLike();
                            addDisLike();
                            Toast.makeText(textView.getContext().getApplicationContext(), LabelsUtils.UPDATE_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show();
                            break;
                        case DISLIKE:
                            cancelDisLike();
                            Toast.makeText(textView.getContext().getApplicationContext(), LabelsUtils.UPDATE_SUCCESSFUL_MESSAGE, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }

        private void addLike() {
            int thumbUpCount = Integer.parseInt(thumbUpText.getText().toString());
            thumbUpCount++;
            thumbUpText.setText(String.valueOf(thumbUpCount));
            foodItemName.setTooltipText(FoodItemStatus.LIKE.status);
            thumbUpView.setBackgroundResource(R.drawable.ic_thumb_up);
            PlaceholderFragment.updateFoodItem(
                    foodItemName.getText().toString(),
                    FoodItemUpdateType.UPDATE_LIKE,
                    thumbUpCount
            );
        }

        private void cancelLike() {
            int thumbUpCount = Integer.parseInt(thumbUpText.getText().toString());
            thumbUpCount--;
            thumbUpText.setText(String.valueOf(thumbUpCount));
            foodItemName.setTooltipText(NEUTRAL.status);
            thumbUpView.setBackgroundResource(R.drawable.ic_thumb_up_empty);
            PlaceholderFragment.updateFoodItem(
                    foodItemName.getText().toString(),
                    FoodItemUpdateType.UPDATE_LIKE,
                    thumbUpCount
            );
        }

        private void addDisLike() {
            int thumbDownCount = Integer.parseInt(thumbDownText.getText().toString());
            thumbDownCount++;
            thumbDownText.setText(String.valueOf(thumbDownCount));
            foodItemName.setTooltipText(FoodItemStatus.DISLIKE.status);
            thumbDownView.setBackgroundResource(R.drawable.ic_thumb_down);
            PlaceholderFragment.updateFoodItem(
                    foodItemName.getText().toString(),
                    FoodItemUpdateType.UPDATE_DISLIKE,
                    thumbDownCount
            );
        }

        private void cancelDisLike() {
            int thumbDownCount = Integer.parseInt(thumbDownText.getText().toString());
            thumbDownCount--;
            thumbDownText.setText(String.valueOf(thumbDownCount));
            foodItemName.setTooltipText(NEUTRAL.status);
            thumbDownView.setBackgroundResource(R.drawable.ic_thumb_down_empty);
            PlaceholderFragment.updateFoodItem(
                    foodItemName.getText().toString(),
                    FoodItemUpdateType.UPDATE_DISLIKE,
                    thumbDownCount
            );
        }

    }

    private void addLabelToView(TableRow tableRow, String label) {
        TableRow.LayoutParams layoutParams=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TextView textView1 = new TextView(tableRow.getContext());
        layoutParams.setMargins(10, 0, 10, 10);
        textView1.setText(label);
        textView1.setBackgroundColor(ContextCompat.getColor(tableRow.getRootView().getContext(), R.color.very_light_gray)); // hex color 0xAARRGGBB
        textView1.setPadding(20, 10, 20, 10);
        textView1.setLayoutParams(layoutParams);
        tableRow.addView(textView1, 0);
    }
}