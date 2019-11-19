package com.tarasbarabash.acesteps.viewModels;

import androidx.databinding.ViewDataBinding;

import com.tarasbarabash.acesteps.Fragments.BaseFragment;

/**
 * Created by Taras
 * 21-Sep-19, 17:04.
 */
public abstract class FragmentVM<B extends ViewDataBinding> extends BaseVM<B> {
    public FragmentVM(B binding, BaseFragment fragment) {
        super(binding, fragment);
    }

}
