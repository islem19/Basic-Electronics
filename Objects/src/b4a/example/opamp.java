package b4a.example;

import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class opamp extends Activity implements B4AActivity{
	public static opamp mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.opamp");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (opamp).");
				p.finish();
			}
		}
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.opamp");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.opamp", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (opamp) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (opamp) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
		return true;
	}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return opamp.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
		this.setIntent(intent);
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (opamp) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (opamp) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext3 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label4 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label7 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label8 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label6 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label9 = null;
public static int _i = 0;
public static int _j = 0;
public anywheresoftware.b4a.objects.ButtonWrapper _button2 = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinner1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext4 = null;
public b4a.example.dateutils _dateutils = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public b4a.example.main _main = null;
public b4a.example.resistor _resistor = null;
public b4a.example.inductor _inductor = null;
public b4a.example.opamp1 _opamp1 = null;
public b4a.example.opampinfo _opampinfo = null;
public b4a.example.amplifier _amplifier = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 39;BA.debugLine="Activity.LoadLayout(\"opamp\")";
mostCurrent._activity.LoadLayout("opamp",mostCurrent.activityBA);
 //BA.debugLineNum = 40;BA.debugLine="EditText1.SingleLine = True";
mostCurrent._edittext1.setSingleLine(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 41;BA.debugLine="EditText2.SingleLine = True";
mostCurrent._edittext2.setSingleLine(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 42;BA.debugLine="EditText3.SingleLine = True";
mostCurrent._edittext3.setSingleLine(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 43;BA.debugLine="EditText4.SingleLine = True";
mostCurrent._edittext4.setSingleLine(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 46;BA.debugLine="Spinner1.AddAll(Array As String (\"Inverting Amplifier\",\"Non-Inverting Amplifier\"))";
mostCurrent._spinner1.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"Inverting Amplifier","Non-Inverting Amplifier"}));
 //BA.debugLineNum = 47;BA.debugLine="Spinner1.Color = Colors.White";
mostCurrent._spinner1.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 49;BA.debugLine="Activity.AddMenuItem(\"About Opamps\",\"mnpage1\")";
mostCurrent._activity.AddMenuItem("About Opamps","mnpage1");
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 59;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 61;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 56;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 58;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 63;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 64;BA.debugLine="EditText1.InputType = EditText1.INPUT_TYPE_DECIMAL_NUMBERS";
mostCurrent._edittext1.setInputType(mostCurrent._edittext1.INPUT_TYPE_DECIMAL_NUMBERS);
 //BA.debugLineNum = 65;BA.debugLine="EditText2.InputType = EditText2.INPUT_TYPE_DECIMAL_NUMBERS";
mostCurrent._edittext2.setInputType(mostCurrent._edittext2.INPUT_TYPE_DECIMAL_NUMBERS);
 //BA.debugLineNum = 66;BA.debugLine="EditText3.InputType = EditText3.INPUT_TYPE_DECIMAL_NUMBERS";
mostCurrent._edittext3.setInputType(mostCurrent._edittext3.INPUT_TYPE_DECIMAL_NUMBERS);
 //BA.debugLineNum = 68;BA.debugLine="If EditText1.Text =\"\" Then EditText1.Text = 0";
if ((mostCurrent._edittext1.getText()).equals("")) { 
mostCurrent._edittext1.setText((Object)(0));};
 //BA.debugLineNum = 69;BA.debugLine="If EditText2.Text =\"\" Then EditText2.Text = 0";
if ((mostCurrent._edittext2.getText()).equals("")) { 
mostCurrent._edittext2.setText((Object)(0));};
 //BA.debugLineNum = 70;BA.debugLine="If EditText3.Text =\"\" Then EditText3.Text = 0";
if ((mostCurrent._edittext3.getText()).equals("")) { 
mostCurrent._edittext3.setText((Object)(0));};
 //BA.debugLineNum = 72;BA.debugLine="If i=1 OR i=-1 Then EditText4.Text = \"-\" & (EditText1.Text * EditText3.Text /(EditText2.Text*1000) )";
if (_i==1 || _i==-1) { 
mostCurrent._edittext4.setText((Object)("-"+BA.NumberToString(((double)(Double.parseDouble(mostCurrent._edittext1.getText()))*(double)(Double.parseDouble(mostCurrent._edittext3.getText()))/(double)((double)(Double.parseDouble(mostCurrent._edittext2.getText()))*1000)))));};
 //BA.debugLineNum = 73;BA.debugLine="If j=1 OR j=-1 Then EditText4.Text = \"-\" & (EditText1.Text *( EditText3.Text*1000) /EditText2.Text )";
if (_j==1 || _j==-1) { 
mostCurrent._edittext4.setText((Object)("-"+BA.NumberToString(((double)(Double.parseDouble(mostCurrent._edittext1.getText()))*((double)(Double.parseDouble(mostCurrent._edittext3.getText()))*1000)/(double)(double)(Double.parseDouble(mostCurrent._edittext2.getText()))))));};
 //BA.debugLineNum = 74;BA.debugLine="If (i=1 OR i=-1) AND (j=1 OR j=-1) Then EditText4.Text = \"-\" & (EditText1.Text * (EditText3.Text*1000) /(EditText2.Text*1000) )";
if ((_i==1 || _i==-1) && (_j==1 || _j==-1)) { 
mostCurrent._edittext4.setText((Object)("-"+BA.NumberToString(((double)(Double.parseDouble(mostCurrent._edittext1.getText()))*((double)(Double.parseDouble(mostCurrent._edittext3.getText()))*1000)/(double)((double)(Double.parseDouble(mostCurrent._edittext2.getText()))*1000)))));};
 //BA.debugLineNum = 75;BA.debugLine="If i<>1 AND j<>1 AND i<>-1 AND j<>-1 Then EditText4.Text = \"-\" & (EditText1.Text * EditText3.Text /EditText2.Text )";
if (_i!=1 && _j!=1 && _i!=-1 && _j!=-1) { 
mostCurrent._edittext4.setText((Object)("-"+BA.NumberToString(((double)(Double.parseDouble(mostCurrent._edittext1.getText()))*(double)(Double.parseDouble(mostCurrent._edittext3.getText()))/(double)(double)(Double.parseDouble(mostCurrent._edittext2.getText()))))));};
 //BA.debugLineNum = 76;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
 //BA.debugLineNum = 89;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 90;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 91;BA.debugLine="StartActivity(\"Main\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Main"));
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 18;BA.debugLine="Private Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Private EditText1 As EditText";
mostCurrent._edittext1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private EditText2 As EditText";
mostCurrent._edittext2 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private EditText3 As EditText";
mostCurrent._edittext3 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private ImageView1 As ImageView";
mostCurrent._imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private Label2 As Label";
mostCurrent._label2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private Label3 As Label";
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private Label4 As Label";
mostCurrent._label4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private Label7 As Label";
mostCurrent._label7 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private Label8 As Label";
mostCurrent._label8 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private Label6 As Label";
mostCurrent._label6 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private Label9 As Label";
mostCurrent._label9 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim i,j As Int=0";
_i = 0;
_j = (int) (0);
 //BA.debugLineNum = 32;BA.debugLine="Private Button2 As Button";
mostCurrent._button2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private Spinner1 As Spinner";
mostCurrent._spinner1 = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private EditText4 As EditText";
mostCurrent._edittext4 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _label7_click() throws Exception{
 //BA.debugLineNum = 77;BA.debugLine="Sub Label7_Click";
 //BA.debugLineNum = 78;BA.debugLine="i=i+1";
_i = (int) (_i+1);
 //BA.debugLineNum = 79;BA.debugLine="If i=0 Then Label7.Text = \"Ω\"";
if (_i==0) { 
mostCurrent._label7.setText((Object)("Ω"));};
 //BA.debugLineNum = 80;BA.debugLine="If i=1 Then Label7.Text = \"KΩ\"";
if (_i==1) { 
mostCurrent._label7.setText((Object)("KΩ"));};
 //BA.debugLineNum = 81;BA.debugLine="If i=2 Then i=-1";
if (_i==2) { 
_i = (int) (-1);};
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
public static String  _label8_click() throws Exception{
 //BA.debugLineNum = 83;BA.debugLine="Sub Label8_Click";
 //BA.debugLineNum = 84;BA.debugLine="j=j+1";
_j = (int) (_j+1);
 //BA.debugLineNum = 85;BA.debugLine="If j=0 Then Label8.Text = \"Ω\"";
if (_j==0) { 
mostCurrent._label8.setText((Object)("Ω"));};
 //BA.debugLineNum = 86;BA.debugLine="If j=1 Then Label8.Text = \"KΩ\"";
if (_j==1) { 
mostCurrent._label8.setText((Object)("KΩ"));};
 //BA.debugLineNum = 87;BA.debugLine="If j=2 Then j=-1";
if (_j==2) { 
_j = (int) (-1);};
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static String  _mnpage1_click() throws Exception{
 //BA.debugLineNum = 51;BA.debugLine="Sub mnpage1_Click";
 //BA.debugLineNum = 52;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 53;BA.debugLine="StartActivity(\"opampinfo\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("opampinfo"));
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 8;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 12;BA.debugLine="End Sub";
return "";
}
public static String  _spinner1_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 93;BA.debugLine="Sub Spinner1_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 94;BA.debugLine="If Spinner1.SelectedIndex = 1 Then Activity.Finish";
if (mostCurrent._spinner1.getSelectedIndex()==1) { 
mostCurrent._activity.Finish();};
 //BA.debugLineNum = 95;BA.debugLine="If Spinner1.SelectedIndex= 1 Then StartActivity(\"opamp1\")";
if (mostCurrent._spinner1.getSelectedIndex()==1) { 
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("opamp1"));};
 //BA.debugLineNum = 96;BA.debugLine="End Sub";
return "";
}
}
