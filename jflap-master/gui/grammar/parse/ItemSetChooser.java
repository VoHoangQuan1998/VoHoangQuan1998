/* -- JFLAP 4.0 --
 *
 * Copyright information:
 *
 * Susan H. Rodger, Thomas Finley
 * Computer Science Department
 * Duke University
 * April 24, 2003
 * Supported by National Science Foundation DUE-9752583.
 *
 * Copyright (c) 2003
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by the author.  The name of the author may not be used to
 * endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
 
package gui.grammar.parse;

import grammar.*;
import grammar.parse.*;
import gui.SuperMouseAdapter;
import gui.grammar.GrammarTable;
import gui.grammar.GrammarTableModel;
import gui.grammar.ImmutableGrammarTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * This allows a user to specify sets of items.
 * 
 * @author Thomas Finley
 */

public class ItemSetChooser {
    /**
     * Instantiates a new item set chooser for a grammar.
     * @param grammar the grammar to create the item set chooser for
     */
    public ItemSetChooser(Grammar grammar, Component parent) {
	this.grammar = grammar;
	this.parent = parent;
	chooseTable =
	    new GrammarTable(new ImmutableGrammarTableModel(grammar));
	chooseTable.addMouseListener(GTListener);
	choiceTable =
	    new GrammarTable(new ImmutableGrammarTableModel());
	JScrollPane p = new JScrollPane(chooseTable);
	p.setPreferredSize(new Dimension(200,200));
	panel.add(p, BorderLayout.WEST);
	p = new JScrollPane(choiceTable);
	p.setPreferredSize(new Dimension(200,200));
	panel.add(p, BorderLayout.EAST);
	// Set up the tool bar.
	JToolBar bar = new JToolBar();
	bar.add(new AbstractAction("Closure") {
		public void actionPerformed(ActionEvent e) {
		    closure();
		} });
	bar.add(new AbstractAction("Finish") {
		public void actionPerformed(ActionEvent e) {
		    finish();
		} });
	panel.add(bar, BorderLayout.NORTH);
    }

    /**
     * This will add the closure of all selected items in choice to
     * the choice.
     */
    private void closure() {
	HashSet selected = new HashSet();
	GrammarTableModel model = choiceTable.getGrammarModel();
	for (int i=0; i<model.getRowCount()-1; i++)
	    if (choiceTable.isRowSelected(i))
		selected.add(model.getProduction(i));
	if (selected.size() == 0) {
	    JOptionPane.showMessageDialog
		(parent, "Select an item (or items) in the right table.",
		 "Nothing Selected", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	Set closureSet = Operations.closure(grammar, selected);
	closureSet.removeAll(alreadyChosen);
	Iterator it = closureSet.iterator();
	while (it.hasNext()) addItem((ProductionRename)it.next());
    }

    /**
     * This will complete the items in choice.
     */
    private void finish() {
	if (restricted == null) {
	    JOptionPane.showMessageDialog
		(parent, "There is no one right answer in this case.",
		 "Ambiguity", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	HashSet toAdd = new HashSet(restricted);
	toAdd.removeAll(alreadyChosen);
	Iterator it = toAdd.iterator();
	while (it.hasNext()) addItem((ProductionRename)it.next());
    }
    
    /**
     * This will bring up a dialog box allowing a user to specify item
     * sets.
     * @param items the target item set in the event that the user is
     * being asked to give some information that is already known, or
     * <CODE>null</CODE> if there is no prearranged target
     * @param message a small message to display to the user
     * @return an array containing the items the user selected, or
     * <CODE>null</CODE> if the user cancelled the action
     */
    public ProductionRename[] getItemSet(Set items, String message) {
	restricted = items;
	choiceTable.setModel(new ImmutableGrammarTableModel());
	alreadyChosen = new HashSet();
	while (true) {
	    int choice = JOptionPane.showConfirmDialog
		(parent, panel, message, JOptionPane.OK_CANCEL_OPTION);
	    if (choice == JOptionPane.CANCEL_OPTION) return null;
	    // Get those selected.
	    List selected = new ArrayList();
	    GrammarTableModel model = choiceTable.getGrammarModel();
	    for (int i=0; i<model.getRowCount()-1; i++)
		selected.add(model.getProduction(i));
	    // Check if it's our target.
	    if (items != null) {
		Set selectedSet = new HashSet(selected);
		if (!selectedSet.equals(items)) {
		    JOptionPane.showMessageDialog
			(parent, "Some items are missing!",
			 "Items Missing", JOptionPane.ERROR_MESSAGE);
		    continue;
		}
	    }
	    return (ProductionRename[]) selected.toArray(new ProductionRename[0]);
	}
    }

    /**
     * This method should be called when an item should be added.
     * @param item the item that was selected for addition
     */
    private void addItem(ProductionRename item) {
	if (restricted != null && !restricted.contains(item)) {
	    JOptionPane.showMessageDialog
		(parent, item.toString()+" is not part of the set.",
		 "Item not Desirable", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	if (alreadyChosen.contains(item)) {
	    JOptionPane.showMessageDialog
		(parent, item.toString()+" is already chosen.",
		 "Item Already Chosen", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	alreadyChosen.add(item);
	choiceTable.getGrammarModel().addProduction(item);
    }

    private final GrammarTableListener GTListener =
	new GrammarTableListener();
    private class GrammarTableListener extends SuperMouseAdapter {
	public void mouseClicked(MouseEvent event) {
	    GrammarTable gt = (GrammarTable) event.getSource();
	    Point at = event.getPoint();
	    int row = gt.rowAtPoint(at);
	    if (row == -1) return;
	    if (row == gt.getGrammarModel().getRowCount()-1) return;
	    ProductionRename p = gt.getGrammarModel().getProduction(row);
	    ProductionRename[] pItems = Operations.getItems(p);
	    JPopupMenu menu = new JPopupMenu();
	    ItemMenuListener itemListener = new ItemMenuListener(p);
	    for (int i=0; i<pItems.length; i++) {
		JMenuItem item = new JMenuItem(pItems[i].toString());
		item.setActionCommand(pItems[i].getRHS());
		item.addActionListener(itemListener);
		menu.add(item);
	    }
	    menu.show(gt, at.x, at.y);
	}
    }

    private class ItemMenuListener implements ActionListener {
	public ItemMenuListener(ProductionRename p) {
	    prod = p;
	}
	public void actionPerformed(ActionEvent event) {
	    String rhs = event.getActionCommand();
	    ProductionRename p = new ProductionRename(prod.getLHS(), rhs);
	    addItem(p);
	}
	ProductionRename prod;
    }

    /** The parent for dialog boxes. */
    private Component parent;
    /** This is the pane for the grammar. */
    private JPanel panel = new JPanel(new BorderLayout());
    /** Productions we can choose items from. */
    private GrammarTable chooseTable;
    /** What items we have already chosen. */
    private GrammarTable choiceTable;

    /** Items able to be added are restricted to this set.  If null,
     * there are no restrictions. */
    private Set restricted = null; 
    /** The items that have been added sofar are listed here. */
    private Set alreadyChosen;

    /** The grammar for the item set chooser. */
    private Grammar grammar;
}
