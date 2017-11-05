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
import java.util.Iterator;
import java.util.Map;
import java.util.Random;


/**
 * This is a very basic session profile manager which generates uniquely
 * identified profile instances which components can use to store session
 * specific data.
 *
 * <p>Session profiles are a set of name-value pairs which can be associated
 * with a HTTP session through the use of cookies. Values specific to the
 * session can be stored in the profile to maintain state between requests.
 *
 * <p>All profile values must be serializable as the entire map of profiles
 * may be serialized to storage for persistance between restarts.
 */
public class SessionProfileManager {
  private static File sessionFile = new File("./.sessions");
  private static final Random RANDOM = new Random();
  private static final int TOKEN_SIZE = 24;
  private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
  private static HashMap<String, SessionProfile> profileMap = new HashMap<>();
  private static final String SESSION_COOKIE = "CL-Session-Id";




  /**
   * Create a session identifier which is not contained in the session map.
   * 
   * @return a session identifier which is unique to this manager.
   */
  private static String createProfileIdentifier() {
    String retval;
    do {
      retval = generateProfileIdentifier();
    }
    while (profileMap.containsKey(retval));
    return retval;
  }




  /**
   * Remove the identified session from the manager and its identifier from
   * the cookies.
   *
   * @param sessionId the identifier of the session to remove
   * @param session the session profile from which the identifier should be removed.
   */
  public static void destroyProfile(final String sessionId, final IHTTPSession session) {
    profileMap.remove(sessionId);
    final CookieHandler cookies = session.getCookies();
    if (cookies != null) {
      cookies.delete(SESSION_COOKIE);
    }
  }




  /**
   * Generate a random hexadecimal string to be used as a session identifier.
   *
   * @return a unique identifier
   */
  private static String generateProfileIdentifier() {
    final StringBuilder sb = new StringBuilder(TOKEN_SIZE);
    for (int i = 0; i < TOKEN_SIZE; i++) {
      sb.append(HEX[RANDOM.nextInt(HEX.length)]);
    }
    return sb.toString();
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
   * This loads session data from the currently set session file reference.
   *
   * <p>If the session file exists, then try to load it. Otherwise, just
   * return.
   * 
   * <p>One the profile map is loaded, it is purged of any expired profiles.
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
    profileMap = (HashMap<String, SessionProfile>)new ObjectInputStream(input).readObject();
    input.close();
    purgeExpiredProfiles();
  }




  /**
   * Iterate through the profiles and remove any which are expired.
   */
  public static synchronized void purgeExpiredProfiles() {
    final Iterator<Map.Entry<String, SessionProfile>> iter = profileMap.entrySet().iterator();
    while (iter.hasNext()) {
      final Map.Entry<String, SessionProfile> entry = iter.next();
      if (entry.getValue().isExpired()) {
        iter.remove();
      }
    }
  }




  /**
   * Retrieve the session specified in the given set of cookies, or create a
   * new session and place its identifier in those cookies.
   *
   * @param session the session containing the session identifier or to be
   *        populated with the new session identifier if one is created.
   *
   * @return the session associated with the identifier found in the cookies,
   *         or the new session. Will return null if the session is null.
   */
  public static synchronized SessionProfile retrieveOrCreateProfile(final IHTTPSession session) {
    SessionProfile retval = null;
    if (session != null) {
      final CookieHandler cookies = session.getCookies();
      if (cookies != null) {
        String token = cookies.read(SESSION_COOKIE);
        if (token == null) {
          token = createProfileIdentifier();
          cookies.set(SESSION_COOKIE, token, 30);
        }
        if (!profileMap.containsKey(token)) {
          profileMap.put(token, new SessionProfile(token));
        }
        retval = profileMap.get(token);
      }
    }
    return retval;
  }




  /**
   * Save the sessions to the file system using the currently set session file
   * reference.
   * 
   * <p>Expired profiles are removed before being saved.
   *
   * @throws Exception if there were problems writing the file to the file
   *         system.
   */
  public static synchronized void save() throws Exception {
    purgeExpiredProfiles();
    final FileOutputStream output = new FileOutputStream(sessionFile);
    new ObjectOutputStream(output).writeObject(profileMap);
    output.close();
  }




  /**
   * Set the file to which the session manager will write session data.
   *
   * <p>Data will only be written if some component calls the {@link #save()}
   * method.
   *
   * @param file the file to which sessions should be written (serialized).
   */
  public static void setSessionFile(final File file) {
    SessionProfileManager.sessionFile = file;
  }

}