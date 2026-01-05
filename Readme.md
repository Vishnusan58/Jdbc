
---

# 🧠 IT Ticket Management System

## **Complete Project Review Preparation Guide**

> **Goal:** This document helps you **explain the flow, logic, and Java concepts** confidently during project review, even if questions come from any random part of the code.

---

## 📁 Project Structure

```
ITTicketManagementSystem/
│
├── Main.java              → Entry point (main method)
├── TicketSystem.java      → Core system controller & data storage
├── Ticket.java            → Ticket model class
├── User.java              → User model class
├── TicketHistory.java     → Audit log model class
├── ChangeRequest.java     → Change request model class
├── UserMenu.java          → User role menu & actions
├── AgentMenu.java         → Agent role menu & actions
├── AdminMenu.java         → Admin role menu & actions
└── Reports/               → Documentation reports
```

---

## 1️⃣ How to Start Explaining (MOST IMPORTANT)

### If they say: "Explain your project"

Say this slowly and confidently:

> "This is a **Java console-based IT Ticket Management System**.
> 
> The application starts from `Main.java`, which creates a `TicketSystem` object.
> 
> `TicketSystem` acts as the **central controller** and **in-memory database** using ArrayLists.
> 
> After login, the system routes the user to **role-based menus**: User, Agent, or Admin.
> 
> Each role has different permissions, and **ticket history is tracked** for every action.
> 
> The system has **input validation** using while loops to ensure only valid data is accepted."

⚠️ **This opening alone sets a strong impression**

---

## 2️⃣ Complete Execution Flow (Step-by-Step)

### Step 1: JVM Starts the Program

**File:** `Main.java`

```java
public class Main {
    public static void main(String[] args) {
        TicketSystem system = new TicketSystem();
        system.start();
    }
}
```

#### What happens?
1. JVM looks for `main()` method (entry point)
2. Creates `TicketSystem` object using `new` keyword
3. Calls `start()` method to begin the application

#### Java Concepts to Mention:
| Concept | Explanation |
|---------|-------------|
| `public static void main` | JVM entry point - must be exactly this signature |
| `new` keyword | Creates object in heap memory |
| Method call | `system.start()` invokes the method |

---

### Step 2: TicketSystem Constructor Runs

**File:** `TicketSystem.java`

```java
public TicketSystem() {
    // Demo users created
    users.add(new User("U1", "123", "Manu", "user"));
    users.add(new User("A1", "123", "Vishnu", "agent"));
    users.add(new User("AD1", "123", "Rishi", "admin"));
    
    // Demo tickets created
    Ticket t1 = new Ticket("Email Issue", "Email not working", "IT", "Software", "U1");
    tickets.add(t1);
}
```

#### What happens here?
- Demo users are created (Users, Agents, Admin)
- Demo tickets are created for testing
- All data stored in `ArrayList`

#### Why no database?
> "This project focuses on **core Java concepts**. ArrayList simulates database behavior for learning purposes."

#### Java Concepts:
| Concept | Explanation |
|---------|-------------|
| Constructor | Special method called when object is created |
| `ArrayList` | Dynamic array that can grow/shrink |
| Object initialization | Setting initial values for fields |

---

### Step 3: Main Menu Loop Starts

**Method:** `start()`

```java
public void start() {
    while (true) {
        System.out.println("1. Login");
        System.out.println("2. Exit");
        
        int ch = sc.nextInt();
        sc.nextLine();  // Clear buffer
        
        if (ch == 1)
            login();
        else
            break;
    }
}
```

#### Java Concepts:
| Concept | Explanation |
|---------|-------------|
| `while (true)` | Infinite loop - keeps running until `break` |
| `break` | Exits the loop |
| `sc.nextLine()` | Clears input buffer after `nextInt()` |
| Menu-driven design | Common pattern for console applications |

💡 **Interview tip:**
> "Console apps usually use infinite loops to keep running until the user chooses to exit."

---

### Step 4: Login Logic

**Method:** `login()`

```java
private void login() {
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
}
```

#### Flow:
1. Ask for User ID & Password
2. Loop through `users` ArrayList
3. If credentials match → open role-specific menu
4. If no match → show error message

#### Java Concepts:
| Concept | Explanation |
|---------|-------------|
| Enhanced for loop | `for (User u : users)` - iterates through collection |
| `equals()` | Compares string content (not reference) |
| `&&` (AND operator) | Both conditions must be true |
| Role-based routing | Different menu based on user role |

---

## 3️⃣ Model Classes (Data Holders)

### Ticket.java - The Core Entity

```java
public class Ticket {
    public int id;                    // Auto-generated unique ID
    public String title;              // Ticket title
    public String description;        // Issue description
    public String category;           // IT / HR / Network
    public String subCategory;        // Software / Hardware etc.
    public String status;             // open / in-progress / waiting / resolved
    public String createdBy;          // User ID who created
    public String assignedTo;         // Agent ID assigned to
    public boolean escalated;         // Escalation flag
    public int rating;                // 1-5 rating, 0 if not rated
    public Date createdDate;          // When created
    public Date resolvedDate;         // When resolved
    public ArrayList<TicketHistory> history;  // Audit trail
    public ArrayList<String> notes;   // Agent notes
}
```

#### Constructor:
```java
public Ticket(String title, String description, String category, String subCategory, String createdBy) {
    this.id = new java.util.Random().nextInt(9000) + 1000;  // Random 4-digit ID
    this.status = "open";  // Default status
    this.createdDate = new Date();  // Current timestamp
    this.history.add(new TicketHistory(createdBy, "CREATED", "Ticket created"));
}
```

#### Java Concepts:
| Concept | Explanation |
|---------|-------------|
| `this` keyword | Refers to current object's fields |
| `new Random().nextInt(9000) + 1000` | Generates random number 1000-9999 |
| `new Date()` | Creates current timestamp |
| Composition | Ticket HAS-A history (contains other objects) |

---

### User.java - System User

```java
public class User {
    public String id;
    public String password;
    public String name;
    public String role;  // user / agent / admin
    public ArrayList<Ticket> myTickets;
    
    public User(String id, String password, String name, String role) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.role = role;
        this.myTickets = new ArrayList<>();
    }
}
```

---

### TicketHistory.java - Audit Trail

```java
public class TicketHistory {
    public Date timestamp;
    public String actor;    // Who did the action
    public String action;   // What action (CREATED, ASSIGNED, etc.)
    public String details;  // Additional info
    
    @Override
    public String toString() {
        return "[" + timestamp + "] " + actor + ": " + action + " (" + details + ")";
    }
}
```

#### Java Concepts:
| Concept | Explanation |
|---------|-------------|
| `@Override` | Annotation indicating method overrides parent |
| `toString()` | Returns string representation of object |

---

### ChangeRequest.java - Asset Changes

```java
public class ChangeRequest {
    public int id;
    public String assetType;      // laptop / mouse / keyboard
    public String changeType;     // fix / renew
    public String requestedBy;
    public String status;         // pending / approved / sent-to-agent
    public String assignedAgent;
    public Date createdDate;
    public Date expiryDate;       // 1 year from creation
}
```

---

## 4️⃣ Role-Based Menu Flow

---

## 👤 USER FLOW – `UserMenu.java`

### User Menu Options:
```
1. Create Ticket
2. My Tickets
3. Edit Ticket Description
4. View Notes
5. Escalate
6. Raise Change Request
7. Rate Ticket
8. Logout
```

---

### 4.1 Create Ticket (WITH INPUT VALIDATION)

```java
private void createTicket() {
    System.out.print("Title: ");
    String title = sc.nextLine();
    System.out.print("Description: ");
    String desc = sc.nextLine();

    // ✅ INPUT VALIDATION - Category
    String cat = "";
    boolean validCat = false;
    while (!validCat) {
        System.out.println("Select Category:");
        System.out.println("1. IT");
        System.out.println("2. HR");
        System.out.println("3. Network");
        System.out.print("Enter choice (1/2/3): ");
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
            System.out.println("Invalid choice! Please enter 1, 2, or 3.");
        }
    }

    Ticket t = new Ticket(title, desc, cat, subCat, user.id);
    system.assignTicket(t);  // Auto-assign to agent
    system.tickets.add(t);
    user.myTickets.add(t);
}
```

#### Input Validation Pattern (VERY IMPORTANT):
```java
String value = "";
boolean valid = false;

while (!valid) {
    // Show options
    // Read input
    if (/* valid choice */) {
        value = "chosen_value";
        valid = true;  // Exit loop
    } else {
        System.out.println("Invalid! Try again.");
        // Loop continues
    }
}
```

#### Java Concepts:
| Concept | Explanation |
|---------|-------------|
| `while (!valid)` | Loop until valid becomes true |
| `boolean` flag | Controls loop execution |
| `sc.nextLine()` after `nextInt()` | Clears input buffer |
| Object reference sharing | Same ticket object in system.tickets AND user.myTickets |

🗣 **Say this:**
> "The same Ticket object reference is stored in both lists, not copied. Changes reflect everywhere."

---

### 4.2 Rate Ticket (WITH VALIDATION)

```java
private void rateTicket() {
    // Find ticket
    for (Ticket t : user.myTickets) {
        if (t.id == id) {
            // Check if resolved
            if (!t.status.equalsIgnoreCase("resolved")) {
                System.out.println("Ticket is not resolved yet. Cannot rate.");
                return;
            }
            
            // Check if already rated
            if (t.rating > 0) {
                System.out.println("Already rated: " + t.rating + "/5");
                return;
            }
            
            // ✅ VALIDATED RATING INPUT
            int rating = 0;
            boolean validRating = false;
            while (!validRating) {
                System.out.println("Select rating:");
                System.out.println("1 - Very Poor");
                System.out.println("2 - Poor");
                System.out.println("3 - Average");
                System.out.println("4 - Good");
                System.out.println("5 - Excellent");
                System.out.print("Enter rating (1-5): ");
                rating = sc.nextInt();
                
                if (rating >= 1 && rating <= 5) {
                    validRating = true;
                } else {
                    System.out.println("Invalid! Enter 1-5.");
                }
            }
            
            t.rating = rating;
            
            // Flag agent for low rating
            if (rating < 2) {
                System.out.println("[SYSTEM] Low rating flagged for agent: " + t.assignedTo);
                t.addHistory("System", "FLAG", "Low rating flagged");
            }
        }
    }
}
```

#### Business Rules Enforced:
1. Only resolved tickets can be rated
2. Cannot rate twice
3. Rating must be 1-5

---

### 4.3 Escalate Ticket

```java
private void escalateTicket() {
    for (Ticket t : user.myTickets) {
        if (t.id == id && !t.escalated) {
            t.escalated = true;
            System.out.println("Ticket escalated to admin");
            return;
        }
    }
}
```

#### Java Concept:
| Concept | Explanation |
|---------|-------------|
| `boolean` flag | `escalated = true` marks ticket for admin attention |
| Early `return` | Exits method immediately after action |

---

### 4.4 Raise Change Request (WITH VALIDATION)

```java
private void raiseChangeRequest() {
    // ✅ VALIDATE ASSET TYPE
    String asset = "";
    boolean validAsset = false;
    while (!validAsset) {
        System.out.println("Select Asset Type:");
        System.out.println("1. laptop");
        System.out.println("2. mouse");
        System.out.println("3. keyboard");
        System.out.print("Enter choice (1/2/3): ");
        int choice = sc.nextInt();
        sc.nextLine();
        
        if (choice == 1) { asset = "laptop"; validAsset = true; }
        else if (choice == 2) { asset = "mouse"; validAsset = true; }
        else if (choice == 3) { asset = "keyboard"; validAsset = true; }
        else { System.out.println("Invalid choice!"); }
    }
    
    // ✅ VALIDATE CHANGE TYPE
    String change = "";
    boolean validChange = false;
    while (!validChange) {
        System.out.println("Select Change Type:");
        System.out.println("1. fix");
        System.out.println("2. renew");
        System.out.print("Enter choice (1/2): ");
        int choice = sc.nextInt();
        sc.nextLine();
        
        if (choice == 1) { change = "fix"; validChange = true; }
        else if (choice == 2) { change = "renew"; validChange = true; }
        else { System.out.println("Invalid choice!"); }
    }
    
    ChangeRequest cr = new ChangeRequest(id, asset, change, user.id);
    system.changeRequests.add(cr);
}
```

---

## 🧑‍💼 AGENT FLOW – `AgentMenu.java`

### Agent Menu Options:
```
1. My Tickets
2. Add Note
3. Update Status
4. Reassign Ticket
5. Search Tickets
6. Logout
```

---

### 5.1 View Assigned Tickets

```java
private void viewTickets() {
    for (Ticket t : system.tickets) {
        if (agent.id.equals(t.assignedTo)) {
            String tag = t.escalated ? "[ESCALATED]" : "";
            System.out.println(t.id + " | " + t.title + " | " + t.status + " " + tag);
        }
    }
}
```

#### Java Concepts:
| Concept | Explanation |
|---------|-------------|
| `equals()` | Compare strings by content |
| Ternary operator | `condition ? valueIfTrue : valueIfFalse` |
| Filtering | Only show tickets assigned to this agent |

---

### 5.2 Update Status (WITH VALIDATION)

```java
private void updateStatus() {
    for (Ticket t : system.tickets) {
        if (t.id == id && agent.id.equals(t.assignedTo)) {
            
            // ✅ VALIDATED STATUS INPUT
            String newStatus = "";
            boolean validStatus = false;
            while (!validStatus) {
                System.out.println("Select new status:");
                System.out.println("1. in-progress");
                System.out.println("2. waiting");
                System.out.println("3. resolved");
                System.out.print("Enter choice (1/2/3): ");
                int choice = sc.nextInt();
                sc.nextLine();
                
                if (choice == 1) { newStatus = "in-progress"; validStatus = true; }
                else if (choice == 2) { newStatus = "waiting"; validStatus = true; }
                else if (choice == 3) { newStatus = "resolved"; validStatus = true; }
                else { System.out.println("Invalid choice!"); }
            }
            
            // Process based on status
            if (newStatus.equals("waiting")) {
                System.out.print("Reason for waiting: ");
                String reason = sc.nextLine();
                t.addHistory(agent.id, "STATUS_CHANGE", "Changed to waiting: " + reason);
            } else if (newStatus.equals("resolved")) {
                t.resolvedDate = new Date();
                t.addHistory(agent.id, "RESOLVED", "Agent resolved ticket");
            }
            
            t.status = newStatus;
        }
    }
}
```

🗣 **Say this:**
> "This is similar to a state machine - ticket moves through defined states."

---

### 5.3 Reassign Ticket (WITH VALIDATION)

```java
private void reassign() {
    // Find ticket first
    Ticket targetTicket = null;
    for (Ticket t : system.tickets) {
        if (t.id == id) {
            targetTicket = t;
            break;
        }
    }
    
    // Show available agents
    System.out.println("Available Agents:");
    for (User u : system.users) {
        if (u.role.equals("agent")) {
            System.out.println("- " + u.id + " (" + u.name + ")");
        }
    }
    
    // ✅ VALIDATE AGENT EXISTS
    String newAgent = "";
    boolean validAgent = false;
    while (!validAgent) {
        System.out.print("Enter Agent ID: ");
        newAgent = sc.nextLine();
        
        for (User u : system.users) {
            if (u.id.equals(newAgent) && u.role.equals("agent")) {
                validAgent = true;
                break;
            }
        }
        
        if (!validAgent) {
            System.out.println("Invalid Agent ID! Enter from list.");
        }
    }
    
    targetTicket.assignedTo = newAgent;
    targetTicket.addHistory(agent.id, "REASSIGNED", "Reassigned to " + newAgent);
}
```

#### Java Concepts:
| Concept | Explanation |
|---------|-------------|
| `break` | Exit loop early when found |
| Existence validation | Check if agent ID exists before accepting |

---

### 5.4 Search Tickets (WITH VALIDATION)

```java
private void search() {
    // ✅ VALIDATED STATUS SELECTION
    String status = "";
    boolean validStatus = false;
    while (!validStatus) {
        System.out.println("Select status to search:");
        System.out.println("1. open");
        System.out.println("2. in-progress");
        System.out.println("3. waiting");
        System.out.println("4. resolved");
        System.out.println("5. closed");
        System.out.print("Enter choice (1-5): ");
        int choice = sc.nextInt();
        sc.nextLine();
        
        if (choice == 1) { status = "open"; validStatus = true; }
        else if (choice == 2) { status = "in-progress"; validStatus = true; }
        else if (choice == 3) { status = "waiting"; validStatus = true; }
        else if (choice == 4) { status = "resolved"; validStatus = true; }
        else if (choice == 5) { status = "closed"; validStatus = true; }
        else { System.out.println("Invalid choice!"); }
    }
    
    // Display results
    for (Ticket t : system.tickets) {
        if (t.status.equalsIgnoreCase(status)) {
            System.out.println(t.id + " | " + t.title + " | " + t.status);
        }
    }
}
```

---

## 👨‍💼 ADMIN FLOW – `AdminMenu.java`

### Admin Menu Options:
```
1. View All Tickets
2. View Escalated Tickets
3. Assign Escalated Ticket
4. View Change Requests
5. Approve Change Request
6. Report
7. Logout
```

---

### 6.1 View Escalated Tickets

```java
private void viewEscalated() {
    boolean found = false;
    for (Ticket t : system.tickets) {
        if (t.escalated) {
            System.out.println(t.id + " | " + t.title + " | " + t.assignedTo);
            found = true;
        }
    }
    if (!found) {
        System.out.println("No escalated tickets.");
    }
}
```

---

### 6.2 Assign Escalated Ticket (WITH VALIDATION)

```java
private void assignEscalatedTicket() {
    // Find escalated ticket
    Ticket targetTicket = null;
    for (Ticket t : system.tickets) {
        if (t.id == id && t.escalated) {
            targetTicket = t;
            break;
        }
    }
    
    // Show agents & validate
    System.out.println("Available Agents:");
    for (User u : system.users) {
        if (u.role.equals("agent")) {
            System.out.println("- " + u.id + " (" + u.name + ")");
        }
    }
    
    // ✅ VALIDATE AGENT EXISTS
    String agentId = "";
    boolean validAgent = false;
    while (!validAgent) {
        System.out.print("Enter Agent ID: ");
        agentId = sc.nextLine();
        
        for (User u : system.users) {
            if (u.id.equals(agentId) && u.role.equals("agent")) {
                validAgent = true;
                break;
            }
        }
        
        if (!validAgent) {
            System.out.println("Invalid Agent ID!");
        }
    }
    
    targetTicket.assignedTo = agentId;
    targetTicket.status = "in-progress";
}
```

---

### 6.3 Generate Report

```java
private void report() {
    // Count resolved tickets
    int resolved = 0;
    for (Ticket t : system.tickets) {
        if (t.status.equalsIgnoreCase("resolved") || t.status.equalsIgnoreCase("closed")) {
            resolved++;
        }
    }
    System.out.println("Total Resolved/Closed: " + resolved);
    
    // Calculate average rating
    int totalRating = 0;
    int count = 0;
    for (Ticket t : system.tickets) {
        if (t.rating > 0) {
            totalRating += t.rating;
            count++;
        }
    }
    if (count > 0) {
        System.out.println("Average Rating: " + (double) totalRating / count);
    }
}
```

#### Java Concepts:
| Concept | Explanation |
|---------|-------------|
| Counter variables | `resolved++`, `count++` |
| Aggregation | Sum up values |
| Type casting | `(double)` for decimal division |

---

### 6.4 Approve Change Request (WITH VALIDATION)

```java
private void approveChange() {
    for (ChangeRequest cr : system.changeRequests) {
        if (cr.id == id && cr.status.equals("pending")) {
            
            // ✅ VALIDATE CHOICE
            int choice = 0;
            boolean validChoice = false;
            while (!validChoice) {
                System.out.println("1. Approve Renewal");
                System.out.println("2. Send to Agent");
                System.out.print("Enter choice (1/2): ");
                choice = sc.nextInt();
                sc.nextLine();
                
                if (choice == 1 || choice == 2) {
                    validChoice = true;
                } else {
                    System.out.println("Invalid choice!");
                }
            }
            
            if (choice == 1) {
                cr.status = "approved";
            } else {
                // Validate agent and assign
                cr.status = "sent-to-agent";
            }
        }
    }
}
```

---

## 7️⃣ Auto Ticket Assignment Logic (HIGH CHANCE QUESTION)

### Method: `assignTicket(Ticket t)` in TicketSystem.java

```java
public void assignTicket(Ticket t) {
    String bestAgent = null;
    int minTickets = Integer.MAX_VALUE;

    // Loop through all users
    for (User u : users) {
        if (u.role.equals("agent")) {
            int count = 0;
            
            // Count active tickets for this agent
            for (Ticket ticket : tickets) {
                if (ticket.assignedTo != null 
                    && ticket.assignedTo.equals(u.id)
                    && !ticket.status.equals("resolved") 
                    && !ticket.status.equals("closed")) {
                    count++;
                }
            }

            // Track agent with minimum tickets
            if (count < minTickets) {
                minTickets = count;
                bestAgent = u.id;
            }
        }
    }

    if (bestAgent != null) {
        t.assignedTo = bestAgent;
        t.addHistory("System", "ASSIGNED", "Auto-assigned to " + bestAgent);
    }
}
```

#### Algorithm Explanation (SAY THIS):
> "The system loops through all agents, counts active tickets for each agent, and assigns the new ticket to the **agent with the least workload**. This is load balancing."

#### Java Concepts:
| Concept | Explanation |
|---------|-------------|
| `Integer.MAX_VALUE` | Largest possible integer (2147483647) |
| Nested loops | Outer loop: agents, Inner loop: tickets |
| Min-finding algorithm | Track minimum value while iterating |

---

## 8️⃣ Input Validation Pattern (MEMORIZE THIS)

### Standard Pattern Used Throughout:

```java
// Step 1: Initialize variables
String value = "";
boolean valid = false;

// Step 2: Loop until valid
while (!valid) {
    // Show menu options
    System.out.println("1. Option A");
    System.out.println("2. Option B");
    System.out.print("Enter choice: ");
    
    // Read input
    int choice = sc.nextInt();
    sc.nextLine();  // Clear buffer
    
    // Validate
    if (choice == 1) {
        value = "Option A";
        valid = true;  // Exit loop
    } else if (choice == 2) {
        value = "Option B";
        valid = true;  // Exit loop
    } else {
        System.out.println("Invalid! Try again.");
        // Loop continues automatically
    }
}
```

### Where Validation is Applied:

| File | Method | Input Validated |
|------|--------|-----------------|
| UserMenu.java | createTicket() | Category (IT/HR/Network) |
| UserMenu.java | rateTicket() | Rating (1-5) |
| UserMenu.java | raiseChangeRequest() | Asset type, Change type |
| AgentMenu.java | updateStatus() | Status (in-progress/waiting/resolved) |
| AgentMenu.java | search() | Status filter |
| AgentMenu.java | reassign() | Agent ID existence |
| AdminMenu.java | assignEscalatedTicket() | Agent ID existence |
| AdminMenu.java | approveChange() | Choice (1/2), Agent ID |

---

## 9️⃣ Core Java Concepts Mapping (MEMORIZE THIS)

### Object-Oriented Programming (OOP)

| Concept | Example in Project |
|---------|-------------------|
| Class | `Ticket`, `User`, `TicketSystem` |
| Object | `new Ticket(...)`, `new User(...)` |
| Constructor | `public Ticket(...) { }` |
| `this` keyword | `this.id = id;` |
| Composition | Ticket HAS-A history (ArrayList) |

### Collections

| Concept | Example |
|---------|---------|
| `ArrayList` | `ArrayList<Ticket> tickets` |
| `add()` | `tickets.add(t)` |
| Enhanced for loop | `for (Ticket t : tickets)` |

### Control Flow

| Concept | Example |
|---------|---------|
| `while` loop | `while (!valid)` |
| `if-else` | Role-based menu routing |
| `break` | Exit loop early |
| `return` | Exit method early |

### Standard Library

| Class | Usage |
|-------|-------|
| `Scanner` | Read user input |
| `Date` | Timestamps |
| `Random` | Generate ticket IDs |

### String Operations

| Method | Usage |
|--------|-------|
| `equals()` | Compare string content |
| `equalsIgnoreCase()` | Case-insensitive comparison |

---

## **Changelog — Input Validation Updates (Dec 28, 2025)**

- User input validation added across menus using loop-based prompts:
    - User: Category (IT/HR/Network), Rating (1–5), Asset (laptop/mouse/keyboard), Change type (fix/renew)
    - Agent: Status update (in-progress/waiting/resolved), Search filter (open/in-progress/waiting/resolved/closed), Reassign (valid agent IDs)
    - Admin: Assign escalated tickets (valid agent IDs), Approve change (choice 1/2 with validation)
- Pattern used: `while (!valid)` with friendly error messages and `sc.nextLine()` after `nextInt()` to clear buffer
- Invalid inputs no longer update data; the system asks again until valid

---

## **Quick Start**

```powershell
javac *.java
java Main
```

- Logins:
    - User: U1 / 123
    - Agent: A1 / 123
    - Admin: AD1 / 123

---

## **Sample Walkthrough (Beginner-Friendly)**

1. Login as `U1` → choose "Create Ticket"
2. Enter Title + Description → select Category via menu (e.g., 1 for IT)
3. Ticket is auto-assigned to agent with least active load
4. Login as `A1` → "Update Status" → select 2 for `waiting` and add reason
5. Login as `U1` → "Rate Ticket" → select 4 for `Good` (only allowed if `resolved`)
6. Login as `AD1` → "View Escalated" or "Approve Change" → choices validated

---

## **File → Feature Map (For Review Questions)**

- `Main.java`: App entry → creates `TicketSystem`, calls `start()`
- `TicketSystem.java`: Data seeds, `assignTicket()` least-load algorithm, `login()` role routing
- `Ticket.java`: Ticket fields, auto-ID, dates, notes, `history` logging
- `TicketHistory.java`: Timestamped audit entries, `toString()` override
- `ChangeRequest.java`: Asset change model, 1-year `expiryDate`
- `UserMenu.java`:
    - `createTicket()` → category validation loop
    - `rateTicket()` → 1–5 rating loop + low-rating flag
    - `raiseChangeRequest()` → asset + change type validation loops
    - `escalateTicket()` → set `escalated = true`
- `AgentMenu.java`:
    - `updateStatus()` → status validation loop + reason/resolution handling
    - `reassign()` → list agents + validate agent ID
    - `search()` → status filter validation loop
- `AdminMenu.java`:
    - `assignEscalatedTicket()` → find escalated ticket + validate agent ID
    - `approveChange()` → choice validation + optional agent validation
    - `report()` → resolved counts, average rating

---

## 🔟 COMMON REVIEW QUESTIONS & PERFECT ANSWERS

### ❓ Why no database?

> "This project focuses on **core Java concepts**. ArrayList simulates database behavior for learning purposes. In production, we would use MySQL or PostgreSQL."

---

### ❓ How is security handled?

> "Through **role-based access control**. Each role has a separate menu class with different permissions. Login validates credentials before granting access."

---

### ❓ What happens if system restarts?

> "Data resets because it's stored in **memory (RAM)**. This is a limitation. In production, we would persist data to a database."

---

### ❓ Why use ArrayList instead of array?

> "ArrayList is **dynamic** - it can grow and shrink. Regular arrays have fixed size. ArrayList also provides useful methods like `add()` and `size()`."

---

### ❓ Explain the input validation approach.

> "We use a **while loop with a boolean flag**. The loop keeps asking until valid input is received. This prevents invalid data from being stored."

---

### ❓ What is the purpose of `sc.nextLine()` after `sc.nextInt()`?

> "To **clear the input buffer**. `nextInt()` doesn't consume the newline character, so `nextLine()` would read an empty string without this."

---

### ❓ How does auto-assignment work?

> "The system iterates through all agents, counts their active tickets, and assigns the new ticket to the agent with the **least workload**. This is load balancing."

---

## 1️⃣1️⃣ Limitations (Say Honestly - Reviewers Like This)

1. **No persistence** - Data lost on restart
2. **Random ID collision possible** - Same ID could be generated twice
3. **Public fields** - Should use `private` with getters/setters
4. **No encryption** - Passwords stored as plain text
5. **Single user session** - No concurrent user support

---

## 1️⃣2️⃣ Demo Users for Testing

| User ID | Password | Name | Role |
|---------|----------|------|------|
| U1 | 123 | Manu | User |
| U2 | 123 | Karthi | User |
| A1 | 123 | Vishnu | Agent |
| A2 | 123 | Priya | Agent |
| AD1 | 123 | Rishi | Admin |

---

## 1️⃣3️⃣ LAST-MINUTE MEMORY SCRIPT (READ BEFORE REVIEW)

> "**Main** starts the app.
> 
> **TicketSystem** controls everything and stores data.
> 
> **Users** raise tickets with validated category.
> 
> **Agents** resolve tickets with validated status.
> 
> **Admin** monitors escalations and approves changes.
> 
> **ArrayLists** store all data in memory.
> 
> **TicketHistory** tracks every action.
> 
> **Input validation** uses while loops with boolean flags."

---

## ✅ You Are Now REVIEW-READY

You are **not expected to be perfect**.

You are expected to be **clear and confident** — and now you are.

Good luck! 🎯
