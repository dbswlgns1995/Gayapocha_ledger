package com.example.gayapocha_ledger.chart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;

import android.app.DatePickerDialog;
import android.content.ClipData;
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
import com.example.gayapocha_ledger.ledger.IncomeItem;
import com.example.gayapocha_ledger.ledger.IncomeList;
import com.example.gayapocha_ledger.ledger.MainActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.navigation.NavigationView;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class ItemAnalysisActivity extends AppCompatActivity {

    // 품목별 분석

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private long backBtnTime = 0;
    private String TAG = "***ItemAnalysisActivity";
    int mday, mmonth, myear;
    TextView title_text;
    String date_str;
    Realm realm;

    BarChart chart;

    int tday, tmonth, tyear;

    MultiStateToggleButton item_btn;
    MultiStateToggleButton my_btn;

    ArrayList<BarEntry> ylist;
    ArrayList<String> xlist, xyearlist;

    // title
    private static final String[] income_title_list = {"수구레국밥", "선지국밥", "소고기국밥", "공기밥", "계란추가", "수구레무침", "수구레볶음", "오삼불고기",
            "오돌뼈", "닭갈비", "돼지석쇠", "술국", "부추전", "소주", "맥주", "생탁", "음료수"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_analysis);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        // today date
        Calendar cal = Calendar.getInstance();
        tyear = cal.get(cal.YEAR);
        tmonth = cal.get(cal.MONTH) + 1;
        tday = cal.get(cal.DATE);

        chart = findViewById(R.id.item_barchart);

        mmonth = tmonth;
        myear = tyear;

        show(0,0);

        item_btn = findViewById(R.id.item_btn);
        item_btn.setValue(0);
        item_btn.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                Log.d(TAG, value +"");
                show(value, my_btn.getValue());
            }
        });

        my_btn = findViewById(R.id.item_my_btn);
        my_btn.setValue(0);
        my_btn.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                switch (value){
                    case 0:
                        final Calendar today = Calendar.getInstance();
                        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(ItemAnalysisActivity.this, new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // month 를 선택했을때
                                Log.d(TAG, selectedYear + "년 " + (selectedMonth + 1) + "월 선택");
                                mmonth = selectedMonth + 1;
                                myear = selectedYear;
                                show(item_btn.getValue(), 0);
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
                        MonthPickerDialog.Builder year_builder = new MonthPickerDialog.Builder(ItemAnalysisActivity.this, new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // year 를 선택했을때
                                Log.d(TAG, selectedYear + "년 선택");
                                myear = selectedYear;
                                show(item_btn.getValue(), 1);

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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.item_drawer_layout);
        navigationView = findViewById(R.id.item_nav_view);
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
                    startActivity(new Intent(getApplicationContext(), GiAnalysisActivity.class));
                    finish();
                }else if(id == R.id.item7){ // 품목별 분석
                    //startActivity(new Intent(getApplicationContext(), ItemAnalysisActivity.class));
                    //finish();
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
        toolbar = (Toolbar) findViewById(R.id.item_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu); // 메뉴 아이콘

        title_text = findViewById(R.id.item_toolbar_title);


    }

    public void show(int menu, int my){

        int date, date_from, date_to;

        if(my==0){ // 월간

            ylist = new ArrayList<>();
            xlist = new ArrayList<>();
            Log.d(TAG, "월간차트올시다.");
            String month_format = ((mmonth) < 10 ? "0" + mmonth : "" + mmonth);

            for(int i=1; i<=31; i++){
                int count = 0;

                date = Integer.parseInt(myear + "" + month_format +"" +  ((i < 10) ? "0" + i : "" + i));
                String date_Str = myear + "." + (mmonth + 1) + ((i < 10) ? ".0" + i : "." + i);

                RealmResults<IncomeItem> realmResults = realm.where(IncomeItem.class).equalTo("date", date).findAll();
                for (IncomeItem k : realmResults) {
                    for (IncomeList j : k.getIncomeLists()) {
                        if(j.getTitle().equals(income_title_list[menu])){
                            count+=j.getCount();
                        }

                    }
                }
                ylist.add(new BarEntry(i, count));
                xlist.add(date_Str);

            }
            drawChart(xlist, ylist, income_title_list[menu] + " " +myear + "년 " + mmonth+"월");

        }else if(my==1){ // 년간

            ylist = new ArrayList<>();
            xlist = new ArrayList<>();

            for(int i=1;i <=12; i++){
                int count = 0;
                date_from = Integer.parseInt(myear + "" + ((i < 10) ? "0" + i : "" + i) + "0" + 1);
                date_to = Integer.parseInt(myear + "" + ((i < 10) ? "0" + i : "" + i) + "" + 31);
                String date_Str = myear + "." + i;
                Log.d(TAG, date_from+" "+date_to);
                RealmResults<IncomeItem> realmResults = realm.where(IncomeItem.class).between("date", date_from, date_to).findAll();
                for (IncomeItem k : realmResults) {
                    for (IncomeList j : k.getIncomeLists()) {
                        if(j.getTitle().equals(income_title_list[menu])){
                            count+=j.getCount();
                        }

                    }
                }
                ylist.add(new BarEntry(i, count));
                xlist.add(date_Str);
            }
            drawChart(xlist, ylist, income_title_list[menu] + " " +myear + "년");
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

    public void drawChart(final ArrayList<String> x, ArrayList<BarEntry> y, String label) {

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
        barDataSet.setColor(R.color.royalblue);

        BarData data = new BarData(barDataSet);
        chart.animateY(1000);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate();


    }

}