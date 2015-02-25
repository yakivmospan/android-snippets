import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by Yakiv M. on 09.02.2015.
 */
public class AbsApi {

    protected static <S> ServerResponse<S> call(
            @NonNull OkHttpClient client,
            @NonNull Request request,
            @NonNull ConvertMethod<S> method) {
        ServerResponse result = new ServerResponse();
        try {
            Response response = client.newCall(request).execute();
            result = method.convert(response);
        } catch (IOException e) {
            L.d(e.toString());
            result.setException(e);
        }

        return result;
    }

    protected static <S> ServerResponse<S> call(@NonNull AbsCommand<S> command) {
        ServerResponse<S> response = call(
                command.createClient(), command.createRequest(), command.createConvertMethod()
        );
        command.onResponse(response);
        return response;
    }

    protected static <S> void callAsync(
            @NonNull OkHttpClient client, @NonNull Request request,
            @NonNull ConvertMethod<S> method, @Nullable RequestCallback callback) {
        client.newCall(request).enqueue(createCallback(method, callback));
    }

    protected static <S> void callAsync(
            @NonNull AbsCommand<S> command, @Nullable RequestCallback callback) {
        command.createClient()
                .newCall(command.createRequest())
                .enqueue(createCallback(command, callback));
    }

    private static <S> Callback createCallback(
            final AbsCommand<S> command,  final RequestCallback<S> callback) {
        return new Callback() {
            @Override
                public void onFailure(Request request, IOException e) {
                ConvertMethod<S> method = command.createConvertMethod();
                ServerResponse<S> serverResponse = method.convert(request, e);
                command.onResponse(serverResponse);

                if (callback != null) {
                    callback.onResponse(serverResponse);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                ConvertMethod<S> method = command.createConvertMethod();
                ServerResponse<S> serverResponse = method.convert(response);
                command.onResponse(serverResponse);

                if (callback != null) {
                    callback.onResponse(serverResponse);
                }
            }
        };
    }

    private static <S> Callback createCallback(
            final ConvertMethod<S> method, final RequestCallback<S> callback) {
        return new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                ServerResponse<S> serverResponse = method.convert(request, e);

                if (callback != null) {
                    callback.onResponse(serverResponse);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                ServerResponse<S> serverResponse = method.convert(response);
                if (callback != null) {
                    callback.onResponse(serverResponse);
                }
            }
        };
    }
}
