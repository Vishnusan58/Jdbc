import java.util.InputMismatchException;
import java.util.Scanner;

// AGENT actions
public class AgentMenu {
    TicketSystem system;
    User agent;
    Scanner sc = new Scanner(System.in);

    public AgentMenu(TicketSystem system, User agent) {
        this.system = system;
        this.agent = agent;
    }

    public void menu() {
        while (true) {
            try {
                System.out.println("\n----- AGENT MENU -----");
                System.out.println("1. My Tickets");
                System.out.println("2. Add Note");
                System.out.println("3. Update Status");
                System.out.println("4. Reassign Ticket");
                System.out.println("5. Search Tickets");
                System.out.println("6. Logout");
                System.out.print("Choice: ");

                int ch = sc.nextInt();
                sc.nextLine(); // clear buffer

                switch (ch) {
                    case 1: viewTickets(); break;
                    case 2: addNote(); break;
                    case 3: updateStatus(); break;
                    case 4: reassign(); break;
                    case 5: search(); break;
                    case 6: return;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("Unexpected error occurred: " + e.getMessage());
            }
        }
    }

    // AGENT VIEWS ASSIGNED TICKETS
    private void viewTickets() {
        System.out.println("\n--- Assigned Tickets ---");
        System.out.println("ID | Title | Status | Tag");

        boolean found = false;
        for (Ticket t : system.tickets) {
            if (t.assignedTo != null && agent.id.equals(t.assignedTo)) {
                String tag = t.escalated ? "[ESCALATED]" : "";
                System.out.println(t.id + " | " + t.title + " | " + t.status + " " + tag);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No tickets assigned to you.");
        }
    }

    // AGENT UPDATES TICKET STATUS
    private void updateStatus() {
        try {
            System.out.print("Enter Ticket ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            for (Ticket t : system.tickets) {
                if (t.id == id && agent.id.equals(t.assignedTo)) {

                    String newStatus = "";
                    boolean validStatus = false;

                    while (!validStatus) {
                        try {
                            System.out.println("Select new status:");
                            System.out.println("1. in-progress");
                            System.out.println("2. waiting");
                            System.out.println("3. resolved");
                            System.out.print("Enter choice (1/2/3): ");

                            int choice = sc.nextInt();
                            sc.nextLine();

                            if (choice == 1) newStatus = "in-progress";
                            else if (choice == 2) newStatus = "waiting";
                            else if (choice == 3) newStatus = "resolved";
                            else {
                                System.out.println("Invalid choice!");
                                continue;
                            }

                            validStatus = true;

                        } catch (InputMismatchException e) {
                            System.out.println("Please enter a number!");
                            sc.nextLine();
                        }
                    }

                    if ("waiting".equalsIgnoreCase(newStatus)) {
                        System.out.print("Reason for waiting: ");
                        String reason = sc.nextLine();
                        t.addHistory(agent.id, "STATUS_CHANGE", "Waiting: " + reason);
                    } else if ("resolved".equalsIgnoreCase(newStatus)) {
                        t.resolvedDate = new java.util.Date();
                        t.addHistory(agent.id, "RESOLVED", "Ticket resolved");
                    } else {
                        t.addHistory(agent.id, "STATUS_CHANGE", "Changed to " + newStatus);
                    }

                    t.status = newStatus;
                    system.database.updateTicket(t);
                    System.out.println("Ticket status updated successfully");
                    return;
                }
            }

            System.out.println("Ticket not found or not assigned to you");

        } catch (InputMismatchException e) {
            System.out.println("Invalid Ticket ID!");
            sc.nextLine();
        }
    }

    // REASSIGN TO OTHER AGENT
    private void reassign() {
        try {
            System.out.print("Ticket ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            Ticket targetTicket = null;
            for (Ticket t : system.tickets) {
                if (t.id == id) {
                    targetTicket = t;
                    break;
                }
            }

            if (targetTicket == null) {
                System.out.println("Ticket not found!");
                return;
            }

            System.out.println("\nAvailable Agents:");
            for (User u : system.users) {
                if ("agent".equals(u.role)) {
                    System.out.println("- " + u.id + " (" + u.name + ")");
                }
            }

            String newAgent = "";
            boolean validAgent = false;

            while (!validAgent) {
                System.out.print("Enter Agent ID: ");
                newAgent = sc.nextLine();

                for (User u : system.users) {
                    if (u.id.equals(newAgent) && "agent".equals(u.role)) {
                        validAgent = true;
                        break;
                    }
                }

                if (!validAgent) {
                    System.out.println("Invalid Agent ID! Try again.");
                }
            }

            targetTicket.assignedTo = newAgent;
            targetTicket.addHistory(agent.id, "REASSIGNED", "Reassigned to " + newAgent);
            system.database.updateTicket(targetTicket);
            System.out.println("Ticket reassigned successfully");

        } catch (InputMismatchException e) {
            System.out.println("Invalid Ticket ID!");
            sc.nextLine();
        }
    }

    // SEARCH BY STATUS
    private void search() {
        try {
            String status = "";
            boolean validStatus = false;

            while (!validStatus) {
                try {
                    System.out.println("Select status to search:");
                    System.out.println("1. open");
                    System.out.println("2. in-progress");
                    System.out.println("3. waiting");
                    System.out.println("4. resolved");
                    System.out.println("5. closed");
                    System.out.print("Enter choice (1-5): ");

                    int choice = sc.nextInt();
                    sc.nextLine();

                    if (choice == 1) status = "open";
                    else if (choice == 2) status = "in-progress";
                    else if (choice == 3) status = "waiting";
                    else if (choice == 4) status = "resolved";
                    else if (choice == 5) status = "closed";
                    else {
                        System.out.println("Invalid choice!");
                        continue;
                    }

                    validStatus = true;

                } catch (InputMismatchException e) {
                    System.out.println("Please enter a number!");
                    sc.nextLine();
                }
            }

            System.out.println("\n--- Search Results ---");
            boolean found = false;

            for (Ticket t : system.tickets) {
                if (t.status != null && t.status.equalsIgnoreCase(status)) {
                    System.out.println(t.id + " | " + t.title + " | " + t.status);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No tickets found with status: " + status);
            }

        } catch (Exception e) {
            System.out.println("Error while searching tickets: " + e.getMessage());
        }
    }

    private void addNote() {
        try {
            System.out.print("Ticket ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            for (Ticket t : system.tickets) {
                if (t.id == id && agent.id.equals(t.assignedTo)) {
                    System.out.print("Note: ");
                    String note = sc.nextLine();
                    t.notes.add(note);
                    system.database.addNote(t.id, note);
                    System.out.println("Note added successfully");
                    return;
                }
            }

            System.out.println("Ticket not found or not assigned to you");

        } catch (InputMismatchException e) {
            System.out.println("Invalid Ticket ID!");
            sc.nextLine();
        }
    }
}
