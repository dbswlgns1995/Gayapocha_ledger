package com.example.gayapocha_ledger.expend;

import io.realm.RealmObject;

public class SubExpendItem extends RealmObject {
    private int id;
    private String main_title, sub_title;

    public SubExpendItem() {
    }

    public SubExpendItem(int id, String main_title, String sub_title) {
        this.id = id;
        this.main_title = main_title;
        this.sub_title = sub_title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
