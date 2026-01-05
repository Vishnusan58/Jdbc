
import java.util.InputMismatchException;
import java.util.Scanner;

// USER actions
public class UserMenu {
    TicketSystem system;
    User user;
    Scanner sc = new Scanner(System.in);

    public UserMenu(TicketSystem system, User user) {
        this.system = system;
        this.user = user;
    }

    public void menu() {
        while (true) {
            try {
                System.out.println("\n----- USER MENU -----");
                System.out.println("1. Create Ticket");
                System.out.println("2. My Tickets");
                System.out.println("3. Edit Ticket Description");
                System.out.println("4. View Notes");
                System.out.println("5. Escalate");
                System.out.println("6. Raise Change Request");
                System.out.println("7. Rate Ticket");
                System.out.println("8. Logout");
                System.out.print("Choice: ");

                int ch = sc.nextInt();
                sc.nextLine(); // clear buffer

                switch (ch) {
                    case 1: createTicket(); break;
                    case 2: viewTickets(); break;
                    case 3: editTicket(); break;
                    case 4: viewNotes(); break;
                    case 5: escalateTicket(); break;
                    case 6: raiseChangeRequest(); break;
                    case 7: rateTicket(); break;
                    case 8: return;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                sc.nextLine(); // clear invalid input
            } catch (Exception e) {
                System.out.println("Unexpected error occurred: " + e.getMessage());
            }
        }
    }

    // CATEGORY SELECTION
    private void createTicket() {
        try {
            System.out.print("Title: ");
            String title = sc.nextLine();

            System.out.print("Description: ");
            String desc = sc.nextLine();

            String cat = "";
            boolean validCat = false;

            while (!validCat) {
                System.out.println("Select Category:");
                System.out.println("1. IT");
                System.out.println("2. HR");
                System.out.println("3. Network");
                System.out.print("Enter choice (1/2/3): ");

                try {
                    int catChoice = sc.nextInt();
                    sc.nextLine();

                    if (catChoice == 1) {
                        cat = "IT";
                        validCat = true;
                    } else if (catChoice == 2) {
                        cat = "HR";
                        validCat = true;
                    } else if (catChoice == 3) {
                        cat = "Network";
                        validCat = true;
                    } else {
                        System.out.println("Invalid choice!");
                    }

                } catch (InputMismatchException e) {
                    System.out.println("Please enter a number!");
                    sc.nextLine();
                }
            }

            System.out.print("Sub-Category: ");
            String subCat = sc.nextLine();

            Ticket t = new Ticket(title, desc, cat, subCat, user.id);
            system.assignTicket(t);
            system.tickets.add(t);
            user.myTickets.add(t);
            system.database.insertTicket(t);

            System.out.println("Ticket Created Successfully. Ticket ID: " + t.id);

        } catch (Exception e) {
            System.out.println("Error while creating ticket: " + e.getMessage());
        }
    }

    private void editTicket() {
        try {
            System.out.print("Ticket ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            for (Ticket t : user.myTickets) {
                if (t.id == id && t.status != null && !t.status.equalsIgnoreCase("resolved")) {
                    System.out.print("New Description: ");
                    t.description = sc.nextLine();
                    system.database.updateTicket(t);
                    System.out.println("Description updated successfully");
                    return;
                }
            }
            System.out.println("Cannot edit ticket");

        } catch (InputMismatchException e) {
            System.out.println("Invalid Ticket ID format!");
            sc.nextLine();
        }
    }

    private void viewNotes() {
        try {
            System.out.print("Ticket ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            for (Ticket t : user.myTickets) {
                if (t.id == id) {
                    if (t.notes.isEmpty()) {
                        System.out.println("No notes found for this ticket.");
                    } else {
                        for (String note : t.notes) {
                            System.out.println("- " + note);
                        }
                    }
                    return;
                }
            }
            System.out.println("Ticket not found");

        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid Ticket ID!");
            sc.nextLine();
        }
    }

    private void viewTickets() {
        System.out.println("\n--- My Tickets ---");
        System.out.println("ID | Category | Status");
        for (Ticket t : user.myTickets) {
            System.out.println(t.id + " | " + t.category + " | " + t.status);
        }
    }

    private void escalateTicket() {
        try {
            System.out.print("Enter Ticket ID to escalate: ");
            int id = sc.nextInt();
            sc.nextLine();

            for (Ticket t : user.myTickets) {
                if (t.id == id && !t.escalated) {
                    t.escalated = true;
                    system.database.updateTicket(t);
                    System.out.println("Ticket escalated to admin");
                    return;
                }
            }
            System.out.println("Ticket not found or already escalated");

        } catch (InputMismatchException e) {
            System.out.println("Invalid Ticket ID!");
            sc.nextLine();
        }
    }

    private void rateTicket() {
        try {
            System.out.print("Enter Ticket ID to rate: ");
            int id = sc.nextInt();
            sc.nextLine();

            for (Ticket t : user.myTickets) {
                if (t.id == id) {

                    if (!"resolved".equalsIgnoreCase(t.status)) {
                        System.out.println("Ticket not resolved yet.");
                        return;
                    }

                    if (t.rating > 0) {
                        System.out.println("Already rated: " + t.rating + "/5");
                        return;
                    }

                    int rating = 0;
                    boolean valid = false;

                    while (!valid) {
                        try {
                            System.out.print("Enter rating (1-5): ");
                            rating = sc.nextInt();
                            sc.nextLine();

                            if (rating >= 1 && rating <= 5)
                                valid = true;
                            else
                                System.out.println("Rating must be between 1 and 5.");

                        } catch (InputMismatchException e) {
                            System.out.println("Enter numeric rating!");
                            sc.nextLine();
                        }
                    }

                    t.rating = rating;
                    System.out.println("Rating saved successfully!");

                    if (rating < 2) {
                        t.addHistory("System", "FLAG", "Low rating flagged");
                    }

                    t.addHistory(user.id, "RATED", "Rating: " + rating);
                    system.database.updateTicket(t);
                    return;
                }
            }
            System.out.println("Ticket not found");

        } catch (InputMismatchException e) {
            System.out.println("Invalid Ticket ID!");
            sc.nextLine();
        }
    }

    private void raiseChangeRequest() {
        try {
            String asset = "";
            boolean validAsset = false;

            while (!validAsset) {
                try {
                    System.out.println("Select Asset:");
                    System.out.println("1. laptop");
                    System.out.println("2. mouse");
                    System.out.println("3. keyboard");
                    System.out.print("Choice: ");

                    int choice = sc.nextInt();
                    sc.nextLine();

                    if (choice == 1) asset = "laptop";
                    else if (choice == 2) asset = "mouse";
                    else if (choice == 3) asset = "keyboard";
                    else {
                        System.out.println("Invalid choice");
                        continue;
                    }
                    validAsset = true;

                } catch (InputMismatchException e) {
                    System.out.println("Enter number only!");
                    sc.nextLine();
                }
            }

            String change = "";
            boolean validChange = false;

            while (!validChange) {
                try {
                    System.out.println("Select Change:");
                    System.out.println("1. fix");
                    System.out.println("2. renew");
                    System.out.print("Choice: ");

                    int choice = sc.nextInt();
                    sc.nextLine();

                    if (choice == 1) change = "fix";
                    else if (choice == 2) change = "renew";
                    else {
                        System.out.println("Invalid choice");
                        continue;
                    }
                    validChange = true;

                } catch (InputMismatchException e) {
                    System.out.println("Enter number only!");
                    sc.nextLine();
                }
            }

            int id = new java.util.Random().nextInt(900) + 100;
            ChangeRequest cr = new ChangeRequest(id, asset, change, user.id);
            system.changeRequests.add(cr);
            system.database.insertChangeRequest(cr);

            System.out.println("Change request raised successfully!\nReference ID: " + id);

        } catch (Exception e) {
            System.out.println("Error while raising change request: " + e.getMessage());
        }
    }
}
