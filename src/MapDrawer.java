import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by alvarpq on 10/5/2016.
 */
public class MapDrawer extends JFrame implements GLEventListener, KeyListener
{
	 private GLCanvas canvas;



	GLU glu = new GLU();

	GLProfile glProfile = null;
	GLCapabilities glcapabilities = null;

	public float angleX = 0;


	static gridFloatReader gfl;


	float height = 600;
	float width = 1200;

	static public double low, high;
	static public int stepNum, lowR, lowG, lowB, highR, highG, highB;

	public static QuadMap qm;

	static boolean marker = true;

	public MapDrawer()
	{

		super("Mapper");

		canvas = new GLCanvas();
		canvas.addGLEventListener(this);

		getContentPane().add(canvas);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent windowevent)
			{
				remove(canvas);
				dispose();
				System.exit(0);
			}
		});

		setSize((int) width, (int) height);
		setVisible(true);

		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);


	}

	public static void createAndShowGUI(){
		GL2ColorCubePerspective s = new GL2ColorCubePerspective();
		s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.pack();
		s.setVisible(true);
	}

	public static void main(String[] args)
	{

		gfl = new gridFloatReader(args[0]);

		if (args[1].equals("auto"))
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
		glu = new GLU();
		gl.glClearColor(.8f, .8f, .8f, 0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);

	}

	@Override
	public void display(GLAutoDrawable glAutoDrawable)
	{
		System.out.println("Entering display");
		//Get context
		GL2 gl = glAutoDrawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		//Set-up the camera
		glu.gluLookAt(eyePos[0], eyePos[1], eyePos[2], targetPos[0], targetPos[1], targetPos[2], upVector[0], upVector[1], upVector[2]);
		//glu.gluLookAt(3, 0, 0, 0, 0, 0, 0, 1, 0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		//Set a color (redish - no other components)
		gl.glColor3f(0.0f, 1f, 0.0f);
		//Define a primitive -  A polygon in this case
		//DrawArea(gl, stepNum, new Color(lowR, lowG, lowB), new Color(highR, highG, highB));
		gl.glPushMatrix();
		{
			gl.glTranslated(0, 0, -3.0);
			gl.glRotated(90, 0, 0, 1);
			gl.glRotated(45, 0, 1, 0);
			gl.glRotated(45, 1, 0, 0);
			qm.DrawArea(gl);

		}
		gl.glPopMatrix();

	}


	//draws all contour lines
	public void DrawArea(GL2 gl, int stepNum, Color colorStart, Color colorEnd)
	{
		float scaling = Utilities.GetMin(width / (float) gfl.height.length, height / (float) gfl.height[0].length);
		double stepSize = (gfl.maxHeight - gfl.minHeight) / stepNum;
		double[] startColor = new double[]{(double) colorStart.getRed() / 255.0, (double) colorStart.getGreen() / 255.0, (double) colorStart.getBlue() / 255.0};
		double[] endColor = new double[]{(double) colorEnd.getRed() / 255.0, (double) colorEnd.getGreen() / 255.0, (double) colorEnd.getBlue() / 255.0};

		double[] colorStep = new double[3];
		for (int x = 0; x < 3; x++)
		{
			colorStep[x] = (endColor[x] - startColor[x]) / stepNum;
		}

		for (int s = 0; s < stepNum; s++)
		{
			DrawLevel((float) gfl.minHeight + s * (float) stepSize, gl, Utilities.DoubleToColor(Utilities.Add(startColor, Utilities.Multiply(s, colorStep))), scaling);

		}
	}

	//draws one level of contour lines
	public void DrawLevel(float level, GL2 gl, Color color, float scaling)
	{
		for (int x = 0; x < gfl.height.length - 1; x++)
		{
			for (int y = 0; y < gfl.height[x].length - 1; y++)
			{
				Square sq = new Square(x, y, scaling, scaling, gfl);
				sq.DrawContour(level, gl, color);

			}
		}
		if (marker)
		{
			DrawStar(scaling * (float) gfl.maxHeightxi, scaling * (float) gfl.maxHeightyi, Color.YELLOW, gl);
		}
	}


	@Override
	public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height)
	{
		System.out.println("Entering reshape(); x=" + x + " y=" + y + " width=" + width + " height=" + height);
		//Get the context
		GL2 gl = glautodrawable.getGL().getGL2();
		if (height <= 0)
		{
			height = 1;
		}
		final float h = (float) width / (float) height;
		gl.glViewport(3, 6, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(fov, aspectRatio, zNear, zFar);
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
			gl.glVertex2f(x + 10, y);
			// /
			gl.glVertex2f(x + 10, y + 10);
			//\
			gl.glVertex2f(x + 20, y);
			//-
			gl.glVertex2f(x + 30, y);
			///
			gl.glVertex2f(x + 20, y - 5f);
			//\
			gl.glVertex2f(x + 30, y - 10);
			//-
			gl.glVertex2f(x + 20, y - 10);
			///
			gl.glVertex2f(x + 15f, y - 20);
			//\
			gl.glVertex2f(x + 10, y - 10);
		}
		gl.glEnd();
	}

	@Override
	public void keyPressed(KeyEvent ke)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	double fov=45;
	double aspectRatio=1;
	double zNear=0.1;
	double zFar=100;
	double[] eyePos={3,3,3};

	double[] targetPos={0,0,0};
	double[] upVector={0,0,1};
	double dIncrement=.2;
	double aIncrement=0.1;
	double eyeDist=Math.sqrt(eyePos[0]*eyePos[0]+eyePos[1]*eyePos[1]+eyePos[2]*eyePos[2]);
	double phi=0;
	double theta=90.0/180.0*Math.PI;

	@Override
	public void keyTyped(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		System.out.println(ke.getKeyChar());
		char key = ke.getKeyChar();
		boolean updateSpherical=false, updateCartesian=false;
		switch (key)
		{
			case 'x':
			case 'X':
				eyePos[0] -= dIncrement;
				updateSpherical = true;
				break;
			case 'y':
			case 'Y':
				eyePos[1] -= dIncrement;
				updateSpherical = true;
				break;
			case 'z':
			case 'Z':
				eyePos[2] -= dIncrement;
				updateSpherical = true;
				break;
			case 'r':
			case 'R':
				eyeDist -= dIncrement;
				updateCartesian = true;
				break;
			case 't':
			case 'T':
				theta -= aIncrement;
				updateCartesian = true;
				break;
			case 'p':
			case 'P':
				phi -= aIncrement;
				updateCartesian = true;
				break;
		}

		if (updateSpherical)
		{
			eyeDist=Math.sqrt(eyePos[0]*eyePos[0]+eyePos[1]*eyePos[1]+eyePos[2]*eyePos[2]);
			theta=Math.atan2(eyePos[1], eyePos[0]);
			phi=Math.acos(eyePos[2]/eyeDist);
		}
		if (updateCartesian)
		{
			eyePos[0]=eyeDist*Math.cos(theta)*Math.sin(phi);
			eyePos[1]=eyeDist*Math.sin(theta)*Math.sin(phi);
			eyePos[2]=eyeDist*Math.cos(phi);
		}
		//Redisplay
		canvas.display();
	}
}
