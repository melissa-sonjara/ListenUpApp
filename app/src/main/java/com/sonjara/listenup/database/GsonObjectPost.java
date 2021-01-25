package com.sonjara.listenup.database;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GsonObjectPost<S, T> extends Request<T>
{
    private final Gson m_gson;
    private final Class<T> m_responseClass;
    private final Response.Listener<T> m_listener;

    private final Map<String, String> m_params;

    public GsonObjectPost(Gson gson, String url, S payload, Class<T> responseClass, Response.Listener<T> l, Response.ErrorListener errorListener) throws AuthFailureError
    {
        super(Method.POST, url, errorListener);

        m_params = new HashMap<>();

        String json = gson.toJson(payload);
        m_params.put("data", json);

        this.m_listener = l;
        this.m_gson = gson;
        this.m_responseClass = responseClass;
    }

    /**
     * Returns a Map of parameters to be used for a POST or PUT request. Can throw {@link
     * AuthFailureError} as authentication may be required to provide these values.
     *
     * <p>Note that you can directly override {@link #getBody()} for custom data.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    @Override
    protected Map<String, String> getParams() throws AuthFailureError
    {
       return m_params;
    }

    /**
     * Subclasses must implement this to parse the raw network response and return an appropriate
     * response type. This method will be called from a worker thread. The response will not be
     * delivered if you return null.
     *
     * @param response Response from the network
     * @return The parsed response, or null in the case of an error
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response)
    {
        try
        {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            T r = (T) m_gson.fromJson(json, m_responseClass);
            return Response.success(r,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * Subclasses must implement this to perform delivery of the parsed response to their listeners.
     * The given response is guaranteed to be non-null; responses that fail to parse are not
     * delivered.
     *
     * @param response The parsed response returned by {@link
     *                 #parseNetworkResponse(NetworkResponse)}
     */
    @Override
    protected void deliverResponse(T response)
    {
        m_listener.onResponse(response);
    }
}
