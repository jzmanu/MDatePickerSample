package com.manu.mdatepickersample;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.manu.mdatepicker.MDatePicker;
import com.manu.mdatepicker.MPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MPickerView mPickerView = findViewById(R.id.mPickerView);
        List<String> list = new ArrayList<>();
        list.add("2014");
        list.add("2015");
        list.add("2016");
        list.add("2017");
        list.add("2018");
        list.add("2019");
        list.add("2020");
        list.add("2021");
        list.add("2022");
        list.add("2023");
        list.add("2024");
        mPickerView.setText("年");
        mPickerView.setData(list);
    }

    public void btnClickDateBottom(View view) {
        MDatePicker.create(this)
                .setCanceledTouchOutside(true)
                .setGravity(Gravity.BOTTOM)
                .setOnlyYearMonth(true)
                .setTwelveHour(true)
                .setOnDateResultListener(new MDatePicker.OnDateResultListener() {
                    @Override
                    public void onDateResult(long date) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(date);
                        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                        dateFormat.applyPattern("yyyy-MM");
                        Toast.makeText(MainActivity.this, dateFormat.format(new Date(date)), Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();
    }

    public void btnClickDateCenter(View view) {
        MDatePicker.create(this)
                .setCanceledTouchOutside(true)
                .setSupportTime(true)
                .setTwelveHour(true)
                .setOnDateResultListener(new MDatePicker.OnDateResultListener() {
                    @Override
                    public void onDateResult(long date) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(date);
                        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                        dateFormat.applyPattern("yyyy-MM-dd HH:mm");
                        Toast.makeText(MainActivity.this, dateFormat.format(new Date(date)), Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();
    }
}
