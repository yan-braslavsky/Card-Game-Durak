package com.yan.durak.managers;

import com.yan.durak.layouting.pile.IPileLayouter;
import com.yan.durak.models.IPile;

/**
 * Created by ybra on 20/04/15.
 */
public interface IPileLayouterManager {
    IPileLayouter getPileLayouterForPile(IPile pile);
}
