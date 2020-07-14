package com.dgroup.exchangerates.features.cb_rates.di;

import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.RatesManager;
import com.dgroup.exchangerates.data.db_wrapper.DaoTaskMain;
import com.dgroup.exchangerates.data.model.BasicContent;
import com.dgroup.exchangerates.data.model.db.Valute;
import com.dgroup.exchangerates.utils.ActivityScoped;
import com.dgroup.exchangerates.utils.constants.TinyDbKeys;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.dgroup.exchangerates.utils.ValUtils.createBasicValute;


@Module
public class BasicContentModule {

    public static final String DEFAULT_CODE = "RUB";

    @Provides
    @Singleton
    public BasicContent provideBasicContent(){
        return createBasicContent(DEFAULT_CODE);
    }

    private BasicContent createBasicContent(String defaultCode) {
        BasicContent basicContent = new BasicContent();
        long persistValuteCode = TinyDbWrap.getInstance().getLong(TinyDbKeys.KEYS.BASIC_VALUTE_CODE, 0);
        if (persistValuteCode != 0) {
            Valute valute = DaoTaskMain.getSession().getValuteDao().load(persistValuteCode);
            if (valute != null) {
                basicContent.setBasicValute(valute);
            }
        }
        if (basicContent.getBasicValute() == null) {
            basicContent.setBasicValute(createBasicValute(defaultCode));
            DaoTaskMain.getSession().getValutePositionDao().insertOrReplace(basicContent.getBasicValute().getPosition());
            DaoTaskMain.getSession().getValuteDao().insertOrReplace(basicContent.getBasicValute());
        }
        basicContent.setCharCodeBasicCount(
                TinyDbWrap.getInstance().getString(TinyDbKeys.KEYS.BASIC_CODE_COUNT_VALUTE,
                        basicContent.getBasicValute().getCharCode()));
        basicContent.setCount(TinyDbWrap.getInstance().getDouble(TinyDbKeys.KEYS.COUNT_VALUTE, 1000d));

        return basicContent;
    }
}
