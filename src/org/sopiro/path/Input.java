package org.sopiro.path;

import java.awt.event.*;
import java.util.Arrays;

public class Input implements KeyListener, MouseListener, FocusListener
{
    public boolean[] keys;
    private boolean[] _keys;
    public boolean[] mouses;
    private boolean[] _mouses;

    public Input()
    {
        keys = new boolean[65535];
        _keys = new boolean[65535];
        mouses = new boolean[65535];
        _mouses = new boolean[65535];
    }

    public void update()
    {
        _keys = Arrays.copyOf(keys, keys.length);
        _mouses = Arrays.copyOf(mouses, mouses.length);
    }

    public boolean isKeyDown(int key)
    {
        return keys[key];
    }

    public boolean isButtonDown(int button)
    {
        return _mouses[button];
    }

    public boolean isKeyPressed(int key)
    {
        return !_keys[key] && keys[key];
    }

    public boolean isButtonPressed(int button)
    {
        return !_mouses[button] && mouses[button];
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