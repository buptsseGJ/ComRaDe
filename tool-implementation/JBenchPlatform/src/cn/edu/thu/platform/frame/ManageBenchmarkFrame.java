package cn.edu.thu.platform.frame;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.tree.*;

import org.jdesktop.swingx.*;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.treetable.*;
import org.jdesktop.swingx.ux.CheckTreeTableManager2;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import cn.edu.thu.platform.entity.*;
import cn.edu.thu.platform.frame.MyTreeTableNode;
import cn.edu.thu.platform.parser.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.MatteBorder;

/**
 *
 * @author yangxin
 */
public class ManageBenchmarkFrame extends JFrame  implements MouseListener,ItemListener {

	private final CheckTreeTableManager2 manager;
	public DefaultMutableTreeTableNode root ;
	public MyTreeTableNode curNode;
	public boolean isInit = false;//Used to determine whether the initial TextArea is initialized and to determine whether to refresh TreeTableNode
	private JToolBar toolBar;
	public DocumentListener textListener;
	public JXTreeTable treetable;
	public JTextArea detailTextArea;
//	private JTextField detailTextArea_1;
//	public TextArea detailTextArea = new TextArea("",5, 1, TextArea.SCROLLBARS_VERTICAL_ONLY);
	public JCheckBox ALL1,PT,RT,CT,MU,ALL2,bsbs,bcbc,lclc,sbsb,slsl,bsbc,bslc,bssb,bssl,bclc,bcsb,bcsl,lcsb,lcsl,sbsl,ALL3,SS,DS,DD,ALL4,NOS,PartS,INCS;
	public String fileAbsolutePath;
	
	private JPopupMenu popMenu; 
	private int curClick_X = -1,curClick_Y = -1;
	JMenuItem addItem1 = new JMenuItem("add testcase"); 
	JMenuItem addItem2 = new JMenuItem("add race");  
	JMenuItem delItem1 = new JMenuItem("delete testcase");  
	JMenuItem delItem2 = new JMenuItem("delete race");  
	private JLabel lblNewLabel_1;
	private JTextPane Node;
	private JScrollPane scrollPane;
	private JPanel panel_6;

	public ManageBenchmarkFrame(String fileAbsolutePath){
		super("management");
		addWindowListener(new WindowAdapter() {
			 @Override
	            public void windowOpened(WindowEvent e) {
			        Toolkit toolkit = getToolkit();
			        Dimension screenSize = toolkit.getScreenSize();
			        int width = (int) (screenSize.width * 0.93);
			        int height = (int) (screenSize.height * 0.9);
			        setSize(width, height);
	            }
		});
		this.fileAbsolutePath = fileAbsolutePath;
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		MyTreeTableModel treeModel = new MyTreeTableModel(createDummyData());
		treetable = new JXTreeTable(treeModel){
			private static final long serialVersionUID = 1L;
			@Override
			@SuppressWarnings({ "serial" })
			protected JXTableHeader createDefaultTableHeader() {
				return new JXTableHeader(columnModel) {
					@Override
					public void setDraggedColumn(TableColumn aColumn) {
						if (aColumn == getColumnModel().getColumn(0)) {
							return;
						}
						super.setDraggedColumn(aColumn);
					}
				};
			}

			//background color
			@Override
			public Color getBackground() {
				Color c = super.getBackground();
				return c;//Color.green;
			}			
			@Override
			protected TableColumnModel createDefaultColumnModel() {
				return new DefaultTableColumnModel() {
					@Override
					public void moveColumn(int columnIndex, int newIndex) {
						if (columnIndex == 0 || newIndex == 0) {
							return;
						}
						super.moveColumn(columnIndex, newIndex);
					}
				};
			}
		};
		
		treetable.setShowHorizontalLines(false);
		treetable.setRowHeight(45);
		//treetable.setDefaultRenderer(Object.class, new TableViewRenderer());
		//treetable.setTreeCellRenderer(new TableViewRenderer());

		treetable.setHorizontalScrollEnabled(true);
		manager = new CheckTreeTableManager2(treetable);   //  construct checkboxtreetable, is close to JXTreeTable constructor
		treetable.getColumnModel().getColumn(0).setPreferredWidth(170);//sets the width of each column
		treetable.getColumnModel().getColumn(1).setPreferredWidth(220);
		treetable.getColumnModel().getColumn(2).setPreferredWidth(65);
		treetable.getColumnModel().getColumn(3).setPreferredWidth(65);
		treetable.getColumnModel().getColumn(4).setPreferredWidth(65);
		treetable.getColumnModel().getColumn(5).setPreferredWidth(110);
		treetable.getColumnModel().getColumn(6).setPreferredWidth(160);
		treetable.getColumnModel().getColumn(7).setPreferredWidth(170); 
		treetable.getColumnModel().getColumn(8).setPreferredWidth(155); 
		treetable.getColumnModel().getColumn(9).setPreferredWidth(180); 
		treetable.getColumnModel().getColumn(10).setPreferredWidth(160); 
		treetable.getColumnModel().getColumn(11).setPreferredWidth(85); 
		treetable.getColumnModel().getColumn(12).setPreferredWidth(185); 
		treetable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		treetable.expandAll();  //  Expand all nodes
//		treetable.collapseAll();  //  Collapse all nodes
//		treetable.expandRow(0);  
		treetable.setHighlighters(HighlighterFactory.createSimpleStriping());//exchange the single line's background color
		treetable.getTableHeader().setReorderingAllowed(false);//do not allow users to exhcange two columns    
		treetable.getTableHeader().setFont(new Font("Century Gothic", Font.PLAIN, 22));
		treetable.getTableHeader().setSize(getWidth(), 100);		
		Dimension size = treetable.getTableHeader().getPreferredSize();
		size.height = 50;
		treetable.getTableHeader().setPreferredSize(size);
		//treetable.setRowHeight(35);
		treetable.setFont(new Font("Century Gothic", Font.PLAIN, 22));
		treetable.setSelectionBackground(new Color(179,222,255));
		treetable.setSelectionForeground(Color.DARK_GRAY);
		JScrollPane JSP= new JScrollPane(treetable);
		JSP.setPreferredSize(new Dimension(20, 462));
		getContentPane().add(JSP,BorderLayout.CENTER);

		treetable.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				int row=treetable.rowAtPoint(e.getPoint());  
				int col=treetable.columnAtPoint(e.getPoint());  
				if(row>-1 && col>-1){  
					Object value=treetable.getValueAt(row, col);  
					if(null!=value && !"".equals(value))  
						treetable.setToolTipText(value.toString());  
					else  
						treetable.setToolTipText(null);//close tips
				}  
			}

			@Override
			public void mouseDragged(MouseEvent e) {
			}
		});

		JPanel paneDetail = new JPanel();
		paneDetail.setBorder(new LineBorder(Color.LIGHT_GRAY));
		paneDetail.setBackground(new Color(255, 255, 255));
		textListener = new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				if(e==null||curNode==null|| !isInit){
					isInit=true;
					return;
				}
				curNode.setValue5(detailTextArea.getText());	
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				if(e==null||curNode==null|| !isInit){
					isInit=true;
					return;
				}
				curNode.setValue5(detailTextArea.getText());	
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(e==null||curNode==null|| !isInit){
					isInit=true;
					return;
				}
				curNode.setValue5(detailTextArea.getText());	
			}
		};
		paneDetail.setLayout(new BorderLayout(0, 0));
		panel_6 = new JPanel();
		panel_6.setAutoscrolls(true);
		panel_6.setBackground(Color.WHITE);
		panel_6.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		scrollPane = new JScrollPane(panel_6);
		scrollPane.setPreferredSize(new Dimension(1250, 300));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_6.setLayout(new BorderLayout(0, 0));
		
		lblNewLabel_1 = new JLabel("Details :");
		lblNewLabel_1.setHorizontalTextPosition(SwingConstants.LEFT);
		panel_6.add(lblNewLabel_1, BorderLayout.NORTH);
		lblNewLabel_1.setBorder(null);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_1.setFont(new Font("Century Gothic", Font.PLAIN, 22));
		lblNewLabel_1.setPreferredSize(new Dimension(1800, 25));
		detailTextArea = new JTextArea();
		detailTextArea.setEditable(false);
		detailTextArea.setFocusable(false);
		detailTextArea.setAlignmentY(Component.TOP_ALIGNMENT);
		panel_6.add(detailTextArea, BorderLayout.CENTER);
		detailTextArea.setLineWrap(true);
		detailTextArea.setFont(new Font("Century Gothic", Font.PLAIN, 20));
		detailTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		detailTextArea.setBackground(Color.WHITE);
		detailTextArea.getDocument().addDocumentListener(textListener);
		paneDetail.add(scrollPane, BorderLayout.CENTER);
		
		Node = new JTextPane();
		Node.setFocusable(false);
		Node.setEditable(false);
		paneDetail.add(Node, BorderLayout.EAST);
		Node.setPreferredSize(new Dimension(650, 27));
		Node.setBackground(SystemColor.window);
		Node.setCaretColor(new Color(0, 0, 0));
		Node.setMargin(new Insets(3, 4, 3, 3));
		Node.setBorder(new MatteBorder(0, 2, 0, 0, (Color) new Color(128, 128, 128)));
		Node.setFont(new Font("Century Gothic", Font.PLAIN, 20));
		Node.setText("      PT\t— primitive types;              RT\t— reference types;\r\n      CT\t— collection types;           MU\t— mixed usage;\r\n      lc\t— loop condition;              sb\t— statement in branch;  \r\n      sl\t— statement in loop;        bc\t— branch condition;      \r\n      bs\t— basic statement;            sl\t— statement in loop;\r\n      SS\t— the same method of the same class;\t     \r\n      DS\t— different methods of the same class;\r\n      DD\t— different methods of different classes;\r\n      NS\t— No synchronization;        PS\t— Partial synchronization;\r\n      IS\t— Incorrect synchronization;");
		getContentPane().add(paneDetail,BorderLayout.SOUTH);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(1800, 180));
		panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel.setBackground(new Color(255, 255, 205));

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.inactiveCaptionBorder);
		panel_1.setSize(300, 50);
		panel.add(panel_1,BorderLayout.CENTER);
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		

		//Variale Type 
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_2.setBackground(SystemColor.inactiveCaptionBorder);
		panel_1.add(panel_2);
		
		JLabel variableType = new JLabel("Variable Type :");
		variableType.setHorizontalAlignment(SwingConstants.RIGHT);
		variableType.setPreferredSize(new Dimension(145, 21));
		panel_2.add(variableType);
		variableType.setFont(new Font("Century Gothic", Font.PLAIN, 18));
		
		ALL1 = new JCheckBox("ALL ");
		ALL1.setSelected(true);
		panel_2.add(ALL1);
		ALL1.setBackground(SystemColor.inactiveCaptionBorder);
		
		PT = new JCheckBox("PT    ");
		PT.setSelected(true);
		panel_2.add(PT);
		PT.setBackground(SystemColor.inactiveCaptionBorder);
		
		RT = new JCheckBox("RT    ");
		RT.setSelected(true);
		panel_2.add(RT);
		RT.setBackground(SystemColor.inactiveCaptionBorder);
		
		CT = new JCheckBox("CT   ");
		CT.setSelected(true);
		CT.setMargin(new Insets(2, 4, 2, 2));
		panel_2.add(CT);
		CT.setBackground(SystemColor.inactiveCaptionBorder);
		
		MU = new JCheckBox("MU");
		MU.setSelected(true);
		panel_2.add(MU);
		MU.setBackground(SystemColor.inactiveCaptionBorder);
		
		ALL1.addItemListener(this);
		RT.addItemListener(this);
		PT.addItemListener(this);
		CT.addItemListener(this);
		MU.addItemListener(this);
		
		
		//Code Structure
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(SystemColor.inactiveCaptionBorder);
		panel_1.add(panel_3);
		
		JLabel lblCodeStructure = new JLabel("Code Structure :");
		lblCodeStructure.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblCodeStructure.setPreferredSize(new Dimension(145, 21));
		lblCodeStructure.setFont(new Font("Century Gothic", Font.PLAIN, 18));
		panel_3.add(lblCodeStructure);
		
		ALL2 = new JCheckBox("ALL ");
		ALL2.setSelected(true);
		ALL2.setBackground(SystemColor.inactiveCaptionBorder);
		panel_3.add(ALL2);
		
		bsbs = new JCheckBox("bs-bs ");
		bsbs.setSelected(true);
		bsbs.setBackground(SystemColor.inactiveCaptionBorder);
		panel_3.add(bsbs);
		
		bcbc = new JCheckBox("bc-bc ");
		bcbc.setSelected(true);
		bcbc.setBackground(SystemColor.inactiveCaptionBorder);
		panel_3.add(bcbc);
		
		lclc = new JCheckBox("lc-lc");
		lclc.setSelected(true);
		lclc.setMargin(new Insets(2, 4, 2, 2));
		lclc.setBackground(SystemColor.inactiveCaptionBorder);
		panel_3.add(lclc);
		
		sbsb = new JCheckBox("sb-sb");
		sbsb.setSelected(true);
		sbsb.setBackground(SystemColor.inactiveCaptionBorder);
		panel_3.add(sbsb);
		
		slsl = new JCheckBox("sl-sl");
		slsl.setSelected(true);
		panel_3.add(slsl);
		slsl.setBackground(SystemColor.inactiveCaptionBorder);
		
		bsbc = new JCheckBox("bs-bc");
		bsbc.setSelected(true);
		panel_3.add(bsbc);
		bsbc.setBackground(SystemColor.inactiveCaptionBorder);
		
		bslc = new JCheckBox("bs-lc");
		bslc.setSelected(true);
		panel_3.add(bslc);
		bslc.setBackground(SystemColor.inactiveCaptionBorder);

		bssb = new JCheckBox("bs-sb");
		bssb.setSelected(true);
		panel_3.add(bssb);
		bssb.setBackground(SystemColor.inactiveCaptionBorder);
		
		bssl = new JCheckBox("bs-sl");
		bssl.setSelected(true);
		panel_3.add(bssl);
		bssl.setBackground(SystemColor.inactiveCaptionBorder);
		
		bclc = new JCheckBox("bc-lc");
		bclc.setSelected(true);
		panel_3.add(bclc);
		bclc.setBackground(SystemColor.inactiveCaptionBorder);
		
		bcsb = new JCheckBox("bc-sb");
		bcsb.setSelected(true);
		panel_3.add(bcsb);
		bcsb.setBackground(SystemColor.inactiveCaptionBorder);
		
		bcsl = new JCheckBox("bc-sl");
		bcsl.setSelected(true);
		panel_3.add(bcsl);
		bcsl.setBackground(SystemColor.inactiveCaptionBorder);
		
		lcsb = new JCheckBox("lc-sb");
		lcsb.setSelected(true);
		panel_3.add(lcsb);
		lcsb.setBackground(SystemColor.inactiveCaptionBorder);
		
		lcsl = new JCheckBox("lc-sl");
		lcsl.setSelected(true);
		panel_3.add(lcsl);
		lcsl.setBackground(SystemColor.inactiveCaptionBorder);
		
		sbsl = new JCheckBox("sb-sl");
		sbsl.setSelected(true);
		panel_3.add(sbsl);
		sbsl.setBackground(SystemColor.inactiveCaptionBorder);
		
		ALL2.addItemListener(this);
		bsbs.addItemListener(this);bcbc.addItemListener(this);lclc.addItemListener(this);sbsb.addItemListener(this);slsl.addItemListener(this);
		bsbc.addItemListener(this);bslc.addItemListener(this);bssb.addItemListener(this);bssl.addItemListener(this);bclc.addItemListener(this);
		bcsb.addItemListener(this);bcsl.addItemListener(this);lcsb.addItemListener(this);lcsl.addItemListener(this);sbsl.addItemListener(this);
		
		
		//Method Span
		JPanel panel_4 = new JPanel();
		panel_1.add(panel_4);
		panel_4.setBackground(SystemColor.inactiveCaptionBorder);
		
		JLabel lblMethodSpan = new JLabel("   Method Span :");
		lblMethodSpan.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblMethodSpan.setPreferredSize(new Dimension(145, 21));
		lblMethodSpan.setFont(new Font("Century Gothic", Font.PLAIN, 18));
		panel_4.add(lblMethodSpan);
		
		ALL3 = new JCheckBox("ALL ");
		ALL3.setSelected(true);
		ALL3.setBackground(SystemColor.inactiveCaptionBorder);
		panel_4.add(ALL3);
		
		SS = new JCheckBox("SS    ");
		SS.setSelected(true);
		SS.setBackground(SystemColor.inactiveCaptionBorder);
		panel_4.add(SS);
		
		DS = new JCheckBox("DS    ");
		DS.setSelected(true);
		DS.setBackground(SystemColor.inactiveCaptionBorder);
		panel_4.add(DS);
		
		DD = new JCheckBox("DD");
		DD.setSelected(true);
		DD.setMargin(new Insets(2, 4, 2, 2));
		DD.setBackground(SystemColor.inactiveCaptionBorder);
		panel_4.add(DD);
		
		ALL3.addItemListener(this);
		SS.addItemListener(this);
		DS.addItemListener(this);
		DD.addItemListener(this);


		//Cause
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setPreferredSize(new Dimension(800, 21));
		panel_4.add(lblNewLabel);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBackground(SystemColor.inactiveCaptionBorder);
		panel_1.add(panel_5);
		
		JLabel lblCause = new JLabel(" Cause :");
		lblCause.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCause.setPreferredSize(new Dimension(145, 21));
		lblCause.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblCause.setFont(new Font("Century Gothic", Font.PLAIN, 18));
		panel_5.add(lblCause);
		
		ALL4 = new JCheckBox("ALL ");
		ALL4.setSelected(true);
		ALL4.setBackground(SystemColor.inactiveCaptionBorder);
		panel_5.add(ALL4);
		
		NOS = new JCheckBox("NS    ");
		NOS.setSelected(true);
		NOS.setMargin(new Insets(2, 2, 2, 0));
		NOS.setBackground(SystemColor.inactiveCaptionBorder);
		panel_5.add(NOS);
		
		PartS = new JCheckBox("PS    ");
		PartS.setSelected(true);
		PartS.setMargin(new Insets(2, 4, 2, 2));
		PartS.setBackground(SystemColor.inactiveCaptionBorder);
		panel_5.add(PartS);
		
		INCS = new JCheckBox("IS");
		INCS.setSelected(true);
		INCS.setMargin(new Insets(2, 4, 2, 2));
		INCS.setBackground(SystemColor.inactiveCaptionBorder);
		panel_5.add(INCS);

		ALL4.addItemListener(this);
		NOS.addItemListener(this);
		PartS.addItemListener(this);
		INCS.addItemListener(this);
		
		
		
		toolBar = new JToolBar();
		toolBar.setAutoscrolls(true);
		toolBar.setPreferredSize(new Dimension(200, 10));
		toolBar.setOrientation(SwingConstants.VERTICAL);
		toolBar.setFloatable(false);
		toolBar.setBackground(SystemColor.inactiveCaptionBorder);
		toolBar.setForeground(Color.DARK_GRAY);
		toolBar.setFont(new Font("Century Gothic", Font.PLAIN, 22));
		//toolBar.setLocation(10,getWidth()-100);
		FlowLayout fl_toolBar = new FlowLayout();
		toolBar.setLayout(fl_toolBar);
		JButton button1 = new JButton("Save   ");
		button1.setMargin(new Insets(20, 2, 2, 14));
		button1.setHorizontalAlignment(SwingConstants.LEFT);
		button1.setPreferredSize(new Dimension(135, 60));
		toolBar.add(button1);
		button1.setOpaque(false);
		button1.setForeground(Color.DARK_GRAY);
		button1.setBackground(SystemColor.inactiveCaptionBorder);
		button1.setFont(new Font("Century Gothic", Font.PLAIN, 23));
		button1.setFocusable(false);
		button1.setIcon(new ImageIcon(ManageBenchmarkFrame.class.getResource("/com/sun/java/swing/plaf/windows/icons/FloppyDrive.gif")));
		
				button1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						updateStaticVariables();
					}
				});  
		
		JButton btnSaveAs = new JButton("Save AS   ");
		btnSaveAs.setHorizontalAlignment(SwingConstants.LEFT);
		btnSaveAs.setPreferredSize(new Dimension(135, 40));
		toolBar.add(btnSaveAs);
		btnSaveAs.setOpaque(false);
		btnSaveAs.setIcon(new ImageIcon(ManageBenchmarkFrame.class.getResource("/com/sun/java/swing/plaf/windows/icons/FloppyDrive.gif")));
		btnSaveAs.setForeground(Color.DARK_GRAY);
		btnSaveAs.setFont(new Font("Century Gothic", Font.PLAIN, 23));
		btnSaveAs.setFocusable(false);
		btnSaveAs.setBackground(SystemColor.inactiveCaptionBorder);
		btnSaveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    JFileChooser chooser = new JFileChooser();  
			    File file;    
			    FileNameExtensionFilter filter = new FileNameExtensionFilter("xml file(*.xml)", "xml");  
			    chooser.setFileFilter(filter);  
			    int option = chooser.showSaveDialog(null);  
			    if(option==JFileChooser.APPROVE_OPTION){
			    	file = chooser.getSelectedFile();
			    	if(file != null) {
			    		updateStaticVariables(file.getAbsolutePath());
			    	}
			    }
			}
		});  
		JButton button2 = new JButton("Cancle  ");
		button2.setHorizontalAlignment(SwingConstants.LEFT);
		button2.setPreferredSize(new Dimension(135, 40));
		toolBar.add(button2);
		button2.setOpaque(false);
		button2.setBackground(SystemColor.inactiveCaptionBorder);
		button2.setForeground(Color.DARK_GRAY);
		button2.setFont(new Font("Century Gothic", Font.PLAIN, 22));
		button2.setFocusable(false);
		button2.setIcon(new ImageIcon(ManageBenchmarkFrame.class.getResource("/org/jdesktop/swingx/plaf/basic/resources/clear.gif")));
		button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		panel.add(toolBar, BorderLayout.EAST);
		getContentPane().add(panel,BorderLayout.NORTH);

		treetable.addMouseListener(this);  


		setForeground(Color.DARK_GRAY);
		setFont(new Font("Century Gothic", Font.PLAIN, 22));
		popMenu = new JPopupMenu();  
		popMenu.add(addItem1); 
		popMenu.add(addItem2);  
		popMenu.add(delItem1);  
		popMenu.add(delItem2);
		//add test cases
		addItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(curClick_X>0&&curClick_Y>0) {
					TreePath path = treetable.getPathForLocation(curClick_X,curClick_Y);
					DefaultMutableTreeTableNode curNode = (DefaultMutableTreeTableNode) path.getLastPathComponent();
					String name = ((MyTreeTableNode)curNode.getUserObject()).getName();
					if(name.indexOf("Case")>=0) {
						int nodeNum = curNode.getParent().getChildCount();
						MyTreeTableNode leaf = new MyTreeTableNode("TestCase_"+(nodeNum));
						DefaultMutableTreeTableNode tmp = new DefaultMutableTreeTableNode(leaf);
						((DefaultMutableTreeTableNode) path.getParentPath().getLastPathComponent()).add(tmp);
					}else {
						int nodeNum = curNode.getParent().getParent().getChildCount();
						MyTreeTableNode leaf = new MyTreeTableNode("TestCase_"+(nodeNum));
						DefaultMutableTreeTableNode tmp = new DefaultMutableTreeTableNode(leaf);
						((DefaultMutableTreeTableNode) path.getParentPath().getParentPath().getLastPathComponent()).add(tmp);
					}
					treetable.updateUI();
				}
			}

		});  
		//add race
		addItem2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(curClick_X>0&&curClick_Y>0) {
					TreePath path = treetable.getPathForLocation(curClick_X,curClick_Y);
					DefaultMutableTreeTableNode curNode = (DefaultMutableTreeTableNode) path.getLastPathComponent();
					String name = ((MyTreeTableNode)curNode.getUserObject()).getName();
					if(name.indexOf("Case")>=0) {
						int nodeNum = curNode.getChildCount();
						String parentID = ((MyTreeTableNode)curNode.getUserObject()).getName();
						MyTreeTableNode leaf = new MyTreeTableNode("race"+(parentID.substring(8,9))+"_"+(nodeNum+1));
						DefaultMutableTreeTableNode tmp = new DefaultMutableTreeTableNode(leaf);
						curNode.add(tmp);
					}else {
						int nodeNum = curNode.getParent().getChildCount();
						String parentID = ((MyTreeTableNode)curNode.getUserObject()).getName();
						MyTreeTableNode leaf = new MyTreeTableNode("race"+(parentID.substring(4,5))+"_"+(nodeNum+1));
						DefaultMutableTreeTableNode tmp = new DefaultMutableTreeTableNode(leaf);
						((DefaultMutableTreeTableNode) path.getParentPath().getLastPathComponent()).add(tmp);
					}
					treetable.updateUI();
				}
			}

		});  
		//delete UseCase Function
		delItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(curClick_X>0&&curClick_Y>0) {
					TreePath path = treetable.getPathForLocation(curClick_X,curClick_Y);
					DefaultMutableTreeTableNode toBeDeletedNode = (DefaultMutableTreeTableNode) path.getLastPathComponent();
					treetable.getTreeSelectionModel().clearSelection();
					toBeDeletedNode.removeFromParent();
				}
				treetable.updateUI();
			}
		}); 
		delItem2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(curClick_X>0&&curClick_Y>0) {
					TreePath path = treetable.getPathForLocation(curClick_X,curClick_Y);
					DefaultMutableTreeTableNode toBeDeletedNode = (DefaultMutableTreeTableNode) path.getLastPathComponent();
					treetable.getTreeSelectionModel().clearSelection();
					toBeDeletedNode.removeFromParent();
				}
				treetable.updateUI();
			}
		});  
	}
	public void updateStaticVariables(){
		updateStaticVariables(null);
	}

	public void updateStaticVariables(String saveFilePath){
		boolean isSave = true;
		int pathIndex=0;
		Map<String, Report> reports = new HashMap<String, Report>();
		TreePath rootPath = treetable.getPathForLocation(0, 0).getParentPath();
		DefaultMutableTreeTableNode rootNode = (DefaultMutableTreeTableNode)rootPath.getLastPathComponent();

		treetable.expandAll();

		int caseNum = rootNode.getChildCount();
		for(int i=0;i<caseNum;i++) {
			pathIndex++;
			MyTreeTableNode curUseCaseMyTreeNode = (MyTreeTableNode)rootNode.getChildAt(i).getUserObject();

			if(curUseCaseMyTreeNode.getValue1().contains("<")||curUseCaseMyTreeNode.getValue1().contains(">")||curUseCaseMyTreeNode.getValue1().contains("&")||curUseCaseMyTreeNode.getValue1().contains("'")||curUseCaseMyTreeNode.getValue1().contains("\"")){
				JOptionPane.showMessageDialog(null, "Testcase name can't contain '<','>','&',''','\"'");
				isSave = false;
				break;
			}else if(reports.containsKey(curUseCaseMyTreeNode.getValue1())){//test case is repeated
				JOptionPane.showMessageDialog(null, "Testcase name must be unique !");
				isSave = false;
				break;
			}else if(curUseCaseMyTreeNode.getValue1().equals("")){//test case name is null
				JOptionPane.showMessageDialog(null, "Testcase name can't be null !");
				isSave = false;
				break;
			}else {
				Set<Race> races = new HashSet<Race>();
//				System.out.println(((DefaultMutableTreeTableNode)  treetable.getPathForRow(pathIndex-1).getLastPathComponent()).getUserObject());
				DefaultMutableTreeTableNode curUseCaseNode = (DefaultMutableTreeTableNode)  treetable.getPathForRow(pathIndex-1).getLastPathComponent();
				for(int j=0;j<curUseCaseNode.getChildCount();j++) {
					pathIndex++;
					MyTreeTableNode curRaceNode = (MyTreeTableNode)curUseCaseNode.getChildAt(j).getUserObject();
					/*
					 * 	private String line1;
						private String line2;
						private String variable;
						private String packageClass;
						private String detail;
						private String time;
						private String variableType;
						private String codeStructure;
						private String methodSpan;
						private String sensitiveBranch;
						private String sensitiveLoop;
						private String cause;
						private String commonUsage;
						private String bug;
					 */					
					Race newRace = new Race(curRaceNode.getValue2(), curRaceNode.getValue3(), curRaceNode.getValue4(), curRaceNode.getValue1(), curRaceNode.getValue5(),
											curRaceNode.getValue6(), curRaceNode.getValue7(), curRaceNode.getValue8(), curRaceNode.getValue9(), curRaceNode.getValue10(),
											curRaceNode.getValue11(), curRaceNode.getValue12(), curRaceNode.getValue13(), curRaceNode.getValue14());
					if(!isValidate(newRace,curUseCaseMyTreeNode)) {//the content has problems
						treetable.getTreeSelectionModel().setSelectionPath(treetable.getPathForLocation(pathIndex, 0));
						isSave = false;
						break;
					}else if(curRaceNode.getValue3().equals("")||curRaceNode.getValue2().equals("")){//line number is null
						JOptionPane.showMessageDialog(null, "line number can't be null !");
						treetable.getTreeSelectionModel().setSelectionPath(treetable.getPathForLocation(pathIndex, 0));
						isSave = false;
						break;
					}else {
						newRace.Update();////Place the smaller line number in the first position 
						Iterator it = races.iterator();
						while(it.hasNext()){
							Race race = (Race) it.next();
							if(race.getLine1().equals(curRaceNode.getValue3())&&race.getLine2().equals(curRaceNode.getValue4())
									||race.getLine2().equals(curRaceNode.getValue3())&&race.getLine1().equals(curRaceNode.getValue4())){

								JOptionPane.showMessageDialog(null, "race repetition!");
								isSave = false;
								break;
							}
						}
						if(isSave) {
							races.add(newRace);
						}else {
							break;
						}
					}
				}
				if(isSave) {	
					if(races.size()>0){
						Report tempReport = new Report(races,curUseCaseMyTreeNode.getValue1(),curUseCaseMyTreeNode.getValue7());
						reports.put(curUseCaseMyTreeNode.getValue1(), tempReport);
					}
				}else {
					break;
				}
			}
		}
		if(isSave) {//save to file
			//eliminate all the element in memory
			ParseXml parse = new ParseXml();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			factory.setExpandEntityReferences(false);
			Document doc = null;
			try {
				parse.validateXml(fileAbsolutePath);
				doc = parse.buildDocument(fileAbsolutePath);
			} catch (SAXException | IOException e1) {
				e1.printStackTrace();
			}
			for(int i=0;i<Reports.programNames.size();i++) {
				Report re = Reports.reports.get(Reports.programNames.get(i));
				if (re != null) {
					Set<Race> races = re.getRaces();
					Iterator<Race> reIt = races.iterator();
					while (reIt.hasNext()) {
						parse.deleteElement(doc,Reports.programNames.get(i),reIt.next());
					}
				}
			}

			Iterator<Entry<String, Report>> iter = reports.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Report> entry = iter.next();
				Report tmpReport = entry.getValue();
				String tmpCaseName = entry.getKey();
				Iterator<Race> it = tmpReport.getRaces().iterator();
				while (it.hasNext()) {
					parse.addElement(doc,tmpCaseName, it.next(),tmpReport.getTime());
					try {
						if(!parse.writeDomToXml(doc,saveFilePath)){
							JOptionPane.showMessageDialog(null, "Write/Read file Failed ！");
							return;
						}
					} catch (TransformerException e) {//| SAXException | IOException e) {
						e.printStackTrace();
					}
				}
			}
			Reports.removeAllBenchmakrs();
			treetable.removeAll();
			ParseXml parser = new ParseXml();
			Document validationResult = parser.validateXml(fileAbsolutePath);
			if (validationResult != null) {
				Reports.removeAllBenchmakrs();
				DomToEntity convert = new DomToEntity();
				convert.startDom(validationResult);
			} else {
			}
			treetable.updateUI();
			JOptionPane.showMessageDialog(null, "Successfully save the benchmarks!");
//
//			dispose();

		}
	}

	public boolean isValidate(Race race, MyTreeTableNode curUseCaseMyTreeNode){
		String line1 = race.getLine1();
		String line2 = race.getLine2();
		String totalTime = curUseCaseMyTreeNode.getValue7();
		if(totalTime.lastIndexOf("s")>=0){
			totalTime = totalTime.substring(0, totalTime.lastIndexOf("s"));
		}
		String variable = race.getVariable();
		String packageClass = race.getPackageClass();
		Pattern isIntegerPattern = Pattern.compile("[0-9]*");
		Matcher isInteger = isIntegerPattern.matcher(line1);
		Pattern isFloatPattern = Pattern.compile("^\\d+(\\.\\d+)?$");
		Matcher isFloat = isFloatPattern.matcher(totalTime);
		
		if(!totalTime.equals("")){
			if( !isFloat.matches()){
				JOptionPane.showMessageDialog(null, "Time ("+totalTime+") must be a nonnegative floating number!");
				return false;
			}else{
				curUseCaseMyTreeNode.setValue7(Float.valueOf(totalTime).toString()+"s");
			}
		}
		if( !isInteger.matches() ){
			JOptionPane.showMessageDialog(null, "line1 must be an integer, can not contain letters, characters and so on !");
			return false;
		}
		isInteger = isIntegerPattern.matcher(line2);
		if( !isInteger.matches() ){
			JOptionPane.showMessageDialog(null, "line2 must be an integer, can not contain letters, characters and so on !");
			return false;
		}

		if(line1.contains("<")||line1.contains(">")||line1.contains("&")||line1.contains("'")||line1.contains("\"")){
			JOptionPane.showMessageDialog(null, "line1 can't contain '<','>','&',''','\"'");
			return false;
		}else if(line2.contains("<")||line2.contains(">")||line2.contains("&")||line2.contains("'")||line2.contains("\"")){
			JOptionPane.showMessageDialog(null, "line2 can't contain '<','>','&',''','\"'");
			return false;
		}else if(variable.contains("<")||variable.contains(">")||variable.contains("&")||variable.contains("'")||variable.contains("\"")){
			JOptionPane.showMessageDialog(null, "variable can't contain '<','>','&',''','\"'");
			return false;
		}else if(packageClass.contains("<")||packageClass.contains(">")||packageClass.contains("&")||packageClass.contains("'")||packageClass.contains("\"")){
			JOptionPane.showMessageDialog(null, "packageClass can't contain '<','>','&',''','\"'");
			return false;
		}
		return true;
	}		
	/**
	 * construct a treetablemodel, and return root node
	 *
	 * @return root node
	 */
	private DefaultMutableTreeTableNode createDummyData() {

		root = new DefaultMutableTreeTableNode(new MyTreeTableNode("root"));

		for (int i = 0; i < Reports.programNames.size(); i++) {
			Report reports = Reports.reports.get(Reports.programNames.get(i));
			if(reports!=null){
				MyTreeTableNode tb = new MyTreeTableNode("TestCase_"+i,reports.getName(),"","","","","",reports.getTime(),"","","","","","","");
				DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(tb);
	
				if (reports != null) {
					Set<Race> races = reports.getRaces();
					Race race = null;
					int count = 0;
					Iterator<Race> it = races.iterator();
					while (it.hasNext()) {
						count++;
						race = it.next();
						MyTreeTableNode leaf = new MyTreeTableNode("race"+i+"_"+count,race.getPackageClass(),race.getLine1(),race.getLine2(),
								race.getVariable(),race.getDetail(),race.getVariableType(),race.getTime(),race.getCodeStructure(),race.getMethodSpan(),race.getSensitiveBranch(),
								race.getSensitiveLoop(),race.getCause(),race.getCommonUsage(),"");
						DefaultMutableTreeTableNode tmp = new DefaultMutableTreeTableNode(leaf);
						node.add(tmp);
					}
				}
				root.add(node);
			}
		}
		return root;
	}

	
	private void updateDummyData() {

		//loop to find if the races  satisfy  the checkboxs
		Map<String, Set<Race>> cloneReports = Reports.cloneReports();
		for (int i = 0; i < Reports.programNames.size(); i++) {
			Set<Race> cloneRaces = cloneReports.get(Reports.programNames.get(i));
			if (cloneRaces != null) {
				Race race = null;
				Iterator<Race> it = cloneRaces.iterator();
				while (it.hasNext()) {
					race = it.next();
					if(!ALL1.isSelected()){//filter out specified variable type
						if((!RT.isSelected()&&race.getVariableType().equals(RT.getText().trim()))
								||(!PT.isSelected()&&race.getVariableType().equals(PT.getText().trim()))
								||(!CT.isSelected()&&race.getVariableType().equals(CT.getText().trim()))
								||(!MU.isSelected()&&race.getVariableType().equals(MU.getText().trim()))){
							it.remove();
							continue;
						}
					}
					if(!ALL2.isSelected()){//filter out specified code structure
						if(     (!bsbs.isSelected()&&race.getCodeStructure().equals(bsbs.getText().trim()))  ||   (!bcbc.isSelected()&&race.getCodeStructure().equals(bcbc.getText().trim()))
							||  (!lclc.isSelected()&&race.getCodeStructure().equals(lclc.getText().trim()))  ||   (!sbsb.isSelected()&&race.getCodeStructure().equals(sbsb.getText().trim()))
							||  (!slsl.isSelected()&&race.getCodeStructure().equals(slsl.getText().trim()))  ||   (!bsbc.isSelected()&&race.getCodeStructure().equals(bsbc.getText().trim()))
							||  (!bslc.isSelected()&&race.getCodeStructure().equals(bslc.getText().trim()))  ||   (!bssb.isSelected()&&race.getCodeStructure().equals(bssb.getText().trim()))
							||  (!bssl.isSelected()&&race.getCodeStructure().equals(bssl.getText().trim()))  ||   (!bclc.isSelected()&&race.getCodeStructure().equals(bclc.getText().trim()))
							||  (!bcsb.isSelected()&&race.getCodeStructure().equals(bcsb.getText().trim()))  ||   (!bcsl.isSelected()&&race.getCodeStructure().equals(bcsl.getText().trim()))
							||  (!lcsl.isSelected()&&race.getCodeStructure().equals(lcsl.getText().trim()))  ||   (!sbsl.isSelected()&&race.getCodeStructure().equals(sbsl.getText().trim()))
							||  (!lcsb.isSelected()&&race.getCodeStructure().equals(lcsb.getText().trim()))
						){
							it.remove();
							continue;
						}
					}
					if(!ALL3.isSelected()){//filter out specified span of data race
						if((!SS.isSelected()&&race.getMethodSpan().equals(SS.getText().trim()))||(!DS.isSelected()&&race.getMethodSpan().equals(DS.getText().trim()))
								||(!DD.isSelected()&&race.getMethodSpan().equals(DD.getText().trim()))){
							it.remove();
							continue;
						}
					}
					if(!ALL4.isSelected()){//filter out specified cause causing data race
						if((!NOS.isSelected()&&race.getCause().equals(NOS.getText().trim()))||(!PartS.isSelected()&&race.getCause().equals(PartS.getText().trim()))
								||(!INCS.isSelected()&&race.getCause().equals(INCS.getText().trim()))){
							it.remove();
							continue;
						}
					}
				}
			}
		}
		
		//clear the treetable
		treetable.expandAll();
		
		int caseNum = root.getChildCount();

		treetable.getTreeSelectionModel().clearSelection();
		for(int i=caseNum-1;i>=0;i--) {
			((DefaultMutableTreeTableNode)root.getChildAt(i)).removeFromParent();
		}
		//add the cloneReports which satisfy the checkboxs

		for (int i = 0; i < Reports.programNames.size(); i++) {
			Set<Race> races = cloneReports.get(Reports.programNames.get(i));

			MyTreeTableNode tb = new MyTreeTableNode("TestCase_"+i,Reports.programNames.get(i),"","","","","","","","","","","","","");
			DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(tb);

			if (races != null) {
				
				Race race = null;
				int count = 0;
				Iterator<Race> it = races.iterator();
				while (it.hasNext()) {
					count++;
					race = it.next();
					MyTreeTableNode leaf = new MyTreeTableNode("race"+i+"_"+count,race.getPackageClass(),race.getLine1(),race.getLine2(),
							race.getVariable(),race.getDetail(),race.getVariableType(),race.getTime(),race.getCodeStructure(),race.getMethodSpan(),race.getSensitiveBranch(),
							race.getSensitiveLoop(),race.getCause(),race.getCommonUsage(),"");
					DefaultMutableTreeTableNode tmp = new DefaultMutableTreeTableNode(leaf);
					node.add(tmp);
				}
				if(count>0)root.add(node);
			}
		}
		treetable.updateUI();
		treetable.expandAll();
		treetable.updateUI();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		TreePath path = treetable.getPathForLocation(e.getX(), e.getY()); // it is important to use this method.
		if (path == null) {  
			return;  
		}  
		treetable.getTreeSelectionModel().setSelectionPath(path);//.setSelectionPath(path);  
		if (e.getButton() == 3) { 
			curClick_X=e.getX();
			curClick_Y=e.getY();
			if(((DefaultMutableTreeTableNode)path.getLastPathComponent()).isLeaf()) {
				popMenu.remove(delItem1);
				popMenu.add(delItem2);
			}else {
				popMenu.remove(delItem2);
				popMenu.add(delItem1);
			}
			popMenu.show(treetable, e.getX(), e.getY());  
		}else if (e.getButton() == 1) { 
			curClick_X=e.getX();
			curClick_Y=e.getY();
			DefaultMutableTreeTableNode tempNode = ((DefaultMutableTreeTableNode)path.getLastPathComponent());
			if(curNode == (MyTreeTableNode)tempNode.getUserObject()){
				//Clicking the same line dose not response
			}else {
				isInit = false;
				curNode = (MyTreeTableNode)tempNode.getUserObject();
				if(tempNode.isLeaf()){
					detailTextArea.setText(curNode.getValue5());
					detailTextArea.setFocusable(true);
					detailTextArea.setEditable(true);
				}else {
					detailTextArea.setText("");
					detailTextArea.setFocusable(false);
					detailTextArea.setEditable(false);
					curNode=null;
				}
			}
		} 
	}

	//A custom table plotter that automatically wraps JTextPane, but cannot be vertically centered, 
	//and JXTreeTable does not allow different peers to set different heights, so it is not currently in use
	class TableViewRenderer extends JIMSendTextPane implements  TreeCellRenderer, TableCellRenderer
	{ 
		public TableViewRenderer() 
		{ 
//			SimpleAttributeSet aSet = new SimpleAttributeSet();   
//			StyleConstants.setForeground(aSet, Color.blue);  
//			StyleConstants.setBackground(aSet, Color.orange);  
//			StyleConstants.setFontFamily(aSet, "lucida bright italic");  
//			StyleConstants.setFontSize(aSet, 24);  
//			doc.setCharacterAttributes(105, doc.getLength()-105, aSet, false);  

			SimpleAttributeSet bSet = new SimpleAttributeSet();  
			StyleConstants.setAlignment(bSet, StyleConstants.ALIGN_JUSTIFIED);  
			StyleConstants.setAlignment(bSet, SwingConstants.CENTER);  

//			StyleConstants.setUnderline(bSet, true);  
//			StyleConstants.setFontFamily(bSet, "lucida typewriter bold");  
//			StyleConstants.setFontSize(bSet, 24);  

			StyledDocument doc = this.getStyledDocument();  
			doc.setParagraphAttributes(0, 54, bSet, false);  
		}
		@Override
		public Component getTableCellRendererComponent(JTable table, Object obj,boolean isSelected, boolean hasFocus, int row, int column) 
		{ 
			this.setFont(new Font("Century Gothic", Font.PLAIN, 22));
			setBackground(isSelected ? new Color(179,222,255) : Color.white);

			int maxPreferredHeight = 0; 
			// compute feasible height for current line
			for (int i = 0; i < table.getColumnCount(); i++) { 
				setText("" + table.getValueAt(row, i)); 
				setSize(table.getColumnModel().getColumn(column).getWidth(), 0); 
				maxPreferredHeight = Math.max(maxPreferredHeight, getPreferredSize().height); 
			}

			if (getPreferredSize().height < maxPreferredHeight){
				treetable.setRowHeight(maxPreferredHeight);
			}
			setText(obj == null ? "" : obj.toString()); 
			return this;
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			return this;
		}
	} 
	public class JIMSendTextPane extends JTextPane{  

		//Automatically split line

		private class WarpEditorKit extends StyledEditorKit {  

			private ViewFactory defaultFactory = new WarpColumnFactory();  

			@Override  
			public ViewFactory getViewFactory() {  
				return defaultFactory;  
			}  
		}  

		private class WarpColumnFactory implements ViewFactory {  

			@Override
			public View create( javax.swing.text.Element  elem){  
				String kind = elem.getName();  
				if (kind != null) {  
					if (kind.equals(AbstractDocument.ContentElementName)) {  
						return new WarpLabelView(elem);  
					} else if (kind.equals(AbstractDocument.ParagraphElementName)) {  
						return new ParagraphView(elem);  
					} else if (kind.equals(AbstractDocument.SectionElementName)) {  
						return new BoxView(elem, View.Y_AXIS);  
					} else if (kind.equals(StyleConstants.ComponentElementName)) {  
						return new ComponentView(elem);  
					} else if (kind.equals(StyleConstants.IconElementName)) {  
						return new IconView(elem);  
					}  
				}  

				// default to text display  
				return new LabelView(elem);  
			}
		}  

		private class WarpLabelView extends LabelView{  

			public WarpLabelView(javax.swing.text.Element elem) {  
				super(elem);
			}  

			@Override  
			public float getMinimumSpan(int axis) {  
				switch (axis) {  
				case View.X_AXIS:  
					return 0;  
				case View.Y_AXIS:  
					return super.getMinimumSpan(axis);  
				default:  
					throw new IllegalArgumentException("Invalid axis: " + axis);  
				}  
			}  
		}  

		public JIMSendTextPane() {  
			super();  
			this.setEditorKit(new WarpEditorKit());  
		}
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	/*
	 * when choose or not choose one checkbox, first remove the ItemListener, second check the reference checkbox's state ( eg. ALL )
	 * third, add the ItemListener
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBox box = (JCheckBox) e.getSource();
		ItemListener listener = box.getItemListeners()[0];
		ALL1.removeItemListener(listener); PT.removeItemListener(listener); RT.removeItemListener(listener); CT.removeItemListener(listener); MU.removeItemListener(listener);
		ALL2.removeItemListener(listener); ALL3.removeItemListener(listener); SS.removeItemListener(listener); DD.removeItemListener(listener); DS.removeItemListener(listener);
		bsbs.removeItemListener(listener);bcbc.removeItemListener(listener);lclc.removeItemListener(listener);sbsb.removeItemListener(listener);slsl.removeItemListener(listener);
		bsbc.removeItemListener(listener);bslc.removeItemListener(listener);bssb.removeItemListener(listener);bssl.removeItemListener(listener);bclc.removeItemListener(listener);
		bcsb.removeItemListener(listener);bcsl.removeItemListener(listener);lcsb.removeItemListener(listener);lcsl.removeItemListener(listener);sbsl.removeItemListener(listener);
		ALL4.removeItemListener(listener); NOS.removeItemListener(listener); PartS.removeItemListener(listener); INCS.removeItemListener(listener);
				
		if(box==ALL1){
			if(ALL1.isSelected()){
				
				PT.setSelected(true);
				RT.setSelected(true);
				CT.setSelected(true);
				MU.setSelected(true);
				//refresh table
			}else{
				PT.setSelected(false);
				RT.setSelected(false);
				CT.setSelected(false);
				MU.setSelected(false);
				//refresh table
			}
		}else if(box==PT||box==RT||box==CT||box==MU){
			if(box.isSelected()){
				if(PT.isSelected()&&RT.isSelected()&&CT.isSelected()&&MU.isSelected()) ALL1.setSelected(true);
				//refresh table
			}else{
				 ALL1.setSelected(false);
				//refresh table
			}
		}else if(box==ALL2){
			if(ALL2.isSelected()){
				bsbs.setSelected(true);bcbc.setSelected(true);lclc.setSelected(true);sbsb.setSelected(true);slsl.setSelected(true);
				bsbc.setSelected(true);bslc.setSelected(true);bssb.setSelected(true);bssl.setSelected(true);bclc.setSelected(true);
				bcsb.setSelected(true);bcsl.setSelected(true);lcsb.setSelected(true);lcsl.setSelected(true);sbsl.setSelected(true);
				//refresh table
			}else{
				bsbs.setSelected(false);bcbc.setSelected(false);lclc.setSelected(false);sbsb.setSelected(false);slsl.setSelected(false);
				bsbc.setSelected(false);bslc.setSelected(false);bssb.setSelected(false);bssl.setSelected(false);bclc.setSelected(false);
				bcsb.setSelected(false);bcsl.setSelected(false);lcsb.setSelected(false);lcsl.setSelected(false);sbsl.setSelected(false);
			}
		}else if(box==bsbs||box==bcbc||box==lclc||box==sbsb||box==slsl||box==bsbc||box==bslc
				||box==bssb||box==bssl||box==bclc||box==bcsb||box==bcsl||box==lcsb||box==lcsl||box==sbsl){

			if(		bsbs.isSelected()&&bcbc.isSelected()&&lclc.isSelected()&&sbsb.isSelected()&&slsl.isSelected()&&
					bsbc.isSelected()&&bslc.isSelected()&&bssb.isSelected()&&bssl.isSelected()&&bclc.isSelected()&&
					bcsb.isSelected()&&bcsl.isSelected()&&lcsb.isSelected()&&lcsl.isSelected()&&sbsl.isSelected()
			  ){ 
				ALL2.setSelected(true);
				//refresh table
			}else{
				ALL2.setSelected(false);
			}
		}else if(box==ALL3){
			if(ALL3.isSelected()){
				SS.setSelected(true);
				DD.setSelected(true);
				DS.setSelected(true);
				//refresh table
			}else{
				SS.setSelected(false);
				DD.setSelected(false);
				DS.setSelected(false);
			}
		}else if(box==SS||box==DD||box==DS){
			if(box.isSelected()){
				if(SS.isSelected()&&DD.isSelected()&&DS.isSelected()) ALL3.setSelected(true);
			}else{
				 ALL3.setSelected(false);
			}
		}else if(box==ALL4){
			if(ALL4.isSelected()){
				NOS.setSelected(true);
				PartS.setSelected(true);
				INCS.setSelected(true);
				//refresh table
			}else{
				NOS.setSelected(false);
				PartS.setSelected(false);
				INCS.setSelected(false);
			}
		}else if(box==NOS||box==PartS||box==INCS){
			if(box.isSelected()){
				if(NOS.isSelected()&&PartS.isSelected()&&INCS.isSelected()) ALL4.setSelected(true);
			}else{
				ALL4.setSelected(false);
			}
		} 
		ALL1.addItemListener(listener); PT.addItemListener(listener); RT.addItemListener(listener); CT.addItemListener(listener); MU.addItemListener(listener);
		ALL2.addItemListener(listener); ALL3.addItemListener(listener); SS.addItemListener(listener); DD.addItemListener(listener); DS.addItemListener(listener);
		bsbs.addItemListener(listener);bcbc.addItemListener(listener);lclc.addItemListener(listener);sbsb.addItemListener(listener);slsl.addItemListener(listener);
		bsbc.addItemListener(listener);bslc.addItemListener(listener);bssb.addItemListener(listener);bssl.addItemListener(listener);bclc.addItemListener(listener);
		bcsb.addItemListener(listener);bcsl.addItemListener(listener);lcsb.addItemListener(listener);lcsl.addItemListener(listener);sbsl.addItemListener(listener);
		ALL4.addItemListener(listener); NOS.addItemListener(listener); PartS.addItemListener(listener); INCS.addItemListener(listener);
	
		updateDummyData();
	}
}
