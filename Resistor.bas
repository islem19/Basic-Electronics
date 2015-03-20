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
	Dim i,j,k,m As Int = 0
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private Label1 As Label
	Private Label2 As Label
	Private Label3 As Label
	Private Label4 As Label
	Private Panel1 As Panel
	Private Button2 As Button
	Private EditText1 As EditText
	Private EditText2 As EditText
	Private Label6 As Label
	Private Label7 As Label
	Private Button1 As Button
	Private Spinner1 As Spinner
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("resistor4band")
	Label1.Color = Colors.Black 
	Label2.Color = Colors.Black 
	Label3.Color = Colors.Black 
	Label4.Color = Colors.RGB(130,65,0) 
	
Activity.AddMenuItem("4 Band Resistor","mnuPage1") 
Activity.AddMenuItem("Inductor","mnuPage2") 
Activity.AddMenuItem("Back","mnuPage3") 


	
	Spinner1.Add("4 Band Resistor")
	Spinner1.Add("Inductor")
End Sub
Sub mnuPage1_Click
 
 End Sub
 Sub mnuPage2_Click
 StartActivity("inductor")
 Activity.Finish
 End Sub
  Sub mnuPage3_Click
 Activity.Finish
 StartActivity("Main")
 End Sub
 
 

Sub Activity_Resume
	
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Label1_Click
    i=i+1
	
	Select i 
	Case 0 
	Label1.Color = Colors.Black
	Case 1 
	Label1.color = Colors.RGB(130,65,0)
	Case 2 
	Label1.color = Colors.Red
	Case 3 
	Label1.color = Colors.RGB(255,130,0)
	Case 4 
	Label1.color = Colors.Yellow
	Case 5
	Label1.color = Colors.Green
	Case 6 
	Label1.color = Colors.Blue
	Case 7 
	Label1.color = Colors.Magenta
	Case 8 
	Label1.color = Colors.Gray
	Case 9
	Label1.color = Colors.White
	Case 10 
	i = -1
	End Select
End Sub

Sub Label4_Click
	m=m+1
	Select m
	Case 0 
	Label4.Color = Colors.RGB(130,65,0)
	Case 1 
	Label4.color = Colors.Red
	Case 2 
	Label4.color = Colors.Green
	Case 3 
	Label4.color = Colors.Blue
	Case 4 
	Label4.color = Colors.Magenta
	Case 5
	Label4.color = Colors.Gray
	Case 6 
	Label4.color = Colors.RGB(255, 215, 0)
	Case 7 
	Label4.color = Colors.RGB(229,229,229)
	Case 8 
	m = -1
	End Select
End Sub
Sub Label3_Click
	k=k+1
	Select k
	Case 0 
	Label3.Color = Colors.Black
	Case 1 
	Label3.color = Colors.RGB(130,65,0)
	Case 2 
	Label3.color = Colors.Red
	Case 3 
	Label3.color = Colors.RGB(255,130,0)
	Case 4 
	Label3.color = Colors.Yellow
	Case 5
	Label3.color = Colors.Green
	Case 6 
	Label3.color = Colors.Blue
	Case 7 
	Label3.color = Colors.Magenta
	Case 8 
	Label3.color = Colors.Gray
	Case 9
	Label3.color = Colors.White
	Case 10 
	k = -1
	End Select
End Sub
Sub Label2_Click
	j=j+1
	Select j
	Case 0 
	Label2.Color = Colors.Black
	Case 1 
	Label2.color = Colors.RGB(130,65,0)
	Case 2 
	Label2.color = Colors.Red
	Case 3 
	Label2.color = Colors.RGB(255,130,0)
	Case 4 
	Label2.color = Colors.Yellow
	Case 5
	Label2.color = Colors.Green
	Case 6 
	Label2.color = Colors.Blue
	Case 7 
	Label2.color = Colors.Magenta
	Case 8 
	Label2.color = Colors.Gray
	Case 9
	Label2.color = Colors.White
	Case 10 
	j = -1
	End Select
End Sub


Sub Button2_Click
    If i=-1 AND j=-1 AND k=-1  Then 
	EditText1.Text = 99*Power(10,9)
	Else If i=-1 AND j=-1 Then 
	EditText1.Text = 99*Power(10,k)
	Else If j=-1 AND k=-1 Then 
	EditText1.Text =(9+i*10)*Power(10,9)
	Else If i=-1 AND k=-1 Then 
	EditText1.Text = (j+90)*Power(10,9)
	Else If  i=-1 Then 
	EditText1.Text = (90+j)*Power(10,k)
	Else If  j=-1 Then 
	EditText1.Text = (i*10+9)*Power(10,k)
	Else If  k=-1 Then 
	EditText1.Text = (i*10+j)*Power(10,9)
	Else
	EditText1.Text = (j+i*10)*Power(10,k)
	End If 
	
	If EditText1.Text >= Power(10,9) Then Label7.Text ="GΩ"
	If Power(10,9) > EditText1.Text AND EditText1.Text >= Power(10,6) Then Label7.Text ="MΩ"
	If EditText1.Text >= 1000 AND EditText1.Text < Power(10,6) Then Label7.Text ="KΩ" 
	If EditText1.Text < 1000 Then  Label7.Text ="Ω"
	
	If EditText1.Text >= Power(10,9) Then EditText1.Text = EditText1.Text/Power(10,9)
	If Power(10,9) > EditText1.Text AND EditText1.Text >= Power(10,6) Then EditText1.Text = EditText1.Text/Power(10,6)
	If EditText1.Text >= 1000 AND EditText1.Text < Power(10,6) Then EditText1.Text = EditText1.Text/1000 
	
	
	If m =0 Then 
	EditText2.Text = "1%"
	Else If m=1 Then 
	EditText2.Text = "2%"
	Else If m=2 Then 
	EditText2.Text = "0.5%"
	Else If m=3 Then 
	EditText2.Text = "0.25%"
	Else If m=4 Then 
	EditText2.Text = "0.1%"
	Else If m=5 Then 
	EditText2.Text = "0.05%"
	Else If m=6 Then 
	EditText2.Text = "5%"
	Else If m=7 Then 
	EditText2.Text = "10%"
	Else 
	EditText2.Text = "1%"
	End If 
	
End Sub
Sub Button1_Click
	Activity.Finish
	StartActivity("Main")
End Sub
Sub Spinner1_ItemClick (Position As Int, Value As Object)
	If Spinner1.SelectedIndex = 1 Then  Activity.Finish
	If Spinner1.SelectedIndex = 1 Then StartActivity("inductor")
End Sub