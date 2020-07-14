package com.dgroup.exchangerates.features.banks;

import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.BanksRepository;
import com.dgroup.exchangerates.data.model.db.BankCourse;
import com.dgroup.exchangerates.ui.base.BasePresenter;
import com.dgroup.exchangerates.ui.view.custom.SearchBar;
import com.dgroup.exchangerates.utils.SortUtils;
import com.dgroup.exchangerates.utils.Utils;
import com.dgroup.exchangerates.utils.constants.TinyDbKeys;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.dgroup.exchangerates.data.BanksRepository.Params;
import static com.dgroup.exchangerates.data.model.Provider.DB;
import static com.dgroup.exchangerates.data.model.Provider.WEB;
import static com.dgroup.exchangerates.utils.SortUtils.BanksSortVariant.ALPHABET;
import static com.dgroup.exchangerates.utils.SortUtils.getBanksComparator;
import static com.dgroup.exchangerates.ui.view.custom.SearchBar.SearchMode.SEARCH_BANK;
import static com.dgroup.exchangerates.ui.view.custom.SearchBar.SearchMode.SEARCH_CITY;
import static com.dgroup.exchangerates.utils.TimeFormatter.HOUR;
import static com.dgroup.exchangerates.utils.Utils.getStringFirstUpperCase;
import static com.dgroup.exchangerates.utils.constants.TinyDbKeys.KEYS.LAST_BANK_COURSES_UPDATE;


public class BankCoursesPresenter extends BasePresenter<BankCoursesMvpView, BanksRouter> implements SearchBar.OnSearchListener {

    private static final String TAG = BankCoursesPresenter.class.getSimpleName();

    private final BanksRepository mBanksRepository;
    private List<BankCourse> mBankCourses;
    private SortUtils.BanksSortVariant mBanksSortVariant;
    private Params mParams;
    private SearchBar.SearchMode mSearchMode;
    private int currentCityCode;

    private Observable<Map<String, Integer>> cityCodesObsrv;
    private Subscription cityCodesSubscription;

    @Inject
    public BankCoursesPresenter(BanksRepository banksRepository, Observable<Map<String, Integer>> cityCodesObsrv) {
//        Log.i(TAG, "constructor");
        this.mBanksRepository = banksRepository;
        this.cityCodesObsrv = cityCodesObsrv.cache();
        mBankCourses = new ArrayList<>();
        mBanksSortVariant = ALPHABET;
        currentCityCode = TinyDbWrap.getInstance().getInt(TinyDbKeys.KEYS.SAVED_CITY_CODE, Utils.getDefaultCity());
        mParams = new Params(DB);
        mParams.setCity(currentCityCode);
    }

    public void subscribe(BankCoursesMvpView bankCoursesMvpView, BanksRouter banksRouter) {
        attachView(bankCoursesMvpView);
        setRouter(banksRouter);
        if (isViewAttached()) {
            getMvpView().setSearchCallback(this);
        } else {
            ExRatesApp.logException(new RuntimeException("subscribe isViewAttached false!"));
        }
        Log.i(TAG, "mBanksRepository exist = " + (mBanksRepository != null));
    }

    public void unSubscribe() {
        if (cityCodesSubscription != null) {
            cityCodesSubscription.unsubscribe();
        }
        if (isViewAttached()) {
            getMvpView().setSearchCallback(null);
        } else {
            ExRatesApp.logException(new RuntimeException("unSubscribe isViewAttached false!"));
        }
        mBanksRepository.unSubscribe();
        setRouter(null);
        detachView();
    }

    public void updateBanksData() {
        if (isViewAttached()) {
            mParams.setProvider(WEB);
            getMvpView().showLoading();
            loadBanksData(mParams);
        }
    }

    public void loadBanksData() {
        mParams.setProvider(DB);
        loadBanksData(mParams);
        getMvpView().showLoading();
    }

    public void loadBanksData(final Params params) {
        mBanksRepository.execute(params, new Subscriber<List<BankCourse>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if (isViewAttached()) {
                    getMvpView().showError();
                }
            }

            @Override
            public void onNext(List<BankCourse> bankCourses) {
                Log.i(TAG, "loadbankCourses db " + params.isDb() + " " + bankCourses.size());
                if (bankCourses.size() > 0) {
                    mBankCourses = bankCourses;
                    if (isViewAttached()) {
                        getMvpView().showData(mBankCourses);
                    }
                }
                if (params.isDb()) {
                    if (bankCourses.size() == 0 ||
                            System.currentTimeMillis() - TinyDbWrap.getInstance().getLong(LAST_BANK_COURSES_UPDATE, 0) > HOUR) {
                        params.setProvider(WEB);
                        loadBanksData(params);
                    }
                } else {
                    TinyDbWrap.getInstance().putLong(LAST_BANK_COURSES_UPDATE, System.currentTimeMillis());
                }
            }
        });
    }

    public void setBanksSortVariant(int pos) {
        Log.i(TAG, "setBanksSortVariant " + pos);
        mBanksSortVariant = SortUtils.BanksSortVariant.getByOrder(pos);
        resort();
    }

    private void resort() {
        mParams.setProvider(DB);
        mParams.setCourseComparator(getBanksComparator(mBanksSortVariant));
        loadBanksData(mParams);
    }

    public int getBanksSortVariant() {
        Log.i(TAG, "getBanksSortVariant " + mBanksSortVariant);
        return mBanksSortVariant.ordinal();
    }

    public void openBankSearch(String name) {
        Log.i(BankCoursesFragment.class.getSimpleName(), "click " + name);
        getRouter().openBankSearch(name);
    }

    @Override
    public void searchRequest(CharSequence s) {
        if (mSearchMode != null) {
            switch (mSearchMode) {
                case SEARCH_BANK:
                    getMvpView().filterBanks(s);
                    break;
//            case SEARCH_CITY:
//                getMvpView().filterCities(s);
//                break;
            }
        }
    }

    @Override
    public void onItemSelect(String city) {
        Log.i(TAG, "onItemSelect " + city);
        if (mSearchMode == SEARCH_BANK) {
            return;
        }
        cityCodesSubscription = cityCodesObsrv.
                observeOn(AndroidSchedulers.mainThread()).
                map(stringIntegerMap -> {
                    String preparedCity = getStringFirstUpperCase(city);
                    Integer code = stringIntegerMap.get(preparedCity);
                    if (code != null) {
                        TinyDbWrap.getInstance().putInt(TinyDbKeys.KEYS.SAVED_CITY_CODE, code);
                        TinyDbWrap.getInstance().putString(TinyDbKeys.KEYS.SAVED_CITY_NAME, preparedCity);
                    } else {
                        throw new NullPointerException();
                    }
                    return code;
                }).
                subscribe(code -> {
                    Log.i(TAG, "onItemSelect cityCodesObsrv " + code + " " + Thread.currentThread().getName());
                    getMvpView().showCity(getStringFirstUpperCase(city));
                    mParams.setCity(code);
                    if (currentCityCode != code) {
                        currentCityCode = code;
                        updateBanksData();
                    }
                }, throwable -> {
                    Log.i(TAG, "onItemSelect NOT FOUND city " + city.trim() + " " + Thread.currentThread().getName());
                    Toast.makeText(ExRatesApp.getApp(), ExRatesApp.getApp().getString(R.string.city_not_found), Toast.LENGTH_SHORT).show();
                });
    }

    public void startCitySearch(Pair<Integer, Integer> coords, String hint) {
        mSearchMode = SEARCH_CITY;
        if (isViewAttached()) {
            getMvpView().showSearch(coords, hint, true);
        }
    }

    public void startBankSearch(Pair<Integer, Integer> coords, String hint) {
        mSearchMode = SEARCH_BANK;
        if (isViewAttached()) {
            getMvpView().showSearch(coords, hint, false);
        }
    }

}
