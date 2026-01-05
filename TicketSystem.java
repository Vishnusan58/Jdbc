import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

// Core system
public class TicketSystem {

    public ArrayList<User> users = new ArrayList<>();
    public ArrayList<Ticket> tickets = new ArrayList<>();
    public ArrayList<ChangeRequest> changes = new ArrayList<>();
    public ArrayList<ChangeRequest> changeRequests = new ArrayList<>();
    public final Database database;

    Scanner sc = new Scanner(System.in);

    public TicketSystem() {

        database = new Database();
        loadFromDatabase();
    }

    private void loadFromDatabase() {
        try {
            this.users = database.loadUsers();
            this.tickets = database.loadTickets();
            this.changeRequests = database.loadChangeRequests();
            database.syncUserTickets(this.users, this.tickets);
        } catch (Exception e) {
            System.out.println("Error while initializing system data: " + e.getMessage());
        }
    }

    // Auto-assign ticket to agent with LEAST number of active tickets
    public void assignTicket(Ticket t) {
        try {
            String bestAgent = null;
            int minTickets = Integer.MAX_VALUE;

            for (User u : users) {
                if ("agent".equals(u.role)) {
                    int count = 0;

                    for (Ticket ticket : tickets) {
                        if (ticket.assignedTo != null
                                && ticket.assignedTo.equals(u.id)
                                && ticket.status != null
                                && !ticket.status.equals("resolved")
                                && !ticket.status.equals("closed")) {
                            count++;
                        }
                    }

                    if (count < minTickets) {
                        minTickets = count;
                        bestAgent = u.id;
                    }
                }
            }

            if (bestAgent != null) {
                t.assignedTo = bestAgent;
                System.out.println("Auto-assigned to agent: " + bestAgent);
                t.addHistory("System", "ASSIGNED", "Auto-assigned to " + bestAgent);
            } else {
                System.out.println("No agents available.");
            }

        } catch (Exception e) {
            System.out.println("Error while assigning ticket: " + e.getMessage());
        }
    }

    public void start() {
        while (true) {
            try {
                System.out.println("\n===== IT TICKET SYSTEM =====");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print("Choice: ");

                int ch = sc.nextInt();
                sc.nextLine(); // clear buffer

                if (ch == 1)
                    login();
                else if (ch == 2)
                    break;
                else
                    System.out.println("Invalid choice!");

            } catch (InputMismatchException e) {
                System.out.println("Please enter a number only!");
                sc.nextLine(); // clear wrong input
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    // LOGIN WITH PASSWORD
    private void login() {
        try {
            System.out.print("User ID: ");
            String id = sc.nextLine();

            System.out.print("Password: ");
            String pass = sc.nextLine();

            for (User u : users) {
                if (u.id.equals(id) && u.password.equals(pass)) {
                    System.out.println("Welcome " + u.name);

                    if (u.role.equals("user"))
                        new UserMenu(this, u).menu();
                    else if (u.role.equals("agent"))
                        new AgentMenu(this, u).menu();
                    else
                        new AdminMenu(this).menu();
                    return;
                }
            }

            System.out.println("Invalid credentials!");

        } catch (Exception e) {
            System.out.println("Login failed due to error: " + e.getMessage());
        }
    }
}

