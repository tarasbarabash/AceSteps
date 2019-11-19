package com.tarasbarabash.acesteps.Activities;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.tarasbarabash.acesteps.Adapters.BasePagerAdapter;
import com.tarasbarabash.acesteps.R;
import com.tarasbarabash.acesteps.databinding.ActivityPagedBinding;

/**
 * Created by Taras
 * 15-Sep-19, 20:37.
 */
public abstract class BasePagedActivity extends AppCompatActivity {
    private static final String TAG = BasePagedActivity.class.getSimpleName();
    private ActivityPagedBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_paged);
        ViewPager viewPager = mBinding.viewPager;
        BasePagerAdapter adapter = getAdapter();
        viewPager.setAdapter(adapter);
    }

    public ActivityPagedBinding getBinding() {
        return mBinding;
    }

    public abstract BasePagerAdapter getAdapter();
}
