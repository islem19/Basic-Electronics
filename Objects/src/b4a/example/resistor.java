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

public class resistor extends Activity implements B4AActivity{
	public static resistor mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.resistor");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (resistor).");
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
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.resistor");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.resistor", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (resistor) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (resistor) Resume **");
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
		return resistor.class;
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
        BA.LogInfo("** Activity (resistor) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (resistor) Resume **");
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
public static int _i = 0;
public static int _j = 0;
public static int _k = 0;
public static int _m = 0;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label4 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label6 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label7 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinner1 = null;
public b4a.example.dateutils _dateutils = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public b4a.example.main _main = null;
public b4a.example.inductor _inductor = null;
public b4a.example.opamp _opamp = null;
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
 //BA.debugLineNum = 30;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 32;BA.debugLine="Activity.LoadLayout(\"resistor4band\")";
mostCurrent._activity.LoadLayout("resistor4band",mostCurrent.activityBA);
 //BA.debugLineNum = 33;BA.debugLine="Label1.Color = Colors.Black";
mostCurrent._label1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 34;BA.debugLine="Label2.Color = Colors.Black";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 35;BA.debugLine="Label3.Color = Colors.Black";
mostCurrent._label3.setColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 36;BA.debugLine="Label4.Color = Colors.RGB(130,65,0)";
mostCurrent._label4.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (130),(int) (65),(int) (0)));
 //BA.debugLineNum = 38;BA.debugLine="Activity.AddMenuItem(\"4 Band Resistor\",\"mnuPage1\")";
mostCurrent._activity.AddMenuItem("4 Band Resistor","mnuPage1");
 //BA.debugLineNum = 39;BA.debugLine="Activity.AddMenuItem(\"Inductor\",\"mnuPage2\")";
mostCurrent._activity.AddMenuItem("Inductor","mnuPage2");
 //BA.debugLineNum = 40;BA.debugLine="Activity.AddMenuItem(\"Back\",\"mnuPage3\")";
mostCurrent._activity.AddMenuItem("Back","mnuPage3");
 //BA.debugLineNum = 44;BA.debugLine="Spinner1.Add(\"4 Band Resistor\")";
mostCurrent._spinner1.Add("4 Band Resistor");
 //BA.debugLineNum = 45;BA.debugLine="Spinner1.Add(\"Inductor\")";
mostCurrent._spinner1.Add("Inductor");
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 65;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 61;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 227;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 228;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 229;BA.debugLine="StartActivity(\"Main\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Main"));
 //BA.debugLineNum = 230;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
 //BA.debugLineNum = 177;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 178;BA.debugLine="If i=-1 AND j=-1 AND k=-1  Then";
if (_i==-1 && _j==-1 && _k==-1) { 
 //BA.debugLineNum = 179;BA.debugLine="EditText1.Text = 99*Power(10,9)";
mostCurrent._edittext1.setText((Object)(99*anywheresoftware.b4a.keywords.Common.Power(10,9)));
 }else if(_i==-1 && _j==-1) { 
 //BA.debugLineNum = 181;BA.debugLine="EditText1.Text = 99*Power(10,k)";
mostCurrent._edittext1.setText((Object)(99*anywheresoftware.b4a.keywords.Common.Power(10,_k)));
 }else if(_j==-1 && _k==-1) { 
 //BA.debugLineNum = 183;BA.debugLine="EditText1.Text =(9+i*10)*Power(10,9)";
mostCurrent._edittext1.setText((Object)((9+_i*10)*anywheresoftware.b4a.keywords.Common.Power(10,9)));
 }else if(_i==-1 && _k==-1) { 
 //BA.debugLineNum = 185;BA.debugLine="EditText1.Text = (j+90)*Power(10,9)";
mostCurrent._edittext1.setText((Object)((_j+90)*anywheresoftware.b4a.keywords.Common.Power(10,9)));
 }else if(_i==-1) { 
 //BA.debugLineNum = 187;BA.debugLine="EditText1.Text = (90+j)*Power(10,k)";
mostCurrent._edittext1.setText((Object)((90+_j)*anywheresoftware.b4a.keywords.Common.Power(10,_k)));
 }else if(_j==-1) { 
 //BA.debugLineNum = 189;BA.debugLine="EditText1.Text = (i*10+9)*Power(10,k)";
mostCurrent._edittext1.setText((Object)((_i*10+9)*anywheresoftware.b4a.keywords.Common.Power(10,_k)));
 }else if(_k==-1) { 
 //BA.debugLineNum = 191;BA.debugLine="EditText1.Text = (i*10+j)*Power(10,9)";
mostCurrent._edittext1.setText((Object)((_i*10+_j)*anywheresoftware.b4a.keywords.Common.Power(10,9)));
 }else {
 //BA.debugLineNum = 193;BA.debugLine="EditText1.Text = (j+i*10)*Power(10,k)";
mostCurrent._edittext1.setText((Object)((_j+_i*10)*anywheresoftware.b4a.keywords.Common.Power(10,_k)));
 };
 //BA.debugLineNum = 196;BA.debugLine="If EditText1.Text >= Power(10,9) Then Label7.Text =\"GΩ\"";
if ((double)(Double.parseDouble(mostCurrent._edittext1.getText()))>=anywheresoftware.b4a.keywords.Common.Power(10,9)) { 
mostCurrent._label7.setText((Object)("GΩ"));};
 //BA.debugLineNum = 197;BA.debugLine="If Power(10,9) > EditText1.Text AND EditText1.Text >= Power(10,6) Then Label7.Text =\"MΩ\"";
if (anywheresoftware.b4a.keywords.Common.Power(10,9)>(double)(Double.parseDouble(mostCurrent._edittext1.getText())) && (double)(Double.parseDouble(mostCurrent._edittext1.getText()))>=anywheresoftware.b4a.keywords.Common.Power(10,6)) { 
mostCurrent._label7.setText((Object)("MΩ"));};
 //BA.debugLineNum = 198;BA.debugLine="If EditText1.Text >= 1000 AND EditText1.Text < Power(10,6) Then Label7.Text =\"KΩ\"";
if ((double)(Double.parseDouble(mostCurrent._edittext1.getText()))>=1000 && (double)(Double.parseDouble(mostCurrent._edittext1.getText()))<anywheresoftware.b4a.keywords.Common.Power(10,6)) { 
mostCurrent._label7.setText((Object)("KΩ"));};
 //BA.debugLineNum = 199;BA.debugLine="If EditText1.Text < 1000 Then  Label7.Text =\"Ω\"";
if ((double)(Double.parseDouble(mostCurrent._edittext1.getText()))<1000) { 
mostCurrent._label7.setText((Object)("Ω"));};
 //BA.debugLineNum = 201;BA.debugLine="If EditText1.Text >= Power(10,9) Then EditText1.Text = EditText1.Text/Power(10,9)";
if ((double)(Double.parseDouble(mostCurrent._edittext1.getText()))>=anywheresoftware.b4a.keywords.Common.Power(10,9)) { 
mostCurrent._edittext1.setText((Object)((double)(Double.parseDouble(mostCurrent._edittext1.getText()))/(double)anywheresoftware.b4a.keywords.Common.Power(10,9)));};
 //BA.debugLineNum = 202;BA.debugLine="If Power(10,9) > EditText1.Text AND EditText1.Text >= Power(10,6) Then EditText1.Text = EditText1.Text/Power(10,6)";
if (anywheresoftware.b4a.keywords.Common.Power(10,9)>(double)(Double.parseDouble(mostCurrent._edittext1.getText())) && (double)(Double.parseDouble(mostCurrent._edittext1.getText()))>=anywheresoftware.b4a.keywords.Common.Power(10,6)) { 
mostCurrent._edittext1.setText((Object)((double)(Double.parseDouble(mostCurrent._edittext1.getText()))/(double)anywheresoftware.b4a.keywords.Common.Power(10,6)));};
 //BA.debugLineNum = 203;BA.debugLine="If EditText1.Text >= 1000 AND EditText1.Text < Power(10,6) Then EditText1.Text = EditText1.Text/1000";
if ((double)(Double.parseDouble(mostCurrent._edittext1.getText()))>=1000 && (double)(Double.parseDouble(mostCurrent._edittext1.getText()))<anywheresoftware.b4a.keywords.Common.Power(10,6)) { 
mostCurrent._edittext1.setText((Object)((double)(Double.parseDouble(mostCurrent._edittext1.getText()))/(double)1000));};
 //BA.debugLineNum = 206;BA.debugLine="If m =0 Then";
if (_m==0) { 
 //BA.debugLineNum = 207;BA.debugLine="EditText2.Text = \"1%\"";
mostCurrent._edittext2.setText((Object)("1%"));
 }else if(_m==1) { 
 //BA.debugLineNum = 209;BA.debugLine="EditText2.Text = \"2%\"";
mostCurrent._edittext2.setText((Object)("2%"));
 }else if(_m==2) { 
 //BA.debugLineNum = 211;BA.debugLine="EditText2.Text = \"0.5%\"";
mostCurrent._edittext2.setText((Object)("0.5%"));
 }else if(_m==3) { 
 //BA.debugLineNum = 213;BA.debugLine="EditText2.Text = \"0.25%\"";
mostCurrent._edittext2.setText((Object)("0.25%"));
 }else if(_m==4) { 
 //BA.debugLineNum = 215;BA.debugLine="EditText2.Text = \"0.1%\"";
mostCurrent._edittext2.setText((Object)("0.1%"));
 }else if(_m==5) { 
 //BA.debugLineNum = 217;BA.debugLine="EditText2.Text = \"0.05%\"";
mostCurrent._edittext2.setText((Object)("0.05%"));
 }else if(_m==6) { 
 //BA.debugLineNum = 219;BA.debugLine="EditText2.Text = \"5%\"";
mostCurrent._edittext2.setText((Object)("5%"));
 }else if(_m==7) { 
 //BA.debugLineNum = 221;BA.debugLine="EditText2.Text = \"10%\"";
mostCurrent._edittext2.setText((Object)("10%"));
 }else {
 //BA.debugLineNum = 223;BA.debugLine="EditText2.Text = \"1%\"";
mostCurrent._edittext2.setText((Object)("1%"));
 };
 //BA.debugLineNum = 226;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 16;BA.debugLine="Private Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Private Label2 As Label";
mostCurrent._label2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Private Label3 As Label";
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Private Label4 As Label";
mostCurrent._label4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private Button2 As Button";
mostCurrent._button2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private EditText1 As EditText";
mostCurrent._edittext1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private EditText2 As EditText";
mostCurrent._edittext2 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private Label6 As Label";
mostCurrent._label6 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private Label7 As Label";
mostCurrent._label7 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private Spinner1 As Spinner";
mostCurrent._spinner1 = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 28;BA.debugLine="End Sub";
return "";
}
public static String  _label1_click() throws Exception{
 //BA.debugLineNum = 69;BA.debugLine="Sub Label1_Click";
 //BA.debugLineNum = 70;BA.debugLine="i=i+1";
_i = (int) (_i+1);
 //BA.debugLineNum = 72;BA.debugLine="Select i";
switch (_i) {
case 0:
 //BA.debugLineNum = 74;BA.debugLine="Label1.Color = Colors.Black";
mostCurrent._label1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 break;
case 1:
 //BA.debugLineNum = 76;BA.debugLine="Label1.color = Colors.RGB(130,65,0)";
mostCurrent._label1.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (130),(int) (65),(int) (0)));
 break;
case 2:
 //BA.debugLineNum = 78;BA.debugLine="Label1.color = Colors.Red";
mostCurrent._label1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 break;
case 3:
 //BA.debugLineNum = 80;BA.debugLine="Label1.color = Colors.RGB(255,130,0)";
mostCurrent._label1.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (255),(int) (130),(int) (0)));
 break;
case 4:
 //BA.debugLineNum = 82;BA.debugLine="Label1.color = Colors.Yellow";
mostCurrent._label1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 break;
case 5:
 //BA.debugLineNum = 84;BA.debugLine="Label1.color = Colors.Green";
mostCurrent._label1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 break;
case 6:
 //BA.debugLineNum = 86;BA.debugLine="Label1.color = Colors.Blue";
mostCurrent._label1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 break;
case 7:
 //BA.debugLineNum = 88;BA.debugLine="Label1.color = Colors.Magenta";
mostCurrent._label1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Magenta);
 break;
case 8:
 //BA.debugLineNum = 90;BA.debugLine="Label1.color = Colors.Gray";
mostCurrent._label1.setColor(anywheresoftware.b4a.keywords.Common.Colors.Gray);
 break;
case 9:
 //BA.debugLineNum = 92;BA.debugLine="Label1.color = Colors.White";
mostCurrent._label1.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 break;
case 10:
 //BA.debugLineNum = 94;BA.debugLine="i = -1";
_i = (int) (-1);
 break;
}
;
 //BA.debugLineNum = 96;BA.debugLine="End Sub";
return "";
}
public static String  _label2_click() throws Exception{
 //BA.debugLineNum = 148;BA.debugLine="Sub Label2_Click";
 //BA.debugLineNum = 149;BA.debugLine="j=j+1";
_j = (int) (_j+1);
 //BA.debugLineNum = 150;BA.debugLine="Select j";
switch (_j) {
case 0:
 //BA.debugLineNum = 152;BA.debugLine="Label2.Color = Colors.Black";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 break;
case 1:
 //BA.debugLineNum = 154;BA.debugLine="Label2.color = Colors.RGB(130,65,0)";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (130),(int) (65),(int) (0)));
 break;
case 2:
 //BA.debugLineNum = 156;BA.debugLine="Label2.color = Colors.Red";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 break;
case 3:
 //BA.debugLineNum = 158;BA.debugLine="Label2.color = Colors.RGB(255,130,0)";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (255),(int) (130),(int) (0)));
 break;
case 4:
 //BA.debugLineNum = 160;BA.debugLine="Label2.color = Colors.Yellow";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 break;
case 5:
 //BA.debugLineNum = 162;BA.debugLine="Label2.color = Colors.Green";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 break;
case 6:
 //BA.debugLineNum = 164;BA.debugLine="Label2.color = Colors.Blue";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 break;
case 7:
 //BA.debugLineNum = 166;BA.debugLine="Label2.color = Colors.Magenta";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Magenta);
 break;
case 8:
 //BA.debugLineNum = 168;BA.debugLine="Label2.color = Colors.Gray";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.Gray);
 break;
case 9:
 //BA.debugLineNum = 170;BA.debugLine="Label2.color = Colors.White";
mostCurrent._label2.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 break;
case 10:
 //BA.debugLineNum = 172;BA.debugLine="j = -1";
_j = (int) (-1);
 break;
}
;
 //BA.debugLineNum = 174;BA.debugLine="End Sub";
return "";
}
public static String  _label3_click() throws Exception{
 //BA.debugLineNum = 121;BA.debugLine="Sub Label3_Click";
 //BA.debugLineNum = 122;BA.debugLine="k=k+1";
_k = (int) (_k+1);
 //BA.debugLineNum = 123;BA.debugLine="Select k";
switch (_k) {
case 0:
 //BA.debugLineNum = 125;BA.debugLine="Label3.Color = Colors.Black";
mostCurrent._label3.setColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 break;
case 1:
 //BA.debugLineNum = 127;BA.debugLine="Label3.color = Colors.RGB(130,65,0)";
mostCurrent._label3.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (130),(int) (65),(int) (0)));
 break;
case 2:
 //BA.debugLineNum = 129;BA.debugLine="Label3.color = Colors.Red";
mostCurrent._label3.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 break;
case 3:
 //BA.debugLineNum = 131;BA.debugLine="Label3.color = Colors.RGB(255,130,0)";
mostCurrent._label3.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (255),(int) (130),(int) (0)));
 break;
case 4:
 //BA.debugLineNum = 133;BA.debugLine="Label3.color = Colors.Yellow";
mostCurrent._label3.setColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 break;
case 5:
 //BA.debugLineNum = 135;BA.debugLine="Label3.color = Colors.Green";
mostCurrent._label3.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 break;
case 6:
 //BA.debugLineNum = 137;BA.debugLine="Label3.color = Colors.Blue";
mostCurrent._label3.setColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 break;
case 7:
 //BA.debugLineNum = 139;BA.debugLine="Label3.color = Colors.Magenta";
mostCurrent._label3.setColor(anywheresoftware.b4a.keywords.Common.Colors.Magenta);
 break;
case 8:
 //BA.debugLineNum = 141;BA.debugLine="Label3.color = Colors.Gray";
mostCurrent._label3.setColor(anywheresoftware.b4a.keywords.Common.Colors.Gray);
 break;
case 9:
 //BA.debugLineNum = 143;BA.debugLine="Label3.color = Colors.White";
mostCurrent._label3.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 break;
case 10:
 //BA.debugLineNum = 145;BA.debugLine="k = -1";
_k = (int) (-1);
 break;
}
;
 //BA.debugLineNum = 147;BA.debugLine="End Sub";
return "";
}
public static String  _label4_click() throws Exception{
 //BA.debugLineNum = 98;BA.debugLine="Sub Label4_Click";
 //BA.debugLineNum = 99;BA.debugLine="m=m+1";
_m = (int) (_m+1);
 //BA.debugLineNum = 100;BA.debugLine="Select m";
switch (_m) {
case 0:
 //BA.debugLineNum = 102;BA.debugLine="Label4.Color = Colors.RGB(130,65,0)";
mostCurrent._label4.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (130),(int) (65),(int) (0)));
 break;
case 1:
 //BA.debugLineNum = 104;BA.debugLine="Label4.color = Colors.Red";
mostCurrent._label4.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 break;
case 2:
 //BA.debugLineNum = 106;BA.debugLine="Label4.color = Colors.Green";
mostCurrent._label4.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 break;
case 3:
 //BA.debugLineNum = 108;BA.debugLine="Label4.color = Colors.Blue";
mostCurrent._label4.setColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 break;
case 4:
 //BA.debugLineNum = 110;BA.debugLine="Label4.color = Colors.Magenta";
mostCurrent._label4.setColor(anywheresoftware.b4a.keywords.Common.Colors.Magenta);
 break;
case 5:
 //BA.debugLineNum = 112;BA.debugLine="Label4.color = Colors.Gray";
mostCurrent._label4.setColor(anywheresoftware.b4a.keywords.Common.Colors.Gray);
 break;
case 6:
 //BA.debugLineNum = 114;BA.debugLine="Label4.color = Colors.RGB(255, 215, 0)";
mostCurrent._label4.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (255),(int) (215),(int) (0)));
 break;
case 7:
 //BA.debugLineNum = 116;BA.debugLine="Label4.color = Colors.RGB(229,229,229)";
mostCurrent._label4.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (229),(int) (229),(int) (229)));
 break;
case 8:
 //BA.debugLineNum = 118;BA.debugLine="m = -1";
_m = (int) (-1);
 break;
}
;
 //BA.debugLineNum = 120;BA.debugLine="End Sub";
return "";
}
public static String  _mnupage1_click() throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub mnuPage1_Click";
 //BA.debugLineNum = 49;BA.debugLine="End Sub";
return "";
}
public static String  _mnupage2_click() throws Exception{
 //BA.debugLineNum = 50;BA.debugLine="Sub mnuPage2_Click";
 //BA.debugLineNum = 51;BA.debugLine="StartActivity(\"inductor\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("inductor"));
 //BA.debugLineNum = 52;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return "";
}
public static String  _mnupage3_click() throws Exception{
 //BA.debugLineNum = 54;BA.debugLine="Sub mnuPage3_Click";
 //BA.debugLineNum = 55;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 56;BA.debugLine="StartActivity(\"Main\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Main"));
 //BA.debugLineNum = 57;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Dim i,j,k,m As Int = 0";
_i = 0;
_j = 0;
_k = 0;
_m = (int) (0);
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _spinner1_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 231;BA.debugLine="Sub Spinner1_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 232;BA.debugLine="If Spinner1.SelectedIndex = 1 Then  Activity.Finish";
if (mostCurrent._spinner1.getSelectedIndex()==1) { 
mostCurrent._activity.Finish();};
 //BA.debugLineNum = 233;BA.debugLine="If Spinner1.SelectedIndex = 1 Then StartActivity(\"inductor\")";
if (mostCurrent._spinner1.getSelectedIndex()==1) { 
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("inductor"));};
 //BA.debugLineNum = 234;BA.debugLine="End Sub";
return "";
}
}
