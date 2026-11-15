import java.io.*;
import java.util.*;

// Student class (Serializable so we can save it to a file)
class Student implements Serializable {
    String rollNo;
    String name;
    String department;
    HashMap<String, Double> marks = new HashMap<>();

    Student(String rollNo, String name, String department) {
        this.rollNo = rollNo;
        this.name = name;
        this.department = department;
    }

    double getTotal() {
        double total = 0;
        for (double m : marks.values()) {
            total = total + m;
        }
        return total;
    }

    double getAverage() {
        if (marks.size() == 0) {
            return 0;
        }
        return getTotal() / marks.size();
    }

    String getGrade() {
        double avg = getAverage();
        if (avg >= 90) return "A+";
        else if (avg >= 80) return "A";
        else if (avg >= 70) return "B";
        else if (avg >= 60) return "C";
        else if (avg >= 50) return "D";
        else return "F";
    }

    public String toString() {
        return rollNo + " | " + name + " | " + department
                + " | Total=" + getTotal()
                + " | Avg=" + String.format("%.2f", getAverage())
                + " | Grade=" + getGrade();
    }
}

public class StudentRecordSystem {
    static ArrayList<Student> students = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static final String FILE = "students.dat";

    public static void main(String[] args) {
        loadFromFile();   // load old data if file exists
        int choice;

        do {
            System.out.println("\n===== STUDENT RECORD SYSTEM =====");
            System.out.println("1. Add student");
            System.out.println("2. Search student");
            System.out.println("3. Update marks");
            System.out.println("4. Delete student");
            System.out.println("5. Display all students");
            System.out.println("6. Rank list (by average)");
            System.out.println("7. Top N students");
            System.out.println("8. Class statistics");
            System.out.println("0. Save and exit");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
                choice = -1;
                continue;
            }

            switch (choice) {
                case 1: addStudent(); break;
                case 2: searchStudent(); break;
                case 3: updateMarks(); break;
                case 4: deleteStudent(); break;
                case 5: displayAll(); break;
                case 6: rankList(); break;
                case 7: topN(); break;
                case 8: statistics(); break;
                case 0:
                    saveToFile();
                    System.out.println("Goodbye!");
                    break;
                default: System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    // find a student by roll number (returns null if not found)
    static Student findStudent(String roll) {
        for (Student s : students) {
            if (s.rollNo.equals(roll)) {
                return s;
            }
        }
        return null;
    }

    static void addStudent() {
        System.out.print("Roll no: ");
        String roll = sc.nextLine();
        if (findStudent(roll) != null) {
            System.out.println("Roll number already exists.");
            return;
        }
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Department: ");
        String dept = sc.nextLine();

        Student s = new Student(roll, name, dept);

        System.out.print("How many subjects? ");
        int n = Integer.parseInt(sc.nextLine());
        for (int i = 1; i <= n; i++) {
            System.out.print("Subject " + i + " name: ");
            String subject = sc.nextLine();
            System.out.print("Marks (0-100): ");
            double m = Double.parseDouble(sc.nextLine());
            if (m < 0 || m > 100) {
                System.out.println("Invalid marks, skipping this subject.");
            } else {
                s.marks.put(subject, m);
            }
        }
        students.add(s);
        System.out.println("Student added.");
    }

    static void searchStudent() {
        System.out.print("Enter roll no: ");
        Student s = findStudent(sc.nextLine());
        if (s == null) {
            System.out.println("Student not found.");
        } else {
            System.out.println(s);
            for (String subject : s.marks.keySet()) {
                System.out.println("   " + subject + " : " + s.marks.get(subject));
            }
        }
    }

    static void updateMarks() {
        System.out.print("Enter roll no: ");
        Student s = findStudent(sc.nextLine());
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.print("Subject: ");
        String subject = sc.nextLine();
        System.out.print("Marks (0-100): ");
        double m = Double.parseDouble(sc.nextLine());
        if (m < 0 || m > 100) {
            System.out.println("Invalid marks.");
            return;
        }
        s.marks.put(subject, m);
        System.out.println("Updated: " + s);
    }

    static void deleteStudent() {
        System.out.print("Enter roll no: ");
        Student s = findStudent(sc.nextLine());
        if (s == null) {
            System.out.println("Student not found.");
        } else {
            students.remove(s);
            System.out.println("Deleted.");
        }
    }

    static void displayAll() {
        if (students.isEmpty()) {
            System.out.println("No records.");
            return;
        }
        for (Student s : students) {
            System.out.println(s);
        }
    }

    // sort a copy of the list so the original order is not changed
    static ArrayList<Student> sortedByAverage() {
        ArrayList<Student> copy = new ArrayList<>(students);
        Collections.sort(copy, new Comparator<Student>() {
            public int compare(Student a, Student b) {
                return Double.compare(b.getAverage(), a.getAverage());  // highest first
            }
        });
        return copy;
    }

    static void rankList() {
        ArrayList<Student> sorted = sortedByAverage();
        if (sorted.isEmpty()) {
            System.out.println("No records.");
            return;
        }
        int rank = 1;
        for (Student s : sorted) {
            System.out.println(rank + ". " + s);
            rank++;
        }
    }

    static void topN() {
        System.out.print("Enter N: ");
        int n = Integer.parseInt(sc.nextLine());
        ArrayList<Student> sorted = sortedByAverage();
        for (int i = 0; i < n && i < sorted.size(); i++) {
            System.out.println((i + 1) + ". " + sorted.get(i));
        }
    }

    static void statistics() {
        if (students.isEmpty()) {
            System.out.println("No records.");
            return;
        }
        double sum = 0;
        int a = 0, b = 0, c = 0, d = 0, f = 0;
        for (Student s : students) {
            sum = sum + s.getAverage();
            String g = s.getGrade();
            if (g.equals("A+") || g.equals("A")) a++;
            else if (g.equals("B")) b++;
            else if (g.equals("C")) c++;
            else if (g.equals("D")) d++;
            else f++;
        }
        System.out.println("Total students : " + students.size());
        System.out.println("Class average  : " + String.format("%.2f", sum / students.size()));
        System.out.println("A/A+: " + a + "  B: " + b + "  C: " + c + "  D: " + d + "  F: " + f);
    }

    static void saveToFile() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE));
            out.writeObject(students);
            out.close();
            System.out.println("Data saved.");
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    static void loadFromFile() {
        File f = new File(FILE);
        if (!f.exists()) {
            return;
        }
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
            students = (ArrayList<Student>) in.readObject();
            in.close();
            System.out.println("Loaded " + students.size() + " record(s).");
        } catch (Exception e) {
            System.out.println("Error loading: " + e.getMessage());
        }
    }
}
