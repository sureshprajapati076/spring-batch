package com.example.batch;


import com.example.batch.domain.PersonDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements RowMapper<PersonDto> {


    @Override
    public PersonDto mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new PersonDto(rs.getInt("id"), rs.getString("name"),"empty Note");
    }
}
