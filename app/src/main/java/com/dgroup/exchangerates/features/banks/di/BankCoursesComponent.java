package com.dgroup.exchangerates.features.banks.di;

import com.dgroup.exchangerates.features.banks.BankCoursesFragment;
import com.dgroup.exchangerates.utils.FragmentScoped;

import dagger.Subcomponent;


@FragmentScoped
@Subcomponent(modules = {BankCoursesPresenterModule.class})
public interface BankCoursesComponent {

    void inject(BankCoursesFragment bankCoursesFragment);

}
