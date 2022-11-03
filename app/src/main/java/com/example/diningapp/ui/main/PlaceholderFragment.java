package com.example.diningapp.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.diningapp.database.DBHandler;
import com.example.diningapp.databinding.FragmentMainBinding;
import com.example.diningapp.util.DiningHallHour;
import com.example.diningapp.util.FoodItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    ObjectMapper mapper = new ObjectMapper();

    private PageViewModel       pageViewModel;
    private FragmentMainBinding binding;
    private DBHandler           dbHandler;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding                           = FragmentMainBinding.inflate(inflater, container, false);
        dbHandler                         = new DBHandler(this.getContext());
        View                 root         = binding.getRoot();
        List<DiningHallHour> hallHourList = new ArrayList<>();
        List<FoodItem>       menuList;

        final ListView listView = binding.mobileList;


        try {
            String hoursJsonString = loadJSONFromAsset(this.getContext(), "hours.json");
            hallHourList           = mapper.readValue(hoursJsonString, new TypeReference<List<DiningHallHour>>() {});
            String menuJsonString  = loadJSONFromAsset(this.getContext(), "menu.json");
            menuList               = mapper.readValue(menuJsonString, new TypeReference<List<FoodItem>>() {});
            dbHandler.init(menuList);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        List<Map<String, String>> hoursData = new ArrayList<>();
        for(DiningHallHour diningHallHour: hallHourList) {
            Map<String, String> map = new HashMap<>(2);
            map.put("First Line", diningHallHour.getDiningHall());
            map.put("Second Line",diningHallHour.getHours());
            hoursData.add(map);
        }

        List<Map<String, String>> menuData = new ArrayList<>();

        List<FoodItem> foodItems =  dbHandler.getAllFoodItem();
        Collections.reverse(foodItems);
        for(FoodItem foodItem: foodItems) {
            Map<String, String> map = new HashMap<>(2);
            map.put("First Line", foodItem.getName());
            map.put("Second Line",foodItem.getDescription());
            menuData.add(map);
        }

        SimpleAdapter simpleAdapter= new SimpleAdapter(this.getContext(), menuData,
                android.R.layout.simple_list_item_2,
                new String[] {"First Line", "Second Line" },
                new int[] {android.R.id.text1, android.R.id.text2 });

        SimpleAdapter simpleAdapter2= new SimpleAdapter(this.getContext(), hoursData,
                android.R.layout.simple_list_item_2,
                new String[] {"First Line", "Second Line"},
                new int[] {android.R.id.text1, android.R.id.text2 });

        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // textView.setText(s);
                if (StringUtils.equals(s, "Hello world from section: 2")) {
                    listView.setAdapter(simpleAdapter);
                } else {
                    listView.setAdapter(simpleAdapter2);
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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