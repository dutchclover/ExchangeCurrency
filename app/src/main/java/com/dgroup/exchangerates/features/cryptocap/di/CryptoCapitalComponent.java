package com.dgroup.exchangerates.features.cryptocap.di;

import com.dgroup.exchangerates.features.cryptocap.CMCapitalFragment;
import com.dgroup.exchangerates.utils.FragmentScoped;

import dagger.Subcomponent;

@FragmentScoped
@Subcomponent(modules = {CryptoCapitalModule.class})
public interface CryptoCapitalComponent {

    void inject(CMCapitalFragment cmCapitalFragment);
}
