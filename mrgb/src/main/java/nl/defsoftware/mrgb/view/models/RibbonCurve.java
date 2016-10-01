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
    private Integer id;

    public RibbonCurve(Integer id, int rank, boolean openingCurve) {
        this.id = id;
        setBasicShapeProperties(rank, openingCurve);
    }

    private void setBasicShapeProperties(int rank, boolean openingCurve) {
        rank--;
        if (openingCurve) {
            setStartX(0.0f);
            setStartY(CURVE_SIZE);

            setControlX1(CURVE_SIZE);
            setControlY1(CURVE_SIZE);

            setControlX2(0.0f);
            setControlY2(Math.ceil(rank * -20.0));

            setEndX(CURVE_SIZE);
            setEndY(Math.ceil(rank * -20.0));
            setRotate(90.0f);
        } else {
            setStartX(0.0f);
            setStartY(0.0f);

            setControlX1(CURVE_SIZE);
            setControlY1(0.0f);

            setControlX2(0.0f);
            setControlY2(CURVE_SIZE);

            setEndX(CURVE_SIZE);
            setEndY(CURVE_SIZE);
            setRotate(90.0f);
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
