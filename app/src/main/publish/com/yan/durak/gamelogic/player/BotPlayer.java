package com.yan.durak.gamelogic.player;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.CardsHelper;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerMetaData;
import com.yan.durak.gamelogic.game.GameSession;
import com.yan.durak.gamelogic.utils.math.MathHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Yan-Home on 12/21/2014.
 */
public class BotPlayer extends BasePlayer {

    public static final int MIN_THINKING_MILLISECONDS = 1500;
    public static final int MAX_THINKING_MILLISECONDS = 3500;

    public BotPlayer(final int indexInGame, final GameSession gameSession
            , final int pileIndex, final PlayerMetaData playerMetaData) {
        super(indexInGame, gameSession, pileIndex, playerMetaData);
    }

    @Override
    public Card getCardForAttack() {
        final Pile playerPile = mGameSession.getPilesStack().get(getPileIndex());

        if (playerPile.getCardsInPile().isEmpty())
            return null;

        //TODO : simulate thinking
        simulateThinking();

        //just pick a random card , since this bot is not very smart
        final Card cardForAttack = playerPile.getCardsInPile().get(MathHelper.randomInRange(0, playerPile.getCardsInPile().size() - 1));
        return cardForAttack;
    }


    @Override
    public List<Pile> retaliatePiles(final List<Pile> pilesToRetaliate) {

        //TODO : simulate thinking
        simulateThinking();

        //create a deep copy of original piles
        final List<Pile> pilesAfterRetaliation = createDeepCopyOfPiles(pilesToRetaliate);

        //we creating player pile for temporary use
        final Pile playerHandPileCopy = createPileDeepCopy(obtainPlayerPile());

        //try to retaliate using non trump cards
        for (final Pile pile : pilesAfterRetaliation) {
            final Card cardToRetaliate = pile.getCardsInPile().get(0);
            final List<Card> filteredCards = CardsHelper.filterCardsBySuit(playerHandPileCopy, cardToRetaliate.getSuit());

            for (final Card filteredCard : filteredCards) {
                //if current card is bigger than card that needs to be retaliated , then use it for retaliation
                if (CardsHelper.compareCards(cardToRetaliate, filteredCard, mGameSession.getTrumpSuit()) > 0) {
                    //add card to field pile
                    pile.addCardToPile(filteredCard);
                    //remove card from player pile
                    playerHandPileCopy.removeCardFromPile(filteredCard);
                    break;
                }
            }
        }


        //try to retaliate using trumps from the remaining cards in hand
        for (final Pile pile : pilesAfterRetaliation) {

            //we trying only on not retaliated piles
            if (pile.getCardsInPile().size() > 1)
                continue;

            final Card cardToRetaliate = pile.getCardsInPile().get(0);
            final List<Card> filteredTrumpCards = CardsHelper.filterCardsBySuit(playerHandPileCopy, mGameSession.getTrumpSuit());

            for (final Card filteredCard : filteredTrumpCards) {
                //if current card is bigger than card that need to retaliate , then use it for retaliation
                if (CardsHelper.compareCards(cardToRetaliate, filteredCard, mGameSession.getTrumpSuit()) > 0) {
                    //add card to field pile
                    pile.addCardToPile(filteredCard);
                    //remove card from player pile
                    playerHandPileCopy.removeCardFromPile(filteredCard);
                    break;
                }
            }
        }

        return pilesAfterRetaliation;
    }

    private void simulateThinking() {
        try {
            Thread.sleep(MathHelper.randomInRange(MIN_THINKING_MILLISECONDS, MAX_THINKING_MILLISECONDS));
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Card> getThrowInCards(final Collection<String> allowedRanksToThrowIn, final int allowedAmountOfCardsToThrowIn) {

        //TODO : simulate thinking
        simulateThinking();

        //Bot player has 50/50 chance to pass his throw in
        if (MathHelper.randomInRange(0, 500) > 250)
            return new ArrayList<>();

        final List<Card> retList = getPossibleThrowInCards(allowedRanksToThrowIn);
        return (retList.size() > allowedAmountOfCardsToThrowIn) ? retList.subList(0, allowedAmountOfCardsToThrowIn) : retList;
    }

    private List<Pile> createDeepCopyOfPiles(final List<Pile> pilesToRetaliate) {
        final List<Pile> pilesAfterRetaliation = new ArrayList<>();

        for (final Pile pile : pilesToRetaliate) {
            final Pile pileCopy = createPileDeepCopy(pile);
            pilesAfterRetaliation.add(pileCopy);
        }
        return pilesAfterRetaliation;
    }

    private Pile createPileDeepCopy(final Pile pile) {
        final Pile pileCopy = new Pile();
        for (int i = 0; i < pile.getCardsInPile().size(); i++) {
            final Card card = pile.getCardsInPile().get(i);
            pileCopy.addCardToPile(card);
        }
        return pileCopy;
    }


    private Pile obtainPlayerPile() {
        return mGameSession.getPilesStack().get(getPileIndex());
    }
}
