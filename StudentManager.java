import java.io.*;
import java.util.*;

/**
 * Core logic.
 *  - ArrayList : ordered master list of records (also what gets serialized)
 *  - HashMap   : O(1) lookup by roll number
 *  - TreeSet   : built on demand to sort students by a chosen Comparator
 */
public class StudentManager {
    private List<Student> students = new ArrayList<>();
    private Map<String, Student> index = new HashMap<>();

    // ---------- CRUD ----------
    public boolean add(Student s) {
        if (index.containsKey(s.getRollNo())) return false;   // duplicate roll no
        students.add(s);
        index.put(s.getRollNo(), s);
        return true;
    }

    public Student find(String rollNo) { return index.get(rollNo); }

    public boolean remove(String rollNo) {
        Student s = index.remove(rollNo);
        return s != null && students.remove(s);
    }

    public List<Student> all() { return Collections.unmodifiableList(students); }
    public int size()          { return students.size(); }

    // ---------- Ranking ----------
    /** Rank using the natural ordering (Comparable). */
    public TreeSet<Student> rank() {
        return new TreeSet<>(students);
    }

    /** Rank using any Comparator. */
    public TreeSet<Student> rank(Comparator<Student> criteria) {
        TreeSet<Student> set = new TreeSet<>(criteria);
        set.addAll(students);
        return set;
    }

    public List<Student> topN(int n) {
        List<Student> out = new ArrayList<>();
        for (Student s : rank()) {
            if (out.size() == n) break;
            out.add(s);
        }
        return out;
    }

    // ---------- Statistics ----------
    public double classAverage() {
        if (students.isEmpty()) return 0;
        double sum = 0;
        for (Student s : students) sum += s.getAverage();
        return sum / students.size();
    }

    public Map<String, Integer> gradeDistribution() {
        Map<String, Integer> dist = new TreeMap<>();
        for (Student s : students) dist.merge(s.getGrade(), 1, Integer::sum);
        return dist;
    }

    // ---------- Serialization (binary) ----------
    public void save(String file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            out.writeObject(new ArrayList<>(students));
        }
    }

    @SuppressWarnings("unchecked")
    public int load(String file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            List<Student> loaded = (List<Student>) in.readObject();
            students = new ArrayList<>(loaded);
            index = new HashMap<>();
            for (Student s : students) index.put(s.getRollNo(), s);   // rebuild the HashMap
            return students.size();
        }
    }

    // ---------- Text file I/O (CSV report) ----------
    public void exportCsv(String file, Comparator<Student> order) throws IOException {
        try (PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            w.println("Rank,RollNo,Name,Department,Total,Average,Grade");
            int rank = 1;
            for (Student s : rank(order)) {
                w.printf("%d,%s,%s,%s,%.2f,%.2f,%s%n", rank++, s.getRollNo(),
                        s.getName(), s.getDepartment(), s.getTotal(), s.getAverage(), s.getGrade());
            }
        }
    }
}
