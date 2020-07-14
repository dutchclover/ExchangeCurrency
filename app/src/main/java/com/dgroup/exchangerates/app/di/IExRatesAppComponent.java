package com.dgroup.exchangerates.app.di;

import com.dgroup.exchangerates.features.banks.di.BanksRepositoryModule;
import com.dgroup.exchangerates.features.cb_rates.di.BasicContentModule;
import com.dgroup.exchangerates.features.cb_rates.di.CBRepositoryModule;
import com.dgroup.exchangerates.features.cb_rates.ui.KeyboardActivity;
import com.dgroup.exchangerates.features.main.di.MainNavigationComponent;
import com.dgroup.exchangerates.features.main.di.MainNavigationModule;
import com.dgroup.exchangerates.features.main.di.RepositoryComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, ApiModule.class, BasicContentModule.class})
public interface IExRatesAppComponent {
    void inject(KeyboardActivity keyboardActivity);

    RepositoryComponent plus(BanksRepositoryModule banksRepositoryModule, CBRepositoryModule cbRepositoryModule);
    MainNavigationComponent plus(MainNavigationModule mainNavigationModule);
}
