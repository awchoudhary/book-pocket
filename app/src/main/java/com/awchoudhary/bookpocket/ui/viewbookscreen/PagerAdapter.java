package com.awchoudhary.bookpocket.ui.viewbookscreen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.awchoudhary.bookpocket.ui.mybooksscreen.Book;


/**
 * Created by awaeschoudhary on 3/21/17.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private Book book; // Book being edited/viewed
    private int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, Book book) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.book = book;
    }

    @Override
    public Fragment getItem(int position) {
        //return tabs to edit book if editing and view if viewing
        switch (position) {
            case 0:
                return ViewDetailsTabFragment.newInstance(book);
            case 1:
                return NotesTabFragment.newInstance(book);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
