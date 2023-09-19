package com.angopapo.datooapp.modules.charting.interfaces.dataprovider;

import com.angopapo.datooapp.modules.charting.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
