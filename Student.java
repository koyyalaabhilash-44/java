import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A student record. Serializable so it can be saved to disk.
 * Comparable: the natural ordering is the default RANKING order
 * (higher average first, then higher total, then roll number).
 */
public class Student implements Serializable, Comparable<Student> {
    private static final long serialVersionUID = 1L;

    private final String rollNo;
    private String name;
    private String department;
    private final Map<String, Double> marks = new LinkedHashMap<>();

    public Student(String rollNo, String name, String department) {
        this.rollNo = rollNo;
        this.name = name;
        this.department = department;
    }

    public String getRollNo()      { return rollNo; }
    public String getName()        { return name; }
    public String getDepartment()  { return department; }
    public Map<String, Double> getMarks() { return marks; }

    public void setName(String name)             { this.name = name; }
    public void setDepartment(String department) { this.department = department; }

    public void setMark(String subject, double score) {
        if (score < 0 || score > 100)
            throw new IllegalArgumentException("Marks must be between 0 and 100");
        marks.put(subject, score);
    }

    public double getTotal() {
        double t = 0;
        for (double m : marks.values()) t += m;
        return t;
    }

    public double getAverage() {
        return marks.isEmpty() ? 0 : getTotal() / marks.size();
    }

    public String getGrade() {
        double a = getAverage();
        if (a >= 90) return "A+";
        if (a >= 80) return "A";
        if (a >= 70) return "B";
        if (a >= 60) return "C";
        if (a >= 50) return "D";
        return "F";
    }

    /** Natural ordering = default ranking: average desc, total desc, roll no asc. */
    @Override
    public int compareTo(Student o) {
        int c = Double.compare(o.getAverage(), getAverage());
        if (c != 0) return c;
        c = Double.compare(o.getTotal(), getTotal());
        if (c != 0) return c;
        return rollNo.compareTo(o.rollNo);   // tie-break keeps TreeSet from dropping students
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Student && rollNo.equals(((Student) o).rollNo);
    }

    @Override
    public int hashCode() { return rollNo.hashCode(); }

    @Override
    public String toString() {
        return String.format("%-8s %-18s %-6s Total=%7.2f Avg=%6.2f Grade=%s",
                rollNo, name, department, getTotal(), getAverage(), getGrade());
    }
}
