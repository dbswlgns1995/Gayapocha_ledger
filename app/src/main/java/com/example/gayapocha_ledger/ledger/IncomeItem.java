package com.example.gayapocha_ledger.ledger;

import com.example.gayapocha_ledger.R;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class IncomeItem extends RealmObject {

    private int id;
    private int type; // 0 cash, 1 card, 2 receivable
    private int date;
    private int packing; // 0 non packing, 1 packing

    private RealmList<IncomeList> incomeLists;

    public IncomeItem() {
    }

    public IncomeItem(int id, int type, int date, int packing, RealmList<IncomeList> incomeLists) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.packing = packing;
        this.incomeLists = incomeLists;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getPacking() {
        return packing;
    }

    public void setPacking(int packing) {
        this.packing = packing;
    }

    public RealmList<IncomeList> getIncomeLists() {
        return incomeLists;
    }

    public void setIncomeLists(RealmList<IncomeList> incomeLists) {
        this.incomeLists = incomeLists;
    }
}
