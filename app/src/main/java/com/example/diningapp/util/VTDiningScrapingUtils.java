package com.example.diningapp.util;

import android.os.Build;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The util class providing vt dinning website scraping functionality
 */
public class VTDiningScrapingUtils {

    public static final String[] DINING_HOURS_HEADER     = { "Dining Hall", "Date", "Hours"};
    public static final String[] DINING_MENU_DISH_HEADER = { "Food Item Name", "Label", "Description", "Amount", "Type", "Other Info", "Dinning Hall"};
    public static final String VT_MENU_URL               = "https://foodpro.dsa.vt.edu/menus/";
    public static final String VT_HOUR_URL               = "https://saapps.students.vt.edu/hours/";

    /**
     * Connect to VT menu website and parsed important information
     * @return A list of food item display in the website
     * @throws IOException
     */
    public static List<FoodItem> scrapingVTDiningMenu() throws IOException {
        Document doc = Jsoup.connect(VT_MENU_URL).get();
        List<Element> diningHallList = doc.getElementsByClass("dining_menu_button");

        List<FoodItem> records = new ArrayList<>();
        for (Element diningHall : diningHallList) {
            // Getting the specific dining hall link for the menu of that dining hall
            String link = diningHall.getElementsByTag("a").attr("href");
            Document diningHallMenus = Jsoup.connect(VT_MENU_URL + link).get();

            List<Element> cardList = diningHallMenus.getElementsByClass("card");
            for (Element card : cardList) {
                List<Element> recipeContainerList = card.getElementsByClass("recipe_container");
                String cardHeader =
                        card.getElementsByClass("card-header").size() > 0
                                ? card.getElementsByClass("card-header").get(0).text()
                                : "";

                Element pane = card.parent();
                Element tab = diningHallMenus.getElementById(pane.attr("aria-labelledby"));

                for (Element element1 : recipeContainerList) {
                    FoodItem foodItem = FoodItem.FoodItemBuilder.aFoodItem()
                            .name(element1.getElementsByClass("recipe_title").size() > 0
                                    ? element1.getElementsByClass("recipe_title").get(0).text()
                                    : "")
                            .label(element1.getElementsByClass("legend_icon").size() > 0
                                    ? element1.getElementsByClass("legend_icon").get(0).text()
                                    : "")
                            .description(element1.getElementsByClass("recipe_description").size() > 0
                                    ? element1.getElementsByClass("recipe_description").get(0).text()
                                    : "")
                            .amount(element1.getElementsByClass("portion_size").size() > 0
                                    ? element1.getElementsByClass("portion_size").get(0).text()
                                    : "")
                            .type(tab.text())
                            .diningHall(diningHall.text())
                            .otherInfo(cardHeader)
                            .build();

                    records.add(foodItem);
                }
            }

        }

        return records;
    }

    /**
     * Connect to VT menu website and parsed important information
     * @return A list of hour object display in the website
     * @throws IOException
     */
    public static List<DiningHallHour> scrapingVTDiningHours() throws IOException, InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        driver.get(VT_HOUR_URL);
        // Wait up to 5 secs so page can be fully loaded
        driver.wait(5000);
        Document doc = Jsoup.parse(driver.getPageSource());
        driver.quit();
        List<Element> cards = doc.getElementsByClass("card");
        List<DiningHallHour> records = new ArrayList<>();
        for (Element card : cards) {
            DiningHallHour diningHallHour = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                diningHallHour = DiningHallHour.DiningHallHourBuilder
                        .aDiningHallHour()
                        .hours(
                                card.getElementsByClass("list-group-item").size() > 0
                                        ? card.getElementsByClass("list-group-item").get(0).text()
                                        : "")
                        .date(LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE))
                        .diningHall(card.getElementsByTag("h5").size() > 0
                                ? card.getElementsByTag("h5").get(0).text()
                                : "").build();
            }

            records.add(diningHallHour);
        }
        return records;
    }

    public static void printToCsvFile(String fileName, String[] header, List<List<String>> records) throws IOException{
        FileWriter out = new FileWriter(fileName + ".csv", true);
        CSVFormat csvFormat = header.length > 0 ? CSVFormat.DEFAULT.withHeader(DINING_MENU_DISH_HEADER) : CSVFormat.DEFAULT;
        try (CSVPrinter printer = new CSVPrinter(out, csvFormat)) {
            for (List<String> record : records) {
                printer.printRecord(record);
            }
        }
    }

    public static void printToJsonFile(String type) throws IOException{
        if (StringUtils.equals(type, "hours")) {
            List<DiningHallHour> list = readingHourFromCsvFile("hours", DINING_HOURS_HEADER);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("hours.json"), list);
        }
       else if (StringUtils.equals(type, "menu")) {
            List<FoodItem> list = readingMenuFromCsvFile("menu", DINING_MENU_DISH_HEADER);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("menu.json"), list);
        }
    }

    public static List<DiningHallHour> readingHourFromCsvFile (String fileName, String[] header) throws IOException {
        Reader in = new FileReader(fileName + ".csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(header).parse(in);
        List<DiningHallHour> list = new ArrayList<>();
        for (CSVRecord record : records) {
            list.add(
                DiningHallHour.DiningHallHourBuilder.aDiningHallHour()
                        .date( record.get("Date"))
                        .diningHall(record.get("Dining Hall"))
                        .hours( record.get("Hours"))
                        .build()
            );
        }

        return list;
    }

    public static List<FoodItem> readingMenuFromCsvFile(String fileName, String[] header) throws IOException {
        Reader in = new FileReader(fileName + ".csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(header).parse(in);
        List<FoodItem> list = new ArrayList<>();
        for (CSVRecord record : records) {
            list.add(
                    FoodItem.FoodItemBuilder.aFoodItem()
                            .name(record.get("Food Item Name"))
                            .diningHall(record.get("Dinning Hall"))
                            .type(record.get("Type"))
                            .amount(record.get("Amount"))
                            .description(record.get("Description"))
                            .label(record.get("Label"))
                            .otherInfo(record.get("Other Info"))
                            .build()
            );
        }

        return list;
    }
}
