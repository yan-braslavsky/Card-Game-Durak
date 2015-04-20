package com.yan.durak.managers;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.nodes.CardNode;

import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;

/**
 * Created by ybra on 20/04/15.
 */
public interface ICardNodesManager {

    CardNode getCardNodeForCard(Card card);

    int getTotalCardsAmount();
}
