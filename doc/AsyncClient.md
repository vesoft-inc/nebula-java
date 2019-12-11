#Async Nebula Java Client

This doc provides instructions on how to use async Java client to connect Nebula Graph.

## Using Async Client

### Creating an async client

- Meta Client

  - ```AsyncMetaClient``` takes a list of address or a single address with host and port. 
  - Use ```connect()``` function to make the client connected, or use  ```init()``` to connect and also get a Map<String, Integer> which stores the spaces name and id.

  - The following code shows an example:

    ```java
    AsyncMetaClient asyncMetaClient = new AsyncMetaClientImpl("127.0.0.1", 45500);
    asyncMetaClient.init();
    ```

  - ```listSpaces()``` function takes no argument and returns ```ListenableFuture<Optional<ListSpacesResp>>```, you can add you self-defined callback function to the ListenableFuture.

    What you should do is check the present of Optional. If it is present, check the ErrorCode then. 

    If succeed, you are getting the spaces now. You can either store them in a map or just print them out or what ever you want. Here is an example:

    ```java
    private static ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    
    ListenableFuture<Optional<ListSpacesResp>> future = asyncMetaClient.listSpaces();
    
    Futures.addCallback(future, new FutureCallback<Optional<ListSpacesResp>>() {
      @Override
      public void onSuccess(@Nullable Optional<ListSpacesResp> listSpacesRespOptional) {
        if (listSpacesRespOptional.isPresent()) {
          ListSpacesResp resp = listSpacesRespOptional.get();
          if (resp.getCode() != ErrorCode.SUCCEEDED) {
            LOGGER.error(String.format("List Spaces Error Code: %s", resp.getCode()));
            return;
          }
          for (IdName space : resp.getSpaces()) {
            LOGGER.info(String.format("Space name: %s, Space Id: %d", space.name, space.id));
          }
        } else {
          LOGGER.info(String.format("No Space Founded"));
        }
      }
    
      @Override
      public void onFailure(Throwable throwable) {
        LOGGER.error("List Spaces Error");
      }
    }, service);
    ```

  - Other functions including ```getPartsAlloc()```, ```listTags()``` and ```listEdges()``` take a specific String of space name or Integer of space Id to proceed. The return type are all ListenableFuture of response of the corresponding function type.