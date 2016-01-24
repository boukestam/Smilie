package speech;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

public class Smiley {
	
	private int x, y, width, height;
	
	private static final Color FACE_FILL_COLOR = new Color(255, 255, 0);
	private static final Color FACE_OUTLINE_COLOR = new Color(0, 0, 0);
	
	private static final Color EYE_FILL_COLOR = new Color(0, 0, 0);
	
	private static final float EYE_X_OFFSET = 0.3f;
	private static final float EYE_Y_OFFSET = 0.2f;
	private static final float EYE_WIDTH = 0.1f;
	private static final float EYE_HEIGHT = 0.3f;
	
	private static final float MOUTH_WIDTH = 0.7f;
	private static final float MOUTH_Y_OFFSET = 0.4f;
	
	private static final Color MOUTH_FILL_COLOR = new Color(255, 255, 255);
	private static final Color MOUTH_OUTLINE_COLOR = new Color(0, 0, 0);
	
	private float mouthOpen = 0.0f;
	
	public Smiley(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void draw(Graphics2D g){
		//Draw face
		g.setColor(FACE_FILL_COLOR);
		g.fillOval(x, y, width, height);
		
		g.setColor(FACE_OUTLINE_COLOR);
		g.drawOval(x, y, width, height);
		
		//Draw eyes
		g.setColor(EYE_FILL_COLOR);
		g.fillOval(
				x + (int)(EYE_X_OFFSET * width), 
				y + (int)(EYE_Y_OFFSET * height), 
				(int)(width * EYE_WIDTH), 
				(int)(height * EYE_HEIGHT)
		);
		
		g.setColor(EYE_FILL_COLOR);
		g.fillOval(
				x + width - (int)(EYE_X_OFFSET * width) - (int)(width * EYE_WIDTH), 
				y + (int)(EYE_Y_OFFSET * height), 
				(int)(width * EYE_WIDTH), 
				(int)(height * EYE_HEIGHT)
		);
		
		//Draw mouth
		Path2D path = new Path2D.Double();

		float mouthWidth = width * MOUTH_WIDTH;
		float mouthX = x + ((width - mouthWidth) / 2);
		
		float mouthY = y + height - (MOUTH_Y_OFFSET * height);
		float mouthSmileY = mouthY + (height * mouthOpen);
		
		path.moveTo(mouthX, mouthY);
		path.curveTo(
				mouthX + (mouthWidth * (1f / 3f)),
				mouthSmileY,
				mouthX + (mouthWidth * (2f / 3f)),
				mouthSmileY,
				mouthX + mouthWidth,
				mouthY
		);
		
		g.setColor(MOUTH_FILL_COLOR);
		g.fill(path);
		
		g.setColor(MOUTH_OUTLINE_COLOR);
		g.draw(path);
		
		g.drawLine((int)mouthX, (int)mouthY, (int)mouthX + (int)mouthWidth, (int)mouthY);
	}
	
	public void openMouth(){
		mouthOpen = 0.3f;
	}
	
	public void halfOpenMouth(){
		mouthOpen = 0.15f;
	}
	
	public void closeMouth(){
		mouthOpen = 0.0f;
	}
}
