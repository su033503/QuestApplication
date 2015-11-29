package com.example.user1.questdiary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    EditText edtDiary, edtDate;
    Button btnWrite;
    DatePicker dp;
    String fileName;
    View dateView;
    Calendar cal;
    int cYear, cMonth, cDay;
    String diaryPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("발전된 일기장");

        String strSDpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        final File myDir = new File(strSDpath+"/mydiary");

        if(!myDir.exists()) {
            myDir.mkdirs();
            Toast.makeText(getApplicationContext(), "디렉토리생성", Toast.LENGTH_SHORT).show();

        }
        diaryPath = myDir.getAbsolutePath();

        edtDiary = (EditText)findViewById(R.id.edtDiary);
        edtDate = (EditText)findViewById(R.id.edtDate);
        btnWrite = (Button)findViewById(R.id.btnWrite);

        cal = Calendar.getInstance();
        cYear = cal.get(Calendar.YEAR);
        cMonth = cal.get(Calendar.MONTH);
        cDay = cal.get(Calendar.DAY_OF_MONTH);
        makeDiary(cYear, cMonth, cDay);             //일기 생성

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateView = (View) View.inflate(MainActivity.this, R.layout.datepick, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("날짜 선택");
                dlg.setView(dateView);

                dp = (DatePicker) dateView.findViewById(R.id.datePicker1);
                dp.init(cYear, cMonth, cDay, new DatePicker.OnDateChangedListener() {
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        cal.set(year, monthOfYear, dayOfMonth);
                    }
                });

                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cYear = cal.get(Calendar.YEAR);
                        cMonth = cal.get(Calendar.MONTH);
                        cDay = cal.get(Calendar.DAY_OF_MONTH);
                        makeDiary(cYear, cMonth, cDay);
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File file = new File(diaryPath, fileName);
                    FileOutputStream outFs = new FileOutputStream(file);
                    String str = edtDiary.getText().toString();
                    outFs.write(str.getBytes());
                    outFs.close();
                    Toast.makeText(getApplicationContext(), fileName + " 이 저장됨", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void makeDiary(int year, int month, int day) {
        String date = Integer.toString(year)+"년 "+Integer.toString(month + 1)+"월 "+ Integer.toString(day)+"일";
        fileName = Integer.toString(year) + "_" + Integer.toString(month + 1) + "_" + Integer.toString(day) + ".txt";
        String str = readDiary(fileName);
        edtDate.setText(date);
        edtDiary.setText(str);
        btnWrite.setEnabled(true);
    }
    String readDiary(String fName) {
        String diaryStr = null;
        FileInputStream inFs;
        try {
            File file = new File(diaryPath, fName);
            inFs = new FileInputStream(file);
            byte [] txt = new byte[500];
            inFs.read(txt);
            inFs.close();
            diaryStr = (new String(txt)).trim();
            btnWrite.setText("수정하기");
        } catch (IOException e) {
            edtDiary.setHint("일기 없음");
            btnWrite.setText("새로 저장");
        }
        return diaryStr;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.reload:
                Toast.makeText(getApplicationContext(), "리로드", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete:
                Toast.makeText(getApplicationContext(), "삭제", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.fontBig:
                edtDiary.setTextSize(30);
                return true;
            case R.id.fontNormal:
                edtDiary.setTextSize(20);
                return true;
            case R.id.fontSmall:
                edtDiary.setTextSize(10);
                return true;
        }
        return false;
    }
}
