package com.dao_generator.model.db_main;

import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;

/**
 * ololo Created by dimon for Snaappy .
 */

public class BankCourse {
    public static Entity addSchema(Schema pSchema) {
        Entity bankCourse = pSchema.addEntity(BankCourse.class.getSimpleName());
        bankCourse.addIdProperty();
        bankCourse.addStringProperty("name").notNull();
        bankCourse.addFloatProperty("usdBuy").notNull();
        bankCourse.addFloatProperty("usdSell").notNull();
        bankCourse.addFloatProperty("eurBuy").notNull();
        bankCourse.addFloatProperty("eurSell").notNull();
        bankCourse.addLongProperty("timestamp").notNull();
        bankCourse.addStringProperty("city").notNull();
        bankCourse.addBooleanProperty("actual").notNull();
        bankCourse.addStringProperty("desc");
        return bankCourse;
    }
}
