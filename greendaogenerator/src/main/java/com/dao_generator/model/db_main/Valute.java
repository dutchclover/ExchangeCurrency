package com.dao_generator.model.db_main;


import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;

/**
 * Created by dimon for Snaappy.
 */
public class Valute {
    public static Entity addSchema(Schema pSchema, Entity coursePosition) {
        Entity valute = pSchema.addEntity(Valute.class.getSimpleName());
        Property primaryKey = valute.addLongProperty("numCode").primaryKey().getProperty();
        valute.addStringProperty("id");
        valute.addStringProperty("charCode");
        valute.addStringProperty("nominal");
        valute.addStringProperty("name");
        valute.addDoubleProperty("value");
        valute.addToOne(coursePosition, primaryKey, "position");
        valute.implementsInterface("Parcelable");
        return valute;
    }
}
