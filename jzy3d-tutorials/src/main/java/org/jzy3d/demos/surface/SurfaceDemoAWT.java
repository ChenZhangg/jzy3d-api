package org.jzy3d.demos.surface;

import java.io.IOException;

import org.jzy3d.analysis.AWTAbstractAnalysis;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class SurfaceDemoAWT extends AWTAbstractAnalysis {
    public static void main(String[] args) throws Exception {
    	SurfaceDemoAWT d = new SurfaceDemoAWT();
    	openAndPrintFrame(d);
        //AnalysisLauncher.open(d);
    }

	private static void openAndPrintFrame(SurfaceDemoAWT d) throws InterruptedException, IOException {
		d.init();
    	Chart chart = d.getChart();
    	chart.addMouseCameraController();
    	FrameAWT f = (FrameAWT)chart.open();
    	//Thread.sleep(1000);
    	String file = "./target/" + d.getClass().getSimpleName() + ".png";
    	Frame.print(chart, f, file);
	}

	

    @Override
    public void init() {
        // Define a function to plot
        Mapper mapper = new Mapper() {
            @Override
            public double f(double x, double y) {
                return x * Math.sin(x * y);
            }
        };

        // Define range and precision for the function to plot
        Range range = new Range(-3, 3);
        int steps = 80;

        // Create the object to represent the function over the given range.
        final Shape surface = new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(true);
        surface.setWireframeColor(Color.BLACK);

        // Create a chart
        chart = AWTChartFactory.chart(Quality.Advanced);
        chart.getScene().getGraph().add(surface);
    }
}
