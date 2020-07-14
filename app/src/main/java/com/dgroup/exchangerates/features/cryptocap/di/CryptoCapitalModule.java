package com.dgroup.exchangerates.features.cryptocap.di;


import com.dgroup.exchangerates.features.cryptocap.domain.CryptoCurrencyRepo;
import com.dgroup.exchangerates.features.cryptocap.domain.CryptoMarketInteractor;
import com.dgroup.exchangerates.utils.FragmentScoped;
import java.util.concurrent.Executor;
import javax.inject.Named;
import dagger.Module;
import dagger.Provides;



@Module
public class CryptoCapitalModule {

    @Provides
    @FragmentScoped
    CryptoMarketInteractor provideParser(@Named("MultiThread") Executor executor, CryptoCurrencyRepo cryptoCurrencyRepo){
        return new CryptoMarketInteractor(executor, cryptoCurrencyRepo);
    }
}
