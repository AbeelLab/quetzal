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
public class RibbonCurve extends CubicCurve {

    private static final float CURVE_SIZE = 20.0f;
    private static final float BASE = 0.0f;
    private Integer id;

    public RibbonCurve(Integer id, int rank, int startX, int startY, boolean openingCurve) {
        this.id = id;
        setBasicShapeProperties(rank, startX, startY, openingCurve);
    }

    private void setBasicShapeProperties(int rank, int startX, int startY, boolean openingCurve) {
        rank--;
        if (openingCurve) {
            setStartX(CURVE_SIZE);
            setStartY(BASE);

            setEndX(BASE);
            setEndY(CURVE_SIZE);
            
            setControlX1(BASE);
            setControlY1(BASE);

            setControlX2(CURVE_SIZE);
            setControlY2(CURVE_SIZE);

            setRotate(90.0f);
            relocate(startX+4, startY+5);
        } else {
            setStartX(0.0f);
            setStartY(0.0f);

            setEndX(CURVE_SIZE);
            setEndY(CURVE_SIZE);

            setControlX1(CURVE_SIZE);
            setControlY1(0.0f);

            setControlX2(0.0f);
            setControlY2(CURVE_SIZE);

            setRotate(90.0f);
            relocate(startX+4, startY+5);
            setTranslateX(-CURVE_SIZE);
        }
        setStrokeLineCap(StrokeLineCap.BUTT);
        setStrokeWidth(3.0);
        setStroke(Color.valueOf("BLACK"));
        setStrokeType(StrokeType.CENTERED);
        setFill(Color.valueOf("ffffff00"));
        setOpacity(0.5);
    }

    public Integer getRibbonId() {
        return id;
    }
}
