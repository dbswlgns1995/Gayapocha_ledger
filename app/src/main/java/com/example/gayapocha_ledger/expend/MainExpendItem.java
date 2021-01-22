package com.example.gayapocha_ledger.expend;

import io.realm.RealmObject;

public class MainExpendItem extends RealmObject {
    private int id;
    private String title;

    public MainExpendItem() {
    }

    public MainExpendItem(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
