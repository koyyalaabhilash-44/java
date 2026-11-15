import java.util.*;

/** Menu-driven console front end. */
public class StudentRecordSystem {
    private static final String DATA_FILE = "students.ser";
    private static final String CSV_FILE  = "ranking.csv";

    private static final StudentManager mgr = new StudentManager();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // auto-load existing data
        if (new java.io.File(DATA_FILE).exists()) {
            try {
                System.out.println("Loaded " + mgr.load(DATA_FILE) + " record(s) from " + DATA_FILE);
            } catch (Exception e) {
                System.out.println("Could not load saved data: " + e.getMessage());
            }
        }

        while (true) {
            System.out.println("\n===== STUDENT RECORD MANAGEMENT & RANKING =====");
            System.out.println("1. Add student");
            System.out.println("2. Search by roll number");
            System.out.println("3. Update / add marks");
            System.out.println("4. Delete student");
            System.out.println("5. Display all students");
            System.out.println("6. Rank list (choose criteria)");
            System.out.println("7. Top N students");
            System.out.println("8. Class statistics");
            System.out.println("9. Save (serialize)");
            System.out.println("10. Load (deserialize)");
            System.out.println("11. Export ranking to CSV");
            System.out.println("0. Save & exit");
            System.out.print("Choice: ");

            if (!sc.hasNextLine()) { saveQuietly(); return; }
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1":  addStudent(); break;
                    case "2":  search(); break;
                    case "3":  updateMarks(); break;
                    case "4":  delete(); break;
                    case "5":  display(mgr.rank(StudentComparators.BY_ROLL_NO)); break;
                    case "6":  rankMenu(); break;
                    case "7":  topN(); break;
                    case "8":  stats(); break;
                    case "9":  mgr.save(DATA_FILE); System.out.println("Saved to " + DATA_FILE); break;
                    case "10": System.out.println("Loaded " + mgr.load(DATA_FILE) + " record(s)."); break;
                    case "11": mgr.exportCsv(CSV_FILE, StudentComparators.BY_AVERAGE);
                               System.out.println("Exported to " + CSV_FILE); break;
                    case "0":  saveQuietly(); System.out.println("Goodbye!"); return;
                    default:   System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void saveQuietly() {
        try { mgr.save(DATA_FILE); System.out.println("Data saved."); }
        catch (Exception e) { System.out.println("Save failed: " + e.getMessage()); }
    }

    private static void addStudent() {
        System.out.print("Roll no: ");    String roll = sc.nextLine().trim();
        System.out.print("Name: ");       String name = sc.nextLine().trim();
        System.out.print("Department: "); String dept = sc.nextLine().trim();
        if (roll.isEmpty() || name.isEmpty()) { System.out.println("Roll no and name are required."); return; }
        Student s = new Student(roll, name, dept);
        System.out.print("How many subjects? ");
        int n = Integer.parseInt(sc.nextLine().trim());
        for (int i = 1; i <= n; i++) {
            System.out.print("Subject " + i + " name: ");  String sub = sc.nextLine().trim();
            System.out.print("Marks (0-100): ");            double m = Double.parseDouble(sc.nextLine().trim());
            s.setMark(sub, m);
        }
        System.out.println(mgr.add(s) ? "Student added." : "Roll number already exists.");
    }

    private static void search() {
        System.out.print("Roll no: ");
        Student s = mgr.find(sc.nextLine().trim());
        if (s == null) { System.out.println("Not found."); return; }
        System.out.println(s);
        s.getMarks().forEach((k, v) -> System.out.printf("    %-15s %6.2f%n", k, v));
    }

    private static void updateMarks() {
        System.out.print("Roll no: ");
        Student s = mgr.find(sc.nextLine().trim());
        if (s == null) { System.out.println("Not found."); return; }
        System.out.print("Subject: ");       String sub = sc.nextLine().trim();
        System.out.print("Marks (0-100): "); double m = Double.parseDouble(sc.nextLine().trim());
        s.setMark(sub, m);
        System.out.println("Updated. " + s);
    }

    private static void delete() {
        System.out.print("Roll no: ");
        System.out.println(mgr.remove(sc.nextLine().trim()) ? "Deleted." : "Not found.");
    }

    private static void display(Collection<Student> list) {
        if (list.isEmpty()) { System.out.println("No records."); return; }
        int r = 1;
        for (Student s : list) System.out.printf("%2d. %s%n", r++, s);
    }

    private static void rankMenu() {
        System.out.println("Rank by: 1) Average (default Comparable)  2) Total  3) Name  4) Subject");
        System.out.print("Choice: ");
        switch (sc.nextLine().trim()) {
            case "1": display(mgr.rank()); break;
            case "2": display(mgr.rank(StudentComparators.BY_TOTAL)); break;
            case "3": display(mgr.rank(StudentComparators.BY_NAME)); break;
            case "4":
                System.out.print("Subject: ");
                display(mgr.rank(StudentComparators.bySubject(sc.nextLine().trim())));
                break;
            default: System.out.println("Invalid choice.");
        }
    }

    private static void topN() {
        System.out.print("N: ");
        display(mgr.topN(Integer.parseInt(sc.nextLine().trim())));
    }

    private static void stats() {
        System.out.println("Students        : " + mgr.size());
        System.out.printf ("Class average   : %.2f%n", mgr.classAverage());
        System.out.println("Grade breakdown : " + mgr.gradeDistribution());
    }
}
