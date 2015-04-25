package com.yan.durak.service;

import java.util.HashMap;

/**
 * Created by Yan-Home on 4/25/2015.
 */
public class ServiceLocator {

    private static final HashMap<Class<? extends IService>, Object> mServicesMap = new HashMap<>();

    public static final <T extends IService> void addService(T service) {
        mServicesMap.put(service.getClass(), service);
    }

    public static final <T extends IService> T locateService(Class<T> serviceClass) {
        return (T) mServicesMap.get(serviceClass);
    }
}
