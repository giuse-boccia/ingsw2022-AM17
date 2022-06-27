package it.polimi.ingsw.model.game_objects;

public class Student {
    private final Color color;

    public Student(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        return color == student.color;
    }
}
