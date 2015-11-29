package com.example.user1.optiondiary;

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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    EditText edtDiary;
    TextView textDate, textDialog;
    Button btnWrite;
    DatePicker dp;
    String fileName;
    View dateView, dialogView;
    Calendar cal;
    int cYear, cMonth, cDay;
    String diaryPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("발전된 일기장");

        final String strSDpath = Environment.getExternalStorageDirectory().getAbsolutePath();     //sd카드 경로를 저장
        System.out.println(strSDpath);
        final File myDir = new File(strSDpath+"/mydiary");     //mydiary 경로를 지정하여 File객체 생성
        if(!myDir.exists()) {                            //앱 실행시 mydiary 폴더가 없다면 폴더를 만든다.
            myDir.mkdir();
            Toast.makeText(getApplicationContext(), "mydiary폴더가 sd카드에 자동생성 되었습니다.", Toast.LENGTH_SHORT).show();
        }

        diaryPath = myDir.getAbsolutePath();            //mydiary의 절대경로를 String 형태로 diaryPath라는 변수에 저장
        System.out.println(diaryPath);
        edtDiary = (EditText)findViewById(R.id.edtDiary);       //일기를 작성할 edtDiary를 연결
        textDate = (TextView)findViewById(R.id.dateView);       //날짜를 표시하고 수정할 DateView를 연결
        btnWrite = (Button)findViewById(R.id.btnWrite);         //저장버튼의 역할을 할 btnWrite를 연결

        cal = Calendar.getInstance();                           //앱 실행시 현재 년,월,일을 받아와서
        cYear = cal.get(Calendar.YEAR);                         //변수들에 각각 저장
        cMonth = cal.get(Calendar.MONTH);
        cDay = cal.get(Calendar.DAY_OF_MONTH);

        makeDiary(cYear, cMonth, cDay);                         //오늘 날짜에 해당하는 일기가 있는지 체크, 있으면 불러온다.

        textDate.setOnTouchListener(new View.OnTouchListener() {        //textDate에 OnTouch리스너를 등록, 날짜변경을 하려면 터치하게 만든다.
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dateView = (View) View.inflate(MainActivity.this, R.layout.datepick, null);     //datepicker를 사용한 datepick.xml을 다이얼로그로 불러온다.
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("날짜 선택");
                dlg.setView(dateView);

                dp = (DatePicker) dateView.findViewById(R.id.datePicker1);              //datePicker1을 연결
                dp.init(cYear, cMonth, cDay, new DatePicker.OnDateChangedListener() {
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {  //현재 년,월,일로 DatePicker 위젯을 세팅한다.
                        cal.set(year, monthOfYear, dayOfMonth);
                    }
                });

                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {            //사용자가 날짜선택을 마치고 확인버튼을 누르면
                        cYear = cal.get(Calendar.YEAR);                                 //현재 선택된 년,월,일 값으로 변수들을 갱신한다.
                        cMonth = cal.get(Calendar.MONTH);
                        cDay = cal.get(Calendar.DAY_OF_MONTH);
                        makeDiary(cYear, cMonth, cDay);                                 //해당 날짜의 일기가 있는지 체크, 있으면 불러온다.
                    }
                });
                dlg.setNegativeButton("취소", null);                                    //취소버튼을 누르면 아무런 변경없이 그대로 다이얼로그종료
                dlg.show();
                return false;
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                   //저장버튼에 OnClick 리스너를 달아준다.
                try {
                    File file = new File(diaryPath, fileName);                //diary폴더까지의 절대경로와 파일이름을 인자로 주고 File객체를 생성한다.
                    FileOutputStream outFs = new FileOutputStream(file);      //FileOutputStream객체를 생성, 방금만든 File객체와 연결한다.
                    String str = edtDiary.getText().toString();               //작성된 일기를 String문자열에 저장하고
                    outFs.write(str.getBytes());                              //byte형태로 변환하여 파일에 쓴다.
                    outFs.close();                                            //close()는 잊지않고 해준다.
                    Toast.makeText(getApplicationContext(), fileName + " 이 저장됨", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void makeDiary(int year, int month, int day) {                     //년,월,일을 인자로 받아 파일이름을 조합하여 생성하는 메소드이다.
        String date = Integer.toString(year)+"년 "+Integer.toString(month + 1)+"월 "+ Integer.toString(day)+"일";      //날짜를 표시하기 위한 변수이다.
        fileName = Integer.toString(year) + "_" + Integer.toString(month + 1) + "_" + Integer.toString(day) + ".txt";  //파일이름을 조합하여 만들고 변수에 저장한다.
        String str = readDiary(fileName);                              //readDiary메소드에 조합한 파일이름을 넘겨준다. 그 결과를 String변수로 받아 저장한다.
        textDate.setText(date);                                        //버튼과 일기장의 텍스트를 갱신한다.
        edtDiary.setText(str);
        btnWrite.setEnabled(true);
    }
    String readDiary(String fName) {                        //파일이름을 넘겨받아 해당파일이 있으면 내용을 읽어오고 없다면 없다는 상태를 표시하는 메소드이다.
        String diaryStr = null;
        FileInputStream inFs;
        try {
            File file = new File(diaryPath, fName);         //diary폴더까지의 절대경로와 파일이름을 인자로 주고 File객체를 생성한다.
            inFs = new FileInputStream(file);               //FileInputStream객체를 생성, 방금만든 File객체와 연결한다.
            byte [] txt = new byte[500];
            inFs.read(txt);                                 //byte형태로 읽어와서 txt라는 변수에 저장
            inFs.close();                                   //잊지않고 close()한다.
            diaryStr = (new String(txt)).trim();            //String형태로 변환하여 저장한다.
            btnWrite.setText("수정하기");                    //일기가 있다면 저장버튼은 수정하기로 이름을 바꿔준다.
        } catch (IOException e) {
            edtDiary.setHint("일기 없음");                   //일기가 없다면 일기없음이라는 메세지를 edtDiary에 표시
            btnWrite.setText("새로 저장");                   //저장버튼을 새로 저장이라는 이름으로 세팅.
        }
        return diaryStr;                                    //읽어온 내용을 리턴
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu1, menu);              //menu1.xml을 옵션으로 불러옴
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {                          //선택에 따라
            case R.id.reload:                               //다시읽기를 누르면 저장내용을 그대로 다시 불러온다.
                makeDiary(cYear, cMonth, cDay);             //변수에 저장되어있는 날짜 그대로 불러온다.
                Toast.makeText(getApplicationContext(), "다시 읽기", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete:
                deleteDialog();                             //삭제시 deleteDialog()메소드를 호출
                return true;
            case R.id.fontBig:                              //글씨크기 조절
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
    public void deleteDialog() {                            //현재 날짜에 해당하는 일기를 삭제하는 메소드
        dialogView = (View) View.inflate(MainActivity.this, R.layout.deletedialog, null);   //textView를 띄울 deletedialog를 불러와 연결한다
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("삭제하기");
        dlg.setView(dialogView);
        textDialog = (TextView) dialogView.findViewById(R.id.dlgText);                      //textView를 연결하고
        textDialog.setText(textDate.getText() + " 일기를 삭제하시겠습니까?");                 //현재 날짜와 연동시켜 일기를 삭제하겠냐는 메세지를 출력한다.

        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {               //확인버튼을 누를 경우
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(diaryPath, fileName);                                  //저장되어 있는 경로와 파일이름을 가지고
                if(file.exists()) {                                                         //파일이 존재하면
                    file.delete();                                                          //파일삭제
                    edtDiary.setText("");                                                   //일기장과 버튼을 갱신한다.
                    edtDiary.setHint("일기 없음");
                    btnWrite.setText("새로 저장");
                    Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show(); //삭제되었다고 토스트메세지출력
                }
                else
                    Toast.makeText(getApplicationContext(), "해당 일기가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();    //파일이 없다면 없다고 토스트메세지출력
            }
        });
        dlg.setNegativeButton("취소", null);      //취소버튼을 누르면 그냥 종료
        dlg.show();
    }
}
