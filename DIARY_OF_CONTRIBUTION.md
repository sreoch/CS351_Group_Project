# CS351 Group Programming Assignment - Diary of Contribution

**Group Members:** Jack Bray, Fraser Nixon, Scott Reoch

---

## Week 1: 3rd - 9th November 2025

### Meeting Minutes (3rd November, 3pm)
**Attendees:** All members present  
**Duration:** 25 minutes  
**Location:** Microsoft Teams

**Discussed:**
- Project requirements and brief overview
- GitHub repository setup
- Planning tools (Jira Board, LucidChart)
- How we are going to tackle creating the planning diagrams and requirements
- Initial design approach for server/client architecture

**Decisions:**
- Use GitHub for version control
- Communicating through teams
- Meet again on Friday to tackle the design stage, everyone will come with ideas and a good understanding of the brief
- Going to look into creating a task board, this will help with tracking who has done what work, and what work is available to pick up.

### Meeting Minutes (7th November, 3:30pm)
**Attendees:** All members present  
**Duration:** 70 minutes  
**Location:** In Person (Location: University)

**Discussed:**
- The planning stage, what diagrams we are going to create
- How we are going to manage feature branches (main -> develop -> feature)
- Pull Request reviews before merging code
- Technical design for the app (message format, what parent classes we need)
- Timeline for project completion
- Delegating planning tasks
- What process communication method we need to use

### Individual Contributions

**Jack:**
- Class Diagram
- (pls fill out the rest)

**Fraser:**
- Design overview diagram 
- Initial draft of API docs

**Scott:**
- Started on design writeup
- Set up repo including diary of contribution
- Created folder structure 
- Implemented shared classes (Message, MessageType, TransactionType, Account, Transaction)
- Added BanKClient implementation with features (login, transfer, deposit, withdraw, view transactions, logout)
- Created unit tests for account and transaction classes

**Collaborative Work:**
- All members: Collaboratively came to an agreement on design process and project scope (4th Nov, 1.5 hours)

---

## Week 2: 10th - 17th November 2025

### Individual Contributions

**Jack:**


**Fraser:**


**Scott:**
- Added some code improvement for thread safety and code quality
- synchronized the transfer method in the server to prevent race conditions
- Improved the transaction validation flow in the ClientHandler a bit
- Moved a lot of magic numbers into a constants file
- Upgraded the ClientHandler to use try-with-resources for proper stream management
- Created concurrent testing suite (ConcurrentServerTest.java)
- Wrote a few tests in this suite to verify that we are handling thread safety properly

**Collaborative Work:**
---

