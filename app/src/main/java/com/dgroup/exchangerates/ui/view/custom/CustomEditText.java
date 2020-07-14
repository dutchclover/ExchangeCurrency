package com.dgroup.exchangerates.ui.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import com.dgroup.exchangerates.app.ExRatesApp;

/**
 * Created by gabber on 03.09.17.
 */

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {

    private int startSelection;

    public void setHandleDismissingKeyboard(
            HandleDismissingKeyboard HandleDismissingKeyboard) {
        this.handleDismissingKeyboard = HandleDismissingKeyboard;
    }

    private HandleDismissingKeyboard handleDismissingKeyboard;

    public void setStartSelection(int startSelection) {
        this.startSelection = startSelection;
    }

    public interface HandleDismissingKeyboard {
        void dismissKeyboard();
    }

    public CustomEditText(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            handleDismissingKeyboard.dismissKeyboard();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onEditorAction(int actionCode) {
        if(actionCode== EditorInfo.IME_ACTION_DONE){
            handleDismissingKeyboard.dismissKeyboard();
        }
        super.onEditorAction(actionCode);

    }

    @Override
    public void onSelectionChanged(int start, int end) {
        CharSequence text = getText();
        try {
            if (text != null) {
                if (start <= startSelection) {
                    setSelection(startSelection, startSelection);
                    return;
                }
            }
            super.onSelectionChanged(start, end);
        }catch (Exception e){
            ExRatesApp.logException(new RuntimeException("onSelectionChanged start "+start+" end "+end+" text "+text));
        }
    }

}