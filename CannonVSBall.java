/*
 * Homework 6: Cannon vs Ball
Paul MacLean (MAC7537@calu.edu), Michael Gorse (GOR9632@calu.edu), Anthony Carrola (CAR3766@calu.edu)
Group 8 (2^3)
CET 350 - Technical Computer using Java
*/
 


import java.io.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import java.lang.*;					//math stuff should be included in lang
import java.awt.List;
import java.awt.event.*;
import java.awt.geom.Point2D;





class CannonVSBall implements ActionListener,WindowListener,AdjustmentListener,ComponentListener,Runnable, MouseListener, MouseMotionListener, ItemListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//SCROLLER are only used to get the ratio of the scrollbar, regardless of its purpose.
	
	private Frame Game = new Frame();
	
	
	private final int SCROLLER_THUMBOFFSET	= 10;
	private final int SCROLLER_MIN			= 0;
	private final int SCROLLER_MAX			= 100;
	private final int SCROLLER_RANGE		= SCROLLER_MAX - SCROLLER_MIN;
	private final int SCROLLER_RANGE_VIS	= SCROLLER_RANGE - SCROLLER_THUMBOFFSET;
	private final int SCROLLER_DEFAULT		= SCROLLER_RANGE_VIS / 2;
	private final int SCROLLER_BLOCKVAL		= SCROLLER_RANGE_VIS / 10;
	
	//VELOCITY constants
	private final float VELOCITY_MIN			= 50;		//milliseconds
	private final float VELOCITY_MAX			= 1000;		//milliseconds
	private final float VELOCITY_RANGE			= Math.abs(VELOCITY_MAX-VELOCITY_MIN);	//Range should be positive.
	
	//ANGLE constants
	private final float ANGLE_MIN			= 0;		//milliseconds
	private final float ANGLE_MAX			= 90;		//milliseconds
	private final float ANGLE_RANGE			= Math.abs(ANGLE_MAX);	//Range should be positive.
	
	private volatile boolean TimePause = true;
	private Thread SteppingThread;
	
	private Button RunButton			= new Button("Run");
	private Button PauseButton			= new Button("Pause");
	private Button RestartButton 		= new Button("Restart");

	private Scrollbar VelocBar;
	private Scrollbar AngleBar;

	private Label VelocLabel			= new Label("Velocity");
	private Label AngleLabel			= new Label("Cannon Angle");
	
	private TextField BallScoreBox 		= new TextField("0");
	private Label BallScoreLabel 		= new Label("BallScore: ");
	private TextField CannScoreBox 		= new TextField("0");
	private Label CannScoreLabel 		= new Label("Your Score: ");
	
	private Label MsgBox 				= new Label("Hello There");
	
	private TextField AngleofCannon  			= new TextField("45");
	private TextField VelocityNumberLabel		= new TextField("475");
	
	private Panel sheet = null;
	private Panel control = null;
	
	
	private CanObj canvas = null;
	private Cannon cannon = null;
	private Projectile projectile = null;
	
	private int X1 = 0;
	private int Y1 = 0;
	private int X2 = 0;
	private int Y2 = 0;
	
	private CheckboxMenuItem chkcurrsize;
	private CheckboxMenuItem chkcurrspeed;
	private CheckboxMenuItem chkcurrgrav;
	
	
	private boolean MouseDoubleClicked = false;	
	
	//MENU SHTUFFFF-----------------
	
	private	MenuBar MB;
		private Menu Control;
			private MenuItem RunMenu;
			private MenuItem PauseMenu;
			private MenuItem RestartMenu;
			private MenuItem QuitMenu;
		private Menu Parameters; 
			private Menu Size;
				private CheckboxMenuItem XSM, SM, MEDS, LRG, XLRG;
			private Menu Speed;
				private CheckboxMenuItem XSL, SL, MEDF, FST, XFST;
		private Menu Environment;
				private CheckboxMenuItem Mercury, Venus, Earth, Moon, Mars, Jupiter, Saturn, Uranus, Neptune, Pluto;
	
	//------------------------------
	
	CannonVSBall() {
		initComponents();
	}
	
	public static void main(String[] args) throws IOException
	{
		new CannonVSBall();
	}
	
	private void initComponents()
	{
		
		//MENU STUFFFF-------------------------------------
		MB = new MenuBar();							//main menu bar
		
		Control = new Menu ("Control");				//control menu
		RunMenu = Control.add(new MenuItem("Run", new MenuShortcut(KeyEvent.VK_R)));
		RunMenu.addActionListener(this);
		Control.addSeparator();
		PauseMenu = Control.add(new MenuItem("Pause", new MenuShortcut(KeyEvent.VK_P)));
		PauseMenu.addActionListener(this);
		Control.addSeparator();
		RestartMenu = Control.add(new MenuItem("Restart"));
		RestartMenu.addActionListener(this);
		Control.addSeparator();
		QuitMenu = Control.add(new MenuItem("Quit"));
		QuitMenu.addActionListener(this);
		
		Parameters = new Menu("Parameters");
		
		Speed = new Menu("Speed");
			Speed.add(XSL = new CheckboxMenuItem("X-Slow"));
				XSL.addItemListener(this);
			Speed.add(SL = new CheckboxMenuItem("Slow"));
				SL.addItemListener(this);
			Speed.add(MEDF = new CheckboxMenuItem("Medium"));
				MEDF.addItemListener(this);
				MEDF.setState(true);
			Speed.add(FST = new CheckboxMenuItem("Fast"));
				FST.addItemListener(this);
			Speed.add(XFST = new CheckboxMenuItem("Sonic"));
				XFST.addItemListener(this);
				
		Size = new Menu("Size");
			Size.add(XSM = new CheckboxMenuItem("X-Small"));
				XSM.addItemListener(this);
			Size.add(SM = new CheckboxMenuItem("Small"));
				SM.addItemListener(this);
			Size.add(MEDS = new CheckboxMenuItem("Medium"));
				MEDS.addItemListener(this);
				MEDS.setState(true);
			Size.add(LRG = new CheckboxMenuItem("Large"));
				LRG.addItemListener(this);
			Size.add(XLRG = new CheckboxMenuItem("Shaq"));
				XLRG.addItemListener(this);
		
		Parameters.add(Speed);
		Parameters.addSeparator();
		Parameters.add(Size);
		
		Environment = new Menu("Environment");
			Environment.add(Mercury = new CheckboxMenuItem("Mecury_12.14 ft/s/s"));
				Mercury.addItemListener(this);
				Environment.addSeparator();
			Environment.add(Venus = new CheckboxMenuItem("Venus_29.10 ft/s/s"));
				Venus.addItemListener(this);
				Environment.addSeparator();
			Environment.add(Earth = new CheckboxMenuItem("Earth_ 32.14 ft/s/s"));
				Earth.setState(true);
				Earth.addItemListener(this);
				Environment.addSeparator();
			Environment.add(Moon = new CheckboxMenuItem("Moon_5.31 ft/s/s"));
				Moon.addItemListener(this);
				Environment.addSeparator();
			Environment.add(Mars = new CheckboxMenuItem("Mars_12.17 ft/s/s"));
				Mars.addItemListener(this);
				Environment.addSeparator();
			Environment.add(Jupiter = new CheckboxMenuItem("Jupiter_81.76 ft/s/s"));
				Jupiter.addItemListener(this);
				Environment.addSeparator();
			Environment.add(Saturn = new CheckboxMenuItem("Saturn_34.25 ft/s/s"));
				Saturn.addItemListener(this);
				Environment.addSeparator();
			Environment.add(Uranus = new CheckboxMenuItem("Uranus_29.10 ft/s/s"));
				Uranus.addItemListener(this);
				Environment.addSeparator();
			Environment.add(Neptune = new CheckboxMenuItem("Neptune_36.58 ft/s/s"));
				Neptune.addItemListener(this);
				Environment.addSeparator();
			Environment.add(Pluto = new CheckboxMenuItem("Pluto_1.90 ft/s/s"));
				Pluto.addItemListener(this);
			
				
		
		MB.add(Control);
		MB.add(Parameters);
		MB.add(Environment);
		Game.setMenuBar(MB);
		
		chkcurrsize = MEDS;
		chkcurrspeed = MEDF;
		chkcurrgrav = Earth;
		
		//--------------------------------------------------------
		
		Game.setBounds(50, 50 , 800, 500);												//OFFSET 32Y
		Game.setMinimumSize(new Dimension(700, 400));
		Game.setResizable(true);
		
		cannon = new Cannon();
		projectile = new Projectile();
		
		canvas = new CanObj(Game.getWidth(), Game.getHeight()-48, Game.getInsets(),cannon, projectile, BallScoreBox, CannScoreBox, MsgBox);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.setVisible(true);
		
		control = new Panel();
		control.setBounds(0, 0, 800, 300);
		positionComponents();
		
		BorderLayout sheetlayout = new BorderLayout();
		sheet = new Panel();
		sheet.setVisible(true);
		sheet.setSize(800,300);
		sheet.setLayout(sheetlayout);
		sheet.add(canvas, BorderLayout.CENTER);

		BorderLayout border = new BorderLayout();
		Game.setLayout(border);
		Game.add(sheet, BorderLayout.CENTER);
		Game.add(control, BorderLayout.SOUTH);
		
		Game.addComponentListener(this);
		Game.addWindowListener(this);
		
		Game.setVisible(true);
		
		start();
		run();
	}
	
	private void positionComponents() {
		VelocBar = new Scrollbar(Scrollbar.HORIZONTAL);
		AngleBar = new Scrollbar(Scrollbar.HORIZONTAL);
		
		
		//Gridbag stuff
		GridBagConstraints GBConstr = new GridBagConstraints();					// creates new grid bag constraints
		GridBagLayout GBLayout = new GridBagLayout();							// creates new grid bag layout
		
		int[] colWidth = {1,1,1,1,1,1,1,1,1,1,1,1,1};							//16
		int[] rowWidth = {1,1,1,1,1,1};
		
		double colWeight[] = {1,1,1,1,1,1,1,1,1,1,1,1,1};						//weight to cols
		double rowWeight[] = {1,1,1,1,1,1};											//weight to rows
		
		GBLayout.columnWeights = colWeight;
		GBLayout.rowWeights = rowWeight;
		GBLayout.columnWidths = colWidth;
		GBLayout.rowHeights = rowWidth;
		
		//VelocBar
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 2;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 10;
		GBConstr.gridy = 4;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(VelocBar, GBConstr);
		
		//VelocLabel
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 2;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 8;
		GBConstr.gridy = 4;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(VelocLabel, GBConstr);
		
		
		//Restart Button
		/*GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridx = 5;
		GBConstr.gridy = 4;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(RestartButton, GBConstr);*/
	
		//AngleBar
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 2;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 10;
		GBConstr.gridy = 1;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(AngleBar, GBConstr);
		
		//AngleLabel
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 8;
		GBConstr.gridy = 1;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(AngleLabel, GBConstr);
		
		// Angle of Cannon Label
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 7;
		GBConstr.gridy = 1;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(AngleofCannon, GBConstr);
		
		// Velocity of Cannon Label
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 7;
		GBConstr.gridy = 4;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(VelocityNumberLabel, GBConstr);
		
		
		//Cannon Score Label (i.e. users)
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 1;
		GBConstr.gridy = 1;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(CannScoreLabel, GBConstr);

		
		//BallScore Label
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 3;
		GBConstr.gridy = 1;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(BallScoreLabel, GBConstr);
		
		//Cannon Score 
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 1;
		GBConstr.gridy = 4;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.CENTER;
		GBLayout.setConstraints(CannScoreBox, GBConstr);

		
		//Ball Score
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 3;
		GBConstr.gridy = 4;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.CENTER;
		GBLayout.setConstraints(BallScoreBox, GBConstr);
		
		
		//Message box 
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 4;
		GBConstr.gridheight = 2;
		GBConstr.gridx = 1;
		GBConstr.gridy = 6;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(MsgBox, GBConstr);
		
		
		
		VelocBar.setValues(SCROLLER_DEFAULT, SCROLLER_THUMBOFFSET, SCROLLER_MIN, SCROLLER_MAX);
		VelocBar.setBlockIncrement(SCROLLER_BLOCKVAL);
		VelocBar.setUnitIncrement(2);
		VelocBar.setBackground(Color.ORANGE);
		VelocBar.addAdjustmentListener(this);
		VelocBar.setVisible(true);
		
		AngleBar.setValues(SCROLLER_DEFAULT, SCROLLER_THUMBOFFSET, SCROLLER_MIN, SCROLLER_MAX);
		AngleBar.setBlockIncrement(SCROLLER_BLOCKVAL);
		AngleBar.setUnitIncrement(2);
		AngleBar.setBackground(Color.ORANGE);
		AngleBar.addAdjustmentListener(this);
		AngleBar.setVisible(true);
		
		VelocLabel.setAlignment(Label.CENTER);
		VelocLabel.setVisible(true);
		
		
		//RestartButton.addActionListener(this);
		//RestartButton.setVisible(true);
		
		AngleLabel.setAlignment(Label.CENTER);
		AngleLabel.setVisible(true);
		
		AngleofCannon.setVisible(true);
		AngleofCannon.setEnabled(false);
		
		VelocityNumberLabel.setVisible(true);
		VelocityNumberLabel.setEnabled(false);
		
		CannScoreLabel.setVisible(true);
		CannScoreLabel.setForeground(Color.BLUE);
		
		BallScoreLabel.setVisible(true);
		BallScoreLabel.setForeground(Color.RED);
		
		CannScoreBox.setVisible(true);
		CannScoreBox.setEnabled(false);

		BallScoreBox.setVisible(true);
		BallScoreBox.setEnabled(false);
		
		MsgBox.setVisible(true);
		MsgBox.setForeground(Color.MAGENTA);
		
		
		control.setLayout(GBLayout);
		control.add(VelocBar);
		control.add(VelocLabel);
		//control.add(RestartButton);
		control.add(AngleBar);
		control.add(AngleLabel);
		control.add(AngleofCannon);
		control.add(VelocityNumberLabel);
		control.add(BallScoreLabel);
		control.add(CannScoreLabel);
		control.add(BallScoreBox);
		control.add(CannScoreBox);
		control.add(MsgBox);
	}
	
	public void start()
	{
		if(SteppingThread == null)
		{
			SteppingThread = new Thread(this);
			SteppingThread.start();
		}
	}
	
	public void run()
	{
		SteppingThread.setPriority(Thread.MAX_PRIORITY);
		
		while(true) {
			try {
				Thread.sleep(1);
			} catch(InterruptedException e){}
			
			canvas.processTimeStep();
		}
	}
	
	public void stop()
	{
		//System.out.println("IN STOP");
		SteppingThread.setPriority(Thread.MIN_PRIORITY);
		Game.removeWindowListener(this);
		System.exit(0);
		Game.dispose();
	}



	
	//=======================================================METHODS FOR BUTTONS
	
	public void setTimePaused(boolean paused) {
		SteppingThread.interrupt();
		TimePause = paused;
		
		canvas.setTimePaused(paused);
		
		try {
			SteppingThread.start();
		} catch(IllegalThreadStateException e) {}
	}
	
	//=======================================================MOUSE EVENTS
	
	public void mousePressed(MouseEvent e) {
		if(e.getClickCount()> 1)
		{
			MouseDoubleClicked = true;
		}
		if(MouseDoubleClicked == false) 
		{
			X1 = e.getX();
			Y1 = e.getY();
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(MouseDoubleClicked == false)
		{
			canvas.storeRectangle();
		}
		MouseDoubleClicked = false;
	}
	
	public void mouseDragged(MouseEvent e) {
		if(MouseDoubleClicked == false)
		{
			X2 = e.getX();
			Y2 = e.getY();
			canvas.setRectParameters(X1,Y1, X2, Y2);
		}
		//System.out.println("Right Mouse Clicked: " + MouseDoubleClicked);
	}
	public void mouseClicked(MouseEvent e) {
        Point clk = new Point(0, 0);
        
        if (cannon.isInCannon(e.getPoint())) {
            if(e.getButton()==1 && TimePause == false)
            projectile.fireProjectile(cannon.getAngle(),cannon.getBarrelEnd(),canvas.getTimePaused(),canvas.getWaitingReset());
        }
        
        if( e.getClickCount()>1 )
        {
            //System.out.println("delete");
            clk = e.getPoint();
            if(e.getButton() == 1)
            canvas.deleteRectangle(clk);
        }
    }
	public void mouseMoved(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	
	
	public void componentResized(ComponentEvent e) {
		//canvas.CheckEdge(Game,this.control);
		canvas.processScreenResize(sheet.getWidth(), sheet.getHeight(), Game.getInsets());
	}
	
	
	
	
	public void windowClosed(WindowEvent e) {
		stop();
	}
	public void windowClosing(WindowEvent e) {
		stop();
	}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
	
	//=======================================================INTERACTION EVENTS
	
	
	
	public void actionPerformed(ActionEvent e) {
		Object click = e.getSource();
		if(click == RunMenu) {
			setTimePaused(false);
		}
		else if (click == PauseMenu) {
			setTimePaused(true);
		}
		else if (click == QuitMenu)
		{
			stop();
		}
		else if (click == RestartMenu)
		{
			canvas.resetBoard();
			canvas.resetScores();
		}
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e) {
		Scrollbar temp = (Scrollbar) e.getSource();
        
        float SBRatio = (float) temp.getValue() / (float) SCROLLER_RANGE_VIS;
        if (temp == VelocBar) {
        	double Velocity = (SBRatio * VELOCITY_RANGE) + VELOCITY_MIN;
        	VelocityNumberLabel.setText(String.valueOf((int)(Velocity)));
        	projectile.setVelocity(Velocity);
        } else if (temp == AngleBar) {
            double Angle = (SBRatio * ANGLE_RANGE) + ANGLE_MIN;
            AngleofCannon.setText(String.valueOf((int)(Angle)));
            canvas.setCannonOrientation(Angle);
            canvas.paint(canvas.getGraphics());
        }
	}
	

	public void itemStateChanged(ItemEvent e)
	{
		CheckboxMenuItem chkBox = (CheckboxMenuItem) e.getSource();
		MenuContainer Parent = chkBox.getParent();
		boolean newstate = chkBox.getState();
		
		if (chkBox == chkcurrspeed) {
			//system.out.println("Speed same");
			chkcurrspeed.setState(true);
		} else if (chkBox == chkcurrsize) {
			chkcurrsize.setState(true);
		} else {
			if (Parent == Speed) {
				//system.out.println("Changing speed");
				//assume all states false
				XSL.setState(false);
				SL.setState(false);
				MEDF.setState(false);
				FST.setState(false);
				XFST.setState(false);
				
				//compare each checkbox
				if (chkBox == XSL)
					canvas.setBallRate(80);
				else if (chkBox == SL)
					canvas.setBallRate(35);
				else if (chkBox == MEDF)
					canvas.setBallRate(15);
				else if (chkBox == FST)
					canvas.setBallRate(5);
				else if (chkBox == XFST)
					canvas.setBallRate(1);
				
				chkcurrspeed = chkBox;
				chkBox.setState(true); //set the checkbox to be true
			} else if(Parent == Size) {
				XSM.setState(false);
				SM.setState(false);
				MEDS.setState(false);
				LRG.setState(false);
				XLRG.setState(false);
				
				int newsize = 0;
				if (chkBox == XSM) {
					newsize = 10;
				} else if (chkBox == SM) {
					newsize = 30;
				} else if (chkBox == MEDS) {
					newsize = 50;
				} else if (chkBox == LRG) {
					newsize = 70;
				} else if (chkBox == XLRG) {
					newsize = 100;
				}
				
				if (canvas.setBallSize(newsize)) {
					chkcurrsize = chkBox;
					chkBox.setState(true); //set the checkbox to be true
				} else
					chkcurrsize.setState(true);
			} else if(Parent == Environment) {
				Mercury.setState(false);
				Venus.setState(false);
				Earth.setState(false);
				Moon.setState(false);
				Mars.setState(false);
				Jupiter.setState(false);
				Saturn.setState(false);
				Uranus.setState(false);
				Neptune.setState(false);
				Pluto.setState(false);
				
				int newgrav = 0;
				
				//planet ft/s/s / 0.09 = newgrav
				if (chkBox == Mercury) {
					newgrav = 133;
				} else if (chkBox == Venus) {
					newgrav = 322;
				} else if (chkBox == Earth) {
					newgrav = 355;
				} else if (chkBox == Moon) {
					newgrav = 59;
				} else if (chkBox == Mars) {
					newgrav = 145;
				} else if (chkBox == Jupiter) {
					newgrav = 900;
				} else if (chkBox == Saturn) {
					newgrav = 380;
				} else if (chkBox == Uranus) {
					newgrav = 323;
				} else if (chkBox == Neptune) {
					newgrav = 407;
				} else if (chkBox == Pluto) {
					newgrav = 21;
				}
				
				/*
				Environment.add(Mercury = new CheckboxMenuItem("Mecury_12.14 ft/s/s"));
				Environment.add(Venus = new CheckboxMenuItem("Venus_29.10 ft/s/s"));
				Environment.add(Earth = new CheckboxMenuItem("Earth_ 32.14 ft/s/s"));
				Environment.add(Moon = new CheckboxMenuItem("Moon_5.31 ft/s/s"));
				Environment.add(Mars = new CheckboxMenuItem("Mars_12.17 ft/s/s"));
				Environment.add(Jupiter = new CheckboxMenuItem("Jupiter_81.76 ft/s/s"));
				Environment.add(Saturn = new CheckboxMenuItem("Saturn_34.25 ft/s/s"));
				Environment.add(Uranus = new CheckboxMenuItem("Uranus_29.10 ft/s/s"));
				Environment.add(Neptune = new CheckboxMenuItem("Neptune_36.58 ft/s/s"));
				Environment.add(Pluto = new CheckboxMenuItem("Pluto_1.90 ft/s/s"));
				*/
				
				chkcurrgrav = chkBox;
				chkBox.setState(true); //set the checkbox to be true
				projectile.setGravity(newgrav);
			}
		}
	}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}
}
	





class Cannon
{
	private double theta = 45;
	
	//Temp points
	private Point PointofRef;
	private Point CenterCannon;
	private int CannonLength = 150;
	private int CannonWidthhalf = 30;
	
	private Rectangle maxWheel;
	private boolean destroyed = false;
	
	private int[] CannonXPoints = {0, 0 , 0, 0};
	private int[] CannonYPoints = {0, 0 , 0, 0};
	private Polygon CannonPolygon;
	private Point BarrelEnd;
	
	public void positionCannon(int framewidth, int frameheight, Insets insets) {
		this.PointofRef = new Point(framewidth-40,frameheight-40);
	}
	
	public double getAngle() {
		return theta;
	}
	public Polygon getPolygon() {
		return this.CannonPolygon;
	}
	public Point getPointofRef() {
		return PointofRef;
	}
	public Rectangle getMaxWheel() {
		return maxWheel;
	}
	public Point getBarrelEnd() {
		return BarrelEnd;
	}
	
	public boolean isInCannon(Point clk) {
        return CannonPolygon.contains(clk);
    }
	
	public boolean intersects(Rectangle temp) {
		boolean retval = false;
		if(!destroyed)
		{
			if(this.getPolygon().intersects(temp) || maxWheel.intersects(temp))
			{
				retval = true;
			}
		}
		else
		{
			retval = false;
		}
		return retval;
	}
	
	public void destroyCannon()
	{
		destroyed = true;
	}
	
	public void fixCannon()
	{
		destroyed = false;
	}
	
	public void orientCannon(double theta)
	{
		this.theta = theta;
		double rtheta = Math.toRadians(theta);
		double gamma = Math.toRadians(theta + 90);
		
		int Anchor2x = (int) (PointofRef.x - CannonLength*Math.cos(rtheta));
		int Anchor2y = (int) (PointofRef.y - CannonLength*Math.sin(rtheta));
		BarrelEnd = new Point(Anchor2x,Anchor2y);
		
		int offsetx = (int) (CannonWidthhalf*Math.cos(gamma));			//CannonWidthhalf = half of Cannon
		int offsety = (int) (CannonWidthhalf*Math.sin(gamma));
		
		Point CanPoint1 = new Point(PointofRef.x - offsetx, PointofRef.y - offsety);
		Point CanPoint2 = new Point (Anchor2x - offsetx,  Anchor2y -offsety);
		Point CanPoint3 = new Point (Anchor2x + offsetx, Anchor2y + offsety);
		Point CanPoint4 = new Point (PointofRef.x + offsetx, PointofRef.y+offsety);
		
		int[] tempx = {CanPoint1.x, CanPoint2.x, CanPoint3.x, CanPoint4.x};
		int[] tempy = {CanPoint1.y,CanPoint2.y, CanPoint3.y, CanPoint4.y};
		
		CannonXPoints = tempx;
		CannonYPoints = tempy;
		
		Polygon temppoly = new Polygon(CannonXPoints, CannonYPoints, 4);
		CannonPolygon = temppoly;

	}
	
	public void drawCannon(Graphics g)
	{
		if(!destroyed)
		{
	

			g.setColor(Color.BLACK);
			g.fillPolygon(CannonPolygon);
			g.setColor(Color.CYAN);
			g.drawPolygon(CannonPolygon);
			g.setColor(Color.RED);
			
			maxWheel = new Rectangle(PointofRef.x-40, PointofRef.y-40, 80, 80);
			g.drawOval(PointofRef.x-40, PointofRef.y-40, 80, 80);
			g.drawOval(PointofRef.x-37, PointofRef.y-37, 74, 74);
			g.drawOval(PointofRef.x-20, PointofRef.y-20, 40, 40);
			g.fillOval(PointofRef.x-20, PointofRef.y-20, 40, 40);
			g.drawLine(PointofRef.x-30, PointofRef.y-30, PointofRef.x+30, PointofRef.y+30);
		}
	}
}


class Projectile
{
	private int projectilesize = 30;
	
	private double init_vel = 475;			//set every time scrollbar is adjusted
	private double init_vel_x;		//calculated only upon fireProjectile(); based off of init_vel
	private double init_vel_y;		//same^
	private Point init_pos;			//set from fireProjectile
	private double offset_x=0;
	private double offset_y=0;
	private Rectangle position;
	private Label mssgbox;
	
	private long time_fired;
	private long time_stopped;
	
	int gravity = 50;
	
	boolean traveling = false;
	boolean debounce = false;
	
	public Rectangle getPosition() {
		return position;
	}
	
	
	public void fireProjectile(double theta,Point initialpos,boolean timerunning,boolean waitingforreset) {
		if (!traveling && !waitingforreset) { //if ready to fire, set initial values
			//system.out.println("Firing");
			
			//set initial values
			if (timerunning)
				time_fired = 0;
			else
				time_fired = System.nanoTime();
			time_stopped = 0;
			init_vel_x = init_vel*Math.cos(Math.toRadians(theta));
			init_vel_y = init_vel*Math.sin(Math.toRadians(theta));
			
			init_pos = new Point(initialpos.x - projectilesize/2,initialpos.y - projectilesize/2); //create a new initial pos object
			position = new Rectangle(init_pos.x - (int)offset_x, init_pos.y - (int)offset_y,projectilesize,projectilesize);
			setProjectileTraveling(true); //disallow any consecutive shots to be fired
		}
	}
	
	public void setProjectileTraveling(boolean toggle) {
		traveling = toggle;
		if (!traveling) {
			//system.out.println("Ready to fire");
			
		}
	}
	
	public void setProjectilePaused(boolean paused) {
		if (paused) {
			time_stopped = System.nanoTime();
		} else {
			time_fired = time_fired + (System.nanoTime() - time_stopped);
			//system.out.println("Time fired: " + time_fired);
			//system.out.println("Time stopped: " + time_stopped);
		}
	}
	
	public void setGravity(int gravity) {
		this.gravity = gravity;
	}
	
	public void setVelocity(double velocity) {
		this.init_vel = velocity;
	}
	
	File pathToFile = new File("F:\\PyzdrowskiCannon.png");
	public void drawProjectile(Graphics g) {
		if (traveling && position != null) {
			
			try {
			    Image image = ImageIO.read(pathToFile);
			    g.drawImage(image, position.x,position.y,null);
			} catch (IOException ex) {
				g.fillOval(position.x,position.y,position.width,position.height);
			}
		}
	}
	

	
	public void stepProjectile(int width,int height, Cannon cann, CanObj canvas, Label mssgbox) { //sets offset_x and offset_y based on the difference in time since last calculation
		if (debounce) return;
		debounce = true;
		this.mssgbox = mssgbox;

		if (traveling) {
			float dt = (float)(System.nanoTime() - time_fired) / 1000000000;
			offset_x = dt*init_vel_x;
			offset_y = dt*init_vel_y - 0.5 * gravity*Math.pow(dt, 2);
			//sets position of projectile
			position = new Rectangle(init_pos.x - (int)offset_x, init_pos.y - (int)offset_y,projectilesize,projectilesize);
			
			if (position.y > height || position.getMaxX() < 0)
			{
				setProjectileTraveling(false);
				mssgbox.setText("He won't be coming back!");
			} else if(projectileIntersectsWall(canvas))
			{
				setProjectileTraveling(false);
				mssgbox.setText("Oh wow");
			} else if(projectileIntersectsBall(canvas))
            {
				setProjectileTraveling(false);
                canvas.destroyBall();
                canvas.setResetCounter(); //reset on wait complete
            }
			else if(projectileIntersectsCann(cann.getPolygon(), cann.getMaxWheel())) {
				if( offset_y < -4)
				{
					setProjectileTraveling(false);
					cann.destroyCannon();
					canvas.updateBallScore();
					canvas.setResetCounter();
					mssgbox.setText("You killed yourself, fool!");
				}
			}
		}
		
		debounce = false;
	}
	
	public boolean projectileIntersectsWall(CanObj canvas) 				//if projectile intersects rectangle
	{
		
		boolean intersects = false;
		int i = 0;
		Vector<Rectangle> Walls = canvas.getWalls();
		
		while(i<Walls.size() && intersects == false)
		{
			if((Walls.elementAt(i).intersects(position))) {
				Walls.removeElementAt(i);
				intersects = true;
			}
			else
			{
				i++;
				intersects = false;
			}
		}
		return intersects;
	}
	
	public boolean projectileIntersectsCann(Polygon cann, Rectangle wheel )
	{
		boolean intersects = false;
		if(cann.intersects(position) || wheel.intersects(position))
		{
			intersects = true;
		}
		return intersects;
	}
	
	
	public boolean projectileIntersectsBall(CanObj canvas)					//if projectile intersects ball
    {
        boolean intersects = false;
        Rectangle ballTemp = canvas.getBall();
        if(ballTemp.intersects(position))
        {
            intersects = true;
        }
        else
        {
            intersects = false;
        }
        return intersects;
    }
}






class CanObj extends Canvas 
{
	private static final long serialVersionUID = 1L;

	boolean positionsetting = false;
	private Image Buffer;
	
	private Point Dir = new Point(1,1);
	private Point CurrPos = new Point(40,40);
	private Point LastPos = new Point(10,10);
	
	private int LastSize = 50;
	private int CurrSize = 50;
	private int width;
	private int height;
	private int moverate = 15;
	private int numsteps = 0;
	private int waitcounter = -1; //-1 = not waiting 
	
	private int numballscore = 0;
	private int numyourscore = 0;
	
	 
	private Cannon cannon = null;
	private Projectile projectile = null;
	
	private TextField ballscore;
	private TextField cannscore;
	private Label mssgbox;
	
	private boolean KillHim;
	private boolean addRect = false;
	
	private boolean timepaused = true;
	
	//=======================Variables for rectangles
	
	//===========Rectangle 

	private Rectangle newRect = new Rectangle(0, 0, 0, 0);
	
	private Vector<Rectangle> Walls = new Vector<Rectangle>();
	
	//==============================================
	
	CanObj(int width, int height, Insets insets,Cannon cannon,Projectile projectile, TextField ballscore, TextField cannscore, Label mssgbox) { //constructor
		this.projectile = projectile;
		this.cannon = cannon;
		this.mssgbox = mssgbox;
		this.ballscore = ballscore;
		this.cannscore = cannscore;
		
		this.setBackground(Color.lightGray);
		
		processScreenResize(width, height, insets);
	}
 

	

	//======================================
	//=======================PRIMARY METHODS
	//======================================
	
	public void paint(Graphics g)
    {
        LastPos = CurrPos;
        LastSize = CurrSize;
        
        Buffer = createImage(this.width, this.height);
        Graphics og = Buffer.getGraphics();
        
        projectile.drawProjectile(og);
        cannon.drawCannon(og);
        drawBall(og);
        drawRect(og);
        
        g.drawImage(Buffer, 0, 0, null);
        og.dispose();
    }
	
	public void updateBallScore()
	{
		numballscore++;
		ballscore.setText(String.valueOf(numballscore));
		mssgbox.setText("Oh crap, ball got a point!");
	}
	
	public void updateYourScore()
	{
		numyourscore++;
		cannscore.setText(String.valueOf(numyourscore));
		mssgbox.setText("And you got a point! Yay!");
	}
	public void resetScores()
	{
		numyourscore = 0;
		numballscore = 0;
		cannscore.setText(String.valueOf(numyourscore));
		ballscore.setText(String.valueOf(numballscore));
		
	}
		
	//======================================
	//=======================EVENT HANDLER METHODS
	//======================================
	
	public boolean getTimePaused() {
		if (waitcounter > 0 && timepaused) 
			return true;
		else
			return false;
	}
	public void setTimePaused(boolean paused) {
		timepaused = paused;
		projectile.setProjectilePaused(paused);
	}
	
	public void processTimeStep() {
		if (!timepaused && !waitingOnReset()) {
			stepBallPos();
			projectile.stepProjectile(width,height, cannon, this, mssgbox);
			
			paint(getGraphics());

			numsteps++; //increment # of steps (Keeps track of ball pos)
		}
	}
	
	public boolean getWaitingReset() {
		return waitcounter >= 0;
	}
	
	public boolean waitingOnReset() {
		boolean waiting = false;
		
		//System.out.println(waitcounter);
		
		if(waitcounter > 0) { //if we still have waiting to do, decrement counter
			waitcounter--;
			waiting = true;
		} else if (waitcounter == 0) { //if we've hit end of wait, reset the board, decrement once more
			resetBoard();
			waitcounter--;
			waiting = false;
		} 
		//waitingcounter rests at -1 when not waiting
		
		return waiting;
	}
	
	public void setResetCounter() {
		waitcounter = 2000;
	}
	
	public void resetBoard() {
		CurrPos = new Point(40,40);
		deleteWalls();
		cannon.fixCannon();
		KillHim = false;
	}
	
	public void processScreenResize(int framewidth, int frameheight, Insets insets)		//resizes canvas for screen
	{
		this.width = framewidth;// - (2*insets.left) - 10;
		this.height = frameheight;// - insets.top;
		cannon.positionCannon(framewidth,frameheight,insets);
		setCannonOrientation(cannon.getAngle());
		//this.setBounds(insets.left+5, insets.top+5, this.width, this.height);
	}
	
	public void setCannonOrientation(double theta) {
		for( int i = 0; i< Walls.size(); i++)
        {
            if(cannon.intersects(Walls.elementAt(i)))
            {
                Walls.removeElementAt(i);
            }
        }
		cannon.orientCannon(theta);
	}
	
	//======================================
	//=======================BALL METHODS
	//======================================
	
	public void destroyBall()
    {
        KillHim = true;
        updateYourScore();
    }
	
	public void stepBallPos() //sets position of the ball based on the Dir vector and the current position
    {
		if (numsteps >= moverate) {
			numsteps = 0;
			Point nextpos = new Point(CurrPos.x + Dir.x,CurrPos.y + Dir.y); //make predictive vector
			
	        if(nextpos.x - 1 < 0 || nextpos.x + CurrSize + 1 > width)
	            Dir.x *= -1;
	        
	        if(nextpos.y - 1 < 0 || nextpos.y + CurrSize + 1 > height)
	            Dir.y *= -1;
	        
	        checkWalls(nextpos,CurrSize);
	        if(cannon.intersects(new Rectangle(CurrPos.x, CurrPos.y, CurrSize, CurrSize)))
	        {
    			cannon.destroyCannon();
    			updateBallScore();
    			setResetCounter(); //reset on wait complete
	        };
	        
	        LastPos = CurrPos;
	        CurrPos = nextpos;
		}
    }
	
	public void drawBall(Graphics g)							//draws oval
	{
        if(!KillHim)
	    {
        	int size = CurrSize;
	        Point pos = CurrPos;
    		g.setColor(Color.orange);
			g.fillOval(pos.x, pos.y, size, size);
			g.setColor(Color.darkGray);
			g.fillOval(pos.x+1, pos.y+1, size-2, size-2);
	    }
	}
	
	public void setBallRate(int rate) {
		moverate = rate;
	}
	
	public Rectangle getBall() {
        Rectangle ball = new Rectangle(CurrPos.x,CurrPos.y,CurrSize,CurrSize);
        return ball;
    }
	
	
	public boolean setBallSize(int size) {				//resizes object based on sb
		boolean success = true;
		LastSize = CurrSize;
		
		if(size + CurrPos.x + 1 > width || size+CurrPos.y + 1 > height)			//ball size too big for SCREEN
		{
			success = false;
		}
		else {
	        Rectangle next = new Rectangle(CurrPos.x, CurrPos.y, size, size); //temporary rectangle to mirror the size of the ball after potential resize
	        int i = 0;
	        boolean hitflag = false;
	        while (i < Walls.size() && hitflag == false) //loop through walls
	        {
	            if(next.intersects(Walls.elementAt(i)))//if hit wall, say wall hit
	            	hitflag = true;
	            i++;
	        }
	        
	        if (hitflag) { //if wall hit by new size,
	        	success = false;
	        } else { //ball no hit no wall nor no SCREEN
	        	success = true;
                CurrSize = size;
            }
		}
		
		paint(this.getGraphics());
		return success;
	}
	
	public boolean checkForBall(Rectangle RectTemp)
	{
		boolean check = false;
		if(RectTemp.intersects(CurrPos.x, CurrPos.y, CurrSize, CurrSize))
		{
			//System.out.println("IN CHECK FOR BALL");
			check = true;
			paint(this.getGraphics());
		}
		
		return check;
	}
	
	
	//======================================
	//=======================WALL METHODS
	//======================================
	
	
	public void drawRect(Graphics g) //draws all existing walls & mouse-drawn rectangle
	{
		//System.out.println("In drawRect");
		
		g.setColor(Color.PINK);
		g.fillRect(newRect.x, newRect.y, newRect.width, newRect.height);
		int i = 0;
		int numwalls = Walls.size();
		while (i < numwalls)
		{	
			g.setColor(Color.PINK);
			g.fillRect(Walls.elementAt(i).x, Walls.elementAt(i).y, Walls.elementAt(i).width, Walls.elementAt(i).height);
			i++;
		}
		
		addRect = false;
	}
	
	
	public void checkWalls(Point nextpos,int size) //collision detection & handling of the ball between the walls & frame 
	{
        Rectangle nextposrect = new Rectangle(nextpos.x,nextpos.y,size,size);
        
        for(int i = 0;i<Walls.size();i++)
        {
            Rectangle Wall = Walls.elementAt(i);
            
            int ball_left = nextpos.x;
            int ball_right = ball_left + CurrSize;
            int ball_top = nextpos.y;
            int ball_bottom = nextpos.y+CurrSize;
            
            int wall_left = Wall.x;
            int wall_right = wall_left+Wall.width;
            int wall_top = Wall.y;
            int wall_bottom = wall_top+Wall.height;
            
            boolean IsLeft    = (ball_right   <=  wall_left+1);
            boolean IsRight   = (ball_left    >=  wall_right-1);
            boolean IsAbove   = (ball_bottom  <=  wall_top+1);
            boolean IsBelow   = (ball_top     >=  wall_bottom-1);
            
            if (nextposrect.intersects(Wall)) {
                
                if (IsLeft && Dir.x == 1) {
                    Dir.x = -1;
                } 
                if (IsRight && Dir.x == -1) {
                    Dir.x = 1;
                } 
                if (IsAbove && Dir.y == 1) {
                    Dir.y = -1;
                } 
                if (IsBelow && Dir.y == -1) {
                    Dir.y = 1;
                }
            }
        }
	}
	
	/*public boolean checkCannonCollision(Rectangle RectTemp)
	{
		boolean check = false;
		if(cannon.intersects(RectTemp)) {
			check = true;
			//paint(this.getGraphics());
		}
		
		return check;
	}*/
	
	
	public void setRectParameters(int X1, int Y1, int X2, int Y2) //As dragging rectangle, sets parameters of the newRect rectangle
	{
		int temp = 0;
		if(X1 > X2)
		{
			temp = X1;
			X1 = X2;
			X2 = temp;
			//System.out.println(X1+ " " +X2);
		}
		if(Y1 > Y2)
		{
			temp = Y1;
			Y1 = Y2;
			Y2 = temp;
			///System.out.println(Y1+ " " +Y2);
		}
		newRect.x = X1;
		newRect.y = Y1;
		newRect.width = Math.abs(X2-X1);
		newRect.height = Math.abs(Y2-Y1);
		//System.out.println(" WIDTH:" + newRect.width+ " " + "HEIGHT:" + newRect.height);
		
		
		addRect = true;
		paint(this.getGraphics());
	}
	
	//stores the rectangles
	
	public void storeRectangle()
	{
		Rectangle ZERO = new Rectangle (0,0,0,0);
		Rectangle RectTemp = new Rectangle(newRect);
		
		
		if(!cannon.intersects(RectTemp) && !checkForBall(RectTemp)&&intersectsWall(RectTemp) && RectTemp.getHeight() > 5 && RectTemp.getWidth() > 5)
		{
			if((RectTemp.y +RectTemp.height) >= height)
				RectTemp.setBounds(RectTemp.x,RectTemp.y,RectTemp.width,height-RectTemp.y);
			if((RectTemp.x +RectTemp.width) >= width)
				RectTemp.setBounds(RectTemp.x,RectTemp.y,width-RectTemp.x,RectTemp.height);
			loopAndCheck(RectTemp);
			Walls.add(RectTemp);
		}
		
		newRect.setBounds(0,0,0,0);
		
		if(intersectsWall(RectTemp) && !(RectTemp.equals(ZERO)))
		{
			
			paint(this.getGraphics());
		}
	}
	
	
	public void loopAndCheck(Rectangle RectTemp)
	{
		int i=0;
		
		while(i<Walls.size())
		{
			if((Walls.elementAt(i).equals(Walls.elementAt(i).intersection(newRect))))
			{
				Walls.removeElementAt(i);
			}
			else
			{
				i++;
			}
		}
	}
	
	//checks to see if the ball intersects walls
	public boolean intersectsWall(Rectangle RectTemp)
	{
		boolean check = true;
		int i = 0;
		while (i < Walls.size() && check == true)
		{
			if(RectTemp.intersection(Walls.elementAt(i)).equals(RectTemp))
			{
				check = false;
			}
			i++;
		}
		return check;
	}
	
	//deletes all walls
	public void deleteWalls() {
		//int i=0;
		Walls.removeAll(Walls);
	}
	
	//deletes rectangle at specified point
	public void deleteRectangle(Point clk) 
	{
		int i = 0;
		while(i < Walls.size())
		{
			if (Walls.elementAt(i).contains(clk))
			{
				Walls.removeElementAt(i);
			}
			else
			{
				paint(this.getGraphics());
				i++;
			}
		}
		addRect = true;
		paint(this.getGraphics());
		addRect = false;
	}
	
	public Vector<Rectangle> getWalls() {
		return Walls;
	}
}