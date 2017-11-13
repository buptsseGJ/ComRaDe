package cn.edu.thu.platform.frame;

import java.awt.Font;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import cn.edu.thu.platform.comparison.Comparison;
import cn.edu.thu.platform.comparison.ComparisonResult;
import cn.edu.thu.platform.entity.Race;
import cn.edu.thu.platform.entity.Report;
import cn.edu.thu.platform.entity.Reports;
import cn.edu.thu.platform.entity.Result;
import cn.edu.thu.platform.match.Match;
import cn.edu.thu.platform.match.MatchInterface;
import java.awt.Component;
import java.awt.Color;
import javax.swing.JTable;
import java.awt.Dimension;
import javax.swing.border.LineBorder;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import java.awt.Insets;
import javax.swing.ScrollPaneConstants;
import java.awt.Rectangle;

public class RoughResultFrame extends JFrame {
	private String totalMessage = "   Note:\n\t1.PR = precision rate\n\t2.FNR = false negative rate\n\t3.FPR = false positive rate\n\t4.OVERHEAD = （detection time – original running time）/ original running time \n\n   Extra introducodution:\n";
	private JTextPane txtpnTimeTool = new JTextPane();
	private JTable table;
	JTextPane txtpane = new JTextPane();
	Object[][] data;
	String[] columnNames = {"No.","NAME","BENCHMARKS","SCRIPTFILE","TOTAL","FOUNDED","MISSED","EXTRA","PR","FNR","FPR","OVERHEAD"};
	public DecimalFormat df=new DecimalFormat(".##");
	
	public RoughResultFrame() {
		getContentPane().setSize(new Dimension(1500, 1000));
		setSize(new Dimension(1500, 1000));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setFont(new Font("Century Gothic", Font.PLAIN, 22));
		getContentPane().setBackground(Color.WHITE);

		//add the basic JPanel
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setSize(new Dimension(1494, 1000));
		scrollPane.setPreferredSize(new Dimension(1494, 1000));
		scrollPane.setBounds(0, 0, 1494, 1000);
		//scrollPane.setSize(new Dimension(0, 4000));
		JPanel panel = new JPanel();
		panel.setBounds(new Rectangle(0, 0, 1494, 1000));
		panel.setSize(new Dimension(1494, 1000));
		panel.setBackground(new Color(255, 255, 255));
		scrollPane.setViewportView(panel);
		panel.setLayout(null);
		getContentPane().add(scrollPane);
		if (Reports.flag == true){
			MatchInterface match = Match.getMatch(ComparisonResult.tool);
			match.matchFile();
		}
		getContentPane().setLayout(null);		
		JScrollPane txtscrollPane = new JScrollPane();
		txtpane.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 20));
		txtpane.setEditable(false);
		txtscrollPane.setViewportView(txtpane);
		txtscrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		txtscrollPane.setSize(1400, 60);
		panel.add(txtscrollPane);		
		generateReport();
		//title
		JLabel lblNewLabel = new JLabel("Data Race Detection Report");
		lblNewLabel.setBounds(0, 0, 1494,131);
		lblNewLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Century Gothic", Font.BOLD, 24));
		panel.add(lblNewLabel);

		//time&tool
		txtpnTimeTool.setPreferredSize(new Dimension(6, 10));
		txtpnTimeTool.setMargin(new Insets(5, 10, 0, 10));
		txtpnTimeTool.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 20));
		txtpnTimeTool.setEditable(false);
		txtpnTimeTool.setBounds(900,126, 635,37);
		panel.add(txtpnTimeTool);

		//line1
		JTextPane txtpaneLine1 = new JTextPane();
		txtpaneLine1.setBounds(65,160, 1396, 85);
		panel.add(txtpaneLine1);
		txtpaneLine1.setPreferredSize(new Dimension(6, 10));
		txtpaneLine1.setMargin(new Insets(5, 10, 0, 10));
		txtpaneLine1.setEditable(false);
		txtpaneLine1.setText("       \r\n               The detection results of test cases in this test are compared with the benchmark. The comparison results are shown in the following table. More detailed data race information can be found in resultReport.csv which is located in the root directory of this tool.");
		txtpaneLine1.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 20));
		
		table = new JTable(data, columnNames) {
			@Override
			public JTableHeader getTableHeader() {
				// TODO Auto-generated method stub
				JTableHeader j =  super.getTableHeader();
				
				j.setVisible(true);
				
				return j;
			}
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				// TODO Auto-generated method stub
				Component c =  super.prepareRenderer(renderer, row, column);
				
				
				
				return c;
			}
		};
		
		table.setPreferredSize(new Dimension(825, 400));
		table.setPreferredScrollableViewportSize(new Dimension(825, 200));
		table.setFillsViewportHeight(true);
		table.setName("");
		table.setColumnSelectionAllowed(true);
		table.setFont(new Font("Century Gothic", Font.PLAIN, 20));
		table.setGridColor(new Color(128, 128, 128));
		table.setEnabled(false);
		table.setBorder(null);
		table.setAutoCreateColumnsFromModel(false);
		table.setModel(new DefaultTableModel(data,columnNames) {
			boolean[] columnEditables = new boolean[] {
					true, true, true, true, true, true, true, true, true, false, false, false, false
			};
			@Override
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
			
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(160);
		table.getColumnModel().getColumn(2).setPreferredWidth(90);
		table.getColumnModel().getColumn(3).setPreferredWidth(50);
		table.getColumnModel().getColumn(4).setPreferredWidth(30);
		table.getColumnModel().getColumn(5).setPreferredWidth(50);
		table.getColumnModel().getColumn(6).setPreferredWidth(40);
		table.getColumnModel().getColumn(7).setPreferredWidth(20);
		table.getColumnModel().getColumn(8).setPreferredWidth(20);
		table.getColumnModel().getColumn(9).setPreferredWidth(20);
		table.getColumnModel().getColumn(10).setPreferredWidth(20);
		table.getColumnModel().getColumn(11).setPreferredWidth(60);
		table.setRowHeight(30);
		table.setRequestFocusEnabled(false);
		table.setVerifyInputWhenFocusTarget(false);
		table.setUpdateSelectionOnSort(false);
		table.setRowSelectionAllowed(false);
		Dimension size = table.getTableHeader().getPreferredSize();
		size.height = 50;
		table.getTableHeader().setFont(new Font("Century Gothic", Font.PLAIN, 20));
		table.getTableHeader().setPreferredSize(size);
		table.setBackground(new Color(240, 248, 255));

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setViewportView(table);
		scrollPane_1.setBackground(new Color(255, 255, 255));
		scrollPane_1.setBorder(new LineBorder(Color.DARK_GRAY));
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_1.setBounds(65,262, 1350, 50);
		scrollPane_1.setSize(new Dimension(1350,table.getRowCount()*30+50));
		panel.add(scrollPane_1);

		//line2
		txtscrollPane.setBorder(null);
		txtscrollPane.setSize(1494, txtpane.getText().split("\n").length*35);
		txtscrollPane.setLocation(45, table.getRowCount()*30+340);
		txtscrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		txtpane.setPreferredSize(new Dimension(1170,txtpane.getText().split("\n").length*35));
//		txtpane.setBounds(70, scrollPane_1.getY()+table.getRowCount()*30+50, 1170, txtpane.getPreferredSize().height+30);
		
		int height = txtpane.getPreferredSize().height+scrollPane_1.getHeight()+400>997? txtpane.getPreferredSize().height+scrollPane_1.getHeight()+400:997;
		panel.setPreferredSize(new Dimension(1494, height));
//		txtpane.setPreferredSize(new Dimension(1170,txtpane.getPreferredSize().height));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	}

	public void generateReport() {
		String filePath = "../resultReport.csv";
		try {
			FileWriter writer = new FileWriter(filePath, true);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			writer.write("DATE:,"+df.format(new Date())+"\n");
			writer.write("TOOL:,"+ComparisonResult.tool+"\n");

			txtpnTimeTool.setText("DATE : "+df.format(new Date())+"    TOOL : "+ComparisonResult.tool);

			writeResult(writer);

			writer.write("\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeResult(FileWriter writer) {
		//header
		String csvTotalStr = ",,,Statistic description:\n";
		String csvDetailStr = ",,,Races details:\n";
		try {
			writer.write(",,No.,USECASE NAME,IS IN BENCHMARKS,IS IN SCRIPTFILE,TOTAL,FOUNDED,MISSED,EXTRA,PR,FNR,FPR,OVERHEAD\n");
			Comparison com = new Comparison();
			Report originalReport;
			Report newReport;
			int count = 1;

			data= new Object[(Reports.wrongNames.size()+Reports.programNames.size())][12];

			for(int i = 0;i < Reports.userNames.size(); i++){
				String name = Reports.userNames.get(i);

				originalReport = Reports.compareReports.get(name);
				newReport = Reports.userReports.get(name);
				com.compare(name, originalReport, newReport);
				Result curCaseResults = ComparisonResult.resultsMap.get(name);
				int totalRaceNum = curCaseResults.getTotalRaace();
				int missRaceNum = curCaseResults.getMissedRace();
				int rightRaceNum = curCaseResults.getMathchedRace();
				int extraRaceNum = curCaseResults.getAdditianlRace();data[count-1][0]= count;
				data[count-1][1]= name;
				data[count-1][2]= "TRUE";
				data[count-1][3]= "TRUE";
				data[count-1][4]= totalRaceNum;
				data[count-1][5]= rightRaceNum;
				data[count-1][6]= missRaceNum;
				data[count-1][7]= extraRaceNum;
				data[count-1][8]= (100.0*rightRaceNum/totalRaceNum)+"%";
				data[count-1][10]= (100.0*(extraRaceNum)/totalRaceNum)+"%";
				data[count-1][9]= (100.0*missRaceNum/totalRaceNum)+"%";
				double testTime = Double.valueOf(newReport.getTime().substring(0, newReport.getTime().length()-1));
				double origionTime = Double.valueOf(originalReport.getTime().substring(0, originalReport.getTime().length()));
				data[count-1][11]= df.format((testTime-origionTime)/origionTime).toString();
				System.out.println("originalTime:"+origionTime+" testTime:" + testTime);
				System.out.println("rightRaceNum:"+rightRaceNum+"\ntotalRaceNum"+totalRaceNum);
				writer.write(",,"+count+","+curCaseResults.getProgramName()+",TRUE,TRUE,"+totalRaceNum+","+rightRaceNum+","+
						missRaceNum+","+extraRaceNum+","+(100.0*rightRaceNum/totalRaceNum)+"%,"+(100.0*(missRaceNum)/totalRaceNum)+
						"%,"+(100.0*extraRaceNum/totalRaceNum)+"%,"+df.format((testTime-origionTime)/origionTime)+",\n");
				totalMessage += "	"+count + ". The number of race in " + name +" is "+totalRaceNum+". This tool found "+rightRaceNum+" race successfully, missed "+
						missRaceNum+" race, and found "+extraRaceNum+" extra race;\n	      So the precision is "+(100.0*rightRaceNum/totalRaceNum)+"%, false negative rate is "+(100.0*(missRaceNum)/totalRaceNum)+
						"%,the false positive rate is "+(100.0*extraRaceNum/totalRaceNum)+"%, time overhead is "+df.format((testTime-origionTime)/origionTime)+";\n";
				csvTotalStr = csvTotalStr+",,"+count + ","+name+",total "+totalRaceNum+" races;,"+rightRaceNum+" right race;,"+missRaceNum+" missing race;,"+extraRaceNum+" extra race;,precision is "+(100.0*rightRaceNum/totalRaceNum)+"%;,FNR is "+(100.0*(missRaceNum)/totalRaceNum)+"%;,FPR is"+(100.0*extraRaceNum/totalRaceNum)+"%;,\n";
				count++;
				Set<Race> rightRaces = ComparisonResult.findRace.get(name).getCompareRaces();
				Iterator<Race> rightIt = rightRaces.iterator();
				int raceNum = 1;
				while(rightIt.hasNext()) {
					Race r = rightIt.next();
					if(raceNum==1) {
						csvDetailStr = csvDetailStr +",,,NAME,No.,STATE,CLASS,Line1,Line2,VARIABLE,DETAILs,\n";
						csvDetailStr = csvDetailStr +",,,"+name+","+raceNum + ",correctly found," +r.getPackageClass()+ "," +r.getLine1()+ "," +r.getLine2()+ "," +r.getVariable()+ "," +r.getDetail()+",\n";
					}else
						csvDetailStr = csvDetailStr +",,,,"+raceNum + ",correctly found," +r.getPackageClass()+ "," +r.getLine1()+ "," +r.getLine2()+ "," +r.getVariable()+ "," +r.getDetail()+",\n";
					raceNum++;
				}
				Set<Race> missRaces = ComparisonResult.missRace.get(name).getCompareRaces();
				Iterator<Race> missIt = missRaces.iterator();
				while(missIt.hasNext()) {
					Race r = missIt.next();
					if(raceNum==1) {
						csvDetailStr = csvDetailStr +",,,NAME,No.,STATE,CLASS,Line1,Line2,VARIABLE,DETAILs,\n";
						csvDetailStr = csvDetailStr +",,,"+name+","+raceNum + ",missing," +r.getPackageClass()+ "," +r.getLine1()+ "," +r.getLine2()+ "," +r.getVariable()+ "," +r.getDetail()+",\n";
					}else
						csvDetailStr = csvDetailStr +",,,,"+raceNum + ",missing," +r.getPackageClass()+ "," +r.getLine1()+ "," +r.getLine2()+ "," +r.getVariable()+ "," +r.getDetail()+",\n";
					raceNum++;
				}
				Set<Race> extraRaces = ComparisonResult.additianalRace.get(name).getCompareRaces();
				Iterator<Race> extraIt = extraRaces.iterator();
				while(extraIt.hasNext()) {
					Race r = extraIt.next();
					if(raceNum==1) {
						csvDetailStr = csvDetailStr +",,,NAME,No.,STATE,CLASS,Line1,Line2,VARIABLE,DETAILs,\n";
						csvDetailStr = csvDetailStr +",,,"+name+","+raceNum + ",additional," +r.getPackageClass()+ "," +r.getLine1()+ "," +r.getLine2()+ "," +r.getVariable()+ "," +r.getDetail()+",\n";
					}else
						csvDetailStr = csvDetailStr +",,,,"+raceNum + ",additional," +r.getPackageClass()+ "," +r.getLine1()+ "," +r.getLine2()+ "," +r.getVariable()+ "," +r.getDetail()+",\n";
					raceNum++;
				}
			}

			//test case is in script, not in benchmark's xml file
			for(int i = 0; i < Reports.wrongNames.size(); i++){
				totalMessage += "	"+count + ". In the benchmark, there is no project [" + Reports.wrongNames.get(i) + "];\n";
				writer.write(",,"+count+","+Reports.wrongNames.get(i)+",FALSE,TRUE,"+"--"+","+"--"+","+
						"--"+","+"--"+","+"--"+","+"--"+","+"--"+",\n");
				csvTotalStr = csvTotalStr+",,"+count + ". ,"+Reports.wrongNames.get(i)+",is not in benchmarks;\n";
				data[count-1][0]= count;
				data[count-1][1]= Reports.wrongNames.get(i);
				data[count-1][2]= "FALSE";
				data[count-1][3]= "TRUE";
				data[count-1][4]= "--";
				data[count-1][5]= "--";
				data[count-1][6]= "--";
				data[count-1][7]= "--";
				data[count-1][8]= "--";
				data[count-1][9]= "--";
				data[count-1][10]= "--";
				data[count-1][11]= "--";
				count++;
			}
			//out program name which is omitted 
			List<String> subtractionList = new ArrayList<String>();
			subtractionList.addAll(Reports.programNames);
			subtractionList.removeAll(Reports.userNames);
			if(!subtractionList.isEmpty()){
				int innerCount = 1;
				for(int i = 0; i < subtractionList.size(); i++){
					totalMessage += "	" + count + ". "+subtractionList.get(i) + " is in the benchmark, but has not been tested;\n";
					writer.write(",,"+count+","+subtractionList.get(i)+",TRUE,FALSE,"+","+"--"+","+"--"+","+
							"--"+","+"--"+","+"--"+","+"--"+","+"--"+",\n");
					csvTotalStr =csvTotalStr+",,"+count + ". ,"+subtractionList.get(i)+",in scriptfile,not in benchmarks;\n";
					data[count-1][0]= count;
					data[count-1][1]= subtractionList.get(i);
					data[count-1][2]= "TRUE";
					data[count-1][3]= "FALSE";
					data[count-1][4]= "--";
					data[count-1][5]= "--";
					data[count-1][6]= "--";
					data[count-1][7]= "--";
					data[count-1][8]= "--";
					data[count-1][9]= "--";
					data[count-1][10]= "--";
					data[count-1][11]= "--";
					innerCount ++;
					count++;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			writer.write(csvTotalStr);
			writer.write(csvDetailStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		txtpane.setText(totalMessage);
		txtpane.setPreferredSize(new Dimension(1470, 60));
	}
}
