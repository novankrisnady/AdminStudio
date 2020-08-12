package com.novankisnady.staffstudioband.Interface;

import com.novankisnady.staffstudioband.Model.Studio;

import java.util.List;

public interface IStudioBandLoadListener {
    void onStudioBandLoadSuccess(List<Studio> studioList);
    void onStudioBandLoadFailed(String message);
}
