import com.jogamp.opengl.GL2;

import java.awt.*;

/**
 * Created by alvar on 11/5/2016.
 */
public class QuadMap
{
	private Square[][] strips;

	private float scaling = 1;

	public QuadMap(gridFloatReader gfl, Color lowColor, Color highColor)
	{
		strips = new Square[gfl.nrows - 1][gfl.ncols - 1];
		FillStrips(gfl);
	}

	public QuadMap(gridFloatReader gfl, Color lowColor, Color highColor, float scale)
	{
		this(gfl, lowColor, highColor);
		scaling = scale;
		FillStrips(gfl);

	}

	public void DrawContours(int num)
	{

	}

	private void DrawContour(double level)
	{

	}

	private void FillStrips(gridFloatReader gfl)
	{
		for(int x = 0; x < gfl.nrows - 1; x++)
		{
			for(int y = 0; y < gfl.ncols - 1; y++)
			{

				strips[x][y] = new Square(x, y, scaling, scaling, gfl);
			}
		}
	}

	public void SetScale(float scale, gridFloatReader gfl)
	{
		scaling = scale;
		FillStrips(gfl);

	}

	public void DrawArea(GL2 gl)
	{

		for(int x = 0; x < strips.length; x++)
		{
			DrawStrip(gl, x, false);
		}

	}

	private void DrawStrip(GL2 gl, int strip, boolean wireframe)
	{
		if(wireframe)
			gl.glBegin(GL2.GL_QUADS);
		else
			gl.glBegin(GL2.GL_QUAD_STRIP);


		for(int x = 0; x < strips[strip].length; x++)
		{

			//x,y,z, y is height.
			strips[strip][x].QuadInstructions(gl, Color.RED, Color.GREEN);
		}
		gl.glEnd();

		for(int x = 0; x < strips[strip].length; x++)
		{

			//x,y,z, y is height.
			strips[strip][x].DrawContour(0.0, gl, Color.black);
		}




		/*gl.glBegin(GL2.GL_QUADS);
		{
			// Front Face
			gl.glVertex3f(-1.0f, -1.0f, 1.0f);
			gl.glVertex3f(1.0f, -1.0f, 1.0f);
			gl.glVertex3f(1.0f, 1.0f, 1.0f);
			gl.glVertex3f(-1.0f, 1.0f, 1.0f);
			// Back Face
			gl.glVertex3f(-1.0f, -1.0f, -1.0f);
			gl.glVertex3f(-1.0f, 1.0f, -1.0f);
			gl.glVertex3f(1.0f, 1.0f, -1.0f);
			gl.glVertex3f(1.0f, -1.0f, -1.0f);

			gl.glVertex3f(-1.0f, 1.0f, -1.0f);
			gl.glVertex3f(-1.0f, 1.0f, 1.0f);
			gl.glVertex3f(1.0f, 1.0f, 1.0f);
			gl.glVertex3f(1.0f, 1.0f, -1.0f);

			gl.glVertex3f(-1.0f, -1.0f, -1.0f);
			gl.glVertex3f(1.0f, -1.0f, -1.0f);
			gl.glVertex3f(1.0f, -1.0f, 1.0f);
			gl.glVertex3f(-1.0f, -1.0f, 1.0f);

			gl.glVertex3f(1.0f, -1.0f, -1.0f);
			gl.glVertex3f(1.0f, 1.0f, -1.0f);
			gl.glVertex3f(1.0f, 1.0f, 1.0f);
			gl.glVertex3f(1.0f, -1.0f, 1.0f);

			gl.glVertex3f(-1.0f, -1.0f, -1.0f);
			gl.glVertex3f(-1.0f, -1.0f, 1.0f);
			gl.glVertex3f(-1.0f, 1.0f, 1.0f);
			gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		}
		gl.glEnd();*/
	}
}
