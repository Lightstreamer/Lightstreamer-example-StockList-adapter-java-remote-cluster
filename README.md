# Lightstreamer - Stock-List Demo - Java Remote Adapter

The Stock-List demos simulate a market data feed and front-end for stock quotes. They show a list of stock symbols and updates prices and other fields displayed on the page in real-time.

This project contains the source code and all the resources needed to install a remote version of the Java Stock-List Demo Data Adapter.

As example of [Clients Using This Adapter](https://github.com/Lightstreamer?utf8=%E2%9C%93&q=lightstreamer-example-stocklist-client&type=&language=), you may refer to the [Lightstreamer - Basic Stock-List Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-StockList-client-javascript#basic-stock-list-demo---html-client) and view the corresponding [Live Demo](http://demos.lightstreamer.com/StockListDemo_Basic).

## Details

This project includes the implementation of the remote versions of the DataProvider interface for the *Stock-List Demo*.
The Metadata Adapter functionalities are absolved by the `LiteralBasedProvider`, a simple Remote Metadata Adapter already included in the [Remote Java Adapters SDK](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-remote) binaries, which is enough for all demo clients.
See also [LiteralBasedProvider Metadata Adapter](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-remote#literalbasedprovider-metadata-adapter).

* `StockQuotesDataAdapter.java` is a porting of the class with the same name from the [Lightstreamer - Stock-List Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java). 
It implements the *DataProvider* interface and calls back Lightstreamer through the *ItemEventListener* interface. Use it as a starting point to implement your custom data adapter.
It also implements the custom `ExternalFeedListener` interface to receive updates from the `ExternalFeedSimulator` instance (see next point)
* `ExternalFeedSimulator.java` is the same exact class from the [Lightstreamer - Stock-List Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java). It randomly generates the
stock quotes used by the demo.
* `ServerMain.java` is the main of the Remote Server application. It instantiates a DataProviderServer and a MetadataProviderServer and launches them using the `ServerStarter` class.

Check out the sources for further explanations.

![General Architecture](generalarchitecture.png)

The project supports two different policies for the connection with the Proxy Adapters on the Server.
The normal connection policy, as also depicted in the architecture diagram above, consists in the Remote Server connecting to a listening TCP port opened by the Proxy Adapter.
Another available policy is to invert the roles and have the Remote Server open a listening TCP port and the Proxy Adapter connect to it.
The two options are implemented by the `ServerStarter` class and can be chosen via a command line argument, which affects both the Data Adapter and the Metadata Adapter connections.
Obviously, each option can be chosen only provided that the Proxy Adapters on the Server are configured accordingly.

## Install

### Install the demo in the normal way

As said, in this case, the Remote Java Adapter Server will connect to the Proxy Adapters, not vice-versa.

If you want to install a version of this demo in your local Lightstreamer server, follow these steps:
* Download the [latest Lightstreamer distribution](http://www.lightstreamer.com/download/) (Lightstreamer Server comes with a free non-expiring demo license for 20 connected users) from [Lightstreamer Download page](http://www.lightstreamer.com/download.htm), and install it, as explained in the `GETTING_STARTED.TXT` file in the installation home directory.
* Get the `deploy_normal.zip` file of the [latest release](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java-remote/releases) and unzip it.
    * Plug the Proxy Data Adapter and the Proxy MetaData Adapter into the Server: go to the `Deployment_LS` folder and copy the `RemoteStockList` directory and all of its files to the `adapters` folder of your Lightstreamer Server installation.
    * Alternatively, you may plug the **robust** versions of the Proxy Data Adapter and the Proxy MetaData Adapter: go to the `Deployment_LS(robust)` folder and copy the `RemoteStockList` directory and all of its files into `adapters`. This Adapter Set demonstrates the provided "robust" versions of the standard Proxy Data and Metadata Adapters. The robust Proxy Data Adapter can handle the case in which a Remote Data Adapter is missing or fails, by suspending the data flow and trying to connect to a new Remote Data Adapter instance. The robust Proxy Metadata Adapter can handle the case in which a Remote Metadata Adapter is missing or fails, by temporarily denying all client requests and trying to connect to a new Remote Data Adapter instance. See the comments embedded in the provided [`adapters.xml` file template](https://lightstreamer.com/docs/ls-server/latest/remote_adapter_robust_conf_template/adapters.xml), for details. Note that this extended Adapter Set also requires that the client is able to manage the case of missing data. Currently, only the [Lightstreamer - Stock-List Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-StockList-client-javascript#stocklist-demo) and the [Lightstreamer - Framed Stock-List Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-StockList-client-javascript#framed-stocklist-demo) front-ends have such ability.
    * Open a command line to the `Deployment_Java_Server` folder and launch the Java Remote Server through the proper `start_adapter` script or (in Windows) by issuing the following command:
      ```sh
          java -cp "./example-StockList-adapter-java-remote-0.1.0-SNAPSHOT.jar;./dependency/*" ^
              com.lightstreamer.example_StockList_adapter_java_remote.server.ServerMain ^
              -host localhost -metadata_rrport 6663 -data_rrport 6661
      ```
* Launch Lightstreamer Server. The Server startup will complete only after a successful connection between the Proxy Adapters and the Remote Adapters.
* Test the Adapter, launching one of the [compatible clients](https://github.com/Lightstreamer?utf8=%E2%9C%93&q=lightstreamer-example-stocklist-client&type=&language=).
    * To make the Stock-List Demo applications, use the newly installed Adapter Set, you need to modify the code to change the required Adapter Set name from DEMO to STOCKLISTDEMO_REMOTE. Depending on the client library in use, the code might vary (e.g., in JavaScript `new LightstreamerClient(hostToUse,"DEMO");` has to be replaced by `new LightstreamerClient(hostToUse, "STOCKLISTDEMO_REMOTE");`). (You don't need to reconfigure the Data Adapter name, as it is the same in both Adapter Sets).
    * In case the JavaScript client is used, you might need to disable the connection sharing to avoid adapter sets conflicts (e.g., by removing or modifiyng `sharingClient.connectionSharing.enableSharing("DemoCommonConnection","ls/","SHARE_SESSION", true);`)
You can now launch the demo that will be fed by the remote adapter.

#### Available improvements

##### Add Encryption

Each TCP connection from a Remote Adapter can be encrypted via TLS. To have the Proxy Adapters accept only TLS connections, a suitable configuration should be added in adapters.xml in the <data_provider> block, like this:
```xml
  <data_provider>
    ...
    <param name="tls">Y</param>
    <param name="tls.keystore.type">JKS</param>
    <param name="tls.keystore.keystore_file">my_keystore_path</param>
    <param name="tls.keystore.keystore_password.type">file</param>
    <param name="tls.keystore.keystore_password">my_keystore_password_path</param>
    ...
  </data_provider>
```
and the same should be added in the <metadata_provider> block.
This requires that a suitable keystore with a valid certificate is provided. See the configuration details in the [provided template](https://lightstreamer.com/docs/ls-server/latest/remote_adapter_robust_conf_template/adapters.xml).

The sample Remote Server provided in the `Deployment_Java_Server` directory in `deploy_normal.zip` is already predisposed for TLS connection on all ports. You can rerun the demo with the new configuration by launching the Java Remote Server with a command like this:
```sh
    java -cp "./example-StockList-adapter-java-remote-0.1.0-SNAPSHOT.jar;./dependency/*" ^
        com.lightstreamer.example_StockList_adapter_java_remote.server.ServerMain ^
        -host xxxxxxxx -tls -metadata_rrport 6663 -data_rrport 6661
```
where the same hostname supported by the provided certificate must be supplied.

NOTE: For your experiments, you can configure the adapters.xml to use the same JKS keystore "myserver.keystore" provided out of the box in the Lightstreamer distribution.
Since this keystore contains an invalid certificate, the Remote Server should be configured to "trust" it and to omit certificate hostname verification.
Since the sample code in the `ServerStarter` class leans on the JDK implementation, trusted certificates information can be provided on the command line through the JSSE system properties `javax.net.ssl.trustStore` and `javax.net.ssl.trustStorePassword`.
Hostname verification is done by the `ServerStarter` class directly and can be suppressed via command line.
The command to be issued should be of this form:
```sh
    java -cp "./example-StockList-adapter-java-remote-0.1.0-SNAPSHOT.jar;./dependency/*" ^
        -Djavax.net.ssl.trustStore=<path-to-myserver.keystore> -Djavax.net.ssl.trustStorePassword=<see-myserver.keypass> ^
        com.lightstreamer.example_StockList_adapter_java_remote.server.ServerMain ^
        -host localhost -tls noverify -metadata_rrport 6663 -data_rrport 6661
```

##### Add Authentication

Each TCP connection from a Remote Adapter can be subject to Remote Adapter authentication through the submission of user/password credentials. To enforce credential check on the Proxy Adapters, a suitable configuration should be added in adapters.xml in the <data_provider> block, like this:
```xml
  <data_provider>
    ...
    <param name="auth">Y</param>
    <param name="auth.credentials.1.user">user1</param>
    <param name="auth.credentials.1.password">pwd1</param>
    ...
  </data_provider>
```
and the same should be added in the <metadata_provider> block.

See the configuration details in the [provided template](https://lightstreamer.com/docs/ls-server/latest/remote_adapter_robust_conf_template/adapters.xml).
The sample Remote Server provided in the `Deployment_Java_Server` directory in `deploy_normal.zip` is already predisposed for credential submission on both adapters. You can rerun the demo with the new configuration by launching the Java Remote Server with a command like this:
```sh
    java -cp "./example-StockList-adapter-java-remote-0.1.0-SNAPSHOT.jar;./dependency/*" ^
        com.lightstreamer.example_StockList_adapter_java_remote.server.ServerMain ^
        -host localhost -user user1 -password pwd1 -metadata_rrport 6663 -data_rrport 6661
```

Authentication can (and should) be combined with TLS encryption.
In the TLS case, the Proxy Adapter can further enforce authentication by requiring that the Remote Server issues a trusted client-side TLS certificate. This can be done by adding
```xml
    <param name="tls.force_client_auth">Y</param>
```
in the <data_provider> and/or <metadata_provider> block.

### Install the demo with the connection inversion option

As said, in this case, the Proxy Adapters will connect to the Remote Java Adapter Server, not vice-versa.
This Requires a specific configuration on Lightstreamer Server in adapters.xml, where
```xml
    <param name="remote_host">localhost</param>
```
is added in the <data_provider> or <metadata_provider> block corresponding to each Proxy Adapter.

If you want to install a version of this demo in your local Lightstreamer server, follow these steps:
* Download the [latest Lightstreamer distribution](http://www.lightstreamer.com/download/) (Lightstreamer Server comes with a free non-expiring demo license for 20 connected users) from [Lightstreamer Download page](http://www.lightstreamer.com/download.htm), and install it, as explained in the `GETTING_STARTED.TXT` file in the installation home directory.
* Get the `deploy_inverted.zip` file of the [latest release](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java-remote/releases) and unzip it.
    * Plug the Proxy Data Adapter and the Proxy MetaData Adapter into the Server: go to the `Deployment_LS` folder and copy the `RemoteStockList` directory and all of its files to the `adapters` folder of your Lightstreamer Server installation.
    * Alternatively, you may plug the **robust** versions of the Proxy Data Adapter and the Proxy MetaData Adapter: go to the `Deployment_LS(robust)` folder and copy the `RemoteStockList` directory and all of its files into `adapters`. This Adapter Set demonstrates the provided "robust" versions of the standard Proxy Data and Metadata Adapters. The robust Proxy Data Adapter can handle the case in which a Remote Data Adapter is missing or fails, by suspending the data flow and trying to connect to a new Remote Data Adapter instance. The robust Proxy Metadata Adapter can handle the case in which a Remote Metadata Adapter is missing or fails, by temporarily denying all client requests and trying to connect to a new Remote Data Adapter instance. See the comments embedded in the provided [`adapters.xml` file template](https://lightstreamer.com/docs/ls-server/latest/remote_adapter_robust_conf_template/adapters.xml), for details. Note that this extended Adapter Set also requires that the client is able to manage the case of missing data. Currently, only the [Lightstreamer - Stock-List Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-StockList-client-javascript#stocklist-demo) and the [Lightstreamer - Framed Stock-List Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-StockList-client-javascript#framed-stocklist-demo) front-ends have such ability.
    * Open a command line to the `Deployment_Java_Server` folder and launch the Java Remote Server through the proper `start_adapter` script or (in Windows) by issuing the following command:
      ```sh
          java -cp "./example-StockList-adapter-java-remote-0.1.0-SNAPSHOT.jar;./dependency/*" ^
              com.lightstreamer.example_StockList_adapter_java_remote.server.ServerMain ^
              -metadata_rrport 6663 -data_rrport 6661
      ```
* Launch Lightstreamer Server. The Server startup will complete only after a successful connection between the Proxy Adapters and the Remote Adapters.
* Test the Adapter, launching one of the [compatible clients](https://github.com/Lightstreamer?utf8=%E2%9C%93&q=lightstreamer-example-stocklist-client&type=&language=).
    * To make the Stock-List Demo applications, use the newly installed Adapter Set, you need to modify the code to change the required Adapter Set name from DEMO to STOCKLISTDEMO_REMOTE. Depending on the client library in use, the code might vary (e.g., in JavaScript `new LightstreamerClient(hostToUse,"DEMO");` has to be replaced by `new LightstreamerClient(hostToUse, "STOCKLISTDEMO_REMOTE");`). (You don't need to reconfigure the Data Adapter name, as it is the same in both Adapter Sets).
    * In case the JavaScript client is used, you might need to disable the connection sharing to avoid adapter sets conflicts (e.g., by removing or modifiyng `sharingClient.connectionSharing.enableSharing("DemoCommonConnection","ls/","SHARE_SESSION", true);`)
You can now launch the demo that will be fed by the remote adapter.

#### Available improvements

##### Add Encryption

Each TCP connection from a Proxy Adapter can be encrypted via TLS. To have the Remote Server accept only TLS connections, a suitable keystore with a valid certificate should be provided to the Remote Server.
The sample code in the `ServerStarter` class supports this case by leaning on the JDK implementation, hence keystore information can be provided on the command line through the JSSE system properties `javax.net.ssl.keyStore` and `javax.net.ssl.keyStorePassword`.
The sample Remote Server provided in the `Deployment_Java_Server` directory in `deploy_inverted.zip` is already predisposed for TLS connection on all ports. You can rerun the demo with the new configuration by launching the Java Remote Server with a command like this:
```sh
    java -cp "./example-StockList-adapter-java-remote-0.1.0-SNAPSHOT.jar;./dependency/*" ^
        -Djavax.net.ssl.keyStore=<path-to-keystore> -Djavax.net.ssl.keyStorePassword=<keystore-password> ^
        com.lightstreamer.example_StockList_adapter_java_remote.server.ServerMain ^
        -tls -metadata_rrport 6663 -data_rrport 6661
```

A corresponding configuration is needed on Lightstreamer Server in adapters.xml. Just add
```xml
    <param name="tls">Y</param>
```
in both the <data_provider> and <metadata_provider> block and ensure that the name in the `remote_host` parameter corresponds to the name on the certificate.

NOTE: For your experiments, you can specify to the Remote Server to use the same JKS keystore "myserver.keystore" provided out of the box in the Lightstreamer distribution.
Since this keystore contains an invalid certificate, the Proxy Adapter should be configured to "trust" it and to omit certificate hostname verification.
This can be done by adding suitable parameters in adapters.xml in the <data_provider> and <metadata_provider> block, like this:
```xml
    <param name="tls.truststore.type">JKS</param>
    <param name="tls.truststore.truststore_file">path-to-myserver.keystore</param>
    <param name="tls.truststore.truststore_password.type">file</param>
    <param name="tls.truststore.truststore_password">path-to-myserver.keypass</param>
    <param name="tls.skip_hostname_check">Y</param>
```
See the configuration details in the [provided template](https://lightstreamer.com/docs/ls-server/latest/remote_adapter_robust_conf_template/adapters.xml).

##### Add Authentication

The Proxy Adapter can authenticate the Remote Server through user/password credentials in the same way shown for the normal connection policy.
However, with the connection inversion policy, the TLS certificate check performed by the Proxy Adapter upon connection can also be used to authenticate the Remote Server.
On the other hand, to allow the Remote Server authenticate a Proxy Adapter which connects to it, no user/password credential check is available.
The only option is for the Remote Server to require that the Proxy Adapter issues a trusted client-side TLS certificate. The needed certificate can be configured on the Proxy Adapter by leveraging the `tls.keystore.*` settings.

Actually, when the inversion policy is leveraged, it is assumed a scenario in which LS Server and the Remote Server both stay inside the back-end.
If LS Server stays in a DMZ (not to mention the outer Internet), which implies allowing on the back-end incoming connections from the DMZ, the inversion policy is not recommended;
rather, we recommend the normal connection policy, where only outgoing connections from the back-end to the DMZ have to be enabled.

## Build

To build your own version of this demo, instead of using the one provided in the deploy.zip file from the Install section above, you have two options:
either use [Maven](https://maven.apache.org/) (or other build tools) to take care of dependencies and building (recommended) or gather the necessary jars yourself and build it manually.
For the sake of simplicity only the Maven case is detailed here.

### Maven

You can easily build and run this application using Maven through the pom.xml file located in the root folder of this project. As an alternative, you can use an alternative build tool (e.g. Gradle, Ivy, etc.) by converting the provided pom.xml file.

Assuming Maven is installed and available in your path you can build the demo by running
```sh
 mvn package
```

You can also run the application with the following command
```sh
 mvn exec:java -Dexec.args="-host localhost -metadata_rrport 6663 -data_rrport 6661"
```
(or
```sh
 mvn exec:java -Dexec.args="-metadata_rrport 6663 -data_rrport 6661"
```
for the connection inversion option).

## See Also
* [Adapter Remoting Infrastructure Network Protocol Specification](https://lightstreamer.com/api/ls-generic-adapter/latest/ARI%20Protocol.pdf)

### Related Projects
* [Lightstreamer Java Remote Adapter SDK](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-remote)
* [LiteralBasedProvider Metadata Adapter](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-remote#literalbasedprovider-metadata-adapter)
* [Lightstreamer - Stock-List Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java)

## Lightstreamer Compatibility Notes

* Compatible with Lightstreamer SDK for Java Remote Adapters version 1.7 or newer and Lightstreamer Server version 7.4 or newer.
- For a version of this example compatible with Lightstreamer Server version since 7.0, please refer to [this tag](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java-remote/tree/for_Lightstreamer_7.3).
- For a version of this example compatible with SDK for Java Remote Adapters 1.4 to 1.6, please refer to [this tag](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java-remote/tree/for_Lightstreamer_7.3).
- For a version of this example compatible with SDK for Java Remote Adapters 1.3 please refer to [this tag](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java-remote/tree/for_release_1.3).
- For a version of this example compatible with SDK for Java Remote Adapters 1.1 to 1.2 please refer to [this tag](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java-remote/tree/for_Lightstreamer_7.0).
