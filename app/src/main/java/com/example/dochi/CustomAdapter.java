package com.example.dochi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> implements moveorder.OnItemMoveListener{
    public interface onStartDragListener{
        void onStartDrag(CustomViewHolder holder);
    }
    private ArrayList<todo> mList;
    private Context mContext;
    private final onStartDragListener mStartDragListener;

    //마감 날짜를 불러오기 위한 변수들은 버튼이 눌렸을 경우에만 필요해서 밖에서 정의
    private int mYear;
    private int mMonth;
    private int mDay;

    private LinearLayout popup;
    private TextView editDate;
    private ImageButton erase;

    @Override
    public void onItemMove(int fromPosition, int toPosition){
        Collections.swap(mList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        protected TextView mTitle;
        protected TextView mDetail;
        protected CheckBox mCheck;
        protected TextView mDate;
        protected ImageButton mDragHandler;

        public CustomViewHolder(View view) {
            super(view);

            this.mTitle = (TextView) view.findViewById(R.id.title);
            this.mDetail = (TextView) view.findViewById(R.id.detail);
            this.mCheck = (CheckBox) view.findViewById(R.id.checkBox);
            this.mDate = (TextView) view.findViewById(R.id.date);
            this.mDragHandler = (ImageButton)view.findViewById(R.id.menu);


            mCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cBox = (CheckBox) view;
                    mList.get(getAdapterPosition()).setSelected(cBox.isChecked());
                }
            });

            view.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            //꾸욱 눌렀을 경우 컨택스트 메뉴로 편집과 삭제를 만들어준다
            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "edit");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "del");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        //메뉴에서 항목 클릭시 동작을 설정
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {



            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case 1001:  //edit
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        //edit.xml의 뷰를 다시 보여준다
                        View view = LayoutInflater.from(mContext)
                                .inflate(R.layout.edit, null, false);
                        builder.setView(view);
                        final Button ButtonSubmit = (Button) view.findViewById(R.id.save);
                        final EditText editTitle = (EditText) view.findViewById(R.id.edit_title);
                        final EditText editDetail = (EditText) view.findViewById(R.id.edit_detail);
                        final ImageButton datepick = (ImageButton) view.findViewById(R.id.calender);

                        popup = (LinearLayout) view.findViewById(R.id.popup);
                        editDate = (TextView) view.findViewById(R.id.edit_date);
                        erase = (ImageButton) view.findViewById(R.id.cancel);

                        //기존의 내용을 불러오고
                        editTitle.setText(mList.get(getAdapterPosition()).getTitle());
                        editDetail.setText(mList.get(getAdapterPosition()).getDetail());

                        final Calendar cal = Calendar.getInstance();

                        if(!mList.get(getPosition()).getDate().equals("")){
                            popup.setVisibility(View.VISIBLE);
                            editDate.setText(mList.get(getPosition()).getDate());
                        }

                        //편집 중 마감날짜를 삭제할경우
                        erase.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mYear = cal.get(Calendar.YEAR);
                                mMonth = cal.get(Calendar.MONTH);
                                mDay = cal.get(Calendar.DAY_OF_MONTH);
                                editDate.setText("");
                                popup.setVisibility(View.INVISIBLE);
                            }
                        });
                        datepick.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DatePickerDialog dialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                                        String msg = String.format("%d. %d. %d", year, month+1, date);
                                        popup.setVisibility(View.VISIBLE);
                                        mYear = year;
                                        mMonth = month+1;
                                        mDay = date;
                                        editDate.setText(msg);
                                    }
                                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                                dialog.show();

                            }

                        });

                        final AlertDialog dialog = builder.create();
                        //변경 내용 저장
                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {


                            // 7. 수정 버튼을 클릭하면 현재 UI에 입력되어 있는 내용으로

                            public void onClick(View v) {
                                String strTitle = editTitle.getText().toString();
                                String strDetail = editDetail.getText().toString();
                                String strDate = editDate.getText().toString();

                                //바뀐 내용을 리스트에 추가
                                todo dict = new todo(strTitle, strDetail, strDate, mList.get(getAdapterPosition()).isSelected());
                                mList.set(getAdapterPosition(), dict);
                                notifyItemChanged(getAdapterPosition()); //adapter에게 알림
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;

                    case 1002: //삭제
                        mList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), mList.size());
                        break;
                }
                return true;
            }
        };

    }

    public CustomAdapter(Context context, ArrayList<todo> list, onStartDragListener startDragListener) {
        mList = list;
        mContext = context;
        mStartDragListener = startDragListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.itemlist, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder viewholder, final int position) {

        viewholder.mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        viewholder.mDetail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        viewholder.mTitle.setText(mList.get(position).getTitle());
        viewholder.mDetail.setText(mList.get(position).getDetail());
        viewholder.mDate.setText(mList.get(position).getDate());
        viewholder.mCheck.setChecked(mList.get(position).isSelected());

        viewholder.mDragHandler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN){
                    mStartDragListener.onStartDrag(viewholder);
                }
                return false;
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
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }
}
