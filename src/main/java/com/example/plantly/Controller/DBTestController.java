package com.example.plantly.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@RestController
public class DBTestController{

        @Autowired
        DataSource dataSource;

        @RequestMapping(value = "/dbtest")
        public String testDb() throws SQLException {
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1+1")) {
                rs.next();
                int two = rs.getInt(1);
                return "Database connectivity seems " + (two == 2 ? "OK." : "weird!");
            }
        }

}
