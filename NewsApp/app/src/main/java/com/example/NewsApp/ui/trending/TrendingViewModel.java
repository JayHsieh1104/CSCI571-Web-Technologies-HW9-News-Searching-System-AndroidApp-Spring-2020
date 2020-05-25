package com.example.NewsApp.ui.trending;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrendingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TrendingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is trending fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}