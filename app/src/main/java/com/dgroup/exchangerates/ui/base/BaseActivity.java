package com.dgroup.exchangerates.ui.base;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.dgroup.exchangerates.R;

/**
 * Created by dimon on 07.09.17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Bitmap recentsIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_white_192);
            ActivityManager.TaskDescription tDesc =
                    new ActivityManager.TaskDescription(
                            getString(R.string.app_name), recentsIcon, ContextCompat.getColor(this, R.color.app_theme));
            setTaskDescription(tDesc);
        }
    }
}
