package com.dgroup.exchangerates.features.main.di;


import com.dgroup.exchangerates.features.main.MainActivity;
import com.dgroup.exchangerates.utils.ActivityScoped;

import dagger.Subcomponent;

@ActivityScoped
@Subcomponent(modules = MainNavigationModule.class)
public interface MainNavigationComponent {
    void inject(MainActivity mainRatesFragment);
}
