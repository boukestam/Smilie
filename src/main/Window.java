package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import speech.Smiley;

public class Window implements Runnable{
	
	private JFrame frame;
	private Smiley smiley;
	
	public static final int FRAME_WIDTH = 100, FRAME_HEIGHT = 100;

	public Window(Smiley smiley){
		frame = new JFrame();
        
		frame.setTitle("Smilie");
    	frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
    	frame.setAlwaysOnTop(true);
    	frame.setUndecorated(true);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setShape(new Ellipse2D.Double(0, 0, FRAME_WIDTH, FRAME_HEIGHT));
    	
        frame.setVisible(true);
        
        this.smiley = smiley;
        
        new Thread(this).start();
	}
	
	public void run(){
		long lastDraw = 0;
		
		while(true){
			long now = System.currentTimeMillis();
			
			if(now - lastDraw > 10){
				draw();
				lastDraw = now;
			}
			
			try{
				Thread.sleep(1);
			}catch(Exception e){
				
			}
		}
	}
	
	public void draw(){
		Point mousePosition = MouseInfo.getPointerInfo().getLocation();
		
		Point framePosition = frame.getLocationOnScreen();
		Point frameCenterPosition = frame.getLocationOnScreen();
		frameCenterPosition.x += frame.getWidth() / 2;
		frameCenterPosition.y += frame.getHeight() / 2;
		
		double distance = Math.sqrt(
				Math.pow(mousePosition.x - frameCenterPosition.x, 2) + 
				Math.pow(mousePosition.y - frameCenterPosition.y, 2));
		
		double angle = Math.atan2(mousePosition.y - frameCenterPosition.y, mousePosition.x - frameCenterPosition.x);
		
		if(distance > 600){
			double speed = 20;
			
			frame.setLocation(framePosition.x + (int)(Math.cos(angle) * speed), framePosition.y + (int)(Math.sin(angle) * speed));
		}else if(distance < 200){
			double speed = 20;
			
			frame.setLocation(framePosition.x - (int)(Math.cos(angle) * speed), framePosition.y - (int)(Math.sin(angle) * speed));
		}
		
		BufferStrategy bs = frame.getBufferStrategy();
		if (bs == null) {
			frame.createBufferStrategy(3);
			frame.requestFocus();
			return;
		}
		
		Graphics g=bs.getDrawGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
		
		smiley.draw((Graphics2D)g);
		
		g.dispose();
		bs.show();
	}
}
