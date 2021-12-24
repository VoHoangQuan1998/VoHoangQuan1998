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

package file;

/**
 * This error indicates that a structure could not be properly encoded
 * with the encoder.  This should be thrown by {@link file.Encoder}
 * implementing objects to indicate a problem writing the file.
 * 
 * @author Thomas Finley
 */

public class EncodeException extends RuntimeException {
    /**
     * Creates a generic encoder exception.
     */
    public EncodeException() {
	super();
    }
    
    /**
     * Creates a encode exception with the given message.
     * @param message the exception message
     */
    public EncodeException(String message) {
	super(message);
    }
}
