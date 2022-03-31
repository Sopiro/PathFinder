package org.sopiro.path;

enum NodeType
{
    Air,
    Wall,
    Start,
    End,
    Road,
    Explored,
}

public class Node
{
    public int x, y;
    public double f, g, h;
    public NodeType type;
    public Node parent;

    public Node(int x, int y)
    {
        this(x, y, NodeType.Air);
    }

    public Node(int x, int y, NodeType type)
    {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void clearData()
    {
        f = 0;
        g = 0;
        h = 0;
    }

    public String toString()
    {
        return "(" + x + ", " + y + ", type:" + type + "), (f:" + f + ", g:" + g + ", h:" + h + ")";
    }
}
