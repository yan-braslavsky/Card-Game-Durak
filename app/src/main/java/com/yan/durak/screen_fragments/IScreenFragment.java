package com.yan.durak.screen_fragments;

import com.yan.glengine.assets.atlas.YANTextureAtlas;
import com.yan.glengine.nodes.YANTexturedNode;
import com.yan.glengine.util.geometry.YANReadOnlyVector2;

import java.util.Collection;

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
