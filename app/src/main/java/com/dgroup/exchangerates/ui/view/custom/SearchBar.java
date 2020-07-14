package com.dgroup.exchangerates.ui.view.custom;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.support.v4.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.utils.Utils;

/**
 * Created by gabber on 26.03.17.
 */

public class SearchBar extends FrameLayout {

    public interface OnSearchListener {
        void searchRequest(CharSequence s);

        void onItemSelect(String item);
    }

    public enum SearchMode {
        SEARCH_BANK, SEARCH_CITY
    }

    //views
    private View mClearSearch;
    private AutoCompleteTextView mSearchEditText;

    private String[] autoCompleteStrings;
    private ArrayAdapter<String> mAutoCompleteAdapter;
    private Pair<Integer, Integer> coords;

    private Runnable hideKeyboardTask = new Runnable() {
        @Override
        public void run() {
            Utils.hideKeyboard(getContext(), mSearchEditText);
        }
    };

    private SearchDecorator mSearchDecorator;

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchBar(Context context) {
        super(context);
        init();
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_search_bar, this);

        setVisibility(INVISIBLE);
        setClickable(true);
        mClearSearch = findViewById(R.id.search_delete_button);
        Utils.setEnabledSearch(mClearSearch, false);
        mClearSearch.setOnClickListener(v -> {
            mSearchEditText.getEditableText().clear();
            Utils.setEnabledSearch(v, false);
        });

        mSearchEditText = (AutoCompleteTextView) findViewById(R.id.search_edit_text);
        View backSearchBtn = findViewById(R.id.search_button_back);
        backSearchBtn.setOnClickListener(v -> hideSearch());
        this.mSearchDecorator = new SearchDecorator();
        setCommonListener(mSearchDecorator);
        mSearchDecorator.setActionAfterText(this::invalidateClearBtn);
    }

    private void setCommonListener(SearchDecorator searchListener) {
        mSearchEditText.setOnItemClickListener(searchListener);
        mSearchEditText.addTextChangedListener(mSearchDecorator);
        mSearchEditText.setOnEditorActionListener(searchListener);
    }

    public void invalidateClearBtn() {
        Utils.setEnabledSearch(mClearSearch, !TextUtils.isEmpty(mSearchEditText.getText()));
    }

    public void clearListeners() {
        Log.i("SearchBar", "clearListeners");
        removeCallbacks(hideKeyboardTask);
        if(getHandler()!=null) {
            getHandler().removeCallbacksAndMessages(null);
        }
        mSearchDecorator.clear();
        mSearchEditText.removeTextChangedListener(mSearchDecorator);
        mSearchEditText.setOnEditorActionListener(null);
        mSearchEditText.setOnItemClickListener(null);
        mSearchDecorator = null;
    }

    public void showSearch(Pair<Integer, Integer> coords, String hint, boolean useCitiesHint) {
        Log.i("SearchBar", "showSearch coords " + coords.toString());
        this.coords = coords;
        Utils.showSearchBar(this, coords);
        mSearchEditText.setHint(hint);
        if (useCitiesHint) {
            mSearchEditText.setAdapter(mAutoCompleteAdapter);
        } else {
            mSearchEditText.setAdapter(null);
        }
        postDelayed(() -> {
            mSearchEditText.requestFocus();
            InputMethodManager imm
                    = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mSearchEditText, InputMethodManager.SHOW_IMPLICIT);
        }, Utils.SEARCH_KEYBOARD_DELAY);
    }

    public void hideSearch() {
        if (getVisibility() == VISIBLE) {
            mSearchEditText.clearFocus();
            Utils.hideSearchBar(this, coords);
            mSearchEditText.setText("");
            postDelayed(hideKeyboardTask, Utils.REVEAL_ANIM_LENGTH);
            mSearchDecorator.onSearchBarClosed();
        }
    }

    public void setAutoCompleteData(String[] autoCompleteData) {
        autoCompleteStrings = autoCompleteData;
        mAutoCompleteAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_text, autoCompleteStrings);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Log.i("SearchBar", "onSaveInstanceState " + (getVisibility() == VISIBLE));
        Parcelable superState = super.onSaveInstanceState();
        if (getVisibility() == VISIBLE) {
            SavedState savedState = new SavedState(superState);
            savedState.coords = this.coords;
            savedState.visible = getVisibility() == VISIBLE;
            savedState.hint = mSearchEditText.getHint().toString();
            savedState.data = autoCompleteStrings;
            savedState.showAutoComplete = mSearchEditText.getAdapter() != null;
            return savedState;
        } else {
            return superState;
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        this.coords = savedState.coords;
        mSearchEditText.setHint(savedState.hint);
        if (savedState.data != null) {
            autoCompleteStrings = savedState.data;
            mAutoCompleteAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_text, autoCompleteStrings);
            if (savedState.showAutoComplete) {
                mSearchEditText.setAdapter(mAutoCompleteAdapter);
            }
        }
        if (savedState.visible) {
            setVisibility(VISIBLE);
        }
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        Log.i("SearchBar", "setOnSearchListener exist "+(onSearchListener!=null));
        mSearchDecorator.setOnSearchListener(onSearchListener);
    }

    public void setActionAfterClosed(Runnable actionAfterClosed) {
        mSearchDecorator.setActionAfterClosed(actionAfterClosed);
    }

    private static class SavedState extends BaseSavedState {
        Pair<Integer, Integer> coords;
        boolean visible;
        String hint;
        String[] data;
        boolean showAutoComplete;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.coords = new Pair<>(in.readInt(), in.readInt());
            this.visible = in.readByte() != 0;
            this.hint = in.readString();
            int length = in.readInt();
            if(length>0) {
                data = new String[length];
                in.readStringArray(this.data);
            }
            this.showAutoComplete = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.coords.first);
            out.writeInt(this.coords.second);
            out.writeByte(this.visible ? (byte) 1 : (byte) 0);
            out.writeString(hint);
            out.writeInt(data != null ? data.length : 0);
            if(data!=null && data.length>0) {
                out.writeStringArray(data);
            }
            out.writeByte(this.showAutoComplete ? (byte) 1 : (byte) 0);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    private class SearchDecorator implements TextWatcher, TextView.OnEditorActionListener, AdapterView.OnItemClickListener {

        private Runnable actionAfterText;
        private Runnable actionAfterClosed;
        private OnSearchListener mOnSearchListener;

        private void setOnSearchListener(OnSearchListener onSearchListener) {
            this.mOnSearchListener = onSearchListener;
        }

        private void clearSearchListener() {
            this.mOnSearchListener = null;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.i("SearchDecorator", "onTextChanged " + s+" mOnSearchListener exist "+(mOnSearchListener!=null));
            if (mOnSearchListener != null) {
                mOnSearchListener.searchRequest(s);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (actionAfterText != null && getHandler() != null) {
                getHandler().post(actionAfterText);
            }
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (isDoneOrCancel(actionId, event)) {
                if (mOnSearchListener != null) {
                    mOnSearchListener.onItemSelect(toFirstUpperCase(v.getText().toString()));
                }
                return true;
            }
            return false;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mOnSearchListener != null) {
                mOnSearchListener.onItemSelect(toFirstUpperCase(((TextView) view).getText().toString()));
            }
        }

        private void setActionAfterText(Runnable actionAfterText) {
            this.actionAfterText = actionAfterText;
        }

        private void setActionAfterClosed(Runnable actionAfterClosed) {
            this.actionAfterClosed = actionAfterClosed;
        }

        private void clear() {
            clearSearchListener();
            actionAfterText = null;
        }

        private boolean isDoneOrCancel(int actionId, @Nullable KeyEvent event) {
            return actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
        }

        private String toFirstUpperCase(String target) {
            if ((target == null) || (target.length() == 0)) {
                return target;
            }
            return Character.toUpperCase(target.charAt(0))
                    + (target.length() > 1 ? target.substring(1) : "");
        }

        private void onSearchBarClosed() {
            if (actionAfterText != null) {
                getHandler().post(actionAfterClosed);
            }
        }
    }
}
