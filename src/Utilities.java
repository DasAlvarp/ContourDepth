import java.awt.*;

/**
 * Created by alvar on 11/5/2016.
 */
public class Utilities
{
	public static Color DoubleToColor(double[] color)
	{
		return new Color((float) color[0], (float) color[1], (float) color[2]);
	}

	public static double[] ColorToDouble(Color color)
	{
		return new double[]{(double) color.getRed() / 255.0, (double) color.getGreen() / 255.0, (double) color.getBlue() / 255.0};

	}

	public static double[] Add(double[] v1, double[] v2)
	{
		double[] toRet = new double[v1.length];
		for (int x = 0; x < toRet.length; x++)
		{
			toRet[x] = v1[x] + v2[x];
		}

		return toRet;
	}

	public static double[] Subtract(double[] v1, double[] v2)
	{
		double[] toRet = new double[v1.length];
		for (int x = 0; x < toRet.length; x++)
		{
			toRet[x] = v1[x] - v2[x];
		}

		return toRet;
	}

	public static double[] Multiply(double scalar, double[] vals)
	{
		double[] toRet = new double[vals.length];
		for (int x = 0; x < vals.length; x++)
		{
			toRet[x] = vals[x] * scalar;
		}
		return toRet;
	}

	public static float GetMin(float f1, float f2)
	{
		if (f1 < f2)
		{
			return f1;
		}
		else
		{
			return f2;
		}
	}
}