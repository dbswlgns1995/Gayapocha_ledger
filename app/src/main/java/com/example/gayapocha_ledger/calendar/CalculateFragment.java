package com.example.gayapocha_ledger.calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gayapocha_ledger.R;
import com.example.gayapocha_ledger.expend.ExpendItem;
import com.example.gayapocha_ledger.ledger.IncomeItem;
import com.example.gayapocha_ledger.ledger.IncomeList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmResults;


public class CalculateFragment extends Fragment {

    // title
    private static final String[] income_title_list = {"수구레국밥", "선지국밥", "소고기국밥", "공기밥", "계란추가", "수구레무침", "수구레볶음", "오삼불고기",
            "오돌뼈", "닭갈비", "돼지석쇠", "술국", "부추전", "소주", "맥주", "생탁", "음료수"
    };

    ArrayList mNewList;


    // cost
    private static final int[] income_cost_list = {7000, 6000, 6000, 1000, 2000, 20000, 20000, 15000, 15000, 15000, 15000, 6000, 6000, 4000, 4000, 3500, 1500};


    // 정산 fragment

    TextView result_text, date_text, income_text, expend_text;
    Realm realm;
    String expend, income;
    int sum;
    int date;
    String TAG = "***CalculateFragment";
    int expend_cost, income_cost;

    RecyclerView income_recyclerview, expend_recyclerview;
    CalculateExpendAdapter calculateExpendAdapter;
    ImageView alert_imageview;
    LinearLayout linearLayout;
    ConstraintLayout constraintLayout;

    Button income_btn;

    public CalculateFragment(int date) {
        this.date = date;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calculate, container, false);

        result_text = root.findViewById(R.id.calculate_result_text);
        date_text = root.findViewById(R.id.calculate_date_text);
        income_text = root.findViewById(R.id.calculate_income_text);
        expend_text = root.findViewById(R.id.calculate_expend_text);
        alert_imageview = root.findViewById(R.id.calculate_alert_imageview);
        linearLayout = root.findViewById(R.id.calculate_linear);
        constraintLayout = root.findViewById(R.id.calculate_back);
        income_btn = root.findViewById(R.id.calculate_income_btn);

        // 오늘 날짜 출력
        String date_str = Integer.toString(date);
        String date_text_str = "    " + date_str.substring(0, 4) + "년 " + date_str.substring(4, 6) + "월 " + date_str.substring(6) + "일";
        date_text.setText(date_text_str);

        mNewList = new ArrayList(Arrays.asList(income_title_list));

        income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DailyBillActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

        return root;
    }


    // 천 단위 콤마
    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

    // 수입/ 지출 / 합계 및 recycerview refresh 를 위한 onResume
    @Override
    public void onResume() {
        super.onResume();

        reset();
    }


    @Override
    public void onStop() {
        if (!realm.isClosed()) {
            realm.close();
        }
        super.onStop();
    }

    public void reset(){

        Log.d(TAG, "reset: ");

        Realm.init(getContext());
        realm = Realm.getDefaultInstance();
        RealmResults<IncomeItem> incomeItemRealmResults = realm.where(IncomeItem.class).equalTo("date", date).findAll();
        RealmResults<ExpendItem> expendItemRealmResults = realm.where(ExpendItem.class).notEqualTo("cost", 0).equalTo("date", date).findAll();


        // 당일 지출 recyclerview
        expend_recyclerview = getView().findViewById(R.id.calculate_expend_recyclerview);
        expend_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        calculateExpendAdapter = new CalculateExpendAdapter(expendItemRealmResults, true);
        expend_recyclerview.setAdapter(calculateExpendAdapter);
        expend_recyclerview.addOnItemTouchListener(mScrollTouchListener);
        expend_recyclerview.setNestedScrollingEnabled(false);

        if(expendItemRealmResults.size() == 0 && incomeItemRealmResults.size() == 0){ //&& incomeItemRealmResults.size() == 0
            alert_imageview.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);
        }else{
            alert_imageview.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);

        }

        sum = 0;
        expend = "";
        income = "";
        income_cost = 0;
        expend_cost = 0;

        for (ExpendItem i : expendItemRealmResults) {
            expend += i.getSub_title() + "      -" + toNumFormat(i.getCost()) + "\n";
            expend_cost += i.getCost();
        }

        RealmResults<IncomeItem> realmResults =  realm.where(IncomeItem.class).equalTo("date", date).findAll();
        for (IncomeItem k : realmResults){
            for(IncomeList j : k.getIncomeLists()){
                int index = mNewList.indexOf(j.getTitle());
                income_cost += (j.getCount() * income_cost_list[index]);
            }
        }

        sum = income_cost - expend_cost;


        // 수입/ 지출/ 합계 textview reset
        income_text.setText(toNumFormat(income_cost));
        expend_text.setText("-"+toNumFormat(expend_cost));
        result_text.setText(toNumFormat(sum));
    }

    // 내부 리사이클러뷰 잘 돌아가게
    RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            int action = e.getAction();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    rv.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    };
}
