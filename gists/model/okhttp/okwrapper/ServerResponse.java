import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * Created by Yakiv M. on 09.02.2015.
 */
public class ServerResponse<SuccessResult> {
    private int mCode;
    private IOException mException;

    private boolean mIsSuccessufl;
    private SuccessResult mSuccessResult;

    private Headers mHeaders;

    private Response mWrapedResponse;

    private Request mWrapedRequest;

    public boolean isConnectionIssue() {
        return mException instanceof ConnectException || mException instanceof UnknownHostException;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public IOException getException() {
        return mException;
    }

    public void setException(IOException exception) {
        mException = exception;
    }

    public SuccessResult getSuccessResult() {
        return mSuccessResult;
    }

    public void setSuccessResult(SuccessResult successResult) {
        mSuccessResult = successResult;
    }

    public Headers getHeaders() {
        return mHeaders;
    }

    public void setHeaders(Headers headers) {
        mHeaders = headers;
    }

    public boolean isSuccessufl() {
        return mIsSuccessufl;
    }

    public void setSuccessufl(boolean isSuccessufl) {
        mIsSuccessufl = isSuccessufl;
    }

    public Response getWrapedResponse() {
        return mWrapedResponse;
    }

    public void setWrapedResponse(Response wrapedResponse) {
        mWrapedResponse = wrapedResponse;
    }

    public Request getWrapedRequest() {
        return mWrapedRequest;
    }

    public void setWrapedRequest(Request wrapedRequest) {
        mWrapedRequest = wrapedRequest;
    }
}
