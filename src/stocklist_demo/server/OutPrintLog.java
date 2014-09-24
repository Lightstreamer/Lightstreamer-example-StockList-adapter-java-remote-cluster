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
