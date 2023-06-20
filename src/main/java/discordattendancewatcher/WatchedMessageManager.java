package discordattendancewatcher;

import java.util.Map;
import java.text.DateFormat;
import java.util.HashMap;

public class WatchedMessageManager {

    private Map<Long, WatchedMessage> watchedMessages;
    
    public WatchedMessageManager() {
        watchedMessages = new HashMap<>();
    }
    
    public void watchMessage(long msgId, WatchedMessage ws) {
        watchedMessages.put(msgId, ws);
    }
    
    public boolean isWatchingMessage(long msgId) {
        return watchedMessages.containsKey(msgId);
    }
    
    public WatchedMessage getWatchedMessage(long msgId) {
        return watchedMessages.get(msgId);
    }
    
    public void stopWatchingMessage(long msgId) {
        watchedMessages.remove(msgId);
    }
}
