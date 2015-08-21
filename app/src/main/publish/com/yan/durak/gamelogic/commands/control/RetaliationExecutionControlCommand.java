package com.yan.durak.gamelogic.commands.control;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.core.MoveCardFromPileToPileCommand;
import com.yan.durak.gamelogic.commands.custom.PlayerRetaliationRequestCommand;
import com.yan.durak.gamelogic.player.IPlayer;

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
        final PlayerRetaliationRequestCommand retaliationRequestCommand = searchForRecentControlledCommand();
        final List<Pile> pilesBefore = retaliationRequestCommand.getPilesPendingRetaliation();
        final List<Pile> pilesAfter = retaliationRequestCommand.getRetaliatedPiles();
        final IPlayer retaliator = getGameSession().getPlayers().get(retaliationRequestCommand.getPlayerIndex());

        //check what cards should be moved where
        for (final Pile pileAfter : pilesAfter) {

            //the pile is covered
            if (pileAfter.getCardsInPile().size() > 1) {
                //search for corresponding pile in before array
                final Pile pileBefore = searchPileBefore(pileAfter, pilesBefore);

                //copy the cards in pile after to make sure we are not violating them
                final ArrayList<Card> cardsInPileAfter = new ArrayList<>(pileAfter.getCardsInPile());

                //find the additional card that is added into the pile
                //by removing the initial card from pile after
                cardsInPileAfter.remove(pileBefore.getCardsInPile().get(0));

                //the card that remained is the one that was added
                final Card cardAdded = cardsInPileAfter.get(0);

                //issue move command that will make the confirmed transaction
                issueMoveCommand(cardAdded, pileBefore, getGameSession().getPilesStack().get(retaliator.getPileIndex()));
            }
        }
    }

    /**
     * Dispatches the move command
     */
    private void issueMoveCommand(final Card cardToBeMoved, final Pile toPile, final Pile fromPile) {
        final MoveCardFromPileToPileCommand moveCardFromPileToPileCommand = new MoveCardFromPileToPileCommand();
        moveCardFromPileToPileCommand.setCardToMove(cardToBeMoved);
        moveCardFromPileToPileCommand.setFromPileIndex(getGameSession().getPilesStack().indexOf(fromPile));
        moveCardFromPileToPileCommand.setToPileIndex(getGameSession().getPilesStack().indexOf(toPile));
        getGameSession().executeCommand(moveCardFromPileToPileCommand);
    }

    /**
     * Searches pile in the before array that corresponds to the new pile
     */
    private Pile searchPileBefore(final Pile pile, final List<Pile> pilesBefore) {
        for (int i = 0; i < pilesBefore.size(); i++) {
            final Pile pileBefore = pilesBefore.get(i);
            for (final Card cardInPileAfter : pile.getCardsInPile()) {
                if (pileBefore.getCardsInPile().contains(cardInPileAfter)) {
                    //we have found the pile
                    return pileBefore;
                }
            }
        }
        return null;
    }
}
