package it.unibo.citizenDigitalTwin.data.connection.channel;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OkHttpChannelTest {

    private static final String VALUE_1_NAME = "value1name";
    private static final String VALUE_1_VALUE = "value1value";
    private static final String BASE_PATH = "/";
    private static final int PORT_POST = 6000;
    private static final int PORT_PATCH = 6001;
    private static final int PORT_GET = 6002;
    private static final String MOCK_RESPONSE_BODY_START = "[text=";
    private static final String MOCK_RESPONSE_BODY_END = "]";
    private static final String PIPPO = "pippo";

    @Test
    public void sendPostCorrectly() {
        try {
            final MockWebServer server = createServer(PORT_POST);
            final HttpChannel channel = createClient(server);
            final JSONObject data = new JSONObject().put(VALUE_1_NAME,VALUE_1_VALUE);
            channel.post(BASE_PATH,data);
            checkIfRequestWasSentCorrectly(server,data);
        } catch (final Exception e) {
            System.out.println(e.getLocalizedMessage());
            fail();
        }
    }

    @Test
    public void sendPatchCorrectly() {
        try {
            final MockWebServer server = createServer(PORT_PATCH);
            final HttpChannel channel = createClient(server);
            final JSONObject data = new JSONObject().put(VALUE_1_NAME,VALUE_1_VALUE);
            channel.patch(BASE_PATH,data);
            checkIfRequestWasSentCorrectly(server,data);
        } catch (final Exception e) {
            System.out.println(e.getLocalizedMessage());
            fail();
        }
    }

    @Test
    public void sendGetCorrectly() {
        try {
            final MockWebServer server = createServer(PORT_GET);
            final HttpChannel channel = createClient(server);
            channel.get(BASE_PATH);
            final RecordedRequest request = server.takeRequest();
            assertEquals(BASE_PATH,request.getPath());
        } catch (final Exception e) {
            System.out.println(e.getLocalizedMessage());
            fail();
        }
    }

    private String getBody(final RecordedRequest request) {
        return request.getBody().toString()
                .replace(MOCK_RESPONSE_BODY_START,"")
                .replace(MOCK_RESPONSE_BODY_END,"");
    }

    private synchronized MockWebServer createServer(final int port) throws IOException {
        final MockWebServer server = new MockWebServer();
        server.start(port);
        return server;
    }

    private OkHttpChannel createClient(final MockWebServer server) {
        return new OkHttpChannel(server.getHostName()+":"+server.getPort());
    }

    private void checkIfRequestWasSentCorrectly(final MockWebServer server, final JSONObject data) throws InterruptedException, JSONException {
        final RecordedRequest request = server.takeRequest();
        assertEquals(BASE_PATH,request.getPath());
        assertEquals(data.toString(),getBody(request));
    }
}
