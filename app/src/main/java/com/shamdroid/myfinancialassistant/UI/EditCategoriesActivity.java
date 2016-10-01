package com.shamdroid.myfinancialassistant.UI;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shamdroid.myfinancialassistant.Models.CategorySource;
import com.shamdroid.myfinancialassistant.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditCategoriesActivity extends AppCompatActivity {

    @BindView(R.id.drawerLayout)
    DrawerLayout mDraweLayout;


    @BindView(R.id.viewpagerCategories)
    ViewPager viewPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_categories);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        CategoriesPagerAdapter adapter = new CategoriesPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        Util.initNavigationView(this);

    }



    @Override
    public void onBackPressed() {

        if(mDraweLayout.isDrawerOpen(GravityCompat.START))
            mDraweLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();

    }

    class CategoriesPagerAdapter extends FragmentPagerAdapter{


        public CategoriesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if(position == 0)
                return getString(R.string.categories);
            else
                return getString(R.string.sources);

        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public Fragment getItem(int position) {
            return new EditCategories(EditCategoriesActivity.this,position==0? CategorySource.TYPE_CAT: CategorySource.TYPE_SRC);
        }


    }


}
