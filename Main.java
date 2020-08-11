/*
 * Homework 3: Primitive Directory & Copier
Paul MacLean (MAC7537@calu.edu), Michael Gorse (GOR9632@calu.edu), Anthony Carrola (CAR3766@calu.edu)
Group 8 (2^3)
CET 350 - Technical Computer using Java
*/


import java.io.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.awt.List;
import java.awt.event.*;


class Main extends Frame implements ActionListener,WindowListener
{
	private static String updir = "..";
	
	private static File currentdir;
	private static File srcfile;
	private static File dstfile;
	private static String srcfilepath = null;
	
	private static int colormodifier = 0;
	
	private static final long serialVersionUID = 1L;

	private Button OKButton = new Button();
	private List DirList = new List(13);
	private Label SourceNameTip = new Label("Source: ",Label.RIGHT);
	private Button TargetButton = new Button();
	private Label MssgDisplay = new Label(null);
	private Label FileNameTip = new Label("File Name: ",Label.RIGHT);
	
	private Label SourceDisplay = new Label(null);
	private TextField TargetDisplay = new TextField(null);
	private TextField FileNameField = new TextField(null);
	
	
	
	private static int FRAME_X          = 800;
	private static int FRAME_Y          = 500;
	
	private static int GRIDCELLSX		= 10;
	private static int GRIDCELLSY		= 17;
	
	
	Main(String passeddir) {
		

		String default_dirname = System.getProperty("user.dir");				//Default directory
		String initial_dirname;													//Directory given to our Navigator constructor.
		
		if (passeddir == null) {
			initial_dirname = default_dirname;
		} else {
			initial_dirname = passeddir;
			File temp = new File(initial_dirname);
			
			if ( temp.exists() == false || temp.isDirectory() == false )		//check if input arg exists and is a directory
				initial_dirname = default_dirname;								//if not, set the initial name as the default directory name
		}
		
		
		
		
		int[] colWidth = new int[GRIDCELLSX];
		int[] rowWidth = new int[GRIDCELLSY];
		double colWeight[] = {1,1,1,1,1,1,1,1,1,1};
		double rowWeight[] = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		
		Arrays.fill(colWeight, 1);
		Arrays.fill(rowWeight, 1);
		
		GridBagConstraints GBConstr = new GridBagConstraints();
		GridBagLayout GBLayout = new GridBagLayout();
		
		GBLayout.columnWeights = colWeight;
		GBLayout.rowWeights = rowWeight;
		GBLayout.columnWidths = colWidth;
		GBLayout.rowHeights = rowWidth;
		
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = GRIDCELLSX;
		GBConstr.gridheight = GRIDCELLSY-5;
		GBConstr.gridx = 0;
		GBConstr.gridy = 0;
		GBConstr.fill = GridBagConstraints.BOTH;
		
		this.setLayout(GBLayout);
		
		
		GBLayout.setConstraints(DirList,GBConstr);
		DirList.setBackground(new Color(0,240,240));
		DirList.setForeground(new Color(20,20,20));
		DirList.setVisible(true);
		this.add(DirList);
		DirList.addActionListener(this);
		
		//sourcename
		
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = GRIDCELLSX-10;
		GBConstr.gridy = GRIDCELLSY-4;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(SourceNameTip,GBConstr);
		SourceNameTip.setText("Source: ");
		SourceNameTip.setForeground(new Color(20,90,20));
		SourceNameTip.setBackground(new Color(20,200,20));
		SourceNameTip.setVisible(true);
		this.add(SourceNameTip);
		
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Source name
		
		//source text
		
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = GRIDCELLSX-2;
		GBConstr.gridheight = 1;
		GBConstr.gridx = GRIDCELLSX-9;
		GBConstr.gridy = GRIDCELLSY-4;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(SourceDisplay, GBConstr);
		SourceDisplay.setForeground(new Color(255,255,20));
		SourceDisplay.setBackground(new Color(140,70,70));
		SourceDisplay.setVisible(true);
		this.add(SourceDisplay);
		
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Source text
		
		//TargetButton
		
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = GRIDCELLSX-10;
		GBConstr.gridy = GRIDCELLSY-3;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(TargetButton, GBConstr);
		TargetButton.setBackground(new Color(200,240,20));
		TargetButton.setForeground(new Color(120,60,60));
		TargetButton.setLabel("Target");
		this.add(TargetButton);
		TargetButton.addActionListener(this);
				
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~TargetButton
				
		//Target Display Name
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = GRIDCELLSX-2;
		GBConstr.gridheight = 1;
		GBConstr.gridx = GRIDCELLSX-9;
		GBConstr.gridy = GRIDCELLSY-3;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(TargetDisplay, GBConstr);
		TargetDisplay.setBackground(new Color(200,240,200));
		TargetDisplay.setText(null);
		this.add(TargetDisplay, GBConstr);
		TargetDisplay.addActionListener(this);
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ TargetDisplay Name
				
		//File Name Tip
				
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = GRIDCELLSX-10;
		GBConstr.gridy = GRIDCELLSY-2;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(FileNameTip,  GBConstr);
		FileNameTip.setBackground(new Color(20,20,220));
		FileNameTip.setForeground(new Color(245,245,35));
		this.add(FileNameTip, GBConstr);
				
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~FileNameTip
				
		//FileNameField
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = GRIDCELLSX-2;
		GBConstr.gridheight = 1;
		GBConstr.gridx  = GRIDCELLSX-9;
		GBConstr.gridy = GRIDCELLSY-2;
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(FileNameField,  GBConstr);
		FileNameField.setVisible(true);
		FileNameField.addActionListener(this);
		this.add(FileNameField, GBConstr);
		
		
		//Mssg Display
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = GRIDCELLSX;
		GBConstr.gridheight = 1;
		GBConstr.gridx = 0;
		GBConstr.gridy = GRIDCELLSY-1; 
		GBConstr.fill = GridBagConstraints.BOTH;
		GBLayout.setConstraints(MssgDisplay, GBConstr);
		this.add(MssgDisplay);
		MssgDisplay.setText("2^3 gang welcomes Old Man to the playing field");
		
				
		//OKBUTTON
		
		GBConstr.weightx = 1;
		GBConstr.weighty = 1;
		GBConstr.gridwidth = 1;
		GBConstr.gridheight = 1;
		GBConstr.gridx = GRIDCELLSX-1;
		GBConstr.gridy = GRIDCELLSY-2;
		GBConstr.fill = GridBagConstraints.BOTH;
		
		GBLayout.setConstraints(OKButton,GBConstr);
		DirList.setBackground(new Color(0,240,240));
		DirList.setForeground(new Color(20,20,20));
		DirList.setVisible(true);
		this.add(OKButton);
		OKButton.setLabel(" OK ");
		OKButton.addActionListener(this);
				
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~OKBUTTON
		
		
		this.setSize(FRAME_X, FRAME_Y);													//OFFSET 32Y
		this.setVisible(true);
		this.setResizable(true);
		this.setTitle(initial_dirname);
		
		this.addWindowListener(this);
		this.requestFocus();
		
		
		setCurrentDirectory(initial_dirname);
		populateList();
		
	}
	
	public static void main(String[] args) throws IOException
	{
		if (args.length == 0)
			new Main(null);						//Window variable may be unused any further.
		else
			new Main(args[0]);						//Window variable may be unused any further.
	}
	
	
	
	
	
	
	
	
	
	
	
	public void printMssg(String mssg)
	{
		 MssgDisplay.setText(mssg);
	}
	
	
	public void clearMssg()
	{
		MssgDisplay.setText(null);
	}

	private void clearList() {
		DirList.removeAll();
	}
	
	private void setCurrentDirectory(String fullfilename) {
		if (fullfilename.equals(updir)) {
			String parentname = currentdir.getAbsoluteFile().getParent();
			File parent = new File(parentname);
			
			if (parent.exists())
				currentdir = parent;
		} else
		{
			currentdir = new File(fullfilename);
		}
		this.setTitle(currentdir.getAbsolutePath());
	}
	
	private boolean hasChildDirectory(File file) {
		if(file.isDirectory())
		{
			File[] children = file.listFiles();
			if(children!=null)
			{
				for(File subchild : children) //loop through all child files of given directory
					if (subchild.isDirectory())
						return true;
			}
		}
		return false;
	}
	
	private void populateList() {
		clearList();
		
		boolean hasParent = currentdir.getParentFile() != null;
		if (hasParent)
			DirList.add("..");
		
		
		File[] children = currentdir.listFiles();
		if(children!=null)
		{
			for(File child : children) //loop through all child files of currentdir
			{
				String childname = child.getName();
				if (hasChildDirectory(child))
					childname = childname + " + ";
				DirList.add(childname);
			}
		}
	}
	
	private void listElementClicked(String elementvalue) {
		if (elementvalue.matches(".* [+] "))
			elementvalue = elementvalue.substring(0,elementvalue.length()-3);			//remove plus signs from any directories (only in program data; UI remains unchanged)
		
		
		String selectedfilename = currentdir.getAbsolutePath() + "\\" + elementvalue;
		File temp = new File(selectedfilename);
		
		if (elementvalue.equals(updir))
		{
			printMssg("Directory selected (Last: ..)");
			setCurrentDirectory("..");
			populateList();
			return;
		}
		
		
		if (temp.exists())
		{
			if (temp.isDirectory())
			{
				printMssg("Directory selected");
				setCurrentDirectory(selectedfilename);
				populateList();
			}
			else if (temp.isFile())
			{
				setSource(temp);
			}
		} else
			printMssg("An item was selected but it didn't have a recognized file name. Fascinating");
		
	}
	
	private void setSource(File newsource) 
	{
		srcfile = newsource;
		if(srcfile.exists())
		{
			srcfilepath = srcfile.getAbsolutePath();
			SourceDisplay.setText(srcfilepath);
			printMssg("Source set");
		} else
			printMssg("Source file invalid");
	}
	
	private void targetClicked() {
		if(srcfile != null && srcfile.exists())
		{
			printMssg("Target set");
			TargetDisplay.setText(currentdir.getAbsolutePath());
			dstfile = currentdir;
		} else
			printMssg("Source file not selected");
	}

	private void setTargetFromField(String newtarget) {
		File temp = new File(newtarget);
		if (srcfile != null && srcfile.exists()) {
			if (temp.exists()) {
				dstfile = temp;
				TargetDisplay.setText(newtarget);
				printMssg("Target set");
			} else
			{
				TargetDisplay.setText("");
				printMssg("Entered target file doesn't exist");
			}
		} else
		{
			TargetDisplay.setText("");
			printMssg("Source file not selected");
		}
	}
	
	private void OKClicked() throws IOException 
	{
		if (srcfile != null && srcfile.exists())
		{
			if (dstfile != null && dstfile.exists())
			{
				String dupname = dstfile.getAbsoluteFile() + "\\" + FileNameField.getText();
				File duplicate = new File(dupname);
		
					CopyFile(duplicate);
					SourceDisplay.setText("");
					FileNameField.setText("");
					TargetDisplay.setText("");
					srcfile = null;
					dstfile = null;
					populateList();
					printMssg("File copied");

			} 
			else
			{
				printMssg("Target directory invalid");
			}
		} else
			printMssg("Source file invalid");
	}
	
	private static void CopyFile(File duplicate) throws IOException //should work copied almost directly from program 2 backupfile function
	{
		String otpfstring = null;
		BufferedReader fileread  = new BufferedReader(new FileReader(srcfile));
		
		duplicate.createNewFile();
		BufferedWriter filewrite = new BufferedWriter (new FileWriter(duplicate));
		while ((otpfstring = fileread.readLine()) != null)
			{
				filewrite.write(otpfstring);
				filewrite.newLine();
			}
		
		filewrite.close();
		fileread.close();
	}
	


	public void windowActivated(WindowEvent e) {
		
	}

	public void windowClosed(WindowEvent e) {
		System.exit(0);
	}

	public void windowClosing(WindowEvent e) {
		this.removeWindowListener(this);
		this.dispose();
	}

	public void windowDeactivated(WindowEvent e) {
		
	}

	public void windowDeiconified(WindowEvent e) {
		
	}

	public void windowIconified(WindowEvent e) {
		
	}

	public void windowOpened(WindowEvent e) {
		
	}

	public void actionPerformed(ActionEvent e) {
		//color button
		colormodifier = colormodifier + 15;
		OKButton.setBackground(new Color(((colormodifier + 85) % 255),((colormodifier + 170) % 255),((colormodifier) % 255)));
		
		//clear output message
		clearMssg();
		
		Object s = e.getSource();
		if (s == DirList)
		{
			listElementClicked(DirList.getSelectedItem());
		} else if (s == TargetButton)
		{
			targetClicked();
		} else if (s == OKButton)
		{
			try 
			{
				OKClicked();
			} 
			catch (IOException f) 
			{
				printMssg("File name invalid");
			}
		} else if (s == FileNameField)
		{
			try {
				OKClicked();
			} catch (IOException f) {
				f.printStackTrace();
			}
		} else if (s == TargetDisplay)
		{
			setTargetFromField(TargetDisplay.getText());
		}
	}
}




