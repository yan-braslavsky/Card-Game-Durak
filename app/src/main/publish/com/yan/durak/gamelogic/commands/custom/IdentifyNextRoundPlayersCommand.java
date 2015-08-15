package com.yan.durak.gamelogic.commands.custom;


import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.exceptions.SameAttackerAsDefenderException;
import com.yan.durak.gamelogic.player.Player;
import com.yan.durak.gamelogic.utils.math.MathHelper;

/**
 * Created by Yan-Home on 12/26/2014.
 */
public class IdentifyNextRoundPlayersCommand extends BaseSessionCommand {

    private int mNextRoundAttackerPlayerIndex;
    private int mNextRoundDefenderPlayerIndex;

    @Override
    public void execute() {

        //if no retaliation command , then randomly set attacker , defender should be the one next to it
        PlayerAttackRequestCommand attackCommand = getGameSession().searchForRecentCommand(PlayerAttackRequestCommand.class);
        if (attackCommand == null) {
            randomlySetAttackerAndDefender();
            return;
        }

        //get previous defending player
        Player previousDefender = getGameSession().getPlayers().get(attackCommand.getDefendingPlayerIndex());

        //identify if previous defender took all cards or he actually covered everything
        MoveAllFieldPilesCardsCommand movePilesCommand = getGameSession().searchForRecentCommand(MoveAllFieldPilesCardsCommand.class);
        boolean playerTookTheCards = previousDefender.getPileIndex() == movePilesCommand.getToPileIndex();

        if (playerTookTheCards) {
            // took all cards , next attacker is the player after the defender (defender + 1)
            mNextRoundAttackerPlayerIndex = getGameSession().retrieveNextPlayerIndex(previousDefender.getGameIndex());
        } else {
            // the attacker is the current defending player
            mNextRoundAttackerPlayerIndex = previousDefender.getGameIndex();
        }

        //get the next attacker player
        Player nextAttacker = getGameSession().getPlayers().get(mNextRoundAttackerPlayerIndex);

        //if the new attacker has no cards (shift attacker to the next player with cards)
        while (isPlayerHasNoCards(nextAttacker)) {
            mNextRoundAttackerPlayerIndex = getGameSession().retrieveNextPlayerIndex(mNextRoundAttackerPlayerIndex);
            nextAttacker = getGameSession().getPlayers().get(mNextRoundAttackerPlayerIndex);
        }

        //the new defender is the player next to attacker that has cards (and he is not the attacker)
        mNextRoundDefenderPlayerIndex = getGameSession().retrieveNextPlayerIndex(mNextRoundAttackerPlayerIndex);
        Player nextDefender = getGameSession().getPlayers().get(mNextRoundDefenderPlayerIndex);
        while (isPlayerHasNoCards(nextDefender)) {
            mNextRoundDefenderPlayerIndex = getGameSession().retrieveNextPlayerIndex(mNextRoundDefenderPlayerIndex);
            nextDefender = getGameSession().getPlayers().get(mNextRoundAttackerPlayerIndex);
        }

        //not allowed situation when attacker is the same as defender
        if (mNextRoundAttackerPlayerIndex == mNextRoundDefenderPlayerIndex) {
            throw new SameAttackerAsDefenderException(mNextRoundAttackerPlayerIndex, mNextRoundDefenderPlayerIndex);
        }

    }

    private boolean isPlayerHasNoCards(Player nextDefender) {
        return getGameSession().getPilesStack().get(nextDefender.getPileIndex()).getCardsInPile().isEmpty();
    }

    private void randomlySetAttackerAndDefender() {
        mNextRoundAttackerPlayerIndex = MathHelper.randomInRange(0, getGameSession().getPlayers().size() - 1);
        mNextRoundDefenderPlayerIndex = getGameSession().retrieveNextPlayerIndex(mNextRoundAttackerPlayerIndex);
    }


    public int getNextRoundAttackerPlayerIndex() {
        return mNextRoundAttackerPlayerIndex;
    }

    public int getNextRoundDefenderPlayerIndex() {
        return mNextRoundDefenderPlayerIndex;
    }


}
