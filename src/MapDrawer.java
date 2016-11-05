import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by alvarpq on 10/5/2016.
 */
public class MapDrawer extends Frame implements GLEventListener
{
	static
	{
		GLProfile.getDefault();
	}

	private GLCanvas canvas;

	GLProfile glProfile = null;
	GLCapabilities glcapabilities = null;
	GLCanvas glcanvas = null;

	static gridFloatReader gfl ;



	float height = 600;
	float width = 1200;

	static public double low, high;
	static public int stepNum, lowR, lowG, lowB, highR, highG, highB;

	public static QuadMap qm;

	static boolean marker = true;

	public MapDrawer()
	{

		super("Mapper");
		glProfile = GLProfile.getDefault();
		glcapabilities = new GLCapabilities(glProfile);

		glcanvas = new GLCanvas(glcapabilities);
		glcanvas.addGLEventListener(this);

		add(glcanvas);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent windowevent)
			{
				remove(glcanvas);
				dispose();
				System.exit(0);
			}
		});

		setSize((int)width, (int)height);
		setVisible(true);
	}


	public static void main(String[] args)
	{

		gfl = new gridFloatReader(args[0]);

		if(args[1].equals("auto"))
		{
			low = gfl.minHeight;
			high = gfl.maxHeight;
			stepNum = 10;

			lowR = Integer.parseInt(args[2]);
			lowG = Integer.parseInt(args[3]);
			lowB = Integer.parseInt(args[4]);

			highR = Integer.parseInt(args[5]);
			highB = Integer.parseInt(args[6]);
			highG = Integer.parseInt(args[7]);

			if (args[8].equals("marker=false"))
			{
				marker = false;
			}
		}
		else
		{
			low = Double.parseDouble(args[1]);
			high = Double.parseDouble(args[2]);
			stepNum = Integer.parseInt(args[3]);

			lowR = Integer.parseInt(args[4]);
			lowG = Integer.parseInt(args[5]);
			lowB = Integer.parseInt(args[6]);

			highR = Integer.parseInt(args[7]);
			highB = Integer.parseInt(args[8]);
			highG = Integer.parseInt(args[9]);

			if (args[10].equals("marker=false"))
			{
				marker = false;
			}
		}
		qm = new QuadMap(gfl, new Color(lowR, lowG, lowB), new Color(lowR, lowG, lowB));
		new MapDrawer();
	}

	@Override
	public void init(GLAutoDrawable glAutoDrawable)
	{
		System.out.println("init happening");
		GL2 gl = glAutoDrawable.getGL().getGL2();
		gl.glClearColor(.8f, .8f, .8f, 0f);
	}

	@Override
	public void display(GLAutoDrawable glAutoDrawable)
	{
		System.out.println("Entering display");
		//Get context
		GL2 gl = glAutoDrawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		//Set a color (redish - no other components)
		gl.glColor3f(0.0f, 1f, 0.0f);
		//Define a primitive -  A polygon in this case
		DrawArea(gl, stepNum, new Color(lowR, lowG, lowB), new Color(highR, highG, highB));
	}


	//draws all contour lines
	public void DrawArea(GL2 gl, int stepNum, Color colorStart, Color colorEnd)
	{
		float scaling = Utilities.GetMin(width / (float)gfl.height.length, height / (float)gfl.height[0].length);
		double stepSize = (gfl.maxHeight - gfl.minHeight) / stepNum;
		double[] startColor = new double[] {(double)colorStart.getRed()/255.0, (double)colorStart.getGreen()/255.0,(double)colorStart.getBlue()/255.0};
		double[] endColor = new double[] {(double)colorEnd.getRed()/255.0, (double)colorEnd.getGreen()/255.0,(double)colorEnd.getBlue()/255.0};

		double[] colorStep = new double[3];
		for(int x = 0; x < 3; x++)
		{
			colorStep[x] = (endColor[x] - startColor[x])/stepNum;
		}

		for(int s = 0; s < stepNum; s++)
		{
			DrawLevel((float)gfl.minHeight + s * (float)stepSize, gl, Utilities.DoubleToColor(Utilities.Add(startColor, Utilities.Multiply(s, colorStep))), scaling);

		}
	}

	//draws one level of contour lines
	public void DrawLevel(float level, GL2 gl, Color color,float scaling)
	{
		for(int x = 0; x < gfl.height.length - 1; x++)
		{
			for(int y = 0; y < gfl.height[x].length - 1; y++)
			{
				Square sq = new Square(x,y, scaling, scaling, new float[][] {{gfl.height[x][y],gfl.height[x][y +1 ]},{gfl.height[x + 1][y],gfl.height[x + 1][y + 1]}});
				sq.DrawContour(level, gl, color);

			}
		}
		if(marker)
			DrawStar(scaling * (float)gfl.maxHeightxi, scaling * (float)gfl.maxHeightyi, Color.YELLOW, gl);
	}


	@Override
	public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
		System.out.println("Entering reshape(); x="+x+" y="+y+" width="+width+" height="+height);
		//Get the context
		GL2 gl=glautodrawable.getGL().getGL2();
		//Set up projection
		gl.glMatrixMode( GL2.GL_PROJECTION );
		gl.glLoadIdentity();
		//this glOrtho call sets up a 640x480 unit plane with a parallel projection.
		gl.glOrtho(0,640,0,480,0,10);
		//Handle aspect ratio
		double AR= this.width/this.height;

		if (AR*height<width)
				gl.glViewport(x, y, (int) (AR*height), height);
		else
			gl.glViewport(x, y, width, (int) (width/AR));

		gl.glViewport(x, y, (int) (AR*height), height);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}


	@Override
	public void dispose(GLAutoDrawable glAutoDrawable)
	{

	}

	public void DrawStar(float x, float y, Color c, GL2 gl)
	{
		gl.glColor3d(Utilities.ColorToDouble(c)[0], Utilities.ColorToDouble(c)[1], Utilities.ColorToDouble(c)[2]);
		gl.glBegin(GL2.GL_POLYGON);
		{//the most disappointing star you've seen in your life.
			gl.glVertex2f(x, y);
			//-
			gl.glVertex2f(x+10, y);
			// /
			gl.glVertex2f(x+10, y+10);
			//\
			gl.glVertex2f(x+20, y);
			//-
			gl.glVertex2f(x+30, y);
			///
			gl.glVertex2f(x+20, y - 5f);
			//\
			gl.glVertex2f(x + 30, y -10 );
			//-
			gl.glVertex2f(x + 20, y - 10);
			///
			gl.glVertex2f(x + 15f, y - 20);
			//\
			gl.glVertex2f(x + 10, y - 10);
		}
		gl.glEnd();
	}
}
