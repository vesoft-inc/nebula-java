# Async Nebula Java Client

This doc provides instructions on how to connect **Nebula Graph** with async Java client.

## Using Async Client

### Creating an Async Client

- Meta Client

  - `AsyncMetaClient` takes a list of addresses or a single address with host and port.
  - Use the `connect()` function to connect the client, or the `init()` function to connect and get a Map<String, Integer> which stores the spaces names and IDs.

  - The following code shows an example:

    ```java
    AsyncMetaClient asyncMetaClient = new AsyncMetaClientImpl("127.0.0.1", 45500);
    asyncMetaClient.init();
    ```

  - `listSpaces()` function takes no argument and returns `ListenableFuture<Optional<ListSpacesResp>>`, you can add your customized callback function to the `ListenableFuture`.

    Next check the existence of Optional. If it exists, check the ErrorCode then.

    If successful, you can get the spaces now. You can store them in a map or just print them out or do whatever you want. Here is an example:

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

  - To use `getPartsAlloc()`, `listTags()` and `listEdges()` functions, you need to pass an argument to them, be it of a space name (string) or space ID (integer). The return types are all `ListenableFuture` of the functions' corresponding response.
