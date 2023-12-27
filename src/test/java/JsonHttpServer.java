import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class JsonHttpServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        // Create an HTTP server that listens on the specified port
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Create a context for the "/json" path and set a handler for it
        server.createContext("/json", new JsonHandler());

        // Start the server
        server.start();

        System.out.println("Server is running on port " + port);
    }

    // Custom handler for the "/json" path
    static class JsonHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Create a list of sample Person objects
            List<Person> people = new ArrayList<>();
            people.add(new Person("John Doe", 30, "New York", new Address("123 Main St", "Apt 456", "NY", "12345")));
            people.add(new Person("Jane Smith", 25, "Los Angeles", new Address("456 Oak St", "Unit 789", "CA", "56789")));

            // ObjectMapper for writing JSON
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert the list of Person objects to a JSON string
            String jsonString = objectMapper.writeValueAsString(people);

            // Set the response headers
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonString.length());

            // Get the output stream and write the JSON response
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonString.getBytes());
            }
        }
    }

    // Sample Person class with Address
    static class Person {
        private String name;
        private int age;
        private String city;
        private Address address;

        public Person(String name, int age, String city, Address address) {
            this.name = name;
            this.age = age;
            this.city = city;
            this.address = address;
        }

        // Getters and setters (or lombok annotations)...

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    // Sample Address class
    static class Address {
        private String street;
        private String apt;
        private String state;
        private String zipCode;

        public Address(String street, String apt, String state, String zipCode) {
            this.street = street;
            this.apt = apt;
            this.state = state;
            this.zipCode = zipCode;
        }

        // Getters and setters (or lombok annotations)...

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getApt() {
            return apt;
        }

        public void setApt(String apt) {
            this.apt = apt;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }
    }
}
