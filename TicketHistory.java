
import java.util.Date;

// Logs history of actions
public class TicketHistory {
    public Date timestamp;
    public String actor;
    public String action;
    public String details;

    public TicketHistory(String actor, String action, String details) {
        this.timestamp = new Date();
        this.actor = actor;
        this.action = action;
        this.details = details;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + actor + ": " + action + " (" + details + ")";
    }
}
