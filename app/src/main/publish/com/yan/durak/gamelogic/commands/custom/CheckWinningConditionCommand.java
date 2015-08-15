package com.yan.durak.gamelogic.commands.custom;


import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.player.IPlayer;

/**
 * Created by Yan-Home on 12/25/2014.
 */
public class CheckWinningConditionCommand extends BaseSessionCommand {

    private boolean mGameOver;
    private int mLoosingPlayerIndex = -1;

    @Override
    public void execute() {

        final boolean isStockPileEmpty = getGameSession().findPileByTag(Pile.PileTags.STOCK_PILE_TAG).getCardsInPile().size() == 0;

        if (!isStockPileEmpty)
            return;

        //count gow many player piles are empty.
        //in case there is only one not empty left , that is a game over
        int notEmptyPlayerPilesCount = getGameSession().getPlayers().size();
        for (final Pile pile : getGameSession().getPilesStack()) {
            if (pile.hasTag(Pile.PileTags.PLAYER_PILE_TAG)) {
                if (pile.getCardsInPile().size() == 0) {
                    notEmptyPlayerPilesCount--;
                }
            }
        }

        //check game over condition
        if (notEmptyPlayerPilesCount == 1) {
            mGameOver = true;
            for (final IPlayer player : getGameSession().getPlayers()) {
                final Pile playerPile = getGameSession().getPilesStack().get(player.getPileIndex());
                if (!playerPile.getCardsInPile().isEmpty()) {
                    mLoosingPlayerIndex = player.getGameIndex();
                    break;
                }
            }
        }
    }

    public boolean isGameOver() {
        return mGameOver;
    }

    public int getLoosingPlayerIndex() {
        return mLoosingPlayerIndex;
    }
}
