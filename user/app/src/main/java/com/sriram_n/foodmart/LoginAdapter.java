package com.sriram_n.foodmart;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginAdapter extends FragmentPagerAdapter {

    private Context context;
    int totalTabs;

    public LoginAdapter(FragmentManager fm, Context c, int totalTabs) {
        super(fm);
        this.context = c;
        this.totalTabs = totalTabs;

    }

    public Fragment getItem(int position) {
       switch (position){
           case 0:
               sign_in_tab l = new sign_in_tab();
               return l;
           case 1:
               sign_up_tab s = new sign_up_tab();
               return s;
           default:
               return null;
       }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
