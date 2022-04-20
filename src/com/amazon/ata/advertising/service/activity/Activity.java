package com.amazon.ata.advertising.service.activity;

public interface Activity<T, T1> {
    public T1 getActivity();

    public T getActivityType();
}
