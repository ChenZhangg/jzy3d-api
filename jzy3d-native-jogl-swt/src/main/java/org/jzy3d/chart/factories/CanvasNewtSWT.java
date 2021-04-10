package org.jzy3d.chart.factories;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jzy3d.chart.IAnimator;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.nativewindow.ScalableSurface;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.newt.swt.NewtCanvasSWT;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * A Newt canvas wrapped in an AWT Newt is supposed to be faster than any other canvas, either for
 * AWT or Swing. If a non AWT panel where required, follow the guidelines given in
 * {@link IScreenCanvas} documentation.
 */
public class CanvasNewtSWT extends Composite implements IScreenCanvas, INativeCanvas {

  public CanvasNewtSWT(IChartFactory factory, Scene scene, Quality quality,
      GLCapabilitiesImmutable glci) {
    this(factory, scene, quality, glci, false, false);
  }

  public CanvasNewtSWT(IChartFactory factory, Scene scene, Quality quality,
      GLCapabilitiesImmutable glci, boolean traceGL, boolean debugGL) {
    super(((SWTChartFactory) factory).getComposite(), SWT.NONE);
    this.setLayout(new FillLayout());
    window = GLWindow.create(glci);
    canvas = new NewtCanvasSWT(this, SWT.NONE, window);
    view = scene.newView(this, quality);
    view.getPainter().setCanvas(this);

    renderer =
        ((NativePainterFactory) factory.getPainterFactory()).newRenderer3D(view, traceGL, debugGL);
    window.addGLEventListener(renderer);

    if (quality.isPreserveViewportSize()) {
      setPixelScale(
          new float[] {ScalableSurface.IDENTITY_PIXELSCALE, ScalableSurface.IDENTITY_PIXELSCALE});
    }

    window.setAutoSwapBufferMode(quality.isAutoSwapBuffer());

    animator = ((SWTChartFactory) factory).newAnimator(window);

    if (quality.isAnimated()) {
      animator.start();
    }

    addDisposeListener(e -> new Thread(() -> {
      if (animator != null) {
        animator.stop();
      }
      if (renderer != null) {
        renderer.dispose(window);
      }
      window = null;
      renderer = null;
      view = null;
      animator = null;
    }).start());
  }

  @Override
  public IAnimator getAnimation() {
    return animator;
  }


  @Override
  public void setPixelScale(float[] scale) {
    if (scale != null) {
      window.setSurfaceScale(scale);
    } else {
      window.setSurfaceScale(new float[] {1f, 1f});
    }
  }
  
  @Override
  public Coord2d getPixelScale() {
    Logger.getLogger(CanvasNewtSWT.class).info("getPixelScale() not implemented. Will return {1,1}"); 
    return new Coord2d(1, 1);
  }


  public GLWindow getWindow() {
    return window;
  }

  public NewtCanvasSWT getCanvas() {
    return canvas;
  }

  @Override
  public GLAutoDrawable getDrawable() {
    return window;
  }

  @Override
  public void display() {
    window.display();
  }

  @Override
  public void forceRepaint() {
    display();
  }

  @Override
  public TextureData screenshot() {
    renderer.nextDisplayUpdateScreenshot();
    display();
    return renderer.getLastScreenshot();
  }

  @Override
  public void screenshot(File file) throws IOException {
    if (!file.getParentFile().exists())
      file.mkdirs();

    TextureData screen = screenshot();
    TextureIO.write(screen, file);
  }

  @Override
  public String getDebugInfo() {
    GL gl = ((NativeDesktopPainter) getView().getPainter()).getCurrentGL(this);

    StringBuilder sb = new StringBuilder();
    sb.append("Chosen GLCapabilities: " + window.getChosenGLCapabilities() + "\n");
    sb.append("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR) + "\n");
    sb.append("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER) + "\n");
    sb.append("GL_VERSION: " + gl.glGetString(GL.GL_VERSION) + "\n");
    return sb.toString();
  }

  /**
   * Provide the actual renderer width for the open gl camera settings, which is obtained after a
   * resize event.
   */
  @Override
  public int getRendererWidth() {
    return (renderer != null ? renderer.getWidth() : 0);
  }

  /**
   * Provide the actual renderer height for the open gl camera settings, which is obtained after a
   * resize event.
   */
  @Override
  public int getRendererHeight() {
    return (renderer != null ? renderer.getHeight() : 0);
  }

  @Override
  public Renderer3d getRenderer() {
    return renderer;
  }

  /** Provide a reference to the View that renders into this canvas. */
  @Override
  public View getView() {
    return view;
  }

  /* */

  public synchronized void addKeyListener(KeyListener l) {
    getWindow().addKeyListener(l);
  }

  public void addMouseListener(MouseListener l) {
    getWindow().addMouseListener(l);
  }

  public void removeMouseListener(com.jogamp.newt.event.MouseListener l) {
    getWindow().removeMouseListener(l);
  }

  public void removeKeyListener(com.jogamp.newt.event.KeyListener l) {
    getWindow().removeKeyListener(l);
  }

  @Override
  public void addMouseController(Object o) {
    addMouseListener((MouseListener) o);
  }

  @Override
  public void addKeyController(Object o) {
    addKeyListener((KeyListener) o);
  }

  @Override
  public void removeMouseController(Object o) {
    removeMouseListener((MouseListener) o);
  }

  @Override
  public void removeKeyController(Object o) {
    removeKeyListener((KeyListener) o);
  }

  protected View view;
  protected Renderer3d renderer;
  protected IAnimator animator;
  protected GLWindow window;
  protected NewtCanvasSWT canvas;
}
