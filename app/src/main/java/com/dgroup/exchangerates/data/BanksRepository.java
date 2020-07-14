package com.dgroup.exchangerates.data;

import android.util.Log;

import com.dgroup.exchangerates.api.Api;
import com.dgroup.exchangerates.data.db_wrapper.DaoTaskMain;
import com.dgroup.exchangerates.data.model.Provider;
import com.dgroup.exchangerates.data.model.db.BankCourse;
import com.dgroup.exchangerates.data.model.db.BankCourseDao;
import com.dgroup.exchangerates.utils.ActivityScoped;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.exceptions.Exceptions;

import static com.dgroup.exchangerates.utils.SortUtils.BanksSortVariant.ALPHABET;
import static com.dgroup.exchangerates.utils.SortUtils.getBanksComparator;
import static com.dgroup.exchangerates.utils.Utils.getDefaultCity;

//todo refactoring: repo should not implement Interactor
@ActivityScoped
public class BanksRepository extends Interactor<List<BankCourse>, BanksRepository.Params> {

    private final Observable<Api> apiProvider;

    @Inject
    public BanksRepository(Observable<Api> apiProvider, Executor multiExecutor) {
        super(multiExecutor);
        this.apiProvider = apiProvider.cache();
        apiProvider.cache();
    }

    @Override
    protected Observable<List<BankCourse>> buildObservable(Params parameter) {
        Log.i("BanksRepository","buildObservable comparator "+parameter.mCourseComparator.hashCode()+" "+Thread.currentThread().getName());
        if (parameter.provider.equals(Provider.WEB)) {
            return loadBanksFromWeb(parameter.cityCode, parameter.actual).
                    map(bankCourses -> {
                        Collections.sort(bankCourses, parameter.mCourseComparator);
                        return bankCourses;
                    });
        } else {
            return loadBanksFromDB(parameter.cityCode, parameter.actual).
                    map(bankCourses -> {
                        Collections.sort(bankCourses, parameter.mCourseComparator);
                        return bankCourses;
                    }).cache();
        }
    }

    private Observable<List<BankCourse>> loadBanksFromDB(final int cityCode, final boolean actual) {
        return Observable.create(new Observable.OnSubscribe<List<BankCourse>>() {
            @Override
            public void call(Subscriber<? super List<BankCourse>> subscriber) {
                try {
                    Log.i("loadBanksFromDB", "thread = " + Thread.currentThread().getName());
                    ArrayList<BankCourse> courses = new ArrayList<>(
                            DaoTaskMain.getSession().getBankCourseDao().
                                    queryBuilder().
                                    orderAsc(BankCourseDao.Properties.Name).
                                    where(BankCourseDao.Properties.City.eq(cityCode)).
                                    list());
                    subscriber.onNext(courses);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    private Observable<List<BankCourse>> loadBanksFromWeb(int cityCode, boolean actual) {
        return apiProvider.map(api -> {
            try {
                List<BankCourse> banksCourses = api.getBankCourses(cityCode, actual);
                Log.i("BanksRepository"," banksCourses getEurBuy "+banksCourses.get(0).getEurBuy());
                if (banksCourses.size() > 0) {
                    DaoTaskMain.getSession().getBankCourseDao().deleteAll();
                    DaoTaskMain.getSession().getBankCourseDao().insertOrReplaceInTx(banksCourses);
                }
                return banksCourses;
            } catch (Throwable e) {
                e.printStackTrace();
                throw Exceptions.propagate(e);
            }
        });
    }

    public static class Params {

        private Comparator<BankCourse> mCourseComparator;

        private Provider provider;
        private boolean actual;
        private int cityCode;

        public Params() {
            this.provider = Provider.DB;
            cityCode = getDefaultCity();
            actual = false;
            mCourseComparator = getBanksComparator(ALPHABET);
        }

        public Params(Provider provider) {
            this();
            this.provider = provider;
        }

        public Params(Provider provider, int city) {
            this(provider);
            this.cityCode = city;
        }

        public Params(Provider provider, int city, boolean actual) {
            this(provider, city);
            this.actual = actual;
        }

        public void setCity(int cityCode) {
            this.cityCode = cityCode;
        }

        public void setProvider(Provider provider) {
            this.provider = provider;
        }

        public void setActual(boolean actual) {
            this.actual = actual;
        }

        public boolean isDb() {
            return provider == Provider.DB;
        }

        public void setCourseComparator(Comparator<BankCourse> courseComparator) {
            mCourseComparator = courseComparator;
        }
    }
}
