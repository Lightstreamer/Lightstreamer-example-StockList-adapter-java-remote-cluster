# Lightstreamer - Stock-List Demo - Java Remote Adapter

The Stock-List demos simulate a market data feed and front-end for stock quotes. They show a list of stock symbols and updates prices and other fields displayed on the page in real-time.

This project contains the source code and all the resources needed to install a remote version of the Java Stock-List Demo Data Adapter.

As example of [Clients Using This Adapter](https://github.com/Lightstreamer?query=lightstreamer-example-stocklist-client), you may refer to the [Lightstreamer - Basic Stock-List Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-StockList-client-javascript#basic-stock-list-demo---html-client) and view the corresponding [Live Demo](http://demos.lightstreamer.com/StockListDemo_Basic).

## Details

This project includes the implementation of the remote versions of the DataProvider interface for the *Stock-List Demo*.
The Metadata Adapter functionalities are absolved by the `LiteralBasedProvider`, a simple Remote Metadata Adapter already included in the Remote Java Adapters SDK binaries, which is enough for all demo clients.
See also [Lightstreamer - Reusable Metadata Adapters - Remote Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-ReusableMetadata-adapter-java-remote).

* `StockQuotesDataAdapter.java` is a porting of the class with the same name from the [Lightstreamer - Stock-List Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java). 
It implements the *DataProvider* interface and calls back Lightstreamer through the *ItemEventListener* interface. Use it as a starting point to implement your custom data adapter.
It also implements the custom `ExternalFeedListener` interface to receive updates from the `ExternalFeedSimulator` instance (see next point)
* `ExternalFeedSimulator.java` is the same exact class from the [Lightstreamer - Stock-List Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java). It randomly generates the
stock quotes used by the demo.
* `ServerMain.java` is the main of the remote server application. It instantiate a DataProviderServer and a MetadataProviderServer and launches them using the `ServerStarter` class.
* `OutPrintLog.java` is a simple implementation of the LoggerProvider interface. It provides the library with a logging layer that prints messages on the system out. The LoggerProvider interface can be easily 
implemented to wrap any third-party logging library like logj4 or logback

Check out the sources for further explanations.

![General Architecture](generalarchitecture.png)

## Install

If you want to install a version of this demo in your local Lightstreamer server, follow these steps:
* Download the [latest Lightstreamer distribution](http://www.lightstreamer.com/download/) (Lightstreamer Server comes with a free non-expiring demo license for 20 connected users) from [Lightstreamer Download page](http://www.lightstreamer.com/download.htm), and install it, as explained in the `GETTING_STARTED.TXT` file in the installation home directory.
* Get the `deploy.zip` file of the [latest release](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java-remote/releases) and unzip it.
    * Plug the Proxy Data Adapter and the Proxy MetaData Adapter into the Server: go to the `Deployment_LS` folder and copy the `RemoteStockList` directory and all of its files to the `adapters` folder of your Lightstreamer Server installation.
    * Alternatively, you may plug the **robust** versions of the Proxy Data Adapter and the Proxy MetaData Adapter: go to the `Deployment_LS(robust)` folder and copy the `RemoteStockList` directory and all of its files into `adapters`. This Adapter Set demonstrates the provided "robust" versions of the standard Proxy Data and Metadata Adapters. The robust Proxy Data Adapter can handle the case in which a Remote Data Adapter is missing or fails, by suspending the data flow and trying to connect to a new Remote Data Adapter instance. The robust Proxy Metadata Adapter can handle the case in which a Remote Metadata Adapter is missing or fails, by temporarily denying all client requests and trying to connect to a new Remote Data Adapter instance. See the comments embedded in the generic `adapters.xml` file template, `DOCS-SDKs/adapter_remoting_infrastructure/doc/adapter_robust_conf_template/adapters.xml`, for details. Note that this extended Adapter Set also requires that the client is able to manage the case of missing data. Currently, only the [Lightstreamer - Stock-List Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-StockList-client-javascript#stocklist-demo) and the [Lightstreamer - Framed Stock-List Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-StockList-client-javascript#framed-stocklist-demo) front-ends have such ability.
    * Open a command line to the `Deployment_Java_Server` folder and launch the Java Remote Server by issuing the following command
      `java -cp ./lib/ls-adapter-remote.jar;./lib/SLDRemoteAdapter.jar; stocklist_demo.server.ServerMain -host localhost -metadata_rrport 6663 -data_rrport 6661 -data_notifport 6662`
* Launch Lightstreamer Server. The Server startup will complete only after a successful connection between the Proxy Adapters and the Remote Adapters.
* Test the Adapter, launching one of the [compatible clients](https://github.com/Lightstreamer?query=lightstreamer-example-stocklist-client).
    * To make the Stock-List Demo applications, use the newly installed Adapter Set, you need to modify the code
to change the required Adapter Set name from DEMO to STOCKLISTDEMO_REMOTE. Depending on the client library in use, the code might vary (e.g., in JavaScript `new LightstreamerClient(hostToUse,"DEMO");` has to be replaced by `new LightstreamerClient(hostToUse, "STOCKLISTDEMO_REMOTE");`). 
(You don't need to reconfigure the Data Adapter name, as it is the same in both Adapter Sets).
    * In case the JavaScript client is used, you might need to disable the connection sharing to avoid adapter sets conflicts (e.g., by removing or modifiyng `sharingClient.connectionSharing.enableSharing("DemoCommonConnection","ls/","SHARE_SESSION", true);`)
You can now launch the demo that will be fed by the remote adapter.

Please note that the Remote Java Adapter Server connects to the Proxy Adapters, not vice versa.

## Build

Grab the ls-adapter-remote.jar from the Lightstreamer Remote Java Adapter Library and put in the lib folder.

Then, assuming javac and jar are available on the path, from the command line run
```sh
javac -classpath ./lib/ls-adapter-remote.jar -d ./classes ./src/stocklist_demo/adapters/*.java ./src/stocklist_demo/feed_simulator/*.java ./src/stocklist_demo/server/*.java
 
jar cvf java_sld_adapters.jar -C classes ./
```

## See Also
* [Adapter Remoting Infrastructure Network Protocol Specification](http://www.lightstreamer.com/docs/adapter_generic_base/ARI%20Protocol.pdf)

### Related Projects
* [Lightstreamer - Reusable Metadata Adapters - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-ReusableMetadata-adapter-java)
* [Lightstreamer - Stock-List Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java)

## Lightstreamer Compatibility Notes

- Compatible with Lightstreamer SDK for Java Remote Adapters since 1.1
