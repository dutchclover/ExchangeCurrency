package com.dgroup.exchangerates.features.cb_rates.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.model.BasicContent;
import com.dgroup.exchangerates.data.model.db.Valute;
import com.dgroup.exchangerates.features.cb_rates.CBRatesMvpView;
import com.dgroup.exchangerates.features.cb_rates.CBRatesPresenter;
import com.dgroup.exchangerates.features.cb_rates.CBRouter;
import com.dgroup.exchangerates.features.cb_rates.listeners.SimpleItemTouchHelperCallback;
import com.dgroup.exchangerates.features.main.MainActivity;
import com.dgroup.exchangerates.features.main.MainMvpView;
import com.dgroup.exchangerates.ui.adapter.ValuteRatesAdapter;
import com.dgroup.exchangerates.ui.view.FirstItemDecorator;
import com.dgroup.exchangerates.utils.TimeFormatter;
import com.dgroup.exchangerates.utils.constants.TinyDbKeys;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;

import java.util.List;

import javax.inject.Inject;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static com.dgroup.exchangerates.features.main.MainActivity.CALC_VAL_REQUEST_COUNT;


/**
 * Created by dmitriy on 09.02.15.
 */
public class CBRatesFragment extends Fragment implements CBRatesMvpView, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = CBRatesFragment.class.getSimpleName();

    @Inject
    protected CBRatesPresenter mCBRatesPresenter;

    private View rootView;
    private TextView updateDate;
    private SwipeRefreshLayout mRefreshLayout;

    private ValuteRatesAdapter valuteRatesAdapter;

    private ItemTouchHelper mItemTouchHelper;

    private MainMvpView mainMvpView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainMvpView = (MainMvpView) activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainMvpView = (MainMvpView) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExRatesApp.getApp().plusCBRatesComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_rates_list, container, false);
        View menu = inflater.inflate(R.layout.toolbar_cb_rates, null);
        initUi(rootView);
        initMenu(menu);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCBRatesPresenter.subscribe(this, (CBRouter) getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!valuteRatesAdapter.isDragMode()){
            mainMvpView.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    protected void initUi(View rootView) {
        valuteRatesAdapter = new ValuteRatesAdapter();
        valuteRatesAdapter.setHasStableIds(true);
        valuteRatesAdapter.setOnItemClickListener(new ValuteRatesAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v) {
                Valute valute = (Valute) v.getTag();
                mCBRatesPresenter.showCalculator(valute);
            }

            @Override
            public void startDrug(RecyclerView.ViewHolder viewHolder) {
                mItemTouchHelper.startDrag(viewHolder);
            }
        });

        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        mRefreshLayout.setOnRefreshListener(this);

        RecyclerView ratesList = (RecyclerView) rootView.findViewById(R.id.rates);
        ratesList.setHasFixedSize(true);
        ratesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        ratesList.setItemAnimator(new DefaultItemAnimator());
        ratesList.addItemDecoration(
                new FirstItemDecorator(ContextCompat.getColor(getActivity(), R.color.item_select)), 0);
        ratesList.setAdapter(valuteRatesAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCBRatesPresenter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(ratesList);

        updateDate = (TextView) rootView.findViewById(R.id.date);
    }

    private void initMenu(View menu) {
        ((MainActivity) getActivity()).invalidateMenu(menu);
        View swap = menu.findViewById(R.id.swap);
        swap.setOnClickListener(v -> {
            boolean selected = !v.isSelected();
            v.setSelected(selected);
            valuteRatesAdapter.setDragMode(selected);
            mainMvpView.setHoldBackMenu(() -> {
                v.setSelected(false);
                valuteRatesAdapter.setDragMode(false);
                mainMvpView.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRefreshLayout.setOnRefreshListener(null);
        mCBRatesPresenter.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mainMvpView = null;
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        mCBRatesPresenter.updateCBRates();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALC_VAL_REQUEST_COUNT && resultCode == Activity.RESULT_OK) {
            String key = data.getExtras().getString("key");
            float value = data.getExtras().getFloat("value");
            float count = data.getExtras().getFloat("count");
            mCBRatesPresenter.recalculationCount(key, count, value);
        }
    }

    @Override
    public void showList(List<Valute> valutes, boolean showHint) {
        bindDate(TinyDbWrap.getInstance().getLong(TinyDbKeys.KEYS.VALUTE_UPDATED, 0));
        valuteRatesAdapter.setValutes(valutes);
        update();
        if (showHint) {
            Snackbar.make(rootView, getString(R.string.loading_complete), LENGTH_SHORT).show();
        }
    }

    @Override
    public void showLoading(boolean isShown) {
        Log.i(TAG, "showLoading " + isShown);
        mRefreshLayout.setRefreshing(isShown);
    }

    @Override
    public void showError() {
        mRefreshLayout.setRefreshing(false);
        Snackbar.make(rootView, getString(R.string.loading_error), LENGTH_SHORT).show();
    }

    @Override
    public void setBasicContent(BasicContent basicContent, boolean hint) {
        Log.i(TAG, "setBasicContent " + basicContent.getBasicValute().getName() + " valutes=" + valuteRatesAdapter.getItemCount());
        valuteRatesAdapter.setBasicContent(basicContent);
        if (valuteRatesAdapter.getItemCount() > 0) {
            update();
        }
        if (hint) {
            Snackbar.make(rootView, getString(R.string.change_basic, basicContent.getBasicValute().getCharCode()), LENGTH_SHORT).show();
        }
    }

    @Override
    public void notifyMovedItems(int from, int to) {
        valuteRatesAdapter.notifyMovedItems(from, to);
    }

    public void update() {
        valuteRatesAdapter.notifyDataSetChanged();
    }

    public void bindDate(long date) {
        if (date == 0) {
            updateDate.setText(getString(R.string.update));
        } else {
            updateDate.setText(getString(R.string.updated, TimeFormatter.getInstance().getStringServTime(date)));
        }
    }

//    @Override
//    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
//        mItemTouchHelper.startDrag(viewHolder);
//    }
}
