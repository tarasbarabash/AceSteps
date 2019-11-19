package com.tarasbarabash.acesteps.Activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.tarasbarabash.acesteps.R;

/**
 * Created by Taras
 * 12-Sep-19, 20:04.
 */
public abstract class BaseFragmentActivity extends AppCompatActivity {
    private static final String TAG = BaseFragmentActivity.class.getSimpleName();
    private FragmentManager mFragmentManager;
    private static final int containerId = R.id.fragment_container;

    public abstract Fragment getFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        mFragmentManager = getSupportFragmentManager();
        Fragment fragment = mFragmentManager.findFragmentById(containerId);
        if (fragment == null) {
            fragment = getFragment();
            String tag = fragment.getClass().getSimpleName();
            mFragmentManager.beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
        fragmentTransaction.replace(
                containerId,
                fragment,
                fragment.getClass().getSimpleName()
        ).commit();
    }
}
