package com.dao_generator;


import com.dao_generator.model.db_main.BankCourse;
import com.dao_generator.model.db_main.CryptoMarketCurrencyInfo;
import com.dao_generator.model.db_main.Valute;
import com.dao_generator.model.db_main.ValutePosition;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

import java.io.IOException;


public class GreenDaoGenerator {

    private static final int dbSchemaVersion = 3;

    private static final String dbPackage = "com.dgroup.exchangerates.data.model.db";

    public static void main(String[] args) throws Exception {
        System.out.println("main " + args[0]);
        Schema data1Schema = new Schema(dbSchemaVersion, dbPackage);
        data1Schema.enableKeepSectionsByDefault();

        addDataMainDb(data1Schema);

        try {
            DaoGenerator gen = new DaoGenerator();

            gen.generateAll(data1Schema, args[0]);

            System.out.println("Successfully generated all files to: " + args[0]);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception error: " + e.getMessage());
        }
    }

    private static void addDataMainDb(Schema schema) {
        Entity valutePos = ValutePosition.addSchema(schema);
        Entity valute = Valute.addSchema(schema, valutePos);
        Entity bankCourse = BankCourse.addSchema(schema);
        Entity cmcInfo = CryptoMarketCurrencyInfo.addSchema(schema);
    }
}
