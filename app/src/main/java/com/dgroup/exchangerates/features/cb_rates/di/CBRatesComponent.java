package com.dgroup.exchangerates.features.cb_rates.di;


import com.dgroup.exchangerates.features.cb_rates.ui.CBRatesFragment;
import com.dgroup.exchangerates.utils.ActivityScoped;

import dagger.Subcomponent;

@ActivityScoped
@Subcomponent(modules = {CBRatesPresenterModule.class})
public interface CBRatesComponent {
    void inject(CBRatesFragment CBRatesFragment);
}
