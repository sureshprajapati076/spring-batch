package com.example.batch.domain;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class User {

    private Long id;
    private String gender;
    private String fname;
    private String lname;
    private String title;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String email;
    private String dob;
    private Integer age;
    private String phone;
    private String comments;

}
