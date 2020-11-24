package org.jzy3d.chart.factories;

import java.util.Date;

import org.apache.log4j.Logger;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.AWTScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.AWTMousePickingController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.primitives.axes.AxisBox;
import org.jzy3d.plot3d.primitives.axes.IAxis;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;

public class AWTChartFactory extends NativeChartFactory {
    static Logger logger = Logger.getLogger(AWTChartFactory.class);
    
    public static Chart chart() {
        return chart(Quality.Intermediate);
    }

    public static Chart chart(Quality quality) {
        AWTChartFactory f = new AWTChartFactory();
        return f.newChart(quality);
    }

    /* */

    /**
     */
    @Override
    public Chart newChart(IChartFactory factory, Quality quality) {
        return new AWTChart(factory, quality);
    }

    @Override
    public IAxis newAxe(BoundingBox3d box, View view) {
        AxisBox axe = new AxisBox(box);
        axe.setView(view);
        return axe;
    }

    @Override
    public IViewportLayout newViewportLayout() {
        return new ViewAndColorbarsLayout();
    }

    /**
     * The AWTView support Java2d defined components (tooltips, background
     * images)
     */
    @Override
    public View newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        return new AWTView(factory, scene, canvas, quality);
    }

    /** Provide AWT Texture loading for screenshots */
    @Override
    public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL) {
        return new AWTRenderer3d(view, traceGL, debugGL);
    }

    @Override
    public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
        boolean traceGL = false;
        boolean debugGL = false;
        
        if(isOffscreen()) {
            return newOffscreenCanvas((NativeChartFactory)factory, scene, quality, traceGL, debugGL);
        }
        else
        	return new CanvasAWT((NativeChartFactory) factory, scene, quality, getCapabilities(), traceGL, debugGL);
    }


    @Override
    public IChartFactory getFactory() {
        return this;
    }

    @Override
    public ICameraMouseController newMouseCameraController(Chart chart) {
        return new AWTCameraMouseController(chart);
    }
    
    @Override
    public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
        return new AWTMousePickingController(chart, clickWidth);
    }

    /**
     * Output file of screenshot can be configured using {@link IScreenshotKeyController#setFilename(String)}.
     */
    @Override
    public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
        // trigger screenshot on 's' letter
        String file = SCREENSHOT_FOLDER + "capture-" + Utils.dat2str(new Date(), "yyyy-MM-dd-HH-mm-ss") + ".png";
        IScreenshotKeyController screenshot;

        //if (!chart.getWindowingToolkit().equals("newt"))
            screenshot = new AWTScreenshotKeyController(chart, file);
        //else
        //    screenshot = new NewtScreenshotKeyController(chart, file);
        
        
        screenshot.addListener(new IScreenshotEventListener() {
            @Override
            public void failedScreenshot(String file, Exception e) {
                logger.error("Failed to save screenshot to '" + file + "'", e);
            }

            @Override
            public void doneScreenshot(String file) {
                logger.info("Screenshot save to '" + file + "'");
            }
        });
        return screenshot;
    }

    @Override
    public ICameraKeyController newKeyboardCameraController(Chart chart) {
        return new AWTCameraKeyController(chart);
    }

    @Override
    public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
        return new FrameAWT(chart, bounds, title, null);
    }
}
