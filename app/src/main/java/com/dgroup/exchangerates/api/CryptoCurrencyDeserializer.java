package com.dgroup.exchangerates.api;


import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;



public class CryptoCurrencyDeserializer implements JsonDeserializer<CryptoMarketCurrencyInfo> {


    @Override
    public CryptoMarketCurrencyInfo deserialize(JsonElement jsonElement, Type typeOf,
                                                JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        CryptoMarketCurrencyInfo info;

        JsonObject rootOject = jsonElement.getAsJsonObject();

        String coinId = rootOject.get("id").getAsString();
        String name = rootOject.get("name").getAsString();
        String symbol = rootOject.get("symbol").getAsString();
        int rank = rootOject.get("rank").getAsInt();
        //String icon = "https://s2.coinmarketcap.com/static/img/coins/32x32/" + coinId + ".png";
        String icon = "https://raw.githubusercontent.com/cjdowner/cryptocurrency-icons/master/32/icon/"+symbol.toLowerCase()+".png";
        float price_usd = rootOject.get("price_usd").getAsFloat();
        float price_btc = rootOject.get("price_btc").getAsFloat();
        float volume_usd_24h = rootOject.get("24h_volume_usd").getAsFloat();
        double market_cap_usd = rootOject.get("market_cap_usd").getAsDouble();
        double available_supply = rootOject.get("available_supply").getAsDouble();
        double total_supply = 0;
        JsonElement element = rootOject.get("total_supply");
        if (!element.isJsonNull()) {
            total_supply = rootOject.get("total_supply").getAsDouble();
        }
        double max_supply = 0;
        JsonElement element2 = rootOject.get("max_supply");
        if (!element2.isJsonNull()) {
            max_supply = rootOject.get("max_supply").getAsDouble();
        }
        float percent_change_1h = 0;
        JsonElement j_percent_change_1h = rootOject.get("percent_change_1h");
        if(!j_percent_change_1h.isJsonNull()) {
            percent_change_1h = rootOject.get("percent_change_1h").getAsFloat();
        }
        float percent_change_24h = 0;
        JsonElement j_percent_change_24h = rootOject.get("percent_change_24h");
        if(!j_percent_change_24h.isJsonNull()) {
            percent_change_24h = rootOject.get("percent_change_24h").getAsFloat();
        }
        float percent_change_7d = 0;
        JsonElement j_percent_change_7d = rootOject.get("percent_change_7d");
        if(!j_percent_change_7d.isJsonNull()) {
            percent_change_7d = rootOject.get("percent_change_7d").getAsFloat();
        }
        long last_updated = rootOject.get("last_updated").getAsLong();

        info = new CryptoMarketCurrencyInfo(
                (long) coinId.hashCode(), coinId, name, symbol, rank, icon, price_usd, price_btc,
                volume_usd_24h, market_cap_usd, available_supply, total_supply, max_supply,
                percent_change_1h, percent_change_24h, percent_change_7d, last_updated, null
        );

        return info;
    }

}
