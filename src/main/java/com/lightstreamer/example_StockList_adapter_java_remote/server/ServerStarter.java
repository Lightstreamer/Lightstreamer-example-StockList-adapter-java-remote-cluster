/*
*
* Copyright (c) Lightstreamer Srl
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package com.lightstreamer.example_StockList_adapter_java_remote.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.lightstreamer.adapters.remote.DataProviderException;
import com.lightstreamer.adapters.remote.ExceptionHandler;
import com.lightstreamer.adapters.remote.MetadataProviderException;
import com.lightstreamer.adapters.remote.RemotingException;
import com.lightstreamer.adapters.remote.Server;
import static com.lightstreamer.log.LogManager.getLogger;

public class ServerStarter implements Runnable {

	private static class ServerExceptionHandler implements ExceptionHandler {

		private boolean closed = false;
		private Server server;

		ServerExceptionHandler(Server server) {
			this.server = server;
		}

		@Override
		public boolean handleException(RemotingException exception) {
			synchronized (this) {
				if (!closed) {
					closed = true;
					log.error("Caught exception: " + exception.getMessage(), exception);
					server.close();
				}
			}
			return false;
		}

		@Override
		public boolean handleIOException(IOException exception) {
			synchronized (this) {
				if (!closed) {
					closed = true;
					log.error("Connection to Lightstreamer Server closed", exception);
					server.close();
				}
			}
			return false;
		}
	}

	private static com.lightstreamer.log.Logger log = getLogger("LS_demos_Logger.StockQuotes.ServerStarter");

	private static Socket acceptProperSocket(boolean isTls, int port) throws IOException {
		log.info("Listening on port " + port + (isTls ? " with TLS" : "") + "...");
		try (ServerSocket serverSocket = createServerSocket(isTls, port)) {
			Socket s = serverSocket.accept();
			if (isTls) {
				((SSLSocket) s).startHandshake();
			}
			log.info("Connection on port " + port + " opened");
			return s;
		}
	}

	private static ServerSocket createServerSocket(boolean isTls, int port) throws IOException {
		ServerSocket serverSocket;
		if (isTls) {
			SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			// leans on the following java system properties to be configured:
			// - javax.net.ssl.keyStore
			// - javax.net.ssl.keyStorePassword
			serverSocket = factory.createServerSocket(port);
			if (false) {
				// possible further authentication challenge on the Proxy Adapter
				((SSLServerSocket) serverSocket).setNeedClientAuth(true);
			}
		} else {
			serverSocket = new ServerSocket(port);
		}
		return serverSocket;
	}

	private boolean isTls;

	private int rrPort;

	private Supplier<? extends Server> serverSupplier;

	public ServerStarter(boolean isTls, int rrPort) {
		this.isTls = isTls;
		this.rrPort = rrPort;
	}

	public final void launch(Supplier<? extends Server> serverSupplier) {
		this.serverSupplier = serverSupplier;
		Thread t = new Thread(this);
		t.start();
	}

	public final void run() {
		while (true) {
			log.info("Connecting...");
			Socket rrSocket = null;
			try {
				rrSocket = acceptProperSocket(isTls, rrPort);
				Server server = serverSupplier.get();
				server.setExceptionHandler(new ServerExceptionHandler(server));
				server.setRequestStream(rrSocket.getInputStream());
				server.setReplyStream(rrSocket.getOutputStream());

				log.info("Connected");

				try {
					server.start();
				} catch (DataProviderException | MetadataProviderException | RemotingException e) {
					log.fatal("Exception caught while starting the server: " + e.getMessage() + ", aborting...", e);
					server.close();
				}

			} catch (IOException e) {
				log.info("Connection failed: " + e);
				if (rrSocket != null) {
					try {
						rrSocket.close();
					} catch (IOException e1) {
					}
				}
			}

		}
	}
}
