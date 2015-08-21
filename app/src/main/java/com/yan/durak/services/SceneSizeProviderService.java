package com.yan.durak.services;


import glengine.yan.glengine.service.IService;

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

    public void setSceneSize(final float sceneWidth, final float sceneHeight) {
        mSceneWidth = sceneWidth;
        mSceneHeight = sceneHeight;
    }

    @Override
    public void clearServiceData() {
        //Does nothing
    }
}