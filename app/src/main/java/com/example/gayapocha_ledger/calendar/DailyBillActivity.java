package com.example.gayapocha_ledger.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gayapocha_ledger.R;
import com.example.gayapocha_ledger.ledger.IncomeItem;
import com.example.gayapocha_ledger.ledger.IncomeList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DailyBillActivity extends AppCompatActivity {

    ArrayList mNewList;

    // 갯수 counttext
    private List<TextView> count_text_list;
    private static final int[] count_text_id = {
            (R.id.count_a1_text), (R.id.count_a2_text), (R.id.count_a3_text), (R.id.count_a4_text), (R.id.count_a5_text),
            (R.id.count_b1_text), (R.id.count_b2_text), (R.id.count_b3_text), (R.id.count_b4_text), (R.id.count_b5_text), (R.id.count_b6_text), (R.id.count_b7_text), (R.id.count_b8_text),
            (R.id.count_c1_text), (R.id.count_c2_text), (R.id.count_c3_text), (R.id.count_c4_text)
    };

    // 갯수 cost_text
    private List<TextView> cost_text_list;
    private static final int[] cost_text_id = {
            (R.id.cost_a1_text), (R.id.cost_a2_text), (R.id.cost_a3_text), (R.id.cost_a4_text), (R.id.cost_a5_text),
            (R.id.cost_b1_text), (R.id.cost_b2_text), (R.id.cost_b3_text), (R.id.cost_b4_text), (R.id.cost_b5_text), (R.id.cost_b6_text), (R.id.cost_b7_text), (R.id.cost_b8_text),
            (R.id.cost_c1_text), (R.id.cost_c2_text), (R.id.cost_c3_text), (R.id.cost_c4_text)
    };

    private int[] daily_count;

    int date;

    Realm realm;

    // title
    private static final String[] income_title_list = {"수구레국밥", "선지국밥", "소고기국밥", "공기밥", "계란추가", "수구레무침", "수구레볶음", "오삼불고기",
            "오돌뼈", "닭갈비", "돼지석쇠", "술국", "부추전", "소주", "맥주", "생탁", "음료수"
    };

    // cost
    private static final int[] income_cost = {8000, 7000, 7000, 1000, 2000, 25000, 25000, 15000, 15000, 15000, 15000, 6000, 6000, 4000, 4000, 3500, 1500};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_bill);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        mNewList = new ArrayList(Arrays.asList(income_title_list));

        init(this);

        Intent intent = getIntent();
        date = intent.getIntExtra("date", 0);

        RealmResults<IncomeItem> realmResults =  realm.where(IncomeItem.class).equalTo("date", date).findAll();
        for (IncomeItem k : realmResults){
            for(IncomeList j : k.getIncomeLists()){
                int index = mNewList.indexOf(j.getTitle());
                daily_count[index] += j.getCount();
            }
        }

        for(int i=0; i< count_text_id.length; i++){
            count_text_list.get(i).setText(toNumFormat(daily_count[i]));
            cost_text_list.get(i).setText(toNumFormat(daily_count[i] * income_cost[i]));
        }



    }


    public void init(Context context){

        count_text_list = new ArrayList<>();
        cost_text_list = new ArrayList<>();
        daily_count = new int[17];

        for (int i = 0; i < count_text_id.length; i++) {

            daily_count[i] = 0;

            TextView count_text = (TextView) ((Activity)context).findViewById(count_text_id[i]);
            count_text_list.add(count_text);

            TextView cost_text = (TextView) ((Activity)context).findViewById(cost_text_id[i]);
            cost_text_list.add(cost_text);


        }
    }

    // 천 단위 콤마
    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }
}