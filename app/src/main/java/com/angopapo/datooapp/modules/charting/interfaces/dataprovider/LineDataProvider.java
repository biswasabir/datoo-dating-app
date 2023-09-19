package com.angopapo.datooapp.modules.charting.interfaces.dataprovider;

import com.angopapo.datooapp.modules.charting.components.YAxis;
import com.angopapo.datooapp.modules.charting.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
