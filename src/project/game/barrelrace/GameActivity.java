/**
 * Author	: Arun Kumar Konduru Chandra & Chitturi Varsha
 * NetID	: axk138230 & vxc130730
 * Date 	: 04/04/2014
 * Class	: CS6301.013
 * Purpose	: Assignment 4 for UI Design course.
 * Description: This is the main activity. It contains the basic options for this game.
 */

package project.game.barrelrace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.SurfaceView;
import android.widget.Toast;

//GameActivity handles the sensor listener and movement of the horse and collisions. 
public class GameActivity extends Activity implements SensorEventListener{
	
	float XPost,XAcc,YPost,YAcc,xmax,ymax;
	/*XPost & YPost are X and Y positions of horse.
	XAcc & YAcc are accelerometer values for X and Y respectively.
	xmax & ymax are the maximum screen width along X and Y axis respectively.*/
	Sensor mSensor;
	SensorManager mSensorManager;
	Boolean gameStarted=false,gameFinished=false,xcrash=false,ycrash=false;;
	Boolean barrelCrossed=false,finishTimeSet=false,collision=false;
	int crashCount=0, i=0,a=0,b=0,c=0,d=0,e=0;
	long startTime=0,elapsedTime,finishTime;
	Point[] points=new Point[1000];
	float[] x1 = new float[60];
	float[] x2 = new float[30];
	float[] y1 = new float[30];
	float[] y2 = new float[30];
	float[] y3 = new float[30];
	long score=0;
	Boolean fileCreated=false;
	File file;
    File external = Environment.getExternalStorageDirectory();
    String sdcardPath = external.getPath();
	
    //Accelerometer Sensor is initialized.
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
		Display display = getWindowManager().getDefaultDisplay();
		xmax = display.getWidth();
		ymax = display.getHeight();
		XPost = 7*xmax/16;
		YPost = 25*ymax/32;
}
	
	//Accelerometer sensor is registered.
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	//Accelerometer sensor is unregistered so that the sensor stops when game is inactive.
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	
	//Accelerometer sensor values are used to set the position of the horse.
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
		{
			XAcc=event.values[0];
			YAcc=-event.values[1];
			setHorsePosition();
		}
	}
	
	/*Author		: 	Arun Kumar Konduru Chandra
	Description		:	Horse movement is restricted within the fence area here.
	*/
	public void setHorsePosition()
	{	
		/*If the horse moves to bottom of the fence then count the crash 
		and restrict the horse within the fence.*/
		if(YPost-YAcc>25*ymax/32)
		{
			YPost=25*ymax/32;
			if(ycrash==false&&gameStarted&&!gameFinished)
				crashCount++;
			if(gameStarted)
				ycrash=true;
		}
		/*If the horse moves to top of the screen then count the crash 
		 * and restrict the horse within the fence.*/
		else if(YPost-YAcc<ymax/10&&!gameFinished)
		{
			YPost=ymax/10;
			if(ycrash==false)
				crashCount++;
			ycrash=true;
		}
		//If the horse is within the fence move horse according to the sensor values returned.
		else
		{
			YPost-=YAcc;
			ycrash=false;
		}
		/*If the horse moves to left of the fence then count the crash 
		and restrict the horse within the fence.*/
		if(XPost-XAcc<xmax/96)
		{
			XPost=xmax/96;
			if(xcrash==false)
				crashCount++;
			xcrash=true;
		}
		/*If the horse moves to right of the fence then count the crash 
		and restrict the horse within the fence.*/
		else if(XPost-XAcc>xmax-7*xmax/48)
		{
			XPost=xmax-7*xmax/48;
			if(xcrash==false)
				crashCount++;
			xcrash =true;
		}
		//If the horse is within the fence move horse according to the sensor values returned.
		else
		{
			XPost-=XAcc; 
			xcrash = false;
		}
		//Method to check if horse has left the stable or returned to the stable.
		horseMovement();
		//Method to check if the horse has collided with the barrel.
		hasCollidedWithBarrel();
		//Method to check if horse has gone around the barrel.
		hasHorseGoneAroundBarrels();
	}
	
	/*Author		: 	Arun Kumar Konduru Chandra
	Description		:	This method is used to start and end the game. If horse move out of the stable then 
	 * the game starts. If the horse moves into the sable again the game stops.*/
	public void horseMovement()
	{
		//Start game if horse moves out of the stable.
		if(YPost<3*ymax/4||XPost<7*xmax/24||XPost>9*xmax/16)
		{
			gameStarted=true;
			if(startTime==0)
				startTime = System.currentTimeMillis();
		}
		else
		{
			//Stop game if horse returns to the stable.
			if(gameStarted==true&&(YPost>3*ymax/4||XPost>7*xmax/24||XPost<9*xmax/16))
			{
				gameFinished=true;
			}		
		}
		//Used to store the all the points taken by the horse to move around the barrels.
		while(gameStarted==true&&gameFinished==false&&i<999)
		{
			points[i] = new Point((int)XPost,(int)YPost);
			i++;
		}
		//If game finishes then calculate the finish time and score.
		if(gameFinished == true)
		{
			XPost = 7*xmax/16;
			YPost = 62*ymax/80;
			if(finishTimeSet==false)
			{
				finishTime = System.currentTimeMillis()-startTime;
				finishTimeSet=true;
				score=1000-TimeUnit.MILLISECONDS.toSeconds(finishTime)*10-crashCount*10;
			}
		}
	}

	/*Author		: 	Chitturi Varsha
	Description		:	This method is used to identify if the horse has collided with barrels.*/
	public void hasCollidedWithBarrel()
	{
		if(gameStarted&&horseCollision(XPost-xmax/14,YPost-xmax/14,xmax/14,xmax/4,3*ymax/5,xmax/18)&&!collision)
		{
			gameFinished=true;
			collision=true;
		}
		if(gameStarted&&horseCollision(XPost-xmax/14,YPost-xmax/14,xmax/14,3*xmax/4,3*ymax/5,xmax/18)&&!collision)
		{
			gameFinished=true;
			collision=true;
		}
		if(gameStarted&&horseCollision(XPost-xmax/14,YPost-xmax/14,xmax/14,xmax/2,ymax/3,xmax/18)&&!collision)
		{
			gameFinished=true;
			collision=true;
		}
	}
	
	/*Author		: 	Chitturi Varsha
	Description		:	This method is used to calculate if the distance between two circles is less 
	than the sum of radius of the circles to determine whether collision has occurred. 
	If the distance between the centers of the horse and barrel is less than the sum of radius
	of the barrel and horse then collision has occurred.*/
	public boolean horseCollision(float x1,float y1,float r1,float x2,float y2,float r2)
	{
		float xDif = x1 - x2;
		float yDif = y1 - y2;
		float distanceSquared = xDif * xDif + yDif * yDif;
		if( distanceSquared < (r1 + r2) * (r1 + r2))
			return true;
		else
			return false;
	}
	
	/*Author		: 	Arun Kumar Konduru Chandra
	Description		:	This method is used to identify if the horse has gone around all the three barrels.
	 * Concept: I'm saving all the points taken by the horse along the vertical and 
	 * horizontal axis of the center of the barrels. I use this axis and check if the horse 
	 * has passed on the left,right, top and bottom of the barrel. If the horse had not gone 
	 * around a barrel then the values of the top or bottom or left or right may not be 
	 * true and a flag is set.*/
	public void hasHorseGoneAroundBarrels()
	{
		//Save the y values if it has crossed the vertical axis of center of barrel1.
		if((XPost<xmax/4-xmax/14&&XPost+XAcc>xmax/4-xmax/14)||
				(XPost>xmax/4-xmax/14&&XPost+XAcc<xmax/4-xmax/14))
		{
			if(a<30)
			{
				y1[a]=YPost;
				a++;
			}
		}
		//Save the y values if it has crossed the vertical axis of center of barrel2.
		if((XPost<3*xmax/4-xmax/14&&XPost+XAcc>3*xmax/4-xmax/14)
				||(XPost>3*xmax/4-xmax/14&&XPost+XAcc<3*xmax/4-xmax/14))
		{
			if(b<30)
			{
				y2[b]=YPost;
				b++;
			}
		}
		//Save the y values if it has crossed the vertical axis of center of barrel3.
		if((XPost<xmax/2-xmax/14&&XPost+XAcc>xmax/2-xmax/14)
				||(XPost>xmax/2-xmax/14&&XPost+XAcc<xmax/2-xmax/14))
		{
			if(c<30)
			{
				y3[c]=YPost;
				c++;
			}
		}
		/*Save the x values if it has crossed the horizontal axis of center 
		of barrel1 and barrel2 because both have same axis.*/
		if((YPost<3*ymax/5-xmax/14&&YPost-YAcc>3*ymax/5-xmax/14)
				||(YPost>3*ymax/5-xmax/14&&YPost-YAcc<3*ymax/5-xmax/14))
		{
			if(d<60)
			{
				x1[d]=XPost;
				d++;
			}
		}
		//Save the x values if it has crossed the horizontal axis of center of barrel2.
		if((YPost<ymax/3-xmax/14&&YPost-YAcc>ymax/3-xmax/14)
				||(YPost>ymax/3-xmax/14&&YPost-YAcc<ymax/3-xmax/14))
		{
			if(e<30)
			{
				x2[e]=XPost;
				e++;
			}
		}
		if(gameFinished)
			checkBarrelRotation();
	}
	
	/*Author		: 	Arun Kumar Konduru Chandra
	Description		:	This method checks all the points taken by horse to find if they have passed top,
	bottom,left and right of the barrel.*/
	public void checkBarrelRotation()
	{
		boolean b1v=false,b2v=false,b3v=false,b1h=false,b2h=false,b3h=false;
		boolean barrel1=false,barrel2=false,barrel3=false;
		boolean b1t=false,b1b=false,b2t=false,b2b=false,b3t=false,b3b=false;
		boolean b1l=false,b1r=false,b2l=false,b2r=false,b3l=false,b3r=false;
		//Check for top and bottom of barrel1.
		for(int i=0;i<y1.length;i++)
		{
			if(y1[i]<3*ymax/5-xmax/18&&y1[i]>ymax/3+xmax/18&&!b1t)
				b1t=true;
			if(y1[i]>3*ymax/5+xmax/18&&!b1b)
				b1b=true;
			if(b1t&&b1b)
				b1v=true;
		}
		//Check for top and bottom of barrel2.
		for(int i=0;i<y2.length;i++)
		{
			if(y2[i]<3*ymax/5-xmax/18&&y2[i]<ymax/3+xmax/18&&!b2t)
				b2t=true;
			if(y2[i]>3*ymax/5+xmax/18&&!b2b)
				b2b=true;
			if(b2t&&b2b)
				b2v=true;
		}
		//Check for top and bottom of barrel3.
		for(int i=0;i<y3.length;i++)
		{
			if(y3[i]<ymax/3-xmax/18&&!b3t)
				b3t=true;
			if(y3[i]>ymax/3+xmax/18-xmax/18&&!b3b)
				b3b=true;
			if(b3t&&b3b)
				b3v=true;
		}
		//Check for left and right of barrel1.
		for(int i=0;i<x1.length;i++)
		{
			if(x1[i]<xmax/4-xmax/18&&!b1l)
				b1l=true;
			if(x1[i]>xmax/4+xmax/18&&!b1r)
				b1r=true;
			if(b1l&&b1r)
				b1h=true;
		}
		//Check for left and right of barrel2.
		for(int i=0;i<x1.length;i++)
		{
			if(x1[i]<3*xmax/4-xmax/18&&!b2l)
				b2l=true;
			if(x1[i]>3*xmax/4+xmax/18&&!b2r)
				b2r=true;
			if(b2l&&b2r)
				b2h=true;
		}
		//Check for left and right of barrel3.
		for(int i=0;i<x2.length;i++)
		{
			if(x2[i]<xmax/2-xmax/18&&!b3l)
				b3l=true;
			if(x2[i]>xmax/2+xmax/18&&!b3r)
				b3r=true;
			if(b3l&&b3r)
				b3h=true;
		}
		if(b1v&&b1h)
			barrel1=true;
		if(b2v&&b2h)
			barrel2=true;
		if(b3v&&b3h)
			barrel3=true;
		if(barrel1&&barrel2&&barrel3)
			barrelCrossed=true;
		else 
			barrelCrossed= false;
	
	}
	
	/*Author		: 	Chitturi Varsha
	Description		:	This method is used to create a new file to save scores.*/
	public void createNewFile()
	{
		try
	    {
			file = new File(sdcardPath + "/Documents/scores.txt");
			if(!file.exists())
				file.createNewFile();
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file,true)));
				out.println(Long.toString(score));
				out.close();
	    }
	    catch(Exception e)
	    {
	    	Toast toast = Toast.makeText(getApplicationContext(),
	    			"Unable to create text file.",Toast.LENGTH_SHORT);
	    	toast.show(); 	    	
	    }
	}
   
	/*Author		: 	Chitturi Varsha
	Description		:	This method gets the scores from the file and displays it in a dialog box.*/
    public void highScore()
    {
    	file = new File(sdcardPath + "/Documents/scores.txt");
		ArrayList<String> scores = new ArrayList<String>();
		if(file.length()!=0)
		{
			try
		    {
				FileInputStream fstream = new FileInputStream(file);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				while((line=br.readLine())!=null)
				{
					scores.add(line);
				}
				Collections.sort(scores);
				Collections.reverse(scores);
				String[] items = (String[]) scores.toArray(new String[scores.size()]);
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("Scores");
				dialog.setItems(items,null);
				dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface d, int whichButton) {
					    //Cancelled.
					  }
					});
				dialog.show();
				br.close();
				in.close();
				fstream.close();
		    }
		    catch(Exception e)
		    {
		    	Toast toast = Toast.makeText(getApplicationContext(),
		    			"Unable to display scores.",Toast.LENGTH_SHORT);
		    	toast.show(); 	    	
		    }
		}
		else
		{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Scores");
			dialog.setMessage("No High Score Yet!");
			dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface d, int whichButton) {
				    //Cancelled.
				  }
				});
			dialog.show();
		}
    }
	
    //Auto Generated method. Did not do anything in this method.
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	//Game Activity implements the view of the game.
	class Game extends SurfaceView {

		int radius;
		Bitmap brl,hrs,gnd;
		Bitmap barrel,horse,ground;
		int countdown=10;
		
		public Game(Context context) {
			super(context);			
	        this.setWillNotDraw(false);
		}
		
		/*Author		: 	Arun Kumar Konduru Chandra
		Description		:	onDraw is used to draw the graphics of the game.*/
		@SuppressLint("DrawAllocation")
		@Override
	    public void onDraw(Canvas canvas) {
	        super.onDraw(canvas);
	        radius = 25;
	        Paint timePaint = new Paint();
	        timePaint.setColor(Color.WHITE);
	        timePaint.setTextSize(25);
	        Paint textPaint = new Paint();
	        textPaint.setColor(Color.BLACK);
	        textPaint.setTextSize(25);
	        Paint dotPaint = new Paint();
	        dotPaint.setColor(Color.BLACK);
	        gnd = BitmapFactory.decodeResource(getResources(), R.drawable.background);
	        ground = Bitmap.createScaledBitmap(gnd, (int)xmax, 
	        		(int)(ymax-23*ymax/100), false); 
	        canvas.drawBitmap(ground,0,3*ymax/32,null); 
			brl = BitmapFactory.decodeResource(getResources(), R.drawable.barrels);
		    //Variable that represents the barrel bitmap image.
			barrel = Bitmap.createScaledBitmap(brl, (int)xmax/9, (int)xmax/9, false);
		    canvas.drawBitmap(barrel, xmax/2-xmax/18, ymax/3-xmax/18, null);
		    canvas.drawBitmap(barrel, xmax/4-xmax/18, 3*ymax/5-xmax/18, null);
		    canvas.drawBitmap(barrel, 3*xmax/4-xmax/18, 3*ymax/5-xmax/18, null);
			hrs = BitmapFactory.decodeResource(getResources(), R.drawable.horse);
			//Variable that represents the horse bitmap image.
			horse = Bitmap.createScaledBitmap(hrs, (int)xmax/7, (int)xmax/7, false);
			canvas.drawBitmap(horse, XPost, YPost,null);
			if(gameStarted==true && gameFinished==false)
			{
				elapsedTime=System.currentTimeMillis() - startTime;
				canvas.drawText("Time 		~ "
				+DateFormat.format("mm:ss", elapsedTime),xmax/24,7*ymax/180, timePaint);
				canvas.drawText("CrashCount ~ "+crashCount,xmax/24,3*ymax/40, timePaint);
				if(xcrash==true||ycrash==true)
					canvas.drawText("Crashed onto fence: -5 Sec",xmax/5,ymax/2, textPaint);
				for(int j=0;j<i;j++)
				{
					if(points[j].x!=0&&points[j].y!=0)
						canvas.drawPoint(points[j].x,points[j].y, dotPaint);
				}
			}
			//If player completes the game correctly.
			if(gameFinished==true&&barrelCrossed)
			{
				canvas.drawText("Time 		~ "
			+DateFormat.format("mm:ss", finishTime),xmax/24,7*ymax/180, timePaint);
				canvas.drawText("CrashCount ~ "+crashCount,xmax/24,3*ymax/40, timePaint);
				textPaint.setTextSize(50);
				canvas.drawText("Track Completed",xmax/7,4*ymax/9, textPaint);
				if(!fileCreated)
				{
					createNewFile();
					highScore();
					fileCreated=true;
				}
			}
			//If the player crashes into the barrel.
			else if(gameFinished==true&&!barrelCrossed)
			{
				canvas.drawText("Time 		~ "
			+DateFormat.format("mm:ss", finishTime),xmax/24,7*ymax/180, timePaint);
				canvas.drawText("CrashCount ~ "+crashCount,xmax/24,3*ymax/40, timePaint);
				textPaint.setTextSize(50);
				canvas.drawText("Game Over",2*xmax/7,5*ymax/11, textPaint);
				textPaint.setTextSize(30);
				canvas.drawText("You did not go around all barrels."
						,xmax/14,ymax/2, textPaint);
				if(collision)
					canvas.drawText("Crashed! Start Again.",xmax/5,2*ymax/7, textPaint);
			}
			invalidate(); 
	    }
	}

	/*Author		: 	Chitturi Varsha
	Description		:	When playing game if back button is pressed the BarrelRaceActivity is started.*/
	public void onBackPressed()
	{
		Intent intent = new Intent(this, BarrelRaceActivity.class);
		startActivity(intent);
	}
	
}