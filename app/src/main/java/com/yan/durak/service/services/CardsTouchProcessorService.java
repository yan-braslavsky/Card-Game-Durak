package com.yan.durak.service.services;

import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.service.IService;

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
}
