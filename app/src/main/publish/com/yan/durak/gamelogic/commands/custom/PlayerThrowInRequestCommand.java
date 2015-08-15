package com.yan.durak.gamelogic.commands.custom;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.game.GameSession;
import com.yan.durak.gamelogic.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
        Player player = getGameSession().getPlayers().get(mThrowingInPlayer);
        mThrowInCards = player.getThrowInCards(mAllowedRanksToThrowIn, mThrowInAmount);
    }

    public static Collection<String> findAllowedRanksToThrowIn(GameSession gameSession) {

        Collection<String> retList = new HashSet<>();
        for (Pile pile : gameSession.getPilesStack()) {
            if (pile.hasTag(Pile.PileTags.FIELD_PILE)) {
                for (Card card : pile.getCardsInPile()) {
                    retList.add(card.getRank());
                }
            }
        }
        
        return retList;
    }

    private List<Pile> retrieveAllCardsInField() {
        List<Pile> pilesToRetaliate = new ArrayList<>();
        //we are choosing all field piles that are not covered yet (contain only one card)
        for (Pile pile : getGameSession().getPilesStack()) {
            if (pile.hasTag(Pile.PileTags.FIELD_PILE) && (pile.getCardsInPile().size() == 1)) {
                pilesToRetaliate.add(pile);
            }
        }
        return pilesToRetaliate;
    }

    public void setThrowingInPlayer(int throwingInPlayer) {
        mThrowingInPlayer = throwingInPlayer;
    }

    public void setThrowInAmount(int throwInAmount) {
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

    public void setAllowedRanksToThrowIn(Collection<String> allowedRanksToThrowIn) {
        mAllowedRanksToThrowIn = allowedRanksToThrowIn;
    }
}
