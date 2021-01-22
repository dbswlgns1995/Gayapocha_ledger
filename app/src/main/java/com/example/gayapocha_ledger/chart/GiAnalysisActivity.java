package com.example.gayapocha_ledger.chart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gayapocha_ledger.R;
import com.example.gayapocha_ledger.bill.BillActivity;
import com.example.gayapocha_ledger.calendar.CalendarActivity;
import com.example.gayapocha_ledger.expend.ExpendActivity;
import com.example.gayapocha_ledger.expend.ExpendItem;
import com.example.gayapocha_ledger.ledger.IncomeItem;
import com.example.gayapocha_ledger.ledger.IncomeList;
import com.example.gayapocha_ledger.ledger.MainActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.navigation.NavigationView;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class GiAnalysisActivity extends AppCompatActivity {
    // 총소득 분석

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private long backBtnTime = 0;
    private String TAG = "***GiAnalysisActivity";
    int mday, mmonth, myear;
    TextView title_text;
    String date_str;
    Realm realm;

    MultiStateToggleButton ie_btn, date_btn;

    BarChart chart;

    int tday, tmonth, tyear;

    ArrayList<BarEntry> ylist;
    ArrayList<String> xlist, xyearlist;

    // title
    private static final String[] income_title_list = {"수구레국밥", "선지국밥", "소고기국밥", "공기밥", "계란추가", "수구레무침", "수구레볶음", "오삼불고기",
            "오돌뼈", "닭갈비", "돼지석쇠", "술국", "부추전", "소주", "맥주", "생탁", "음료수"
    };

    ArrayList mNewList;

    // cost
    private int[] income_cost_list = {7000, 6000, 6000, 1000, 2000, 20000, 20000, 15000, 15000, 15000, 15000, 6000, 6000, 4000, 4000, 3500, 1500};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gi_analysis);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        chart = findViewById(R.id.gi_barchart);

        // today date
        Calendar cal = Calendar.getInstance();
        tyear = cal.get(cal.YEAR);
        tmonth = cal.get(cal.MONTH) + 1;
        tday = cal.get(cal.DATE);

        mmonth = tmonth;
        myear = tyear;

        mNewList = new ArrayList(Arrays.asList(income_title_list));

        show(0,0);


        ie_btn = findViewById(R.id.gi_ie_btn);
        ie_btn.setValue(0);
        ie_btn.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                show(value, date_btn.getValue());
            }
        });

        date_btn = findViewById(R.id.gi_date_btn);
        date_btn.setValue(0);
        date_btn.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                switch (value){
                    case 0:
                        final Calendar today = Calendar.getInstance();
                        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(GiAnalysisActivity.this, new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // month 를 선택했을때
                                Log.d(TAG, selectedYear + "년 " + (selectedMonth + 1) + "월 선택");
                                mmonth = selectedMonth + 1;
                                myear = selectedYear;
                                show(ie_btn.getValue(), 0);
                                //

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

                    case 1:
                        MonthPickerDialog.Builder year_builder = new MonthPickerDialog.Builder(GiAnalysisActivity.this, new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // year 를 선택했을때
                                Log.d(TAG, selectedYear + "년 선택");
                                myear = selectedYear;
                                show(ie_btn.getValue(), 1);

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




        // navview drawer setting
        mDrawerLayout = (DrawerLayout) findViewById(R.id.gi_drawer_layout);
        navigationView = findViewById(R.id.gi_nav_view);
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
                    startActivity(new Intent(getApplicationContext(), IncomeAnalysisActivity.class));
                    finish();
                }else if(id == R.id.item6){ // 소득 분석
                    //startActivity(new Intent(getApplicationContext(), GiAnalysisActivity.class));
                    //finish();
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
        toolbar = (Toolbar) findViewById(R.id.gi_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu); // 메뉴 아이콘

        title_text = findViewById(R.id.gi_toolbar_title);
        //title_text.setText(myear+"."+mmonth+"."+mday+" 정산");


    }

    public void show(int ie, int my){
        int date, date_from, date_to;

        if(my==0){ // 월간

            ylist = new ArrayList<>();
            xlist = new ArrayList<>();
            Log.d(TAG, "월간차트올시다.");
            String month_format = ((mmonth) < 10 ? "0" + mmonth : "" + mmonth);

            if (ie ==0 ){ // 소득

                for(int i=1; i<=31; i++){
                    int income_cost = 0;
                    int expend_cost = 0;
                    int result_cost = 0;

                    date = Integer.parseInt(myear + "" + month_format +"" +  ((i < 10) ? "0" + i : "" + i));
                    String date_Str = myear + "." + (mmonth + 1) + ((i < 10) ? ".0" + i : "." + i);

                    RealmResults<IncomeItem> realmResults =  realm.where(IncomeItem.class).equalTo("date", date).findAll();
                    for (IncomeItem k : realmResults){
                        for(IncomeList j : k.getIncomeLists()){
                            int index = mNewList.indexOf(j.getTitle());
                            income_cost += (j.getCount() * income_cost_list[index]);
                        }
                    }

                    RealmResults<ExpendItem> realmResults1 =  realm.where(ExpendItem.class).equalTo("date", date).findAll();
                    for (ExpendItem j : realmResults1){
                        expend_cost += j.getCost();
                    }

                    result_cost = income_cost - expend_cost;

                    ylist.add(new BarEntry(i, result_cost));
                    xlist.add(date_Str);

                }
                drawChart(xlist, ylist, " " +myear + "년 " + mmonth+"월 소득",0);


            }else if(ie == 1){ // 수입
                for(int i=1; i<=31; i++){
                    int income_cost = 0;

                    date = Integer.parseInt(myear + "" + month_format +"" +  ((i < 10) ? "0" + i : "" + i));
                    String date_Str = myear + "." + (mmonth + 1) + ((i < 10) ? ".0" + i : "." + i);

                    RealmResults<IncomeItem> realmResults =  realm.where(IncomeItem.class).equalTo("date", date).findAll();
                    for (IncomeItem k : realmResults){
                        for(IncomeList j : k.getIncomeLists()){
                            int index = mNewList.indexOf(j.getTitle());
                            income_cost += (j.getCount() * income_cost_list[index]);
                        }
                    }


                    ylist.add(new BarEntry(i, income_cost));
                    xlist.add(date_Str);

                }
                drawChart(xlist, ylist, " " +myear + "년 " + mmonth+"월 수입",1);

            }else if(ie == 2){ // 지출

                for(int i=1; i<=31; i++){
                    int expend_cost = 0;

                    date = Integer.parseInt(myear + "" + month_format +"" +  ((i < 10) ? "0" + i : "" + i));
                    String date_Str = myear + "." + (mmonth + 1) + ((i < 10) ? ".0" + i : "." + i);

                    RealmResults<ExpendItem> realmResults =  realm.where(ExpendItem.class).equalTo("date", date).findAll();
                    for (ExpendItem j : realmResults){
                        expend_cost += j.getCost();
                    }
                    ylist.add(new BarEntry(i, expend_cost));
                    xlist.add(date_Str);

                }
                drawChart(xlist, ylist, " " +myear + "년 " + mmonth+"월 지출",2);

            }





        }else if(my==1){ // 년간

            ylist = new ArrayList<>();
            xlist = new ArrayList<>();
            Log.d(TAG, "년간차트올시다.");
            String month_format = ((mmonth) < 10 ? "0" + mmonth : "" + mmonth);

            if (ie ==0 ){ // 소득

                for(int i=1; i<=12; i++){
                    int income_cost = 0;
                    int expend_cost = 0;
                    int result_cost = 0;

                    date_from = Integer.parseInt(myear + "" + ((i < 10) ? "0" + i : "" + i) + "0" + 1);
                    date_to = Integer.parseInt(myear + "" + ((i < 10) ? "0" + i : "" + i) + "" + 31);
                    String date_Str = myear + "." + i;
                    Log.d(TAG, date_from+" "+date_to);

                    RealmResults<IncomeItem> realmResults =  realm.where(IncomeItem.class).between("date", date_from, date_to).findAll();
                    for (IncomeItem k : realmResults){
                        for(IncomeList j : k.getIncomeLists()){
                            int index = mNewList.indexOf(j.getTitle());
                            income_cost += (j.getCount() * income_cost_list[index]);
                        }
                    }

                    RealmResults<ExpendItem> realmResults1 =  realm.where(ExpendItem.class).between("date", date_from, date_to).findAll();
                    for (ExpendItem j : realmResults1){
                        expend_cost += j.getCost();
                    }

                    result_cost = income_cost - expend_cost;

                    ylist.add(new BarEntry(i, result_cost));
                    xlist.add(date_Str);

                }
                drawChart(xlist, ylist, " " +myear + "년 소득",0);


            }else if(ie == 1){ // 수입
                for(int i=1; i<=12; i++){
                    int income_cost = 0;

                    date_from = Integer.parseInt(myear + "" + ((i < 10) ? "0" + i : "" + i) + "0" + 1);
                    date_to = Integer.parseInt(myear + "" + ((i < 10) ? "0" + i : "" + i) + "" + 31);
                    String date_Str = myear + "." + i;
                    Log.d(TAG, date_from+" "+date_to);

                    RealmResults<IncomeItem> realmResults =  realm.where(IncomeItem.class).between("date", date_from, date_to).findAll();
                    for (IncomeItem k : realmResults){
                        for(IncomeList j : k.getIncomeLists()){
                            int index = mNewList.indexOf(j.getTitle());
                            income_cost += (j.getCount() * income_cost_list[index]);
                        }
                    }


                    ylist.add(new BarEntry(i, income_cost));
                    xlist.add(date_Str);

                }
                drawChart(xlist, ylist, " " +myear + "년 수입",1);

            }else if(ie == 2){ // 지출

                for(int i=1; i<=12; i++){
                    int expend_cost = 0;

                    date_from = Integer.parseInt(myear + "" + ((i < 10) ? "0" + i : "" + i) + "0" + 1);
                    date_to = Integer.parseInt(myear + "" + ((i < 10) ? "0" + i : "" + i) + "" + 31);
                    String date_Str = myear + "." + i;
                    Log.d(TAG, date_from+" "+date_to);

                    RealmResults<ExpendItem> realmResults =  realm.where(ExpendItem.class).between("date", date_from, date_to).findAll();
                    for (ExpendItem j : realmResults){
                        expend_cost += j.getCost();
                    }
                    ylist.add(new BarEntry(i, expend_cost));
                    xlist.add(date_Str);

                }
                drawChart(xlist, ylist, " " +myear + "년 지출",2);

            }

        }

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

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {mDrawerLayout.closeDrawers();}

        // 2번 눌러서 종료
        long curTime = System.currentTimeMillis();
        long gapTime =  curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime){
            super.onBackPressed();
        }
        else{
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
    }

    public void drawChart(final ArrayList<String> x, ArrayList<BarEntry> y, String label, int color) {

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(12);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setStartAtZero(false);



        /*
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

         */

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        ValueFormatter vf = new ValueFormatter() { //value format here, here is the overridden method
            @Override
            public String getFormattedValue(float value) {
                return ""+(int)value;
            }
        };

        BarDataSet barDataSet = new BarDataSet(y, label);
        barDataSet.setValueFormatter(vf);
        switch (color){
            case 0:
                barDataSet.setColor(R.color.royalblue);
                break;
            case 1:
                barDataSet.setColor(Color.BLUE);
                break;
            case 2:
                barDataSet.setColor(Color.RED);
                break;
        }

        BarData data = new BarData(barDataSet);
        chart.animateY(1000);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate();


    }
}