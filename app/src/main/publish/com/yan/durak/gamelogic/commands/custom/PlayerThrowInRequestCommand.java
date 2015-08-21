package com.yan.durak.gamelogic.commands.custom;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.game.GameSession;
import com.yan.durak.gamelogic.game.IGameRules;
import com.yan.durak.gamelogic.player.IPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.yan.durak.gamelogic.cards.Pile.PileTags.FIELD_PILE;

/**
 * Created by Yan-Home on 12/21/2014.
 * <p/>
 * The commands will request player to cover attacking cards.
 * Command will store player retaliation information.
 * No cards displacement will be done.
 */
public class PlayerThrowInRequestCommand extends BaseSessionCommand {

    private int mThrowingInPlayer;
    private int mThrowInAmount;
    private List<Card> mThrowInCards;
    private Collection<String> mAllowedRanksToThrowIn;

    @Override
    public void execute() {
        final IPlayer player = getGameSession().getPlayers().get(mThrowingInPlayer);
        mThrowInCards = player.getThrowInCards(mAllowedRanksToThrowIn, mThrowInAmount);
    }

    public static Collection<String> findAllowedRanksToThrowIn(final GameSession gameSession) {

        final Collection<String> retList = new HashSet<>();
        for (int i = 0; i < gameSession.getPilesStack().size(); i++) {
            final Pile pile = gameSession.getPilesStack().get(i);
            if (pile.hasTag(FIELD_PILE)) {
                for (final Card card : pile.getCardsInPile()) {
                    retList.add(card.getRank());
                }
            }
        }

        return retList;
    }

    private List<Pile> retrieveAllCardsInField() {
        final List<Pile> pilesToRetaliate = new ArrayList<>(IGameRules.MAX_PILES_ON_FIELD_AMOUNT);
        //we are choosing all field piles that are not covered yet (contain only one card)
        for (int i = 0; i < getGameSession().getPilesStack().size(); i++) {
            final Pile pile = getGameSession().getPilesStack().get(i);
            if (pile.hasTag(FIELD_PILE) && (pile.getCardsInPile().size() == 1)) {
                pilesToRetaliate.add(pile);
            }
        }
        return pilesToRetaliate;
    }

    public void setThrowingInPlayer(final int throwingInPlayer) {
        mThrowingInPlayer = throwingInPlayer;
    }

    public void setThrowInAmount(final int throwInAmount) {
        mThrowInAmount = throwInAmount;
    }

    public List<Card> getThrowInCards() {
        return mThrowInCards;
    }

    public int getThrowingInPlayerIndex() {
        return mThrowingInPlayer;
    }

    public int getThrowInAmount() {
        return mThrowInAmount;
    }

    public Collection<String> getAllowedRanksToThrowIn() {
        return mAllowedRanksToThrowIn;
    }

    public void setAllowedRanksToThrowIn(final Collection<String> allowedRanksToThrowIn) {
        mAllowedRanksToThrowIn = allowedRanksToThrowIn;
    }
}
