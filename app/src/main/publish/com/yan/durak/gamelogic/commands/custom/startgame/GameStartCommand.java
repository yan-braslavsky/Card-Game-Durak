package com.yan.durak.gamelogic.commands.custom.startgame;


import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.commands.composite.PrepareGameSessionCommand;
import com.yan.durak.gamelogic.commands.composite.StartRoundCommand;
import com.yan.durak.gamelogic.commands.core.AddBotPlayerCommand;
import com.yan.durak.gamelogic.commands.core.AddRemotePlayerCommand;
import com.yan.durak.gamelogic.commands.custom.IdentifyNextRoundPlayersCommand;
import com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast.BotJoinsGameBroadcastHook;
import com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast.GameOverBroadcastHook;
import com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast.PlayerActionAttackBroadcastHook;
import com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast.PlayerActionRetaliateBroadcastHook;
import com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast.PlayerActionTakeCardsBroadcastHook;
import com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast.PlayerActionThrowInBroadcastPostHook;
import com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast.PlayerActionThrowInBroadcastPreHook;
import com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast.RemoteClientJoinsGameBroadcastHook;
import com.yan.durak.gamelogic.commands.hooks.notifiers.broadcast.RemoteClientsCardsMoveBroadcastHook;
import com.yan.durak.gamelogic.commands.hooks.notifiers.unicast.RemoteClientsGameSetupUnicastHook;
import com.yan.durak.gamelogic.commands.hooks.notifiers.unicast.RemoteClientsWrongCoverageNotifierUnicastHook;
import com.yan.durak.gamelogic.communication.connection.ConnectedPlayer;
import com.yan.durak.gamelogic.validation.GameSessionValidations;

/**
 * Created by Yan-Home on 12/22/2014.
 * <p/>
 * This command starts the game with provided player clients.
 */
public class GameStartCommand extends BaseSessionCommand {

    private ConnectedPlayer[] mConnectedPlayers;

    @Override
    public void execute() {

        //in order to keep our structure modular
        //we are using hooks to analyse commands and
        //take desired actions when needed.
        addHooks();

        //clear the game session and put discard and stock piles
        getGameSession().executeCommand(new PrepareGameSessionCommand());

        //add players to the game
        addPlayers();

        //define who attacks and who defends
        final IdentifyNextRoundPlayersCommand identifyCommand = new IdentifyNextRoundPlayersCommand();
        getGameSession().executeCommand(identifyCommand);

        //let player attack and the next player by it to defend
        final StartRoundCommand startRoundCommand = new StartRoundCommand();
        startRoundCommand.setRoundAttackingPlayerIndex(identifyCommand.getNextRoundAttackerPlayerIndex());
        startRoundCommand.setRoundDefendingPlayerIndex(identifyCommand.getNextRoundDefenderPlayerIndex());
        getGameSession().executeCommand(startRoundCommand);
    }

    private void addPlayers() {
        GameSessionValidations.validateAmountOfPlayersInGame(mConnectedPlayers.length, getGameSession().getGameRules());

        for (int i = 0; i < mConnectedPlayers.length; i++) {
            addPlayer(mConnectedPlayers[i]);
        }
    }

    private void addPlayer(final ConnectedPlayer connectedPlayer) {

        //if there is no remote client for the player
        //add a bot player instead
        if (connectedPlayer.getRemoteClient() == null) {
            final AddBotPlayerCommand addBotPlayerCommand = new AddBotPlayerCommand();
            addBotPlayerCommand.setPlayerMetaData(connectedPlayer.getPlayerMetaData());
            getGameSession().executeCommand(addBotPlayerCommand);
            return;
        }

        //add remote client player
        final AddRemotePlayerCommand addRemotePlayerCommand = new AddRemotePlayerCommand();
        addRemotePlayerCommand.setConnectedPlayer(connectedPlayer);
        getGameSession().executeCommand(addRemotePlayerCommand);
    }

    private void addHooks() {
        //this post hook notifies all remote clients about cards movement
        getGameSession().addPostHook(new RemoteClientsCardsMoveBroadcastHook());

        //this post hook notifies joined remote player about game setup
        //take in mind that it takes only the information that is available for that
        //point of time
        getGameSession().addPostHook(new RemoteClientsGameSetupUnicastHook());

        //notify other remote clients that remote player joins the game
        getGameSession().addPostHook(new RemoteClientJoinsGameBroadcastHook());

        //notify other remote clients that bot joins the game
        getGameSession().addPostHook(new BotJoinsGameBroadcastHook());

        //Those pre hooks are used to notify the next player action
        //That allows the client to change the UI state accordingly
        getGameSession().addPreHook(new PlayerActionAttackBroadcastHook());
        getGameSession().addPreHook(new PlayerActionRetaliateBroadcastHook());
        getGameSession().addPreHook(new PlayerActionThrowInBroadcastPreHook());
        getGameSession().addPreHook(new PlayerActionTakeCardsBroadcastHook());

        //notify of player throw in decision
        getGameSession().addPostHook(new PlayerActionThrowInBroadcastPostHook());

        //add hook that will notify active player of wrong retaliation coverage
        getGameSession().addPostHook(new RemoteClientsWrongCoverageNotifierUnicastHook());

        //add post hook that will notify of game over conditions
        getGameSession().addPostHook(new GameOverBroadcastHook());

    }

    public void setConnectedPlayers(final ConnectedPlayer... remoteClients) {
        this.mConnectedPlayers = remoteClients;
    }

}