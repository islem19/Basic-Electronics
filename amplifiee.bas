Type=Activity
Version=3.2
@EndOfDesignText@
#Region Module Attributes
	#FullScreen: False
	#IncludeTitle: True

#End Region

'Activity module
Sub Process_Globals
	
End Sub

Sub Globals
	Dim whlCustom As ClsWheelSimple
	Dim SelectedFruit As String
	Private Button1 As Button
	Private Label1 As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("amp")
	
	InitDateWheel
End Sub

Sub Activity_Resume
'	whlCustom.BackGroundColor = Colors.Green
	whlCustom.ShadowColor = Colors.DarkGray
	whlCustom.WindowColor = Colors.Blue
	whlCustom.LabelTextColor = Colors.Red
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub InitDateWheel
	Dim lstData As List
	lstData.Initialize
	
	lstData.Add("opamp")	
	lstData.Add("inductor")	
	lstData.Add("resistor")	
		
	
	whlCustom.Initialize(Me, Activity, "Select ", lstData, 24)
End Sub

Sub Button1_Click
	whlCustom.Show(Label1, Label1.Text)
	
	If (Label1.Text = "opamp" )Then StartActivity("opamp")
	
	
	
End Sub