package com.dgroup.exchangerates.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * ololo Created by dimon for Snaappy .
 */

public class Utils {

    public static final float TRANSPARENT_ALPHA = 0f;
    public static final float HALF_ALPHA = 0.5f;
    public static final float LIGHT_ALPHA = 0.25f;
    public static final float FULL_ALPHA = 1f;
    public static final int REVEAL_ANIM_LENGTH = 300;
    public static final long SEARCH_KEYBOARD_DELAY = 100L;

    private static final int defCity = 7701;

    public static SortedMap<Currency, Locale> currencyLocaleMap;
    private static DecimalFormat decimalFormat;

    static {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("000,00", otherSymbols);

        currencyLocaleMap = new TreeMap<Currency, Locale>(new Comparator<Currency>() {
            public int compare(Currency c1, Currency c2) {
                return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
            }
        });
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                currencyLocaleMap.put(currency, locale);
            } catch (Exception e) {
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int getDefaultCity() {
        return defCity;
    }

    public static void showSearchBar(View searchBar, Pair<Integer, Integer> coords) {
        searchBar.setVisibility(View.VISIBLE);
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) searchBar).getChildAt(0);
        float finalRadius = getFinalRevealRadius(searchBar);

        Animator animator;

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                animator = ViewAnimationUtils.createCircularReveal(viewGroup, coords.first, coords.second, 0, finalRadius);
            } else {
                animator = ObjectAnimator.ofFloat(searchBar, "translationY", -100f, 0f);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
            }
            if (animator == null) {
                return;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return;
        }
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(REVEAL_ANIM_LENGTH);

        animator.start();
    }

    public static void hideSearchBar(View searchBar, Pair<Integer, Integer> coords) {
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) searchBar).getChildAt(0);
        float finalRadius = getFinalRevealRadius(searchBar);

        Animator animator;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            animator = ViewAnimationUtils.createCircularReveal(viewGroup, coords.first, coords.second, finalRadius, 0);
        } else {
            animator = ObjectAnimator.ofFloat(searchBar, "translationY", 0f, -100f);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        if (animator == null) {
            return;
        }
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(REVEAL_ANIM_LENGTH);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                searchBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                searchBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        searchBar.setVisibility(View.VISIBLE);
        animator.start();
    }

    public static Pair<Integer, Integer> getRevealStartCoords(View v) {
        int coords[] = new int[2];
        v.getLocationInWindow(coords);
        int startX = (int) coords[0] + v.getWidth() / 2;
        int startY = (int) v.getY() + v.getHeight() / 2;
        return new Pair<>(startX, startY);
    }

    public static float getFinalRevealRadius(View v) {
        float a = (float) v.getWidth() * v.getWidth();
        float b = (float) v.getHeight() * v.getHeight();
        return (float) Math.sqrt(a + b);
    }

    public static void hideKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void setEnabled(@NonNull View view, boolean enabled) {
        view.setAlpha(enabled ? Utils.FULL_ALPHA : Utils.HALF_ALPHA);
        view.setEnabled(enabled);
    }

    public static void setEnabledSearch(@NonNull View view, boolean enabled) {
        view.setAlpha(enabled ? Utils.FULL_ALPHA : Utils.TRANSPARENT_ALPHA);
        view.setEnabled(enabled);
    }

    public static double parseDouble(String number) throws ParseException {
        return decimalFormat.parse(number).doubleValue();
    }

    public static float parseFloat(String number) throws ParseException {
        return decimalFormat.parse(number).floatValue();
    }

    public static String getCurrencySymbol(String currencyCode) {
        try {
            Currency currency = Currency.getInstance(currencyCode);
            Locale locale = currencyLocaleMap.get(currency);
            if (locale != null) {
                return currency.getSymbol(locale);
            }
        } catch (Exception e) {}
        return currencyCode;
    }

    public static String getStringFirstUpperCase(String text) {
        String preparedCity = text.trim();
        if (preparedCity.length() > 1) {
            preparedCity = preparedCity.substring(0, 1).toUpperCase() + preparedCity.substring(1);
        }
        return preparedCity;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static String customFormat(String pattern, double value ) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }

}
