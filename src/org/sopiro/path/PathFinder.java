package org.sopiro.path;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;

import static org.sopiro.path.Heuristics.*;

enum Heuristics
{
    TEN,
    FOURTEEN,
    SQUARE_ROOT
}

public class PathFinder
{
    private static final double SQRT2 = Math.sqrt(2.0);

    private int width;
    private int height;

    private Input input;
    private Node[] nodes;

    private PriorityQueue<Node> open;
    private Set<Node> close;
    private ArrayList<Node> result;

    private Point current;
    private Point previous;

    private Node start;
    private Node end;

    private long lastTime = 0;

    public PathFinder(int width, int height, Input input)
    {
        this.width = width;
        this.height = height;
        this.input = input;

        nodes = new Node[width * height];

        for (int i = 0; i < width * height; i++)
            nodes[i] = new Node(i % width, i / width);

        open = new PriorityQueue<Node>(nodes.length, (Node a, Node b) -> (a.f - b.f) < 0 ? -1 : 1);
        close = new HashSet(nodes.length);
        result = new ArrayList(nodes.length);

        current = new Point(0, 0);
        previous = new Point(0, 0);
    }

    public void update(final Point mousePosition)
    {
        if (mousePosition != null)
        {
            previous.x = current.x;
            previous.y = current.y;
            current.x = mousePosition.x / Main.SCALE;
            current.y = mousePosition.y / Main.SCALE;

            if (input.isButtonDown(MouseEvent.BUTTON1))
            {
                drawLine(NodeType.Wall);
            } else if (input.isButtonDown(MouseEvent.BUTTON3))
            {
                drawLine(NodeType.Air);
            }
        }

        if (input.isButtonPressed(MouseEvent.BUTTON3 + 1) || input.isKeyPressed(KeyEvent.VK_S))
        {
            setType(current.x, current.y, NodeType.Start);
            this.start = nodes[current.x + current.y * this.width];
        }

        if (input.isButtonPressed(MouseEvent.BUTTON3 + 2) || input.isKeyPressed(KeyEvent.VK_E))
        {
            setType(current.x, current.y, NodeType.End);
            this.end = nodes[current.x + current.y * this.width];
        }

        if (input.isKeyPressed(KeyEvent.VK_SPACE)) execute(SQUARE_ROOT);
        if (input.isKeyPressed(KeyEvent.VK_1)) execute(TEN);
        if (input.isKeyPressed(KeyEvent.VK_2)) execute(FOURTEEN);
        if (input.isKeyPressed(KeyEvent.VK_C)) clear();
        if (input.isKeyPressed(KeyEvent.VK_R)) reset();
    }

    private void execute(Heuristics method)
    {
        reset();

        if (start == null || end == null)
        {
            System.err.println("Error : Start or End point does not exist");
            return;
        }

        // Set up heuristic value
        switch (method)
        {
            case FOURTEEN:
            {
                for (int i = 0; i < nodes.length; i++)
                {
                    int dx = Math.abs(end.x - i % width);
                    int dy = Math.abs(end.y - i / width);

                    if (dx > dy)
                        nodes[i].h = (dx - dy) + dy * 14;
                    else
                        nodes[i].h = (dy - dx) + dx * 14;

                    nodes[i].f = nodes[i].h;
                }
                break;
            }
            case TEN:
            {
                for (int i = 0; i < nodes.length; i++)
                {
                    int dx = Math.abs(end.x - i % width);
                    int dy = Math.abs(end.y - i / width);

                    nodes[i].h = (dx + dy) * 10;
                    nodes[i].f = nodes[i].h;
                }
                break;
            }
            case SQUARE_ROOT:
            {
                for (int i = 0; i < nodes.length; i++)
                {
                    double dx = Math.abs(end.x - i % width);
                    double dy = Math.abs(end.y - i / width);

                    nodes[i].h = Math.sqrt(dx * dx + dy * dy);
                    nodes[i].f = nodes[i].h;
                }
                break;
            }
        }

        open.add(start);

        boolean error = false;

        while (true)
        {
            if (open.isEmpty())
            {
                error = true;
                break;
            }
            if (close.contains(end))
            {
                break;
            }

            Node current = open.poll();
            close.add(current);

            if (current.type == NodeType.Air)
                current.type = NodeType.Explored;

            boolean l = false;
            boolean r = false;
            boolean u = false;
            boolean d = false;

            for (int i = 1; i < 9; i += 2)
            {
                int x = current.x - 1 + i % 3;
                int y = current.y - 1 + i / 3;

                if (x < 0 || y < 0 || x >= width || y >= height) continue;

                if (nodes[x + y * width].type == NodeType.Wall)
                {
                    if (i == 1) u = true;
                    else if (i == 3) l = true;
                    else if (i == 5) r = true;
                    else if (i == 7) d = true;
                }
            }

            for (int i = 0; i < 9; i++)
            {
                // Prevent diagonal wall penetrating
                if (l && (i == 0 || i == 6)) continue;
                if (r && (i == 2 || i == 8)) continue;
                if (u && (i == 0 || i == 2)) continue;
                if (d && (i == 6 || i == 8)) continue;

                int x = current.x - 1 + i % 3;
                int y = current.y - 1 + i / 3;

                if (x < 0 || y < 0 || x >= width || y >= height || i == 4) continue;

                Node t = nodes[x + y * width];

                if (getType(x, y) == NodeType.Wall || close.contains(t)) continue;

                double g = (i % 2 == 0 ? SQRT2 : 1.0);

                if (!open.contains(t))
                {
                    t.parent = current;
                    t.g = current.g + g;
                    t.f = t.g + t.h;

                    open.add(t);
                } else
                {
                    if (current.g + g > t.g) continue;

                    t.parent = current;
                    t.g = current.g + g;
                    t.f = t.g + t.h;
                }
            }
        }

        long passedTime = System.currentTimeMillis() - lastTime;

        if (error)
        {
            System.err.println("Error: No path, " + passedTime + "ms");
        } else
        {
            Node t = end.parent;

            // Back track
            while (t != start)
            {
                t.type = NodeType.Road;
                result.add(t);
                t = t.parent;
            }

            Collections.reverse(result);

            System.out.println("Done!: " + result.size() + " blocks far, " + passedTime + "ms");
        }
    }

    private void drawLine(NodeType type)
    {
        final double gradient = Math.abs((current.y - previous.y) / (double) (current.x - previous.x));

        int sx = previous.x;
        int ex = current.x;
        int sy = previous.y;
        int ey = current.y;

        setType(current.x, current.y, type);

        if (gradient < 1.0)
        {
            if (previous.x > current.x)
            {
                sx = current.x;
                ex = previous.x;
                sy = current.y;
                ey = previous.y;
            }

            for (int x = sx; x < ex; x++)
            {
                int t = (int) ((double) (ey - sy) * ((double) (x - sx)) / ((double) (ex - sx)));
                int y = sy + t;

                setType(x, y, type);
            }
        } else
        {
            if (previous.y > current.y)
            {
                sx = current.x;
                ex = previous.x;
                sy = current.y;
                ey = previous.y;
            }

            for (int y = sy; y < ey; y++)
            {
                int t = (int) ((double) (ex - sx) * ((double) (y - sy)) / ((double) (ey - sy)));
                int x = sx + t;

                setType(x, y, type);
            }
        }
    }

    private void setType(int x, int y, NodeType type)
    {
        if (x < 0 || x >= width || y < 0 || y >= height) return;

        switch (type)
        {
            case Start:
            {
                if (start != null) setType(start.x, start.y, NodeType.Air);
                break;
            }
            case End:
            {
                if (end != null) setType(end.x, end.y, NodeType.Air);
                break;
            }
        }

        nodes[x + y * width].type = type;
    }

    private void setType(int i, NodeType type)
    {
        setType(i % width, i / width, type);
    }

    public NodeType getType(int x, int y)
    {
        return nodes[x + y * width].type;
    }

    public NodeType getType(int i)
    {
        return getType(i % width, i / width);
    }

    private void clear()
    {
        for (int i = 0; i < nodes.length; i++)
            setType(i, NodeType.Air);

        open.clear();
        close.clear();
        result.clear();
    }

    private void reset()
    {
        for (int i = 0; i < nodes.length; i++)
        {
            NodeType type = getType(i);

            if (type == NodeType.Road || type == NodeType.Explored)
                setType(i, NodeType.Air);
        }

        for (final Node node : nodes)
            node.clearData();

        open.clear();
        close.clear();
        result.clear();

        lastTime = System.currentTimeMillis();
    }
}
