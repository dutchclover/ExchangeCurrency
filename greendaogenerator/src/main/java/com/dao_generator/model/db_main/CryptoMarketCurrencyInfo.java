package com.dao_generator.model.db_main;

import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;

/**
 * Created by dimon on 12/13/17.
 */

public class CryptoMarketCurrencyInfo {
    public static Entity addSchema(Schema pSchema) {
        Entity cmcInfo = pSchema.addEntity(CryptoMarketCurrencyInfo.class.getSimpleName());
        cmcInfo.addIdProperty().notNull();
        cmcInfo.addStringProperty("coinId");
        cmcInfo.addStringProperty("name");
        cmcInfo.addStringProperty("symbol");
        cmcInfo.addIntProperty("rank").notNull();
        cmcInfo.addStringProperty("icon");
        cmcInfo.addFloatProperty("price_usd").notNull();
        cmcInfo.addFloatProperty("price_btc").notNull();
        cmcInfo.addFloatProperty("volume_usd_24h").notNull();
        cmcInfo.addDoubleProperty("market_cap_usd").notNull();
        cmcInfo.addDoubleProperty("available_supply").notNull();
        cmcInfo.addDoubleProperty("total_supply").notNull();
        cmcInfo.addDoubleProperty("max_supply").notNull();
        cmcInfo.addFloatProperty("percent_change_1h").notNull();
        cmcInfo.addFloatProperty("percent_change_24h").notNull();
        cmcInfo.addFloatProperty("percent_change_7d").notNull();
        cmcInfo.addLongProperty("last_updated").notNull();
        cmcInfo.addStringProperty("graph");
        return cmcInfo;
    }
}