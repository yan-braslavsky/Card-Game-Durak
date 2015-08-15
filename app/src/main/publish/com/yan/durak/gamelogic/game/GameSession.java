package com.yan.durak.gamelogic.game;


import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.SessionCommand;
import com.yan.durak.gamelogic.commands.hooks.CommandHook;
import com.yan.durak.gamelogic.exceptions.NullCommandException;
import com.yan.durak.gamelogic.player.Player;

import java.util.*;

/**
 * Created by ybra on 19.12.2014.
 * This class holds data that relevant to one game session.
 * Game session runs by executing predefined set of commands.
 */
public class GameSession {

    //represents a stack of piles that used in the session
    //there is no specific purpose definition for each pile,the usage will
    //be defined by the commands
    private Stack<Pile> mPilesStack;

    //Commands that are pending execution
    private Queue<SessionCommand> mPendingCommandsQueue;

    //Commands that are already have been executed
    private Stack<SessionCommand> mExecutedCommandsStack;

    private String trumpSuit;

    private List<Player> mPlayers;

    private IGameRules mGameRules;

    private Map<Class<? extends SessionCommand>, List<CommandHook>> mPreHooksMap;
    private Map<Class<? extends SessionCommand>, List<CommandHook>> mPostHooksMap;

    public GameSession() {
        mPilesStack = new Stack<>();
        mPendingCommandsQueue = new LinkedList<>();
        mExecutedCommandsStack = new Stack<>();
        mPlayers = new ArrayList<>(3);
        mPreHooksMap = new HashMap<>();
        mPostHooksMap = new HashMap<>();
    }

    /**
     * Adding command to execution queue.
     * Command will be executed once it turn will come.
     *
     * @throws NullCommandException in case added command is null.
     */
    public void addCommand(SessionCommand command) {
        if (command == null)
            throw new NullCommandException();

        mPendingCommandsQueue.add(command);
    }

    /**
     * Takes the next command from the queue and executes it
     */
    private void executeNextCommand() {
        executeCommand(mPendingCommandsQueue.poll());
    }

    /**
     * Executes command immediately and adds it to the stack
     * of executed commands
     *
     * @throws NullCommandException in case executed command is null.
     */
    public void executeCommand(SessionCommand command) {

        if (command == null)
            throw new NullCommandException();

        //prepare to execution
        command.setGameSession(this);

        //run pre hooks
        checkForHooks(command, mPreHooksMap);

        //execute
        command.execute();
        mExecutedCommandsStack.push(command);

        //run post hooks
        checkForHooks(command, mPostHooksMap);
    }

    /**
     * Adds a Hook that will be triggered before the execution of associated command.
     */
    public void addPreHook(CommandHook commandHook) {
        addHookToMap(commandHook, mPreHooksMap);
    }

    /**
     * Adds a Hook that will be triggered after the execution of associated command.
     */
    public void addPostHook(CommandHook commandHook) {
        addHookToMap(commandHook, mPostHooksMap);
    }

    private void addHookToMap(CommandHook commandHook, Map<Class<? extends SessionCommand>, List<CommandHook>> hooksMap) {
        //get the hooks list for provided hook
        List<CommandHook> hooksList = hooksMap.get(commandHook.getHookTriggerCommandClass());
        //lazy instantiate the list for given hook
        if (hooksList == null) {
            hooksList = new ArrayList<>();
            hooksMap.put(commandHook.getHookTriggerCommandClass(), hooksList);
        }
        hooksList.add(commandHook);
    }


    /**
     * Returns the first occurrence of the pile that contains provided tag.
     * If pile is not found returns null.
     */
    public Pile findPileByTag(String tag) {

        for (Pile pile : mPilesStack) {
            if (pile.hasTag(tag))
                return pile;
        }

        return null;
    }

    /**
     * Helper method , finds the most recent command that was executed with class provided.
     *
     * @param searchCommandClazz class that defines a command
     * @param <T>                class of the command
     * @return the command if found , null otherwise.
     */
    public <T extends SessionCommand> T searchForRecentCommand(Class<T> searchCommandClazz) {
        //iterate backwards and find the latest requested command
        for (int i = getExecutedCommandsStack().size() - 1; i >= 0; i--) {
            SessionCommand sessionCommand = getExecutedCommandsStack().get(i);
            if (sessionCommand.getClass().isAssignableFrom(searchCommandClazz)) {
                return (T) sessionCommand;
            }
        }
        return null;
    }


    /**
     * Hooks will be searched as concrete classes super classes or interfaces.
     * @param command the command that will be checked for hooks.
     * @param hookMap the map that will be used to search for hooks.
     */
    private void checkForHooks(SessionCommand command, Map<Class<? extends SessionCommand>, List<CommandHook>> hookMap) {

        //if there are no hooks at all , there is nothing to search
        if (hookMap.isEmpty())
            return;

        //obtain the class of the command
        Class<?> clazz = command.getClass();

        //we will go through all superclasses and interfaces of the command
        //until we will search through them all
        while (clazz != null) {

            //check if there is a hook for current class
            if (hookMap.containsKey(clazz)) {

                //trigger all hooks that are found for current class
                List<CommandHook> hookList = hookMap.get(clazz);
                for (CommandHook commandHook : hookList) {
                    commandHook.onHookTrigger(command);
                }
                return;
            }

            //check all interfaces for current class
            for (Class<?> interfaze : clazz.getInterfaces()) {
                //check if there is a hook for current interface
                if (hookMap.containsKey(interfaze)) {
                    //trigger all hooks that are found for current interface
                    List<CommandHook> hookList = hookMap.get(interfaze);
                    for (CommandHook commandHook : hookList) {
                        commandHook.onHookTrigger(command);
                    }
                    return;
                }
            }

            //get next super class
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Starts session by executing commands waiting in queue
     */
    public void startSession() {
        while (!mPendingCommandsQueue.isEmpty()) {
            executeNextCommand();
        }
    }

    /**
     * Helper method , returns index of the next player from provided index in circular manner.
     */
    public int retrieveNextPlayerIndex(int currentPlayerIndex) {
        return (currentPlayerIndex + 1) % getPlayers().size();
    }

    public Stack<Pile> getPilesStack() {
        return mPilesStack;
    }

    public String getTrumpSuit() {
        return trumpSuit;
    }

    public void setTrumpSuit(String trumpSuit) {
        this.trumpSuit = trumpSuit;
    }

    public List<Player> getPlayers() {
        return mPlayers;
    }

    public Stack<SessionCommand> getExecutedCommandsStack() {
        return mExecutedCommandsStack;
    }

    public Queue<SessionCommand> getPendingCommandsQueue() {
        return mPendingCommandsQueue;
    }

    public IGameRules getGameRules() {
        return mGameRules;
    }

    public void setGameRules(IGameRules gameRules) {
        mGameRules = gameRules;
    }

}