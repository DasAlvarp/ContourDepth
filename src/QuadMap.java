import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import java.awt.*;

/**
 * Created by alvar on 11/5/2016.
 */
public class QuadMap
{
	Square[][] strips;

	public QuadMap(gridFloatReader gfl, Color lowColor, Color highColor)
	{
		strips = new Square[gfl.nrows][gfl.ncols];
	}

	public void DrawArea(GL2 gl)
	{
		for(int x = 0; x < strips.length; x++)
		{
			DrawStrip(gl, x);
		}
	}
	private void DrawStrip(GL2 gl, int strip)
	{
		gl.glBegin(GL2.GL_QUADS);
		{
			for (int x = 0; x < strips[0].length; x++)
			{

			}
		}
	}
}
