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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.lightstreamer.adapters.remote.DataProviderServer;
import com.lightstreamer.adapters.remote.MetadataProviderServer;
import com.lightstreamer.adapters.remote.Server;
import com.lightstreamer.adapters.remote.metadata.LiteralBasedProvider;
import com.lightstreamer.example_StockList_adapter_java_remote.adapters.StockQuotesDataAdapter;
import com.lightstreamer.log.LogManager;
import com.lightstreamer.log.Logger;
import com.lightstreamer.log.system_out.SystemOutLogProvider;

public class ServerMain {
	private static Logger log = LogManager.getLogger("LS_demos_Logger.StockQuotes.Server");

	public static final String PREFIX1 = "-";
	public static final String PREFIX2 = "/";

	public static final char SEP = '=';

	public static final String ARG_HELP_LONG = "help";
	public static final String ARG_HELP_SHORT = "?";

	public static final String ARG_HOST = "host";
	public static final String ARG_TLS = "tls"; // will use lowercase
	public static final String ARG_METADATA_RR_PORT = "metadata_rrport";
	public static final String ARG_DATA_RR_PORT = "data_rrport";
	public static final String ARG_USER = "user";
	public static final String ARG_PASSWORD = "password";
	public static final String ARG_NAME = "name";

	public static void main(String[] args) {
		Server.setLoggerProvider(new SystemOutLogProvider());
		if (args.length == 0) {
			help();
		}

		log.info("Lightstreamer StockListDemo Adapter Standalone Server starting...");

		Map<String, String> parameters = new HashMap<String, String>();
		String host = null;
		boolean isTls = false;
		int rrPortMD = -1;
		int rrPortD = -1;
		String username = null;
		String password = null;
		String name = null;

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (arg.startsWith(PREFIX1) || arg.startsWith(PREFIX2)) {
				arg = arg.substring(1).toLowerCase();

				if (arg.equals(ARG_HELP_SHORT) || arg.equals(ARG_HELP_LONG)) {
					help();
				} else if (arg.equals(ARG_HOST)) {
					i++;
					host = args[i];

					log.debug("Found argument: '" + ARG_HOST + "' with value: '" + host + "'");
				} else if (arg.equals(ARG_TLS)) {
					isTls = true;

					log.debug("Found argument: '" + ARG_TLS + "'");
				} else if (arg.equals(ARG_METADATA_RR_PORT)) {
					i++;
					rrPortMD = Integer.parseInt(args[i]);

					log.debug("Found argument: '" + ARG_METADATA_RR_PORT + "' with value: '" + rrPortMD + "'");
				} else if (arg.equals(ARG_DATA_RR_PORT)) {
					i++;
					rrPortD = Integer.parseInt(args[i]);

					log.debug("Found argument: '" + ARG_DATA_RR_PORT + "' with value: '" + rrPortD + "'");
				} else if (arg.equals(ARG_USER)) {
					i++;
					username = args[i];

					log.debug("Found argument: '" + ARG_USER + "' with value: '" + username + "'");
				} else if (arg.equals(ARG_PASSWORD)) {
					i++;
					password = args[i];

					log.debug("Found argument: '" + ARG_PASSWORD + "' with value: '" + password + "'");
				} else if (arg.equals(ARG_NAME)) {
					i++;
					name = args[i];

					log.debug("Found argument: '" + ARG_NAME + "' with value: '" + name + "'");
				}

			} else {
				int sep = arg.indexOf(SEP);
				if (sep < 1) {
					log.warn("Skipping unrecognizable argument: '" + arg + "'");

				} else {
					String par = arg.substring(0, sep).trim();
					String val = arg.substring(sep + 1).trim();
					parameters.put(par, val);

					log.debug("Found parameter: '" + par + "' with value: '" + val + "'");
				}
			}
		}

		if ((username != null) != (password != null)) {
			log.error("Incomplete setting of /user and /password arguments");
			return;
		}

		{
			ServerStarter metadataServerStarter = new ServerStarter(isTls, rrPortMD);
			metadataServerStarter.launch(newMetadataProvider(parameters, username, password, name));
		}
		{
			ServerStarter starter = new ServerStarter(isTls, rrPortD);
			starter.launch(newDataProvider(username, password, name));
		}

		log.info("Lightstreamer StockListDemo Adapter Standalone Server running");
	}

	private static Supplier<? extends Server> newMetadataProvider(Map<String, String> parameters, String username,
			String password, String name) {
		return () -> {
			MetadataProviderServer server = new MetadataProviderServer();
			server.setAdapter(new LiteralBasedProvider());
			server.setAdapterParams(parameters);
			// server.setAdapterConfig not needed by LiteralBasedProvider
			if (name != null) {
				server.setName(name);
			}
			if (username != null) {
				server.setRemoteUser(username);
				server.setRemotePassword(password);
			}
			log.debug("Remote Metadata Adapter initialized");
			return server;
		};
	}

	private static Supplier<? extends Server> newDataProvider(String username, String password, String name) {
		return () -> {
			DataProviderServer server = new DataProviderServer();
			server.setAdapter(new StockQuotesDataAdapter());
			// server.AdapterParams not needed by StockListDemoAdapter
			// server.AdapterConfig not needed by StockListDemoAdapter
			if (name != null) {
				server.setName(name);
			}
			if (username != null) {
				server.setRemoteUser(username);
				server.setRemotePassword(password);
			}
			log.debug("Remote Data Adapter initialized");
			return server;
		};
	}

	private static void help() {
		log.fatal("Lightstreamer StockListDemo Adapter Standalone Server Help");
		log.fatal("Usage: ");
		log.fatal("                     [-name <name>]");
		log.fatal("                     [-host <address>] [-tls [noverify]]");
		log.fatal("                     -metadata_rrport <port> -data_rrport <port>");
		log.fatal("                     [-user <user> -password <password>]");
		log.fatal("                     [\"<param1>=<value1>\" ... \"<paramN>=<valueN>\"]");
		log.fatal("Where: <name>        is the symbolic name for both the adapters (1)");
		log.fatal("       <address>     is the host name or ip address of LS server (2)");
		log.fatal("       <port>        is the tcp port number where LS proxy is listening on (2)");
		log.fatal("       -tls          if indicated, initiates a TLS-encrypted communication (3)");
		log.fatal("       <username>    is sent, along with <password>, to the LS proxy (3)");
		log.fatal("       <paramN>      is the Nth metadata adapter parameter name (4)");
		log.fatal("       <valueN>      is the value of the Nth metadata adapter parameter (4)");
		log.fatal("Notes: (1) The adapter name is optional, if it is not given the adapter will be");
		log.fatal("           assigned a progressive number name like \"#1\", \"#2\" and so on");
		log.fatal("       (2) The connection will be from here to LS (normal case),");
		log.fatal("           or from LS to here (inversion case) if host omitted");
		log.fatal("       (3) TLS communication and user-password submission may or may not be needed");
		log.fatal("           depending on the LS Proxy Adapter configuration");
		log.fatal("       (4) The parameters name/value pairs will be passed to the LiteralBasedProvider");
		log.fatal("           Metadata Adapter as a Map in the \"parameters\" Init() argument");
		log.fatal("           The StockListDemo Data Adapter requires no parameters");
		log.fatal("Aborting...");

		System.exit(9);
	}
}