package com.example.dochi;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TouchHelperCallback extends ItemTouchHelper.Callback{
//    private final ItemTouchHelperAdapter mAdapter;
//
//    public TouchHelperCallback(ItemTouchHelperAdapter adapter) {
//        mAdapter = adapter;
//    }
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
    public TouchHelperCallback(OnItemMoveListener listener){ ///////////////////
        mItemMoveListener = listener;
    }
}
