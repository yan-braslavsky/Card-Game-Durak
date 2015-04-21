package com.yan.durak.managers;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.nodes.CardNode;

import java.util.List;

import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;

/**
 * Created by ybra on 20/04/15.
 */
public class CardNodesManager {

    CardNode getCardNodeForCard(Card card) {
        throw new UnsupportedOperationException();
    }

    public int getTotalCardsAmount() {
        throw new UnsupportedOperationException();
    }

    public float getCardNodeWidth() {
        throw new UnsupportedOperationException();
    }

    public float getCardNodeHeight() {
        throw new UnsupportedOperationException();
    }

    public void createNodes(YANTextureAtlas mCardsAtlas) {
        throw new UnsupportedOperationException();
    }

    public void setNodesSizes(YANReadOnlyVector2 sceneSize) {
        throw new UnsupportedOperationException();
    }

    public List<CardNode> getAllCardNodes() {
        throw new UnsupportedOperationException();
    }
}
