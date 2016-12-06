import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.sql.Time;
import java.util.*;
import java.util.Timer;

/**
 * Main class
 * Created by alvarpq on 10/5/2016.
 */
public class MapDrawer extends JFrame implements GLEventListener, KeyListener, MouseMotionListener, MouseListener
{
	static private int stepNum;
	private static QuadMap qm;

	private static gridFloatReader gfl = new gridFloatReader("ned_86879038");
	private double time;

	double halfSide = .5;
	double cubeVertex[][]={{halfSide, halfSide, halfSide},
			{halfSide, halfSide, -halfSide},
			{halfSide, -halfSide, halfSide},
			{halfSide, -halfSide, -halfSide},
			{-halfSide, halfSide, halfSide},
			{-halfSide, halfSide, -halfSide},
			{-halfSide, -halfSide, halfSide},
			{-halfSide, -halfSide, -halfSide}};
	double[] lastDir = new double[]{0,0,0};
	double[] startPos = new double[]{0,0,0};
	public boolean cubeIsAThing = false;
	public  float timeSinceCube = 0;
	//jpanel section
	static JPanel menuParts;
	static JButton go = new JButton("Generate new maps");
	static JTextField inputString = new JTextField();
	static JSlider rStart = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
	static JSlider gStart = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
	static JSlider bStart = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
	static JSlider rStop = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);

	static JSlider gStop = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
	static JSlider bStop = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
	static JSlider itNum = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
	static JLabel loadLabel = new JLabel("File to Load");
	static JLabel lowRl = new JLabel("Low Red");
	static JLabel lowGl = new JLabel("Low Green");
	static JLabel lowBl = new JLabel("Low Blue");
	static JLabel hiRl = new JLabel("High Red");
	static JLabel hiGl = new JLabel("High Green");
	static JLabel hiBl = new JLabel("High Blue");
	static JLabel lab1 = new JLabel("Number of Steps");
	static JCheckBox wireframe = new JCheckBox("Wireframe");
	float[] light_position;
	//graphics crap
	static MapDrawer mp;
	GLU glu = new GLU();
	float height = 1200;
	float width = 1200;
	double fov = 45;
	double aspectRatio = 1;
	double zNear = 1;
	double zFar = 1000;
	double[] eyePos = {80, 80, 0};
	double[] upVector = {0, 0, 1};

	public int face = 0;
	public double facePos = 0;
	public double verticalDir = 0;

	public boolean left = false, right = false, fd = false, bk = false;


	private GLCanvas canvas;

	ActionListener redOp = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			qm = new QuadMap(new gridFloatReader(inputString.getText()), wireframe.isSelected(), new Color(rStart.getValue(), gStart.getValue(), bStart.getValue()), new Color(rStop.getValue(), gStop.getValue(), bStop.getValue()), itNum.getValue() + 1);
			canvas.display();
		}
	};

	public MapDrawer(QuadMap qm)
	{
		super("Mapper");
		go.addActionListener(redOp);

		this.qm = qm;

		canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseListener(this);

		getContentPane().add(canvas);
		menuParts.setPreferredSize(new Dimension((int) width, 80));
		getContentPane().add(menuParts, BorderLayout.NORTH);

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

	public static void main(String[] args)
	{
		menuParts = new JPanel(new GridLayout(2, 8));
		menuParts.add(loadLabel);
		menuParts.add(wireframe);
		menuParts.add(lowRl);
		menuParts.add(lowGl);
		menuParts.add(lowBl);
		menuParts.add(hiRl);
		menuParts.add(hiGl);
		menuParts.add(hiBl);
		menuParts.add(lab1);

		menuParts.add(inputString);
		inputString.setText("ned_86879038");
		menuParts.add(go);
		menuParts.add(rStart);
		menuParts.add(gStart);
		menuParts.add(bStart);
		menuParts.add(rStop);
		menuParts.add(gStop);
		menuParts.add(bStop);
		menuParts.add(itNum);

		stepNum = itNum.getValue();

		qm = new QuadMap(gfl, false, Color.black, Color.GREEN, stepNum);
		mp = new MapDrawer(qm);
	}

	@Override
	public void init(GLAutoDrawable glAutoDrawable)
	{
		System.out.println("init happening");
		GL2 gl = glAutoDrawable.getGL().getGL2();
		glu = new GLU();
		gl.glClearColor(.8f, .8f, .8f, 0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);


		float[] mat_specular   = {1.0f, 1.0f, 1.0f, 1.0f};
		float[] mat_diffuse    = {1.0f, 1.0f, 0.0f, 1.0f};
		float[] mat_ambient    = {0.1f, 0.1f, 0.1f, 1.0f};
		float   mat_shininess  = .3f;
		float[] light_ambient  = {0.0f, 0.0f, 0.0f, 1.0f};
		float[] light_diffuse  = {1.0f, 1.0f, 1.0f, 1.0f};
		float[] light_specular = {.6f, .7f, .5f, 1.0f};
		float[] light_pos_temp = {0.0f, 12, (float) gfl.maxHeight * .5f, 0.0f};
		light_position = light_pos_temp;
		/* set up ambient, diffuse, and specular components for light 0 */
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, FloatBuffer.wrap(light_ambient));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, FloatBuffer.wrap(light_diffuse));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, FloatBuffer.wrap(light_specular));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(light_position));
		/** define material properties for front face of all polygons
		 * Default values are:
		 * Ambient: (0.2,0.2,.0.2,1.0)
		 * Diffuse: (0.8,0.8,0.8,1.0)
		 * Specular: (0,0,0,1)
		 * Shininess: 0 [max is 128 - it is really controlling rate of decay]
		 */
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, FloatBuffer.wrap(mat_specular));
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, FloatBuffer.wrap(mat_ambient));
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, FloatBuffer.wrap(mat_diffuse));
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, mat_shininess);


		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);


		gl.glShadeModel(GL2.GL_SMOOTH); /* enable smooth shading */
		gl.glEnable(GL2.GL_LIGHTING); /* enable lighting */
		gl.glEnable(GL2.GL_LIGHT0);  /* enable light 0 */

		gl.glDisable(GL2.GL_DEPTH_TEST);
		gl.glClearColor(135f/255f, 206f/255f, 235f/255f, 0f); //set to non-transparent black
		gl.glEnable(GL2.GL_DEPTH_TEST);
	}

	@Override
	public void display(GLAutoDrawable glAutoDrawable)
	{
		//Get context
		GL2 gl = glAutoDrawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		//Set-up the camera
		eyePos[2] = (gfl.height[(int)eyePos[0]][(int)eyePos[1]] - gfl.minHeight + 100) * .3;
		glu.gluLookAt(eyePos[0], eyePos[1], eyePos[2], GetLookVector()[0] + eyePos[0],  GetLookVector()[1] +eyePos[1], eyePos[2] + verticalDir, upVector[0], upVector[1], upVector[2]);

		//glu.gluLookAt(3, 0, 0, 0, 0, 0, 0, 1, 0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glPushMatrix();
		{
			gl.glScalef(1,1,1f);
			/*gl.glRotated(45, 0, 0, 2);
			gl.glRotated(-20, 0, 1, 0);
			gl.glRotated(-20, 1, 0, 0);*/
			if(cubeIsAThing) {
				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[6], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[7], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[3], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[2], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glEnd();
				//Back face
				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[1], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[3], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[7], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[5], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glEnd();
				//Top face
				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[0], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[1], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[5], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[4], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glEnd();
				//Front face
				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[0], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[4], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[6], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[2], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glEnd();
				//Left face
				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[4], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[5], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[7], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[6], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glEnd();
				//Right face
				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[2], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[3], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[1], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				gl.glVertex3dv(DoubleBuffer.wrap(Utilities.Add(Utilities.Add(cubeVertex[0], startPos), Utilities.Multiply(timeSinceCube, lastDir))));
				timeSinceCube += .1;
				gl.glEnd();
			}

			qm.DrawArea(gl);
		}
		gl.glPopMatrix();
		ManageMovement(fd,bk,left,right);
	}


	@Override
	public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height)
	{
		System.out.println("Entering reshape\n x = " + x + "\ny = " + y + "\nwidth = " + width + "\nheight = " + height);
		//Get the context
		GL2 gl = glautodrawable.getGL().getGL2();
		if (height <= 0)
		{
			height = 1;
		}

		gl.glViewport(3, 6, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(fov, aspectRatio, zNear, zFar);
	}

	@Override
	public void dispose(GLAutoDrawable glAutoDrawable)
	{

	}

	@Override
	public void keyPressed(KeyEvent ke)
	{
		char key = ke.getKeyChar();
		switch (key)
		{
			case 'q'://away, rightish
			case 'Q':
				TurnLeft(.1);
				break;
			case 'e':
			case 'E'://down
				TurnRight(.1);
				break;
			case ' '://in, leftish
				eyePos[2] += 1;
				break;
			case 'x':
			case 'X'://just kind of makes it...disappear
				eyePos[2] -=1;
				break;
			case 'd'://x,t are horizontal opposites
			case 'D'://see r//p-z are vertical opposites
				right = true;
				//right
				break;
			case 'a':
			case 'A':
				left = true;
				//left
				break;
			case 'S'://down after a spooky flip
			case 's':
				bk = true;
				//back
				break;
			case 'w':
			case 'W':
				fd = true;
				//fd
				break;
			case 'z':
			case 'Z':
				verticalDir +=.1;
		}
		canvas.display();
		//Redisplay
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		char key = arg0.getKeyChar();
		switch (key)
		{
			case 'q'://away, rightish
			case 'Q':
				TurnLeft(.1);
				break;
			case 'e':
			case 'E'://down
				TurnRight(.1);
				break;
			case ' '://in, leftish
				eyePos[2] += 1;
				break;
			case 'x':
			case 'X'://just kind of makes it...disappear
				eyePos[2] -=1;
				break;
			case 'd'://x,t are horizontal opposites
			case 'D'://see r//p-z are vertical opposites
				right = false;
				//right
				break;
			case 'a':
			case 'A':
				left = false;
				//left
				break;
			case 'S'://down after a spooky flip
			case 's':
				bk = false;
				//back
				break;
			case 'w':
			case 'W':
				fd = false;
				//fd
				break;
			case 'z':
			case 'Z':
				verticalDir +=.1;
		}
		canvas.display();

		//Redisplay
	}


	private void TurnRight(double turn)
	{
		facePos += turn;
		if (facePos > 1)
		{
			face = (face + 1) % 4;
			facePos -= 2;
		}
		else if (facePos < -1)
		{
			face = (face + 3) % 4;
			facePos += 2;
		}
	}

	private void TurnLeft(double turn)
	{
		facePos -= turn;
		if (facePos < -1)
		{
			face = (face + 3) % 4;
			facePos += 2;
		}
	}

	public void ManageMovement(boolean forward, boolean backward, boolean left, boolean right)
	{
		if(forward)
		{
			eyePos[0] += GetMovement()[0]/4;
			eyePos[1] += GetMovement()[1]/4;
		}
		if(backward)
		{
			eyePos[0] -= GetMovement()[0]/4;
			eyePos[1] -= GetMovement()[1]/4;
		}
		if(left)
		{
			face = (face + 3) % 4;
			eyePos[0] += GetMovement()[0]/4;
			eyePos[1] += GetMovement()[1]/4;
			face = (face + 1) % 4;
		}
		if(right)
		{
			face = (face + 1) % 4;
			eyePos[0] += GetMovement()[0]/4;
			eyePos[1] += GetMovement()[1]/4;
			face = (face + 3) % 4;
		}
		if(eyePos[0] < 0)
		{
			eyePos[0] = 0;
			bk = false;
			fd = false;
			this.right = false;
			this.left = false;
		}else if(eyePos[0] > 320)
		{
			eyePos[0] = 320;
			bk = false;
			fd = false;
			this.right = false;
			this.left = false;
		}
		if(eyePos[1] < 0) {
			eyePos[1] = 0;
			bk = false;
			fd = false;
			this.right = false;
			this.left = false;
		}else if(eyePos[1] > 283)
		{
			eyePos[1] = 283;
			bk = false;
			fd = false;
			this.right = false;
			this.left = false;
		}
	}

	//from rotated color cube
	@Override
	public void keyTyped(KeyEvent ke)
	{
		canvas.display();
	}

	public double[] GetLookVector()
	{
		switch (face)
		{
			case 0://forward
				return new double[]{facePos, 1};
			case 1://right
				return new double[]{1, -facePos};
			case 2://down
				return new double[]{-facePos, -1};
			default://left
				return new double[]{-1, facePos};
		}
	}

	public void LookVertical(double move)
	{
		verticalDir -=move;
		if(verticalDir > 4)
		{
			verticalDir = 4;
			return;
		}
		else if(verticalDir < -4)
		{
			verticalDir = -4;
		}
	}

	public double[] GetMovement()
	{
		return GetLookVector();
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		try {
			Robot rob = new Robot();
			rob.mouseMove(canvas.getWidth()/2, canvas.getHeight()/2);
		} catch (AWTException x) {
			x.printStackTrace();
		}
		LookVertical((e.getYOnScreen()-(double)canvas.getHeight()/2)/((double)canvas.getHeight()/2));
		TurnRight((e.getXOnScreen()-(double)canvas.getWidth()/2)/((double)canvas.getWidth()/2));
		canvas.display();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		cubeIsAThing = true;
		timeSinceCube = 0;
		lastDir= new double[]{GetLookVector()[0], GetLookVector()[1],verticalDir};
		startPos = new double[]{eyePos[0], eyePos[1], eyePos[2]};
		System.out.println("BANG!");
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}