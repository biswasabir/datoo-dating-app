package com.angopapo.datooapp.modules.charting.interfaces.dataprovider;

import com.angopapo.datooapp.modules.charting.components.YAxis.AxisDependency;
import com.angopapo.datooapp.modules.charting.data.BarLineScatterCandleBubbleData;
import com.angopapo.datooapp.modules.charting.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
