package com.dao_generator.model.db_main;

import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

/**
 * Created by dimon on 25.08.17.
 */

public class ValutePosition {
    public static Entity addSchema(Schema pSchema) {
        Entity coursePosition = pSchema.addEntity(ValutePosition.class.getSimpleName());
        coursePosition.addIdProperty();
        coursePosition.addIntProperty("position").notNull();
        return coursePosition;
    }
}
