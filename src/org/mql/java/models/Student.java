package org.mql.java.models;


public class Student extends Person implements Printable {
    private String university;
    private double gpa;

    public Student(String name, int age, String university) {
        super(name, age);
        this.university = university;
    }

    @Override
    public void print() {
        System.out.println("Student: " + getName() + " from " + university);
    }

    public String getUniversity() {
        return university;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }
}
