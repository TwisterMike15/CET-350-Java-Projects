/*
 * Homework 7: Chat
Paul MacLean (MAC7537@calu.edu), Michael Gorse (GOR9632@calu.edu), Anthony Carrola (CAR3766@calu.edu)
Group 8 (2^3)
CET 350 - Technical Computer using Java
*/

import java.io.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import java.lang.*;					//math stuff should be included in lang
import java.awt.List;
import java.awt.event.*;

import java.net.Socket;
import java.net.SocketException;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;


public class Chat implements Runnable, ComponentListener, WindowListener, ActionListener, KeyListener, ItemListener
{
	private Frame chat = new Frame();
	Frame ColorFrame;
	
	//objects used on the frame
	private TextArea dialoguebox;
	
	private TextField messageField;
	private Button sendButton;
	
	private Label hostLabel;
	private TextField hostField;
	private Button hostChangeButton;
	private Button connectButton;
	private Button disconnectButton;
	
	private Label portLabel;
	private TextField portField;
	private Button portChangeButton;
	private Button startButton;

	private TextArea output;
	
	//objects used for network communication
	private Socket client;
	private Socket server;
	private ServerSocket listen_socket; //Who the client is talking to
	
	private BufferedReader reader;
	private InputStreamReader inputstr;
	private PrintWriter prwriter;
	
	//thread
	private Thread SteppingThread;
	
	//menu related objects
	private Menu Colors;
	private MenuBar MB;
	private Checkbox none, red, blue, green, orange, pink;
	
	//extra data needed for proper communication
	private int timeout = 500;
	
	private boolean canSend = false;
	private int portnum = 44004;
	private String hostname = "127.0.0.1";
	
	private boolean connectionMade = false;
	
	//The possible states of the program
	enum States { 
	    Initial, Client, Server; 
	}
	
	//The possible buttons
	enum ButtonEnums { 
	    HostChange, PortChange, Send, StartServer, Connect, Disconnect;
	}
	
	//The possible fields
	enum FieldEnums {
		HostField, PortField, MessageField;
	}
	
	//The state the program is currently in
	volatile States chatState = States.Initial; 
	
//---------------------------------------------------------
	
	
	//Constructor
	Chat(int timeout)
	{
		initComponents();
		setState(States.Initial);
	}
	
	
	//initializes all components
	private void initComponents()
	{
		
		dialoguebox = new TextArea();
		messageField = new TextField("");
		sendButton = new Button("Send");
		hostLabel = new Label("Host:");
		hostField = new TextField("127.0.0.1");
		hostChangeButton = new Button("Change Host");
		startButton = new Button("Start Server");
		portLabel = new Label("Port:");
		portField = new TextField("44004");
		portChangeButton = new Button("Change Port");
		connectButton = new Button("Connect");
		disconnectButton = new Button("Disconnect");
		output = new TextArea();
		
		//MENU STUFFFF-------------------------------------
		MB = new MenuBar();							//main menu bar
		
		Colors = new Menu("Colors");
		MenuItem colorframe = new MenuItem("ColorBoard");
			
				
		
		MB.add(Colors);
		chat.setMenuBar(MB);
		Colors.add(colorframe);		
		Colors.addActionListener(this);		
		
		//--------------------------------------------------------
		
		chat.setBounds(50, 50 , 800, 500);												//OFFSET 32Y
		chat.setMinimumSize(new Dimension(700, 400));
		chat.setResizable(true);
		
		positionComponents();
		
		chat.addComponentListener(this);
		chat.addWindowListener(this);
		chat.setVisible(true);
		
		messageField.requestFocus();
	}
	
	//positions components on the frame
	private void positionComponents() {
		
		
		//Gridbag stuff
		GridBagConstraints GBConstr = new GridBagConstraints();					// creates new grid bag constraints
		GridBagLayout GBLayout = new GridBagLayout();							// creates new grid bag layout
		
		int[] colWidth = {1,1,1,1,1,1,1,1,1,1,1,1,1};							//16
		int[] rowWidth = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		
		double colWeight[] = {1,1,1,1,1,1,1,1,1,1,1,1,1};						//weight to cols
		double rowWeight[] = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};								//weight to rows
		
		GBLayout.columnWeights = colWeight;
		GBLayout.rowWeights = rowWeight;
		GBLayout.columnWidths = colWidth;
		GBLayout.rowHeights = rowWidth;
		
		//dialoguebox TextArea
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 17;
		GBConstr.gridheight = 2;
		GBConstr.gridx = 0;
		GBConstr.gridy = 0;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(dialoguebox, GBConstr);
		
		dialoguebox.setVisible(true);
		dialoguebox.setEditable(false);
		chat.add(dialoguebox);
		
		
		//message Box TextField
		
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 15;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 0;
		GBConstr.gridy = 8;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(messageField, GBConstr);
		
		messageField.addKeyListener(this);
		messageField.setVisible(true);
		chat.add(messageField);
		
		
		
		//Send Button
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridx = 16;
		GBConstr.gridy = 8;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(sendButton, GBConstr);
		
		sendButton.setVisible(true);
		chat.add(sendButton);
		sendButton.setEnabled(false);
		sendButton.addActionListener(this);
	
		//Host Label
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 1;
		GBConstr.gridy = 9;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(hostLabel, GBConstr);
		
		hostLabel.setVisible(true);
		chat.add(hostLabel);
		
		
		//Host Box
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 9;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 2;
		GBConstr.gridy = 9;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(hostField, GBConstr);
		
		hostField.addKeyListener(this);
		hostField.setVisible(true);
		chat.add(hostField);
		
		
		//Host Change Button
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 14;
		GBConstr.gridy = 9;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(hostChangeButton, GBConstr);
		
		hostChangeButton.setVisible(true);
		chat.add(hostChangeButton);
		hostChangeButton.setEnabled(true);
		hostChangeButton.addActionListener(this);
		
		//Start Button 
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 16;
		GBConstr.gridy = 9;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(startButton, GBConstr);
		
		startButton.setVisible(true);
		chat.add(startButton);
		startButton.setEnabled(true);
		startButton.addActionListener(this);
		
		
		//port Label
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 1;
		GBConstr.gridy = 10;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(portLabel, GBConstr);
		
		portLabel.setVisible(true);
		chat.add(portLabel);

		
		//port box
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 9;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 2;
		GBConstr.gridy = 10;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(portField, GBConstr);
		
		portField.addKeyListener(this);
		portField.setVisible(true);
		chat.add(portField);
		
		//change port button
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 14;
		GBConstr.gridy = 10;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(portChangeButton, GBConstr);
		
		portChangeButton.setVisible(true);
		chat.add(portChangeButton);
		portChangeButton.setEnabled(true);
		portChangeButton.addActionListener(this);
		
		// connect button
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 16;
		GBConstr.gridy = 10;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(connectButton, GBConstr);
		
		connectButton.setVisible(true);
		chat.add(connectButton);
		connectButton.addActionListener(this);
		
		// disconnect button
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 16;
		GBConstr.gridy = 11;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(disconnectButton, GBConstr);
		
		disconnectButton.setVisible(true);
		chat.add(disconnectButton);
		disconnectButton.setEnabled(false);
		disconnectButton.addActionListener(this);
		
		//message field at bottom of screen
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 17;
		GBConstr.gridheight = 2;
		GBConstr.gridx = 0;
		GBConstr.gridy = 12;
		GBConstr.anchor = GridBagConstraints.CENTER;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(output, GBConstr);
		
		output.setVisible(true);
		chat.add(output);
		output.setEnabled(false);
		
		chat.setLayout(GBLayout);
		
	}

	public static void main(String[] args)
	{
		int timeout = 0;
		
		if(args.length > 0)
		{
			try
			{
				timeout = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e)
			{
				timeout = 3;
			}
		}
		new Chat(timeout);
	}
	
	//starts the thread
	public void start()
	{
		if(SteppingThread == null)
		{
			SteppingThread = new Thread(this);
			SteppingThread.start();
		}
	}
	
	//the main thread loop. waits for input from the other program
	public void run()
	{
		SteppingThread.setPriority(Thread.MAX_PRIORITY);
		boolean more = false;
		String message;
		
		do {
			more = false;
			
			try {
				message = reader.readLine();
				if( message != null && !message.isEmpty() )
				{
					recvMessage(message);
					more = true;
				}
			} catch (IOException e) {}
			
		} while(more);
		
		setInitialState();

	 }
	
	//ends the program
	public void stop()
	{
		try {
			SteppingThread.setPriority(Thread.MIN_PRIORITY);
		} catch(NullPointerException e) {}
		chat.removeWindowListener(this);
		System.exit(0);
		chat.dispose();
	}
	
	//Function called if the user selects to be a server
	private void serverFunc()
	{
		dispOutput("Starting server...");
		
		setState(States.Server);
		messageField.requestFocus();
		
		boolean connected = false;
		
		try {
			listen_socket = new ServerSocket(portnum);
			
			try {
				dispOutput("Waiting on connection...");
				
				listen_socket.setSoTimeout(timeout*10);
				client = listen_socket.accept();
				
				dispOutput("Connected");
				
				try{
					inputstr = new InputStreamReader(client.getInputStream());
					reader = new BufferedReader(inputstr);
					prwriter = new PrintWriter(client.getOutputStream());
					
					connected = true;
				} catch (SocketTimeoutException s) {
					dispOutput("Server startup failed; writer/reader initialization failure");
				}
			} catch (IOException e1) {
				dispOutput("Server startup failed; connection timeout");
			}
		} catch (IOException e1) {
			dispOutput("Server startup failed; port occupied");
		}
		
		if(connected) {
			dispOutput("Connection made to client. Say hello!");
			connectionMade = true;
			start();
			updateElementStates();
		} else {
			setInitialState();
		}
	}
	
	//function called if a user decides to be a client
	private void clientFunc()
	{
		dispOutput("Starting server...");
	
		setState(States.Client);
		boolean connected = false;

		messageField.requestFocus();
		
		try {
			InetSocketAddress address = new InetSocketAddress(hostname, portnum);
			server = new Socket();
			
			dispOutput("Waiting on connection...");
			server.connect(address,timeout);
			dispOutput("Connected");
			
			try {
				inputstr = new InputStreamReader(server.getInputStream());
				reader = new BufferedReader(inputstr);
				prwriter = new PrintWriter(server.getOutputStream());
				
				connected = true;
			} catch(Exception e) {
				dispOutput("Client startup failed; buffer error");
			}
		} catch (Exception e) {
			dispOutput("Client startup failed; connection timeout");
		}
		
		if (connected) {
			connectionMade = true;
			start();
			updateElementStates();
			dispOutput("Connection made to server. Say hello!");
		}  else {
			setInitialState();
		}
	}
	
	//function called to set the state the program is in
	private void setState(States state) {
		chatState = state;
		
		switch (chatState) {
			case Client:
				chat.setTitle("Client");
				break;
			case Server:
				chat.setTitle("Server");
				break;
			case Initial:
				chat.setTitle("You Are Nothing");
				break;
		}
	}

	//called to set the initial state or to return to the initial state
	private void setInitialState() {
		reader = null;
		prwriter = null;
		inputstr = null;
		
		//Close any and all existing sockets
		try {
			if(chatState == States.Client) {
				server.close();
			}
			else if(chatState == States.Server) {
				listen_socket.close();
				client.close();
			}
		} catch (Exception e) {}
		
		server = null;
		listen_socket = null;
		client = null;
		
		SteppingThread = null;
		
		setState(States.Initial);
		if(connectionMade==true)
		{
			dispOutput("Connection Ended");
		}
		else if(connectionMade==false)
		{
			dispOutput("Failed to connect");
		}
		connectionMade = false;
		canSend = false;
		
		updateElementStates();
	}
	
	//Sends a disconnect signal to a connected program
	private void sendDisconnectSignal() {
		//Attempt to send a shutdown signal to connected brother
		try {
			prwriter.println("");
			prwriter.flush();
		} catch(Exception e) {}
		
		try {
			SteppingThread.interrupt();
		} catch (Exception e) {}
	}
	
	//Displays an output message in the lower textarea
	private void dispOutput(String message) {
		output.setText(message + "\n");
	}
	
	//Displays client and server messages to the upper textarea
	private void dispMessage(String message) {
		String prefix = "[System]: ";
		if(chatState == States.Client)
		{
			prefix = "[Client]: ";
		}
		else if(chatState == States.Server)
		{
			prefix = "[Server]: ";
		}
		
		dialoguebox.append(prefix + message + "\n");
	}
	
	//recieves and prints messages
	private void recvMessage(String message) {
		String prefix = "[System]: ";
		if(chatState == States.Client)
		{
			prefix = "[Server]: ";
		}
		else if(chatState == States.Server)
		{
			prefix = "[Client]: ";
		}
		
		dialoguebox.append(prefix + message + "\n");
	}

	//sends messages
	private void sendMessage() {
		String message = null;
		message = messageField.getText();
		if(chatState != States.Initial)
		{
			if( !message.equals(""))
			{
				dispMessage(message);
				prwriter.println(message);
				prwriter.flush();
				messageField.setText("");
			}
		}
	}

	//determines whether or not the fields meet the specified criteria to be entered as input
	private boolean fieldSatisfied(FieldEnums field) {
		boolean satisfied = false;
		
		switch(field) {
			case HostField:
				String hosttext = hostField.getText();
				
				if ( !hosttext.isEmpty() && hosttext.matches("[0-9.]*") ) {
					satisfied = true;
				}
				break;
				
			case PortField:
				String porttext = portField.getText();
				
				if ( !porttext.isEmpty() && porttext.matches("[0-9]*") ) {
					satisfied = true;
				}
				break;
				
			case MessageField:
				String text = messageField.getText();
				
				satisfied = !text.isEmpty();
				break;
				
			default:
		}
		
		return satisfied;
	}
	
	//updates the states of the elements
	private void updateElementStates() {
		setButtonEnabled(hostChangeButton);
		setButtonEnabled(portChangeButton);
		setButtonEnabled(sendButton);
		setButtonEnabled(startButton);
		setButtonEnabled(connectButton);
		setButtonEnabled(disconnectButton);
	}
	
	//sets the color of the text on the upper textarea
	private void setColor() {
		if (none.getState())
		{
			dialoguebox.setForeground(Color.BLACK);
		}
		else if (red.getState())
		{
			dialoguebox.setForeground(Color.RED);
		}
		else if (blue.getState())
		{
			dialoguebox.setForeground(Color.BLUE);
		}
		else if (green.getState())
		{
			dialoguebox.setForeground(Color.GREEN);
		}
		else if (orange.getState())
		{
			dialoguebox.setForeground(Color.ORANGE);
		}
		else if (pink.getState())
		{
			dialoguebox.setForeground(Color.PINK);
		}
	}
	
	//sets the specific button states
	private void setButtonEnabled(Button button) {
		boolean state = false;
		boolean isInitial = chatState.equals(States.Initial);
		boolean isClient = chatState.equals(States.Client);
		boolean isServer = chatState.equals(States.Server);
		
		
	    if (button == hostChangeButton) {
			if ( fieldSatisfied(FieldEnums.HostField) && chatState == States.Initial)
				state = true;
			
		} else if ( button == portChangeButton ) {
			
			if ( fieldSatisfied(FieldEnums.PortField) && chatState == States.Initial)
				state = true;
			
		} else if ( button == sendButton ) {
			
			if ( isClient || isServer ) {
				state = true;	
				canSend = true;
			}
			
		} else if ( button == startButton ) {
			
			if ( isInitial )
				state = true;
			
		} 
	    
		else if ( button == connectButton ) {
			
			if ( isInitial )
				state = true;
			
		} else if ( button == disconnectButton ) {
			
			if ( isClient || isServer)
				state = true;
			
			
		}
		
		button.setEnabled(state);
	}

	
	//Window Closed-- called when the frame is closed
	public void windowClosed(WindowEvent e) {
		Frame frame = (Frame)e.getSource();
		
		if (frame.equals(ColorFrame)) {
			ColorFrame.removeWindowListener(this);		
			ColorFrame.dispose();
			ColorFrame = null;
		} else
		{
			stop();
		}
	}
	
	//window Closing-- called when the frame is closing
	public void windowClosing(WindowEvent e) {
		Frame frame = (Frame)e.getSource();
		
		if (frame.equals(ColorFrame)) {
			ColorFrame.removeWindowListener(this);	
			ColorFrame.dispose();	
			ColorFrame = null;
		} else
		{
			stop();
		}
	}
	
	//changes the host
	public void changeHost()
	{
		hostname = hostField.getText();
		dispOutput("Host Changed to: " + hostname);
	}
	
	//changes the port
	public void changePort()
	{
		portnum = Integer.valueOf(portField.getText());
		dispOutput("Port Chnaged to: " + String.valueOf(portnum));
	}
	
	//Window Activated-- called when the window is activated
	public void windowActivated(WindowEvent e) {
		messageField.requestFocus();
	}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {
		messageField.requestFocus();
	}
	
	//used to change the color of text and to see what buttons are pressed
	public void actionPerformed(ActionEvent e) 
	{
		Object obj = e.getSource();
		if(obj == startButton && chatState == States.Initial) {
			serverFunc();
		}
		else if(obj == connectButton && chatState == States.Initial)
		{
			clientFunc();
		}
		else if(obj == disconnectButton) {
			sendDisconnectSignal();
		}
		else if(obj == sendButton) {
			sendMessage();
		}
		else if (obj == hostChangeButton ){
			changeHost();
		}
		else if (obj == portChangeButton ){
			changePort();
		}
		else if (obj == Colors)			
		{
			if (ColorFrame == null) {
				ColorFrame = new Frame();
				
				ColorFrame.setLayout(new GridLayout(0,1));
				
				CheckboxGroup group = new CheckboxGroup();
				none = new Checkbox("Black", group, true);
				none.addItemListener(this);
				red = new Checkbox("RED", group, false);
				red.addItemListener(this);
				blue = new Checkbox("BLUE", group, false);
				blue.addItemListener(this);
				green = new Checkbox("GREEN", group, false);
				green.addItemListener(this);
				orange = new Checkbox("ORANGE", group, false);
				orange.addItemListener(this);
				pink = new Checkbox("PINK", group, false);
				pink.addItemListener(this);
				
				ColorFrame.setVisible(true);
				ColorFrame.setSize(200, 200);
				ColorFrame.add(none);
				ColorFrame.add(red);
				ColorFrame.add(blue);
				ColorFrame.add(green);
				ColorFrame.add(orange);
				ColorFrame.add(pink);
				
				ColorFrame.addComponentListener(this);
				ColorFrame.addWindowListener(this);
			}
		}
	messageField.requestFocus();
	}

	
	public void adjustmentValueChanged(AdjustmentEvent e) {}
	public void itemStateChanged (ItemEvent e) {
		setColor();
	}
	
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}

	//determines when the enter key is typed and accepts or denies input appropriately
	public void keyTyped(KeyEvent e) {
		
		Object obj = e.getKeyChar();
		String temp = obj.toString();
		Object pizza = e.getSource();
		String temp2 = pizza.toString();
	    if(temp.contentEquals("\n"))
		{
    		if(temp2.contains(hostField.getText()) && canSend == false && fieldSatisfied(FieldEnums.HostField))
    		{
    			changeHost();
    		}
    		else if (temp2.contains(portField.getText()) && canSend == false && fieldSatisfied(FieldEnums.PortField))
    		{
    			changePort();
    		}
    		else if(temp2.contains(messageField.getText()) && canSend == true)
    		{
    			sendMessage();
    		}
    		messageField.requestFocus();
		}
	}
	
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {
		Object inpfield = e.getSource();
		
		//Only show start button if there is something to send
		if (inpfield == hostField) {
			setButtonEnabled(hostChangeButton);
		//Only show connect button if there is something to send
		} else if (inpfield == portField) {
			setButtonEnabled(portChangeButton);
		//Only show send button if there is something to send
		} else if (inpfield == messageField) {
			setButtonEnabled(sendButton);
		}
		
	}
}