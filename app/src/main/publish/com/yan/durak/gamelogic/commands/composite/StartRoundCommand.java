package com.yan.durak.gamelogic.commands.composite;


import com.yan.durak.gamelogic.cards.CardsHelper;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.commands.control.AttackRequestControlCommand;
import com.yan.durak.gamelogic.commands.control.PlayerThrowInRequestControlCommand;
import com.yan.durak.gamelogic.commands.control.RetaliationExecutionControlCommand;
import com.yan.durak.gamelogic.commands.control.RetaliationValidationControlCommand;
import com.yan.durak.gamelogic.commands.custom.*;
import com.yan.durak.gamelogic.game.IGameRules;
import com.yan.durak.gamelogic.player.Player;

import java.util.Collection;

/**
 * Created by Yan-Home on 12/25/2014.
 */
public class StartRoundCommand extends BaseSessionCommand {

    private int mRoundAttackingPlayerIndex;
    private int mRoundDefendingPlayerIndex;

    @Override
    public void execute() {

        //give cards to each player
        dealCardsToPlayers();

        //attacking player performs his move on the retaliator
        performFirstAttack();

        //the retaliator must retaliate from the attack
        performRetaliation();

        //now other players will add cards until
        //the player will retaliate with all his cards or
        //until he will take all field cards into his hands
        //TODO : rethink to use state machine for retaliation and throwins...
        performRetaliationLoop();

        //check for end game conditions
        CheckWinningConditionCommand winningConditionCommand = new CheckWinningConditionCommand();
        getGameSession().executeCommand(winningConditionCommand);

        if (winningConditionCommand.isGameOver()) {

            //Finish the game
            FinishGameCommand finishGameCommand = new FinishGameCommand();
            finishGameCommand.setLoosingPlayerIndex(winningConditionCommand.getLoosingPlayerIndex());
            getGameSession().executeCommand(finishGameCommand);
        } else {

            //proceed to the next round
            startNextRound();
        }

        //TODO : use hook to log piles instead of a command
        //log the status at the end of the round
        getGameSession().executeCommand(new LogPilesStatusCommand());

    }


    /**
     * All non retaliating players getting a chance to throw in some cards
     *
     * @return total amount of throw in cards
     */
    private int performThrowIn() {

        int totalThrowInAmount = 0;
        int amountOfCardsAllowedToThrowIn = findAmountOfCardsAllowedToThrowIn();

        //throw in command for attacker
        totalThrowInAmount += letPlayerThrowInCards(amountOfCardsAllowedToThrowIn, mRoundAttackingPlayerIndex);

        //we need to find how much cards is it possible to throw in
        amountOfCardsAllowedToThrowIn = findAmountOfCardsAllowedToThrowIn();
        if (amountOfCardsAllowedToThrowIn > 0) {

            //throw in command for third player not the retaliator and not the attacker of this round
            totalThrowInAmount += letPlayerThrowInCards(amountOfCardsAllowedToThrowIn, getGameSession().retrieveNextPlayerIndex(mRoundDefendingPlayerIndex));
        }

        return totalThrowInAmount;
    }

    /**
     * @param amountOfCardsAllowedToThrowIn
     * @param throwingInPlayer
     * @return amount of throwed in cards
     */
    private int letPlayerThrowInCards(int amountOfCardsAllowedToThrowIn, int throwingInPlayer) {

        if (amountOfCardsAllowedToThrowIn < 1)
            return 0;

        //If player don't have cards that he can throw in , than skip this step
        Collection<String> allowedRanksToThrowIn = PlayerThrowInRequestCommand.findAllowedRanksToThrowIn(getGameSession());

        //find pile of player that is requested to throw in
        Pile pile = getGameSession().getPilesStack().get(getGameSession().getPlayers().get(throwingInPlayer).getPileIndex());

        //when player does not have any card that he can possibly throw in , just skip this request
        if (!CardsHelper.isOneOfTheRanksInPile(allowedRanksToThrowIn, pile.getCardsInPile()))
            return 0;

        PlayerThrowInRequestCommand throwInRequestCommand = new PlayerThrowInRequestCommand();
        throwInRequestCommand.setThrowingInPlayer(throwingInPlayer);
        throwInRequestCommand.setThrowInAmount(amountOfCardsAllowedToThrowIn);
        throwInRequestCommand.setAllowedRanksToThrowIn(allowedRanksToThrowIn);
        getGameSession().executeCommand(throwInRequestCommand);

        //control the throw in command
        getGameSession().executeCommand(new PlayerThrowInRequestControlCommand());

        return throwInRequestCommand.getThrowInCards().size();
    }

    /**
     * Players will attack retaliator until he will retaliate with all his cards
     * or  until he will take all field cards into his hands
     */
    private void performRetaliationLoop() {

        //we need to analyze current field status to know how to proceed
        CheckFieldPilesStatusCommand fieldPilesStatus = new CheckFieldPilesStatusCommand();
        getGameSession().executeCommand(fieldPilesStatus);

        if (fieldPilesStatus.isEveryFieldPileCovered()) {

            //player retaliated all the cards and now he is
            //open to throw in attacks
            onSuccessfulRetaliation();

        } else {

            //Player couldn't retaliate all the cards
            //and now he will take all field cards and
            //some throw ins
            onNotSuccessfulRetaliation();
        }
    }

    private void onNotSuccessfulRetaliation() {

        //get the defending player
        Player defendingPlayer = getGameSession().getPlayers().get(mRoundDefendingPlayerIndex);

        //Defending player did not cover all the cards ,he states his intention to take cards, no actual cards movement will be done in this command
        PlayerTakesCardsFromFieldCommand playerTakesCardsFromFieldCommand = new PlayerTakesCardsFromFieldCommand();
        playerTakesCardsFromFieldCommand.setTakingPlayer(defendingPlayer);
        getGameSession().executeCommand(playerTakesCardsFromFieldCommand);

        //before player will take all the cards
        //other player will have a chance to throw in
        performThrowIn();

        // all field cards will go to user hand .
        MoveAllFieldPilesCardsCommand moveAllFieldPilesCardsCommand = new MoveAllFieldPilesCardsCommand();
        moveAllFieldPilesCardsCommand.setToPileIndex(defendingPlayer.getPileIndex());
        getGameSession().executeCommand(moveAllFieldPilesCardsCommand);
    }

    private void onSuccessfulRetaliation() {

        //we need to find how much cards is it possible to throw in
        if (findAmountOfCardsAllowedToThrowIn() < 1) {

            //player covered all cards and no more cards left in his hand
            //all field cards will be moved to discard now .
            moveAllFieldCardsToDiscardPile();
            return;
        }

        //players will throw in some cards
        int totalTrowedInCards = performThrowIn();

        if (totalTrowedInCards < 1) {
            //means nobody didn't throw in any cards
            //we should clean field piles to discard pile
            moveAllFieldCardsToDiscardPile();
        } else {

            //let player retaliate all added cards
            performRetaliation();

            //now other players will add cards until
            //the player will retaliate with all his cards or
            //until he will take all field cards into his hands
            performRetaliationLoop();
        }
    }

    /**
     * Moves all the cards in the field into discard pile and
     * removes the field piles.
     */
    private void moveAllFieldCardsToDiscardPile() {
        MoveAllFieldPilesCardsCommand moveAllFieldPilesCardsCommand = new MoveAllFieldPilesCardsCommand();
        moveAllFieldPilesCardsCommand.setToPileIndex(getGameSession().getPilesStack().indexOf(getGameSession().findPileByTag(Pile.PileTags.DISCARD_PILE_TAG)));
        getGameSession().executeCommand(moveAllFieldPilesCardsCommand);
    }


    /**
     * Returns the amount of cards that is allowed to throw in to the player for retaliation
     */
    private int findAmountOfCardsAllowedToThrowIn() {
        CheckFieldPilesStatusCommand fieldPilesStatus = new CheckFieldPilesStatusCommand();
        getGameSession().executeCommand(fieldPilesStatus);
        int retaliatorPileCardsInHand = getGameSession().getPilesStack().get(getGameSession().getPlayers().get(mRoundDefendingPlayerIndex).getPileIndex()).getCardsInPile().size();

        //get field piles status
        int uncoveredFieldPilesAmount = fieldPilesStatus.getUncoveredPiles().size();
        int coveredFieldPilesAmount = fieldPilesStatus.getCoveredPiles().size();
        int totalPilesOnField = uncoveredFieldPilesAmount + coveredFieldPilesAmount;

        //calculate the amount that is allowed to be throwed in
        return Math.min(IGameRules.MAX_PILES_ON_FIELD_AMOUNT - totalPilesOnField, retaliatorPileCardsInHand - uncoveredFieldPilesAmount);
    }

    /**
     * Identifying next round attacker and defender
     * and proceeding to the next round
     */
    private void startNextRound() {
        //identify next round attackers and defenders
        IdentifyNextRoundPlayersCommand identifyCommand = new IdentifyNextRoundPlayersCommand();
        getGameSession().executeCommand(identifyCommand);

        //Start a new round
        StartRoundCommand startRoundCommand = new StartRoundCommand();
        startRoundCommand.setRoundAttackingPlayerIndex(identifyCommand.getNextRoundAttackerPlayerIndex());
        startRoundCommand.setRoundDefendingPlayerIndex(identifyCommand.getNextRoundDefenderPlayerIndex());

        //in order to let this command to end ,we are "adding" the next round command , rather than execute it immediately
        getGameSession().addCommand(startRoundCommand);
    }

    /**
     * Player retaliates the field piles and the retaliation
     * gets validated and executed.
     */
    private void performRetaliation() {
        //request player retaliation
        PlayerRetaliationRequestCommand playerRetaliationRequest = new PlayerRetaliationRequestCommand();
        playerRetaliationRequest.setPlayerIndex(mRoundDefendingPlayerIndex);
        getGameSession().executeCommand(playerRetaliationRequest);

        //we need validate the retaliation response from the player
        RetaliationValidationControlCommand retaliationValidationCommand = new RetaliationValidationControlCommand();
        getGameSession().executeCommand(retaliationValidationCommand);

        //now we need to make sure the response from the player is according to the rules
        //we will request player to retaliate until he will provide correct cards
        while (!retaliationValidationCommand.getFailedValidationsList().isEmpty()) {

            //TODO : warning ! we are reusing the same command ! Make sure there is no leftovers from previous executions !

            //request player retaliation again
            getGameSession().executeCommand(playerRetaliationRequest);

            //validate retaliation by checking if covering cards are actually valid.
            //Not all piles has to be covered , but the ones that are covered , need to be valid
            getGameSession().executeCommand(retaliationValidationCommand);
        }

        //retaliation control command will do cards displacement
        getGameSession().executeCommand(new RetaliationExecutionControlCommand());
    }

    private void performFirstAttack() {
        //let starting player walk on nearest player
        PlayerAttackRequestCommand letPlayerWalkOnOtherPlayer = new PlayerAttackRequestCommand();
        letPlayerWalkOnOtherPlayer.setAttackingPlayerIndex(mRoundAttackingPlayerIndex);
        letPlayerWalkOnOtherPlayer.setDefendingPlayerIndex(mRoundDefendingPlayerIndex);
        getGameSession().executeCommand(letPlayerWalkOnOtherPlayer);

        //Notice : We don't have to validate the attack response.
        //Player can attack with any card  he wants.

        //control the attack command executes the actual card movement
        getGameSession().executeCommand(new AttackRequestControlCommand());
    }

    /**
     * Each player will fill his hand until it is full or the stack pile is empty
     */
    private void dealCardsToPlayers() {
        for (Player player : getGameSession().getPlayers()) {
            CompletePlayerHandCommand completePlayerHandCommand = new CompletePlayerHandCommand();
            completePlayerHandCommand.setPlayerIndex(player.getGameIndex());
            getGameSession().executeCommand(completePlayerHandCommand);
        }
    }

    public int getRoundAttackingPlayerIndex() {
        return mRoundAttackingPlayerIndex;
    }

    public void setRoundAttackingPlayerIndex(int roundAttackingPlayerIndex) {
        mRoundAttackingPlayerIndex = roundAttackingPlayerIndex;
    }

    public int getRoundDefendingPlayerIndex() {
        return mRoundDefendingPlayerIndex;
    }

    public void setRoundDefendingPlayerIndex(int roundDefendingPlayerIndex) {
        mRoundDefendingPlayerIndex = roundDefendingPlayerIndex;
    }
}
