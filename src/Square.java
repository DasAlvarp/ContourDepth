import com.jogamp.opengl.GL2;

import java.awt.*;

/**
 * Created by alvar on 10/18/2016.
 */
public class Square
{
	private final double[][] heights = new double[2][2];
	public int x;
	public int y;
	public double xScale = 1;
	public double yScale = 1;

	private Coordinate[][] corners = new Coordinate[2][2];

	public Square(int x, int y, float xScale, float yScale, gridFloatReader gfl)
	{
		//location in coords
		this.x = x;
		this.y = y;

		//scaling factors to make it square and boxy
		this.xScale = 2.0 / (double) gfl.height.length;
		this.yScale = 2.0 / (double) gfl.height[0].length;

		double hDif = gfl.maxHeight - gfl.minHeight;


		corners[0][0] = new Coordinate(((double) x) * this.xScale - 1.0, ((double) y) * this.yScale - 1.0);
		corners[0][1] = new Coordinate((double) x * this.xScale - 1.0, (y + 1.0) * this.yScale - 1.0);
		corners[1][0] = new Coordinate((x + 1.0) * this.xScale - 1.0, ((double) y) * this.yScale - 1.0);
		corners[1][1] = new Coordinate((x + 1.0) * this.xScale - 1.0, (y + 1.0) * this.yScale - 1.0);

		this.heights[0][0] = ((gfl.height[x][y] - gfl.minHeight) / hDif);
		this.heights[0][1] = ((gfl.height[x][y + 1] - gfl.minHeight) / hDif);
		this.heights[1][1] = ((gfl.height[x + 1][y + 1] - gfl.minHeight) / hDif);
		this.heights[1][0] = ((gfl.height[x + 1][y] - gfl.minHeight) / hDif);
	}

	public void DrawContour(double height, GL2 gl, Color color)
	{
		int numTrue = 0;

		boolean[][] overUnder = new boolean[2][2];

		for (int x = 0; x < 2; x++)
		{
			for (int y = 0; y < 2; y++)
			{
				overUnder[x][y] = (this.heights[x][y] > height);
			}
		}


		//left = 0
		//top = 1
		//right = 2
		//bottom = 3

		boolean[] sideTrue = new boolean[4];
		for (int x = 0; x < 4; x++)
		{
			sideTrue[x] = false;
		}

		if (overUnder[0][0] != overUnder[0][1])
		{
			numTrue++;
			sideTrue[0] = true;
		}
		if (overUnder[0][1] != overUnder[1][1])
		{
			numTrue++;
			sideTrue[1] = true;
		}
		if (overUnder[1][1] != overUnder[1][0])
		{
			numTrue++;
			sideTrue[2] = true;
		}
		if (overUnder[1][0] != overUnder[0][0])
		{
			numTrue++;
			sideTrue[3] = true;
		}

		Coordinate[] sideCoords = new Coordinate[4];
		sideCoords[0] = corners[0][0].GetBetween(corners[0][1]);
		sideCoords[1] = corners[0][1].GetBetween(corners[1][1]);
		sideCoords[2] = corners[1][1].GetBetween(corners[1][0]);
		sideCoords[3] = corners[1][0].GetBetween(corners[0][0]);

		//if 0 do nothing. SHould never be an odd number. that would be a problem and would make no sense.

		if (numTrue == 2)
		{
			gl.glColor3d((float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f);
			gl.glBegin(GL2.GL_LINES);
			{
				for (int x = 0; x < 4; x++)
				{
					if (sideTrue[x])
					{
						gl.glVertex3d(sideCoords[x].x, sideCoords[x].y, height);
					}
				}

			}
			gl.glEnd();
		}
		else if (numTrue == 4)//just always do one case. It should work? Nobody knows.
		{
			gl.glBegin(GL2.GL_LINES);
			{
				gl.glVertex3d(sideCoords[1].x, sideCoords[1].y, height);
				gl.glVertex3d(sideCoords[0].x, sideCoords[0].y, height);
			}
			gl.glEnd();
			gl.glBegin(GL2.GL_LINES);
			{
				gl.glVertex3d(sideCoords[2].x, sideCoords[2].y, height);
				gl.glVertex3d(sideCoords[3].x, sideCoords[3].y, height);
			}
			gl.glEnd();
		}
		//	System.out.println("in2" + heights[0][0]);W

	}

	//Draw a quad,
	public void QuadInstructions(GL2 gl, Color cmin, Color cmax)
	{

		double[] colorDif = Utilities.Subtract(Utilities.ColorToDouble(cmax), Utilities.ColorToDouble(cmin));
		double[] color = Utilities.ColorToDouble(cmin);
		color = Utilities.Add(color, Utilities.Multiply((this.heights[1][1] + 1.0) / 2.0, colorDif));

		gl.glColor3d(color[0], color[1], color[2]);
		gl.glVertex3d(corners[1][1].x, corners[1][1].y, this.heights[1][1]);
		gl.glVertex3d(corners[0][1].x, corners[0][1].y, this.heights[0][1]);
		gl.glVertex3d(corners[1][0].x, corners[1][0].y, this.heights[1][0]);
		gl.glVertex3d(corners[0][0].x, corners[0][0].y, this.heights[0][0]);
	}
}
