package org.jzy3d.chart.swt;

import org.eclipse.swt.widgets.Composite;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class SWTChart extends Chart {

    Composite swtcanvas;

    public SWTChart(Composite canvas, IChartFactory factory, Quality quality) {
        super(factory, quality);
        this.swtcanvas = canvas;
    }
}
