package com.yan.durak.communication.client.local;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Yan-Home on 2/23/2015.
 */
public class SharedLocalMessageQueue {

    private static SharedLocalMessageQueue INSTANCE = new SharedLocalMessageQueue();
    private Queue<String> mForClientMessageQueue;
    private Queue<String> mForServerMessageQueue;

    public static final SharedLocalMessageQueue getInstance() {
        return INSTANCE;
    }

    public static final void recreateInstance() {
        INSTANCE = new SharedLocalMessageQueue();
    }

    private SharedLocalMessageQueue() {
        mForClientMessageQueue = new LinkedBlockingQueue<>();
        mForServerMessageQueue = new LinkedBlockingQueue<>();
    }

    public String getMessageForClientQueue() {
        return getMessageFromQueue(mForClientMessageQueue);
    }

    public String getMessageForServerQueue() {
        return getMessageFromQueue(mForServerMessageQueue);
    }

    public void insertMessageForClientQueue(final String msg) {
        mForClientMessageQueue.add(msg);
    }

    public void insertMessageForServerQueue(final String msg) {
        mForServerMessageQueue.add(msg);
    }

    private String getMessageFromQueue(final Queue<String> queue) {
        while (queue.isEmpty()) {
            try {
                Thread.sleep(300);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        return queue.poll();
    }

    public void clearForClientQueue() {
        mForClientMessageQueue.clear();
    }

    public void clearForServerQueue() {
        mForServerMessageQueue.clear();
    }
}