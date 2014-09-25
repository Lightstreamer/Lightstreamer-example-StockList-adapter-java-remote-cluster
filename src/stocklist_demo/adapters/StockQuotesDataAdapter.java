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

package stocklist_demo.adapters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import stocklist_demo.feed_simulator.ExternalFeedListener;
import stocklist_demo.feed_simulator.ExternalFeedSimulator;
import stocklist_demo.server.OutPrintLog;

import com.lightstreamer.adapters.remote.DataProvider;
import com.lightstreamer.adapters.remote.ItemEventListener;
import com.lightstreamer.adapters.remote.SubscriptionException;
import com.lightstreamer.adapters.remote.log.Logger;


/**
 * This Data Adapter accepts a limited set of item names (the names starting
 * with "item") and listens to a (simulated) stock quotes feed, waiting for
 * update events. The events pertaining to the currently subscribed items
 * are then forwarded to Lightstreamer.
 * This example demonstrates how a Data Adapter could interoperate with
 * a broadcast feed (which always sends data for all available items)
 * by selecting the updates to be sent. Many other types of feeds may exist,
 * with very different behaviors.
 */
public class StockQuotesDataAdapter implements DataProvider {

    private volatile ItemEventListener listener;
    private final HashMap<String,Boolean> subscribedItems = new HashMap<String,Boolean>();
    private final ExternalFeedSimulator myFeed;
    
    private Logger logger;
    
    /**
     * Currently unused:
     * A static map, to be used by the Metadata Adapter to find the data
     * adapter instance; this allows the Metadata Adapter to forward client
     * messages to the adapter.
     * The map allows multiple instances of this Data Adapter to be included
     * in different Adapter Sets. Each instance is identified with the name
     * of the related Adapter Set; defining multiple instances in the same
     * Adapter Set is not allowed.
     */
    public static final ConcurrentHashMap<String, StockQuotesDataAdapter> feedMap =
        new ConcurrentHashMap<String, StockQuotesDataAdapter>();

    public StockQuotesDataAdapter() {
        myFeed = new ExternalFeedSimulator();
    }

    /**
     * Starts the simulator feed (or connects to the external
     * feed, for a real feed).
     */
    @Override
    public void init(Map<String, String> params, String configFile) {
        
        logger = OutPrintLog.getInstance().getLogger("LS_demos_Logger.StockQuotes");
        
        //The feedMap of this adapter is never used
        // Read the Adapter Set name, which is supplied by the Server as a parameter
        String adapterSetId = (String) params.get("adapters_conf.id");
        // Put a reference to this instance on a static map
        // to be read by the Metadata Adapter
        feedMap.put(adapterSetId, this);

        myFeed.start();
        logger.info("StockQuotesDataAdapter ready.");
    }

    @Override
    public void setListener(ItemEventListener listener) {
        this.listener = listener;
        myFeed.setFeedListener(new MyFeedListener());
    }

    @Override
    public void subscribe(String itemName) throws SubscriptionException {
        logger.info("Subscribing to " + itemName);
       
        if (itemName.startsWith("item")) {
            synchronized (subscribedItems) {
                subscribedItems.put(itemName, new Boolean(false));
            }
            // now we ask the feed for the snapshot; our feed will insert
            // an event with snapshot information into the normal updates flow
            myFeed.sendCurrentValues(itemName);
        } else {
            logger.error("Cannot subscribe to " + itemName + " - only names starting with \"item\" are supported");
            throw new SubscriptionException("Unexpected item: " + itemName);
        }
    }

    @Override
    public void unsubscribe(String itemName) {
        logger.info("Unsubscribing from " + itemName);
        synchronized (subscribedItems) {
            subscribedItems.remove(itemName);
        }
    }

    @Override
    public boolean isSnapshotAvailable(String itemName) {
        return true;
    }

    public void clearStatus() {
        synchronized (subscribedItems) {
            Set<String> keys = subscribedItems.keySet();
            for (String itemName : keys) {
                listener.clearSnapshot(itemName);
            }
        }
    }
    
    private class MyFeedListener implements ExternalFeedListener {

        /**
         * Called by our feed for each update event occurrence on some stock.
         * If isSnapshot is true, then the event contains a full snapshot
         * with the current values of all fields for the stock.
         */
        
        public void onEvent(String itemName, final HashMap<String,String> currentValues,
                            boolean isSnapshot) {
            synchronized (subscribedItems) {
                if (!subscribedItems.containsKey(itemName)) {
                    return;
                }
                Boolean started = (Boolean) subscribedItems.get(itemName);
                boolean snapshotReceived = started.booleanValue();
                if (!snapshotReceived) {
                    if (!isSnapshot) {
                        // we ignore the update and keep waiting until
                        // a full snapshot for the item has been received
                        return;
                    }
                    subscribedItems.put(itemName, new Boolean(true));
                } else {
                    if (isSnapshot) {
                        // it's not the first event we have received carrying
                        // snapshot information for the item; so, this event
                        // is not a snapshot from Lightstreamer point of view
                        isSnapshot = false;
                    }
                }

               
                listener.update(itemName,currentValues,isSnapshot);
                
                            
            }

        }

    }
    
}
