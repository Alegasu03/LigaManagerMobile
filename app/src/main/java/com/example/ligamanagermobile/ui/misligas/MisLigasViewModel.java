package com.example.ligamanagermobile.ui.misligas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MisLigasViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MisLigasViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Mis Ligas fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
