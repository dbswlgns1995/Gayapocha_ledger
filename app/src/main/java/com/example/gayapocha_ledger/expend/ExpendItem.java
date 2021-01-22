package com.example.gayapocha_ledger.expend;

import io.realm.RealmObject;

public class ExpendItem extends RealmObject {
    private String main_title, sub_title;
    private int cost, date;

    public ExpendItem() {
    }

    public ExpendItem(String main_title, String sub_title, int cost, int date) {
        this.main_title = main_title;
        this.sub_title = sub_title;
        this.cost = cost;
        this.date = date;
    }

    public String getMain_title() {
        return main_title;
    }

    public void setMain_title(String main_title) {
        this.main_title = main_title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
