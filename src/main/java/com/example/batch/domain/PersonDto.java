package com.example.batch.domain;

public class PersonDto {
    private Integer id;
    private String name;

    private String note;

    public PersonDto() {
    }

    public PersonDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public PersonDto(Integer id, String name, String note) {
        this.id = id;
        this.name = name;
        this.note = note;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "PersonDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
