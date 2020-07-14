package com.dgroup.exchangerates.features.banks;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.model.db.BankCourse;
import com.dgroup.exchangerates.features.main.MainActivity;
import com.dgroup.exchangerates.features.main.MainMvpView;
import com.dgroup.exchangerates.ui.adapter.BanksCoursesAdapter;
import com.dgroup.exchangerates.ui.view.custom.SearchBar;
import com.dgroup.exchangerates.utils.Utils;
import com.dgroup.exchangerates.utils.constants.TinyDbKeys;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;


public class BankCoursesFragment extends Fragment implements BankCoursesMvpView {

    public static final String TAG = BankCoursesFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private BanksCoursesAdapter mAdapter;
    private RecyclerView.AdapterDataObserver mAdapterSearchObserver;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mSearchBtn;
    private View searchStub;
    private View cityLayout;
    private TextView cityText;
    private View cityStart;

    @Inject
    protected BankCoursesPresenter mBankCoursesPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate");
        ExRatesApp.getApp().plusBankCoursesComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_banks_courses, container, false);
        View menu = inflater.inflate(R.layout.toolbar_bank_courses, null);
        searchStub = rootView.findViewById(R.id.search_stub);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mBankCoursesPresenter.updateBanksData());
        mAdapter = new BanksCoursesAdapter(new ArrayList<>(),
                bankCourse -> mBankCoursesPresenter.openBankSearch(bankCourse.getName()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mAdapter));
        mAdapterSearchObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (mAdapter.getItemCount() == 0 && mAdapter.isSearchNow()) {
                    showStub(true);
                } else {
                    showStub(false);
                }
                super.onChanged();
            }
        };

        mAdapter.registerAdapterDataObserver(mAdapterSearchObserver);

        initMenu(menu);

        return rootView;
    }

    private void showStub(boolean isShow) {
        searchStub.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBankCoursesPresenter.subscribe(this, (BanksRouter) getActivity());
        mBankCoursesPresenter.loadBanksData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,"onDestroyView");
        mAdapter.unregisterAdapterDataObserver(mAdapterSearchObserver);
        mSwipeRefreshLayout.setOnRefreshListener(null);
        mBankCoursesPresenter.unSubscribe();
    }

    @Override
    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showData(List<BankCourse> courses) {
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.replaceData(courses);
    }

    @Override
    public void showError() {
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setSearchCallback(SearchBar.OnSearchListener searchCallback) {
        Log.i(TAG,"setSearchCallback "+searchCallback+" getActivity() exist "+(getActivity()!=null));
        Activity activity = getActivity();
        if(activity!=null) {
            ((MainActivity) activity).setSearchCallback(searchCallback);
        }
    }

    private void initMenu(View menu) {
        ((MainActivity) getActivity()).invalidateMenu(menu);
        cityLayout = menu.findViewById(R.id.city_container);
        cityStart = menu.findViewById(R.id.triangle);
        cityLayout.setOnClickListener(v -> {
            Pair<Integer, Integer> coords = Utils.getRevealStartCoords(cityStart);
            mBankCoursesPresenter.startCitySearch(coords, getString(R.string.search_city_hint));
        });

        cityText = menu.findViewById(R.id.city);
        cityText.setText(TinyDbWrap.getInstance().getString(TinyDbKeys.KEYS.SAVED_CITY_NAME, getString(R.string.def_city)));

        mSearchBtn = menu.findViewById(R.id.search);
        mSearchBtn.setOnClickListener(v -> {
            Pair<Integer, Integer> coords = Utils.getRevealStartCoords(v);
            mBankCoursesPresenter.startBankSearch(coords, getString(R.string.search_bank_hint));
        });

        View sort = menu.findViewById(R.id.sort);

        PopupMenu popupMenu = new PopupMenu(getActivity(), sort);
        popupMenu.inflate(R.menu.menu_sort);
        popupMenu.setOnMenuItemClickListener(item -> {
            item.setChecked(true);
            mBankCoursesPresenter.setBanksSortVariant(item.getOrder());
            return false;
        });

        popupMenu.getMenu().setGroupCheckable(R.id.sort_group,true, true);

        popupMenu.getMenu().getItem(mBankCoursesPresenter.getBanksSortVariant()).setChecked(true);
        sort.setOnClickListener(v -> popupMenu.show());

    }

    @Override
    public void filterBanks(CharSequence s) {
        Log.i(TAG,"filterBanks "+s);
        mAdapter.getFilter().filter(s);
    }

    @Override
    public void showCity(CharSequence s) {
        //mAdapter.getFilter().filter(s);
        cityText.setText(s);
        ((MainMvpView) getActivity()).hideSearch();
    }

    @Override
    public void showSearch(Pair<Integer, Integer> coords, String hint, boolean autoComplete) {
        ((MainMvpView) getActivity()).showSearch(coords, hint, autoComplete);
    }
}
