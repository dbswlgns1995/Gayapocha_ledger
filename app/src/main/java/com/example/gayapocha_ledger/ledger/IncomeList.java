package com.example.gayapocha_ledger.ledger;

import io.realm.RealmObject;

public class IncomeList extends RealmObject {
    private String title;
    private int count, id;

    public IncomeList() {
    }

    public IncomeList(String title, int count, int id) {
        this.title = title;
        this.count = count;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
