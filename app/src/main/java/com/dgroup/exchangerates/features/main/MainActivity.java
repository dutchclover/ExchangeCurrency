package com.dgroup.exchangerates.features.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.model.db.Valute;
import com.dgroup.exchangerates.features.banks.BankCoursesFragment;
import com.dgroup.exchangerates.features.cb_rates.ui.CBRatesFragment;

import com.dgroup.exchangerates.features.cb_rates.ui.KeyboardActivity;
import com.dgroup.exchangerates.features.cryptocap.CMCapitalFragment;
import com.dgroup.exchangerates.features.debug.DebugFragment;
import com.dgroup.exchangerates.ui.base.BaseActivity;
import com.dgroup.exchangerates.ui.base.CustomBackPressedAction;
import com.dgroup.exchangerates.ui.view.custom.SearchBar;

import javax.inject.Inject;

import static com.dgroup.exchangerates.features.cb_rates.ui.KeyboardActivity.PARAM_VALUTE;


public class MainActivity extends BaseActivity implements MainMvpView, NavigationFragment.NavigationDrawerCallbacks, MainRouter {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int CALC_VAL_REQUEST_COUNT = 1001;

    private NavigationFragment mNavigationDrawerFragment;

    //views
    private ActionBarDrawerToggle toggle;
    private DrawerLayout.DrawerListener mDrawerListener;
    private TextView titleView;
    private Toolbar toolbar;
    private ViewGroup toolbarContainer;
    private DrawerLayout drawerLayout;
    private SearchBar mSearchBar;
    private View dragLayout;
    private String titleText;

    private CustomBackPressedAction customBackPressedAction;
    //  private Fragment[] cachedFragments = new Fragment[5];

    @Inject
    protected MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "lifecycle onCreate");
        setContentView(R.layout.activity_main);

        ExRatesApp.getApp().plusMainNavigationComponent().inject(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dragLayout = findViewById(R.id.actionbar_drag);
        View back = dragLayout.findViewById(R.id.back_drag);
        back.setOnClickListener(v -> onBackPressed());
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText(titleText);
        toolbarContainer = (ViewGroup) findViewById(R.id.menu_container);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);

        mSearchBar = (SearchBar) findViewById(R.id.action_bar_search);

        mSearchBar.setActionAfterClosed(() -> setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED));

        mDrawerListener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset > 0) {
                    //   hideMenu();
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //showMenu();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };

        drawerLayout.addDrawerListener(mDrawerListener);
        drawerLayout.addDrawerListener(toggle);

        mNavigationDrawerFragment = (NavigationFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                drawerLayout);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mMainPresenter.subscribe(this);
        Log.i(TAG, "lifecycle onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "lifecycle onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        mMainPresenter.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        drawerLayout.removeDrawerListener(mDrawerListener);
        drawerLayout.removeDrawerListener(toggle);
        mSearchBar.clearListeners();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment = obtainFragment(position);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getSimpleName())
                .commit();
        onSectionAttached(position);
    }

    private Fragment obtainFragment(int position) {
        Fragment fragment; //= cachedFragments[position];
//        if (fragment != null) {
//            return fragment;
//        }
        switch (position) {
            case 0:
                fragment = CBRatesFragment.instantiate(this, CBRatesFragment.class.getName());
                break;
            case 1:
                fragment = BankCoursesFragment.instantiate(this, BankCoursesFragment.class.getName());
                break;
            case 2:
                fragment = CMCapitalFragment.instantiate(this, CMCapitalFragment.class.getName());
                break;
            case 3:
                fragment = DebugFragment.instantiate(this, DebugFragment.class.getName());
                break;
            default:
                fragment = CBRatesFragment.instantiate(this, CBRatesFragment.class.getName());
        }
        // cachedFragments[position] = fragment;
        return fragment;
    }

    public void onSectionAttached(int number) {
        if (titleView != null) {
            titleView.setText("");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALC_VAL_REQUEST_COUNT && resultCode == RESULT_OK) {
            Fragment fragment = getFragmentManager().findFragmentByTag(CBRatesFragment.class.getSimpleName());
            if (fragment != null) {
                ((CBRatesFragment) fragment).update();
            }
        }
    }

    @Override
    public void invalidateMenu(View menu) {
        Log.i(TAG, "invalidateMenu " + menu.hashCode());
        View old = toolbarContainer.getChildAt(0);
        if (old != null) {
            menu.setAlpha(0);
            toolbarContainer.addView(menu);
            menu.animate().alpha(1f).setDuration(250);
            old.animate().alpha(0f).setDuration(250).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    toolbarContainer.removeView(old);
                }
            });
        } else {
            toolbarContainer.addView(menu);
        }
    }


    @Override
    public void setHoldBackMenu(CustomBackPressedAction customBackPressedAction) {
        this.customBackPressedAction = customBackPressedAction;
        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        dragLayout.setVisibility(View.VISIBLE);
        animArrowMenu(false);
    }


    private void animArrowMenu(boolean close) {
        ValueAnimator anim = ValueAnimator.ofFloat(close ? 1 : 0, close ? 0 : 1);
        anim.addUpdateListener(valueAnimator -> {
            float slideOffset = (Float) valueAnimator.getAnimatedValue();
            toggle.onDrawerSlide(drawerLayout, slideOffset);
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(300);
        anim.start();
    }

    public void clearMenu() {
        if (toolbarContainer.getChildCount() != 0) {
            toolbarContainer.removeAllViews();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mSearchBar.getVisibility() == View.VISIBLE) {
            hideSearch();
            return;
        }
        if (customBackPressedAction != null) {
            animArrowMenu(true);
            dragLayout.setVisibility(View.GONE);
            customBackPressedAction.doBack();
            customBackPressedAction = null;
            return;
        }
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void openBankSearch(@NonNull String name) {
        try {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, name);
            startActivity(intent);
        } catch (Exception e) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=" + name));
            startActivity(browserIntent);
        }
    }

    public void setSearchCallback(SearchBar.OnSearchListener searchCallback) {
        Log.i(TAG, "setSearchCallback searchCallback "+(searchCallback!=null));
        mSearchBar.setOnSearchListener(searchCallback);
    }

    @Override
    public void setAutoCompleteData(String[] autoCompleteData) {
        mSearchBar.setAutoCompleteData(autoCompleteData);
    }

    @Override
    public void showSearch(Pair<Integer, Integer> coords, String string, boolean useCitiesHint) {
        Log.i(TAG, "showSearch " + mSearchBar.hashCode());
        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mSearchBar.showSearch(coords, string, useCitiesHint);
    }

    @Override
    public void hideSearch() {
        Log.i(TAG, "hideSearch");
        mSearchBar.hideSearch();
    }

    @Override
    public void showKeyboard(Valute valute) {
        Intent intent = new Intent(this, KeyboardActivity.class);
        intent.putExtra(PARAM_VALUTE, valute);
        startActivityForResult(intent, CALC_VAL_REQUEST_COUNT);
    }

    @Override
    public void setDrawerLockMode(int mode) {
        Log.i(TAG, "setDrawerLockMode " + mode);
        drawerLayout.setDrawerLockMode(mode);
        toggle.syncState();
    }

    public void setCustomBackPressedAction(CustomBackPressedAction customBackPressedAction) {
        this.customBackPressedAction = customBackPressedAction;
    }

}
