/*
*
* Copyright 2014 Weswit s.r.l.
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

package stocklist_demo.server;

import java.util.HashMap;
import java.util.Map;

import stocklist_demo.adapters.StockQuotesDataAdapter;

import com.lightstreamer.adapters.remote.DataProviderServer;
import com.lightstreamer.adapters.remote.LiteralBasedProvider;
import com.lightstreamer.adapters.remote.MetadataProviderServer;
import com.lightstreamer.adapters.remote.Server;
import com.lightstreamer.adapters.remote.log.Logger;

public class ServerMain {
    private static Logger _log = OutPrintLog.getInstance().getLogger("LS_demos_Logger.StockQuotes.Server");

    public static final String PREFIX1 = "-";
    public static final String PREFIX2 = "/";

    public static final char SEP = '=';

    public static final String ARG_HELP_LONG = "help";
    public static final String ARG_HELP_SHORT = "?";

    public static final String ARG_HOST = "host";
    public static final String ARG_METADATA_RR_PORT = "metadata_rrport";
    public static final String ARG_DATA_RR_PORT = "data_rrport";
    public static final String ARG_DATA_NOTIF_PORT = "data_notifport";
    public static final String ARG_NAME = "name";

    public static void main(String[] args) {
        if (args.length == 0) {
            help();
        }

        _log.info("Lightstreamer StockListDemo Adapter Standalone Server starting...");

        Server.setLoggerProvider(OutPrintLog.getInstance());

        Map<String,String> parameters = new HashMap<String,String>();
        String host = null;
        int rrPortMD = -1;
        int rrPortD = -1;
        int notifPortD = -1;
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

                    _log.debug("Found argument: '" + ARG_HOST + "' with value: '" + host + "'");
                } else if (arg.equals(ARG_METADATA_RR_PORT)) {
                    i++;
                    rrPortMD = Integer.parseInt(args[i]);

                    _log.debug("Found argument: '" + ARG_METADATA_RR_PORT + "' with value: '" + rrPortMD + "'");
                } else if (arg.equals(ARG_DATA_RR_PORT)) {
                    i++;
                    rrPortD = Integer.parseInt(args[i]);

                    _log.debug("Found argument: '" + ARG_DATA_RR_PORT + "' with value: '" + rrPortD + "'");
                } else if (arg.equals(ARG_DATA_NOTIF_PORT)) {
                    i++;
                    notifPortD = Integer.parseInt(args[i]);

                    _log.debug("Found argument: '" + ARG_DATA_NOTIF_PORT + "' with value: '" + notifPortD + "'");
                } else if (arg.equals(ARG_NAME)) {
                    i++;
                    name = args[i];

                    _log.debug("Found argument: '" + ARG_NAME + "' with value: '" + name + "'");
                }

            } else {
                int sep = arg.indexOf(SEP);
                if (sep < 1) {
                    _log.warn("Skipping unrecognizable argument: '" + arg + "'");

                } else {
                    String par = arg.substring(0, sep).trim();
                    String val = arg.substring(sep + 1).trim();
                    parameters.put(par, val);

                    _log.debug("Found parameter: '" + par + "' with value: '" + val + "'");
                }
            }
        }

        {
            MetadataProviderServer server = new MetadataProviderServer();
            server.setAdapter(new LiteralBasedProvider());
            server.setAdapterParams(parameters);
            // server.setAdapterConfig not needed by LiteralBasedProvider
            if (name != null) {
                server.setName(name);
            }
            _log.debug("Remote Metadata Adapter initialized");
   
            ServerStarter starter = new ServerStarter(host, rrPortMD, -1);
            starter.launch(server);
        }
        {
            DataProviderServer server = new DataProviderServer();
            server.setAdapter(new StockQuotesDataAdapter());
            // server.AdapterParams not needed by StockListDemoAdapter
            // server.AdapterConfig not needed by StockListDemoAdapter
            if (name != null) {
                server.setName(name);
            }
            _log.debug("Remote Data Adapter initialized");
   
            ServerStarter starter = new ServerStarter(host, rrPortD, notifPortD);
            starter.launch(server);
        }

        _log.info("Lightstreamer StockListDemo Adapter Standalone Server running");
    }

    private static void help() {
        _log.fatal("Lightstreamer StockListDemo Adapter Standalone Server Help");
        _log.fatal("Usage: ");
        _log.fatal("                     [-name <name>] -host <address>");
        _log.fatal("                     -metadata_rrport <port> -data_rrport <port> -data_notifport <port>");
        _log.fatal("                     [\"<param1>=<value1>\" ... \"<paramN>=<valueN>\"]");
        _log.fatal("Where: <name>        is the symbolic name for both the adapters (1)");
        _log.fatal("       <address>     is the host name or ip address of LS server (2)");
        _log.fatal("       <port>        is the tcp port number where LS proxy is listening on (3)");
        _log.fatal("       <paramN>      is the Nth metadata adapter parameter name (4)");
        _log.fatal("       <valueN>      is the value of the Nth metadata adapter parameter (4)");
        _log.fatal("Notes: (1) The adapter name is optional, if it is not given the adapter will be");
        _log.fatal("           assigned a progressive number name like \"#1\", \"#2\" and so on");
        _log.fatal("       (2) The communication will be from here to LS, not viceversa");
        _log.fatal("       (3) The notification port is necessary for a Data Adapter, while it is");
        _log.fatal("           not needed for a Metadata Adapter");
        _log.fatal("       (4) The parameters name/value pairs will be passed to the LiteralBasedProvider");
        _log.fatal("           Metadata Adapter as a Map in the \"parameters\" Init() argument");
        _log.fatal("           The StockListDemo Data Adapter requires no parameters");
        _log.fatal("Aborting...");

        System.exit(9);
    }
}