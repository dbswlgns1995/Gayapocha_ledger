package com.example.gayapocha_ledger.bill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gayapocha_ledger.R;
import com.example.gayapocha_ledger.calendar.CalendarActivity;
import com.example.gayapocha_ledger.chart.BeveragesActivity;
import com.example.gayapocha_ledger.chart.GiAnalysisActivity;
import com.example.gayapocha_ledger.chart.IncomeAnalysisActivity;
import com.example.gayapocha_ledger.chart.ItemAnalysisActivity;
import com.example.gayapocha_ledger.chart.MealsActivity;
import com.example.gayapocha_ledger.chart.SnacksActivity;
import com.example.gayapocha_ledger.expend.ExpendActivity;
import com.example.gayapocha_ledger.ledger.IncomeItem;
import com.example.gayapocha_ledger.ledger.MainActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class BillActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private long backBtnTime = 0;
    int mday, mmonth, myear;
    String date_str;
    TextView title_text;
    private String TAG = "***billactivity";
    Realm realm;
    private RecyclerView recyclerView;
    private BillRecyclerviewAdapter billRecyclerviewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        init();

        recyclerview_init();



    }

    private void recyclerview_init(){

        RealmResults<IncomeItem> realmResults = realm.where(IncomeItem.class).equalTo("date", Integer.parseInt(date_str)).sort("id", Sort.ASCENDING).findAll();
        Log.d(TAG, "recyclerview_init: "+ realmResults.toString());
        billRecyclerviewAdapter = new BillRecyclerviewAdapter(realmResults, true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(billRecyclerviewAdapter);



    }

    private void init(){
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        recyclerView = findViewById(R.id.bill_recyclerview);

        // today date
        Calendar cal = Calendar.getInstance();
        myear = cal.get ( cal.YEAR );
        mmonth = cal.get ( cal.MONTH ) + 1;
        mday = cal.get ( cal.DATE ) ;
        date_str = myear + "" + ((mmonth) < 10 ? "0" + mmonth : "" + mmonth) + "" + ((mday) < 10 ? "0" + mday : "" + mday);

        // navview drawer setting
        mDrawerLayout = (DrawerLayout) findViewById(R.id.bill_drawer_layout);
        navigationView = findViewById(R.id.bill_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();

                if(id == R.id.item1){ // 수입
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                else if(id == R.id.item2){ // 영수증
                    //startActivity(new Intent(getApplicationContext(), BillActivity.class));
                    //finish();
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
        toolbar = (Toolbar) findViewById(R.id.bill_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu); // 메뉴 아이콘

        title_text = findViewById(R.id.bill_toolbar_title);
        title_text.setText(myear+"."+mmonth+"."+mday+" 영수증");
    }

    @Override
    protected void onResume() {
        recyclerview_init();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
                DatePickerDialog dialogFragment = new DatePickerDialog(BillActivity.this ,dateListener, myear, mmonth-1, mday);
                dialogFragment.show();

        }
        return super.onOptionsItemSelected(item);
    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Log.d(TAG, year + "" + (monthOfYear+1)+ ""+ dayOfMonth + "선택");
            myear = year;
            mmonth = monthOfYear+1;
            mday = dayOfMonth;
            title_text.setText(myear+"."+mmonth+"."+mday+" 영수증");
            date_str = myear + "" + ((mmonth) < 10 ? "0" + mmonth : "" + mmonth) + "" + ((mday) < 10 ? "0" + mday : "" + mday);
            recyclerview_init();

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

    public String sendData() {
        return date_str;
    }
}