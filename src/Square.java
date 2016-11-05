import com.jogamp.opengl.*;

import java.awt.*;

/**
 * Created by alvar on 10/18/2016.
 */
public class Square
{
	public  float x;
	public  float y;

	public  float xScale = 1;
	public  float yScale = 1;

	public  float[][] heights = new float[2][2];

	private Coordinate[][] corners = new Coordinate[2][2];

	public  Square(float x, float y, float xScale, float yScale, float[][] heights)
	{
		this.x = x;
		this.y = y;
		this.xScale = xScale;
		this.yScale = yScale;
		this.heights = heights;

		corners[0][0] = new Coordinate(x * xScale, y * yScale);
		corners[0][1] = new Coordinate(x * xScale, (y + 1) * yScale);
		corners[1][0] = new Coordinate((x + 1) * xScale, y * yScale);
		corners[1][1] = new Coordinate((x + 1) * xScale, (y + 1) * yScale);
	}

	public void DrawContour(float height, GL2 gl, Color color)
	{
		int numTrue = 0;

		boolean[][] overUnder = new boolean[2][2];

		for(int x = 0; x < 2; x++)
		{
			for(int y = 0; y < 2; y++)
			{
				overUnder[x][y] = heights[x][y] > height;
			}
		}

		//left = 0
		//top = 1
		//right = 2
		//bottom = 3

		boolean[] sideTrue = new boolean[4];
		for(int x = 0; x < 4; x++)
		{
			sideTrue[x] = false;
		}
		if(overUnder[0][0] != overUnder[0][1])
		{
			numTrue++;
			sideTrue[0] = true;
		}
		if(overUnder[0][1] != overUnder[1][1])
		{
			numTrue++;
			sideTrue[1] = true;
		}
		if(overUnder[1][1] != overUnder[1][0])
		{
			numTrue++;
			sideTrue[2] = true;
		}
		if(overUnder[1][0] != overUnder[0][0])
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

		if(numTrue == 2)
		{
			gl.glColor3d((float)color.getRed() / 255f, (float)color.getGreen() / 255f, (float)color.getBlue() / 255f);
			gl.glBegin(GL2.GL_LINES);
			{
				for (int x = 0; x < 4; x++)
				{
					if (sideTrue[x])
					{
						gl.glVertex2f(sideCoords[x].x, sideCoords[x].y);
					}
				}
			}
			gl.glEnd();
		}
		else if(numTrue == 4)//just always do one case. It should work? Nobody knows.
		{
			gl.glBegin(GL2.GL_LINES);
			{
				gl.glVertex2f(sideCoords[1].x, sideCoords[1].y);
				gl.glVertex2f(sideCoords[0].x, sideCoords[0].y);
			}
			gl.glEnd();
			gl.glBegin(GL2.GL_LINES);
			{
				gl.glVertex2f(sideCoords[2].x, sideCoords[2].y);
				gl.glVertex2f(sideCoords[3].x, sideCoords[3].y);
			}
			gl.glEnd();
		}

	}
}
