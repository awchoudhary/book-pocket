package com.awchoudhary.bookpocket;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.HashMap;

/**
 * Created by awaeschoudhary on 3/21/17.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private HashMap<Integer, Fragment> tabMap;
    private Book book; // Book being edited/viewed
    private boolean isEdit;
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, Book book, boolean isEdit) {
        super(fm);
        tabMap = new HashMap<Integer, Fragment>();
        this.mNumOfTabs = NumOfTabs;
        this.book = book;
        this.isEdit = isEdit;
    }

    @Override
    public Fragment getItem(int position) {
        //return tabs to edit book if editing and view if viewing
        if(isEdit){
            switch (position) {
                case 0:
                    tabMap.put(0, EditDetailsTabFragment.newInstance(book));
                    return tabMap.get(0);
                case 1:
                    tabMap.put(1, EditNotesTabFragment.newInstance(book));
                    return tabMap.get(1);
                case 2:
                    return EditDetailsTabFragment.newInstance(book);
                default:
                    return null;
            }
        }
        else{
            switch (position) {
                case 0:
                    return ViewDetailsTabFragment.newInstance(book);
                case 1:
                    return ViewDetailsTabFragment.newInstance(book);
                case 2:
                    return ViewDetailsTabFragment.newInstance(book);
                default:
                    return null;
            }
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public Fragment getFragmentByPosition(int position){
        return tabMap.get(position);
    }
}
