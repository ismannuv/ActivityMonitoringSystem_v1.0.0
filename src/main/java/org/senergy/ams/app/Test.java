package org.senergy.ams.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.*;

public class Test {
    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://192.168.1.63:3306/ams_db_v1_0_0";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "sipl";
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        // SQL query to be executed
        String qry = "select count(*) as cnt from (SELECT u.id,u.name,u.emailId,u.mobileNo,u.validFrom,u.validUpto,pg.name AS 'role' FROM user u\n" +
                "LEFT JOIN privilegegroup pg ON pg.id=u.privilegeGroupId\n" +
                " where u.privilegeGroupId not in(0,1))a";

        try {
                System.out.println("######111 :"+System.currentTimeMillis());
                // Establish a connection to the database
                Connection  connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);

                // Create a PreparedStatement with placeholders (?)
                Statement preparedStatement = connection.createStatement();
                System.out.println("######222 :"+System.currentTimeMillis());



            // Execute the query and obtain the result set
            try (ResultSet resultSet = preparedStatement.executeQuery(qry)) {
                System.out.println("######333 :"+System.currentTimeMillis());
                // Process the result set
                while (resultSet.next()) {
                    int cnt = resultSet.getInt("cnt");


                    // Print or process the retrieved data
                    System.out.println("row cnt : "+cnt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("id",1);
        objectNode.put("id",232);
        objectNode.put("operation",4);
        objectNode.put("name","mannu");

        ObjectNode objectNode1 = objectMapper.createObjectNode();
        objectNode1.put("id",1);
        objectNode1.put("name","mannu1");

        ObjectNode objectNode2 = objectMapper.createObjectNode();
        objectNode2.put("id",1);
        objectNode2.put("name","mannu2");

        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.add(objectNode1);
        arrayNode.add(objectNode2);

//        objectNode.set("data",objectNode1);
//        objectNode.set("data2",objectNode2);
        objectNode.set("data2",arrayNode);
        System.out.println(objectNode);
    }
}
