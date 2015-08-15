package com.yan.durak.gamelogic.commands.control;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.core.MoveCardFromPileToPileCommand;
import com.yan.durak.gamelogic.commands.custom.AddPileCommand;
import com.yan.durak.gamelogic.commands.custom.PlayerThrowInRequestCommand;
import com.yan.durak.gamelogic.player.Player;

import java.util.ArrayList;

/**
 * Created by Yan-Home on 12/22/2014.
 * <p/>
 * This command will analyze attack command and dispatch new commands accordingly.
 */
public class PlayerThrowInRequestControlCommand extends BaseControlCommand<PlayerThrowInRequestCommand> {

    @Override
    public void execute() {

        //find recent controlled command in the stack
        PlayerThrowInRequestCommand throwInRequestCommand = searchForRecentControlledCommand();

        //find attacking player by index
        Player throwingPlayer = getGameSession().getPlayers().get(throwInRequestCommand.getThrowingInPlayerIndex());

        for (Card throwInCard : throwInRequestCommand.getThrowInCards()) {

            //create a pile that will be used as an additional pile on the field
            AddPileCommand addPileCommand = new AddPileCommand();
            addPileCommand.setCards(new ArrayList<Card>());

            //create pile and tag it as field pile
            Pile pile = new Pile();
            pile.addTag(Pile.PileTags.FIELD_PILE);
            addPileCommand.setPile(pile);
            getGameSession().executeCommand(addPileCommand);

            //remove the card from throwing player pile and move it to relevant field pile
            moveCard(pile, throwingPlayer.getPileIndex(), throwInCard);
        }

    }

    private void moveCard(Pile toPile, int fromPile, Card card) {
        MoveCardFromPileToPileCommand moveCardFromPileToPileCommand = new MoveCardFromPileToPileCommand();
        moveCardFromPileToPileCommand.setCardToMove(card);
        moveCardFromPileToPileCommand.setFromPileIndex(fromPile);
        moveCardFromPileToPileCommand.setToPileIndex(getGameSession().getPilesStack().indexOf(toPile));
        getGameSession().executeCommand(moveCardFromPileToPileCommand);
    }

    @Override
    protected Class<PlayerThrowInRequestCommand> getControlledCommandClass() {
        return PlayerThrowInRequestCommand.class;
    }
}
