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

	Private ImageView1 As ImageView
	Private ImageView2 As ImageView
	Private Panel1 As Panel
	Private Panel2 As Panel
	Private Panel3 As Panel
	Dim i As Int =0 
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("opinfo")
	Panel1.SetLayout(0,0,1000dip,1000dip)
	Panel2.SetLayout(0,0,1000dip,1000dip)
	Panel3.SetLayout(0,0,1000dip,1000dip)
    ImageView2.Visible = False
	
	Activity.AddMenuItem("Operational Amplifier","mnpage1")
	Activity.AddMenuItem("Main","mnpage2")
	
End Sub
Sub mnpage1_Click
	Activity.Finish
	StartActivity("opamp")
End Sub 
Sub mnpage2_Click
	Activity.Finish
	StartActivity("Main")
End Sub 
Sub Activity_Resume
  
End Sub
Sub Activity_Pause (UserClosed As Boolean)

End Sub
Sub ImageView2_Click
	i=i-1 
	Select i 
	Case 0 
	Panel1.Visible=True
	Panel2.Visible =False
	Panel3.Visible=False
	Case 1 
	Panel2.Visible =True
	Panel1.Visible=False
	Panel3.Visible=False
	Case 2 
	Panel3.Visible = True
	Panel2.Visible =False
	Panel1.Visible=False
	End Select
    If i=0 Then ImageView2.Visible = False
	If i<>0 Then ImageView2.Visible = True
	If i<>2 Then ImageView1.Visible = True
End Sub
Sub ImageView1_Click
	i=i+1 
	Select i 
	Case 0 
	Panel1.Visible=True
	Panel2.Visible =False
	Panel3.Visible=False
	Case 1 
	Panel2.Visible =True
	Panel1.Visible=False
	Panel3.Visible=False
	Case 2
	Panel3.Visible = True
	Panel2.Visible =False
	Panel1.Visible=False
	End Select
   If i=2 Then ImageView1.Visible = False
	If i<>2 Then ImageView1.Visible = True 
	If i<>0 Then ImageView2.Visible = True
End Sub