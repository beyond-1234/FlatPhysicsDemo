package com.mygdx.game.callback;

import com.mygdx.game.FlatBody;

public interface CollisionCallback extends Callback{
    void collide(int indexA, int indexB);
//    public void collide(FlatBody bodyA, FlatBody bodyB);
}
