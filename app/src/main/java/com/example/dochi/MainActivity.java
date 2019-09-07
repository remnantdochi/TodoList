package com.example.dochi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements CustomAdapter.onStartDragListener {
    private ArrayList<todo> mArrayList;
    private CustomAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    //마감 날짜를 불러오기 위한 변수들은 버튼이 눌렸을 경우에만 필요해서 밖에서 정의
    private int mYear;
    private int mMonth;
    private int mDay;

    private LinearLayout popup;
    private TextView editDate;
    private ImageButton erase;

    @Override
    protected void onPause(){
        super.onPause();
        saveInfo(); //나갈때마다 변경된 정보 저장
    }
    @Override
    public void onStartDrag(CustomAdapter.CustomViewHolder holder) {
        mItemTouchHelper.startDrag(holder);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();
        mAdapter = new CustomAdapter( this, mArrayList, this);
        moveorder mCallback = new moveorder(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        readInfo(); //시작하기 전에 저장된 것들 불러오기
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        //추가 버튼 눌렀을때
        ImageButton buttonInsert = (ImageButton)findViewById(R.id.plus);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                //edit.xml의 내용을 불러온다
                View view = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.edit, null, false);
                builder.setView(view);

                //마감기한을 위한 달력 다이얼로그 불러오기 위한 변수 세팅
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                final Button ButtonSubmit = (Button) view.findViewById(R.id.save);
                final EditText editTitle = (EditText) view.findViewById(R.id.edit_title);
                final EditText editDetail = (EditText) view.findViewById(R.id.edit_detail);
                final ImageButton datepick = (ImageButton) view.findViewById(R.id.calender);

                editDate = (TextView) view.findViewById(R.id.edit_date);
                erase = (ImageButton) view.findViewById(R.id.cancel);
                popup = (LinearLayout) view.findViewById(R.id.popup);
                final AlertDialog dialog = builder.create();

                erase.setOnClickListener(new View.OnClickListener() {
                    //마감을 지울 경우 마감날짜를 현재로 변경
                    @Override
                    public void onClick(View view) {
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        popup.setVisibility(View.INVISIBLE);
                    }
                });

                datepick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(0);
                    }

                });


                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String strTitle = editTitle.getText().toString();
                        String strDetail = editDetail.getText().toString();
                        String duedate = String.valueOf(mYear) + ". " + String.valueOf(mMonth) + ". " + String.valueOf(mDay);

                        if (mYear == c.get(Calendar.YEAR) && mMonth == c.get(Calendar.MONTH) && mDay == c.get(Calendar.DAY_OF_MONTH)) {
                            //마감 날짜가 현재일 경우 표시 안함
                            duedate = "";
                        }

                        //새로 추가된 할일을 list에 추가
                        todo newtodo = new todo(strTitle, strDetail, duedate,false );
                        mArrayList.add(0, newtodo);
                        mAdapter.notifyItemInserted(0); //추가 된걸 adapter에게 알림
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthofYear, int dayofMonth) {
                    mYear = year;
                    mMonth = monthofYear+1;
                    mDay = dayofMonth;
                    popup.setVisibility(View.VISIBLE);
                    editDate.setText(String.valueOf(mYear).concat(". ").concat(String.valueOf(mMonth)).concat(". ").concat(String.valueOf(mDay)));
                }
            };
    @Override
    protected Dialog onCreateDialog(int id){
        return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
    }

    private void saveInfo(){
        try {
            File file = new File(this.getFilesDir(), "saved");
            FileOutputStream fOut = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));

            for (int i = 0; i < mArrayList.size(); i++){
                bw.write(String.valueOf(mArrayList.get(i).isSelected()));
                bw.newLine();
                bw.write(mArrayList.get(i).getTitle());
                bw.newLine();
                bw.write(mArrayList.get(i).getDetail());
                bw.newLine();
                bw.write(mArrayList.get(i).getDate());
                bw.newLine();
            }
            bw.close();
            fOut.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void readInfo(){
        File file = new File(this.getFilesDir(), "saved");
        if (!file.exists()){
            return;
        }
        try{
            FileInputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = reader.readLine();
            while (line != null){
                todo temp = new todo();
                temp.setSelected(Boolean.valueOf(line));
                line = reader.readLine();
                temp.setTitle(line);
                line = reader.readLine();
                temp.setDetail(line);
                line = reader.readLine();
                temp.setDate(line);
                mArrayList.add(temp);
                line = reader.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}


