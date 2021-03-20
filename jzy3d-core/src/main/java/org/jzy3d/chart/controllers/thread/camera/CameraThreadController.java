package org.jzy3d.chart.controllers.thread.camera;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot3d.rendering.view.Camera;

/**
 * The {@link CameraThreadController} provides a Thread for controlling the {@link Camera} and make
 * it turn around the view point along its the azimuth dimension.
 * 
 * @author Martin Pernollet
 */
public class CameraThreadController extends AbstractCameraController implements Runnable {

  protected Coord2d move;
  protected Thread process = null;
  protected int sleep = 1;// 1000/25; // nb milisecond wait between two frames
  protected float step = 0.0005f;
  protected static int id = 0;


  public CameraThreadController() {}

  public CameraThreadController(Chart chart) {
    register(chart);
  }

  @Override
  public void dispose() {
    stop();
    super.dispose();
  }

  /** Start the camera rotation . */
  public void start() {
    if (process == null) {
      process = new Thread(this);
      process.setName("CameraThreadController (automatic rotation)" + (id++));
      process.start();
    }
  }

  /** Stop the rotation. */
  public void stop() {
    if (process != null) {
      process.interrupt();
      process = null;
    }
  }

  /** Run the animation. */
  @Override
  public void run() {
    doRun();
  }

  protected void doRun() {
    move = new Coord2d(step, 0);

    while (process != null) {
      try {
        rotate(move);
        Thread.sleep(sleep);
      } catch (InterruptedException e) {
        process = null;
      }
    }
  }

  public float getStep() {
    return step;
  }

  public void setStep(float step) {
    this.step = step;
  }

}
