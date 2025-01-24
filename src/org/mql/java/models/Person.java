package org.mql.java.models;


public abstract class Person implements Identifiable {
    private String name;
    private int age;
    private String id;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}