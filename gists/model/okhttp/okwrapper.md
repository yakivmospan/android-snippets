### Model: okhttp wrapper

Snippet shows how to use [okwrapper](/gists/model/okhttp/okwrapper)

- Init request manager

```java
public class App extends Application {
	@Override
	public void onCreate() {
		//..
        RequestManager.initializeWith(this);
	}
}
```
- Create your API, class that will manage requests

```java
public class ExampleApi extends AbsApi {
	//...
}
```

- Use built in `AbsApi` help methods

```java
public class ExampleApi extends AbsApi {
    public static ServerResponse<List<People>> loadAllPeople () {
		//create request builder
		Request.Builder builder = new Request.Builder();
		
		//add authorization
		builder.header("Authorization", Credentials.basic(
                        "login", "password")
        );
		
		//add headers
		builder.addHeader("App-Version", BuildConfig.VERSION_NAME);
		
		//add body
        FormEncodingBuilder bodyBuilder = new FormEncodingBuilder();
        bodyBuilder.add("organization", organizationId);
		
        builder.post(bodyBuilder.build());
		
		//add url
		builder.url(UrlFactory.createPeopleUrl());
		
		//make sync call to the server
		//use callAsync() for async requests
        return call(
                //set client to be used for request
				RequestManager.client(),
                builder.build(),
				//choose result convert method
				//by default there are available 3 methods
				//JsonConverter, XmlConverter and StringConverter
				//you can create your own one by extending ConvertMethod
                new JsonConverter<>(new PeopleJsonHandler())
        );
    }
}
```

- Separate your API requests in Commands

```java
public class PeopleCommand extends AbsCommand<List<People>> {

    @NonNull
    @Override
    public OkHttpClient createClient() {
        return RequestManager.client();
    }

    @NonNull
    @Override
    public Request createRequest() {
		Request.Builder builder = new Request.Builder();
		builder.header("Authorization", Credentials.basic(
                        "login", "password")
        );
		builder.addHeader("App-Version", BuildConfig.VERSION_NAME);
        FormEncodingBuilder bodyBuilder = new FormEncodingBuilder();
        bodyBuilder.add("organization", organizationId);
        builder.post(bodyBuilder.build());
		builder.url(UrlFactory.createPeopleUrl());
        return builder.build();
    }

    @NonNull
    @Override
    public ConvertMethod<List<People>> createConvertMethod() {
        return new JsonConverter<>(new PeopleJsonHandler());
    }

    @NonNull
    @Override
    public void onResponse(ServerResponse<List<People>> response) {
		//operate with your result before finishing
		if (response.isSuccessufl()) {
            //..
        }
    }
}
```

- And simply use them in your API class
```java
public class ExampleApi extends AbsApi {

	//...

    public static ServerResponse<List<People>> loadAllPeople () {
        return call(new PeopleCommand());
    }
}
```



