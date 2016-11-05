/**
 * Created by alvar on 10/18/2016.
 */
public class Coordinate
{
	public float x;
	public float y;


	public Coordinate(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public Coordinate GetBetween(Coordinate other)
	{
		return new Coordinate((x + other.x) /2, (y + other.y) / 2);
	}
}