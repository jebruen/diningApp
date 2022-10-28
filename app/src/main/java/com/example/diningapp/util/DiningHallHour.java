package com.example.diningapp.util;

public class DiningHallHour {
    private String hours;
    private String diningHall;
    private String date;

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getDiningHall() {
        return diningHall;
    }

    public void setDiningHall(String diningHall) {
        this.diningHall = diningHall;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static final class DiningHallHourBuilder {
        private String hours;
        private String diningHall;
        private String date;

        private DiningHallHourBuilder() {
        }

        public static DiningHallHourBuilder aDiningHallHour() {
            return new DiningHallHourBuilder();
        }

        public DiningHallHourBuilder hours(String hours) {
            this.hours = hours;
            return this;
        }

        public DiningHallHourBuilder diningHall(String diningHall) {
            this.diningHall = diningHall;
            return this;
        }

        public DiningHallHourBuilder date(String date) {
            this.date = date;
            return this;
        }

        public DiningHallHour build() {
            DiningHallHour diningHallHour = new DiningHallHour();
            diningHallHour.date = this.date;
            diningHallHour.diningHall = this.diningHall;
            diningHallHour.hours = this.hours;
            return diningHallHour;
        }
    }
}
