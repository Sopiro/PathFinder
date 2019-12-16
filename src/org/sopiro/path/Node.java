package org.sopiro.path;

/**
 * @author Sopiro
 *
 */
public class Node
{
	public static final int Air = 0;
	public static final int Block = 1;
	public static final int Start = 2;
	public static final int End = 3;
	public static final int Road = 4;
	public static final int Explored = 5;

	public int x, y;
	public double f, g, h;
	public int type;
	public Node parent;

	public Node()
	{
		this(0, 0);
	}

	public Node(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void clearData()
	{
		f = g = h = 0;
	}

	public String toString()
	{
		return "(" + x + ", " + y + ", type:" + type + "), (f:" + f + ", g:" + g + ", h:" + h + ")";
	}
}
