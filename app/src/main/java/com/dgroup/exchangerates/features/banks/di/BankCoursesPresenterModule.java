package com.dgroup.exchangerates.features.banks.di;

import com.dgroup.exchangerates.data.BanksRepository;
import com.dgroup.exchangerates.features.banks.BankCoursesPresenter;
import com.dgroup.exchangerates.utils.FragmentScoped;

import java.util.Map;

import dagger.Module;
import dagger.Provides;
import rx.Observable;


@Module
public class BankCoursesPresenterModule {

    @Provides
    @FragmentScoped
    public BankCoursesPresenter provideBankCoursesPresenter(BanksRepository banksRepository, Observable<Map<String, Integer>> codesObsrv) {
        return new BankCoursesPresenter(banksRepository, codesObsrv);
    }
}
