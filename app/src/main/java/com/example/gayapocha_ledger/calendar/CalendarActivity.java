package com.example.gayapocha_ledger.calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gayapocha_ledger.R;
import com.example.gayapocha_ledger.bill.BillActivity;
import com.example.gayapocha_ledger.chart.BeveragesActivity;
import com.example.gayapocha_ledger.chart.GiAnalysisActivity;
import com.example.gayapocha_ledger.chart.IncomeAnalysisActivity;
import com.example.gayapocha_ledger.chart.ItemAnalysisActivity;
import com.example.gayapocha_ledger.chart.MealsActivity;
import com.example.gayapocha_ledger.chart.SnacksActivity;
import com.example.gayapocha_ledger.expend.ExpendActivity;
import com.example.gayapocha_ledger.expend.ExpendItem;
import com.example.gayapocha_ledger.ledger.IncomeItem;
import com.example.gayapocha_ledger.ledger.IncomeList;
import com.example.gayapocha_ledger.ledger.MainActivity;
import com.example.gayapocha_ledger.util.NewBill;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener, OnDateSelectedListener, OnMonthChangedListener {

    // Material CalendarView - https://github.com/prolificinteractive/material-calendarview

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private long backBtnTime = 0;
    TextView title_text;

    MaterialCalendarView calendarView;
    private BottomSheetBehavior bottomSheetBehavior;

    ImageView cancel_imageview;
    FrameLayout calendar_framelayout;
    TextView income_text, expend_text, result_text;

    CalculateFragment calculateFragment;


    private String TAG = "***CalendarActivity";
    int income_cost=0, expend_cost=0, result_cost=0;
    int date_from, date_to;

    ArrayList<CalendarDay> dates;

    Realm realm;

    // title
    private static final String[] income_title_list = {"수구레국밥", "선지국밥", "소고기국밥", "공기밥", "계란추가", "수구레무침", "수구레볶음", "오삼불고기",
            "오돌뼈", "닭갈비", "돼지석쇠", "술국", "부추전", "소주", "맥주", "생탁", "음료수"
    };

    ArrayList mNewList;


    // cost
    private static final int[] income_cost_list = {7000, 6000, 6000, 1000, 2000, 20000, 20000, 15000, 15000, 15000, 15000, 6000, 6000, 4000, 4000, 3500, 1500};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        cancel_imageview = findViewById(R.id.calendar_cancel_imageview);
        calendar_framelayout = findViewById(R.id.calendar_framelayout);

        income_text = findViewById(R.id.calendar_income_text);
        expend_text = findViewById(R.id.calendar_expend_text);
        result_text = findViewById(R.id.calendar_result_text);

        // navview drawer setting
        mDrawerLayout = (DrawerLayout) findViewById(R.id.calendar_layout_drawer);
        navigationView = findViewById(R.id.calendar_nav_view);
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
                    //startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                    //finish();
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
        toolbar = (Toolbar) findViewById(R.id.calendar_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu); // 메뉴 아이콘

        // bottomSheet(끌수있는) 초기화
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // cancel button 숨기기
        cancel_imageview.setVisibility(View.INVISIBLE);

        mNewList = new ArrayList(Arrays.asList(income_title_list));

        // 오늘 날짜
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date today = new Date();
        final String today_str = simpleDateFormat.format(today);
        calculateFragment = new CalculateFragment(Integer.parseInt(today_str));
        // bottomsheet에 calculateFragment 붙이기
        getSupportFragmentManager().beginTransaction().add(R.id.calendar_framelayout, calculateFragment).commit();

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        // 월 수입, 지출, 합계 계산용 realm query
        date_from = Integer.parseInt(today_str.substring(0,6)+"01");
        date_to = Integer.parseInt(today_str.substring(0,6)+"31");

        RealmResults<IncomeItem> realmResults =  realm.where(IncomeItem.class).between("date", date_from, date_to).findAll();
        for (IncomeItem i : realmResults){
            for(IncomeList j : i.getIncomeLists()){
                int index = mNewList.indexOf(j.getTitle());
                if (i.getDate() >= 20220328){
                    income_cost += (j.getCount() * NewBill.new_income_list[index]);
                } else {
                    income_cost += (j.getCount() * income_cost_list[index]);
                }

            }
        }

        int realm_expend = realm.where(ExpendItem.class).notEqualTo("cost", 0).between("date", date_from, date_to).sum("cost").intValue();

        expend_cost += realm_expend;
        result_cost = income_cost - expend_cost;

        income_text.setText(toNumFormat(income_cost));
        expend_text.setText(toNumFormat(expend_cost));
        result_text.setText(toNumFormat(result_cost));


        // Material CalendarView

        calendarView.setSelectedDate(today);
        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2010, 0, 1))
                .setMaximumDate(CalendarDay.from(2040, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        calendarView.addDecorators(new SundayDecorator(),
                new SaturdayDecorator()); // 일요일 빨간색, 토요일 파란색

        // 정산 완료된 날짜 표기용 realm
        RealmResults<IncomeItem> incomeItemRealmResults = realm.where(IncomeItem.class).findAll();
        RealmResults<ExpendItem> expendItemRealmResults = realm.where(ExpendItem.class).notEqualTo("cost", 0).findAll();

        // 정산 완료 여부 표시하기
        dates = new ArrayList<>();
        for(IncomeItem i :incomeItemRealmResults){
            String date_str = Integer.toString(i.getDate());
            String date_year = date_str.substring(0,4);
            String date_month = date_str.substring(4,6);
            String date_day = date_str.substring(6,8);

            CalendarDay day = CalendarDay.from(Integer.parseInt(date_year), Integer.parseInt(date_month)-1, Integer.parseInt(date_day));
            dates.add(day);

            Log.d(TAG, day.toString());

        }

        for(ExpendItem i : expendItemRealmResults){
            String date_str = Integer.toString(i.getDate());
            String date_year = date_str.substring(0,4);
            String date_month = date_str.substring(4,6);
            String date_day = date_str.substring(6,8);

            CalendarDay day = CalendarDay.from(Integer.parseInt(date_year), Integer.parseInt(date_month)-1, Integer.parseInt(date_day));
            dates.add(day);
        }

        calendarView.addDecorator(new EventDecorator(dates));

        // x 표시 누르면 onbackpressed
        cancel_imageview.setOnClickListener(this);

        // 확장했을때 cancel button visible 그 외에는 invisible
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        cancel_imageview.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        // bottomsheet 내려가면 calendarfragment refresh
                        cancel_imageview.setVisibility(View.INVISIBLE);

                        // 정산 완료된 날짜 표기용 realm
                        RealmResults<IncomeItem> incomeItemRealmResults = realm.where(IncomeItem.class).findAll();
                        RealmResults<ExpendItem> expendItemRealmResults = realm.where(ExpendItem.class).notEqualTo("cost", 0).findAll();

                        // 정산 완료 여부 표시하기
                        dates = new ArrayList<>();
                        for(IncomeItem e :incomeItemRealmResults){
                            String date_str = Integer.toString(e.getDate());
                            String date_year = date_str.substring(0,4);
                            String date_month = date_str.substring(4,6);
                            String date_day = date_str.substring(6,8);

                            CalendarDay day = CalendarDay.from(Integer.parseInt(date_year), Integer.parseInt(date_month)-1, Integer.parseInt(date_day));
                            dates.add(day);

                            Log.d(TAG, day.toString());

                        }

                        for(ExpendItem e : expendItemRealmResults){
                            String date_str = Integer.toString(e.getDate());
                            String date_year = date_str.substring(0,4);
                            String date_month = date_str.substring(4,6);
                            String date_day = date_str.substring(6,8);

                            CalendarDay day = CalendarDay.from(Integer.parseInt(date_year), Integer.parseInt(date_month)-1, Integer.parseInt(date_day));
                            dates.add(day);
                        }
                        calendarView.addDecorator(new EventDecorator(dates));



                        // 월 수입, 지출, 합계 계산용 realm query
                        Log.d(TAG, "바텀시트 내려가면 수입, 지출, 합계 리셋");

                        int realm_expend = realm.where(ExpendItem.class).notEqualTo("cost", 0).between("date", date_from, date_to).sum("cost").intValue();

                        income_cost = 0;
                        expend_cost = 0;
                        result_cost = 0;

                        RealmResults<IncomeItem> realmResults =  realm.where(IncomeItem.class).between("date", date_from, date_to).findAll();
                        for (IncomeItem k : realmResults){
                            for(IncomeList j : k.getIncomeLists()){
                                int index = mNewList.indexOf(j.getTitle());

                                if (k.getDate() >= 20220328){
                                    income_cost += (j.getCount() * NewBill.new_income_list[index]);
                                } else {
                                    income_cost += (j.getCount() * income_cost_list[index]);
                                }
                            }
                        }

                        expend_cost += realm_expend;
                        result_cost = income_cost - expend_cost;

                        income_text.setText(toNumFormat(income_cost));
                        expend_text.setText("-"+toNumFormat(expend_cost));
                        result_text.setText(toNumFormat(result_cost));

                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        cancel_imageview.setVisibility(View.INVISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        cancel_imageview.setVisibility(View.INVISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        cancel_imageview.setVisibility(View.INVISIBLE);
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                cancel_imageview.setVisibility(View.INVISIBLE);
            }


        });



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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendar_cancel_imageview:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }

    // calendarView 날짜 변경 시 bottomsheet 올라오게 하기왜요
    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        calendar_framelayout.removeAllViews(); // framelayout 초기화
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        String month_str = (date.getMonth()+1) < 10 ? "0" + (date.getMonth()+1) : ""+ (date.getMonth()+1);
        String day_str = date.getDay() < 10 ? "0" + date.getDay() : ""+ date.getDay();;
        String date_str = date.getYear() +  month_str + day_str;
        Log.d(TAG, date_str);
        calculateFragment = new CalculateFragment(Integer.parseInt(date_str));
        getSupportFragmentManager().beginTransaction().add(R.id.calendar_framelayout, calculateFragment).commit();
    }

    // calendarView 달 변경시 월수입, 지출, 합계 텍스트뷰 변경
    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        String month_str = (date.getMonth()+1) < 10 ? "0" + (date.getMonth()+1) : ""+ (date.getMonth()+1);

        date_from = Integer.parseInt(date.getYear()+month_str+"01");
        date_to = Integer.parseInt(date.getYear()+month_str+"31");

        int realm_expend = realm.where(ExpendItem.class).notEqualTo("cost", 0).between("date", date_from, date_to).sum("cost").intValue();

        income_cost = 0;
        expend_cost = 0;
        result_cost = 0;

        RealmResults<IncomeItem> realmResults = realm.where(IncomeItem.class).between("date", date_from, date_to).findAll();
        for (IncomeItem k : realmResults){
            for(IncomeList j : k.getIncomeLists()){
                int index = mNewList.indexOf(j.getTitle());
                Log.d(TAG, j.getTitle() + " " + j.getCount());

                if (k.getDate() >= 20220328){
                    income_cost += (j.getCount() * NewBill.new_income_list[index]);
                } else {
                    income_cost += (j.getCount() * income_cost_list[index]);
                }
            }
        }

        expend_cost += realm_expend;
        result_cost = income_cost - expend_cost;

        income_text.setText(toNumFormat(income_cost));
        expend_text.setText("-"+toNumFormat(expend_cost));
        result_text.setText(toNumFormat(result_cost));

    }

    // 천 단위 콤마
    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
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
}