Type=Class
Version=3.2
@EndOfDesignText@
'Class module
'ClsWheelSimpel3
Sub Class_Globals
	Private pnlScreen, pnlMain, pnlBackground, pnlTop, pnlMiddle, pnlBottom As Panel
	Private btnOK, btnCancel As Button
	Private lblTitle As Label
	Private Callback As Object			' calling module
	Private scvWheel As ScrollView	' ScrollView for the data
	Private ColWidth As Int					' width of the column (ScrollView)
	Private FontSize As Float				' font size for the text in the ScrollView
	Private lblHeight As Int				' height of the ScrollView
	
	Private WheelContent As List		' content of the 
	
	' variables used to calculate the dimensions of pnlBackground
	Private Left, Width, Height As Float
	
	Private lineWidth = 4dip As Int							' width of the lines
	
	' colors
	Private colBackGround = Colors.Blue As Int	' background
	Private colShadow = Colors.Black As Int			' shadow of top and bottom panels
	Private colWindowLine = Colors.Red As Int		' lines of center window
	Private colWindow = Colors.ARGB(96, 255, 0, 0) As Int ' center window
	Private colLabelTextColor = Colors.Black As Int		' scrollview label text color
	
	Private TimerWheel 				As Timer

	Private y0		 						As Int 
	Private t0		 						As Long
	Private speed 						As Double
	
	Private btnSpace, btnHeight, btnWidth As Int	' button dimensions
	Private pnlMainWidth, pnlMainHeight As Int		' pnlMain dimensions
	Private Title As String
	Private CallBackView As Object
End Sub

'Initializes the Wheel.
'CallbackModule	= calling module
'Parent 				= parent activity or panel
'cTitle					= title for the input
'cWheelContent 	= List object with the content on the wheel
'cFontSize			= font size of the text in the wheel
Public Sub Initialize(CallbackModule As Object, Parent As Object, cTitle As String, cWheelContent As List, cFontSize As Float)
	' internal variables
	Callback = CallbackModule
	Title = cTitle
	FontSize = cFontSize
	WheelContent = cWheelContent
	
	btnHeight = 50dip
	btnWidth = 100dip
	btnSpace = 6dip

	' calculate the text height in pixels acording to the text size
	Dim cvs As Canvas
	cvs.Initialize(Parent)
	lblHeight = cvs.MeasureStringHeight("Ag", Typeface.DEFAULT, FontSize) + DipToCurrent(FontSize / 2)
	
	' we admit the total height to 5 times the label height
	Height = 5 * lblHeight
	' calculate the width of the longest text in the ScrollView according to the text size
	ColWidth = 0
	For j = 0 To WheelContent.Size -1
		ColWidth = Max(ColWidth, cvs.MeasureStringWidth(WheelContent.get(j), Typeface.DEFAULT, FontSize))
	Next 
	ColWidth = ColWidth + 20dip					' add a margin
	Width = ColWidth + 2 * lineWidth		' add two line widths for the total width
	
	' initialize pnlScreen and add it onto the activity
	pnlScreen.Initialize("pnlScreen")
	Dim act As Activity
	act = Parent
	act.AddView(pnlScreen, 0, 0, 100%x, 100%y)
	pnlScreen.Color = Colors.ARGB(196, 24, 24, 24)
	pnlScreen.Visible = False
	
	' initialize pnlMain and add it onto pnlScreen
	pnlMain.Initialize("pnlMain")
	' gets the max width value between the wheel and the buttons
	pnlMainWidth = Max(2 * btnWidth + 4 * btnSpace, Width)
	' gets the max width value between the current pnlMainWidth and the title
	pnlMainWidth = Max(pnlMainWidth, cvs.MeasureStringWidth(Title, Typeface.DEFAULT, FontSize))
	pnlMainHeight = 6 * lblHeight + 2 * btnSpace + btnHeight
	pnlScreen.AddView(pnlMain, (100%x - pnlMainWidth) / 2, (100%y - pnlMainHeight) / 2, pnlMainWidth, pnlMainHeight)
	pnlMain.Color = colShadow
	
	' initialize lblTitle and add it onto pnlMain
	lblTitle.Initialize("")
	pnlMain.AddView(lblTitle, 0, 0, pnlMainWidth, lblHeight)
	lblTitle.Color = colShadow
	lblTitle.TextSize = FontSize
	lblTitle.Gravity = Bit.OR(Gravity.CENTER_HORIZONTAL, Gravity.BOTTOM)
	lblTitle.Text = Title
	
	' initialize btnOK and add it onto pnlMain
	btnOK.Initialize("btnOK")
	pnlMain.AddView(btnOK, pnlMainWidth / 2 - btnSpace - btnWidth, pnlMainHeight - btnSpace - btnHeight, btnWidth, btnHeight)
	btnOK.Text = "O K"
	
	' initialize btnCancel and add it onto pnlMain
	btnCancel.Initialize("btnCancel")
	pnlMain.AddView(btnCancel, pnlMainWidth / 2 + btnSpace, pnlMainHeight - btnSpace - btnHeight, btnWidth, btnHeight)
	btnCancel.Text = "Cancel"
	
	' initialize pnlBackground and add it onto the parent object
	pnlBackground.Initialize("pnlBackground")
	Left = (pnlMainWidth - Width) / 2		' center pnlBackground in the parent object
'	Top = (pnlMainHeight - Height) / 2
	pnlMain.AddView(pnlBackground, Left, lblHeight, Width, Height)
	pnlBackground.Color = colBackGround
	
	' initialize the ScrollView and add it onto the parent object
	scvWheel.Initialize2(lblHeight * (WheelContent.Size + 4), "scvWheel")
	pnlBackground.AddView(scvWheel, lineWidth, 0, ColWidth, Height)
	
	' add the Touch event to the ScrollView
	Private objWheel 				As Reflector
	objWheel.Target = scvWheel
	objWheel.SetOnTouchListener("scvWheel_Touch")
	objWheel.RunMethod2("setVerticalScrollBarEnabled", False, "java.lang.boolean")
	
	' fill the ScrollView
	For j = 0 To WheelContent.Size + 5
		Dim lbl As Label
		lbl.Initialize("")
		scvWheel.Panel.AddView(lbl, 0, j * lblHeight, ColWidth, lblHeight)
		lbl.Gravity = Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL
		lbl.Color = Colors.White
		lbl.TextColor = colLabelTextColor
		lbl.TextSize = FontSize
		If j >= 2 AND j <= WheelContent.Size + 2 - 1 Then
			lbl.Text = WheelContent.get(j - 2)
		Else
			lbl.Text = ""
		End If
	Next
	
	' initialize the top panel and set its background
	pnlTop.Initialize("")
	pnlBackground.AddView(pnlTop, 0, 0, Width, 2 * lblHeight)
	TopBackground

	' initialize the middle panel and set its background color
	pnlMiddle.Initialize("")
	pnlBackground.AddView(pnlMiddle, 0, 2 * lblHeight, Width, lblHeight)
	pnlMiddle.Color = colWindow
	
	' initialize the bottom panel and set its background
	pnlBottom.Initialize("")
	pnlBackground.AddView(pnlBottom, 0, (2 + 1) * lblHeight, Width, 2 * lblHeight)
	BottomBackGround

	TimerWheel.Initialize("TimerWheel", 200)

End Sub

'show the wheel
'cCallBackView = view or string variable that gets the returned value
'Value = value to preset
Public Sub Show(cCallBackView As Object, Value As Object)
	Dim index As Int
	
	CallBackView = cCallBackView
	
	pnlScreen.Visible = True
	
	index = WheelContent.IndexOf(Value)
	scvWheel.ScrollPosition = index * lblHeight
	DoEvents
	scvWheel.ScrollPosition = index * lblHeight
	DoEvents
End Sub

Private Sub scvWheel_Touch(ViewTag As Object, Action As Int, X As Float, Y As Float, MotionEvent As Object) As Boolean
	Dim dt As Long		' delta time 
	Dim tt As Int
	
	Select Action
	Case 0	' ACTION_DOWN
		t0 = DateTime.Now
		y0 = Y
	Case 1	' ACTION_UP
		tt = Max(10, -Logarithm(1 / speed, cE) * 110)	' estimated time till scroll end
		TimerWheel.Interval = tt
		TimerWheel.Enabled = True
	Case 2	' ACTION_MOVE
		dt = (DateTime.Now - t0)		' time difference between current and previous position
		speed = Abs((Y - y0) / dt * 250)	' speed of the move
		t0 = DateTime.Now		' reset time
		y0 = Y
	End Select
	
	Return False
End Sub

Private Sub TimerWheel_Tick
	TimerWheel.Enabled = False
	scvWheel.ScrollPosition = Floor(scvWheel.ScrollPosition / lblHeight + .5) * lblHeight
End Sub

Private Sub GetSelection As String
	Dim i As Int
	
	i = (Floor(scvWheel.ScrollPosition / lblHeight))
	Return WheelContent.get(i)
End Sub 

Private Sub btnOK_Click
	If CallBackView Is Label Then
		Dim lbl As Label
		lbl = CallBackView
		lbl.Text = GetSelection
	Else If CallBackView Is String Then
		CallBackView = GetSelection
	End If
	pnlScreen.Visible = False
End Sub

Private Sub btnCancel_Click
	pnlScreen.Visible = False
End Sub

Private Sub pnlScreen_Click
	' empty to consume the pnlScreen events
End Sub

Private Sub pnlMain_Click
	' empty to consume the pnlMain events
End Sub

Private Sub TopBackground
	Dim gdw As GradientDrawable
	Dim cols(2) As Int
	cols(0) = colShadow
	cols(1) = Colors.Transparent
	gdw.Initialize("TOP_BOTTOM", cols)
	gdw.CornerRadius = 0
	pnlTop.Background = gdw
	Dim cvs1 As Canvas
	cvs1.Initialize(pnlTop)	' initialize a canvas and draw the line on the bottom
	cvs1.DrawLine(lineWidth, pnlTop.Height - lineWidth / 2, Width - lineWidth, pnlTop.Height - lineWidth / 2, colWindowLine, lineWidth)
End Sub

Private Sub BottomBackGround
	Dim gdw As GradientDrawable
	Dim cols(2) As Int
	cols(0) = colShadow
	cols(1) = Colors.Transparent
	gdw.Initialize("BOTTOM_TOP", cols)
	gdw.CornerRadius = 0
	pnlBottom.Background = gdw
	Dim cvs2 As Canvas
	cvs2.Initialize(pnlBottom)	' initialize a canvas and draw the line on the top
	cvs2.DrawLine(lineWidth, lineWidth / 2, Width - lineWidth, lineWidth / 2, colWindowLine, lineWidth)
End Sub

'Gets or sets the BackgroundColor property
Sub setBackGroundColor(col As Int)
	colBackGround = col
	pnlBackground.Color = colBackGround
End Sub

Sub getBackGroundColor As Int
	Return colBackGround
End Sub

'Gets or sets the ShadowColor property
Sub setShadowColor(col As Int)
	colShadow = col
	pnlMain.Color = colShadow
	lblTitle.Color = colShadow
	TopBackground
	BottomBackGround
End Sub

Sub getShadowColor As Int
	Return colShadow
End Sub

'Gets or sets the ShadowColor property
Sub setWindowColor(col As Int)
	colWindowLine = col
	
	' get the color components 
	Dim res(4) As Int
	res(0) = Bit.UnsignedShiftRight(Bit.AND(colWindowLine, 0xff000000), 24)	' alpha
	res(1) = Bit.UnsignedShiftRight(Bit.AND(colWindowLine, 0xff0000), 16)		' red
	res(2) = Bit.UnsignedShiftRight(Bit.AND(colWindowLine, 0xff00), 8)			' green
	res(3) = Bit.AND(colWindowLine, 0xff)																		' blue
	' sets the alpha value to 96 
	colWindow = Colors.ARGB(96, res(1), res(2), res(3))
		
	TopBackground
	BottomBackGround
	pnlMiddle.Color = colWindow
End Sub

Sub getWindowColor As Int
	Return colWindowLine
End Sub

'Gets or sets the LabelTextColor property
Sub setLabelTextColor(col As Int)
	colLabelTextColor = col
	
	Dim i As Int
	
	For i = 0 To scvWheel.Panel.NumberOfViews - 1
		Dim lbl As Label
		lbl = scvWheel.Panel.GetView(i)    ' get the Label from the scrollView
		lbl.TextColor = colLabelTextColor	 ' set the new color
	Next
End Sub

Sub getLabelTextColor As Int
	Return colLabelTextColor
End Sub

