package com.yan.durak.layouting.pile;

import com.yan.durak.models.PileModel;
import com.yan.durak.service.ServiceLocator;
import com.yan.durak.service.services.PileManagerService;

import java.util.HashMap;
import java.util.List;

import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;
import glengine.yan.glengine.util.geometry.YANVector2;

/**
 * Created by Yan-Home on 5/3/2015.
 */
public class FieldPilePositioner {

    /**
     * Key represents the amount of active piles on field
     * Pair key represents the pile and the value represents pile position
     */
    private HashMap<Integer,HashMap<PileModel, YANReadOnlyVector2>> mPilePositionsForActiveFieldPiles;

    public FieldPilePositioner() {
        mPilePositionsForActiveFieldPiles = new HashMap<>();
    }

    public YANReadOnlyVector2 getPositionForPile(final PileModel pile) {
        int key = calculateActivePilesAmount();
        YANReadOnlyVector2 vector2 = mPilePositionsForActiveFieldPiles.get(key).get(pile);
        return vector2;
    }

    private int calculateActivePilesAmount() {
        int activePilesAmount = 0;
        for (PileModel fieldPile : ServiceLocator.locateService(PileManagerService.class).getFieldPiles()) {
            if (!fieldPile.getCardsInPile().isEmpty())
                activePilesAmount++;
        }
        return activePilesAmount;
    }

    public void init(final float sceneWidth, final float sceneHeight, final float cardWidth, final float cardHeight) {

        //cache some values
        float halfCardWidth = cardWidth / 2;
        float halfCardHeight = cardHeight / 2;

        //get reference to field piles
        List<PileModel> fieldPiles = ServiceLocator.locateService(PileManagerService.class).getFieldPiles();

        //zero piles
        HashMap<PileModel, YANReadOnlyVector2> zeroCardsOnFieldMap = new HashMap<>();
        for (PileModel fieldPile : fieldPiles) {
            zeroCardsOnFieldMap.put(fieldPile,new YANVector2());
        }
//        mPilePositionsForActiveFieldPiles.put(0, zeroCardsOnFieldMap);

        //one pile
        YANVector2 oneCardFirstPilePos = new YANVector2((sceneWidth - cardWidth) / 2, (sceneHeight - cardHeight) / 2);
        HashMap<PileModel, YANReadOnlyVector2> oneCardOnFieldMap = new HashMap<>();
        oneCardOnFieldMap.put(fieldPiles.get(0), oneCardFirstPilePos);
        mPilePositionsForActiveFieldPiles.put(1, oneCardOnFieldMap);

        //two piles
        YANVector2 twoCardsFirstPilePos = new YANVector2(oneCardFirstPilePos.getX() - cardWidth, oneCardFirstPilePos.getY());
        YANVector2 twoCardsSecondPilePos = new YANVector2(oneCardFirstPilePos.getX() + cardWidth, oneCardFirstPilePos.getY());
        HashMap<PileModel, YANReadOnlyVector2> twoCardsOnFieldMap = new HashMap<>();
        twoCardsOnFieldMap.put(fieldPiles.get(0), twoCardsFirstPilePos);
        twoCardsOnFieldMap.put(fieldPiles.get(1), twoCardsSecondPilePos);
        mPilePositionsForActiveFieldPiles.put(2, twoCardsOnFieldMap);

        //three piles
        YANVector2 threeCardsFirstPilePos = new YANVector2(twoCardsFirstPilePos.getX(), twoCardsFirstPilePos.getY() - (halfCardHeight));
        YANVector2 threeCardsSecondPilePos = new YANVector2(twoCardsSecondPilePos.getX(), twoCardsSecondPilePos.getY() - (halfCardHeight));
        YANVector2 threeCardsThirdPilePos = new YANVector2(oneCardFirstPilePos.getX(), oneCardFirstPilePos.getY() + (halfCardHeight));
        HashMap<PileModel, YANReadOnlyVector2> threeCardsOnFieldMap = new HashMap<>();
        threeCardsOnFieldMap.put(fieldPiles.get(0), threeCardsFirstPilePos);
        threeCardsOnFieldMap.put(fieldPiles.get(1), threeCardsSecondPilePos);
        threeCardsOnFieldMap.put(fieldPiles.get(2), threeCardsThirdPilePos);
        mPilePositionsForActiveFieldPiles.put(3, threeCardsOnFieldMap);

        //four piles
        YANVector2 fourCardsFirstPilePos = new YANVector2(threeCardsFirstPilePos.getX(), threeCardsFirstPilePos.getY());
        YANVector2 fourCardsSecondPilePos = new YANVector2(threeCardsSecondPilePos.getX(), threeCardsSecondPilePos.getY());
        YANVector2 fourCardsThirdPilePos = new YANVector2(threeCardsFirstPilePos.getX(), threeCardsThirdPilePos.getY());
        YANVector2 fourCardsFourthPilePos = new YANVector2(threeCardsSecondPilePos.getX(), threeCardsThirdPilePos.getY());
        HashMap<PileModel, YANReadOnlyVector2> fourCardsOnFieldMap = new HashMap<>();
        fourCardsOnFieldMap.put(fieldPiles.get(0), fourCardsFirstPilePos);
        fourCardsOnFieldMap.put(fieldPiles.get(1), fourCardsSecondPilePos);
        fourCardsOnFieldMap.put(fieldPiles.get(2), fourCardsThirdPilePos);
        fourCardsOnFieldMap.put(fieldPiles.get(3), fourCardsFourthPilePos);
        mPilePositionsForActiveFieldPiles.put(4, fourCardsOnFieldMap);

        //five piles
        YANVector2 fiveCardsFirstPilePos = new YANVector2(fourCardsFirstPilePos.getX(), fourCardsFirstPilePos.getY());
        YANVector2 fiveCardsSecondPilePos = new YANVector2(fourCardsSecondPilePos.getX(), fourCardsSecondPilePos.getY());
        YANVector2 fiveCardsThirdPilePos = new YANVector2(fourCardsThirdPilePos.getX(), fourCardsThirdPilePos.getY());
        YANVector2 fiveCardsFourthPilePos = new YANVector2(fourCardsFourthPilePos.getX(), fourCardsFourthPilePos.getY());
        YANVector2 fiveCardsFifthPilePos = new YANVector2(oneCardFirstPilePos.getX(), oneCardFirstPilePos.getY());
        HashMap<PileModel, YANReadOnlyVector2> fiveCardsOnFieldMap = new HashMap<>();
        fiveCardsOnFieldMap.put(fieldPiles.get(0), fiveCardsFirstPilePos );
        fiveCardsOnFieldMap.put(fieldPiles.get(1), fiveCardsSecondPilePos);
        fiveCardsOnFieldMap.put(fieldPiles.get(2), fiveCardsThirdPilePos);
        fiveCardsOnFieldMap.put(fieldPiles.get(3), fiveCardsFourthPilePos);
        fiveCardsOnFieldMap.put(fieldPiles.get(4), fiveCardsFifthPilePos);
        mPilePositionsForActiveFieldPiles.put(5, fiveCardsOnFieldMap);


        //six piles
        YANVector2 sixCardsFirstPilePos = new YANVector2(fiveCardsFirstPilePos.getX() - halfCardWidth, fiveCardsFirstPilePos.getY());
        YANVector2 sixCardsSecondPilePos = new YANVector2(fiveCardsSecondPilePos.getX() + halfCardWidth, fiveCardsSecondPilePos.getY());
        YANVector2 sixCardsThirdPilePos = new YANVector2(fiveCardsThirdPilePos.getX() - halfCardWidth, fiveCardsThirdPilePos.getY());
        YANVector2 sixCardsFourthPilePos = new YANVector2(fiveCardsFourthPilePos.getX() + halfCardWidth, fiveCardsFourthPilePos.getY());
        YANVector2 sixCardsFifthPilePos = new YANVector2(fiveCardsFifthPilePos.getX(), fiveCardsFifthPilePos.getY() - halfCardHeight);
        YANVector2 sixCardsSixthPilePos = new YANVector2(fiveCardsFifthPilePos.getX(), fiveCardsFifthPilePos.getY() + halfCardHeight);
        HashMap<PileModel, YANReadOnlyVector2> sixCardsOnFieldMap = new HashMap<>();
        sixCardsOnFieldMap.put(fieldPiles.get(0), sixCardsFirstPilePos );
        sixCardsOnFieldMap.put(fieldPiles.get(1), sixCardsSecondPilePos);
        sixCardsOnFieldMap.put(fieldPiles.get(2), sixCardsThirdPilePos);
        sixCardsOnFieldMap.put(fieldPiles.get(3), sixCardsFourthPilePos);
        sixCardsOnFieldMap.put(fieldPiles.get(4), sixCardsFifthPilePos);
        sixCardsOnFieldMap.put(fieldPiles.get(5), sixCardsSixthPilePos);
        mPilePositionsForActiveFieldPiles.put(6, sixCardsOnFieldMap);


        //TODO : remove
        mPilePositionsForActiveFieldPiles.put(7, zeroCardsOnFieldMap);
        mPilePositionsForActiveFieldPiles.put(8, zeroCardsOnFieldMap);
        mPilePositionsForActiveFieldPiles.put(9, zeroCardsOnFieldMap);
        mPilePositionsForActiveFieldPiles.put(10, zeroCardsOnFieldMap);
    }
}
