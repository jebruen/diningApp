package com.example.diningapp;

//import static com.example.diningapp.util.VTDiningScrapingUtils.DINING_HOURS_HEADER;
//import static com.example.diningapp.util.VTDiningScrapingUtils.DINING_MENU_DISH_HEADER;
//import static com.example.diningapp.util.VTDiningScrapingUtils.VT_HOUR_URL;
//import static com.example.diningapp.util.VTDiningScrapingUtils.printToCsvFile;
//import static com.example.diningapp.util.VTDiningScrapingUtils.scrapingVTDiningHours;
//import static com.example.diningapp.util.VTDiningScrapingUtils.scrapingVTDiningMenu;

import static com.example.diningapp.util.VTDiningScrapingUtils.printToJsonFile;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.example.diningapp.util.DiningHallHour;
import com.example.diningapp.util.FoodItem;

import io.github.bonigarcia.wdm.WebDriverManager;
//import com.gargoylesoftware.htmlunit.BrowserVersion;
//import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
//import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;

public class VTDiningScrapingUtilsTest {

//  @Test
//  public void scrapingHoursWorks() throws IOException, InterruptedException {
//      List<DiningHallHour> hours = scrapingVTDiningHours();
//      List<List<String>> records =
//              hours.stream()
//              .map(dinningHallHours ->
//                      Arrays.asList(
//                      dinningHallHours.getDiningHall(),
//                      dinningHallHours.getDate(),
//                      dinningHallHours.getHours()
//              )).collect(Collectors.toList());
//
//      Assert.assertTrue(records.size() > 0);
//      printToCsvFile("hours.csv", DINING_HOURS_HEADER, records);
//  }

//    @Test
//    public void scrapingMenusWorks() throws IOException {
//        List<FoodItem> foodItemList = scrapingVTDiningMenu();
//        List<List<String>> records =
//                foodItemList.stream()
//                        .map(foodItem ->
//                                Arrays.asList(
//                                        foodItem.getName(),
//                                        foodItem.getLabel(),
//                                        foodItem.getDescription(),
//                                        foodItem.getAmount(),
//                                        foodItem.getType(),
//                                        foodItem.getOtherInfo(), // Card header indicate whether it is breakfast or lunch
//                                        foodItem.getDiningHall()
//                                )).collect(Collectors.toList());
//
//        Assert.assertTrue(records.size() > 0);
//        printToCsvFile("menu.csv", DINING_MENU_DISH_HEADER, records);
//    }

    @Test
    public void test2() throws IOException, InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://saapps.students.vt.edu/hours/");
        WebElement myDynamicElement = driver.findElement(By.className("unitsOpenOnDay"));
        Document doc = Jsoup.parse(driver.getPageSource());
        System.out.println(doc.toString());
        driver.quit();
    }

    @Test
    public void test3() throws IOException, InterruptedException {
        printToJsonFile("menu");
    }
}

