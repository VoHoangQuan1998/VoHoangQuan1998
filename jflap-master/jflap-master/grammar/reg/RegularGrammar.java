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
 
package grammar.reg;

import grammar.*;

/**
 * The regular grammar object is a representation of a regular grammar.
 * This object is a data structure of sorts, maintaining the data
 * pertinent to the definition of a regular grammar.
 *
 * @author Ryan Cavalcante
 */

public class RegularGrammar extends Grammar {
    /**
     * Creates an instance of <CODE>RegularGrammar</CODE>.  The 
     * created instance has no productions, no terminals, no variables, 
     * and specifically no start variable.
     */
    public RegularGrammar() {
	super();
	setLinearity(-1);
    }

    /**
     * Sets the linearity of the regular grammar to the
     * value represented by <CODE>linearity</CODE>.
     * (0 is left-linear, 1 is right-linear).
     * @param linearity the linearity of the grammar.
     */
    private void setLinearity(int linearity) {
	myLinearity = linearity;
    }

    /**
     * Returns the linearity of the grammar in the form of
     * an int.  0 means left-linear, 1 means right-linear.
     * @return the linearity of the grammar.
     */
    private int getLinearity() {
	return myLinearity;
    }
    
    /**
     * This checks the production.
     * @param production the production
     * @throws IllegalArgumentException if the production is in some
     * way illegal for the grammar
     */
    public void checkProduction(ProductionRename production) {
	if (!PC.isRestrictedOnLHS(production))
	    throw new IllegalArgumentException
		("The production is unrestricted on the left hand side.");
	if (!PC.isLeftLinear(production) && !PC.isRightLinear(production))
	    throw new IllegalArgumentException
		("The production is neither left nor right linear!");
	// Have we even MADE a decision yet?
	if (getLinearity() != LEFT_LINEAR && getLinearity() != RIGHT_LINEAR)
	    return;
	// What if it's just a terminal?
	if (PC.isLeftLinear(production) && PC.isRightLinear(production))
	    return;
	// Does linearity match up?
	if (getLinearity() == LEFT_LINEAR && PC.isRightLinear(production))
	    throw new IllegalArgumentException
		("The production is right linear, "+
		 "the grammar is left linear.");
	if (getLinearity() == RIGHT_LINEAR && PC.isLeftLinear(production))
	    throw new IllegalArgumentException
		("The production is left linear, "+
		 "the grammar is right linear.");
    }

    /**
     * Adds a production to the grammar.  After the production is
     * added, this method also sets the linearity of this grammar.
     * @throws IllegalArgumentException if this production is somehow
     * illegal for this grammar (i.e., linearities don't match up)
     */
    public void addProduction(ProductionRename production) {
	super.addProduction(production);
	// If it's both, we shouldn't change at all.
	if (PC.isRightLinear(production) && PC.isLeftLinear(production))
	    return;
	// If we get to this point it must be either left or right
	// linear, and not both.
	setLinearity(PC.isRightLinear(production)?RIGHT_LINEAR:LEFT_LINEAR);
    }

    /** The linearity of the grammar, either right or left. */
    protected int myLinearity;
    /** The int that represents the linearity of the grammar. */
    protected static final int LEFT_LINEAR = 0;
    /** The int to represent a right-linear grammar. */
    protected static final int RIGHT_LINEAR = 1;
    /** The production checker. */
    private static final ProductionChecker PC = new ProductionChecker();
}
