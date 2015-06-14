package com.yan.durak.services;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.models.PileModel;
import com.yan.durak.nodes.CardNode;
import com.yan.durak.services.hud.HudManagementService;
import com.yan.durak.session.GameInfo;
import com.yan.durak.session.states.IActivePlayerState;
import com.yan.durak.session.states.impl.OtherPlayerTurnState;
import com.yan.durak.session.states.impl.ThrowInState;

import java.util.ArrayList;
import java.util.List;

import glengine.yan.glengine.service.IService;
import glengine.yan.glengine.service.ServiceLocator;
import glengine.yan.glengine.util.math.YANMathUtils;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by Yan-Home on 6/13/2015.
 * <p/>
 * PURPOSE :
 * Manages player moves including server messaging , animations and
 * UI changes.
 */
public class PlayerMoveService implements IService {

    /**
     * Depending on the state performs an auto move for a player.
     */
    public void makeAutoMoveForState(IActivePlayerState.ActivePlayerStateDefinition stateDefinition) {
        switch (stateDefinition) {
            case REQUEST_CARD_FOR_ATTACK:
                throwRandomCardToTheField();
                break;
            case REQUEST_RETALIATION:
                makePlayerTakesCardsMove();
                break;
            case REQUEST_THROW_IN:
                throwInWhatIsSelected();
                break;
            case OTHER_PLAYER_TURN:
                //TODO : show some banner ? like player's time is up ?
                break;
            default:
                //Do nothing
                break;
        }
    }

    /**
     * Makes a throw in move for player with cards he has selected so far.
     * Sends a message to the server.
     * As a result also changes states and UI.
     */
    public void throwInWhatIsSelected() {
        //hide the button
        ServiceLocator.locateService(HudManagementService.class).hideFinishButton();

        //cache services
        PileManagerService pileManagerService = ServiceLocator.locateService(PileManagerService.class);
        CardNodesManagerService cardNodesManagerService = ServiceLocator.locateService(CardNodesManagerService.class);

        //get throw in state
        ThrowInState throwInState = (ThrowInState) ServiceLocator.locateService(GameInfo.class).getActivePlayerState();

        //enable all disabled cards
        ArrayList<Card> allowedCardsToThrowIn = throwInState.getAllowedCardsToThrowIn();
        for (Card cardInPile : pileManagerService.getBottomPlayerPile().getCardsInPile()) {
            //in case this cards is not allowed currently to be thrown in
            if (!allowedCardsToThrowIn.contains(cardInPile)) {
                //enable the node back
                CardNode cardNode = cardNodesManagerService.getCardNodeForCard(cardInPile);
                cardNodesManagerService.enableCardNode(cardNode);
            }
        }

        //disable the hand of the player by setting another state
        ServiceLocator.locateService(GameInfo.class).setActivePlayerState(YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class));

        //layout the bottom player pile
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(pileManagerService.getBottomPlayerPile()).layout();

        //send the response to server
        ServiceLocator.locateService(GameServerMessageSender.class).sendThrowInResponse(throwInState.getChosenCardsToThrowIn());

    }

    //used to cache cards for later removal
    private List<Card> _cardsToRemoveCachedList;

    /**
     * Makes the move when player takes all the cards on the field
     * and sends a message to the server.
     * As a result also plays animations and switches the states and UI.
     */
    public void makePlayerTakesCardsMove() {
        //hide the take button
        ServiceLocator.locateService(HudManagementService.class).hideTakeButton();

        PileManagerService pileManagerService = ServiceLocator.locateService(PileManagerService.class);

        //TODO : allocate once !
        _cardsToRemoveCachedList = new ArrayList<>();

        //return all field pile cards to player hands
        for (PileModel pileModel : pileManagerService.getFieldPiles()) {

            _cardsToRemoveCachedList.clear();
            for (Card cardInFieldPile : pileModel.getCardsInPile()) {

                //add this card to player pile
                pileManagerService.getBottomPlayerPile().addCard(cardInFieldPile);
                //remove the card from field pile
                _cardsToRemoveCachedList.add(cardInFieldPile);
            }

            //remove all the cards that we moved to player hands from this pile
            for (Card card : _cardsToRemoveCachedList) {
                pileModel.removeCard(card);
            }
        }

        //disable the hand of the player by setting another state
        ServiceLocator.locateService(GameInfo.class).setActivePlayerState(YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class));

        //layout the bottom player pile
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(pileManagerService.getBottomPlayerPile()).layout();

        //just send empty message to the
        //send the response
        ServiceLocator.locateService(GameServerMessageSender.class).sendResponseRetaliatePiles(null);
    }

    /**
     * Moves the card that is selected for attack
     * to the field and sends the response to server.
     *
     * @param card selected card for attack
     */
    public void makeCardForAttackMove(Card card) {

        //cache services
        GameInfo gameInfo = ServiceLocator.locateService(GameInfo.class);

        //add card to the first field pile
        PileModel firstFieldPile = ServiceLocator.locateService(PileManagerService.class).getFieldPiles().get(0);
        firstFieldPile.addCard(card);

        //layout the field pile
        ServiceLocator.locateService(PileLayouterManagerService.class).getPileLayouterForPile(firstFieldPile).layout();

        //reset the state
        gameInfo.getActivePlayerState().resetState();

        //disable the hand of the player by setting another state
        gameInfo.setActivePlayerState(YANObjectPool.getInstance().obtain(OtherPlayerTurnState.class));

        //we can just send the response
        ServiceLocator.locateService(GameServerMessageSender.class).sendCardForAttackResponse(card);
    }

    private void throwRandomCardToTheField() {
        //we are selecting a random card from player hand and throwing it to the field as if
        //he was selecting it for himself
        List<Card> cardsInBottomPile = ServiceLocator.locateService(PileManagerService.class).getBottomPlayerPile().getCardsInPile();
        Card randomCard = cardsInBottomPile.get((int) YANMathUtils.randomInRange(0, cardsInBottomPile.size() - 1));
        makeCardForAttackMove(randomCard);
    }

    @Override
    public void clearServiceData() {
        //TODO :
    }
}
