package it.unibo.citizenDigitalTwin.data.connection.channel;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.unibo.citizenDigitalTwin.data.connection.channel.response.ChannelResponse;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class OkHttpChannelTest {

    private static final String TAG = "[OkHttpChannelTest]";
    private static final String VALUE_1_NAME = "value1name";
    private static final String VALUE_1_VALUE = "value1value";
    private static final String TEST_PATH = "/";
    private static final int PORT_SEND_POST = 6000;
    private static final int PORT_SEND_PATCH = 6001;
    private static final int PORT_SEND_GET = 6002;
    private static final int PORT_POST_RECEIVE = 6003;
    private static final int PORT_PATCH_RECEIVE = 6004;
    private static final int PORT_GET_RECEIVE = 6005;
    private static final int PORT_SEND_HEADER = 6006;
    private static final String MOCK_RESPONSE_BODY_START = "[text=";
    private static final String MOCK_RESPONSE_BODY_END = "]";
    private static final int DEFAULT_RESPONSE_CODE = 200;
    private static final String DEFAULT_RESPONSE_DATA = "{\"" + VALUE_1_NAME + "\":\"" + VALUE_1_VALUE + "\"}";
    private static final MockResponse DEFAULT_RESPONSE = new MockResponse()
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .setResponseCode(DEFAULT_RESPONSE_CODE)
                        .setBody(DEFAULT_RESPONSE_DATA);
    private static final String DEFAULT_TOKEN = "my_token";

    @Test
    public void sendPostCorrectly() {
        try {
            final MockWebServer server = createServer(PORT_SEND_POST);
            final HttpChannel channel = createClient(server);
            final JSONObject data = new JSONObject().put(VALUE_1_NAME,VALUE_1_VALUE);
            channel.post(TEST_PATH,data);
            checkIfRequestWasSentCorrectly(server,data);
        } catch (final Exception e) {
            Log.e(TAG,e.getLocalizedMessage());
            fail();
        }
    }

    @Test
    public void sendPatchCorrectly() {
        try {
            final MockWebServer server = createServer(PORT_SEND_PATCH);
            final HttpChannel channel = createClient(server);
            final JSONObject data = new JSONObject().put(VALUE_1_NAME,VALUE_1_VALUE);
            channel.patch(TEST_PATH,data);
            checkIfRequestWasSentCorrectly(server,data);
        } catch (final Exception e) {
            Log.e(TAG,e.getLocalizedMessage());
            fail();
        }
    }

    @Test
    public void sendGetCorrectly() {
        try {
            final MockWebServer server = createServer(PORT_SEND_GET);
            final HttpChannel channel = createClient(server);
            channel.get(TEST_PATH);
            final RecordedRequest request = server.takeRequest();
            assertEquals(TEST_PATH,request.getPath());
        } catch (final Exception e) {
            System.out.println(e.getLocalizedMessage());
            fail();
        }
    }

    @Test
    public void sendHeadersCorrectly() {
        try {
            final MockWebServer server = createServer(PORT_SEND_HEADER);
            final HttpChannel channel = createClient(server);
            final Map<HttpChannel.Header,String> headers = new HashMap<>();
            final String authorizationValue = HttpChannel.Header.BEARER_TOKEN.getName() + DEFAULT_TOKEN;
            headers.put(HttpChannel.Header.AUTHORIZATION,authorizationValue);
            channel.setDefaultHeaders(headers);
            channel.get(TEST_PATH);
            final RecordedRequest request = server.takeRequest();
            assertEquals(authorizationValue,request.getHeader(HttpChannel.Header.AUTHORIZATION.getName()));
        } catch (final Exception e) {
            System.out.println(e.getLocalizedMessage());
            fail();
        }
    }

    @Test
    public void receiveDataFromPostCorrectly() {
        try {
            final MockWebServer server = createServer(PORT_POST_RECEIVE);
            server.setDispatcher(createDispatcher());
            final HttpChannel channel = createClient(server);
            final JSONObject data = new JSONObject().put(VALUE_1_NAME,VALUE_1_VALUE);
            final ChannelResponse response = channel.post(TEST_PATH,data).get();
            checkIfASuccessResponseWasCorrectlyReceived(response);
        } catch (final Exception e) {
            Log.e(TAG,e.getLocalizedMessage());
            fail();
        }
    }

    @Test
    public void receiveDataFromPatchCorrectly() {
        try {
            final MockWebServer server = createServer(PORT_PATCH_RECEIVE);
            server.setDispatcher(createDispatcher());
            final HttpChannel channel = createClient(server);
            final JSONObject data = new JSONObject().put(VALUE_1_NAME,VALUE_1_VALUE);
            final ChannelResponse response = channel.patch(TEST_PATH,data).get();
            checkIfASuccessResponseWasCorrectlyReceived(response);
        } catch (final Exception e) {
            Log.e(TAG,e.getLocalizedMessage());
            fail();
        }
    }

    @Test
    public void receiveDataFromGetCorrectly() {
        try {
            final MockWebServer server = createServer(PORT_GET_RECEIVE);
            server.setDispatcher(createDispatcher());
            final HttpChannel channel = createClient(server);
            final ChannelResponse response = channel.get(TEST_PATH).get();
            checkIfASuccessResponseWasCorrectlyReceived(response);
        } catch (final Exception e) {
            Log.e(TAG,e.getLocalizedMessage());
            fail();
        }
    }

    private Dispatcher createDispatcher() {
        return createDispatcher(new HashMap<>());
    }

    private Dispatcher createDispatcher(final Map<String,MockResponse> responsesPerRequests) {
        return new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch (@NotNull final RecordedRequest request) {
                return Objects.requireNonNull(
                        responsesPerRequests.getOrDefault(
                                request.getPath(),
                                DEFAULT_RESPONSE
                        )
                );
            }
        };
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

    private void checkIfRequestWasSentCorrectly(final MockWebServer server, final JSONObject data) throws InterruptedException {
        final RecordedRequest request = server.takeRequest();
        assertEquals(TEST_PATH,request.getPath());
        assertEquals(data.toString(),getBody(request));
    }

    private void checkIfASuccessResponseWasCorrectlyReceived(final ChannelResponse response) {
        checkIfASuccessResponseWasCorrectlyReceived(response,DEFAULT_RESPONSE_CODE,DEFAULT_RESPONSE_DATA);
    }

    private void checkIfASuccessResponseWasCorrectlyReceived(final ChannelResponse response, final int expectedCode, final String expectedBody) {
        assertEquals(expectedCode,response.getCode());
        assertEquals(expectedBody,response.getData().get().toString());
        assertFalse(response.getErrorMessage().isPresent());
    }
}
