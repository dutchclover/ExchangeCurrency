package com.dgroup.exchangerates.utils;

import android.util.Log;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.model.BanksCourses;
import com.dgroup.exchangerates.data.model.ServBankCourse;
import com.dgroup.exchangerates.data.model.db.BankCourse;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.dgroup.exchangerates.data.model.db.Valute;
import com.dgroup.exchangerates.data.model.db.ValutePosition;
import com.dgroup.exchangerates.features.cryptocap.data.CryptoCurrencyHeader;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;

import java.util.List;

import static com.dgroup.exchangerates.utils.constants.TinyDbKeys.KEYS.CRYPTO_LAST_UPDATED;

/**
 * Created by gabber on 15.10.16.
 */

public class ValUtils {

    public static Valute createBasicValute(String charCode) {
        return createBasicValute(null, charCode);
    }

    public static Valute createBasicValute(String name, String charCode) {
        Valute valute = new Valute();
        valute.setNumCode(-1L);
        valute.setCharCode(charCode);
        valute.setId("-1");
        valute.setValue(1d);
        valute.setNominal("1");
        if (name != null) {
            valute.setName(name);
        } else {
            switch (charCode) {
                case "RUB":
                    valute.setName(ExRatesApp.getApp().getResources().getString(R.string.RUB));
                    break;
                case "USD":
                    valute.setName(ExRatesApp.getApp().getResources().getString(R.string.USD));
                    break;
                case "EUR":
                    valute.setName(ExRatesApp.getApp().getResources().getString(R.string.EUR));
                    break;
            }
        }
        ValutePosition valutePosition = new ValutePosition(valute.getNumCode());
        valutePosition.setPosition(0);
        valute.setPosition(valutePosition);
        return valute;
    }

    public static void copyCourses(BanksCourses banksCourses, List<BankCourse> dbModelCourses, int cityCode, boolean actual) {
        Log.i("ValUtils", "copyCourses actual " + banksCourses.getActualCourses().size() + " non actual " +
                (banksCourses.getNonActualCourses() != null ? banksCourses.getNonActualCourses().size() : "is null"));
        for (int i = 0; i < banksCourses.getActualCourses().size(); i++) {
            ServBankCourse currentCourse = banksCourses.getActualCourses().get(i);
            BankCourse bankCourse = BankCourse.CreateFromServerCourse(currentCourse, cityCode, true);
            dbModelCourses.add(bankCourse);
        }
        if (!actual && banksCourses.getNonActualCourses() != null) {
            for (int i = 0; i < banksCourses.getNonActualCourses().size(); i++) {
                ServBankCourse currentCourse = banksCourses.getNonActualCourses().get(i);
                BankCourse bankCourse = BankCourse.CreateFromServerCourse(currentCourse, cityCode, false);
                dbModelCourses.add(bankCourse);
            }
        }
    }
}
