package cn.edu.thu.platform.frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;

import cn.edu.thu.platform.comparison.ComparisonResult;
import cn.edu.thu.platform.entity.Reports;
import cn.edu.thu.platform.script.Script;
import java.awt.Component;

/**
 * 
 */
public class SelectScriptFrame extends JFrame implements ChangeListener {
	public JProgressBar progress = new JProgressBar();
	private JTextArea jText = new JTextArea();
	private JScrollPane jsp;
	private JButton runScript = new JButton("Run script file");
	private JButton btCompare = new JButton("compare ");
	private JButton btChooseResult = new JButton("Select comparison file");
	private JButton btExample = new JButton("See Sample Files");
	private JPanel anotherPanel = new JPanel();
	private BufferedReader readStdout = null;
	private BufferedReader readStderr = null;
	private StringBuffer mStringBuffer = new StringBuffer();
	private String tmp1 = null;
	private String tmp2 = null;
	// private RealtimeProcessInterface mInterface = null;
	// private JFileChooser chooseResult = new JFileChooser();
	private TextArea textArea = null;
	private String textAreaInfo = "";
	private JRadioButton cal, rv, date, other;
	private String fileAbsolutePath = "";
	private FileWriter writer1;
	public String commands = "";// store text information showed in specified window
	public int indexPos = System.getProperty("user.dir").replace('\\', '/')
			.lastIndexOf('/');
	public String writePosition = System.getProperty("user.dir")
			.replace('\\', '/').substring(0, indexPos);

	// public int position = writePosition.lastIndexOf('/');
	// writePosition = writePosition.substring(0, indexPos);

	public SelectScriptFrame() {
		getContentPane().setLayout(null);
		JPanel panel = new JPanel();
		panel.setFont(new Font("Century Gothic", Font.PLAIN, 18));
		panel.setLocation(0, 0);
		panel.setLayout(null);
		panel.setSize(1139, 948);
		panel.setBackground(new Color(245, 245, 245));
		progress.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		progress.setForeground(new Color(135, 206, 250));
		progress.setVisible(false);
		progress.setMinimum(0);
		progress.setStringPainted(true); 
		progress.setBackground(Color.WHITE); 
		progress.setBounds(0, 928, 1139, 20);
		panel.add(progress);

		JButton chooseFile = new JButton("Select script file");
		chooseFile.setBounds(92, 46, 183, 47);
		chooseFile.setFont(new Font("Century Gothic", Font.PLAIN, 20));
		panel.add(chooseFile);

		runScript.setBounds(92, 128, 183, 47);
		runScript.setFont(new Font("Century Gothic", Font.PLAIN, 20));
		panel.add(runScript);
		
		// textbox shows text information 
		jText.setBounds(67, 102, 1002, 648);
		jsp = new JScrollPane(jText);
		jsp.setBounds(45, 221, 1043, 689);
		panel.add(jsp);
		jText.setFont(new Font("Century Gothic", Font.PLAIN, 23));
		jText.setEditable(false);
		if (jText.getText().equals("") || jText == null) {
			runScript.setEnabled(false);
		}
		anotherPanel.setFont(new Font("Century Gothic", Font.PLAIN, 18));
		anotherPanel.setBackground(new Color(245, 245, 245));
		anotherPanel.setBorder(new TitledBorder(new LineBorder(new Color(211, 211, 211), 3, true), "                         ", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		anotherPanel.setLayout(null);
		anotherPanel.setSize(100, 600);
		anotherPanel.setBounds(341, 26, 705, 161);
		anotherPanel.add(cal = new JRadioButton("CalFuzzer", true));
		cal.setBackground(new Color(245, 245, 245));
		cal.setFont(new Font("Century Gothic", Font.PLAIN, 20));
		anotherPanel.add(rv = new JRadioButton("Rv-Predict"));
		rv.setBackground(new Color(245, 245, 245));
		rv.setFont(new Font("Century Gothic", Font.PLAIN, 20));
		anotherPanel.add(date = new JRadioButton("DATE"));
		date.setBackground(new Color(245, 245, 245));
		date.setFont(new Font("Century Gothic", Font.PLAIN, 20));
		anotherPanel.add(other = new JRadioButton("other tool"));
		other.setBackground(new Color(245, 245, 245));
		other.setFont(new Font("Century Gothic", Font.PLAIN, 20));
		btCompare.setFont(new Font("Century Gothic", Font.PLAIN, 20));
		btCompare.setBounds(267, 90, 155, 45);
		anotherPanel.add(btCompare);
		btExample.setVisible(false);
		btExample.setFont(new Font("微软雅黑", Font.PLAIN, 20));
		btExample.setBounds(522, 90, 167, 45);
		btExample.setEnabled(false);
		anotherPanel.add(btExample);
		btChooseResult.setVisible(false);
		btChooseResult.setFont(new Font("微软雅黑", Font.PLAIN, 20));
		btChooseResult.setEnabled(false);
		btChooseResult.setBounds(267, 90, 188, 45);
		anotherPanel.add(btChooseResult);
		cal.setBounds(31, 31, 138, 37);
		rv.setBounds(200, 31, 155, 37);
		date.setBounds(386, 31, 98, 37);
		other.setBounds(515, 31, 155, 37);
		ButtonGroup group = new ButtonGroup();
		group.add(cal);
		group.add(rv);
		group.add(date);
		group.add(other);
		panel.add(anotherPanel);
		
		JLabel lblNewLabel = new JLabel("Compare running results");
		lblNewLabel.setFont(new Font("Century Gothic", Font.PLAIN, 18));
		lblNewLabel.setBounds(15, -11, 248, 36);
		anotherPanel.add(lblNewLabel);
		getContentPane().add(panel);

		// choose a specified tool pattern to extract line numbers of data race
		cal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ComparisonResult.tool = "CalFuzzer";
				btCompare.setEnabled(true);
				btExample.setEnabled(false);
			}

		});
		rv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ComparisonResult.tool = "Rv-Predict";
				btCompare.setEnabled(true);
				btExample.setEnabled(false);
			}
		});
		date.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ComparisonResult.tool = "DATE";
				btCompare.setEnabled(true);
				btExample.setEnabled(false);
			}

		});
		other.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ComparisonResult.tool = "other";
				btCompare.setEnabled(false);
				btChooseResult.setEnabled(true);
				btExample.setEnabled(true);
			}

		});

		btExample.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Start to see the sample file ...");
			}
		});

		// comparison function
		btCompare.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RoughResultFrame rrf = new RoughResultFrame();
				System.out.println("start to see the comparison results ...");
				rrf.setSize(1500, 1000);
				rrf.setLocationRelativeTo(null);
				rrf.setVisible(true);
				rrf.setTitle("Data Race Detection Report");
			}
		});

		// choose a execution script
		chooseFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				String filePath = System.getProperty("user.dir").replace('\\', '/');
				filePath = filePath.substring(0, filePath.indexOf("JBenchPlatfor")-1);
				File rootFile = new File(filePath);
				jfc.setCurrentDirectory(rootFile);
				jfc.showDialog(new JLabel(), "select script ");
				jfc.setBounds(750, 50, 100, 50);
				File file = jfc.getSelectedFile();
				if (file != null) {
					Script.scripts.clear();
					fileAbsolutePath = file.getAbsolutePath();
					System.out.println("file path ：" + file.getAbsolutePath());
					commands = "\n    =====================   COMMANDS IN SCRIPTFILE  =====================\n\n";				
					commands = commands +  readFileByLines(fileAbsolutePath);
					jText.setText(commands);
					if (!(jText.getText().equals("") || jText == null)) {
						runScript.setEnabled(true);
					}
				}
			}
		});

		// run test cases
		runScript.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(System.getProperty("user.dir"));
				System.out.println("writePosition:" + writePosition);

				Reports.userNames.clear();
				Reports.userReports.clear();

				// refresh schedule progressing
				new Thread() {
					@Override
					public void run() {
						progress.setValue(0);
						progress.setVisible(true);
						progress.setMaximum(Script.scripts.size());

						commands = commands + "\n";


						try {
							commands = commands + "\n\n";
							commands = commands + "    =====================   RUNNING  INFORMATION   =====================\n\n";
							jText.setText(commands);
							DefaultCaret caret = (DefaultCaret) jText.getCaret();
							caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
							jsp.setViewportView(jText);
							
							writer1 = new FileWriter(writePosition + "/result.txt", false);
							for (int i = 0; i < Script.scripts.size(); i++) {
								String temp = Script.scripts.get(i);
								String tempFile = writePosition	+ "/tempFile.bat";
								FileWriter writer = new FileWriter(tempFile,false);
								writer.write(deleteName(temp));
								writer.close();
								
								// run a single command
								String programName = SelectScriptFrame.this.getName(temp);
								if(Reports.programNames.contains(programName)){
									commands = commands + "    " + "Start running usecase [ "+programName+" ] ...\n";
									jText.setText(commands);
									runCommands(tempFile, writePosition, temp, programName, writer1);
								}else{
									commands = commands + "    " + "Can't find usecase [ "+programName+" ] in our benchmarks, skip it !\n";
									jText.setText(commands);
									System.out.println("Can't find test case : " + programName+" in the benchmarks, skipped ...");
									Reports.wrongNames.add(programName);
								}
								// refresh schedule progressing
								progress.setValue(i + 1);
								System.out.println("progress value ：" + (progress.getValue()));
							}
							writer1.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						JOptionPane.showMessageDialog(null, "All the scripts  have been finished !");
						anotherPanel.setVisible(true);
						progress.setValue(0);
						progress.setVisible(false);
					}
				}.start();

			}
		});
	}

		public void runCommands(String scriptFile, String filePath, String command,	String programName, FileWriter writer) {

			try {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				long startTime=System.currentTimeMillis(); 
				Process proc = Runtime.getRuntime().exec(scriptFile);
				long tempTime = System.currentTimeMillis();
				System.out.println("Time 1;" + (tempTime - startTime)/1000);
				 BufferedInputStream in = null;
				// get standard output
				readStdout = new BufferedReader(new InputStreamReader(proc.getInputStream(),Charset.forName("GBK")));
				// get error output
				readStderr = new BufferedReader(new InputStreamReader(proc.getErrorStream(),Charset.forName("GBK")));
//				mStringBuffer.replace(0, mStringBuffer.length(), "");
				mStringBuffer.delete(0, mStringBuffer.length());
				mStringBuffer = new StringBuffer();
				Thread execThread = new Thread() {
					@Override
					public void run() {
						try {
							while ((tmp1 = readStdout.readLine()) != null) {
								if (tmp1 != null) {
									System.out.println(tmp1);
									textAreaInfo = textAreaInfo +tmp1.toString()+"\n";
									mStringBuffer.append(tmp1 + "\n");
								}
								DefaultCaret caret = (DefaultCaret) jText.getCaret();
								caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
								jsp.setViewportView(jText);
								
								textArea.setText(textAreaInfo);
								textArea.setCaretPosition(textArea.getText().length());
							}

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				};
				Thread execThread1 = new Thread() {
					@Override
					public void run() {
						try {
							while ((tmp2 = readStderr.readLine()) != null) {
								if (tmp2 != null) {
									System.out.println(tmp2);
									textAreaInfo = textAreaInfo +tmp2.toString()+"\n";
									mStringBuffer.append(tmp2 + "\n");
								}
								DefaultCaret caret = (DefaultCaret) jText.getCaret();
								caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
								jsp.setViewportView(jText);
								
								textArea.setText(textAreaInfo);
								textArea.setCaretPosition(textArea.getText().length());
							}

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				};
				execThread.start();
				execThread1.start();
				execThread.join();
				execThread1.join();
//				proc.waitFor();
				long endTime = System.currentTimeMillis();
				long timeConsumer = (endTime - startTime) / 1000;
				System.out.println("Time 2：" + timeConsumer);
				Reports.userNames.add(programName);
				writer.write("\n>>>>>start command " + command + " about "+ programName + " on " + df.format(new Date()) + " <<<<<\n");
				writer.write(mStringBuffer.toString());
				if (proc.waitFor() != 0) {
					if (proc.exitValue() == 1)
						System.err.println("Command execution failed !");
					writer.write("Command execution failed!\n");
					commands = commands + "          BUILD FAILD !\n\n";
					jText.setText(commands);
				}else {
					commands = commands + "          BUILD SUCCESSFUL !\n\n";
					jText.setText(commands);
				}
				
				writer.write("[total time:" + timeConsumer + "s]");
				writer.write("\n>>>>>end<<<<<\n");
				writer.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}finally{
				try {
					if(readStdout!=null){
						readStdout.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if(readStderr!=null){
						readStderr.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	public String getName(String command) {
		String name = command;
		int index = command.indexOf(']');
		name = command.substring(1, index);
		return name;
	}

	public String deleteName(String command) {
		int index = command.indexOf(']');
		command = command.substring(index + 1);
		return command;
	}


	public String readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		String commands = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			while ((tempString = reader.readLine()) != null) {
				System.out.println("line " + line + ": " + tempString);
				line++;
				commands += tempString + "\n";
				Script.scripts.add(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return commands;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == progress) {
			int value = progress.getValue();
			progress.setString("zhi:" + (value * 1.0) / (Script.scripts.size())* 100 + "%");
			System.out.println("zhi:" + (value * 1.0) / (Script.scripts.size())* 100 + "%");
		}
	}
	public void setTextArea(TextArea textArea) {
		this.textArea = textArea;		
	}
}
