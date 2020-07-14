package com.dgroup.exchangerates.features.debug;

import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dgroup.exchangerates.R;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;



public class DebugFragment extends Fragment{

    private static final String TAG = "DebugFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_debug, container, false);

        View btn1 = root.findViewById(R.id.test1);
        View btn2 = root.findViewById(R.id.test2);
        View btn3 = root.findViewById(R.id.test3);
        View btn4 = root.findViewById(R.id.test4);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFromWeb().startWith(getFromDB()).startWith(getFromMem().onErrorResumeNext(throwable -> Observable.empty())).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG,"result "+integer);
                    }
                });
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEmpty().isEmpty().map(new Func1<Boolean, String>() {
                    @Override
                    public String call(Boolean integer) {
                        return "integer exist "+(integer!=null);
                    }
                }).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i(TAG,"s "+s);
                    }
                });
            }
        });

        return root;
    }

    private Observable<Integer> getEmpty(){
        return Observable.empty();
    }


    private Observable<Integer> getFromWeb(){
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Log.i(TAG,"getFromWeb");
                SystemClock.sleep(1000);
                return 100;
            }
        });
    }

    private Observable<Integer> getFromDB(){
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Log.i(TAG,"getFromDB");
                SystemClock.sleep(1000);
                return 50;
            }
        });
    }

    private Observable<Integer> getFromMem(){
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Log.i(TAG,"getFromMem");
                throw new RuntimeException();
              //  return 1;
            }
        });
    }

}
