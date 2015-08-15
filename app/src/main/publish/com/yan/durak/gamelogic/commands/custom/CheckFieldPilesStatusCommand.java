package com.yan.durak.gamelogic.commands.custom;


import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan-Home on 12/25/2014.
 * <p/>
 * Command will analise filed piles condition and decide whether all field piles
 * are covered or not.
 */
public class CheckFieldPilesStatusCommand extends BaseSessionCommand {


    private boolean mEveryFieldPileCovered;
    private List<Pile> mUncoveredPiles;
    private List<Pile> mCoveredPiles;

    @Override
    public void execute() {

        //allocating new list
        mUncoveredPiles = new ArrayList<>();
        mCoveredPiles = new ArrayList<>();

        //depending on field piles status , decide what to do next
        mEveryFieldPileCovered = true;

        //filter all field piles
        for (final Pile pile : getGameSession().getPilesStack()) {
            if (pile.hasTag(Pile.PileTags.FIELD_PILE)) {

                //check if the pile is uncovered (has 1 cards)
                if (pile.getCardsInPile().size() == 1) {
                    mEveryFieldPileCovered = false;
                    mUncoveredPiles.add(pile);
                }
                //check if card is covered (has 2 card)
                else if (pile.getCardsInPile().size() == 2) {
                    mCoveredPiles.add(pile);
                }
            }
        }
    }


    public boolean isEveryFieldPileCovered() {
        return mEveryFieldPileCovered;
    }

    public List<Pile> getUncoveredPiles() {
        return mUncoveredPiles;
    }

    public List<Pile> getCoveredPiles() {
        return mCoveredPiles;
    }
}
