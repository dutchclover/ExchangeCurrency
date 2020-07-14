package com.dgroup.exchangerates.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.data.model.db.BankCourse;
import com.dgroup.exchangerates.ui.view.holders.BankCourseHolder;
import com.dgroup.exchangerates.ui.view.holders.BankHeaderHolder;
import com.dgroup.exchangerates.utils.Utils;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class BanksCoursesAdapter extends RecyclerView.Adapter<BankCourseHolder> implements Filterable, StickyRecyclerHeadersAdapter<BankHeaderHolder> {

    private List<BankCourse> mBankCourses;
    private List<BankCourse> mFilteredBankCourses;

    private OnBankClickListener mOnBankClickListener;
    private CharSequence lastSearch;

    public BanksCoursesAdapter(List<BankCourse> bankCourses, OnBankClickListener onBankClickListener) {
        this.mBankCourses = bankCourses;
        this.mFilteredBankCourses = bankCourses;
        this.mOnBankClickListener = onBankClickListener;
    }

    @Override
    public BankCourseHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bank_course_view, parent, false);

        BankCourseHolder vh = new BankCourseHolder(v, mOnBankClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(BankCourseHolder holder, int position) {
        holder.bind(mFilteredBankCourses.get(position), lastSearch);
    }

    @Override
    public long getHeaderId(int position) {
        return mFilteredBankCourses.get(position).getActual() ? 0 : 1;
    }

    @Override
    public BankHeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_banks, parent, false);
        return new BankHeaderHolder(root, parent.getWidth() - Utils.dpToPx(6));
    }

    @Override
    public void onBindHeaderViewHolder(BankHeaderHolder holder, int position) {
        long type = getHeaderId(position);
        holder.bind(type == 0 ? holder.itemView.getContext().getString(R.string.header_actual) :
                holder.itemView.getContext().getString(R.string.header_not_actual));
    }

    @Override
    public int getItemCount() {
        return mFilteredBankCourses.size();
    }

    public void replaceData(List<BankCourse> bankCourses) {
        mBankCourses = bankCourses;
        getFilter().filter(lastSearch);
        Log.i("BanksCoursesAdapter", "replaceData");
    }

    public void setOnBankClickListener(OnBankClickListener onBankClickListener) {
        mOnBankClickListener = onBankClickListener;
    }

    public interface OnBankClickListener {
        void onBankClicked(BankCourse bankCourse);
    }

    public boolean isSearchNow() {
        return lastSearch != null && lastSearch.length() > 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0) {
                    mFilteredBankCourses = Collections.EMPTY_LIST;
                } else {
                    mFilteredBankCourses = (ArrayList<BankCourse>) results.values;
                }
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence pConstraint) {
                lastSearch = pConstraint;
                FilterResults lResults = new FilterResults();
                if (pConstraint == null || pConstraint.length() == 0) {
                    lResults.values = mBankCourses;
                    lResults.count = mBankCourses.size();
                    return lResults;
                }
                List<BankCourse> filteredArrayNames = new ArrayList<>(mBankCourses.size());

                String filter = pConstraint.toString().toLowerCase();
                for (int i = 0; i < mBankCourses.size(); i++) {
                    if (mBankCourses.get(i).getName().toLowerCase().contains(filter)) {
                        filteredArrayNames.add(mBankCourses.get(i));
                    }
                }

                lResults.count = filteredArrayNames.size();
                lResults.values = filteredArrayNames;
                return lResults;
            }
        };
    }
}
