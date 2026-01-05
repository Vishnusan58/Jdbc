import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Minimal JDBC helper that persists users, tickets and change requests
 * into a lightweight SQLite database instead of in-memory collections.
 */
public class Database {
    private static final String DB_URL = "jdbc:sqlite:tickets.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "SQLite JDBC driver not found. Add sqlite-jdbc (e.g., sqlite-jdbc-3.46.1.3.jar) to the classpath.",
                    e);
        }
    }

    public Database() {
        initialize();
    }

    private void initialize() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (id TEXT PRIMARY KEY, password TEXT, name TEXT, role TEXT)");
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS tickets (" +
                    "id INTEGER PRIMARY KEY, title TEXT, description TEXT, category TEXT, sub_category TEXT, " +
                    "status TEXT, created_by TEXT, assigned_to TEXT, escalated INTEGER, rating INTEGER, " +
                    "created_date INTEGER, resolved_date INTEGER"
                + ")"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS notes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, ticket_id INTEGER, note TEXT"
                + ")"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS change_requests (" +
                    "id INTEGER PRIMARY KEY, asset TEXT, change_type TEXT, requested_by TEXT, status TEXT, " +
                    "assigned_agent TEXT, created_date INTEGER, expiry_date INTEGER, renewal_count INTEGER"
                + ")"
            );

            seedUsers(conn);
            seedTickets(conn);
            seedChangeRequests(conn);
        } catch (SQLException e) {
            System.out.println("Failed to initialize database: " + e.getMessage());
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void seedUsers(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) == 0) {
                insertUser(conn, new User("U1", "123", "Manu", "user"));
                insertUser(conn, new User("U2", "123", "Karthi", "user"));
                insertUser(conn, new User("A1", "123", "Vishnu", "agent"));
                insertUser(conn, new User("A2", "123", "Priya", "agent"));
                insertUser(conn, new User("AD1", "123", "Rishi", "admin"));
            }
        }
    }

    private void seedTickets(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tickets")) {
            if (rs.next() && rs.getInt(1) == 0) {
                insertTicket(conn, new Ticket("Email Issue", "Email not working", "IT", "Software", "U1"), "A1", "in-progress", 0, false, null);
                insertTicket(conn, new Ticket("Network Slow", "Internet is very slow", "IT", "Network", "U2"), "A2", "open", 0, false, null);
                insertTicket(conn, new Ticket("Mouse Broken", "Left click not working", "Hardware", "Peripherals", "U1"), "A1", "resolved", 4, false, new Date());
                insertTicket(conn, new Ticket("Payroll Error", "Salary mismatch", "HR", "Payroll", "U2"), "A2", "open", 0, false, null);
                insertTicket(conn, new Ticket("VPN Access", "Need VPN access", "IT", "Network", "U1"), "A1", "waiting", 0, false, null);
            }
        }
    }

    private void seedChangeRequests(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM change_requests")) {
            if (rs.next() && rs.getInt(1) == 0) {
                insertChangeRequest(conn, new ChangeRequest(101, "laptop", "renew", "U1"));
            }
        }
    }

    private void insertUser(Connection conn, User user) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO users(id, password, name, role) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, user.id);
            ps.setString(2, user.password);
            ps.setString(3, user.name);
            ps.setString(4, user.role);
            ps.executeUpdate();
        }
    }

    private void insertTicket(Connection conn, Ticket ticket, String assignedTo, String status, int rating, boolean escalated, Date resolved) throws SQLException {
        ticket.assignedTo = assignedTo;
        ticket.status = status;
        ticket.rating = rating;
        ticket.escalated = escalated;
        ticket.resolvedDate = resolved;
        insertTicket(ticket);
    }

    public ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT id, password, name, role FROM users")) {
            while (rs.next()) {
                users.add(new User(rs.getString("id"), rs.getString("password"), rs.getString("name"), rs.getString("role")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to load users: " + e.getMessage());
        }
        return users;
    }

    public ArrayList<Ticket> loadTickets() {
        ArrayList<Ticket> tickets = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM tickets")) {
            while (rs.next()) {
                Ticket ticket = new Ticket(
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("sub_category"),
                    rs.getString("created_by")
                );
                ticket.id = rs.getInt("id");
                ticket.status = rs.getString("status");
                ticket.assignedTo = rs.getString("assigned_to");
                ticket.escalated = rs.getInt("escalated") == 1;
                ticket.rating = rs.getInt("rating");
                long created = rs.getLong("created_date");
                ticket.createdDate = created > 0 ? new Date(created) : new Date();
                long resolved = rs.getLong("resolved_date");
                ticket.resolvedDate = resolved > 0 ? new Date(resolved) : null;
                ticket.notes = loadNotesForTicket(conn, ticket.id);
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.out.println("Failed to load tickets: " + e.getMessage());
        }
        return tickets;
    }

    public ArrayList<ChangeRequest> loadChangeRequests() {
        ArrayList<ChangeRequest> requests = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM change_requests")) {
            while (rs.next()) {
                ChangeRequest cr = new ChangeRequest(
                    rs.getInt("id"),
                    rs.getString("asset"),
                    rs.getString("change_type"),
                    rs.getString("requested_by")
                );
                cr.status = rs.getString("status");
                cr.assignedAgent = rs.getString("assigned_agent");
                long created = rs.getLong("created_date");
                cr.createdDate = created > 0 ? new Date(created) : new Date();
                cr.expiryDate = new Date(rs.getLong("expiry_date"));
                cr.renewalCount = rs.getInt("renewal_count");
                requests.add(cr);
            }
        } catch (SQLException e) {
            System.out.println("Failed to load change requests: " + e.getMessage());
        }
        return requests;
    }

    public void insertTicket(Ticket ticket) {
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tickets(id, title, description, category, sub_category, status, created_by, assigned_to, escalated, rating, created_date, resolved_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, ticket.id);
            ps.setString(2, ticket.title);
            ps.setString(3, ticket.description);
            ps.setString(4, ticket.category);
            ps.setString(5, ticket.subCategory);
            ps.setString(6, ticket.status);
            ps.setString(7, ticket.createdBy);
            ps.setString(8, ticket.assignedTo);
            ps.setInt(9, ticket.escalated ? 1 : 0);
            ps.setInt(10, ticket.rating);
            ps.setLong(11, ticket.createdDate != null ? ticket.createdDate.getTime() : 0L);
            ps.setLong(12, ticket.resolvedDate != null ? ticket.resolvedDate.getTime() : 0L);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to insert ticket: " + e.getMessage());
        }
    }

    public void updateTicket(Ticket ticket) {
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(
                "UPDATE tickets SET title=?, description=?, category=?, sub_category=?, status=?, assigned_to=?, escalated=?, rating=?, created_date=?, resolved_date=? WHERE id=?")) {
            ps.setString(1, ticket.title);
            ps.setString(2, ticket.description);
            ps.setString(3, ticket.category);
            ps.setString(4, ticket.subCategory);
            ps.setString(5, ticket.status);
            ps.setString(6, ticket.assignedTo);
            ps.setInt(7, ticket.escalated ? 1 : 0);
            ps.setInt(8, ticket.rating);
            ps.setLong(9, ticket.createdDate != null ? ticket.createdDate.getTime() : 0L);
            ps.setLong(10, ticket.resolvedDate != null ? ticket.resolvedDate.getTime() : 0L);
            ps.setInt(11, ticket.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update ticket: " + e.getMessage());
        }
    }

    public void addNote(int ticketId, String note) {
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO notes(ticket_id, note) VALUES (?, ?)")) {
            ps.setInt(1, ticketId);
            ps.setString(2, note);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to insert note: " + e.getMessage());
        }
    }

    private ArrayList<String> loadNotesForTicket(Connection conn, int ticketId) throws SQLException {
        ArrayList<String> notes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT note FROM notes WHERE ticket_id=?")) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notes.add(rs.getString("note"));
                }
            }
        }
        return notes;
    }

    public void insertChangeRequest(ChangeRequest cr) {
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO change_requests(id, asset, change_type, requested_by, status, assigned_agent, created_date, expiry_date, renewal_count) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, cr.id);
            ps.setString(2, cr.assetType);
            ps.setString(3, cr.changeType);
            ps.setString(4, cr.requestedBy);
            ps.setString(5, cr.status);
            ps.setString(6, cr.assignedAgent);
            ps.setLong(7, cr.createdDate != null ? cr.createdDate.getTime() : 0L);
            ps.setLong(8, cr.expiryDate != null ? cr.expiryDate.getTime() : 0L);
            ps.setInt(9, cr.renewalCount);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to insert change request: " + e.getMessage());
        }
    }

    private void insertChangeRequest(Connection conn, ChangeRequest cr) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO change_requests(id, asset, change_type, requested_by, status, assigned_agent, created_date, expiry_date, renewal_count) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, cr.id);
            ps.setString(2, cr.assetType);
            ps.setString(3, cr.changeType);
            ps.setString(4, cr.requestedBy);
            ps.setString(5, cr.status);
            ps.setString(6, cr.assignedAgent);
            ps.setLong(7, cr.createdDate != null ? cr.createdDate.getTime() : 0L);
            ps.setLong(8, cr.expiryDate != null ? cr.expiryDate.getTime() : 0L);
            ps.setInt(9, cr.renewalCount);
            ps.executeUpdate();
        }
    }

    public void updateChangeRequest(ChangeRequest cr) {
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(
                "UPDATE change_requests SET asset=?, change_type=?, requested_by=?, status=?, assigned_agent=?, created_date=?, expiry_date=?, renewal_count=? WHERE id=?")) {
            ps.setString(1, cr.assetType);
            ps.setString(2, cr.changeType);
            ps.setString(3, cr.requestedBy);
            ps.setString(4, cr.status);
            ps.setString(5, cr.assignedAgent);
            ps.setLong(6, cr.createdDate != null ? cr.createdDate.getTime() : 0L);
            ps.setLong(7, cr.expiryDate != null ? cr.expiryDate.getTime() : 0L);
            ps.setInt(8, cr.renewalCount);
            ps.setInt(9, cr.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update change request: " + e.getMessage());
        }
    }

    public void syncUserTickets(ArrayList<User> users, ArrayList<Ticket> tickets) {
        Map<String, User> userMap = new HashMap<>();
        for (User user : users) {
            user.myTickets.clear();
            userMap.put(user.id, user);
        }
        for (Ticket ticket : tickets) {
            User owner = userMap.get(ticket.createdBy);
            if (owner != null) {
                owner.myTickets.add(ticket);
            }
        }
    }
}
