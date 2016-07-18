package io.virtualapp.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import io.virtualapp.R;
import io.virtualapp.VCommends;
import io.virtualapp.abs.ui.VActivity;
import io.virtualapp.home.adapters.AppPagerAdapter;

/**
 * @author Lody
 */
public class ListAppActivity extends VActivity {

    private ViewPager mViewPager;
    private PagerTabStrip mPagerTabStrip;

    public static void gotoListApp(Activity activity) {
        Intent intent = new Intent(activity, ListAppActivity.class);
        activity.startActivityForResult(intent, VCommends.REQUEST_SELECT_APP);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_app);
        ActionBar actionBar = getSupportActionBar();
        setupActionBar(actionBar);

        mViewPager = (ViewPager) findViewById(R.id.app_list_pager);
        mPagerTabStrip = (PagerTabStrip) findViewById(R.id.app_pager_tap_strip);
        mPagerTabStrip.setTabIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));
        mViewPager.setAdapter(new AppPagerAdapter(getSupportFragmentManager()));
    }

    private void setupActionBar(ActionBar actionBar) {
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.add_app);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
