/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote
 *      - Initial concept and implementation
 */
package coyote.commons.network.http.responder;

import coyote.commons.DateUtil;
import coyote.commons.StringUtil;
import coyote.commons.WebServer;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.auth.Auth;
import coyote.dataframe.DataFrame;
import coyote.i13n.*;
import coyote.loader.cfg.Config;
import coyote.loader.log.Log;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;


/**
 * This provides access to all the stats for the loader(server).
 *
 * <p>This is expected to the added to the WebServer thusly:<pre>
 * "/api/stat/" : { "Class" : "coyote.commons.network.http.responder.StatBoardHandler", "Format": true },
 * "/api/stat/:metric" : { "Class" : "coyote.commons.network.http.responder.StatBoardHandler", "Format": true },
 * "/api/stat/:metric/:name" : { "Class" : "coyote.commons.network.http.responder.StatBoardHandler", "Format": true },
 * </pre>
 * If no metric or name is given, then the entire statistics board is
 * serialized as a response. If a metric is given, but no name then all the
 * metrics of that type are returned. Otherwise just the named metric is returned.
 */
@Auth
public class StatBoardResponder extends AbstractJsonResponder implements Responder {
  private static final String ARM = "ARM";
  private static final String TIMER = "Timer";
  private static final String GAUGE = "Gauge";
  private static final String STATUS = "Status";
  private static final String COUNTER = "Counter";
  private static final String VERSION = "Version";
  private static final String HOSTNAME = "DnsName";
  private static final String OS_ARCH = "OSArch";
  private static final String OS_NAME = "OSName";
  private static final String OS_VERSION = "OSVersion";
  private static final String RUNTIME_NAME = "RuntimeName";
  private static final String RUNTIME_VENDOR = "RuntimeVendor";
  private static final String RUNTIME_VERSION = "RuntimeVersion";
  private static final String STARTED = "Started";
  private static final String STATE = "State";
  private static final String USER_NAME = "Account";
  private static final String VM_AVAIL_MEM = "AvailableMemory";
  private static final String VM_CURR_HEAP = "CurrentHeap";
  private static final String VM_FREE_HEAP = "FreeHeap";
  private static final String VM_FREE_MEM = "FreeMemory";
  private static final String VM_HEAP_PCT = "HeapPercentage";
  private static final String VM_MAX_HEAP = "MaxHeapSize";
  private static final String FIXTURE_ID = "InstanceId";
  private static final String FIXTURE_NAME = "InstanceName";
  private static final String HOST_ADDRESS = "IpAddress";
  private static final String NAME = "Name";
  private static final String UPTIME = "Uptime";
  private static final String FORMAT_TAG = "Format";


  /**
   *
   */
  @Override
  public Response get(Resource resource, Map<String, String> urlParams, HTTPSession session) {
    WebServer loader = resource.initParameter(0, WebServer.class);
    Config config = resource.initParameter(1, Config.class);

    // Get the command from the URL parameters specified when we were registered with the router 
    String metric = urlParams.get("metric");
    String name = urlParams.get("name");

    // are we going to format the output?
    if (config.containsIgnoreCase(FORMAT_TAG)) {
      try {
        setFormattingJson(config.getBoolean(FORMAT_TAG));
      } catch (NumberFormatException e) {
        Log.warn("Illegal value for '" + FORMAT_TAG + "' tag");
      }
    }

    if (loader.getStats() != null) {
      if (StringUtil.isNotBlank(metric)) {
        if (StringUtil.isNotBlank(name)) {
          // TODO: get just the named metric of that type (e.g. 'counters' with a specific name )
        } else {
          // TODO: return all the metrics of type (e.g. all 'counters')
        }
      } else {
        results.merge(createStatus(loader));
      }
    }

    // create a response using the superclass methods
    return Response.createFixedLengthResponse(getStatus(), getMimeType(), getText());
  }


  private DataFrame createStatus(WebServer loader) {
    StatBoard statboard = loader.getStats();

    DataFrame retval = new DataFrame();
    retval.add(NAME, STATUS);

    // Add information from the statboard  fixture
    retval.add(FIXTURE_ID, statboard.getId());
    retval.add(FIXTURE_NAME, loader.getName());
    retval.add(OS_NAME, System.getProperty("os.name"));
    retval.add(OS_ARCH, System.getProperty("os.arch"));
    retval.add(OS_VERSION, System.getProperty("os.version"));
    retval.add(RUNTIME_VERSION, System.getProperty("java.version"));
    retval.add(RUNTIME_VENDOR, System.getProperty("java.vendor"));
    retval.add(RUNTIME_NAME, "Java");
    retval.add(STARTED, DateUtil.ISO8601Format(statboard.getStartedTime()));
    retval.add(UPTIME, DateUtil.formatSignificantElapsedTime((System.currentTimeMillis() - statboard.getStartedTime()) / 1000));
    retval.add(USER_NAME, System.getProperty("user.name"));
    retval.add(VM_AVAIL_MEM, statboard.getAvailableMemory());
    retval.add(VM_CURR_HEAP, statboard.getCurrentHeapSize());
    retval.add(VM_FREE_HEAP, statboard.getFreeHeapSize());
    retval.add(VM_FREE_MEM, statboard.getFreeMemory());
    retval.add(VM_MAX_HEAP, statboard.getMaxHeapSize());
    retval.add(VM_HEAP_PCT, statboard.getHeapPercentage());
    String text = statboard.getHostname();
    retval.add(HOSTNAME, (text == null) ? "unknown" : text);
    InetAddress addr = statboard.getHostIpAddress();
    retval.add(HOST_ADDRESS, (addr == null) ? "unknown" : addr.getHostAddress());

    DataFrame childPacket = new DataFrame();

    // get the list of component versions registered with the statboard
    Map<String, String> versions = statboard.getVersions();
    for (String key : versions.keySet()) {
      childPacket.add(key, versions.get(key));
    }
    retval.add(VERSION, childPacket);

    // Get all counters
    childPacket.clear();
    for (Iterator<Counter> it = statboard.getCounterIterator(); it.hasNext(); ) {
      Counter counter = it.next();
      childPacket.add(counter.getName(), counter.getValue());
    }
    retval.add(COUNTER, childPacket);

    // Get all states
    childPacket.clear();
    for (Iterator<State> it = statboard.getStateIterator(); it.hasNext(); ) {
      State state = it.next();
      if (state.getValue() != null) {
        childPacket.add(state.getName(), state.getValue());
      } else {
        Log.info("State " + state.getName() + " is null");
      }
    }
    retval.add(STATE, childPacket);

    childPacket.clear();
    for (Iterator<Gauge> it = statboard.getGaugeIterator(); it.hasNext(); ) {
      DataFrame packet = it.next().toFrame();
      if (packet != null) {
        childPacket.add(packet);
      }
    }
    retval.add(GAUGE, childPacket);

    childPacket.clear();
    for (Iterator<TimingMaster> it = statboard.getTimerIterator(); it.hasNext(); ) {
      DataFrame cap = it.next().toFrame();
      if (cap != null) {
        childPacket.add(cap);
      }
    }
    retval.add(TIMER, childPacket);

    childPacket.clear();
    for (Iterator<ArmMaster> it = statboard.getArmIterator(); it.hasNext(); ) {
      DataFrame cap = it.next().toFrame();
      if (cap != null) {
        childPacket.add(cap);
      }
    }
    retval.add(ARM, childPacket);

    return retval;
  }

}
