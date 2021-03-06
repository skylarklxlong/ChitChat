package online.himakeit.chitchat.server.network.http;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URI;

/**
 * @author：LiXueLong
 * @date：2018/1/8
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class AsyncHttpResponseHandler implements ResponseHandlerInterface {
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;
    protected static final int PROGRESS_MESSAGE = 4;
    protected static final int RETRY_MESSAGE = 5;

    protected static final int BUFFER_SIZE = 4096;

    private Handler handler;
    public static final String DEFAULT_CHARSET = "UTF-8";
    private String responseCharset = DEFAULT_CHARSET;
    private Boolean useSynchronousMode = false;

    private URI requestURI = null;
    private Header[] requestHeaders = null;

    @Override
    public URI getRequestURI() {
        return this.requestURI;
    }

    @Override
    public Header[] getRequestHeaders() {
        return this.requestHeaders;
    }

    @Override
    public void setRequestURI(URI requestURI) {
        this.requestURI = requestURI;
    }

    @Override
    public void setRequestHeaders(Header[] requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    // avoid leaks by using a non-anonymous handler class
    // with a weak reference
    static class ResponderHandler extends Handler {
        private final WeakReference<AsyncHttpResponseHandler> mResponder;

        ResponderHandler(AsyncHttpResponseHandler service) {
            mResponder = new WeakReference<AsyncHttpResponseHandler>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            AsyncHttpResponseHandler service = mResponder.get();
            if (service != null) {
                service.handleMessage(msg);
            }
        }
    }

    public boolean getUseSynchronousMode() {
        return (useSynchronousMode);
    }

    @Override
    public void setUseSynchronousMode(boolean value) {
        useSynchronousMode = value;
    }

    /**
     * Sets the charset for the response string. If not set, the default is UTF-8.
     *
     * @param charset to be used for the response string.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Charset</a>
     */
    public void setCharset(final String charset) {
        this.responseCharset = charset;
    }

    public String getCharset() {
        return this.responseCharset == null ? DEFAULT_CHARSET : this.responseCharset;
    }

    /**
     * Creates a new AsyncHttpResponseHandler
     */
    public AsyncHttpResponseHandler() {
        // Set up a handler to post events back to the correct thread if possible
        if (Looper.myLooper() != null) {
            handler = new ResponderHandler(this);
        }
    }


    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when the request progress, override to handle in your own code
     *
     * @param bytesWritten offset from start of file
     * @param totalSize    total size of file
     */
    public void onProgress(int bytesWritten, int totalSize) {
    }

    /**
     * Fired when the request is started, override to handle in your own code
     */
    public void onStart() {
    }

    /**
     * Fired in all cases when the request is finished, after both success and failure, override to
     * handle in your own code
     */
    public void onFinish() {
    }

    /**
     * Fired when a request returns successfully, override to handle in your own code
     *
     * @param content the body of the HTTP response from the server
     * @deprecated use {@link #onSuccess(int, Header[], byte[])}
     */
    @Deprecated
    public void onSuccess(String content) {
    }

    /**
     * Fired when a request returns successfully, override to handle in your own code
     *
     * @param statusCode the status code of the response
     * @param headers    the headers of the HTTP response
     * @param content    the body of the HTTP response from the server
     * @deprecated use {@link #onSuccess(int, Header[], byte[])}
     */
    @Deprecated
    public void onSuccess(int statusCode, Header[] headers, String content) {
        onSuccess(statusCode, content);
    }

    /**
     * Fired when a request returns successfully, override to handle in your own code
     *
     * @param statusCode the status code of the response
     * @param content    the body of the HTTP response from the server
     * @deprecated use {@link #onSuccess(int, Header[], byte[])}
     */
    @Deprecated
    public void onSuccess(int statusCode, String content) {
        onSuccess(content);
    }

    /**
     * Fired when a request returns successfully, override to handle in your own code
     *
     * @param statusCode   the status code of the response
     * @param headers      return headers, if any
     * @param responseBody the body of the HTTP response from the server
     */
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            String response = responseBody == null ? null : new String(responseBody, getCharset());
            onSuccess(statusCode, headers, response);
        } catch (UnsupportedEncodingException e) {
            onFailure(statusCode, headers, e, null);
        }
    }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     *
     * @param error the underlying cause of the failure
     * @deprecated use {@link #onFailure(Throwable, String)}
     */
    @Deprecated
    public void onFailure(Throwable error) {
    }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     *
     * @param error   the underlying cause of the failure
     * @param content the response body, if any
     * @deprecated use {@link #onFailure(int, Header[], byte[], Throwable)}
     */
    @Deprecated
    public void onFailure(Throwable error, String content) {
        // By default, call the deprecated onFailure(Throwable) for compatibility
        onFailure(error);
    }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     *
     * @param statusCode return HTTP status code
     * @param error      the underlying cause of the failure
     * @param content    the response body, if any
     * @deprecated use {@link #onFailure(int, Header[], byte[], Throwable)}
     */
    @Deprecated
    public void onFailure(int statusCode, Throwable error, String content) {
        // By default, call the chain method onFailure(Throwable,String)
        onFailure(error, content);
    }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     *
     * @param statusCode return HTTP status code
     * @param headers    return headers, if any
     * @param error      the underlying cause of the failure
     * @param content    the response body, if any
     * @deprecated use {@link #onFailure(int, Header[], byte[], Throwable)}
     */
    @Deprecated
    public void onFailure(int statusCode, Header[] headers, Throwable error, String content) {
        // By default, call the chain method onFailure(int,Throwable,String)
        onFailure(statusCode, error, content);
    }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     *
     * @param statusCode   return HTTP status code
     * @param headers      return headers, if any
     * @param responseBody the response body, if any
     * @param error        the underlying cause of the failure
     */
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        try {
            String response = responseBody == null ? null : new String(responseBody, getCharset());
            onFailure(statusCode, headers, error, response);
        } catch (UnsupportedEncodingException e) {
            onFailure(statusCode, headers, e, null);
        }
    }

    /**
     * Fired when a retry occurs, override to handle in your own code
     */
    public void onRetry() {
    }


    //
    // Pre-processing of messages (executes in background threadpool thread)
    //

    @Override
    final public void sendProgressMessage(int bytesWritten, int bytesTotal) {
        sendMessage(obtainMessage(PROGRESS_MESSAGE, new Object[]{bytesWritten, bytesTotal}));
    }

    @Override
    final public void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBody) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{statusCode, headers, responseBody}));
    }

    @Override
    final public void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{statusCode, headers, responseBody, error}));
    }

    @Override
    final public void sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null));
    }

    @Override
    final public void sendFinishMessage() {
        sendMessage(obtainMessage(FINISH_MESSAGE, null));
    }

    @Override
    final public void sendRetryMessage() {
        sendMessage(obtainMessage(RETRY_MESSAGE, null));
    }

    // Methods which emulate android's Handler and Message methods
    protected void handleMessage(Message msg) {
        Object[] response;

        switch (msg.what) {
            case SUCCESS_MESSAGE:
                response = (Object[]) msg.obj;
                if (response != null && response.length >= 3) {
                    onSuccess((Integer) response[0], (Header[]) response[1], (byte[]) response[2]);
                } else {
                }
                break;
            case FAILURE_MESSAGE:
                response = (Object[]) msg.obj;
                if (response != null && response.length >= 4) {
                    onFailure((Integer) response[0], (Header[]) response[1], (byte[]) response[2], (Throwable) response[3]);
                } else {
                }
                break;
            case START_MESSAGE:
                onStart();
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
            case PROGRESS_MESSAGE:
                response = (Object[]) msg.obj;
                if (response != null && response.length >= 2) {
                    try {
                        onProgress((Integer) response[0], (Integer) response[1]);
                    } catch (Throwable t) {
                    }
                } else {
                }
                break;
            case RETRY_MESSAGE:
                onRetry();
                break;
        }
    }

    protected void sendMessage(Message msg) {
        if (getUseSynchronousMode() || handler == null) {
            handleMessage(msg);
        } else if (!Thread.currentThread().isInterrupted()) { // do not send messages if request has been cancelled
            handler.sendMessage(msg);
        }
    }

    protected void postRunnable(Runnable r) {
        if (r != null) {
            handler.post(r);
        }
    }

    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg;
        if (handler != null) {
            msg = handler.obtainMessage(responseMessage, response);
        } else {
            msg = Message.obtain();
            if (msg != null) {
                msg.what = responseMessage;
                msg.obj = response;
            }
        }
        return msg;
    }

    @Override
    public void sendResponseMessage(HttpResponse response) throws IOException {
        // do not process if request has been cancelled
        if (!Thread.currentThread().isInterrupted()) {
            StatusLine status = response.getStatusLine();
            byte[] responseBody;
            responseBody = getResponseData(response.getEntity());
            // additional cancellation check as getResponseData() can take non-zero time to process
            if (!Thread.currentThread().isInterrupted()) {
                if (status.getStatusCode() >= 300) {
                    sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), responseBody, new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
                } else {
                    sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), responseBody);
                }
            }
        }
    }

    byte[] getResponseData(HttpEntity entity) throws IOException {
        byte[] responseBody = null;
        if (entity != null) {
            InputStream instream = entity.getContent();
            if (instream != null) {
                long contentLength = entity.getContentLength();
                if (contentLength > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                }
                if (contentLength < 0) {
                    contentLength = BUFFER_SIZE;
                }
                try {
                    ByteArrayBuffer buffer = new ByteArrayBuffer((int) contentLength);
                    try {
                        byte[] tmp = new byte[BUFFER_SIZE];
                        int l, count = 0;
                        // do not send messages if request has been cancelled
                        while ((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                            count += l;
                            buffer.append(tmp, 0, l);
                            sendProgressMessage(count, (int) contentLength);
                        }
                    } finally {
                        instream.close();
                    }
                    responseBody = buffer.toByteArray();
                } catch (OutOfMemoryError e) {
                    System.gc();
                    throw new IOException("File too large to fit into available memory");
                }
            }
        }
        return responseBody;
    }
}
