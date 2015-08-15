package com.yan.durak.gamelogic.commands.control;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.CardsHelper;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.custom.PlayerRetaliationRequestCommand;
import com.yan.durak.gamelogic.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan-Home on 12/23/2014.
 * <p/>
 * Looks at the retaliation command status and validates the retaliated cards.
 * Stores validation information.
 */
public class RetaliationValidationControlCommand extends BaseControlCommand<PlayerRetaliationRequestCommand> {

    /**
     * Holds the data regarding cards validation
     */
    public static class ValidationDetails {

        private boolean mValid;
        private Card mCoveringCard;
        private Card mCoveredCard;

        public ValidationDetails(boolean valid, Card coveringCard, Card coveredCard) {
            mValid = valid;
            mCoveringCard = coveringCard;
            mCoveredCard = coveredCard;
        }

        public boolean isValid() {
            return mValid;
        }

        public Card getCoveringCard() {
            return mCoveringCard;
        }

        public Card getCoveredCard() {
            return mCoveredCard;
        }
    }


    private ArrayList<ValidationDetails> mFailedValidationsList;
    private Player mRetaliatedPlayer;

    public RetaliationValidationControlCommand() {
        super();
        mFailedValidationsList = new ArrayList<>();
    }

    @Override
    protected Class<PlayerRetaliationRequestCommand> getControlledCommandClass() {
        return PlayerRetaliationRequestCommand.class;
    }

    @Override
    public void execute() {

        //clear the list
        mFailedValidationsList.clear();

        PlayerRetaliationRequestCommand retaliationRequest = searchForRecentControlledCommand();
        List<Pile> pilesBefore = retaliationRequest.getPilesPendingRetaliation();
        List<Pile> pilesAfter = retaliationRequest.getRetaliatedPiles();
        mRetaliatedPlayer =  getGameSession().getPlayers().get(retaliationRequest.getPlayerIndex());

        //check what cards should be moved where
        for (Pile pileAfter : pilesAfter) {

            //the pile is covered
            if (pileAfter.getCardsInPile().size() > 1) {
                //search for corresponding pile in before array
                Pile pileBefore = searchPileBefore(pileAfter, pilesBefore);

                //find the additional card that is added into the pile
                //by removing the initial card from pile after
                Card coveredCard = pileBefore.getCardsInPile().get(0);

                //copy the cards in pile after to make sure we are not violating them
                ArrayList<Card> cardsInPileAfter = new ArrayList<>(pileAfter.getCardsInPile());

                //remove the card that was in the initial pile
                cardsInPileAfter.remove(coveredCard);

                //the card that remained is the one that was added (the covering card)
                Card coveringCard = cardsInPileAfter.get(0);

                //now we can validate if the covering card is actually can cover the underlying card
                ValidationDetails validationDetails = validateCoverage(coveredCard, coveringCard);

                //in case the coverage is not valid , we will store the details of the coverage
                if (!validationDetails.isValid()) {
                    mFailedValidationsList.add(validationDetails);
                }
            }
        }
    }

    /**
     * Validates if the coverage is legal
     *
     * @param bottomCard card that should be covered
     * @param topCard    card that is covering
     */
    private ValidationDetails validateCoverage(Card bottomCard, Card topCard) {

        //compare cards
        int comparisonResult = CardsHelper.compareCards(bottomCard, topCard, getGameSession().getTrumpSuit());

        //in case the top card is smaller than (or equal to) bottom card
        //validation is failed
        boolean isValid = (comparisonResult > 0);

        //return validation details
        return new ValidationDetails(isValid, topCard, bottomCard);
    }

    /**
     * Searches pile in the before array that corresponds to the new pile
     */
    private Pile searchPileBefore(Pile pile, List<Pile> pilesBefore) {
        for (Pile pileBefore : pilesBefore) {
            for (Card cardInPileAfter : pile.getCardsInPile()) {
                if (pileBefore.getCardsInPile().contains(cardInPileAfter)) {
                    //we have found the pile
                    return pileBefore;
                }
            }
        }
        return null;
    }

    public Player getRetaliatedPlayer() {
        return mRetaliatedPlayer;
    }

    public ArrayList<ValidationDetails> getFailedValidationsList() {
        return mFailedValidationsList;
    }
}
