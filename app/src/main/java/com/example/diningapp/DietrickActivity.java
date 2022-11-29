package com.example.diningapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.example.diningapp.database.DBHandler;
import com.example.diningapp.databinding.ActivityDietrickBinding;
import com.example.diningapp.databinding.ActivityMainBinding;
import com.example.diningapp.databinding.FragmentMainBinding;
import com.example.diningapp.ui.main.PageViewModel;
import com.example.diningapp.ui.main.SectionsPagerAdapter;
import com.example.diningapp.util.DiningHallHour;
import com.example.diningapp.util.FoodItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.tabs.TabLayout;
import com.example.diningapp.ui.main.PageViewModel;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DietrickActivity extends AppCompatActivity {
    PopupWindow pop;
    private ActivityDietrickBinding binding;

    ObjectMapper mapper = new ObjectMapper();
    private PageViewModel pageViewModel;

    private DBHandler           dbHandler;
    int quantity = 0;

    ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietrick);

        cl = (ConstraintLayout) findViewById(R.id.constraintLayout1);
        binding = ActivityDietrickBinding.inflate(getLayoutInflater());

        dbHandler = new DBHandler(getApplicationContext());
        View                 root         = binding.getRoot();

        final ListView listView = binding.mobileList1;

        List<FoodItem>       menuList = null;
         try {
           String menuJsonString  = loadJSONFromAsset(getApplicationContext(), "menu.json");
            menuList               = mapper.readValue(menuJsonString, new TypeReference<List<FoodItem>>() {});
            dbHandler.init(menuList);
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        List<Map<String, String>> menuData = new ArrayList<>();

        List<FoodItem> foodItems =  dbHandler.getAllFoodItem();
        Collections.reverse(foodItems);
        for(FoodItem foodItem: foodItems) {
            // D2 only
            String hall = foodItem.getAmount();
            if(hall.contains("Dietrick")) {
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

        List<FoodItem> finalMenuList = menuList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long test){

            }
        });

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