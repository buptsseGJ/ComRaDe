/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jdesktop.swingx.ux;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 *
 * @author yx
 */
public class CheckTreeCellProvider extends ComponentProvider<JPanel> {

    private CheckTreeSelectionModel selectionModel;
    private TristateCheckBox _checkBox = null;
    private JLabel _label = null;

    public CheckTreeCellProvider(CheckTreeSelectionModel selectionModel) {
        this.selectionModel = selectionModel;
        _checkBox = new TristateCheckBox();
        _checkBox.setOpaque(false); //  set TristateCheckBox without background
        _label = new JLabel();
    }

    @Override
    protected void format(CellContext arg0) {
        //  get text and picture of tree from checktree
        JTree tree = (JTree) arg0.getComponent();
        DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) arg0.getValue();
        Object obj = node.getUserObject();
        _label.setText(obj.toString());
        _label.setFont(new Font("Century Gothic", Font.PLAIN, 22));
        _label.setIcon(arg0.getIcon());

        //set the surface of TristateCheckBox according to selectionMode
        TreePath path = tree.getPathForRow(arg0.getRow());
        if (path != null) {
            if (selectionModel.isPathSelected(path, true)) {
                _checkBox.setState(Boolean.TRUE);
            } else if (selectionModel.isPartiallySelected(path)) {
                _checkBox.setState(null);
            } else {
                _checkBox.setState(Boolean.FALSE);
            }
        }

        // place TristateCheckBox and JLabel orderly
        rendererComponent.setLayout(new BorderLayout());
        //rendererComponent.add(_checkBox);
        rendererComponent.add(_label, BorderLayout.LINE_END);
    }

    @Override
    protected void configureState(CellContext arg0) {
    }
    
    @Override
    protected JPanel createRendererComponent() {
        JPanel panel = new JPanel();
        return panel;
    }
}