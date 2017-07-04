package nl.defsoftware.mrgb.view.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

/**
 * @author D.L. Ettema
 * @date: 27 September 2016
 *
 */
public class DrawableEdgeCurve extends CubicCurve {

    private static final float CURVE_SIZE = 20.0f;
    private static final float NEG_CURVE_SIZE = -20.0f;
    private static final float BASE = 0.0f;
    private static final float X_CORRECTION = 1.5f;

    public DrawableEdgeCurve(int rank, int startX, int startY, int endX, int endY, boolean openingCurve) {
        relocate(startX + X_CORRECTION, startY);
        setBasicShapeProperties(rank, startX, startY, endX, endY,openingCurve);
    }

    private void setBasicShapeProperties(int rank, int startX, int startY, int endX, int endY, boolean openingCurve) {
        rank--;
        
        float LENGTH_CURVE_SIZE = CURVE_SIZE + CURVE_SIZE * rank;
        
        if (openingCurve) {
            LENGTH_CURVE_SIZE = endX - startX;
            setStartX(CURVE_SIZE);
            setStartY(BASE);

            setEndX(BASE);
            setEndY(LENGTH_CURVE_SIZE);
            
            setControlX1(BASE);
            setControlY1(BASE);

            setControlX2(CURVE_SIZE);
            setControlY2(LENGTH_CURVE_SIZE);
            
            setTranslateX(0.0 + rank * 10.0);
            setTranslateY(0.0 + rank * -10.0);
            setRotate(90.0f);
            
        } else {
            LENGTH_CURVE_SIZE = startX - endX;
            setStartX(BASE);
            setStartY(BASE);

            setEndX(CURVE_SIZE);
            setEndY(LENGTH_CURVE_SIZE);

            setControlX1(CURVE_SIZE);
            setControlY1(BASE);

            setControlX2(BASE);
            setControlY2(LENGTH_CURVE_SIZE);
            setTranslateY(-10.0f * rank);
            if (rank > 0) {
                setTranslateX(-20.0f - (10.0f * rank));
            } else {
                setTranslateX(NEG_CURVE_SIZE + (NEG_CURVE_SIZE*rank));
            }
            setRotate(90.0f);
        }
        setStrokeLineCap(StrokeLineCap.BUTT);
        setStrokeWidth(3);
        setStroke(Color.valueOf("BLACK"));
        setStrokeType(StrokeType.CENTERED);
        setFill(Color.valueOf("ffffff00"));
        setOpacity(0.5);
    }
}
