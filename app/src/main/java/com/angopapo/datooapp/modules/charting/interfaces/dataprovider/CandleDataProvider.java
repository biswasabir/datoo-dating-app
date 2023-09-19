package com.angopapo.datooapp.modules.charting.interfaces.dataprovider;

import com.angopapo.datooapp.modules.charting.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
