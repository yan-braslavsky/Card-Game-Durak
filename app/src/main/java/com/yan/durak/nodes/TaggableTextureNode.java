package com.yan.durak.nodes;

import glengine.yan.glengine.assets.atlas.YANAtlasTextureRegion;
import glengine.yan.glengine.nodes.YANTexturedNode;

/**
 * Created by yan.braslavsky on 8/21/2015.
 * <p>
 * Node that is capable of holding Tagged object
 */
public class TaggableTextureNode extends YANTexturedNode {

    private Object mTag;

    public TaggableTextureNode(YANAtlasTextureRegion textureRegion) {
        super(textureRegion);
    }

    public <T> T getTag() {
        return (T) mTag;
    }

    public void setTag(Object mTag) {
        this.mTag = mTag;
    }
}
