package com.dgroup.exchangerates.features.cryptocap;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.dgroup.exchangerates.features.cryptocap.view.CryptoCapitalView;
import com.dgroup.exchangerates.features.main.MainActivity;
import com.dgroup.exchangerates.utils.TimeFormatter;
import com.inqbarna.tablefixheaders.TableFixHeaders;

import java.util.List;

import javax.inject.Inject;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;




public class CMCapitalFragment extends Fragment implements CryptoCapitalView {

    private TableFixHeaders tableFixHeaders;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView updateDate;

    private TableAdapter mTableAdapter;

    @Inject
    protected CryptoCapitalPresenter mCryptoCapitalPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExRatesApp.getApp().plusCryptoCapitalComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_crypto_top, container, false);
        View menu = inflater.inflate(R.layout.toolbar_crypto_cap, null);
        initMenu(menu);
        updateDate = (TextView) root.findViewById(R.id.date);
        mSwipeRefreshLayout = root.findViewById(R.id.swipe_layout);
        tableFixHeaders = root.findViewById(R.id.table);
        mSwipeRefreshLayout.setOnChildScrollUpCallback((parent, child) -> !tableFixHeaders.isTopVisible());
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCryptoCapitalPresenter.loadActualRates();
            }
        });
        mTableAdapter = new TableAdapter(getActivity(), mCryptoCapitalPresenter.getChangePricePosition());
        mTableAdapter.setOnChancePriceClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pum = new PopupMenu(v.getContext(), v);
                pum.inflate(R.menu.menu_change_rates);
                pum.getMenu().setGroupCheckable(R.id.sort_group,true, true);
                pum.getMenu().getItem(mCryptoCapitalPresenter.getChangePricePosition()).setChecked(true);
                pum.setOnMenuItemClickListener(item -> {
                    item.setChecked(true);
                    mCryptoCapitalPresenter.setChangePricePosition(item.getOrder());
                    mTableAdapter.setChangePriceInterval(item.getOrder());
                    return false;
                });
                pum.show();
            }
        });
        tableFixHeaders.setAdapter(mTableAdapter);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCryptoCapitalPresenter.subscribe(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCryptoCapitalPresenter.unsubscribe();
    }

    @Override
    public void showContent(List<CryptoMarketCurrencyInfo> content, long date) {
        mTableAdapter.setContent(content);
        if(date!=0) {
            updateDate.setText(getString(R.string.updated, TimeFormatter.getInstance().getStringServTime(date)));
        }else{
            updateDate.setText(getString(R.string.update));
        }
    }

    @Override
    public void showError(Throwable e) {
        mSwipeRefreshLayout.setRefreshing(false);
        Snackbar.make(getView(), getString(R.string.loading_error), LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(boolean show) {
        mSwipeRefreshLayout.setRefreshing(show);
    }

    private void initMenu(View menu){
        ((MainActivity) getActivity()).invalidateMenu(menu);
    }

}
