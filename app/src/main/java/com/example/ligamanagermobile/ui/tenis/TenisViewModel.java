package com.example.ligamanagermobile.ui.tenis;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TenisViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TenisViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tenis fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
