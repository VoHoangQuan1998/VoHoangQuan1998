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
 
package gui.regular;

import automata.Transition;
import javax.swing.table.AbstractTableModel;

/**
 * The <CODE>TransitionTableModel</CODE> is a model for displaying a
 * table of transitions.  This will display one transition per row
 * with, in order, the from state ID number, the to state ID number,
 * and the transition description.
 * 
 * @author Thomas Finley
 */

public class TransitionTableModel extends AbstractTableModel {
    /**
     * Instantiates a new <CODE>TransitionTableModel</CODE>.
     * @param transitions the array of transitions to create the table
     * for
     */
    public TransitionTableModel(Transition[] transitions) {
	this.transitions = transitions;
    }
    
    /**
     * Instantiates a new <CODE>TransitionTableModel</CODE> with no
     * contents.
     */
    public TransitionTableModel() {
	this(new Transition[0]);
    }

    /**
     * This table model has three columns.
     * @return the number 3!
     */
    public int getColumnCount() {
	return 3;
    }

    /**
     * This table model has as many rows are there are transitions.
     * @return the number of transitions
     */
    public int getRowCount() {
	return transitions.length;
    }

    /**
     * Returns the transition for a row.  This method will fail if
     * there isn't a transition for this index.
     * @param row the row to get the transition for
     * @return the transition for this row
     */
    public Transition getTransition(int row) {
	return transitions[row];
    }

    /**
     * Returns the contents of a cell.
     * @param row the number of the transition to display
     * @param column controls which parameter of the transition shall
     * be displayed, for 0 through 2, this is from, to, and
     * description, in that order
     * @return the string description for the appropriate part of a
     * transition
     */
    public Object getValueAt(int row, int column) {
	switch (column) {
	case 0: return ""+transitions[row].getFromState().getID();
	case 1: return ""+transitions[row].getToState().getID();
	case 2: return transitions[row].getDescription();
	default: return null;
	}
    }

    /**
     * Returns the name for a column.
     * @param column the index of the column to get the name for
     * @return the name for the indicated column
     */
    public String getColumnName(int column) {
	return COLUMN_NAMES[column];
    }

    /** The list of transitions displayed in the table. */
    private Transition[] transitions;
    /** The column names. */
    private static final String[] COLUMN_NAMES = {"From", "To", "Label"};
}