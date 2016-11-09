import com.jogamp.opengl.GL2;

import java.awt.*;

/**
 * Created by alvar on 11/5/2016.
 */
public class QuadMap
{
	private Square[][] strips;

	private float scaling = 1;

	Color low, hi;
	int steps;

	public QuadMap(gridFloatReader gfl, Color lowColor, Color highColor, int stepNum)
	{
		strips = new Square[gfl.nrows - 1][gfl.ncols - 1];
		low = lowColor;
		hi = highColor;
		steps = stepNum;
		FillStrips(gfl);
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


	public void DrawArea(GL2 gl)
	{

		for(int x = 0; x < strips.length; x++)
		{
			DrawStrip(gl, x, false, low, hi, steps);
		}

	}

	private void DrawStrip(GL2 gl, int strip, boolean wireframe, Color colorLow, Color colorHigh, int contourNum)
	{
		if(wireframe)
			gl.glBegin(GL2.GL_QUADS);
		else
			gl.glBegin(GL2.GL_QUAD_STRIP);


		for(int x = 0; x < strips[strip].length; x++)
		{

			//x,y,z, y is height.
			strips[strip][x].QuadInstructions(gl, colorLow, colorHigh);
		}
		gl.glEnd();

		for(int x = -contourNum; x < contourNum; x++)
		{

			//x,y,z, y is height.
			strips[strip][x].DrawContour((float)((float)x - (float)contourNum / 2.0) / (float)contourNum , gl, Color.black);
		}
	}
}
