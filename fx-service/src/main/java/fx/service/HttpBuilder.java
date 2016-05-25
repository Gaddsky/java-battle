package fx.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class used for build http-request and receiving a http-response
 */
public class HttpBuilder {
    private HttpUriRequest request;
    private String uri;
    /**
     * uri of internet-resource, (for example http://bash.im)
     */
    private String url;
    /**
     * url (full query to internet-resource with parameters)
     */
    private Map<String, String> headers = new HashMap<String, String>();
    /**
     * list of name-value pairs of request headers
     */
    public HttpBuilder(String uri) {
        this.uri = uri;
    }

    public HttpBuilder get() {
        request = new HttpGet(url);
        return this;
    }

    public HttpBuilder post() {
        request = new HttpPost(url);
        return this;
    }

    public HttpBuilder header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HttpBuilder parameter(String name, String value) {
        StringBuilder urlSB = new StringBuilder();
        if (url == null) { // if no parameters added for this moment
            urlSB.append(uri);
            urlSB.append('?');
        } else {
            urlSB.append('&'); // second and others parameter
        }
        urlSB.append(name).append('=').append(value);
        url = urlSB.toString();
        return this;
    }

    public Response execute() {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Response response = new Response();

        for (String name : headers.keySet()) {
            request.addHeader(name, headers.get(name));
        }
        try {
            HttpResponse resp = httpClient.execute(request);
            response.setCode(resp.getStatusLine().getStatusCode());
            response.setMessage(resp.getStatusLine().getReasonPhrase());
            HttpEntity entity = resp.getEntity();
            if (entity != null) {
                InputStream stream = entity.getContent();
                String line;
                StringBuilder body = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                while ((line = br.readLine()) != null) {
                    body.append(line);
                }
                response.setBody(body.toString());
            }
        } catch (
                IOException e
                ) {
            e.printStackTrace();
        }
        return response;
    }
}
