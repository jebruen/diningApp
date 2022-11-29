package com.example.diningapp;

import static com.example.diningapp.ui.main.PlaceholderFragment.MENU_JSON_FILE;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

// import com.example.diningapp.database.DBHandler;
import com.example.diningapp.databinding.ActivityDietrickBinding;
import com.example.diningapp.databinding.ActivityJohnstonBinding;
import com.example.diningapp.ui.main.PageViewModel;
import com.example.diningapp.util.FoodItem;
import com.example.diningapp.util.RestClient;
import com.example.diningapp.util.VTDiningScrapingUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class JohnstonActivity extends AppCompatActivity {
    private ActivityJohnstonBinding binding;

    ObjectMapper mapper = new ObjectMapper();
    private PageViewModel pageViewModel;

    // private DBHandler           dbHandler;
    private static       List<FoodItem>       foodItems         = new ArrayList<>();

    private        final RestClient client            = new RestClient();
    AlertDialog dialog;
    LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_johnston);

        binding = ActivityJohnstonBinding.inflate(getLayoutInflater());

        // dbHandler = new DBHandler(getApplicationContext());
        View                 root         = binding.getRoot();

        final ListView listView = binding.mobileList1;

        List<FoodItem>       menuList;
        try {
            if (MainActivity.USE_REMOTE_DATA) {
                Optional<String> response = client.getFoodItems();
                if (response.isPresent()) {
                    // Update local data
                    foodItems = mapper.readValue(response.get(), new TypeReference<List<FoodItem>>() {});
                } else {
                    String menuJsonString = VTDiningScrapingUtils.loadJSONFromAsset(getApplicationContext(), MENU_JSON_FILE);
                    foodItems = mapper.readValue(menuJsonString, new TypeReference<List<FoodItem>>() {});
                }

            } else if (foodItems.size() == 0){
                String menuJsonString = VTDiningScrapingUtils.loadJSONFromAsset(getApplicationContext(), MENU_JSON_FILE);
                foodItems = mapper.readValue(menuJsonString, new TypeReference<List<FoodItem>>() {});
            }
        }
        catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        List<Map<String, String>> menuData = new ArrayList<>();

        // List<FoodItem> foodItems =  dbHandler.getAllFoodItem();
        Collections.reverse(foodItems);
        for(FoodItem foodItem: foodItems) {
            // D2 only
            String hall = foodItem.getAmount();
            if(hall.contains("Johnston")) {
                Map<String, String> map = new HashMap<>(2);
                map.put("First Line", foodItem.getName());
                map.put("Second Line",foodItem.getAmount());
                menuData.add(map);
           }
        }

       SimpleAdapter simpleAdapter3= new SimpleAdapter(this, menuData,
                android.R.layout.simple_list_item_2,
                new String[] {"First Line", "Second Line" },
                new int[] {android.R.id.text1, android.R.id.text2 });

        listView.setAdapter(simpleAdapter3);

        setContentView(root);

    }

    /**
     * The helper function reading json file from assets
     * @param context
     * @param fileName the name of json file in assets
     * @return a string of file
     */
    public String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


}