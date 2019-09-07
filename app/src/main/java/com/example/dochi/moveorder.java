package com.example.dochi;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
//메뉴 버튼을 눌러서 순서를 바꾼다
public class moveorder extends ItemTouchHelper.Callback {
    //좌우 swipe는 사용하지 않는다
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags,0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
        mItemMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    public interface OnItemMoveListener {
        void onItemMove(int fromPosition, int toPoition);
    }

    private final OnItemMoveListener mItemMoveListener;
    public moveorder(OnItemMoveListener listener){ ///////////////////
        mItemMoveListener = listener;
    }
}

