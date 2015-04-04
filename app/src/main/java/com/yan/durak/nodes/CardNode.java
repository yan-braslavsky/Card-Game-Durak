package com.yan.durak.nodes;

import android.support.annotation.NonNull;

import com.yan.durak.gamelogic.cards.Card;

import java.util.HashSet;

import glengine.yan.glengine.assets.atlas.YANAtlasTextureRegion;
import glengine.yan.glengine.nodes.YANTexturedNode;


/**
 * Created by Yan-Home on 10/26/2014.
 */
public class CardNode extends YANTexturedNode {

    public static final String TAG_TEMPORALLY_COVERED = "TAG_TEMPORALLY_COVERED";
    public static final String TAG_SHOULD_NOT_RESIZE = "TAG_SHOULD_NOT_RESIZE";

    private final YANAtlasTextureRegion mFrontTextureRegion;
    private final YANAtlasTextureRegion mBackTextureRegion;
    private Card mCard;
    private HashSet<String> mTags;

    public CardNode(@NonNull YANAtlasTextureRegion frontTextureRegion, @NonNull YANAtlasTextureRegion backTextureRegion, @NonNull Card card) {
        super(frontTextureRegion);
        mFrontTextureRegion = frontTextureRegion;
        mBackTextureRegion = backTextureRegion;
        mCard = card;
        mTags = new HashSet<>();
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

    public boolean containsTag(String tag) {
        return mTags.contains(tag);
    }

    public void addTag(String tag) {
        mTags.add(tag);
    }

    public void removeTag(String tag) {
        mTags.remove(tag);
    }

    public void removeAllTags() {
        mTags.clear();
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
