package org.jzy3d.chart.controllers;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Graph;
import jgl.GL;

public class AdaptiveMouseController extends AWTCameraMouseController{
  protected Quality currentQuality;
  protected boolean currentHiDPI;
  protected EmulGLCanvas canvas;
  protected GL gl;
  protected EmulGLPainter painter;
  protected Policy policy;
  
  public static class Policy{
    public boolean optimizeWithWireframe = true;
    public boolean optimizeWithFace = false;
    public boolean optimizeWithHiDPI = false;
    public RateLimiter rateLimiter = null;
  }
  
  
  public AdaptiveMouseController() {
    super();
  }
  public AdaptiveMouseController(Chart chart, Policy policy) {
    super(chart, policy.rateLimiter);
    this.policy = policy;
  }
  public AdaptiveMouseController(Chart chart) {
    super(chart);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    loadChartItems(getChart());
    
    if(policy.optimizeWithWireframe)
      disableWireframe(getChart());
    if(policy.optimizeWithFace)
      disableFaces(getChart());
    if(policy.optimizeWithHiDPI)
      reduceQuality(getChart());
    
    super.mousePressed(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e) {
    super.mouseReleased(e);
    
    if(policy.optimizeWithWireframe)
      reloadWireframe();
    if(policy.optimizeWithFace)
      reloadFace();    
    if(policy.optimizeWithHiDPI)
      reloadQuality(getChart()); // trigger a last rendering
    
    // for display of this last image with the new HiDPI setting
    canvas.forceRepaint();

  }

  protected void loadChartItems(Chart chart) {
    painter = (EmulGLPainter)chart.getPainter();
    currentQuality = chart.getQuality();
    currentHiDPI = painter.getGL().isAutoAdaptToHiDPI();
    canvas = (EmulGLCanvas)chart.getCanvas();
    gl = ((EmulGLPainter)chart.getPainter()).getGL();
  }
  
  // TODO : return false if quality was not reduced before
  protected void reduceQuality(Chart chart) {
    Quality alternativeQuality = currentQuality.clone();
    alternativeQuality.setPreserveViewportSize(true);
    chart.setQuality(alternativeQuality);
    gl.setAutoAdaptToHiDPI(false);
  }
  
  protected void reloadQuality(Chart chart) {
    chart.setQuality(currentQuality);
    gl.setAutoAdaptToHiDPI(currentHiDPI);
    //System.out.println("Release : hidpi : " + gl.isAutoAdaptToHiDPI());
    
    // this force the GL image to apply the new HiDPI setting
    gl.updatePixelScale(canvas.getGraphics());
    gl.resetViewport();
  }
  
  protected void disableWireframe(Chart chart) {
    Graph graph = chart.getScene().getGraph();
    for(Drawable d: graph.getAll()){
      if(d instanceof Wireframeable) {
        Wireframeable w = (Wireframeable)d;
        if(w.getWireframeDisplayed()) {
          wire.add(w);
          w.setWireframeDisplayed(false);                  
        }
      }
    }
  }

  protected void disableFaces(Chart chart) {
    Graph graph = chart.getScene().getGraph();
    for(Drawable d: graph.getAll()){
      if(d instanceof Wireframeable) {
        Wireframeable w = (Wireframeable)d;
        if(w.getFaceDisplayed()) {
          face.add(w);
          w.setFaceDisplayed(false);                  
        }
      }
    }
  }

  protected List<Wireframeable> wire = new ArrayList<>();
  protected List<Wireframeable> face = new ArrayList<>();
  
  protected  void reloadWireframe() {
    for(Wireframeable w: wire) {
      w.setWireframeDisplayed(true);
    }
  }
  protected  void reloadFace() {
    for(Wireframeable w: face) {
      w.setFaceDisplayed(true);
    }
  }

}