/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 */
package coyote.commons.network.http.nugget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.IHTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;
import coyote.loader.log.Log;


/**
 * 
 */
public class UriResource {

  private static final Pattern PARAM_PATTERN = Pattern.compile( "(?<=(^|/)):[a-zA-Z0-9_-]+(?=(/|$))" );

  private static final String PARAM_MATCHER = "([A-Za-z0-9\\-\\._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=\\s]+)";

  private static final Map<String, String> EMPTY = Collections.unmodifiableMap( new HashMap<String, String>() );

  private final String uri;

  private final Pattern uriPattern;

  final int priority;

  private final Class<?> handler;

  private final Object[] initParameter;

  private final List<String> uriParams = new ArrayList<String>();




  public UriResource( final String uri, final int priority, final Class<?> handler, final Object... initParameter ) {
    this.handler = handler;
    this.initParameter = initParameter;
    if ( uri != null ) {
      this.uri = HTTPDRouter.normalizeUri( uri );
      parse();
      uriPattern = createUriPattern();
    } else {
      uriPattern = null;
      this.uri = null;
    }
    this.priority = priority + ( uriParams.size() * 1000 );
  }




  private Pattern createUriPattern() {
    String patternUri = uri;
    Matcher matcher = PARAM_PATTERN.matcher( patternUri );
    int start = 0;
    while ( matcher.find( start ) ) {
      uriParams.add( patternUri.substring( matcher.start() + 1, matcher.end() ) );
      patternUri = new StringBuilder( patternUri.substring( 0, matcher.start() ) )//
          .append( PARAM_MATCHER )//
          .append( patternUri.substring( matcher.end() ) ).toString();
      start = matcher.start() + PARAM_MATCHER.length();
      matcher = PARAM_PATTERN.matcher( patternUri );
    }
    return Pattern.compile( patternUri );
  }




  public String getUri() {
    return uri;
  }




  public <T> T initParameter( final Class<T> paramClazz ) {
    return initParameter( 0, paramClazz );
  }




  public <T> T initParameter( final int parameterIndex, final Class<T> paramClazz ) {
    if ( initParameter.length > parameterIndex ) {
      return paramClazz.cast( initParameter[parameterIndex] );
    }
    Log.append( HTTPD.EVENT,"ERROR: init parameter index not available " + parameterIndex );
    return null;
  }




  public Map<String, String> match( final String url ) {
    final Matcher matcher = uriPattern.matcher( url );
    if ( matcher.matches() ) {
      if ( uriParams.size() > 0 ) {
        final Map<String, String> result = new HashMap<String, String>();
        for ( int i = 1; i <= matcher.groupCount(); i++ ) {
          result.put( uriParams.get( i - 1 ), matcher.group( i ) );
        }
        return result;
      } else {
        return EMPTY;
      }
    }
    return null;
  }




  private void parse() {}




  public Response process( final Map<String, String> urlParams, final IHTTPSession session ) {
    String error = "Error: Problems while processing URI resource";
    if ( handler != null ) {
      try {
        final Object object = handler.newInstance();
        if ( object instanceof UriResponder ) {
          final UriResponder responder = (UriResponder)object;
          switch ( session.getMethod() ) {
            case GET:
              return responder.get( this, urlParams, session );
            case POST:
              return responder.post( this, urlParams, session );
            case PUT:
              return responder.put( this, urlParams, session );
            case DELETE:
              return responder.delete( this, urlParams, session );
            default:
              return responder.other( session.getMethod().toString(), this, urlParams, session );
          }
        } else {
          return HTTPD.newFixedLengthResponse( Status.OK, "text/plain", //
              new StringBuilder( "Return: " )//
                  .append( handler.getCanonicalName() )//
                  .append( ".toString() -> " )//
                  .append( object )//
                  .toString() );
        }
      } catch ( final Exception e ) {
        error = "Error: " + e.getClass().getName() + " : " + e.getMessage();
        Log.append( HTTPD.EVENT, error, e );
      }
    }
    return HTTPD.newFixedLengthResponse( Status.INTERNAL_ERROR, "text/plain", error );
  }




  @Override
  public String toString() {
    return new StringBuilder( "UriResource{uri='" ).append( ( uri == null ? "/" : uri ) )//
        .append( "', urlParts=" ).append( uriParams )//
        .append( '}' )//
        .toString();
  }

}