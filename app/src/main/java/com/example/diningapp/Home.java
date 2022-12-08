package com.example.diningapp;

import static com.example.diningapp.ui.main.PlaceholderFragment.HOUR_JSON_FILE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.fragment.app.Fragment;

import com.example.diningapp.databinding.FragmentHoursBinding;
import com.example.diningapp.databinding.FragmentMainBinding;
import com.example.diningapp.util.DiningHallHour;
import com.example.diningapp.util.FoodItem;
import com.example.diningapp.util.RestClient;
import com.example.diningapp.util.VTDiningScrapingUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Home extends Fragment {
    private FragmentHoursBinding binding;
    private static List<DiningHallHour> hallHourList = new ArrayList<>();
    private        final RestClient client            = new RestClient();
    private static final ObjectMapper mapper            = new ObjectMapper();
    public Home() {


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        binding                           = FragmentHoursBinding.inflate(inflater, container, false);
        View                 root         = binding.getRoot();
        final ListView listView          = binding.hourList;


        try {
            if (MainActivity.USE_REMOTE_DATA) {
                Optional<String> response = client.getHours();
                if (response.isPresent()) {
                    // Update local data
                    hallHourList = mapper.readValue(response.get(), new TypeReference<List<DiningHallHour>>() {});
                } else {
                    String menuJsonString = VTDiningScrapingUtils.loadJSONFromAsset(getContext(), HOUR_JSON_FILE);
                    hallHourList = mapper.readValue(menuJsonString, new TypeReference<List<DiningHallHour>>(){});
                }
            } else if (hallHourList.size() == 0){
                String hoursJsonString = VTDiningScrapingUtils.loadJSONFromAsset(getContext(), HOUR_JSON_FILE);
                hallHourList = mapper.readValue(hoursJsonString, new TypeReference<List<DiningHallHour>>() {});
            }
        }
        catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        List<Map<String, String>> hoursData = new ArrayList<>();
        List<DiningHallHour> diningHallHours =  hallHourList;
        for(DiningHallHour diningHallHour: diningHallHours) {
            Map<String, String> map = new HashMap<>(2);
            map.put("First Line", diningHallHour.getDiningHall());
            map.put("Second Line",diningHallHour.getHours());
            hoursData.add(map);
        }

        SimpleAdapter simpleAdapter2= new SimpleAdapter(this.getContext(), hoursData,
                android.R.layout.simple_list_item_2,
                new String[] {"First Line", "Second Line"},
                new int[] {android.R.id.text1, android.R.id.text2 });


        return root;
    }
}
