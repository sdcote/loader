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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import coyote.commons.network.http.IHTTPSession;
import coyote.commons.network.http.Response;

/**
 * This is the heart of the URI routing mechanism in a nugget server.
 */
public class UriRouter {

  private final List<UriResource> mappings;

  private UriResource error404Url;

  private Class<?> notImplemented;




  public UriRouter() {
    mappings = new ArrayList<UriResource>();
  }




  void addRoute( final String url, final int priority, final Class<?> handler, final Object... initParameter ) {
    if ( url != null ) {
      if ( handler != null ) {
        mappings.add( new UriResource( url, priority + mappings.size(), handler, initParameter ) );
      } else {
        mappings.add( new UriResource( url, priority + mappings.size(), notImplemented ) );
      }
      sortMappings();
    }
  }




  /**
   * Search in the mappings if the given url matches some of the rules.
   * 
   * <p>If there are more than one march, this returns the rule with least 
   * parameters. For example: mapping 1 = /user/:id  - mapping 2 = /user/help. 
   * If the incoming uri is www.example.com/user/help - mapping 2 is returned. 
   * If the incoming uri is www.example.com/user/3232 - mapping 1 is 
   * returned.</p>
   * 
   * @param session
   * 
   * @return the Response from the URI resource processing
   */
  public Response process( final IHTTPSession session ) {
    final String work = HTTPDRouter.normalizeUri( session.getUri() );
    Map<String, String> params = null;
    UriResource uriResource = error404Url;
    for ( final UriResource resource : mappings ) {
      params = resource.match( work );
      if ( params != null ) {
        uriResource = resource;
        break;
      }
    }
    return uriResource.process( params, session );
  }




  void removeRoute( final String url ) {
    final String uriToDelete = HTTPDRouter.normalizeUri( url );
    final Iterator<UriResource> iter = mappings.iterator();
    while ( iter.hasNext() ) {
      final UriResource uriResource = iter.next();
      if ( uriToDelete.equals( uriResource.getUri() ) ) {
        iter.remove();
        break;
      }
    }
  }




  public void setNotFoundHandler( final Class<?> handler ) {
    error404Url = new UriResource( null, 100, handler );
  }




  public void setNotImplemented( final Class<?> handler ) {
    notImplemented = handler;
  }




  private void sortMappings() {
    Collections.sort( mappings, new Comparator<UriResource>() {

      @Override
      public int compare( final UriResource o1, final UriResource o2 ) {
        return o1.priority - o2.priority;
      }
    } );
  }

}