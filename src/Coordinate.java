/**
 * Created by alvar on 10/18/2016.
 */
public class Coordinate
{
	public double x;
	public double y;


	public Coordinate(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Coordinate GetBetween(Coordinate other)
	{
		return new Coordinate((x + other.x) /2, (y + other.y) / 2);
	}
}