import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by alvarpq on 10/5/2016.
 */
public class MapDrawer extends JFrame implements GLEventListener, KeyListener
{
	private GLCanvas canvas;

	GLU glu = new GLU();

	static gridFloatReader gfl = new gridFloatReader("ned_86879038");;


	float height = 1200;
	float width = 1200;

	static public double low, high;
	static public int stepNum, lowR, lowG, lowB, highR, highG, highB;

	public static QuadMap qm;

	//jpanel section
	static JPanel menuParts;
	static JButton go = new JButton("Generate new maps");
	static JTextField inputString = new JTextField();;

	static JSlider rStart = new JSlider(JSlider.VERTICAL, 0, 255, 0);
	static JSlider gStart = new JSlider(JSlider.VERTICAL, 0, 255, 0);
	static JSlider bStart = new JSlider(JSlider.VERTICAL, 0, 255, 0);

	static JSlider rStop = new JSlider(JSlider.VERTICAL, 0, 255, 255);
	static JSlider gStop = new JSlider(JSlider.VERTICAL, 0, 255, 255);
	static JSlider bStop = new JSlider(JSlider.VERTICAL, 0, 255, 255);

	static JSlider itNum = new JSlider(JSlider.VERTICAL, 0, 100, 10);

	static JLabel loadLabel = new JLabel("File to Load");
	static JLabel lowRl = new JLabel("Low Red");
	static JLabel lowGl = new JLabel("Low Green");
	static JLabel lowBl = new JLabel("Low Blue");
	static JLabel hiRl = new JLabel("High Red");
	static JLabel hiGl = new JLabel("High Green");
	static JLabel hiBl = new JLabel("High Blue");
	static JLabel lab1 = new JLabel("Number of Steps");

	static JCheckBox wireframe = new JCheckBox("Wireframe");

static MapDrawer mp;
	public static void main(String[] args)
	{
		menuParts = new JPanel(new GridLayout(2,8));
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
		menuParts.add(go);
		menuParts.add(rStart);
		menuParts.add(gStart);
		menuParts.add(bStart);
		menuParts.add(rStop);
		menuParts.add(gStop);
		menuParts.add(bStop);
		menuParts.add(itNum);



		low = gfl.minHeight;
		high = gfl.maxHeight;
		stepNum = itNum.getValue();

		lowR = rStart.getValue();
		lowG = gStart.getValue();
		lowB = bStart.getValue();

		highR = rStart.getValue();
		highB = gStart.getValue();
		highG = bStart.getValue();





		qm = new QuadMap(gfl, false, new Color(lowR, lowG, lowB), new Color(lowR, lowG, lowB), stepNum);
		mp = new MapDrawer(qm);
	}


	public MapDrawer(QuadMap qm)
	{
		super("Mapper");
		go.addActionListener(redOp);

		this.qm = qm;

		canvas = new GLCanvas();
		canvas.addGLEventListener(this);

		getContentPane().add(canvas);
		menuParts.setPreferredSize(new Dimension((int)width, 80));
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
		glu.gluLookAt(eyePos[0], eyePos[2], eyePos[1], targetPos[0], targetPos[2], targetPos[1], upVector[0], upVector[1], upVector[2]);
		//glu.gluLookAt(3, 0, 0, 0, 0, 0, 0, 1, 0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		//Set a color (redish - no other components)
		gl.glColor3f(0.0f, 1f, 0.0f);
		//Define a primitive -  A polygon in this case
		//DrawArea(gl, stepNum, new Color(lowR, lowG, lowB), new Color(highR, highG, highB));
		gl.glPushMatrix();
		{
			gl.glRotated(45, 0, 0, -1);
			gl.glRotated(-20, 0, 1, 0);
			gl.glRotated(-20, 1, 0, 0);
			qm.DrawArea(gl);

		}
		gl.glPopMatrix();

	}


	@Override
	public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height)
	{
		System.out.println("Entering reshape(); x=" + x + " y=" + y + " width=" + width + " height=" + height);
		//Get the context
		GL2 gl = glautodrawable.getGL().getGL2();
		if(height <= 0)
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
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	double fov = 45;
	double aspectRatio = 1;
	double zNear = 0.1;
	double zFar = 100;
	double[] eyePos = {1, 1, 3};

	double[] targetPos = {0, 0, 0};
	double[] upVector = {0, 0, 1};
	double dIncrement = .2;
	double aIncrement = 0.1;
	double eyeDist = Math.sqrt(eyePos[0] * eyePos[0] + eyePos[1] * eyePos[1] + eyePos[2] * eyePos[2]);
	double phi = 0;
	double theta = 90.0 / 180.0 * Math.PI;

	@Override
	public void keyTyped(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		System.out.println(ke.getKeyChar());
		char key = ke.getKeyChar();
		boolean updateSpherical = false, updateCartesian = false;
		switch(key)
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

		if(updateSpherical)
		{
			eyeDist = Math.sqrt(eyePos[0] * eyePos[0] + eyePos[1] * eyePos[1] + eyePos[2] * eyePos[2]);
			theta = Math.atan2(eyePos[2], eyePos[0]);
			phi = Math.acos(eyePos[1] / eyeDist);
		}
		if(updateCartesian)
		{
			eyePos[0] = eyeDist * Math.cos(theta) * Math.sin(phi);
			eyePos[2] = eyeDist * Math.sin(theta) * Math.sin(phi);
			eyePos[1] = eyeDist * Math.cos(phi);
		}
		//Redisplay
		canvas.display();
	}

	ActionListener redOp = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JButton b = (JButton) arg0.getSource();


			qm = new QuadMap(new gridFloatReader(inputString.getText()), wireframe.isSelected() , new Color(rStart.getValue(), gStart.getValue(), bStart.getValue()), new Color(rStop.getValue(), gStop.getValue(), bStop.getValue()), itNum.getValue() + 1);
			canvas.display();
			//mp = new MapDrawer(qm);
		}
	};
}
