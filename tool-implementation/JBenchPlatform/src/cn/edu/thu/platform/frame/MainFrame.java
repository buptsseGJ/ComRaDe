package cn.edu.thu.platform.frame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.w3c.dom.Document;

import cn.edu.thu.platform.entity.Reports;
import cn.edu.thu.platform.parser.DomToEntity;
import cn.edu.thu.platform.parser.ParseXml;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;

/**
 * main functions： 1) read benchmark suite 2) manage benchmark suite 3) run programs
 * 
 */
public class MainFrame extends JFrame {

	public String fileAbsolutePath;
	private JButton readSuites = new JButton("Import");
	private JButton manageSuites = new JButton("Manage");
	private JButton runProgram = new JButton("About ");
	private JButton scriptFile = new JButton("Run&Compare");
	private final JPanel information = new JPanel();
	private final JLabel lblRunUsecases = new JLabel(" Run test cases");
	File file = null;
	public TextArea textArea = new TextArea("",50, 1, TextArea.SCROLLBARS_BOTH);
	public String textAreaInfo = "";

	public MainFrame() {
		String os = System.getProperty("os.name");  
		SelectScriptFrame.os = os.toLowerCase();
		System.out.println(os);
		setResizable(false);
		getContentPane().setFont(new Font("Century Gothic", Font.PLAIN, 22));
		JPanel p1 = new ButtonPanel();
		p1.setLayout(null);
		p1.setBounds(0, 0, 428, 533);
		getContentPane().setLayout(null);
		getContentPane().add(p1);

		JLabel label = new JLabel("Benchmarks");
		label.setOpaque(true);
		label.setBounds(24, 29, 105, 21);
		p1.add(label);
		label.setEnabled(false);
		label.setBackground(UIManager.getColor("menu"));
		label.setFont(new Font("Century Gothic", Font.PLAIN, 17));

		JPanel benchmarks = new JPanel();
		benchmarks.setFont(new Font("Century Gothic", Font.PLAIN, 22));
		benchmarks.setBorder(new TitledBorder(new LineBorder(new Color(211, 211, 211)), "Benchmarks", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(64, 64, 64)));		
		benchmarks.setBounds(15, 29, 161, 153);
		p1.add(benchmarks);
		benchmarks.setLayout(null);
		manageSuites.setBounds(15, 92, 127, 37);
		manageSuites.setFocusable(false);
		benchmarks.add(manageSuites);
		manageSuites.setForeground(Color.DARK_GRAY);
		manageSuites.setFont(new Font("Century Gothic", Font.PLAIN, 22));
		manageSuites.setEnabled(false);
		readSuites.setBounds(15, 40, 127, 37);
		benchmarks.add(readSuites);
		readSuites.setBackground(Color.WHITE);
		readSuites.setForeground(Color.DARK_GRAY);
		readSuites.setFont(new Font("Century Gothic", Font.PLAIN, 22));
		readSuites.setFocusable(false);
		lblRunUsecases.setOpaque(true);
		lblRunUsecases.setEnabled(false);
		lblRunUsecases.setBounds(211, 29, 132, 21);
		p1.add(lblRunUsecases);
		lblRunUsecases.setFont(new Font("Century Gothic", Font.PLAIN, 17));
		lblRunUsecases.setBackground(SystemColor.menu);
		runProgram.setBounds(219, 141, 164, 37);
		p1.add(runProgram);
		runProgram.setFocusable(false);
		runProgram.setEnabled(false);
		runProgram.setForeground(Color.DARK_GRAY);
		runProgram.setFont(new Font("Century Gothic", Font.PLAIN, 22));
		runProgram.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Run programs ...");
				try {
					String command = "java -cp E:\\courseResource\\programResearch\\benchmark相关\\benchmarks汇总\\calfuzzer_calfuzzer\\CalFuzzer_TestRace1\\bin benchmarks.testcases.TestRace1";
					Process proc = Runtime.getRuntime().exec(command);
					BufferedInputStream in = new BufferedInputStream(proc
							.getInputStream());
					BufferedReader inBr = new BufferedReader(
							new InputStreamReader(in));
					String lineStr;
					while ((lineStr = inBr.readLine()) != null)
					if (proc.waitFor() != 0) {
						if (proc.exitValue() == 1)
							System.err.println("Command execution failed!");
					}
					// inBr.close();
					// in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		JPanel programs = new JPanel();
		programs.setFont(new Font("Century Gothic", Font.PLAIN, 22));
		programs.setBounds(204, 39, 193, 89);
		p1.add(programs);
		programs.setLayout(null);
		scriptFile.setMargin(new Insets(2, 0, 2, 0));
		scriptFile.setFocusable(false);
		scriptFile.setEnabled(false);
		scriptFile.setBounds(15, 30, 164, 37);
		programs.add(scriptFile);
		scriptFile.setForeground(Color.DARK_GRAY);
		scriptFile.setFont(new Font("Century Gothic", Font.PLAIN, 21));
		programs.setBorder(new TitledBorder(new LineBorder(new Color(211, 211, 211)), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(64, 64, 64)));
		information.setBorder(new LineBorder(SystemColor.scrollbar));
		information.setFont(new Font("Century Gothic", Font.PLAIN, 18));
		information.setForeground(Color.DARK_GRAY);
		information.setBackground(SystemColor.inactiveCaptionBorder);
		information.setBounds(10, 219, 396, 304);

		p1.add(information);
		information.setLayout(null);
		textArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		textArea.setBackground(SystemColor.textHighlightText);
		textArea.setEditable(false);
		textArea.setFont(new Font("微软雅黑 Light", Font.PLAIN, 18));
		textArea.setBounds(3, 3, 390, 300);
		information.add(textArea);

		JLabel lblNewLabel = new JLabel("  Running Information :");
		lblNewLabel.setFont(new Font("Century Gothic", Font.PLAIN, 17));
		lblNewLabel.setBounds(15, 198, 231, 21);
		p1.add(lblNewLabel);
		manageSuites.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable(){
					public void run(){			
						ManageBenchmarkFrame mbf = new ManageBenchmarkFrame(fileAbsolutePath);
						System.out.println("Manage benchmark ...");
						mbf.setVisible(true);
						
		//				mbf.setSize(1800, 1000);
						mbf.setBounds(200, 150,1800, 1000);
						
						mbf.treetable.removeAll();
						mbf.treetable.updateUI();
						mbf.setTitle("Management");
					}
				});
			}
		});
		scriptFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectScriptFrame ssf = new SelectScriptFrame();
				System.out.println("Script operations start ...");
				ssf.setBounds(750, 150,1160, 1000);
				ssf.setTextArea(textArea);
				//ssf.setLocationRelativeTo(null);
				ssf.setVisible(true);
				ssf.setTitle("Script");
			}
		});
		readSuites.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Please select file");
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				File rootFile = new File(System.getProperty("user.dir").replace('\\', '/')+"/file");
				jfc.setCurrentDirectory(rootFile);
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			              "XML file(*.xml)", "xml");
			    jfc.setFileFilter(filter);
				jfc.setDialogTitle("Select dataset file:");
				jfc.showDialog(new JLabel(), null);
				file = jfc.getSelectedFile();
				if (file != null) {
					textAreaInfo="";
					textArea.setText(textAreaInfo);
					fileAbsolutePath = file.getAbsolutePath();
					ParseXml parser = new ParseXml();
					Document validationResult = parser.validateXml(fileAbsolutePath);
					if (validationResult != null) {
						textAreaInfo=textAreaInfo+"\nXml file is valid!\n";
						textArea.setText(textAreaInfo);

						Reports.removeAllBenchmakrs();
						DomToEntity convert = new DomToEntity();
						textAreaInfo = convert.startDom(validationResult,textAreaInfo,textArea);										

						textAreaInfo=textAreaInfo+"\nBenchmarks read successfully.\n";
						textArea.setText(textAreaInfo);
						textArea.setCaretPosition(textArea.getText().length());

						JOptionPane.showMessageDialog(getContentPane(), "Successfully read the benchmark!");
						
						manageSuites.setEnabled(true);
						runProgram.setEnabled(true);
						scriptFile.setEnabled(true);
						
					} else {
						System.out.println("Error");
						textAreaInfo=textAreaInfo+"\n\nBenchmarks have problem!\n";
						textArea.setText(textAreaInfo);
						textArea.setCaretPosition(textArea.getText().length());
					}
				}
			}
		});
	}
	
	 protected void do_this_windowOpened(WindowEvent e) {

	    }

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		JFrame mainFrame = new MainFrame();
		mainFrame.setSize(439, 589);
		mainFrame.setTitle("Test Platform");
		mainFrame.setVisible(true);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setBounds(300, 400, 439, 589);
		// frame.pack();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	static class ButtonPanel extends JPanel {

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		}
	}
}
