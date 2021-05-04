package org.jzy3d.plot2d.primitive;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;


/**
 * @author Martin Pernollet
 */
public class AWTColorbarImageGenerator extends AWTAbstractImageGenerator
    implements AWTImageGenerator {
  
  public static final int MIN_BAR_WIDTH = 110;
  public static final int MIN_BAR_HEIGHT = 100;

  
  protected ColorMapper mapper;
  protected ITickProvider provider;
  protected ITickRenderer renderer;
  protected double min;
  protected double max;
  
  public AWTColorbarImageGenerator(IColorMap map, float min, float max, ITickProvider provider,
      ITickRenderer renderer) {
    this(new ColorMapper(map, min, max), provider, renderer);
  }

  public AWTColorbarImageGenerator(ColorMapper mapper, ITickProvider provider,
      ITickRenderer renderer) {
    this.mapper = mapper;
    this.provider = provider;
    this.renderer = renderer;
    this.min = mapper.getMin();
    this.max = mapper.getMax();
    setFont(IAxisLayout.FONT_DEFAULT);
  }


  //public 
  
  @Override
  public BufferedImage toImage(int width, int height) {
    return toImage(width, height, 20);
  }

  /** Renders the {@link AWTColorbarImageGenerator} to an image. */
  public BufferedImage toImage(int width, int height, int barWidth) {
    if (barWidth > width)
      return null;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphic = image.createGraphics();
    
    AWTGraphicsUtils.configureRenderingHints(graphic);

    graphic.setColor(AWTColor.toAWT(Color.WHITE));
    graphic.fillRect(0, 0, width, height);

    configureText(graphic);
    drawBackground(width, height, graphic);
    drawBarColors(height, barWidth, graphic);
    drawBarContour(height, barWidth, graphic);
    drawTextAnnotations(height, barWidth, graphic);
    return image;
  }

  public void drawBarContour(int height, int barWidth, Graphics2D graphic) {
    graphic.setColor(AWTColor.toAWT(foregroundColor));
    graphic.drawRect(0, textSize / 2, barWidth, height - textSize);
  }

  public void drawBarColors(int height, int barWidth, Graphics2D graphic) {
    for (int h = textSize / 2; h <= (height - textSize / 2); h++) {
      // Compute value & color
      double v = min + (max - min) * (h) / (height - textSize);
      Color c = mapper.getColor(v); // To allow the Color to be a variable independent of the
                                    // coordinates

      // Draw line
      graphic.setColor(AWTColor.toAWT(c));
      graphic.drawLine(0, height - h, barWidth, height - h);
    }
  }

  public void drawTextAnnotations(int height, int barWidth, Graphics2D graphic) {
    if (provider != null) {
      double[] ticks = provider.generateTicks(min, max);
      int ypos;
      String txt;
      for (int t = 0; t < ticks.length; t++) {
        ypos = (int) (textSize
            + (height - textSize - (height - textSize) * ((ticks[t] - min) / (max - min)))); 
        txt = renderer.format(ticks[t]);
        graphic.drawString(txt, barWidth + 1, ypos);
      }
    }
  }

  /* */

}
