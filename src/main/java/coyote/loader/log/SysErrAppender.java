/*
 * $Id: SysErrAppender.java,v 1.2 2007/01/04 17:03:12 scote Exp $
 */
package coyote.loader.log;

import java.io.OutputStreamWriter;


/**
 * SysErrAppender is an implementation of Logger that extends LoggerBase and
 * defines event() to write the event to a Writer.
 */
public class SysErrAppender extends ConsoleAppender {

  /**
   * Constructor SysErrAppender
   */
  public SysErrAppender() {
    super( new OutputStreamWriter( System.err ), 0 );
  }




  /**
   * Construct a WriterLogger that writes to System.out with an initial mask
   * value.
   *
   * @param mask The initial mask value.
   */
  public SysErrAppender( final long mask ) {
    super( new OutputStreamWriter( System.err ), mask );
  }
}