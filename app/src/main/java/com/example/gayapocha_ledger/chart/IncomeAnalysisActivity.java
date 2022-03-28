package com.example.gayapocha_ledger.chart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.gayapocha_ledger.R;
import com.example.gayapocha_ledger.bill.BillActivity;
import com.example.gayapocha_ledger.calendar.CalendarActivity;
import com.example.gayapocha_ledger.expend.ExpendActivity;
import com.example.gayapocha_ledger.ledger.IncomeItem;
import com.example.gayapocha_ledger.ledger.IncomeList;
import com.example.gayapocha_ledger.ledger.MainActivity;
import com.example.gayapocha_ledger.ledger.ViewPagerAdapter;
import com.example.gayapocha_ledger.util.NewBill;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class IncomeAnalysisActivity extends AppCompatActivity {

    // 수입분석

    private String TAG = "IncomeAnalysisActivity";

    Realm realm;
    TextView a1_text, a2_text, a3_text, a4_text, a5_text, a6_text, a7_text, a8_text;
    MultiStateToggleButton date_btn;

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    TextView title_text;

    int tday, tmonth, tyear;
    int mday, mmonth, myear;
    String date_str;

    // title
    private static final String[] income_title_list = {"수구레국밥", "선지국밥", "소고기국밥", "공기밥", "계란추가", "수구레무침", "수구레볶음", "오삼불고기",
            "오돌뼈", "닭갈비", "돼지석쇠", "술국", "부추전", "소주", "맥주", "생탁", "음료수"
    };

    ArrayList mNewList;


    // cost
    private static final int[] income_cost_list = {7000, 6000, 6000, 1000, 2000, 20000, 20000, 15000, 15000, 15000, 15000, 6000, 6000, 4000, 4000, 3500, 1500};


    // 수입 분석

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_analysis);

        // today date
        Calendar cal = Calendar.getInstance();
        tyear = cal.get(cal.YEAR);
        tmonth = cal.get(cal.MONTH) + 1;
        tday = cal.get(cal.DATE);
        date_str = tyear + "" + ((tmonth) < 10 ? "0" + tmonth : "" + tmonth) + "" + ((tday) < 10 ? "0" + tday : "" + tday);

        mday = tday;
        mmonth = tmonth;
        myear = tyear;

        Log.d(TAG, date_str);

        init();

        a1_text = (TextView) findViewById(R.id.ia_a1_text);
        a2_text = (TextView)findViewById(R.id.ia_a2_text);
        a3_text = (TextView)findViewById(R.id.ia_a3_text);
        a4_text = (TextView)findViewById(R.id.ia_a4_text);
        a5_text = (TextView)findViewById(R.id.ia_a5_text);
        a6_text = (TextView) findViewById(R.id.ia_a6_text);
        a7_text = (TextView)findViewById(R.id.ia_a7_text);
        a8_text = (TextView)findViewById(R.id.ia_a8_text);



        date_btn = findViewById(R.id.income_a_dmy_btn);
        date_btn.setValue(0);
        date_btn.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                switch (value) {
                    case 0: //day picker
                        DatePickerDialog dialogFragment = new DatePickerDialog(IncomeAnalysisActivity.this, dateListener, tyear, tmonth - 1, tday);
                        dialogFragment.show();
                        break;

                    case 1: // month picker
                        final Calendar today = Calendar.getInstance();
                        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(IncomeAnalysisActivity.this, new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // month 를 선택했을때
                                Log.d(TAG, selectedYear + "년 " + (selectedMonth + 1) + "월 선택");
                                mmonth = selectedMonth + 1;
                                myear = selectedYear;
                                setting(0, mmonth, myear);

                            }
                        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

                        builder.setActivatedMonth(tmonth - 1)
                                .setMinYear(2000)
                                .setActivatedYear(tyear)
                                .setMaxYear(2040)
                                .setMinMonth(Calendar.JANUARY)
                                .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                                .build()
                                .show();

                        break;

                    case 2: // year picker

                        MonthPickerDialog.Builder year_builder = new MonthPickerDialog.Builder(IncomeAnalysisActivity.this, new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // year 를 선택했을때
                                Log.d(TAG, selectedYear + "년 선택");
                                myear = selectedYear;
                                setting(0, 0, myear);

                            }
                        }, myear, 0);

                        year_builder.showYearOnly()
                                .setYearRange(2000, 2040)
                                .build()
                                .show();
                        break;


                }
            }
        });

        setting(tday, tmonth, tyear);





    }

    public void setting(int day, int month, int year) {

        int date, date_from, date_to;

        int[] sum_cost = {0, 0, 0, 0, 0, 0, 0, 0};

        if (day == 0 && month!=0) { // 월간
            date_from = Integer.parseInt(year + "" + ((month) < 10 ? "0" + month : "" + month) + "01");
            date_to = Integer.parseInt(year + "" + ((month) < 10 ? "0" + month : "" + month) + "31");

            title_text.setText(year + "년 " + month + "월 ");

            // 현금식사
            RealmResults<IncomeItem> realmResults1 = realm.where(IncomeItem.class).equalTo("type", 0).equalTo("packing", 0).between("date", date_from, date_to).findAll();
            for (IncomeItem i : realmResults1) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());

                    if (i.getDate() >= 20220328){
                        sum_cost[1] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[1] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 카드식사
            RealmResults<IncomeItem> realmResults2 = realm.where(IncomeItem.class).equalTo("type", 1).equalTo("packing", 0).between("date", date_from, date_to).findAll();
            for (IncomeItem i : realmResults2) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[2] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[2] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 미수금식사
            RealmResults<IncomeItem> realmResults3 = realm.where(IncomeItem.class).equalTo("type", 2).equalTo("packing", 0).between("date", date_from, date_to).findAll();
            for (IncomeItem i : realmResults3) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[3] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[3] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 현금식사
            RealmResults<IncomeItem> realmResults4 = realm.where(IncomeItem.class).equalTo("type", 0).equalTo("packing", 1).between("date", date_from, date_to).findAll();
            for (IncomeItem i : realmResults4) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[5] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[5] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 카드식사
            RealmResults<IncomeItem> realmResults5 = realm.where(IncomeItem.class).equalTo("type", 1).equalTo("packing", 1).between("date", date_from, date_to).findAll();
            for (IncomeItem i : realmResults5) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[6] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[6] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 미수금식사
            RealmResults<IncomeItem> realmResults6 = realm.where(IncomeItem.class).equalTo("type", 2).equalTo("packing", 1).between("date", date_from, date_to).findAll();
            for (IncomeItem i : realmResults6) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[7] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[7] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            sum_cost[0] = sum_cost[1] + sum_cost[2] + sum_cost[3];
            sum_cost[4] = sum_cost[5] + sum_cost[6] + sum_cost[7];

            Log.d(TAG, sum_cost[0] + " " + sum_cost[4]);

            a1_text.setText(toNumFormat(sum_cost[0]));
            a2_text.setText(toNumFormat(sum_cost[1]));
            a3_text.setText(toNumFormat(sum_cost[2]));
            a4_text.setText(toNumFormat(sum_cost[3]));
            a5_text.setText(toNumFormat(sum_cost[4]));
            a6_text.setText(toNumFormat(sum_cost[5]));
            a7_text.setText(toNumFormat(sum_cost[6]));
            a8_text.setText(toNumFormat(sum_cost[7]));


        } else if (day == 0 && month == 0) {  // 년간
            date_from = Integer.parseInt(year + "0101");
            date_to = Integer.parseInt(year + "1231");

            title_text.setText(year + "년");

            // 현금식사
            RealmResults<IncomeItem> realmResults1 = realm.where(IncomeItem.class).equalTo("type", 0).equalTo("packing", 0).between("date", date_from, date_to).findAll();
            for (IncomeItem i : realmResults1) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[1] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[1] += (j.getCount() * income_cost_list[index]);
                    }

                }
            }

            // 카드식사
            RealmResults<IncomeItem> realmResults2 = realm.where(IncomeItem.class).equalTo("type", 1).equalTo("packing", 0).between("date", date_from, date_to).findAll();
            for (IncomeItem i : realmResults2) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[2] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[2] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 미수금식사
            RealmResults<IncomeItem> realmResults3 = realm.where(IncomeItem.class).equalTo("type", 2).equalTo("packing", 0).between("date", date_from, date_to).findAll();
            for (IncomeItem i : realmResults3) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[3] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[3] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 현금식사
            RealmResults<IncomeItem> realmResults4 = realm.where(IncomeItem.class).equalTo("type", 0).equalTo("packing", 1).between("date", date_from, date_to).findAll();
            for (IncomeItem i : realmResults4) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[5] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[5] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 카드식사
            RealmResults<IncomeItem> realmResults5 = realm.where(IncomeItem.class).equalTo("type", 1).equalTo("packing", 1).between("date", date_from, date_to).findAll();
            for (IncomeItem i : realmResults5) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[6] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[6] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 미수금식사
            RealmResults<IncomeItem> realmResults6 = realm.where(IncomeItem.class).equalTo("type", 2).equalTo("packing", 1).between("date", date_from, date_to).findAll();
            for (IncomeItem i : realmResults6) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[7] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[7] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            sum_cost[0] = sum_cost[1] + sum_cost[2] + sum_cost[3];
            sum_cost[4] = sum_cost[5] + sum_cost[6] + sum_cost[7];

            Log.d(TAG, sum_cost[0] + " " + sum_cost[4]);

            a1_text.setText(toNumFormat(sum_cost[0]));
            a2_text.setText(toNumFormat(sum_cost[1]));
            a3_text.setText(toNumFormat(sum_cost[2]));
            a4_text.setText(toNumFormat(sum_cost[3]));
            a5_text.setText(toNumFormat(sum_cost[4]));
            a6_text.setText(toNumFormat(sum_cost[5]));
            a7_text.setText(toNumFormat(sum_cost[6]));
            a8_text.setText(toNumFormat(sum_cost[7]));


        } else { // 일간
            date = Integer.parseInt(year + "" + ((month) < 10 ? "0" + month : "" + month) + "" + ((day) < 10 ? "0" + day : "" + day));

            title_text.setText(year + "년 " + month + "월 " + day + "일");

            // 현금식사
            RealmResults<IncomeItem> realmResults1 = realm.where(IncomeItem.class).equalTo("type", 0).equalTo("packing", 0).equalTo("date", date).findAll();
            for (IncomeItem i : realmResults1) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[1] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[1] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 카드식사
            RealmResults<IncomeItem> realmResults2 = realm.where(IncomeItem.class).equalTo("type", 1).equalTo("packing", 0).equalTo("date", date).findAll();
            for (IncomeItem i : realmResults2) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[2] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[2] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 미수금식사
            RealmResults<IncomeItem> realmResults3 = realm.where(IncomeItem.class).equalTo("type", 2).equalTo("packing", 0).equalTo("date", date).findAll();
            for (IncomeItem i : realmResults3) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[3] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[3] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 현금식사
            RealmResults<IncomeItem> realmResults4 = realm.where(IncomeItem.class).equalTo("type", 0).equalTo("packing", 1).equalTo("date", date).findAll();
            for (IncomeItem i : realmResults4) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[5] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[5] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 카드식사
            RealmResults<IncomeItem> realmResults5 = realm.where(IncomeItem.class).equalTo("type", 1).equalTo("packing", 1).equalTo("date", date).findAll();
            for (IncomeItem i : realmResults5) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[6] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[6] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            // 미수금식사
            RealmResults<IncomeItem> realmResults6 = realm.where(IncomeItem.class).equalTo("type", 2).equalTo("packing", 1).equalTo("date", date).findAll();
            for (IncomeItem i : realmResults6) {
                for (IncomeList j : i.getIncomeLists()) {
                    int index = mNewList.indexOf(j.getTitle());
                    if (i.getDate() >= 20220328){
                        sum_cost[7] += (j.getCount() * NewBill.new_income_list[index]);
                    } else {
                        sum_cost[7] += (j.getCount() * income_cost_list[index]);
                    }
                }
            }

            sum_cost[0] = sum_cost[1] + sum_cost[2] + sum_cost[3];
            sum_cost[4] = sum_cost[5] + sum_cost[6] + sum_cost[7];

            Log.d(TAG, sum_cost[0] + " " + sum_cost[4]);

            a1_text.setText(toNumFormat(sum_cost[0]));
            a2_text.setText(toNumFormat(sum_cost[1]));
            a3_text.setText(toNumFormat(sum_cost[2]));
            a4_text.setText(toNumFormat(sum_cost[3]));
            a5_text.setText(toNumFormat(sum_cost[4]));
            a6_text.setText(toNumFormat(sum_cost[5]));
            a7_text.setText(toNumFormat(sum_cost[6]));
            a8_text.setText(toNumFormat(sum_cost[7]));



        }

    }

    // 천 단위 콤마
    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        if (num==0){
            return "0";
        }
        return df.format(num);
    }

    public void init() {

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        mNewList = new ArrayList(Arrays.asList(income_title_list));

        // navview drawer setting
        mDrawerLayout = (DrawerLayout) findViewById(R.id.income_a_drawer_layout);
        navigationView = findViewById(R.id.income_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();
                String title = item.getTitle().toString();

                if(id == R.id.item1){ // 수입
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                else if(id == R.id.item2){ // 영수증
                    startActivity(new Intent(getApplicationContext(), BillActivity.class));
                    finish();
                }
                else if(id == R.id.item3){ // 지출
                    startActivity(new Intent(getApplicationContext(), ExpendActivity.class));
                    finish();
                }else if(id == R.id.item4){ // 달력
                    startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                    finish();
                }else if(id == R.id.item5){ // 수입 분석
                    //startActivity(new Intent(getApplicationContext(), IncomeAnalysisActivity.class));
                    //finish();
                }else if(id == R.id.item6){ // 소득 분석
                    startActivity(new Intent(getApplicationContext(), GiAnalysisActivity.class));
                    finish();
                }else if(id == R.id.item7){ // 품목별 분석
                    startActivity(new Intent(getApplicationContext(), ItemAnalysisActivity.class));
                    finish();
                }else if(id == R.id.item8){ // 국 분석
                    startActivity(new Intent(getApplicationContext(), MealsActivity.class));
                    finish();
                }else if(id == R.id.item9){ // 안주 분석
                    startActivity(new Intent(getApplicationContext(), SnacksActivity.class));
                    finish();
                }else if(id == R.id.item10){ // 주류 분석
                    startActivity(new Intent(getApplicationContext(), BeveragesActivity.class));
                    finish();
                }

                return true;
            }
        });


        // 액션바 설정
        toolbar = (Toolbar) findViewById(R.id.income_a_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu); // 메뉴 아이콘

        title_text = findViewById(R.id.income_a_toolbar_title);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    // 일간 차트
    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Log.d(TAG, year + "" + (monthOfYear + 1) + "" + dayOfMonth + "선택");
            myear = year;
            mmonth = monthOfYear + 1;
            mday = dayOfMonth;
            setting(mday, mmonth, myear);
        }
    };

}