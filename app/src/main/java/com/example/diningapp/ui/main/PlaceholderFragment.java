package com.example.diningapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diningapp.MainActivity;
import com.example.diningapp.R;
import com.example.diningapp.databinding.FragmentMainBinding;
import com.example.diningapp.util.DiningHallHour;
import com.example.diningapp.util.FoodItem;
import com.example.diningapp.util.RestClient;
import com.example.diningapp.util.VTDiningScrapingUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String MENU_JSON_FILE     = "menu.json";
    public static final String HOUR_JSON_FILE     = "hours.json";
    public static       boolean schedulerRunning  = false;

    public static String currentDiningHall = "Owens";

    private static final ObjectMapper         mapper            = new ObjectMapper();
    private static       List<FoodItem>       foodItems         = new ArrayList<>();
    private static       List<DiningHallHour> hallHourList      = new ArrayList<>();
    private static       List<String>         restaurantOptions = new ArrayList<>();
    private static       List<String>         diningHallOptions = new ArrayList<>();
    private        final RestClient           client            = new RestClient();

    private PageViewModel         pageViewModel;
    private FragmentMainBinding   binding;

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
        View                 root         = binding.getRoot();

        final ListView     listView          = binding.mobileList;
        final TextView     textView          = binding.diningHallLayout.operationHoursText;
        final TextView     hintText          = binding.diningHallLayout.hintText;
        final Spinner      diningHallSpinner = binding.diningHallLayout.diningHallOptionsSpinner;
        final Spinner      hoursSpinner      = binding.diningHallLayout.operationHoursSpinner;
        final Spinner      restaurantSpinner = binding.diningHallLayout.diningHallsSpinner;
        final RecyclerView recyclerView      = binding.diningHallLayout.idFoodItems;
        final LinearLayout linearLayout      = binding.diningHallLayout.diningHallLinearLayout;

        try {
            if (MainActivity.USE_REMOTE_DATA) {
                Optional<String> response = client.getFoodItems();
                if (response.isPresent()) {
                    // Update local data
                    foodItems = mapper.readValue(response.get(), new TypeReference<List<FoodItem>>() {});
                } else {
                    String menuJsonString = VTDiningScrapingUtils.loadJSONFromAsset(getContext(), MENU_JSON_FILE);
                    foodItems = mapper.readValue(menuJsonString, new TypeReference<List<FoodItem>>() {});
                }

                response = client.getHours();
                if (response.isPresent()) {
                    // Update local data
                    hallHourList = mapper.readValue(response.get(), new TypeReference<List<DiningHallHour>>() {});
                } else {
                    String menuJsonString = VTDiningScrapingUtils.loadJSONFromAsset(getContext(), HOUR_JSON_FILE);
                    hallHourList = mapper.readValue(menuJsonString, new TypeReference<List<FoodItem>>(){});
                }
            } else if (foodItems.size() == 0){
                String menuJsonString = VTDiningScrapingUtils.loadJSONFromAsset(getContext(), MENU_JSON_FILE);
                foodItems = mapper.readValue(menuJsonString, new TypeReference<List<FoodItem>>() {});

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
        List<Map<String, String>> menuData = new ArrayList<>();
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

        hintText.setText("Check the Menu of Your Favorite Food Shop at " + currentDiningHall);
        diningHallOptions = foodItems.stream()
                .filter(foodItem -> !StringUtils.equals(foodItem.getDiningHall(), "Dinning Hall"))
                .map(FoodItem::getDiningHall)
                .distinct()
                .collect(Collectors.toList());

        Optional<String> selectedDiningHall = diningHallOptions
                .stream()
                .filter(diningHall -> diningHall.contains(currentDiningHall))
                .findFirst();

        if (selectedDiningHall.isPresent()) {
            diningHallOptions.add(0, selectedDiningHall.get());
            diningHallOptions = diningHallOptions.stream().distinct().collect(Collectors.toList());
        }

        restaurantOptions = foodItems.stream()
                .filter(foodItem -> Objects.equals(foodItem.getDiningHall(), diningHallOptions.get(0)))
                .map(FoodItem::getOtherInfo)
                .distinct()
                .collect(Collectors.toList());

        Optional<DiningHallHour> optionalDiningHallHour =
                hallHourList.stream()
                        .filter(hour -> hasDiningHallHours(hour, diningHallOptions.get(0)))
                        .findFirst();

        optionalDiningHallHour.ifPresent(item -> textView.setText(item.getHours()));

        ArrayAdapter<String> restaurantAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, restaurantOptions);
        restaurantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> diningHallOptionsAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, diningHallOptions);
        diningHallOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter hourAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.operation_hours_spinner, R.layout.spinner_item);
        hoursSpinner.setAdapter(hourAdapter);

        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                // textView.setText(s);
                if (StringUtils.equals(s, "home")) {
                    System.out.println("tab 1" + s);
                    listView.setAdapter(simpleAdapter);
                    linearLayout.setVisibility(View.INVISIBLE);
                }
                else if (StringUtils.equals(s, "hour")) {
                    System.out.println("tab 2" + s);
                    listView.setAdapter(simpleAdapter2);
                    linearLayout.setVisibility(View.INVISIBLE);
                }
                else {
                    System.out.println("tab 3" + s);
                    linearLayout.setVisibility(View.VISIBLE);
                    restaurantSpinner.setAdapter(restaurantAdapter);
                    diningHallSpinner.setAdapter(diningHallOptionsAdapter);
                    diningHallSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selectedDiningHall = parent.getItemAtPosition(position).toString();
                                restaurantOptions = foodItems.stream()
                                        .filter(foodItem -> Objects.equals(foodItem.getDiningHall(), selectedDiningHall))
                                        .map(FoodItem::getOtherInfo)
                                        .distinct()
                                        .collect(Collectors.toList());
                                ArrayAdapter<String> updatedAdapter = new ArrayAdapter<>(
                                        getContext(),
                                        android.R.layout.simple_spinner_item,
                                        restaurantOptions
                                );

                                Optional<DiningHallHour> optionalDiningHallHour =
                                        hallHourList.stream()
                                                .filter(hour -> hasDiningHallHours(hour, selectedDiningHall))
                                                .findFirst();
                                optionalDiningHallHour.ifPresent(item -> textView.setText(item.getHours()));
                                updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                restaurantSpinner.setAdapter(updatedAdapter);
                                setFoodItemListAdapter(recyclerView, foodItems);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    restaurantSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedRestaurant = parent.getItemAtPosition(position).toString();
                            List<FoodItem> updatedOptions = foodItems.stream()
                                    .filter(foodItem -> Objects.equals(foodItem.getOtherInfo(), selectedRestaurant))
                                    .distinct()
                                    .collect(Collectors.toList());
                            setFoodItemListAdapter(recyclerView, updatedOptions);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    setFoodItemListAdapter(recyclerView, foodItems);
                }
            }
        });

        try {
            if (!schedulerRunning) {
                setUpSchedule();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setFoodItemListAdapter(RecyclerView recyclerView, List<FoodItem> foodItems) {
        FoodItemCardAdapter foodItemCardAdapter = new FoodItemCardAdapter(this.getContext(), foodItems);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(foodItemCardAdapter);
    }

    private boolean hasDiningHallHours(DiningHallHour hallHour, String selectedDiningHall) {
        String[] items = selectedDiningHall.split("at");
        if (items.length > 1) {
            return hallHour.getDiningHall().contains(items[1].replace("Hall", "").trim());
        }
        return hallHour.getDiningHall().contains(selectedDiningHall.trim());
    }

    public static void updateFoodItem(String foodItemName, FoodItemCardAdapter.FoodItemUpdateType type, int updatedValue) {
        switch (type) {
            case UPDATE_WAITING_LINE:
                foodItems.stream()
                        .filter(foodItem -> Objects.equals(foodItem.getName(), foodItemName))
                        .forEach(foodItem -> foodItem.setWaitingLine(updatedValue));
                break;
            case UPDATE_LIKE:
                foodItems.stream()
                        .filter(foodItem -> Objects.equals(foodItem.getName(), foodItemName))
                        .forEach(foodItem -> foodItem.setThumbUpCount(updatedValue));
                break;
            case UPDATE_DISLIKE:
                foodItems.stream()
                        .filter(foodItem -> Objects.equals(foodItem.getName(), foodItemName))
                        .forEach(foodItem -> foodItem.setThumbDownCount(updatedValue));
                break;
        }
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * The helper functionality to update waiting line, thumb up, thumb down information
     * @throws InterruptedException
     */
    private void setUpSchedule() throws InterruptedException {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

        Runnable task1 = () -> {
            // TODO: Add logic here
            RestClient client = new RestClient();
            try {
                System.out.println("Updating waiting line...");
                if (MainActivity.USE_REMOTE_DATA) {
                    client.updateAllFoodItemWaitingLine();
                }
                foodItems
                        .stream()
                        .filter(foodItem -> foodItem.getWaitingLine() > 0)
                        .forEach(
                                FoodItem -> {
                                    FoodItem.setWaitingLine(FoodItem.getWaitingLine() -1);
                                });
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }
        };

        // init Delay = 5, repeat the task every 1 minute
        ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(task1, 5, 60, TimeUnit.SECONDS);
    }
}