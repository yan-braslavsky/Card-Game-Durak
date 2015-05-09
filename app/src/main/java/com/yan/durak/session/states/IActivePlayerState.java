package com.yan.durak.session.states;

import glengine.yan.glengine.util.object_pool.YANIPoolableObject;

/**
 * Created by Yan-Home on 4/26/2015.
 */
public interface IActivePlayerState extends YANIPoolableObject {

    enum ActivePlayerStateDefinition {

        /**
         * When player can take no action.
         * Player only observes others
         */
        OTHER_PLAYER_TURN,

        /**
         * When opponent attacks the player , and the player
         * must retaliate the attack
         */
        REQUEST_RETALIATION,
        /**
         * When opponent attacks other opponent , and the player
         * can throw in additional cards
         */
        REQUEST_THROW_IN,

        /**
         * When player must start an attack
         * on opponent with any card.
         */
        REQUEST_CARD_FOR_ATTACK;
    }

    /**
     * Provides an enum that defines the state
     *
     * @return
     */
    ActivePlayerStateDefinition getStateDefinition();

    void applyState();
}
