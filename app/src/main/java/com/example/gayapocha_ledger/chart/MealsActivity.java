package com.example.gayapocha_ledger.chart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gayapocha_ledger.R;
import com.example.gayapocha_ledger.bill.BillActivity;
import com.example.gayapocha_ledger.calendar.CalendarActivity;
import com.example.gayapocha_ledger.expend.ExpendActivity;
import com.example.gayapocha_ledger.ledger.IncomeItem;
import com.example.gayapocha_ledger.ledger.IncomeList;
import com.example.gayapocha_ledger.ledger.MainActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.navigation.NavigationView;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MealsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private long backBtnTime = 0;
    private String TAG = "***MealsActivity";
    int mday, mmonth, myear;
    TextView title_text, bottom_text;
    String date_str;
    Realm realm;

    MultiStateToggleButton date_btn;
    PieChart chart;

    int tday, tmonth, tyear;

    private String[] menu_title = {"수구레국밥","선지국밥","소고기국밥"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        // today date
        Calendar cal = Calendar.getInstance();
        tyear = cal.get(cal.YEAR);
        tmonth = cal.get(cal.MONTH) + 1;
        tday = cal.get(cal.DATE);

        mday = tday;
        mmonth = tmonth;
        myear = tyear;

        chart = findViewById(R.id.meals_piechart);

        bottom_text = findViewById(R.id.meals_text);

        date_btn = findViewById(R.id.meals_dmy_btn);
        date_btn.setValue(0);
        date_btn.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                switch (value){
                    case 0:
                        DatePickerDialog dialogFragment = new DatePickerDialog(MealsActivity.this, dateListener, tyear, tmonth - 1, tday);
                        dialogFragment.show();
                        break;
                    case 1:
                        final Calendar today = Calendar.getInstance();
                        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(MealsActivity.this, new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // month 를 선택했을때
                                Log.d(TAG, selectedYear + "년 " + (selectedMonth + 1) + "월 선택");
                                mmonth = selectedMonth + 1;
                                myear = selectedYear;
                                draw(0, mmonth, myear);

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
                    case 2:
                        MonthPickerDialog.Builder year_builder = new MonthPickerDialog.Builder(MealsActivity.this, new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // year 를 선택했을때
                                Log.d(TAG, selectedYear + "년 선택");
                                myear = selectedYear;
                                draw(0, 0, myear);

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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.meals_drawer_layout);
        navigationView = findViewById(R.id.meals_nav_view);
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
                    startActivity(new Intent(getApplicationContext(), ItemAnalysisActivity.class));
                    finish();
                }else if(id == R.id.item8){ // 국 분석
                    //startActivity(new Intent(getApplicationContext(), MealsActivity.class));
                    //finish();
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
        toolbar = (Toolbar) findViewById(R.id.meals_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu); // 메뉴 아이콘

        title_text = findViewById(R.id.meals_toolbar_title);

        draw(mday, mmonth, myear);

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


    public void draw(int day, int month, int year){

        int date, date_from, date_to;


        if(day == 0 && month !=0){ // 월간
            date_from = Integer.parseInt(year + "" + ((month) < 10 ? "0" + month : "" + month) + "01");
            date_to = Integer.parseInt(year + "" + ((month) < 10 ? "0" + month : "" + month) + "31");

            List<PieEntry> pieEntries = new ArrayList<>();

            for(String str : menu_title){
                int count = 0;
                RealmResults<IncomeItem> realmResults = realm.where(IncomeItem.class).between("date", date_from, date_to).findAll();
                for(IncomeItem i : realmResults){
                    for(IncomeList j : i.getIncomeLists()){
                        if(str.equals(j.getTitle())){
                            count += j.getCount();
                        }
                    }
                }
                pieEntries.add(new PieEntry(count, str));

            }
            drawChart(pieEntries, year + "년 " + month + "월 국류 분석");



        }else if(day == 0 && month == 0){ // 년간
            date_from = Integer.parseInt(year + "0101");
            date_to = Integer.parseInt(year + "1231");

            List<PieEntry> pieEntries = new ArrayList<>();
            String des = "";

            for(String str : menu_title){
                int count = 0;
                RealmResults<IncomeItem> realmResults = realm.where(IncomeItem.class).between("date", date_from, date_to).findAll();
                for(IncomeItem i : realmResults){
                    for(IncomeList j : i.getIncomeLists()){
                        if(str.equals(j.getTitle())){
                            count += j.getCount();
                        }
                    }
                }
                pieEntries.add(new PieEntry(count, str));

                des += str + " count     ";

            }
            drawChart(pieEntries, year + "년 국류 분석");


        }else{ // 일간
            date = Integer.parseInt(year + "" + ((month) < 10 ? "0" + month : "" + month) + "" + ((day) < 10 ? "0" + day : "" + day));

            List<PieEntry> pieEntries = new ArrayList<>();

            for(String str : menu_title){
                int count = 0;
                RealmResults<IncomeItem> realmResults = realm.where(IncomeItem.class).equalTo("date", date).findAll();
                for(IncomeItem i : realmResults){
                    for(IncomeList j : i.getIncomeLists()){
                        if(str.equals(j.getTitle())){
                            count += j.getCount();
                        }
                    }
                }
                pieEntries.add(new PieEntry(count, str));

            }
            drawChart(pieEntries, year + "년 " + month + "월 " + day +"일 국류 분석");

        }


    }

    // 일간 차트
    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Log.d(TAG, year + "" + (monthOfYear + 1) + "" + dayOfMonth + "선택");
            myear = year;
            mmonth = monthOfYear + 1;
            mday = dayOfMonth;
            draw(mday, mmonth, myear);
        }
    };

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

    public void drawChart(List<PieEntry> pieEntries, String center_text){

        String bottom_title="";

        for(PieEntry i : pieEntries){
            bottom_title += i.getLabel() + " " + (int)i.getValue() + "   ";
        }
        bottom_text.setText(bottom_title);

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false); //
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setCenterText(center_text);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(12f);

        PieDataSet dataSet = new PieDataSet(pieEntries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);



        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);



        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());



        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();



    }
}