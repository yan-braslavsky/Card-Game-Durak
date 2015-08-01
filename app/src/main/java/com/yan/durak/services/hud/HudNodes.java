package com.yan.durak.services.hud;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yan.braslavsky on 6/12/2015.
 */
public class HudNodes {

    /* Speech Bubble texts */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            SPEECH_BUBBLE_TAKING_TEXT,
            SPEECH_BUBBLE_PASS_TEXT,
            SPEECH_BUBBLE_DONE_TEXT,
            SPEECH_BUBBLE_THROW_IN_END_TEXT,
            SPEECH_BUBBLE_ATTACK_TEXT,
            SPEECH_BUBBLE_THINKING_TEXT,
            SPEECH_BUBBLE_RETALIATION_TEXT
    })
    public @interface SpeechBubbleText {
    }

    public static final String SPEECH_BUBBLE_TAKING_TEXT = "I'll Take!";
    public static final String SPEECH_BUBBLE_PASS_TEXT = "Pass";
    public static final String SPEECH_BUBBLE_DONE_TEXT = "Done!";
    public static final String SPEECH_BUBBLE_THROW_IN_END_TEXT = "Adding";
    public static final String SPEECH_BUBBLE_THINKING_TEXT = "Thinking";
    public static final String SPEECH_BUBBLE_ATTACK_TEXT = "My Turn!";
    public static final String SPEECH_BUBBLE_RETALIATION_TEXT = "Defending";


    /* Hud Nodes */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            AVATAR_BG_BOTTOM_RIGHT_INDEX,
            AVATAR_BG_TOP_RIGHT_INDEX,
            AVATAR_BG_TOP_LEFT_INDEX,
            AVATAR_ICON_BOTTOM_RIGHT_INDEX,
            AVATAR_ICON_TOP_RIGHT_INDEX,
            AVATAR_ICON_TOP_LEFT_INDEX,
            DONE_BUTTON_INDEX,
            TAKE_BUTTON_INDEX,
            TRUMP_IMAGE_INDEX,
            YOU_WIN_IMAGE_INDEX,
            YOU_LOOSE_IMAGE_INDEX,
            V_BUTTON_INDEX,
            /**
             * We don't want to show all the cards in a stock pile.
             * Instead we are showing only one, which is this node.
             * Underneath this node there is a trump card.
             */
            MASK_CARD_INDEX,
            FENCE_INDEX,
            GLADE_INDEX,
            GLOW_INDEX,
            CIRCLE_TIMER_BOTTOM_RIGHT_INDEX,
            CIRCLE_TIMER_TOP_RIGHT_INDEX,
            CIRCLE_TIMER_TOP_LEFT_INDEX,
            ROOF_INDEX,
            BOTTOM_SPEECH_BUBBLE_INDEX,
            TOP_RIGHT_SPEECH_BUBBLE_INDEX,
            TOP_LEFT_SPEECH_BUBBLE_INDEX,
            BOTTOM_SPEECH_BUBBLE_TEXT_INDEX,
            TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX,
            TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX,
            BG_GRADIENT_INDEX
    })


    public @interface HudNode {
    }
    //Avatar backgrounds
    public static final int AVATAR_BG_BOTTOM_RIGHT_INDEX = 0;
    public static final int AVATAR_BG_TOP_RIGHT_INDEX = 1;
    public static final int AVATAR_BG_TOP_LEFT_INDEX = 2;

    //Avatar icons
    public static final int AVATAR_ICON_BOTTOM_RIGHT_INDEX = 3;
    public static final int AVATAR_ICON_TOP_RIGHT_INDEX = 4;
    public static final int AVATAR_ICON_TOP_LEFT_INDEX = 5;
    public static final int TRUMP_IMAGE_INDEX = 6;

    //Action buttons
    public static final int DONE_BUTTON_INDEX = 7;
    public static final int TAKE_BUTTON_INDEX = 8;

    public static final int GLOW_INDEX = 9;

    //End game dialogs
    public static final int YOU_WIN_IMAGE_INDEX = 10;
    public static final int YOU_LOOSE_IMAGE_INDEX = 11;
    public static final int V_BUTTON_INDEX = 12;


    public static final int MASK_CARD_INDEX = 13;
    public static final int FENCE_INDEX = 14;
    public static final int GLADE_INDEX = 15;
    public static final int CIRCLE_TIMER_BOTTOM_RIGHT_INDEX = 16;
    public static final int CIRCLE_TIMER_TOP_RIGHT_INDEX = 17;
    public static final int CIRCLE_TIMER_TOP_LEFT_INDEX = 18;
    public static final int ROOF_INDEX = 19;
    public static final int BOTTOM_SPEECH_BUBBLE_INDEX = 20;
    public static final int TOP_RIGHT_SPEECH_BUBBLE_INDEX = 21;
    public static final int TOP_LEFT_SPEECH_BUBBLE_INDEX = 22;
    public static final int BOTTOM_SPEECH_BUBBLE_TEXT_INDEX = 23;
    public static final int TOP_RIGHT_SPEECH_BUBBLE_TEXT_INDEX = 24;
    public static final int TOP_LEFT_SPEECH_BUBBLE_TEXT_INDEX = 25;
    public static final int BG_GRADIENT_INDEX = 26;
}
