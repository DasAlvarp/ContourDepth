import java.awt.*;

/**
 * Created by alvarpq on 11/8/2016.
 */
public class BossMain
{

	public static void main(String[] args)
	{

		gfl = new gridFloatReader("ned_86879038");

		low = gfl.minHeight;
		high = gfl.maxHeight;
		stepNum = 10;

		lowR = 0;
		lowG = 0;
		lowB = 0;

		highR = 255;
		highB = 255;
		highG = 255;


		marker = false;


		qm = new QuadMap(gfl, new Color(lowR, lowG, lowB), new Color(lowR, lowG, lowB));
		new MapDrawer();
	}
}
