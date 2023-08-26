package com.example.opportunity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenViewPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tab_indicator;
    SharedPreferences sharedPreferences;
    Button btn_next;
    int position = 0;
    Button btn_get_started;
    Animation button_animation;
    TextView skip_text_view;
    long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (restorePrefData()) {
            Intent intentStartActivity = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(intentStartActivity);
            finish();
        }
        setContentView(R.layout.activity_intro);

        btn_next = findViewById(R.id.btn_next);
        btn_get_started = findViewById(R.id.btn_get_started);
        tab_indicator = findViewById(R.id.tab_indicator);
        button_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        skip_text_view = findViewById(R.id.skip_text_view);

        final List<ScreenItem> screenItemList = new ArrayList<>();
        screenItemList.add(new ScreenItem(
                getString(R.string.app_goal_title),
                getString(R.string.app_goal_description), R.drawable.arrow));

        screenItemList.add(new ScreenItem(
                getString(R.string.self_confidence_title),
                getString(R.string.self_confidence_description), R.drawable.change));

        screenItemList.add(new ScreenItem(
                getString(R.string.your_dream_title),
                getString(R.string.your_dream_description), R.drawable.job_opportunities));

        screenItemList.add(new ScreenItem(
                getString(R.string.remember_this_title),
                getString(R.string.remember_this_description), R.drawable.paper_pin));

        screenViewPager = findViewById(R.id.screen_view_pager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, screenItemList);
        screenViewPager.setAdapter(introViewPagerAdapter);

        tab_indicator.setupWithViewPager(screenViewPager);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenViewPager.getCurrentItem();
                if (position < screenItemList.size()) {
                    position++;
                    screenViewPager.setCurrentItem(position);
                }

                if (position == screenItemList.size() - 1) {
                    loadLastScreen();
                }
            }
        });

        tab_indicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == screenItemList.size() - 1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btn_get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(mainActivity);
                savePrefsData();
                finish();
            }
        });

        /** Skip button click listener */

        skip_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenViewPager.setCurrentItem(screenItemList.size());
            }
        });
    }

    private boolean restorePrefData() {
        sharedPreferences = getApplicationContext().getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        boolean isIntroActivityOpenedBefore = sharedPreferences.getBoolean("IS_INTRO_OPENED", false);
        return isIntroActivityOpenedBefore;
    }

    private void savePrefsData() {

        sharedPreferences = getApplicationContext().getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IS_INTRO_OPENED", true);
        editor.apply();
    }

    /**
     * Show the GetStarted Button and hide the indicator and the next button
     */
    private void loadLastScreen() {

        btn_next.setVisibility(View.INVISIBLE);
        btn_get_started.setVisibility(View.VISIBLE);
        skip_text_view.setVisibility(View.INVISIBLE);
        tab_indicator.setVisibility(View.INVISIBLE);

        /** Add an animation the getStarted button. */
        btn_get_started.setAnimation(button_animation);

    }

    @Override
    public void onBackPressed() {
        if ((time + 2000) > System.currentTimeMillis()) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Press again", Toast.LENGTH_LONG).show();
        }
        time = System.currentTimeMillis();
    }
}