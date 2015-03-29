package com.yan.durak.nodes;

import android.support.annotation.NonNull;

import com.yan.durak.entities.cards.Card;
import com.yan.glengine.assets.atlas.YANAtlasTextureRegion;
import com.yan.glengine.nodes.YANTexturedNode;

/**
 * Created by Yan-Home on 10/26/2014.
 */
public class CardNode extends YANTexturedNode {

    private final YANAtlasTextureRegion mFrontTextureRegion;
    private final YANAtlasTextureRegion mBackTextureRegion;
    private Card mCard;
    private Object mTag;

    /**
     * Used to indicate that current card is temporary covered by other card
     */
    public static class TemporaryCoveredTag {

    }

    public CardNode(@NonNull YANAtlasTextureRegion frontTextureRegion, @NonNull YANAtlasTextureRegion backTextureRegion, @NonNull Card card) {
        super(frontTextureRegion);
        mFrontTextureRegion = frontTextureRegion;
        mBackTextureRegion = backTextureRegion;
        mCard = card;
    }

    @Override
    public void onAttachedToScreen() {
        //TODO : implement
    }

    @Override
    public void onDetachedFromScreen() {
        //TODO : implement
    }

    public Card getCard() {
        return mCard;
    }

    public void useFrontTextureRegion() {
        setTextureRegion(mFrontTextureRegion);
    }

    public void useBackTextureRegion() {
        setTextureRegion(mBackTextureRegion);
    }

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof CardNode || o instanceof Card)) return false;
//        if (getCard() == null) return false;
//
//        //we allow comparison with a card
//        if (o instanceof Card) {
//            return o.equals(getCard());
//        }
//
//        CardNode card = (CardNode) o;
//        if (card.getCard() == null) return false;
//        return getCard().equals(card.getCard());
//    }
//
//    @Override
//    public int hashCode() {
//        return getCard().hashCode();
//    }
}
