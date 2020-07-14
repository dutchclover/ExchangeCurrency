package com.dgroup.exchangerates.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dgroup.exchangerates.R;

import com.dgroup.exchangerates.data.model.BasicContent;
import com.dgroup.exchangerates.data.model.db.Valute;
import com.dgroup.exchangerates.ui.view.holders.ValuteViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ValuteRatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean dragMode;

    public interface OnItemClickListener {
        void onClick(View v);
        void startDrug(RecyclerView.ViewHolder viewHolder);
    }

    private List<Valute> mValutes;
    private OnItemClickListener mOnItemClickListener;

    private BasicContent mBasicContent;

    private Comparator<Valute> mValuteComparator;

    public ValuteRatesAdapter() {
        mValutes = new ArrayList<>();
        mValuteComparator = (lhs, rhs) ->
                Integer.compare(lhs.getPosition().getPosition(), rhs.getPosition().getPosition());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mValutes.size();
    }

    @Override
    public long getItemId(int position) {
        return mValutes.get(position).hashCode();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        return new ValuteViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_valute_view, parent, false),
                mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Valute valute = mValutes.get(position);
        ((ValuteViewHolder) viewHolder).bind(valute, mBasicContent, dragMode);
    }

    public void setValutes(List<Valute> valutes) {
        Collections.sort(valutes, mValuteComparator);
        mValutes = valutes;
    }

    public void setBasicContent(BasicContent basicContent) {
        mBasicContent = basicContent;
    }

    public void notifyMovedItems(int fromPosition, int toPosition) {
        Collections.swap(mValutes, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void setDragMode(boolean selected) {
        dragMode = selected;
        notifyDataSetChanged();
    }

    public boolean isDragMode(){
        return dragMode;
    }
}
