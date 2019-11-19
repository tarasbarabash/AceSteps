package com.tarasbarabash.acesteps.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.tarasbarabash.acesteps.Adapters.BasePagerAdapter;
import com.tarasbarabash.acesteps.Adapters.DaysPagerAdapter;
import com.tarasbarabash.acesteps.Database.WorkoutDBHelper;
import com.tarasbarabash.acesteps.models.Day;

import java.util.ArrayList;

public class MainActivity extends BasePagedActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBinding().settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBinding().viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING || state == ViewPager.SCROLL_STATE_SETTLING) {
                    getBinding().settingsButton.animate().rotation(45).scaleX(0).scaleY(0).setDuration(200).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            getBinding().settingsButton.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            animation.resume();
                        }
                    });
                }
                else {
                    getBinding().settingsButton.animate().rotation(0).scaleX(1).scaleY(1).setDuration(200).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            getBinding().settingsButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            animation.resume();
                        }
                    });
                }
            }
        });
    }

    @Override
    public BasePagerAdapter getAdapter() {
        FragmentManager fm = getSupportFragmentManager();
        return new DaysPagerAdapter(fm) {
            @Override
            public ArrayList<Day> getDays() {
                return WorkoutDBHelper.getInstance(getApplicationContext()).getDays();
            }
        };
    }
}
