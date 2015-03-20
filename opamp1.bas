Type=Activity
Version=3.2
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: False
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
Dim i,j As Int =0
	Private Spinner1 As Spinner
	Private Button1 As Button
	Private Button2 As Button
	Private EditText1 As EditText
	Private EditText2 As EditText
	Private EditText3 As EditText
	Private EditText4 As EditText
	Private ImageView1 As ImageView
	Private Label1 As Label
	Private Label2 As Label
	Private Label3 As Label
	Private Label4 As Label
	Private Label5 As Label
	Private Label6 As Label
	Private Label7 As Label
	Private Label8 As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("opamp1")
	Spinner1.AddAll(Array As String("Non-Inverting Amplifier","Inverting Amplifier"))
	   EditText1.SingleLine = True
		EditText2.SingleLine = True
		EditText3.SingleLine = True
		EditText4.SingleLine = True
		Spinner1.Color = Colors.White
		Activity.AddMenuItem("About Opamps","mnpage1")
End Sub
Sub mnpage1_Click
Activity.Finish
StartActivity("opampinfo")
End Sub
Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub
Sub Spinner1_ItemClick (Position As Int, Value As Object)
	If Spinner1.SelectedIndex = 1 Then Activity.Finish
	If Spinner1.SelectedIndex = 1 Then StartActivity("opamp")
End Sub
Sub Button2_Click
	Activity.Finish
	StartActivity("Main")
End Sub
Sub Button1_Click
	EditText1.InputType = EditText1.INPUT_TYPE_DECIMAL_NUMBERS
	EditText2.InputType = EditText2.INPUT_TYPE_DECIMAL_NUMBERS
	EditText3.InputType = EditText3.INPUT_TYPE_DECIMAL_NUMBERS
	
	If EditText1.Text ="" Then EditText1.Text = 0 
	If EditText2.Text ="" Then EditText2.Text = 0 
	If EditText3.Text ="" Then EditText3.Text = 0 
	
	If i=1 OR i=-1 Then EditText4.Text = EditText1.Text *( 1+(EditText3.Text /(EditText2.Text*1000))) 
	If j=1 OR j=-1 Then EditText4.Text =  EditText1.Text *(1+(( EditText3.Text*1000) /EditText2.Text ))
	If (i=1 OR i=-1) AND (j=1 OR j=-1) Then EditText4.Text =  EditText1.Text *( 1+((EditText3.Text*1000) /(EditText2.Text*1000))) 
	If i<>1 AND j<>1 AND i<>-1 AND j<>-1 Then EditText4.Text = EditText1.Text *(1+( EditText3.Text /EditText2.Text ))

End Sub
Sub Label6_Click
	i=i+1 
	If i=0 Then Label6.Text = "Ω" 
	If i=1 Then Label6.Text = "KΩ"
	If i=2 Then i=-1 
End Sub
Sub Label7_Click
	 j=j+1 
	If j=0 Then Label7.Text = "Ω" 
	If j=1 Then Label7.Text = "KΩ"
	If j=2 Then j=-1 
End Sub