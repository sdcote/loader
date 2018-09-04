package coyote.commons.network.http.responder;

import coyote.commons.StringUtil;
import coyote.commons.network.MimeType;
import coyote.commons.network.http.*;
import coyote.dataframe.DataFrame;
import coyote.dataframe.marshal.JSONMarshaler;
import coyote.dataframe.marshal.MarshalException;
import coyote.dataframe.marshal.XMLMarshaler;
import coyote.loader.log.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public abstract class ServiceResponder extends DefaultResponder implements Responder {


    /**
     * Create a dataframe out of the body of the request in the given session.
     *
     * <p>This method will use the value of the Content-type entity header to assist in parsing the request body.</p>
     *
     * <p>A null return value implies errors in parsing the body or the data frame and the caller can return a
     * "Bad Request" to the requester indicating a problem with the body of the request.</p>
     *
     * @param session the session containing the request.
     * @return a data frame populated with the data in the body, an empty data frame if no body was in the session or
     * null if there were errors parsing the body or the data.
     */
    protected DataFrame marshalBody(HTTPSession session) {
        DataFrame retval = null;
        Body body = null;
        try {
            body = session.parseBody();
        } catch (IOException | ResponseException e) {
            Log.error("Exception parsing request body: "+e.getClass().getSimpleName()+": "+e.getMessage());
        }

        if (body != null && body.size() > 0) {
            try {
                retval = parseBody(body, session);
                if (retval == null) {
                    retval = new DataFrame();
                }
            } catch (final Exception e) {
                Log.error("Exception parsing body data: "+e.getClass().getSimpleName()+": "+e.getMessage());
            }
        }
        return retval;
    }

    /**
     * Retrieve the first data frame from the body of the request.
     *
     * @param body    the request body
     * @param session the session request
     * @return the first dataframe parsed from the body
     * @throws MarshalException if the JSON or XML data could not be parsed
     */
    private DataFrame parseBody(Body body, HTTPSession session) throws MarshalException {
        DataFrame retval = null;
        for (final String key : body.keySet()) {
            final Object obj = body.get(key);

            String data = null;
            if (obj instanceof String) {
                data = (String) obj;
            } else if (obj instanceof ByteBuffer) {
                ByteBuffer buffer = (ByteBuffer) obj;
                data = new String(buffer.array());
            } else {
                Log.error("I don't know how to parse a " + obj.getClass().getName() + " body object.");
                throw new MarshalException("Problems parsing request body. Check logs for details.");
            }

            if (data != null && StringUtil.isNotEmpty(data)) {
                List<DataFrame> frames = null;
                String contentType = session.getRequestHeaders().get(HTTP.HDR_CONTENT_TYPE.toLowerCase());
                if (StringUtil.isNotEmpty(contentType) && contentType.contains(MimeType.XML.getType())) {
                    frames = XMLMarshaler.marshal(data);
                    if (frames == null || frames.size() == 0) {
                        throw new MarshalException("No valid XML data found");
                    }
                } else {
                    frames = JSONMarshaler.marshal(data);
                }
                if (frames != null && frames.size() > 0) {
                    retval = frames.get(0);
                    break; // only get the first dataframe
                } else {
                    Log.warn("No dataframe list to process");
                }
            }
        }
        return retval;
    }


    /**
     * Retrieved the preferred (first) accept type from the request session.
     *
     * @param session the session from which to retrieve the Accept-type value
     * @return the value of the Accept-type header or null if it does not exist.
     */
    protected String getAcceptType(final HTTPSession session) {
        return getPreferredHeaderValue(session, HTTP.HDR_ACCEPT);
    }


    /**
     * Retrieved the content type from the request session.
     *
     * @param session the session from which to retrieve the Content-type value
     * @return the value of the Content-type header or null if it does not exist.
     */
    protected String getContentType(final HTTPSession session) {
        return getPreferredHeaderValue(session, HTTP.HDR_CONTENT_TYPE);
    }


    /**
     * Retrieve the first header value for the given named header.
     *
     * @param session    the session containing the headers
     * @param headerName the name of the header to query
     * @return the first value in that header (may be null)
     */
    private String getPreferredHeaderValue(final HTTPSession session, final String headerName) {
        String retval = null;
        if (session != null && StringUtil.isNotBlank(headerName)) {
            final String value = session.getRequestHeaders().get(headerName.toLowerCase());
            if (StringUtil.isNotEmpty(value)) {
                final String[] tokens = value.split(",");
                retval = tokens[0];
            }
        }
        return retval;
    }

    @Override
    public String getMimeType() {
        return MimeType.JSON.toString();
    }


    @Override
    public Status getStatus() {
        return Status.OK;
    }


    @Override
    public String getText() {
        throw new IllegalStateException("This method should not be called");
    }

}
