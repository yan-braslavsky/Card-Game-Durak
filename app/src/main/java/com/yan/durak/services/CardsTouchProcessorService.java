package com.yan.durak.services;

import com.yan.durak.input.cards.CardsTouchProcessor;

import glengine.yan.glengine.service.IService;


/**
 * Created by Yan-Home on 5/9/2015.
 */
public class CardsTouchProcessorService implements IService {
    private final CardsTouchProcessor mCardsTouchProcessor;

    public CardsTouchProcessorService(CardsTouchProcessor cardsTouchProcessor) {
        mCardsTouchProcessor = cardsTouchProcessor;
    }

    public void unRegister() {
        mCardsTouchProcessor.unRegister();
    }

    public void register() {
        mCardsTouchProcessor.register();
    }

    public void setSceneSize(float screenWidth, float screenHeight) {
        mCardsTouchProcessor.setSceneSize(screenWidth, screenHeight);
    }

    @Override
    public void clearServiceData() {
        unRegister();
    }
}
