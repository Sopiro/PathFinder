package org.sopiro.path;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

/**
 * @author Sopiro
 *
 */
public class Main extends Canvas implements Runnable
{
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH * 9 / 16;
	public static final int SCALE = 8;
	public static final String TITLE = "Advanced Path Finding";

	public boolean run = false;

	public BufferedImage image;
	public int[] pixels;

	public Input input;

	public PathFinder pathFinder;

	public Main()
	{
		Dimension d = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		setMaximumSize(d);
		setMinimumSize(d);
		setPreferredSize(d);

		input = new Input();

		addMouseListener(input);
		addKeyListener(input);
		addFocusListener(input);
	}

	public void init()
	{
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) (image.getRaster().getDataBuffer())).getData();

		pathFinder = new PathFinder(WIDTH, HEIGHT, input);
	}

	public void start()
	{
		if (run)
			return;

		run = true;

		new Thread(this).start();
	}

	public void run()
	{
		final double frameCut = 1000000000.0 / 60.0;
		long lastTime = System.nanoTime();
		long unprocessedTime = 0;

		long frameCounter = System.currentTimeMillis();

		int frames = 0;
		int updates = 0;

		while (run)
		{
			long currentTime = System.nanoTime();
			long passedTime = currentTime - lastTime;
			unprocessedTime += passedTime;
			lastTime = currentTime;

			if (unprocessedTime >= frameCut)
			{
				unprocessedTime -= frameCut;
				update();
				updates++;
			}

			render();
			frames++;

			if (System.currentTimeMillis() - frameCounter >= 1000)
			{
//				System.out.println("frame : " + frames + ", update : " + updates);
				frames = 0;
				updates = 0;
				frameCounter += 1000;
			}
		}
	}

	public void update()
	{
		pathFinder.update(getMousePosition());
		input.update();
	}

	public void render()
	{
		BufferStrategy bs = getBufferStrategy();

		if (bs == null)
		{
			createBufferStrategy(3);
			return;
		}

		for (int i = 0; i < pixels.length; i++)
			pixels[i] = 0;

		for (int i = 0; i < pixels.length; i++)
		{
			pixels[i] = 0x808080;

			if (pathFinder.getType(i) == Node.Air)
				pixels[i] = 0xc0c0c0;
			else if (pathFinder.getType(i) == Node.Block)
				pixels[i] = 0x000000;
			else if (pathFinder.getType(i) == Node.Start)
				pixels[i] = 0x0000ff;
			else if (pathFinder.getType(i) == Node.End)
				pixels[i] = 0xff0000;
			else if (pathFinder.getType(i) == Node.Road)
				pixels[i] = 0xffff00;
			else if (pathFinder.getType(i) == Node.Explored)
				pixels[i] = 0x505050;
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g.dispose();
		bs.show();
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame(TITLE);
		f.setResizable(false);
		Main m = new Main();
		f.add(m);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setAlwaysOnTop(true);
		f.setVisible(true);

		m.init();
		m.start();
	}
}