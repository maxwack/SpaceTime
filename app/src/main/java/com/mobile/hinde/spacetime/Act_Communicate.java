package com.mobile.hinde.spacetime;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.TextView;
import com.mobile.hinde.utils.Tool;
import com.mobile.hinde.utils.UserSettings;

import java.util.ArrayList;
import java.util.List;

public class Act_Communicate extends AppCompatActivity
        implements  Frag_Communicate.OnFragmentInteractionListener,
                    frag_launch.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        TextView userTxt = findViewById(R.id.userIdTxt);
        userTxt.setText(getIntent().getAction());
    }

    @Override
    protected void onResume(){
        super.onResume();
        TextView moneyCount = findViewById(R.id.moneyCount);
        moneyCount.setText(Tool.formatMoneyCount(UserSettings.getInstance().getMoney()));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Frag_Communicate(), "SEND");
        adapter.addFragment(new frag_launch(), "LAUNCH");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void showListImage(View v){
        Intent i = new Intent(getApplicationContext(), Act_ImageList.class);
        startActivity(i);
    }
}
