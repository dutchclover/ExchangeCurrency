package com.dgroup.exchangerates.utils;

import com.dgroup.exchangerates.data.model.db.BankCourse;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;

import java.util.Comparator;

/**
 * ololo Created by dimon for Snaappy .
 */

public class SortUtils {

    public enum BanksSortVariant {
        ALPHABET, USD_BUY, USD_SELL, EUR_BUY, EUR_SELL;

        public static BanksSortVariant getByOrder(int pos) {
            switch (pos) {
                case 1:
                    return USD_BUY;
                case 2:
                    return USD_SELL;
                case 3:
                    return EUR_BUY;
                case 4:
                    return EUR_SELL;
                default:
                    return ALPHABET;
            }
        }
    }

    public enum CryptoSortVariant {
        CAPITAL, COST, VOLATILE_1H, VOLATILE_24H, VOLATILE_7D;

        public static CryptoSortVariant getByOrder(int pos) {
            switch (pos) {
                case 1:
                    return CAPITAL;
                case 2:
                    return COST;
                case 3:
                    return VOLATILE_1H;
                case 4:
                    return VOLATILE_24H;
                case 5:
                    return VOLATILE_7D;
                default:
                    return CAPITAL;
            }
        }
    }

    public static Comparator<BankCourse> getBanksComparator(BanksSortVariant banksSortVariant) {
        switch (banksSortVariant) {
            case USD_BUY:
                return getCustomSort((o1, o2) -> Float.compare(o2.getUsdBuy(), o1.getUsdBuy()));
            case USD_SELL:
                return getCustomSort((o1, o2) -> Float.compare(o1.getUsdSell(), o2.getUsdSell()));
            case EUR_BUY:
                return getCustomSort((o1, o2) -> Float.compare(o2.getEurBuy(), o1.getEurBuy()));
            case EUR_SELL:
                return getCustomSort((o1, o2) -> Float.compare(o1.getEurSell(), o2.getEurSell()));
            default:
                return getCustomSort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        }
    }

    private static Comparator<BankCourse> getCustomSort(Comparator<BankCourse> customCompare) {
        return (o1, o2) -> {
            int compare = Boolean.compare(o2.getActual(), o1.getActual());
            if (compare == 0) {
                return customCompare.compare(o1, o2);
            } else {
                return compare;
            }
        };
    }

    public static Comparator<CryptoMarketCurrencyInfo> getCryptoComparator(CryptoSortVariant cryptoSortVariant) {
        switch (cryptoSortVariant) {
            case CAPITAL:
                return (o1, o2) -> Double.compare(o2.getMarket_cap_usd(), o1.getMarket_cap_usd());
            case COST:
                return (o1, o2) -> Float.compare(o2.getPrice_usd(), o1.getPrice_usd());
            case VOLATILE_1H:
                return (o1, o2) -> Float.compare(o2.getPercent_change_1h(), o1.getPercent_change_1h());
            case VOLATILE_24H:
                return (o1, o2) -> Float.compare(o2.getPercent_change_24h(), o1.getPercent_change_24h());
            case VOLATILE_7D:
                return (o1, o2) -> Float.compare(o2.getPercent_change_7d(), o1.getPercent_change_7d());
            default:
                return (o1, o2) -> o1.getName().compareTo(o2.getName());
        }
    }
}
