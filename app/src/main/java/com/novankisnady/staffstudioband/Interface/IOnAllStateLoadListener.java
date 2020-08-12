package com.novankisnady.staffstudioband.Interface;

import com.novankisnady.staffstudioband.Model.City;

import java.util.List;

public interface IOnAllStateLoadListener {
    void onAllStateLoadSuccess(List<City> cityList);
    void onAllStateLoadFailed(String message);
}
