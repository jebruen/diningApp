package com.example.diningapp;

import static org.junit.Assert.assertEquals;

import com.example.diningapp.util.FoodItem;
import com.example.diningapp.util.RestClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() throws ExecutionException, InterruptedException, TimeoutException {
        RestClient restClient = new RestClient();
        restClient.request("http://localhost:8080/FoodItems").ifPresent(result -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<FoodItem> foodItemList =  mapper.readValue(result, new TypeReference<List<FoodItem>>() {});
                Assert.assertTrue(foodItemList.size() > 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}