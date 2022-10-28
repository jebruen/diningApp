package com.example.diningapp.util;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

import com.codeborne.selenide.Configuration;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The util class providing vt dinning website scraping functionality
 */
public class VTDiningScrapingUtils {

    public static final String[] DINING_HOURS_HEADER = { "Dining Hall", "Date", "Hours"};
    public static final String[] DINING_MENU_DISH_HEADER = { "Food Item Name", "Label", "Description", "Amount", "Type", "Other Info", "Dinning Hall"};
    public static final String VT_MENU_URL = "https://foodpro.dsa.vt.edu/menus/";
    public static final String VT_HOUR_URL = "https://saapps.students.vt.edu/hours/";

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
    public static List<DiningHallHour> scrapingVTDiningHours() throws IOException {
        Configuration.headless = true;
        open(VT_HOUR_URL);
        $(By.tagName("h1")).shouldHave(text("Dining Center Operation Hours"));

        // Wait until specific content loaded
        $(By.id("units_open_on_day_accordion")).shouldBe(visible, Duration.ofSeconds(30));

        Document doc = Jsoup.parse(getWebDriver().getPageSource());
        List<Element> cards = doc.getElementsByClass("card");
        List<DiningHallHour> records = new ArrayList<>();
        for (Element card : cards) {
            DiningHallHour diningHallHour =
                    DiningHallHour.DiningHallHourBuilder
                            .aDiningHallHour()
                            .hours(
                                    card.getElementsByClass("list-group-item").size() > 0
                                            ? card.getElementsByClass("list-group-item").get(0).text()
                                            : "")
                            .date(LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE))
                            .diningHall(card.getElementsByTag("h5").size() > 0
                                    ? card.getElementsByTag("h5").get(0).text()
                                    : "").build();

                records.add(diningHallHour);
        }

            // printToCsvFile("hours.csv", DINING_HOURS_HEADER, Collections.singletonList(records));
        return records;
    }

    public static void printToCsvFile(String fileName, String[] header, List<List<String>> records) throws IOException{
        FileWriter out = new FileWriter(fileName, true);
        CSVFormat csvFormat = header.length > 0 ? CSVFormat.DEFAULT.withHeader(DINING_MENU_DISH_HEADER) : CSVFormat.DEFAULT;
        try (CSVPrinter printer = new CSVPrinter(out, csvFormat)) {
            for (List<String> record : records) {
                printer.printRecord(record);
            }
        }
    }

}
