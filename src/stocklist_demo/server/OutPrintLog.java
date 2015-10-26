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

package stocklist_demo.server;

import com.lightstreamer.adapters.remote.log.Logger;
import com.lightstreamer.adapters.remote.log.LoggerProvider;


public class OutPrintLog implements LoggerProvider {
    
    public static OutPrintLog getInstance() {
        return new OutPrintLog();
    }

    @Override
    public Logger getLogger(final String category) {
        // TODO Auto-generated method stub
        return new Logger() {

            @Override
            public void error(String line) {
               System.out.println(category + "|ERROR|" + line);
            }

            @Override
            public void error(String line, Throwable exception) {
                System.out.println(category + "|ERROR|" + line);
                exception.printStackTrace();
            }

            @Override
            public void warn(String line) {
                System.out.println(category + "|WARN |" + line);
            }

            @Override
            public void warn(String line, Throwable exception) {
                System.out.println(category + "|WARN |" + line);
                exception.printStackTrace();
            }

            @Override
            public void info(String line) {
                System.out.println(category + "|INFO |" + line);
            }

            @Override
            public void info(String line, Throwable exception) {
                System.out.println(category + "|INFO |" + line);
                exception.printStackTrace();
            }

            @Override
            public void debug(String line) {
                System.out.println(category + "|DEBUG|" + line); 
            }

            @Override
            public void debug(String line, Throwable exception) {
                System.out.println(category + "|DEBUG|" + line);
                exception.printStackTrace();
            }

            @Override
            public void fatal(String line) {
                System.out.println(category + "|FATAL|" + line);
            }

            @Override
            public void fatal(String line, Throwable exception) {
                System.out.println(category + "|FATAL|" + line);
                exception.printStackTrace();
            }

            @Override
            public boolean getIsDebugEnabled() {
                return true;
            }

            @Override
            public boolean getIsInfoEnabled() {
                return true;
            }

            @Override
            public boolean getIsWarnEnabled() {
                return true;
            }

            @Override
            public boolean getIsErrorEnabled() {
                return true;
            }

            @Override
            public boolean getIsFatalEnabled() {
                return true;
            }
            
        };
    }
}
