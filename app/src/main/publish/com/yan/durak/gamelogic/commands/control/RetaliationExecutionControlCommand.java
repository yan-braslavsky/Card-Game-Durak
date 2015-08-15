package com.yan.durak.gamelogic.commands.control;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.core.MoveCardFromPileToPileCommand;
import com.yan.durak.gamelogic.commands.custom.PlayerRetaliationRequestCommand;
import com.yan.durak.gamelogic.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan-Home on 12/23/2014.
 * <p/>
 * Looks at the retaliation command status and moves the cards accordingly.
 */
public class RetaliationExecutionControlCommand extends BaseControlCommand<PlayerRetaliationRequestCommand> {
    @Override
    protected Class<PlayerRetaliationRequestCommand> getControlledCommandClass() {
        return PlayerRetaliationRequestCommand.class;
    }

    @Override
    public void execute() {
        PlayerRetaliationRequestCommand retaliationRequestCommand = searchForRecentControlledCommand();
        List<Pile> pilesBefore = retaliationRequestCommand.getPilesPendingRetaliation();
        List<Pile> pilesAfter = retaliationRequestCommand.getRetaliatedPiles();
        Player retaliator = getGameSession().getPlayers().get(retaliationRequestCommand.getPlayerIndex());

        //check what cards should be moved where
        for (Pile pileAfter : pilesAfter) {

            //the pile is covered
            if (pileAfter.getCardsInPile().size() > 1) {
                //search for corresponding pile in before array
                Pile pileBefore = searchPileBefore(pileAfter, pilesBefore);

                //copy the cards in pile after to make sure we are not violating them
                ArrayList<Card> cardsInPileAfter = new ArrayList<>(pileAfter.getCardsInPile());

                //find the additional card that is added into the pile
                //by removing the initial card from pile after
                cardsInPileAfter.remove(pileBefore.getCardsInPile().get(0));

                //the card that remained is the one that was added
                Card cardAdded = cardsInPileAfter.get(0);

                //issue move command that will make the confirmed transaction
                issueMoveCommand(cardAdded, pileBefore, getGameSession().getPilesStack().get(retaliator.getPileIndex()));
            }
        }
    }

    /**
     * Dispatches the move command
     */
    private void issueMoveCommand(Card cardToBeMoved, Pile toPile, Pile fromPile) {
        MoveCardFromPileToPileCommand moveCardFromPileToPileCommand = new MoveCardFromPileToPileCommand();
        moveCardFromPileToPileCommand.setCardToMove(cardToBeMoved);
        moveCardFromPileToPileCommand.setFromPileIndex(getGameSession().getPilesStack().indexOf(fromPile));
        moveCardFromPileToPileCommand.setToPileIndex(getGameSession().getPilesStack().indexOf(toPile));
        getGameSession().executeCommand(moveCardFromPileToPileCommand);
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
}
