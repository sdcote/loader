/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.network.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Random;


/**
 * This is a very basic session manager which generates uniquely identified 
 * session instances components can use to store session specific data.
 */
public class SessionManager {
  private static File sessionFile = new File("./.sessions");
  private static final Random RANDOM = new Random();
  private static final int TOKEN_SIZE = 24;
  private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
  private static HashMap<String, SessionProfile> sessionMap = new HashMap<>();
  private static final String SESSION_COOKIE = "CL-Session-Id";




  /**
   * Remove the identified session from the manager and its identifier from 
   * the cookies.
   * 
   * @param sessionId the identifier of the session to remove
   * @param cookies the cookies from which the identifier should be removed.
   */
  public static void destroy(final String sessionId, final CookieHandler cookies) {
    sessionMap.remove(sessionId);
    cookies.delete(SESSION_COOKIE);
  }




  /**
   * Retrieve the session specified in the given set of cookies, or create a 
   * new session and place its identifier in those cookies.
   * 
   * @param cookies the cookies containing the session identifier or to be 
   *        populated with the new session identifier if one is created.
   * 
   * @return the session associated with the identifier found in the cookies, 
   *         or the new session.
   */
  public static synchronized SessionProfile retrieveOrCreate(final CookieHandler cookies) {
    SessionProfile retval = null;
    if (cookies != null) {
      String token = cookies.read(SESSION_COOKIE);
      if (token == null) {
        token = createSessionIdentifier();
        cookies.set(SESSION_COOKIE, token, 30);
      }
      if (!sessionMap.containsKey(token)) {
        sessionMap.put(token, new SessionProfile(token));
      }
      retval = sessionMap.get(token);
    }
    return retval;
  }




  /**
   * Generate a random hexadecimal string to be used as a session identifier.
   * 
   * @return a unique identifier
   */
  private static String generateSessionIdentifier() {
    final StringBuilder sb = new StringBuilder(TOKEN_SIZE);
    for (int i = 0; i < TOKEN_SIZE; i++) {
      sb.append(HEX[RANDOM.nextInt(HEX.length)]);
    }
    return sb.toString();
  }




  /**
   * Create a session identifier which is not contained in the session map.
   * @return
   */
  private static String createSessionIdentifier() {
    String retval;
    do {
      retval = generateSessionIdentifier();
    }
    while (sessionMap.containsKey(retval));
    return retval;
  }




  /**
   * This loads session data from the currently set session file reference.
   * 
   * <p>If the session file exists, then try to load it. Otherwise, just 
   * return.
   * 
   * @throws Exception if there were problems reading the file from the file 
   *         system.
   */
  @SuppressWarnings("unchecked")
  public static void load() throws Exception {
    if (!sessionFile.exists()) {
      return;
    }
    final FileInputStream input = new FileInputStream(sessionFile);
    sessionMap = (HashMap<String, SessionProfile>)new ObjectInputStream(input).readObject();
    input.close();
  }




  /**
   * Save the sessions to the file system using the currently set session file
   * reference.
   * 
   * @throws Exception if there were problems writing the file to the file
   *         system.
   */
  public static void save() throws Exception {
    final FileOutputStream output = new FileOutputStream(sessionFile);
    new ObjectOutputStream(output).writeObject(sessionMap);
    output.close();
  }




  /**
   * Get the file to which the session manager will write session data.
   * 
   * <p>Data is only written if some component calls the {@link #save()}
   * method.
   * 
   * @return the file to which sessions will be written (serialized).
   */
  public static File getSessionFile() {
    return sessionFile;
  }




  /**
   * Set the file to which the session manager will write session data.
   * 
   * <p>Data will only be written if some component calls the {@link #save()}
   * method.
   * 
   * @param file the file to which sessions should be written (serialized).
   */
  public static void setSessionFile(File file) {
    SessionManager.sessionFile = file;
  }

}