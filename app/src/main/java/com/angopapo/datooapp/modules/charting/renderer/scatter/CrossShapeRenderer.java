package com.angopapo.datooapp.modules.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.angopapo.datooapp.modules.charting.interfaces.datasets.IScatterDataSet;
import com.angopapo.datooapp.modules.charting.utils.Utils;
import com.angopapo.datooapp.modules.charting.utils.ViewPortHandler;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:08
 */
public class CrossShapeRenderer implements IShapeRenderer
{


    @Override
    public void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler,
                            float posX, float posY, Paint renderPaint) {

        final float shapeHalf = dataSet.getScatterShapeSize() / 2f;

        renderPaint.setStyle(Paint.Style.STROKE);
        renderPaint.setStrokeWidth(Utils.convertDpToPixel(1f));

        c.drawLine(
                posX - shapeHalf,
                posY,
                posX + shapeHalf,
                posY,
                renderPaint);
        c.drawLine(
                posX,
                posY - shapeHalf,
                posX,
                posY + shapeHalf,
                renderPaint);

    }
}
