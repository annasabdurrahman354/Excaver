package com.excaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.excaver.CustomUI.NonSwipeableViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private NonSwipeableViewPager viewPager;
    FragmentHome fragmentHome;
    FragmentJob fragmentJob;
    FragmentChat fragmentChat;
    FragmentProfile fragmentProfile;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (NonSwipeableViewPager) findViewById(R.id.main_viewpager);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.main_tab);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.tab_home:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.tab_job:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.tab_chat:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.tab_profile:
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        AdapterMain adapter = new AdapterMain(getSupportFragmentManager());
        fragmentHome= new FragmentHome();
        fragmentJob= new FragmentJob();
        fragmentChat = new FragmentChat();
        fragmentProfile= new FragmentProfile();
        adapter.addFragment(fragmentHome);
        adapter.addFragment(fragmentJob);
        adapter.addFragment(fragmentChat);
        adapter.addFragment(fragmentProfile);
        viewPager.setAdapter(adapter);
    }
}