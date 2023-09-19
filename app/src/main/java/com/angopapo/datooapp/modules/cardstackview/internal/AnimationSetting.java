package com.angopapo.datooapp.modules.cardstackview.internal;

import android.view.animation.Interpolator;

import com.angopapo.datooapp.modules.cardstackview.Direction;


public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
