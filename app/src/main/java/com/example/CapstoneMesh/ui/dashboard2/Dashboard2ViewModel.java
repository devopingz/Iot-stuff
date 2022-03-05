package com.example.CapstoneMesh.ui.dashboard2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Dashboard2ViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public Dashboard2ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard2 fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}