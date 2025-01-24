package org.mql.java.models;


public class Employee extends Person {
    private String company;
    private double salary;
    private UserType employeeType;

    public Employee(String name, int age, String company, double salary) {
        super(name, age);
        this.company = company;
        this.salary = salary;
        this.employeeType = UserType.FULL_TIME;
    }

    public String getCompany() {
        return company;
    }

    public double getSalary() {
        return salary;
    }
}