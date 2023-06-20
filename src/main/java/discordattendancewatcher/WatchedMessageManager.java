package discordattendancewatcher;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


import java.util.concurrent.ConcurrentHashMap;

public class WatchedMessageManager {

    private Map<Long, WatchedMessage> watchedMessages;
    private ScheduledExecutorService ses;
    
    
    public WatchedMessageManager() {
        watchedMessages = new ConcurrentHashMap<>();
        ses = Executors.newScheduledThreadPool(1);
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
        WatchedMessage msg = getWatchedMessage(msgId);
        msg.getChannel().deleteMessageById(msgId).queue();
        watchedMessages.remove(msgId);
    }
    
    public ScheduledExecutorService getScheduledExecutorService() {
        return ses;
    }
}
