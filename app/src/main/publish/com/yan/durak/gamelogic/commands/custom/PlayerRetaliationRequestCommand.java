package com.yan.durak.gamelogic.commands.custom;


import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.player.IPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.yan.durak.gamelogic.cards.Pile.PileTags;
import static com.yan.durak.gamelogic.cards.Pile.PileTags.FIELD_PILE;

/**
 * Created by Yan-Home on 12/21/2014.
 * <p/>
 * The commands will request player to cover attacking cards.
 * Command will store player retaliation information.
 * No cards displacement will be done.
 */
public class PlayerRetaliationRequestCommand extends BaseSessionCommand {

    private int mPlayerIndex;
    private List<Pile> mRetaliatedPiles;
    private List<Pile> mPilesPendingRetaliation;

    @Override
    public void execute() {
        final IPlayer retaliator = getGameSession().getPlayers().get(mPlayerIndex);

        //get field piles that are pending retaliation
        mPilesPendingRetaliation = retrievePilesPendingRetaliation();

        //give piles to player for retaliation
        mRetaliatedPiles = retaliator.retaliatePiles(Collections.unmodifiableList(mPilesPendingRetaliation));
    }

    private List<Pile> retrievePilesPendingRetaliation() {
        final List<Pile> pilesToRetaliate = new ArrayList<>();
        //we are choosing all field piles that are not covered yet (contain only one card)
        for (int i = 0; i < getGameSession().getPilesStack().size(); i++) {
            final Pile pile = getGameSession().getPilesStack().get(i);
            if (pile.hasTag(FIELD_PILE) && (pile.getCardsInPile().size() == 1)) {
                pilesToRetaliate.add(pile);
            }
        }
        return pilesToRetaliate;
    }

    public void setPlayerIndex(final int playerIndex) {
        mPlayerIndex = playerIndex;
    }

    public List<Pile> getRetaliatedPiles() {
        return mRetaliatedPiles;
    }

    public List<Pile> getPilesPendingRetaliation() {
        return mPilesPendingRetaliation;
    }

    public int getPlayerIndex() {
        return mPlayerIndex;
    }


}
