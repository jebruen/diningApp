package com.example.diningapp.util;

public class FoodItem {
    private String name;
    private String label;
    private String description;
    private String amount;
    private String type;
    private String diningHall;
    private String otherInfo;

    public FoodItem(String name, String label, String description, String amount, String type, String diningHall, String otherInfo) {
        this.name = name;
        this.label = label;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.diningHall = diningHall;
        this.otherInfo = otherInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiningHall() {
        return diningHall;
    }

    public void setDiningHall(String diningHall) {
        this.diningHall = diningHall;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public static final class FoodItemBuilder {
        private String name;
        private String label;
        private String description;
        private String amount;
        private String type;
        private String diningHall;
        private String otherInfo;

        private FoodItemBuilder() {
        }

        public static FoodItemBuilder aFoodItem() {
            return new FoodItemBuilder();
        }

        public FoodItemBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FoodItemBuilder label(String label) {
            this.label = label;
            return this;
        }

        public FoodItemBuilder description(String description) {
            this.description = description;
            return this;
        }

        public FoodItemBuilder amount(String amount) {
            this.amount = amount;
            return this;
        }

        public FoodItemBuilder type(String type) {
            this.type = type;
            return this;
        }

        public FoodItemBuilder diningHall(String diningHall) {
            this.diningHall = diningHall;
            return this;
        }

        public FoodItemBuilder otherInfo(String otherInfo) {
            this.otherInfo = otherInfo;
            return this;
        }

        public FoodItem build() {
            return new FoodItem(name, label, description, amount, type, diningHall, otherInfo);
        }
    }
}
