package discordattendancewatcher.raceEvent;

import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;

import java.util.concurrent.ConcurrentHashMap;

public class WatchedMessageManager implements Serializable {

    private static final long serialVersionUID = -5171307135508414035L;
    private Map<Long, WatchedMessage> watchedMessages;
    private transient ScheduledExecutorService ses;
    
    
    public WatchedMessageManager() {
        watchedMessages = new ConcurrentHashMap<>();
        ses = Executors.newScheduledThreadPool(1);
    }
    
    public void watchMessage(long msgId, WatchedMessage ws) {
        watchedMessages.put(msgId, ws);
        System.out.printf("Created new message and monitoring total of %d messages\n", watchedMessages.size());
        saveChanges();
    }
    
    public boolean isWatchingMessage(long msgId) {
        return watchedMessages.containsKey(msgId);
    }
    
    public WatchedMessage getWatchedMessage(long msgId) {
        return watchedMessages.get(msgId);
    }
    
    public void deleteMessage(long msgId) {
        // Message actually only gets deleted from manager, not discord
        WatchedMessage msg = getWatchedMessage(msgId);
        msg.getChannel().editMessageComponentsById(msgId, new ArrayList<LayoutComponent>()).queue(); // create empty list to remove buttons
        msg.getChannel().editMessageById(msgId, "The event is over.").queue();
        watchedMessages.remove(msgId);
        System.out.printf("Stopped watching message and monitoring total of %d messages\n", watchedMessages.size());
        saveChanges();
    }
    
    public void queueMessageDeletion(long timestamp, long msgId) {
        long currentTimestamp = System.currentTimeMillis() / 1000;
        Thread t1 = new Thread(() -> deleteMessage(msgId));
        System.out.printf("Scheduling deletion in %d seconds\n", timestamp - currentTimestamp);
        ses.schedule(t1, timestamp - currentTimestamp, TimeUnit.SECONDS);
    }
    
    private void rescheduleMessageDeletion() {
        for(long msgId : watchedMessages.keySet()) {
            WatchedMessage wm = watchedMessages.get(msgId);
            long timestamp = wm.getDate();
            queueMessageDeletion(timestamp, msgId);
        }
    }
    
    public void markAttendance(long msgId, User user) {
        watchedMessages.get(msgId).markAttendance(user);
        System.out.printf("Marked attendance for %s in message %d\n", user.getEffectiveName(), msgId);
        saveChanges();
    }
    
    public void markAbsence(long msgId, User user) {
        watchedMessages.get(msgId).markAbsence(user);
        System.out.printf("Marked absence for %s in message %d\n", user.getEffectiveName(), msgId);
        saveChanges();
    }
    
    private void saveChanges() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("currentWatched.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        ses = Executors.newScheduledThreadPool(1);
        rescheduleMessageDeletion();
    }
}
