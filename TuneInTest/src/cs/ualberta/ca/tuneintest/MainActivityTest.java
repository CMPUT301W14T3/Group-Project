package cs.ualberta.ca.tuneintest;

import java.util.WeakHashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import cs.ualberta.ca.tunein.MainActivity;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	MainActivity activity;
	EditText textInput;
	EditText textLong;
	EditText textLat;
	Instrumentation instrumentation;
	
	public MainActivityTest() {
		super(MainActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		instrumentation = getInstrumentation();
		activity = getActivity();

		textInput = ((EditText) activity.findViewById(cs.ualberta.ca.tunein.R.id.edit_username));
		textLong = ((EditText) activity.findViewById(cs.ualberta.ca.tunein.R.id.textViewInputChangeLong));
		textLat = ((EditText) activity.findViewById(cs.ualberta.ca.tunein.R.id.textViewInputChangeLat));
	}
	
	
	public void testdatesortbutton() {
		
		ActivityMonitor monitor = getInstrumentation().addMonitor(cs.ualberta.ca.tunein.TopicListActivity.class.getName(), null, false);
		
		Button button = (Button) activity.findViewById(cs.ualberta.ca.tunein.R.id.date_button);
		TouchUtils.clickView(this, button);
		
		cs.ualberta.ca.tunein.TopicListActivity secondActivity = (cs.ualberta.ca.tunein.TopicListActivity) monitor
		          .waitForActivityWithTimeout(5);
		assertNotNull(secondActivity);
		this.sendKeys(KeyEvent.KEYCODE_BACK);
	}
	
	public void testtopicbutton() {
		
		ActivityMonitor monitor = getInstrumentation().addMonitor(cs.ualberta.ca.tunein.TopicListActivity.class.getName(), null, false);
		
		Button button = (Button) activity.findViewById(cs.ualberta.ca.tunein.R.id.buttonTopicList);
		TouchUtils.clickView(this, button);
		
		cs.ualberta.ca.tunein.TopicListActivity secondActivity = (cs.ualberta.ca.tunein.TopicListActivity) monitor
		          .waitForActivityWithTimeout(5);
		assertNotNull(secondActivity);
		this.sendKeys(KeyEvent.KEYCODE_BACK);
	}
	
	public void testchangename()throws Throwable {
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
			
				// TODO Auto-generated method stub
				setname("Changed Name");
			}
		});
		
	}
	
	public void setname(String name) {
		assertNotNull(activity.findViewById(cs.ualberta.ca.tunein.R.id.name_button));
		textInput.setText(name);
		((Button) activity.findViewById(cs.ualberta.ca.tunein.R.id.name_button)).performClick();	
	}
	
	public void testViewProfile() {
	    ActivityMonitor monitor = getInstrumentation().addMonitor(cs.ualberta.ca.tunein.ProfileViewActivity.class.getName(), null, false);
		
		Button button = (Button) activity.findViewById(cs.ualberta.ca.tunein.R.id.buttonViewProfile);
		TouchUtils.clickView(this, button);
		
		cs.ualberta.ca.tunein.ProfileViewActivity secondActivity = (cs.ualberta.ca.tunein.ProfileViewActivity) monitor
		          .waitForActivityWithTimeout(5);
		assertNotNull(secondActivity);
		this.sendKeys(KeyEvent.KEYCODE_BACK);
	}
	
	public void testLoadGPS() {
		ImageButton button = (ImageButton) activity.findViewById(cs.ualberta.ca.tunein.R.id.imageButtonRefreshLoc);
		assertNotNull("Button should not be null", button);
		TouchUtils.clickView(this, button);
	}
	
	public void testSetLocation() {
		Button button = (Button) activity.findViewById(cs.ualberta.ca.tunein.R.id.otherLocation_button);
		assertNotNull("Button should not be null", button);
		TouchUtils.clickView(this, button);
	}

}
