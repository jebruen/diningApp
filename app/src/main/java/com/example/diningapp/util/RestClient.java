package com.example.diningapp.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RestClient {

    private static final OkHttpClient client = new OkHttpClient();
    public static final String HOST                              = "http://10.0.2.2:8080/";
    public static final String BASE_MENU_API_URL                 =  HOST + "FoodItems/";
    public static final String BASE_HOUR_API_URL                 =  HOST + "DiningHallHours/";
    public static final String UPDATE_FOOD_ITEM_WAITING_LINE_URL =  HOST + "FoodItemsWaitingLine?foodName=%s&waitingLine=%s";
    public static final String UPDATE_FOOD_ITEM_THUMB_UP_URL     =  HOST + "FoodItemsThumbUp?foodName=%s&thumbUpCount=%s";
    public static final String UPDATE_FOOD_ITEM_THUMB_DOWN_URL   =  HOST + "FoodItemsThumbDown?foodName=%s&thumbDownCount=%s";
    public static final String UPDATE_FOOD_ITEM_LABEL_URL        =  HOST + "FoodItemsLabel?foodName=%s&label=%s";
    public static final String UPDATE_ALL_FOOD_ITEM_LABEL_URL    =  HOST + "AllFoodItemsWaitingLine";

    /**
     * FIXME: Should replace with AsycnTask?
     * @param url
     */
    public Optional<String> request(String url) throws InterruptedException, ExecutionException, TimeoutException {
        String json;
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<String> future = executor.submit(new Work(url));
        json = future.get(10, TimeUnit.SECONDS);
        return StringUtils.isNotBlank(json) ? Optional.of(json) : Optional.empty();
    }

    public Optional<String> getFoodItems () throws ExecutionException, InterruptedException, TimeoutException {
        return request(BASE_MENU_API_URL);
    }

    public Optional<String> getHours () throws ExecutionException, InterruptedException, TimeoutException {
        return request(BASE_HOUR_API_URL);
    }

    public Optional<String> updateFoodItemWaitingLine (String foodItemName, int updatedValue) throws ExecutionException, InterruptedException, TimeoutException {
        return request(String.format(UPDATE_FOOD_ITEM_WAITING_LINE_URL, foodItemName, updatedValue));
    }

    public Optional<String> updateAllFoodItemWaitingLine () throws ExecutionException, InterruptedException, TimeoutException {
        return request(UPDATE_ALL_FOOD_ITEM_LABEL_URL);
    }

    public Optional<String> updateFoodItemThumbUp(String foodItemName, int updatedValue) throws ExecutionException, InterruptedException, TimeoutException {
        return request(String.format(UPDATE_FOOD_ITEM_THUMB_UP_URL, foodItemName, updatedValue));
    }

    public Optional<String> updateFoodItemThumbDown (String foodItemName, int updatedValue) throws ExecutionException, InterruptedException, TimeoutException {
        return request(String.format(UPDATE_FOOD_ITEM_THUMB_DOWN_URL, foodItemName, updatedValue));
    }

    public Optional<String> updateFoodItemLabels (String foodItemName, String updatedValue) throws ExecutionException, InterruptedException, TimeoutException {
        return request(String.format(UPDATE_FOOD_ITEM_LABEL_URL, foodItemName, updatedValue));
    }


    class Work implements Callable<String> {
        private final String url;
        private  String result = "";

        public Work(String url) {
            this.url = url;
        }

        @Override
        public String call() {
            Request request = new Request.Builder()
                        .url(url)
                        .build();

            try (Response response = client.newCall(request).execute()) {
                result =  response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}