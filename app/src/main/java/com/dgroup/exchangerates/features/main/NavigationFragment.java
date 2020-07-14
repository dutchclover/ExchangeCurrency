package com.dgroup.exchangerates.features.main;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.ui.adapter.NavigationAdapter;

/**
 * Created by dmitriy on 03.02.15.
 */
public class NavigationFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String TAG = NavigationFragment.class.getSimpleName();

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    private final Handler mDrawerHandler = new Handler();

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private NavigationAdapter mNavigationAdapter;

    private int mCurrentSelectedPosition = -1;
    private boolean mFromSavedInstanceState;

    public NavigationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"lifecycle onCreate");

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition != -1 ? mCurrentSelectedPosition : 0, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG,"lifecycle onCreateView");
        View root = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView) root.findViewById(R.id.list);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position, true);
            }
        });
        mNavigationAdapter = new NavigationAdapter(new String[]{
                getString(R.string.title_section1),
                getString(R.string.title_section2),
                getString(R.string.title_section3),
            //    "Debug"
        }, new int[]{
                R.drawable.ic_star_border_black_24dp,
                R.drawable.icon_banks,
                R.drawable.icon_crypto_currency,
               // R.drawable.ic_info_outline_black_24dp,
             //   R.drawable.ic_info_outline_black_24dp,
        });
        mDrawerListView.setAdapter(mNavigationAdapter);

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG,"lifecycle onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"lifecycle onResume");
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }

    private void selectItem(int position, boolean delay) {
        Log.i(TAG,"selectItem "+position);
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
            mNavigationAdapter.notifyDataSetChanged();
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCurrentSelectedPosition == position) {
            return;
        }
        mCurrentSelectedPosition = position;

        if(delay) {
            mDrawerHandler.postDelayed(() -> {
                if (mCallbacks != null && isAdded()) {
                    mCallbacks.onNavigationDrawerItemSelected(mCurrentSelectedPosition);
                }
            }, 250);
        }else {
            mCallbacks.onNavigationDrawerItemSelected(mCurrentSelectedPosition);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG,"lifecycle onAttach");
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

}
