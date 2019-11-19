package com.tarasbarabash.acesteps.viewModels;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import androidx.databinding.BaseObservable;
import androidx.databinding.ViewDataBinding;
import androidx.appcompat.app.AppCompatActivity;

import com.tarasbarabash.acesteps.AceSteps;
import com.tarasbarabash.acesteps.Fragments.BaseFragment;

/**
 * Created by Taras
 * 15-Sep-19, 21:11.
 */
public class BaseVM<B extends ViewDataBinding> extends BaseObservable {
    private BaseFragment mFragment;
    private Context mContext;
    private Activity mActivity;
    private B mBinding;
    private AceSteps mApplication;

    public BaseVM(B binding, BaseFragment fragment) {
        mBinding = binding;
        mFragment = fragment;
        mContext = fragment.getContext();
        mActivity = fragment.getActivity();
        mApplication = (AceSteps) fragment.getActivity().getApplication();
    }

    public BaseVM(B binding, Context context, Application application, Activity activity) {
        mBinding = binding;
        mContext = context;
        mActivity = activity;
        mApplication = (AceSteps) application;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public Context getContext() {
        return mContext;
    }

    public B getBinding() {
        return mBinding;
    }

    public BaseFragment getFragment() {
        return mFragment;
    }

    public void setToolbarText(String text) {
        ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().setTitle(text);
    }

    public AceSteps getApplication() {
        return mApplication;
    }
}
