import java.util.ArrayList;
import java.util.Date;

// Ticket class
public class Ticket {
    public int id;
    public String title;
    public String description;
    public String category;
    public String subCategory; // NEW
    public String status;
    public String createdBy;
    public String assignedTo;
    public boolean escalated;
    public int rating; // 1-5, 0 if not rated
    
    public Date createdDate;
    public Date resolvedDate;
    
    public ArrayList<TicketHistory> history;


    // CHANGE REQUEST FIELDS (NEW)
    public boolean changeRequest;          // raised or not
    public String changeType;              // raise / remove / renew
    public String changeStatus;             // pending / approved / rejected

    public ArrayList<String> notes;

    public Ticket(String title, String description, String category, String subCategory, String createdBy) {
        this.id = new java.util.Random().nextInt(9000) + 1000;
        this.title = title;
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
        this.createdBy = createdBy;
        this.status = "open";
        this.escalated = false;
        this.rating = 0;
        
        this.createdDate = new Date();
        
        this.changeRequest = false;
        this.changeStatus = "NA";
        
        this.notes = new ArrayList<>();
        this.history = new ArrayList<>();
        
        // Log creation
        this.history.add(new TicketHistory(createdBy, "CREATED", "Ticket created"));
    }

    public void addHistory(String actor, String action, String details) {
        this.history.add(new TicketHistory(actor, action, details));
    }

}
