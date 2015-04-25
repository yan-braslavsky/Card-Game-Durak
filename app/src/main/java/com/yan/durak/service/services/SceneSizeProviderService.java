package com.yan.durak.service.services;

import com.yan.durak.service.IService;

/**
 * Created by Yan-Home on 4/25/2015.
 */
public class SceneSizeProviderService implements IService {

    private float mSceneWidth;
    private float mSceneHeight;

    public float getSceneWidth() {
        return mSceneWidth;
    }


    public float getSceneHeight() {
        return mSceneHeight;
    }

    public void setSceneSize(float sceneWidth, float sceneHeight) {
        mSceneWidth = sceneWidth;
        mSceneHeight = sceneHeight;
    }
}
