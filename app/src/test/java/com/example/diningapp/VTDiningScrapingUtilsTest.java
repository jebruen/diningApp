package com.example.diningapp;

import static com.example.diningapp.util.VTDiningScrapingUtils.DINING_HOURS_HEADER;
import static com.example.diningapp.util.VTDiningScrapingUtils.DINING_MENU_DISH_HEADER;
import static com.example.diningapp.util.VTDiningScrapingUtils.printToCsvFile;
import static com.example.diningapp.util.VTDiningScrapingUtils.scrapingVTDiningHours;
import static com.example.diningapp.util.VTDiningScrapingUtils.scrapingVTDiningMenu;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.diningapp.util.DiningHallHour;
import com.example.diningapp.util.FoodItem;

public class VTDiningScrapingUtilsTest {

  @Test
  public void scrapingHoursWorks() throws IOException {
      List<DiningHallHour> hours = scrapingVTDiningHours();
      List<List<String>> records =
              hours.stream()
              .map(dinningHallHours ->
                      Arrays.asList(
                      dinningHallHours.getDiningHall(),
                      dinningHallHours.getDate(),
                      dinningHallHours.getHours()
              )).collect(Collectors.toList());

      Assert.assertTrue(records.size() > 0);
      printToCsvFile("hours.csv", DINING_HOURS_HEADER, records);
  }

    @Test
    public void scrapingMenusWorks() throws IOException {
        List<FoodItem> foodItemList = scrapingVTDiningMenu();
        List<List<String>> records =
                foodItemList.stream()
                        .map(foodItem ->
                                Arrays.asList(
                                        foodItem.getName(),
                                        foodItem.getLabel(),
                                        foodItem.getDescription(),
                                        foodItem.getAmount(),
                                        foodItem.getType(),
                                        foodItem.getOtherInfo(), // Card header indicate whether it is breakfast or lunch
                                        foodItem.getDiningHall()
                                )).collect(Collectors.toList());

        Assert.assertTrue(records.size() > 0);
        printToCsvFile("menu.csv", DINING_MENU_DISH_HEADER, records);
    }
}
