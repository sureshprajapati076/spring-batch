package com.example.batch;


import com.example.batch.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      return User.builder()
              .id(rs.getLong("ID"))
              .dob(rs.getString("DOB"))
              .phone(rs.getString("PHONE"))
              .age(rs.getInt("AGE"))
              .address(rs.getString("ADDRESS"))
              .city(rs.getString("CITY"))
              .fname(rs.getString("FNAME"))
              .lname(rs.getString("LNAME"))
              .state(rs.getString("STATE"))
              .title(rs.getString("TITLE"))
              .email(rs.getString("EMAIL"))
              .country(rs.getString("COUNTRY"))
              .postalCode(rs.getString("POSTAL_CODE"))
              .gender(rs.getString("GENDER"))
              .build();
    }
}
