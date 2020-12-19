package org.jzy3d.emulgl.unit;

import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.EmulGLView;
import org.mockito.Mockito;

public class SpyEmulGLChartFactory extends EmulGLChartFactory{
	public SpyEmulGLChartFactory() {
		super();
	}
	
	@Override
	public EmulGLView newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
		//return Mockito.spy((ChartView) super.newView(factory, scene, canvas, quality));
		EmulGLView view = Mockito.spy((EmulGLView) super.newView(factory, scene, canvas, quality));
		view.initInstance(factory, scene, canvas, quality);
		return view;

	}
}