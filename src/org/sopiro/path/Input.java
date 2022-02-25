package org.sopiro.path;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

public class Input implements KeyListener, MouseListener, FocusListener
{
    public boolean keys[];
    private boolean pkeys[];
    public boolean mouses[];
    private boolean pmouses[];

    public Input()
    {
        keys = new boolean[65535];
        pkeys = new boolean[65535];
        mouses = new boolean[65535];
        pmouses = new boolean[65535];
    }

    public void update()
    {
        pkeys = Arrays.copyOf(keys, keys.length);
        pmouses = Arrays.copyOf(mouses, mouses.length);
    }

    public boolean whenKeyPressed(int key)
    {
        return keys[key];
    }

    public boolean whenButtonPressed(int button)
    {
        return pmouses[button];
    }

    public boolean whenKeyOncePressed(int key)
    {
        return !pkeys[key] && keys[key];
    }

    public boolean whenButtonOncePressed(int button)
    {
        return !pmouses[button] && mouses[button];
    }

    public void mouseClicked(MouseEvent e)
    {

    }

    public void mousePressed(MouseEvent e)
    {
        mouses[e.getButton()] = true;
    }

    public void mouseReleased(MouseEvent e)
    {
        mouses[e.getButton()] = false;
    }

    public void mouseEntered(MouseEvent e)
    {

    }

    public void mouseExited(MouseEvent e)
    {
        for (int i = 0; i < 256; i++)
            mouses[i] = false;
    }

    public void keyTyped(KeyEvent e)
    {

    }

    public void keyPressed(KeyEvent e)
    {
        keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e)
    {
        keys[e.getKeyCode()] = false;
    }

    public void focusGained(FocusEvent e)
    {

    }

    public void focusLost(FocusEvent e)
    {
        for (int i = 0; i < 256; i++)
            keys[i] = false;
    }
}