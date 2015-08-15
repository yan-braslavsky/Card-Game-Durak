package com.yan.durak.gamelogic.commands.custom;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.utils.LogUtils;

/**
 * Created by ybra on 19.12.2014.
 * <p/>
 * This is a test command .
 */
public class LogPilesStatusCommand extends BaseSessionCommand {

    @Override
    public void execute() {

        if (!LogUtils.LOGGING_ENABLED)
            return;

        LogUtils.log("===================================================================================================================================\n");
        LogUtils.log("Selected trump suit is : " + getGameSession().getTrumpSuit());
        for (int i = 0; i < getGameSession().getPilesStack().size(); i++) {
            Pile pile = getGameSession().getPilesStack().get(i);

            LogUtils.log("************************************************************************\n");
            LogUtils.log("Pile " + i + " : " + pile + " cards count = " + pile.getCardsInPile().size());
            LogUtils.log("--------------------------------------");
            for (Card card : pile.getCardsInPile()) {
                LogUtils.log("Rank : " + card.getRank() + " Suit : " + card.getSuit());
            }
        }
    }
}