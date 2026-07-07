package com.example.kucingputeh.model;

public class User {
    private int id;
    private String email;
    private String username;
    private String password;
    private String token;
    private String lease;
    private String role;
    private int is_active;
    private String secret;
    private String phone;
    private String plateNumber;
    private String vehicleModel;


    public User() {
    }


    public User(String name, String email, String password, String plate, String model, String phone) {
        this.username = name;
        this.email = email;
        this.password = password;
        this.plateNumber = plate;
        this.vehicleModel = model;
        this.phone = phone;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}