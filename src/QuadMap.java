import com.jogamp.opengl.GL2;
import java.awt.*;

/**
 * Defines a map, can draw it
 * Created by alvarpq on 11/5/2016.
 */
public class QuadMap
{
	Color low, hi;
	int steps;
	boolean wire;
	private Square[][] strips;
	private float scaling = 1;

	public QuadMap(gridFloatReader gfl, boolean wire, Color lowColor, Color highColor, int stepNum)
	{
		strips = new Square[gfl.nrows - 1][gfl.ncols - 1];
		low = lowColor;
		hi = highColor;
		steps = stepNum;
		this.wire = wire;
		FillStrips(gfl);
	}


	private void FillStrips(gridFloatReader gfl)
	{
		for (int x = 0; x < gfl.nrows - 1; x++)
		{
			for (int y = 0; y < gfl.ncols - 1; y++)
			{
				strips[x][y] = new Square(x, y, gfl);
			}
		}
	}


	public void DrawArea(GL2 gl)
	{
		for (int x = 0; x < strips.length; x++)
		{
			DrawStrip(gl, x, false, low, hi, steps);
		}
	}

	private void DrawStrip(GL2 gl, int strip, boolean wireframe, Color colorLow, Color colorHigh, double contourNum)
	{
		for (double x = 0; x < contourNum; x++)
		{
			//x,y,z, y is height.
			for (int y = 0; y < strips[strip].length; y++)
			{
				strips[strip][y].DrawContour(x / contourNum, gl, Color.black);
			}
		}

		//GlBegin, but strange.
		{
			if(wire)
			{
				gl.glBegin(GL2.GL_QUADS);
			} else
			{
				gl.glBegin(GL2.GL_QUAD_STRIP);
			}


			for(int x = 0; x < strips[strip].length; x++)
			{
				//x,y,z, y is height.
				strips[strip][x].QuadInstructions(gl, colorLow, colorHigh);
			}
		}gl.glEnd();
	}
}