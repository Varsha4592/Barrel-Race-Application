/**
 * Author	: Arun Kumar Konduru Chandra & Chitturi Varsha
 * NetID	: axk138230 & vxc130730
 * Date 	: 04/01/2014
 * Class	: CS6301.013
 * Purpose	: Assignment 4 for UI Design course.
 * Description: This is the main activity. It contains the basic options for this game.
 */

package project.game.barrelrace;

import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

//BarrelRaceActivity contains menu options and menu handlers.
public class BarrelRaceActivity extends GameActivity {
	
	private WakeLock mWakeLock;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_page);
		menu_page();
		PowerManager mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
		//To keep the screen lit when playing the game.
		mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"");
	}
	
	//We acquire the lock when game is resumed to keep the screen lit.
	@Override
	protected void onResume() {
		super.onResume();
	    mWakeLock.acquire();
	}
	 
	//We release the lock when the game is paused.
	@Override
	protected void onPause() {
		super.onPause();
	    mWakeLock.release();
	}   

	/*Author		: 	Arun Kumar Konduru Chandra
	 * Description	:	Menu_Page method contains menu buttons for this game.
	 */
	public void menu_page()
	{
		Button btn_newGame = (Button)findViewById(R.id.btn_newGame);
		Button btn_options = (Button)findViewById(R.id.btn_options);
		Button bt_help = (Button)findViewById(R.id.btn_help);
		Button btn_highScore = (Button)findViewById(R.id.btn_highScore);
		Button btn_credits = (Button)findViewById(R.id.btn_aboutUs);
		Button btn_exit = (Button)findViewById(R.id.btn_exit);
		
		//BarrelRaceActivity is called when the new game is pressed.
		btn_newGame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setContentView(new Game(BarrelRaceActivity.this));
			}
		});
		
		//Options to set or mute sound etc..
		btn_options.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Not yet implemented.
			}
		});
		
		//It displays the game instructions.
		bt_help.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setContentView(R.layout.reserve);
				Button ok = (Button)findViewById(R.id.btnOk);
				TextView text = (TextView)findViewById(R.id.text);
				text.setText(R.string.how_to_play);
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						setContentView(R.layout.main_page);
						menu_page();
					}
				});
			
			}
		});
		
		//This method displays the high scores.
		btn_highScore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				highScore();
			}
		});
		
		//This method displays information about the author.
		btn_credits.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setContentView(R.layout.reserve);
				Button ok = (Button)findViewById(R.id.btnOk);
				TextView text = (TextView)findViewById(R.id.text);
				text.setText(R.string.about_us);
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						setContentView(R.layout.main_page);
						menu_page();
					}
				});
			}
		});
		
		//This button is used to exit the application.
		btn_exit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    Intent intent = new Intent(Intent.ACTION_MAIN);
			    intent.addCategory(Intent.CATEGORY_HOME);
			    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    startActivity(intent);
			}
		});		
	}	
}
