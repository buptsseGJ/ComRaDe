package cn.edu.thu.platform.frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 *                            
 * @author yangxin
 */
public class MyTreeTableModel extends DefaultTreeTableModel implements ActionListener{

    private String[] _names = {"", "Name/Class","Time", "Line1", "Line2", "Variable","VariableType","CodeStructure","MethodSpan","SensitiveBranch","SensitiveLoop","Cause","CommonUsage"};//,"Details"
    private Class[] _types = {Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class};

    public MyTreeTableModel(TreeTableNode node) {
        super(node);
        
    }

	/**
     * the type of columns
     */
    @Override
    public Class getColumnClass(int col) {
        return _types[col];
    }

    /**
     * the number of columns
     */
    @Override
    public int getColumnCount() {
        return _names.length;
    }

    /**
     * Header display contents
     */
    @Override
    public String getColumnName(int column) {
        return _names[column];
    }

    /**
     * Returns the Object displayed in the cell
     */
    @Override
    public void setValueAt(Object aValue, Object node, int col) {  
        if (node instanceof DefaultMutableTreeTableNode) {  
            DefaultMutableTreeTableNode mutableNode = (DefaultMutableTreeTableNode) node;  
            Object o = mutableNode.getUserObject();  
            if (o != null && o instanceof MyTreeTableNode) {  
                MyTreeTableNode bean = (MyTreeTableNode) o;  
                switch (col) {  
                    case 0:  
                        String tmp = (String) aValue;  
                        bean.setName(tmp);  
                        break;  
                    case 1:  
                    	String v1 = (String) aValue;  
                        bean.setValue1(v1);  
                        break;  
                    case 2:  
                    	if(bean.getName().indexOf("Case")>=0){//only update the item 'totalTime' of test cases
                    		String v7 = (String) aValue;  
                    		if(v7.trim().indexOf("s")==-1&&v7.trim().length()>0)
                    			v7 = v7.trim()+"s";
                        	bean.setValue7(v7);  
                    	}
                        break; 
                    case 3:  
                    	String v2 = (String) aValue;  
                        bean.setValue2(v2);  
                        break;  
                    case 4:  
                    	String v3 = (String) aValue;  
                        bean.setValue3(v3);  
                        break;  
                    case 5:  
                    	String v4 = (String) aValue;  
                        bean.setValue4(v4);  
                        break; 
                    case 6:  
                    	String v6 = (String) aValue;  
                        bean.setValue6(v6);  
                        break;   
                    case 7:  
                    	String v8 = (String) aValue;  
                        bean.setValue8(v8);  
                        break;  
                    case 8:  
                    	String v9 = (String) aValue;  
                        bean.setValue9(v9);  
                        break;  
                    case 9:  
                    	String v10 = (String) aValue;  
                        bean.setValue10(v10);  
                        break;  
                    case 10:  
                    	String v11 = (String) aValue;  
                        bean.setValue11(v11);  
                        break;  
                    case 11:  
                    	String v12 = (String) aValue;  
                        bean.setValue12(v12);  
                        break; 
                    case 12:  
                    	String v13 = (String) aValue;  
                        bean.setValue13(v13);  
                        break;  
                }  
            }  
        }  
    }

  @Override
public Object getValueAt(Object node, int column) {
      Object value = null;
      if (node instanceof DefaultMutableTreeTableNode) {
          DefaultMutableTreeTableNode mutableNode = (DefaultMutableTreeTableNode) node;
          Object o = mutableNode.getUserObject();
          if (o != null && o instanceof MyTreeTableNode) {
              MyTreeTableNode bean = (MyTreeTableNode) o;
              switch (column) {
                  case 0:
                      value = bean.getName();
                      break;
                  case 1:
                      value = bean.getValue1();
                      break;
                  case 2:
                	  if(bean.getName().indexOf("Case")>=0)
                		  value = bean.getValue7();
                	  else
                		  value="--";
                      break;
                  case 3:
                      value = bean.getValue2();
                      break;
                  case 4:
                      value = bean.getValue3();
                      break;
                  case 5:
                      value = bean.getValue4();
                      break;
                  case 6:
                      value = bean.getValue6();
                      break;
                  case 7:
                      value = bean.getValue8();
                      break;
                  case 8:
                      value = bean.getValue9();
                      break;
                  case 9:
                      value = bean.getValue10();
                      break;
                  case 10:
                      value = bean.getValue11();
                      break;
                  case 11:
                      value = bean.getValue12();
                      break;
                  case 12:
                      value = bean.getValue13();
                      break;
              }
          }
      }
      return value;
	}
    /**
     * Sets whether all cells can be edited
     * @param the node (i.e. row) for which editing is to be determined
     * @param the column for which editing is to be determined
     * @return false
     */
    @Override
    public boolean isCellEditable(Object node, int column) {
    	if(column==0) {
    		return false;
    	}
    	if(node instanceof DefaultMutableTreeTableNode){
    		 DefaultMutableTreeTableNode mutableNode = (DefaultMutableTreeTableNode) node;  
             Object o = mutableNode.getUserObject();  
             if (o != null && o instanceof MyTreeTableNode) {  
                 MyTreeTableNode bean = (MyTreeTableNode) o;  
                 if(bean.getName().indexOf("Case")>=0&&column>2){
                	 return false;
                 }else if(bean.getName().indexOf("Case")==-1&&column==2){
                	 return false;
                 }else{
                	 return true;
                 }
             }
    	}
        return true;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	@Override
	public boolean isLeaf(Object node) {
		DefaultMutableTreeTableNode mutableNode = (DefaultMutableTreeTableNode) node;
        Object o = mutableNode.getUserObject();
        if (o != null && o instanceof MyTreeTableNode) {
            MyTreeTableNode bean = (MyTreeTableNode) o;
    		return bean.isLeaf();
        }
        return super.isLeaf(node);
	}

}
