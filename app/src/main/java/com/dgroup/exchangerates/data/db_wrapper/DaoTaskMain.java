package com.dgroup.exchangerates.data.db_wrapper;

import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.dgroup.exchangerates.BuildConfig;
import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfoDao;
import com.dgroup.exchangerates.data.model.db.DaoMaster;
import com.dgroup.exchangerates.data.model.db.DaoSession;
import com.dgroup.exchangerates.utils.constants.Constants;
import com.dgroup.exchangerates.utils.constants.TinyDbKeys;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;

import org.greenrobot.greendao.database.Database;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class DaoTaskMain extends Task {
    /**
     * Session for dao-related tasks.
     */
    private static DaoSession sDaoSession;

    private Message msg;

    public DaoTaskMain(){}

    public DaoTaskMain(Message msg){
        this.msg = msg;
    }

    private void createSession() throws Throwable {
        createSession(Constants.DB_MAIN);

        sDaoSession.callInTx(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    onExecuteBackground(sDaoSession);
                } catch (Exception | Error e) {
                    throw e;
                } catch (Throwable ignored) {
                    //can't happen
                }
                return null;
            }
        });
    }


    private void createSession(String db_name) throws Exception {

        final AtomicInteger _oldVersion = new AtomicInteger(0);
        final AtomicInteger _newVersion = new AtomicInteger(0);

        DaoMaster.OpenHelper helper = new DaoMaster.OpenHelper(ExRatesApp.getApp(), db_name, null) {
            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {
                try {
                    upgrade(db, oldVersion, newVersion);
                    _oldVersion.set(oldVersion);
                    _newVersion.set(newVersion);
                    Log.i("greenDao", "onUpgrade, old=" + oldVersion + ", new" + newVersion);
                    devUpgrade(this, db); //for development only!
                } catch (Exception ex) {

                    if(BuildConfig.IS_USE_CRASHLYTICS) {
                        Crashlytics.logException(ex);
                    } else {
                        ex.printStackTrace();
                    }
                }
            }

        };

        SQLiteDatabase database = null;
        Throwable cause = null;
        for (int i = 0; i < 3; i++) {
            try {
                database = helper.getWritableDatabase();
                break;
            } catch (Exception e) {
                Thread.sleep(1000);
                cause = e;
            }
        }
        if (database == null) {
            int restartCount = TinyDbWrap.getInstance().getInt(TinyDbKeys.KEYS.DB_ERROR,0);
            if (restartCount >= 3) {
                File databaseFile = ExRatesApp.getApp().getDatabasePath(Constants.DB_MAIN);
                databaseFile.delete();
                throw new IllegalStateException("Can't open database. File deleted", cause);
            } else {
                TinyDbWrap.getInstance().putInt(TinyDbKeys.KEYS.DB_ERROR, ++restartCount);
                UiThread.run(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                });
            }

            return;
        } else {
            TinyDbWrap.getInstance().putInt(TinyDbKeys.KEYS.DB_ERROR, 0);
        }

        try {
            DaoMaster daoMaster = new DaoMaster(database);

            sDaoSession = daoMaster.newSession();

        } catch (Throwable t) {
            Exception w = new RuntimeException("error in creating dao database = "
                    + database + " database.isDbLockedByCurrentThread() " + database.isDbLockedByCurrentThread(), t);
            if(BuildConfig.IS_USE_CRASHLYTICS) {
                Crashlytics.logException(w);
            } else {
                throw w;
            }

            database.close();
            throw t;
        }
        if (_newVersion.get() > 0) {
            sDaoSession.callInTxNoException(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    afterUpdate(sDaoSession, _oldVersion.get(), _newVersion.get());
                    return null;
                }
            });
        }
    }

    @Override
    protected final void onExecuteBackground() throws Throwable {
        createSession();
    }


    protected abstract void onExecuteBackground(DaoSession daoSession) throws Throwable;


    private static void upgrade(Database db, int oldVersion, int newVersion) throws Exception {
        if (newVersion != DaoMaster.SCHEMA_VERSION) {
            Exception w = new IllegalStateException("Wrong new version of database: " + newVersion + " (" +
                    DaoMaster.SCHEMA_VERSION + " expected)");
            if(BuildConfig.IS_USE_CRASHLYTICS) {
                Crashlytics.logException(w);
            } else {
                throw w;
            }
        }
        switch (oldVersion) {
            case 1:
                upgradeFrom1to2(db);
                break;
        }
    }

    private static void upgradeFrom1to2(Database db) {
        CryptoMarketCurrencyInfoDao.createTable(db, false);
    }

    private static void afterUpdate(DaoSession session, int oldVersion, int newVersion) {
//        switch (oldVersion) {
//        case 0:
//        case 1:
//            for (ThemeEntity entity : session.getThemeEntityDao().queryBuilder()
//                                             .where(ThemeEntityDao.Properties.Free.eq(false))
//                                             .list()) {
//                entity.setNeedPromote(true);
//                entity.update();
//            }
//
//        }
    }

    private static boolean putDefaultLongJsonValue(JSONObject jsonObject, String fieldName) throws JSONException {
        if (jsonObject.has(fieldName)) {
            try {
                jsonObject.getLong(fieldName);
            } catch (JSONException ignored){
                jsonObject.put(fieldName, 0);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onFailBackground(Throwable e) {
        if(BuildConfig.IS_USE_CRASHLYTICS) {
            Crashlytics.logException(e);
        } else {
            e.printStackTrace();
        }
    }

//    public static void execute(DaoTaskMain task) {
//        ExRatesApp.getApp().getBasicThreadPoolExecutor().execute(task);
//    }

    public static DaoSession getSession(){
        if(sDaoSession == null){
            DaoTaskMain daoTaskMain = new DaoTaskMain() {
                @Override
                protected void onExecuteBackground(DaoSession daoSession) throws Throwable {

                }
            };
            try {
                daoTaskMain.onExecuteBackground();
            }catch (Throwable e){e.printStackTrace();}

        }
        return sDaoSession;
    }

    public static void closeDB(){
        sDaoSession.getDatabase().close();
        sDaoSession = null;
    }

    public Message getMsg() {
        return msg;
    }

    public void devUpgrade(DaoMaster.OpenHelper helper, Database db) {
       /* if (Utils.isActivityForeground(ChatActivity.class
                .getName(), SnaappyApp.getInstance(), "abc")){
            Intent intent = new Intent(SnaappyApp.getInstance().getApplicationContext(), ChatActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
            SnaappyApp.getInstance().startActivity(mainIntent);
        }*/
        Log.d("abc", "onUpdate");
        DaoMaster.dropAllTables(db, true); // for development only
        helper.onCreate(db);// for development only
    }
}

