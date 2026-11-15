import java.util.Comparator;

/**
 * Comparators for alternative ranking criteria.
 * Every comparator ends with a roll-number tie-break so that a TreeSet
 * never treats two different students as duplicates.
 */
public final class StudentComparators {
    private StudentComparators() {}

    private static final Comparator<Student> BY_ROLL =
            Comparator.comparing(Student::getRollNo);

    public static final Comparator<Student> BY_AVERAGE =
            Comparator.comparingDouble(Student::getAverage).reversed().thenComparing(BY_ROLL);

    public static final Comparator<Student> BY_TOTAL =
            Comparator.comparingDouble(Student::getTotal).reversed().thenComparing(BY_ROLL);

    public static final Comparator<Student> BY_NAME =
            Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER).thenComparing(BY_ROLL);

    public static final Comparator<Student> BY_ROLL_NO = BY_ROLL;

    /** Highest mark in one subject first. Students without that subject go last. */
    public static Comparator<Student> bySubject(String subject) {
        return (a, b) -> {
            double ma = a.getMarks().getOrDefault(subject, -1.0);
            double mb = b.getMarks().getOrDefault(subject, -1.0);
            int c = Double.compare(mb, ma);
            return c != 0 ? c : a.getRollNo().compareTo(b.getRollNo());
        };
    }
}
