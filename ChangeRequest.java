// Represents an asset change request
import java.util.Date;
public class ChangeRequest {

    public int id;
    public String assetType;      // laptop / mouse / keyboard
    public String changeType;     // raise / remove / renew
    public String requestedBy;
    public String status;         // pending / approved / sent-to-agent / checked
    public String assignedAgent;

    public Date createdDate;
    public Date expiryDate;
    public int renewalCount;

    public ChangeRequest(int id, String assetType, String changeType, String user) {
        this.id = id;
        this.assetType = assetType;
        this.changeType = changeType;
        this.requestedBy = user;
        this.status = "pending";
        this.createdDate = new Date();
        this.renewalCount = 0;

        // Default expiry 1 year from now
        long year = 365L * 24 * 60 * 60 * 1000;
        this.expiryDate = new Date(this.createdDate.getTime() + year);
    }
}
