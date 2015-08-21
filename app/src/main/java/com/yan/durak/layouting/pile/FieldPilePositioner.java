package com.yan.durak.layouting.pile;

import com.yan.durak.models.PileModel;
import com.yan.durak.services.PileManagerService;

import java.util.HashMap;
import java.util.List;

import glengine.yan.glengine.service.ServiceLocator;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;
import glengine.yan.glengine.util.geometry.YANVector2;

import static glengine.yan.glengine.service.ServiceLocator.locateService;

/**
 * Created by Yan-Home on 5/3/2015.
 */
public class FieldPilePositioner {

    /**
     * Key represents the amount of active piles on field
     * Pair key represents the pile and the value represents pile position
     */
    private HashMap<Integer, HashMap<PileModel, YANReadOnlyVector2>> mPilePositionsForActiveFieldPiles;
    private static final YANVector2 ZERO_VECTOR = new YANVector2();

    public FieldPilePositioner() {
        mPilePositionsForActiveFieldPiles = new HashMap<>();
    }

    public YANReadOnlyVector2 getPositionForPile(final PileModel pile) {
        final YANReadOnlyVector2 vector2 = mPilePositionsForActiveFieldPiles.get(calculateActivePilesAmount()).get(pile);

        //FIXME : in some situations there is no suitable vector found
        //FIXME : once server will be limited to 6 cards on field only , try to remove this code
        if (vector2 == null)
            return ZERO_VECTOR;

        return vector2;
    }

    private int calculateActivePilesAmount() {
        int activePilesAmount = 0;
        for (int i = 0; i < locateService(PileManagerService.class).getFieldPiles().size(); i++) {
            final PileModel fieldPile = locateService(PileManagerService.class).getFieldPiles().get(i);
            if (!fieldPile.getCardsInPile().isEmpty())
                activePilesAmount++;
        }
        return activePilesAmount;
    }

    public void init(final float sceneWidth, final float sceneHeight, final float cardWidth, final float cardHeight) {

        //zero vector is initialized to the center of the screen
        ZERO_VECTOR.setXY(sceneWidth / 2, sceneHeight / 2);

        //cache some values
        final float halfCardWidth = cardWidth / 2;

        //get reference to field piles
        final List<PileModel> fieldPiles = locateService(PileManagerService.class).getFieldPiles();

        //zero piles
        final HashMap<PileModel, YANReadOnlyVector2> zeroCardsOnFieldMap = new HashMap<>();
        for (int i = 0; i < fieldPiles.size(); i++) {
            final PileModel fieldPile = fieldPiles.get(i);
            zeroCardsOnFieldMap.put(fieldPile, new YANVector2());
        }

        //one pile
        final YANVector2 oneCardFirstPilePos = new YANVector2((sceneWidth - cardWidth) / 2, (sceneHeight - cardHeight) / 2);
        final HashMap<PileModel, YANReadOnlyVector2> oneCardOnFieldMap = new HashMap<>();
        oneCardOnFieldMap.put(fieldPiles.get(0), oneCardFirstPilePos);
        mPilePositionsForActiveFieldPiles.put(1, oneCardOnFieldMap);

        //two piles
        final YANVector2 twoCardsFirstPilePos = new YANVector2(oneCardFirstPilePos.getX() - cardWidth, oneCardFirstPilePos.getY());
        final YANVector2 twoCardsSecondPilePos = new YANVector2(oneCardFirstPilePos.getX() + cardWidth, oneCardFirstPilePos.getY());
        final HashMap<PileModel, YANReadOnlyVector2> twoCardsOnFieldMap = new HashMap<>();
        twoCardsOnFieldMap.put(fieldPiles.get(0), twoCardsFirstPilePos);
        twoCardsOnFieldMap.put(fieldPiles.get(1), twoCardsSecondPilePos);
        mPilePositionsForActiveFieldPiles.put(2, twoCardsOnFieldMap);

        //three piles
        final YANVector2 threeCardsFirstPilePos = new YANVector2(twoCardsFirstPilePos.getX(), twoCardsFirstPilePos.getY() - (cardHeight));
        final YANVector2 threeCardsSecondPilePos = new YANVector2(twoCardsSecondPilePos.getX(), twoCardsSecondPilePos.getY() - (cardHeight));
        final YANVector2 threeCardsThirdPilePos = new YANVector2(oneCardFirstPilePos.getX(), oneCardFirstPilePos.getY() + (cardHeight));
        final HashMap<PileModel, YANReadOnlyVector2> threeCardsOnFieldMap = new HashMap<>();
        threeCardsOnFieldMap.put(fieldPiles.get(0), threeCardsFirstPilePos);
        threeCardsOnFieldMap.put(fieldPiles.get(1), threeCardsSecondPilePos);
        threeCardsOnFieldMap.put(fieldPiles.get(2), threeCardsThirdPilePos);
        mPilePositionsForActiveFieldPiles.put(3, threeCardsOnFieldMap);

        //four piles
        final YANVector2 fourCardsFirstPilePos = new YANVector2(threeCardsFirstPilePos.getX(), threeCardsFirstPilePos.getY());
        final YANVector2 fourCardsSecondPilePos = new YANVector2(threeCardsSecondPilePos.getX(), threeCardsSecondPilePos.getY());
        final YANVector2 fourCardsThirdPilePos = new YANVector2(threeCardsFirstPilePos.getX(), threeCardsThirdPilePos.getY());
        final YANVector2 fourCardsFourthPilePos = new YANVector2(threeCardsSecondPilePos.getX(), threeCardsThirdPilePos.getY());
        final HashMap<PileModel, YANReadOnlyVector2> fourCardsOnFieldMap = new HashMap<>();
        fourCardsOnFieldMap.put(fieldPiles.get(0), fourCardsFirstPilePos);
        fourCardsOnFieldMap.put(fieldPiles.get(1), fourCardsSecondPilePos);
        fourCardsOnFieldMap.put(fieldPiles.get(2), fourCardsThirdPilePos);
        fourCardsOnFieldMap.put(fieldPiles.get(3), fourCardsFourthPilePos);
        mPilePositionsForActiveFieldPiles.put(4, fourCardsOnFieldMap);

        //five piles
        final YANVector2 fiveCardsFirstPilePos = new YANVector2(fourCardsFirstPilePos.getX() - halfCardWidth, fourCardsFirstPilePos.getY());
        final YANVector2 fiveCardsSecondPilePos = new YANVector2(fourCardsSecondPilePos.getX() + halfCardWidth, fourCardsSecondPilePos.getY());
        final YANVector2 fiveCardsThirdPilePos = new YANVector2(fourCardsThirdPilePos.getX() - halfCardWidth, fourCardsThirdPilePos.getY());
        final YANVector2 fiveCardsFourthPilePos = new YANVector2(fourCardsFourthPilePos.getX() + halfCardWidth, fourCardsFourthPilePos.getY());
        final YANVector2 fiveCardsFifthPilePos = new YANVector2(oneCardFirstPilePos.getX(), oneCardFirstPilePos.getY());
        final HashMap<PileModel, YANReadOnlyVector2> fiveCardsOnFieldMap = new HashMap<>();
        fiveCardsOnFieldMap.put(fieldPiles.get(0), fiveCardsFirstPilePos);
        fiveCardsOnFieldMap.put(fieldPiles.get(1), fiveCardsSecondPilePos);
        fiveCardsOnFieldMap.put(fieldPiles.get(2), fiveCardsThirdPilePos);
        fiveCardsOnFieldMap.put(fieldPiles.get(3), fiveCardsFourthPilePos);
        fiveCardsOnFieldMap.put(fieldPiles.get(4), fiveCardsFifthPilePos);
        mPilePositionsForActiveFieldPiles.put(5, fiveCardsOnFieldMap);


        //six piles
        final YANVector2 sixCardsFirstPilePos = new YANVector2(fiveCardsFirstPilePos.getX() - halfCardWidth, fiveCardsFirstPilePos.getY());
        final YANVector2 sixCardsSecondPilePos = new YANVector2(fiveCardsSecondPilePos.getX() + halfCardWidth, fiveCardsSecondPilePos.getY());
        final YANVector2 sixCardsThirdPilePos = new YANVector2(fiveCardsThirdPilePos.getX() - halfCardWidth, fiveCardsThirdPilePos.getY());
        final YANVector2 sixCardsFourthPilePos = new YANVector2(fiveCardsFourthPilePos.getX() + halfCardWidth, fiveCardsFourthPilePos.getY());
        final YANVector2 sixCardsFifthPilePos = new YANVector2(fiveCardsFifthPilePos.getX(), fiveCardsFifthPilePos.getY() - cardHeight);
        final YANVector2 sixCardsSixthPilePos = new YANVector2(fiveCardsFifthPilePos.getX(), fiveCardsFifthPilePos.getY() + cardHeight);
        final HashMap<PileModel, YANReadOnlyVector2> sixCardsOnFieldMap = new HashMap<>();
        sixCardsOnFieldMap.put(fieldPiles.get(0), sixCardsFirstPilePos);
        sixCardsOnFieldMap.put(fieldPiles.get(1), sixCardsSecondPilePos);
        sixCardsOnFieldMap.put(fieldPiles.get(2), sixCardsThirdPilePos);
        sixCardsOnFieldMap.put(fieldPiles.get(3), sixCardsFourthPilePos);
        sixCardsOnFieldMap.put(fieldPiles.get(4), sixCardsFifthPilePos);
        sixCardsOnFieldMap.put(fieldPiles.get(5), sixCardsSixthPilePos);
        mPilePositionsForActiveFieldPiles.put(6, sixCardsOnFieldMap);


        //TODO : when server throws in cards he sometime uses piles beyond 6
        //FIXME : once server will be limited to 6 cards on field only , remove this code
        mPilePositionsForActiveFieldPiles.put(7, zeroCardsOnFieldMap);
        mPilePositionsForActiveFieldPiles.put(8, zeroCardsOnFieldMap);
        mPilePositionsForActiveFieldPiles.put(9, zeroCardsOnFieldMap);
        mPilePositionsForActiveFieldPiles.put(10, zeroCardsOnFieldMap);
    }
}
