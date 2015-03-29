package com.yan.durak.screen_fragments;


import java.util.Collection;

import glengine.yan.glengine.assets.atlas.YANTextureAtlas;
import glengine.yan.glengine.nodes.YANTexturedNode;
import glengine.yan.glengine.util.geometry.YANReadOnlyVector2;

/**
 * Created by Yan-Home on 1/29/2015.
 */
public interface IScreenFragment {

    void createNodes(YANTextureAtlas atlas);

    void setNodesSizes(YANReadOnlyVector2 sceneSize);

    void layoutNodes(YANReadOnlyVector2 sceneSize);

    void update(float deltaTimeSeconds);

    Collection<? extends YANTexturedNode> getFragmentNodes();
}
