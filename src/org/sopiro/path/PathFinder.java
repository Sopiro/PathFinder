package org.sopiro.path;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Sopiro
 *
 */
public class PathFinder
{
	public int w, h;
	public Input input;
	public Node[] nodes;

	public ArrayList<Node> open;
	public ArrayList<Node> close;
	public ArrayList<Node> res;

	public int cx, cy, px, py;

	public PathFinder(int w, int h, Input input)
	{
		this.w = w;
		this.h = h;
		this.input = input;

		nodes = new Node[w * h];
		for (int i = 0; i < w * h; i++)
			nodes[i] = new Node(i % w, i / w);

		open = new ArrayList<Node>();
		close = new ArrayList<Node>();
		res = new ArrayList<Node>();
	}

	public void update(Point p)
	{
		drawBlock(p);

		if (input.whenButtonOncePressed(MouseEvent.BUTTON3 + 1) || input.whenKeyOncePressed(KeyEvent.VK_S))
			setType(cx, cy, Node.Start);

		if (input.whenButtonOncePressed(MouseEvent.BUTTON3 + 2) || input.whenKeyOncePressed(KeyEvent.VK_E))
			setType(cx, cy, Node.End);

		if (input.whenKeyOncePressed(KeyEvent.VK_C))
			clear();

		if (input.whenKeyOncePressed(KeyEvent.VK_R))
			reset();

		if (input.whenKeyOncePressed(KeyEvent.VK_SPACE))
			execute(2);

		if (input.whenKeyOncePressed(KeyEvent.VK_1))
			execute(0);

		if (input.whenKeyOncePressed(KeyEvent.VK_2))
			execute(1);
	}

	public void execute(int method)
	{
		reset();

		Node start = null;
		Node end = null;

		for (int i = 0; i < nodes.length; i++)
		{
			if (getType(i) == Node.Start)
				start = nodes[i];
			if (getType(i) == Node.End)
				end = nodes[i];
		}

		if (start == null || end == null)
		{
			System.err.println("Error : Start or End point does not exist");
			return;
		}

		if (method == 0)
			for (int i = 0; i < nodes.length; i++)
			{
				int dx = Math.abs(end.x - i % w);
				int dy = Math.abs(end.y - i / w);

				if (dx > dy)
					nodes[i].h = (dx - dy) + dy * 14;
				else
					nodes[i].h = (dy - dx) + dx * 14;

				nodes[i].f = nodes[i].h;
			}

		if (method == 1)
			for (int i = 0; i < nodes.length; i++)
			{
				int dx = Math.abs(end.x - i % w);
				int dy = Math.abs(end.y - i / w);

				nodes[i].h = (dx + dy) * 10;
				nodes[i].f = nodes[i].h;
			}

		if (method == 2)
			for (int i = 0; i < nodes.length; i++)
			{
				double dx = Math.abs(end.x - i % w);
				double dy = Math.abs(end.y - i / w);

				nodes[i].h = Math.sqrt(dx * dx + dy * dy);
				nodes[i].f = nodes[i].h;
			}

		open.add(start);

		boolean done = false;
		boolean error = false;

		while (!done && !error)
		{
			if (open.size() == 0)
			{
				error = true;
				continue;
			}
			if (close.contains(end))
			{
				done = true;
				continue;
			}

			Node it = open.get(0);
			double lf = it.f;
			Node current = it;

			for (int i = 1; i < open.size(); i++)
			{
				it = open.get(i);

				if (it.f < lf)
				{
					current = it;
					lf = it.f;
				}
			}

			open.remove(current);
			close.add(current);

			// System.out.println("current : " + current);

			boolean l = false;
			boolean r = false;
			boolean u = false;
			boolean d = false;

			for (int i = 1; i < 9; i += 2)
			{
				int x = current.x - 1 + i % 3;
				int y = current.y - 1 + i / 3;

				if (x < 0 || y < 0 || x >= w || y >= h)
					continue;

				if (nodes[x + y * w].type == Node.Block)
				{
					if (i == 1)
						u = true;
					if (i == 3)
						l = true;
					if (i == 5)
						r = true;
					if (i == 7)
						d = true;
				}
			}

			// System.out.println(l + ", " + r + ", " + u + ", " + d);

			for (int i = 0; i < 9; i++)
			{
				if (l && (i == 0 || i == 6))
					continue;
				if (r && (i == 2 || i == 8))
					continue;
				if (u && (i == 0 || i == 2))
					continue;
				if (d && (i == 6 || i == 8))
					continue;

				int x = current.x - 1 + i % 3;
				int y = current.y - 1 + i / 3;

				if (x < 0 || y < 0 || x >= w || y >= h || i == 4)
					continue;

				Node t = nodes[x + y * w];

				if (getType(x, y) == Node.Block || close.contains(t))
					continue;

				double g = (i % 2 == 0 ? Math.sqrt(2) : 1);

				if (!open.contains(t))
				{
					t.parent = current;
					t.g = current.g + g;
					t.f = t.g + t.h;
					open.add(t);
				} else
				{
					if (current.g + g > t.g)
					{
						// System.out.println(i + " : " + t + ", parent : " +
						// t.parent);
						continue;
					}

					t.parent = current;
					t.g = current.g + g;
					t.f = t.g + t.h;
				}

				// System.out.println(i + " : " + t + ", parent : " + t.parent);
			}

			// System.out.println("******************************************************************");
		}

		if (error)
		{
			System.err.println("Error : No path");
		} else
		{
			Node t = end.parent;

			while (t != start)
			{
				t.type = Node.Road;
				res.add(t);
				t = t.parent;
			}

			Collections.reverse(res);

			System.out.println("Done ! : " + res.size());
		}
	}

	private void drawBlock(Point p)
	{
		if (p == null)
			return;

		px = cx;
		py = cy;
		cx = p.x / Main.SCALE;
		cy = p.y / Main.SCALE;

		if (input.whenButtonPressed(MouseEvent.BUTTON1))
		{
			int sx = px;
			int ex = cx;
			int sy = py;
			int ey = cy;

			if (px > cx)
			{
				sx = cx;
				ex = px;
				sy = cy;
				ey = py;
			}

			setType(cx, cy, Node.Block);

			int gradient = (int) Math.abs((double) (ey - sy) / (double) (ex - sx));

			if (gradient < 1)
				for (int x = sx; x < ex; x++)
				{
					int t = (int) ((double) (ey - sy) * ((double) (x - sx)) / ((double) (ex - sx)));
					int y = (int) ((sy + t));
					setType(x, y, Node.Block);
				}

			sx = px;
			ex = cx;
			sy = py;
			ey = cy;

			if (py > cy)
			{
				sx = cx;
				ex = px;
				sy = cy;
				ey = py;
			}

			if (gradient >= 1)
				for (int y = sy; y < ey; y++)
				{
					int t = (int) ((double) (ex - sx) * ((double) (y - sy)) / ((double) (ey - sy)));
					int x = (int) ((sx + t));
					setType(x, y, Node.Block);
				}
		}

		if (input.whenButtonPressed(MouseEvent.BUTTON3))
		{
			int sx = px;
			int ex = cx;
			int sy = py;
			int ey = cy;

			if (px > cx)
			{
				sx = cx;
				ex = px;
				sy = cy;
				ey = py;
			}

			setType(cx, cy, Node.Air);

			int gradient = (int) Math.abs((double) (ey - sy) / (double) (ex - sx));

			if (gradient < 1)
				for (int x = sx; x < ex; x++)
				{
					int t = (int) ((double) (ey - sy) * ((double) (x - sx)) / ((double) (ex - sx)));
					int y = (int) ((sy + t));
					setType(x, y, Node.Air);
				}

			sx = px;
			ex = cx;
			sy = py;
			ey = cy;

			if (py > cy)
			{
				sx = cx;
				ex = px;
				sy = cy;
				ey = py;
			}

			if (gradient >= 1)
				for (int y = sy; y < ey; y++)
				{
					int t = (int) ((double) (ex - sx) * ((double) (y - sy)) / ((double) (ey - sy)));
					int x = (int) ((sx + t));
					setType(x, y, Node.Air);
				}
		}
	}

	public void setType(int x, int y, int type)
	{
		if (x < 0 || x >= w || y < 0 || y >= h)
			return;

		if (type == Node.Start)
			for (int i = 0; i < w * h; i++)
				if (nodes[i].type == Node.Start)
					setType(i, 0);

		if (type == Node.End)
			for (int i = 0; i < w * h; i++)
				if (nodes[i].type == Node.End)
					setType(i, 0);

		nodes[x + y * w].type = type;
	}

	public void setType(int i, int type)
	{
		setType(i % w, i / w, type);
	}

	public int getType(int x, int y)
	{
		return nodes[x + y * w].type;
	}

	public int getType(int i)
	{
		return getType(i % w, i / w);
	}

	public void clear()
	{
		for (int i = 0; i < nodes.length; i++)
			setType(i, Node.Air);

		open.clear();
		close.clear();
		res.clear();
	}

	public void reset()
	{
		for (int i = 0; i < nodes.length; i++)
			if (getType(i) == Node.Road)
				setType(i, Node.Air);

		for (int i = 0; i < nodes.length; i++)
			nodes[i].clearData();

		open.clear();
		close.clear();
		res.clear();
	}
}
