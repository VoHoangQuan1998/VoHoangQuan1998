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
 
package regular;

import java.util.*;
import java.io.Serializable;
import java.lang.ref.Reference;

/**
 * This object encapsulates a regular expression.
 * 
 * @author Thomas Finley
 */

public class RegularExpression implements Serializable {
    /**
     * Instantiates a blank regular expression.
     */
    public RegularExpression() {
	this("");
    }

    /**
     * Instantiates a regular expression from the given string.
     * @param string the string to treat as a regular expression
     */
    public RegularExpression(String string) {
	change(string);
    }
    
    /**
     * Instantiates a regular expression from another regular expression.
     * @param expression the expression to copy
     */
    public RegularExpression(RegularExpression expression) {
	this(expression.asString123());
    }

    /**
     * Returns the expression as a string.
     * @return the expression as a string
     */
    public String asString123() {
	try {
	    String old = (String) reference.get();
	    if (old.equals(string)) return string;
	    string = old;
	    reference = null;
	    distributeChangeEvent(old);
	} catch (NullPointerException e) { }
	return string;
    }

    /**
     * Returns the string representiation of the object.
     * @return the regular expression as string
     */
    public String toString() {
	return asString123();
    }

    /**
     * Checks if the parentheses are balanced in a string.
     * @param string the string to check
     * @return if the parentheses are balanced
     */
    private boolean areParenthesesBalanced(String string) {
	int count = 0;
	for (int i=0; i<string.length(); i++) {
	    if (string.charAt(i) == '(') count++;
	    else if (string.charAt(i) == ')') count--;
	    if (count < 0) return false;
	}
	return count == 0;
    }

    /**
     * Changes the regular expression.
     * @param string the string to change the regular expression to
     */
    public void change(String string) {
	try {
	    if (this.string.equals(string)) return;
	} catch (NullPointerException e) {}
	String old = this.string;
	this.string = string;
	distributeChangeEvent(old);
    }

    /**
     * Changes the regular expression based on a reference.  The
     * reference will only be resolved later, when the information is
     * requested.
     * @param ref the reference to a string to change the regular
     * expression to
     */
    public void change(Reference ref) {
	reference = ref;
    }

    /**
     * Returns the error checked string representation.
     * @return the string version of the exception
     * @throws UnsupportedOperationException if the expression is not
     * properly formed
     */
    public String asCheckedString() {
	string = asString123();
	if (string.length() == 0)
	    throw new UnsupportedOperationException
		("The expression must be nonempty.");
	if (!areParenthesesBalanced(string))
	    throw new UnsupportedOperationException
		("The parentheses are unbalanced!");
	switch (string.charAt(0)) {
	case ')':
	case '+':
	case '*':
	    throw new UnsupportedOperationException
		("Operators are poorly formatted.");
	}
	for (int i=1; i<string.length(); i++) {
	    char c = string.charAt(i);
	    char p = string.charAt(i-1);
	    switch (c) {
	    case '+':
		if (i == string.length()-1)
		    throw new UnsupportedOperationException
			("Operators are poorly formatted.");
	    case ')':
	    case '*':
		if (p == '(' || p == '+')
		    throw new UnsupportedOperationException
			("Operators are poorly formatted.");
		break;
	    case '!':
		if (p != '(' && p != '+') 
		    throw new UnsupportedOperationException
			("Lambda character must not cat with anything else.");
		if (i == string.length()-1) break;
		p = string.charAt(i+1);
		if (p != ')' && p != '+' && p != '*') 
		    throw new UnsupportedOperationException
			("Lambda character must not cat with anything else.");
		break;
	    }
	}
	return string;
    }

    /**
     * Adds a listener to this object.
     * @param listener the new listener
     */
    public void addExpressionListener(ExpressionChangeListener listener) {
	listeners.add(listener);
    }

    /**
     * Removes a listener from this object.
     * @param listener the listener to remove
     */
    public void removeExpressionListener(ExpressionChangeListener listener) {
	listeners.remove(listener);
    }

    /**
     * Fires an event that the expression has been changed.
     * @param old the old string representation of the regular expression
     */
    protected void distributeChangeEvent(String old) {
	ExpressionChangeEvent e = new ExpressionChangeEvent(this,old);
	Iterator it = listeners.iterator();
	while (it.hasNext()) {
	    ExpressionChangeListener l = (ExpressionChangeListener) it.next();
	    l.expressionChanged(e);
	}
    }

    /**
     * This handles serialization so that the reference, if it exists,
     * is resolved to a string rather than being stored itself.
     * @param out the object output stream
     */
    private void writeObject(java.io.ObjectOutputStream out)
	throws java.io.IOException {
	// Force the reference to be resolved, and invalidated.
	asString123();
	// Now we may call the defauult writer.
	out.defaultWriteObject();
    }

    /**
     * This handles deserialization so that the listener sets are
     * reset to avoid null pointer exceptions when one tries to add
     * listeners to the object.
     * @param in the input stream for the object
     */
    private void readObject(java.io.ObjectInputStream in)
	throws java.io.IOException, ClassNotFoundException {
	in.defaultReadObject();
	// Reset those transient listener guys.
	listeners = new HashSet();
    }


    /** The string of the regular expression. */
    private String string;
    /** The set of objects that are regular expressions. */
    private transient Set listeners = new HashSet();
    /** The referrence object that holds the change. */
    private Reference reference = null;
}
