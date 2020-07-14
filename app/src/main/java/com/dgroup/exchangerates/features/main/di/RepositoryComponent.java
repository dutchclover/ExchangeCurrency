package com.dgroup.exchangerates.features.main.di;

import com.dgroup.exchangerates.features.banks.di.BankCoursesComponent;
import com.dgroup.exchangerates.features.banks.di.BankCoursesPresenterModule;
import com.dgroup.exchangerates.features.banks.di.BanksRepositoryModule;
import com.dgroup.exchangerates.features.cb_rates.di.BasicContentModule;
import com.dgroup.exchangerates.features.cb_rates.di.CBRatesComponent;
import com.dgroup.exchangerates.features.cb_rates.di.CBRatesPresenterModule;
import com.dgroup.exchangerates.features.cb_rates.di.CBRepositoryModule;
import com.dgroup.exchangerates.features.cryptocap.di.CryptoCapitalComponent;
import com.dgroup.exchangerates.features.cryptocap.di.CryptoCapitalModule;
import com.dgroup.exchangerates.utils.ActivityScoped;

import dagger.Subcomponent;


@ActivityScoped
@Subcomponent(modules = {BanksRepositoryModule.class, CBRepositoryModule.class})
public interface RepositoryComponent {

    BankCoursesComponent plus(BankCoursesPresenterModule bankCoursesPresenterModule);
    CBRatesComponent plus(CBRatesPresenterModule bankCoursesPresenterModule);
    CryptoCapitalComponent plus(CryptoCapitalModule cryptoCapitalModule);
}
