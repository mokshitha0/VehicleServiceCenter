package jdbc;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
	public class DatabaseOperations {

	    public static void createTables(Connection con) {

	        try {

	            Statement stmt = con.createStatement();

	            // Customers table
	            String customersTable = """
	                    CREATE TABLE IF NOT EXISTS customers (
	                        customer_id INT PRIMARY KEY AUTO_INCREMENT,
	                        name VARCHAR(100) NOT NULL,
	                        email VARCHAR(100) NOT NULL UNIQUE,
	                        phone VARCHAR(10) NOT NULL
	                    )
	                    """;

	            stmt.executeUpdate(customersTable);

	            // Vehicles table
	            String vehiclesTable = """
	                    CREATE TABLE IF NOT EXISTS vehicles (
	                        vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
	                        customer_id INT NOT NULL,
	                        vehicle_number VARCHAR(20) NOT NULL UNIQUE,
	                        vehicle_model VARCHAR(100) NOT NULL,
	                        vehicle_type VARCHAR(50) NOT NULL,

	                        FOREIGN KEY (customer_id)
	                            REFERENCES customers(customer_id)
	                    )
	                    """;

	            stmt.executeUpdate(vehiclesTable);

	            // Service records table
	            String serviceRecordsTable = """
	                    CREATE TABLE IF NOT EXISTS service_records (
	                        service_id INT PRIMARY KEY AUTO_INCREMENT,
	                        vehicle_id INT NOT NULL,
	                        service_date DATE NOT NULL,
	                        service_type VARCHAR(100) NOT NULL,
	                        description VARCHAR(255),
	                        service_cost DECIMAL(10,2) NOT NULL CHECK (service_cost > 0),
	                        status VARCHAR(20) DEFAULT 'BOOKED',

	                        FOREIGN KEY (vehicle_id)
	                            REFERENCES vehicles(vehicle_id)
	                    )
	                    """;

	            stmt.executeUpdate(serviceRecordsTable);

	            System.out.println("Tables created successfully!");

	        } catch (Exception e) {

	            e.printStackTrace();
	        }
	    }
	    public static int addCustomer(Connection con, Scanner sc) {

	        try {

	            String name;

	            while (true) {

	                System.out.print("Enter customer name: ");
	                name = sc.nextLine();

	                if (!validation.isNotEmpty(name)) {
	                    System.out.println("Name cannot be empty.");
	                }
	                else if (!validation.containsOnlyAlphabets(name)) {
	                    System.out.println(
	                            "Name should contain alphabets and spaces only."
	                    );
	                }
	                else {
	                    break;
	                }
	            }

	            String email;

	            while (true) {

	                System.out.print("Enter email: ");
	                email = sc.nextLine();

	                if (!validation.isNotEmpty(email)) {
	                    System.out.println("Email cannot be empty.");
	                }
	                else if (!validation.isValidEmail(email)) {
	                    System.out.println("Enter a valid email.");
	                }
	                else {
	                    break;
	                }
	            }

	            String phone;

	            while (true) {

	                System.out.print("Enter phone number: ");
	                phone = sc.nextLine();

	                if (!validation.isNotEmpty(phone)) {
	                    System.out.println("Phone number cannot be empty.");
	                }
	                else if (!validation.isValidPhone(phone)) {
	                    System.out.println(
	                            "Phone number should contain 10 digits and start with 6-9."
	                    );
	                }
	                else {
	                    break;
	                }
	            }

	            String sql =
	                    "INSERT INTO customers (name, email, phone) " +
	                    "VALUES (?, ?, ?)";

	            PreparedStatement ps =
	                    con.prepareStatement(
	                            sql,
	                            Statement.RETURN_GENERATED_KEYS
	                    );

	            ps.setString(1, name);
	            ps.setString(2, email);
	            ps.setString(3, phone);

	            ps.executeUpdate();

	            ResultSet rs = ps.getGeneratedKeys();

	            if (rs.next()) {

	                int customerId = rs.getInt(1);

	                System.out.println("Customer registered successfully!");
	                System.out.println("Your Customer ID: " + customerId);

	                return customerId;
	            }

	            return -1;

	        } catch (SQLIntegrityConstraintViolationException e) {

	            System.out.println("Email already exists.");
	            return -1;

	        } catch (Exception e) {

	            e.printStackTrace();
	            return -1;
	        }
	    }
	    public static void viewCustomers(Connection con) {

	        try {

	            String sql = "SELECT * FROM customers";

	            PreparedStatement ps = con.prepareStatement(sql);

	            ResultSet rs = ps.executeQuery();

	            boolean found = false;

	            while (rs.next()) {

	                found = true;

	                displayCustomer(rs);
	            }

	            if (!found) {
	                System.out.println("No customers found.");
	            }

	        } catch (Exception e) {

	            e.printStackTrace();
	        }
	    }
	    public static void displayCustomer(ResultSet rs) {

	        try {

	            System.out.println("--------------------------------------");

	            System.out.println(
	                    "Customer ID : " + rs.getInt("customer_id")
	            );

	            System.out.println(
	                    "Name        : " + rs.getString("name")
	            );

	            System.out.println(
	                    "Email       : " + rs.getString("email")
	            );

	            System.out.println(
	                    "Phone       : " + rs.getString("phone")
	            );

	            System.out.println("--------------------------------------");

	        } catch (Exception e) {

	            e.printStackTrace();
	        }
	    }
	    public static void updateCustomer(Connection con, Scanner sc) {

	        try {

	            System.out.print("Enter customer ID: ");
	            int customerId = sc.nextInt();
	            sc.nextLine();

	            System.out.println();
	            System.out.println("What do you want to update?");
	            System.out.println("1. Name");
	            System.out.println("2. Email");
	            System.out.println("3. Phone");
	            System.out.println("4. Cancel");

	            System.out.print("Enter your choice: ");
	            int choice = sc.nextInt();
	            sc.nextLine();

	            switch (choice) {

	                case 1:

	                    String name;

	                    while (true) {

	                        System.out.print("Enter new name: ");
	                        name = sc.nextLine();

	                        if (!validation.isNotEmpty(name)) {
	                            System.out.println("Name cannot be empty.");
	                        }
	                        else if (!validation.containsOnlyAlphabets(name)) {
	                            System.out.println(
	                                    "Name should contain alphabets and spaces only."
	                            );
	                        }
	                        else {
	                            break;
	                        }
	                    }

	                    String nameSql =
	                            "UPDATE customers SET name = ? " +
	                            "WHERE customer_id = ?";

	                    PreparedStatement namePs =
	                            con.prepareStatement(nameSql);

	                    namePs.setString(1, name);
	                    namePs.setInt(2, customerId);

	                    int nameRows = namePs.executeUpdate();

	                    if (nameRows > 0) {
	                        System.out.println("Name updated successfully!");
	                    }
	                    else {
	                        System.out.println("Customer not found.");
	                    }

	                    break;

	                case 2:

	                    String email;

	                    while (true) {

	                        System.out.print("Enter new email: ");
	                        email = sc.nextLine();

	                        if (!validation.isNotEmpty(email)) {
	                            System.out.println("Email cannot be empty.");
	                        }
	                        else if (!validation.isValidEmail(email)) {
	                            System.out.println("Enter a valid email.");
	                        }
	                        else {
	                            break;
	                        }
	                    }

	                    String emailSql =
	                            "UPDATE customers SET email = ? " +
	                            "WHERE customer_id = ?";

	                    PreparedStatement emailPs =
	                            con.prepareStatement(emailSql);

	                    emailPs.setString(1, email);
	                    emailPs.setInt(2, customerId);

	                    int emailRows = emailPs.executeUpdate();

	                    if (emailRows > 0) {
	                        System.out.println("Email updated successfully!");
	                    }
	                    else {
	                        System.out.println("Customer not found.");
	                    }

	                    break;

	                case 3:

	                    String phone;

	                    while (true) {

	                        System.out.print("Enter new phone number: ");
	                        phone = sc.nextLine();

	                        if (!validation.isNotEmpty(phone)) {
	                            System.out.println("Phone number cannot be empty.");
	                        }
	                        else if (!validation.isValidPhone(phone)) {
	                            System.out.println(
	                                    "Phone number should contain 10 digits and start with 6-9."
	                            );
	                        }
	                        else {
	                            break;
	                        }
	                    }

	                    String phoneSql =
	                            "UPDATE customers SET phone = ? " +
	                            "WHERE customer_id = ?";

	                    PreparedStatement phonePs =
	                            con.prepareStatement(phoneSql);

	                    phonePs.setString(1, phone);
	                    phonePs.setInt(2, customerId);

	                    int phoneRows = phonePs.executeUpdate();

	                    if (phoneRows > 0) {
	                        System.out.println("Phone updated successfully!");
	                    }
	                    else {
	                        System.out.println("Customer not found.");
	                    }

	                    break;

	                case 4:

	                    System.out.println("Update cancelled.");
	                    break;

	                default:

	                    System.out.println("Invalid choice.");
	            }

	        } catch (SQLIntegrityConstraintViolationException e) {

	            System.out.println("Email already exists.");

	        } catch (Exception e) {

	            e.printStackTrace();
	        }
	    }
	        public static void deleteCustomer(Connection con, Scanner sc) {

	            try {

	                System.out.print("Enter customer ID: ");
	                int customerId = sc.nextInt();
	                sc.nextLine();

	                String sql =
	                        "DELETE FROM customers WHERE customer_id = ?";

	                PreparedStatement ps = con.prepareStatement(sql);

	                ps.setInt(1, customerId);

	                int rows = ps.executeUpdate();

	                if (rows > 0) {
	                    System.out.println("Customer deleted successfully!");
	                }
	                else {
	                    System.out.println("Customer not found.");
	                }

	            } catch (SQLIntegrityConstraintViolationException e) {

	                System.out.println(
	                        "Customer cannot be deleted because vehicles are registered for this customer."
	                );

	            } catch (Exception e) {

	                e.printStackTrace();
	            }
	        
	    }
	        public static void addVehicle(Connection con, Scanner sc, int customerId) {

	            try {

	                // Check whether customer exists
	                String customerCheckSql =
	                        "SELECT customer_id FROM customers WHERE customer_id = ?";

	                PreparedStatement customerCheckPs =
	                        con.prepareStatement(customerCheckSql);

	                customerCheckPs.setInt(1, customerId);

	                ResultSet customerRs =
	                        customerCheckPs.executeQuery();

	                if (!customerRs.next()) {

	                    System.out.println("Customer not found.");
	                    return;
	                }

	                String vehicleNumber;

	                while (true) {

	                    System.out.print("Enter vehicle number: ");
	                    vehicleNumber = sc.nextLine();

	                    if (!validation.isNotEmpty(vehicleNumber)) {
	                        System.out.println("Vehicle number cannot be empty.");
	                    }
	                    else if (!validation.isValidAlphaNumeric(vehicleNumber)) {
	                        System.out.println(
	                                "Vehicle number should contain alphabets, numbers and spaces only."
	                        );
	                    }
	                    else {
	                        break;
	                    }
	                }

	                String vehicleModel;

	                while (true) {

	                    System.out.print("Enter vehicle model: ");
	                    vehicleModel = sc.nextLine();

	                    if (!validation.isNotEmpty(vehicleModel)) {
	                        System.out.println("Vehicle model cannot be empty.");
	                    }
	                    else if (!validation.isValidAlphaNumeric(vehicleModel)) {
	                        System.out.println(
	                                "Vehicle model should contain alphabets, numbers and spaces only."
	                        );
	                    }
	                    else {
	                        break;
	                    }
	                }

	                String vehicleType;

	                while (true) {

	                    System.out.print("Enter vehicle type: ");
	                    vehicleType = sc.nextLine();

	                    if (!validation.isNotEmpty(vehicleType)) {
	                        System.out.println("Vehicle type cannot be empty.");
	                    }
	                    else if (!validation.containsOnlyAlphabets(vehicleType)) {
	                        System.out.println(
	                                "Vehicle type should contain alphabets and spaces only."
	                        );
	                    }
	                    else {
	                        break;
	                    }
	                }

	                String sql =
	                        "INSERT INTO vehicles " +
	                        "(customer_id, vehicle_number, vehicle_model, vehicle_type) " +
	                        "VALUES (?, ?, ?, ?)";

	                PreparedStatement ps =
	                        con.prepareStatement(sql);

	                ps.setInt(1, customerId);
	                ps.setString(2, vehicleNumber);
	                ps.setString(3, vehicleModel);
	                ps.setString(4, vehicleType);

	                ps.executeUpdate();

	                System.out.println("Vehicle registered successfully!");

	            } catch (SQLIntegrityConstraintViolationException e) {

	                System.out.println("Vehicle number already exists.");

	            } catch (Exception e) {

	                e.printStackTrace();
	            }
	        }
	        public static void viewVehicles(Connection con) {

	            try {

	                String sql = "SELECT * FROM vehicles";

	                PreparedStatement ps =
	                        con.prepareStatement(sql);

	                ResultSet rs = ps.executeQuery();

	                boolean found = false;

	                while (rs.next()) {

	                    found = true;

	                    displayVehicle(rs);
	                }

	                if (!found) {
	                    System.out.println("No vehicles found.");
	                }

	            } catch (Exception e) {

	                e.printStackTrace();
	            }
	        }
	        public static void displayVehicle(ResultSet rs) {

	            try {

	                System.out.println("--------------------------------------");

	                System.out.println(
	                        "Vehicle ID     : " + rs.getInt("vehicle_id")
	                );

	                System.out.println(
	                        "Customer ID    : " + rs.getInt("customer_id")
	                );

	                System.out.println(
	                        "Vehicle Number : " + rs.getString("vehicle_number")
	                );

	                System.out.println(
	                        "Vehicle Model  : " + rs.getString("vehicle_model")
	                );

	                System.out.println(
	                        "Vehicle Type   : " + rs.getString("vehicle_type")
	                );

	                System.out.println("--------------------------------------");

	            } catch (Exception e) {

	                e.printStackTrace();
	            }
	        }
	        public static int findCustomer(Connection con, Scanner sc) {

	            try {

	                System.out.print("Enter customer email: ");
	                String email = sc.nextLine();

	                String sql =
	                        "SELECT * FROM customers WHERE email = ?";

	                PreparedStatement ps =
	                        con.prepareStatement(sql);

	                ps.setString(1, email);

	                ResultSet rs = ps.executeQuery();

	                if (rs.next()) {

	                    displayCustomer(rs);

	                    return rs.getInt("customer_id");

	                } else {

	                    System.out.println("Customer not found.");
	                    return -1;
	                }

	            } catch (Exception e) {

	                e.printStackTrace();
	                return -1;
	            }
	        }
	        public static void viewCustomerVehicles(Connection con, int customerId) {

	            try {

	                String sql =
	                        "SELECT * FROM vehicles WHERE customer_id = ?";

	                PreparedStatement ps =
	                        con.prepareStatement(sql);

	                ps.setInt(1, customerId);

	                ResultSet rs = ps.executeQuery();

	                boolean found = false;

	                while (rs.next()) {

	                    found = true;

	                    displayVehicle(rs);
	                }

	                if (!found) {

	                    System.out.println(
	                            "No vehicles registered for this customer."
	                    );
	                }

	            } catch (Exception e) {

	                e.printStackTrace();
	            }
	        }
	            public static void updateVehicle(Connection con, Scanner sc) {

	                try {

	                    System.out.print("Enter vehicle ID: ");
	                    int vehicleId = sc.nextInt();
	                    sc.nextLine();

	                    System.out.println();
	                    System.out.println("What do you want to update?");
	                    System.out.println("1. Vehicle Number");
	                    System.out.println("2. Vehicle Model");
	                    System.out.println("3. Vehicle Type");
	                    System.out.println("4. Cancel");

	                    System.out.print("Enter your choice: ");
	                    int choice = sc.nextInt();
	                    sc.nextLine();

	                    switch (choice) {

	                        case 1:

	                            String vehicleNumber;

	                            while (true) {

	                                System.out.print("Enter new vehicle number: ");
	                                vehicleNumber = sc.nextLine();

	                                if (!validation.isNotEmpty(vehicleNumber)) {
	                                    System.out.println("Vehicle number cannot be empty.");
	                                }
	                                else if (!validation.isValidAlphaNumeric(vehicleNumber)) {
	                                    System.out.println(
	                                            "Vehicle number should contain alphabets, numbers and spaces only."
	                                    );
	                                }
	                                else {
	                                    break;
	                                }
	                            }

	                            String numberSql =
	                                    "UPDATE vehicles SET vehicle_number = ? " +
	                                    "WHERE vehicle_id = ?";

	                            PreparedStatement numberPs =
	                                    con.prepareStatement(numberSql);

	                            numberPs.setString(1, vehicleNumber);
	                            numberPs.setInt(2, vehicleId);

	                            int numberRows = numberPs.executeUpdate();

	                            if (numberRows > 0) {
	                                System.out.println("Vehicle number updated successfully!");
	                            }
	                            else {
	                                System.out.println("Vehicle not found.");
	                            }

	                            break;

	                        case 2:

	                            String vehicleModel;

	                            while (true) {

	                                System.out.print("Enter new vehicle model: ");
	                                vehicleModel = sc.nextLine();

	                                if (!validation.isNotEmpty(vehicleModel)) {
	                                    System.out.println("Vehicle model cannot be empty.");
	                                }
	                                else if (!validation.isValidAlphaNumeric(vehicleModel)) {
	                                    System.out.println(
	                                            "Vehicle model should contain alphabets, numbers and spaces only."
	                                    );
	                                }
	                                else {
	                                    break;
	                                }
	                            }

	                            String modelSql =
	                                    "UPDATE vehicles SET vehicle_model = ? " +
	                                    "WHERE vehicle_id = ?";

	                            PreparedStatement modelPs =
	                                    con.prepareStatement(modelSql);

	                            modelPs.setString(1, vehicleModel);
	                            modelPs.setInt(2, vehicleId);

	                            int modelRows = modelPs.executeUpdate();

	                            if (modelRows > 0) {
	                                System.out.println("Vehicle model updated successfully!");
	                            }
	                            else {
	                                System.out.println("Vehicle not found.");
	                            }

	                            break;

	                        case 3:

	                            String vehicleType;

	                            while (true) {

	                                System.out.print("Enter new vehicle type: ");
	                                vehicleType = sc.nextLine();

	                                if (!validation.isNotEmpty(vehicleType)) {
	                                    System.out.println("Vehicle type cannot be empty.");
	                                }
	                                else if (!validation.containsOnlyAlphabets(vehicleType)) {
	                                    System.out.println(
	                                            "Vehicle type should contain alphabets and spaces only."
	                                    );
	                                }
	                                else {
	                                    break;
	                                }
	                            }

	                            String typeSql =
	                                    "UPDATE vehicles SET vehicle_type = ? " +
	                                    "WHERE vehicle_id = ?";

	                            PreparedStatement typePs =
	                                    con.prepareStatement(typeSql);

	                            typePs.setString(1, vehicleType);
	                            typePs.setInt(2, vehicleId);

	                            int typeRows = typePs.executeUpdate();

	                            if (typeRows > 0) {
	                                System.out.println("Vehicle type updated successfully!");
	                            }
	                            else {
	                                System.out.println("Vehicle not found.");
	                            }

	                            break;

	                        case 4:

	                            System.out.println("Update cancelled.");
	                            break;

	                        default:

	                            System.out.println("Invalid choice.");
	                    }

	                } catch (SQLIntegrityConstraintViolationException e) {

	                    System.out.println("Vehicle number already exists.");

	                } catch (Exception e) {

	                    e.printStackTrace();
	                }
	           
	            }
	            public static void deleteVehicle(Connection con, Scanner sc) {

	                try {

	                    System.out.print("Enter vehicle ID: ");
	                    int vehicleId = sc.nextInt();
	                    sc.nextLine();

	                    String sql =
	                            "DELETE FROM vehicles WHERE vehicle_id = ?";

	                    PreparedStatement ps = con.prepareStatement(sql);

	                    ps.setInt(1, vehicleId);

	                    int rows = ps.executeUpdate();

	                    if (rows > 0) {
	                        System.out.println("Vehicle deleted successfully!");
	                    }
	                    else {
	                        System.out.println("Vehicle not found.");
	                    }

	                } catch (SQLIntegrityConstraintViolationException e) {

	                    System.out.println(
	                            "Vehicle cannot be deleted because service records exist for this vehicle."
	                    );

	                } catch (Exception e) {

	                    e.printStackTrace();
	                }
	            }
	            public static void bookService(Connection con, Scanner sc, int customerId) {

	                try {

	                	System.out.print("Enter vehicle ID: ");
	                	int vehicleId = sc.nextInt();
	                	sc.nextLine();

	                	// Check whether vehicle belongs to this customer
	                	String vehicleCheckSql =
	                	        "SELECT vehicle_id FROM vehicles " +
	                	        "WHERE vehicle_id = ? AND customer_id = ?";
	                    PreparedStatement vehicleCheckPs =
	                            con.prepareStatement(vehicleCheckSql);

	                    vehicleCheckPs.setInt(1, vehicleId);
	                    vehicleCheckPs.setInt(2, customerId);

	                    ResultSet vehicleRs =
	                            vehicleCheckPs.executeQuery();

	                    if (!vehicleRs.next()) {

	                        System.out.println("Vehicle not found.");
	                        return;
	                    }

	                    // Display available services
	                    System.out.println();
	                    System.out.println("========================================");
	                    System.out.println("        AVAILABLE SERVICES");
	                    System.out.println("========================================");
	                    System.out.println("1. Car Wash          - ₹500");
	                    System.out.println("2. Oil Change        - ₹1000");
	                    System.out.println("3. General Service   - ₹2500");
	                    System.out.println("4. Brake Service     - ₹2000");
	                    System.out.println("5. Engine Service    - ₹5000");
	                    System.out.println("6. Tire Service      - ₹1500");
	                    System.out.println("7. Other Service     - ₹3000");
	                    System.out.println("========================================");

	                    System.out.print("Enter your service choice: ");
	                    int serviceChoice = sc.nextInt();
	                    sc.nextLine();

	                    String serviceType;
	                    double serviceCost;

	                    switch (serviceChoice) {

	                        case 1:
	                            serviceType = "Car Wash";
	                            serviceCost = 500;
	                            break;

	                        case 2:
	                            serviceType = "Oil Change";
	                            serviceCost = 1000;
	                            break;

	                        case 3:
	                            serviceType = "General Service";
	                            serviceCost = 2500;
	                            break;

	                        case 4:
	                            serviceType = "Brake Service";
	                            serviceCost = 2000;
	                            break;

	                        case 5:
	                            serviceType = "Engine Service";
	                            serviceCost = 5000;
	                            break;

	                        case 6:
	                            serviceType = "Tire Service";
	                            serviceCost = 1500;
	                            break;

	                        case 7:
	                            serviceType = "Other Service";
	                            serviceCost = 3000;
	                            break;

	                        default:
	                            System.out.println("Invalid service choice.");
	                            return;
	                    }

	                    String serviceDate;

	                    while (true) {

	                        System.out.print("Enter service date (yyyy-MM-dd): ");
	                        serviceDate = sc.nextLine();

	                        if (!validation.isNotEmpty(serviceDate)) {
	                            System.out.println("Service date cannot be empty.");
	                        }
	                        else if (!validation.isValidDate(serviceDate)) {
	                            System.out.println(
	                                    "Enter date in yyyy-MM-dd format."
	                            );
	                        }
	                        else {
	                            break;
	                        }
	                    }

	                    String description;

	                    while (true) {

	                        System.out.print("Enter service description: ");
	                        description = sc.nextLine();

	                        if (!validation.isNotEmpty(description)) {
	                            System.out.println("Description cannot be empty.");
	                        }
	                        else {
	                            break;
	                        }
	                    }

	                    String sql =
	                            "INSERT INTO service_records " +
	                            "(vehicle_id, service_date, service_type, description, service_cost) " +
	                            "VALUES (?, ?, ?, ?, ?)";

	                    PreparedStatement ps =
	                            con.prepareStatement(sql);

	                    ps.setInt(1, vehicleId);
	                    ps.setDate(
	                            2,
	                            java.sql.Date.valueOf(serviceDate)
	                    );
	                    ps.setString(3, serviceType);
	                    ps.setString(4, description);
	                    ps.setDouble(5, serviceCost);

	                    ps.executeUpdate();

	                    System.out.println();
	                    System.out.println("Service booked successfully!");
	                    System.out.println("Service Type : " + serviceType);
	                    System.out.println("Service Cost : ₹" + serviceCost);

	                } catch (IllegalArgumentException e) {

	                    System.out.println(
	                            "Invalid date. Please enter a valid date in yyyy-MM-dd format."
	                    );

	                } catch (Exception e) {

	                    e.printStackTrace();
	                }
	            }
	            
	                        
	            public static void viewServiceHistory(Connection con) {

	                try {

	                    String sql =
	                            "SELECT c.name, " +
	                            "v.vehicle_number, " +
	                            "v.vehicle_model, " +
	                            "s.service_id, " +
	                            "s.service_date, " +
	                            "s.service_type, " +
	                            "s.description, " +
	                            "s.service_cost, " +
	                            "s.status " +
	                            "FROM customers c " +
	                            "JOIN vehicles v " +
	                            "ON c.customer_id = v.customer_id " +
	                            "JOIN service_records s " +
	                            "ON v.vehicle_id = s.vehicle_id " +
	                            "ORDER BY s.service_date DESC";

	                    PreparedStatement ps =
	                            con.prepareStatement(sql);

	                    ResultSet rs = ps.executeQuery();

	                    boolean found = false;

	                    while (rs.next()) {

	                        found = true;

	                        displayServiceHistory(rs);
	                    }

	                    if (!found) {
	                        System.out.println("No service records found.");
	                    }

	                } catch (Exception e) {

	                    e.printStackTrace();
	                }
	            }
	            public static void displayServiceHistory(ResultSet rs) {

	                try {

	                    System.out.println("======================================");

	                    System.out.println(
	                            "Service ID    : " + rs.getInt("service_id")
	                    );

	                    System.out.println(
	                            "Customer Name : " + rs.getString("name")
	                    );

	                    System.out.println(
	                            "Vehicle Number: " + rs.getString("vehicle_number")
	                    );

	                    System.out.println(
	                            "Vehicle Model : " + rs.getString("vehicle_model")
	                    );

	                    System.out.println(
	                            "Service Date  : " + rs.getDate("service_date")
	                    );

	                    System.out.println(
	                            "Service Type  : " + rs.getString("service_type")
	                    );

	                    System.out.println(
	                            "Description   : " + rs.getString("description")
	                    );

	                    System.out.println(
	                            "Service Cost  : " + rs.getDouble("service_cost")
	                    );

	                    System.out.println(
	                            "Status        : " + rs.getString("status")
	                    );

	                    System.out.println("======================================");

	                } catch (Exception e) {

	                    e.printStackTrace();
	                }
	            }
	            public static void searchService(Connection con, Scanner sc) {

	                try {

	                    String vehicleNumber;

	                    while (true) {

	                        System.out.print("Enter vehicle number to search: ");
	                        vehicleNumber = sc.nextLine();

	                        if (!validation.isNotEmpty(vehicleNumber)) {
	                            System.out.println("Vehicle number cannot be empty.");
	                        }
	                        else if (!validation.isValidAlphaNumeric(vehicleNumber)) {
	                            System.out.println(
	                                    "Vehicle number should contain alphabets, numbers and spaces only."
	                            );
	                        }
	                        else {
	                            break;
	                        }
	                    }

	                    String sql =
	                            "SELECT c.name, " +
	                            "v.vehicle_number, " +
	                            "v.vehicle_model, " +
	                            "s.service_id, " +
	                            "s.service_date, " +
	                            "s.service_type, " +
	                            "s.description, " +
	                            "s.service_cost, " +
	                            "s.status " +
	                            "FROM customers c " +
	                            "JOIN vehicles v " +
	                            "ON c.customer_id = v.customer_id " +
	                            "JOIN service_records s " +
	                            "ON v.vehicle_id = s.vehicle_id " +
	                            "WHERE v.vehicle_number = ? " +
	                            "ORDER BY s.service_date DESC";

	                    PreparedStatement ps =
	                            con.prepareStatement(sql);

	                    ps.setString(1, vehicleNumber);

	                    ResultSet rs = ps.executeQuery();

	                    boolean found = false;

	                    while (rs.next()) {

	                        found = true;

	                        displayServiceHistory(rs);
	                    }

	                    if (!found) {
	                        System.out.println(
	                                "No service records found for this vehicle."
	                        );
	                    }

	                } catch (Exception e) {

	                    e.printStackTrace();
	                }
	            }
	            public static void updateService(Connection con, Scanner sc) {

	                try {

	                    System.out.print("Enter service ID: ");
	                    int serviceId = sc.nextInt();
	                    sc.nextLine();

	                    System.out.println();
	                    System.out.println("What do you want to update?");
	                    System.out.println("1. Service Date");
	                    System.out.println("2. Service Type");
	                    System.out.println("3. Description");
	                    System.out.println("4. Status");
	                    System.out.println("5. Cancel");

	                    System.out.print("Enter your choice: ");
	                    int choice = sc.nextInt();
	                    sc.nextLine();

	                    switch (choice) {

	                        case 1:

	                            String serviceDate;

	                            while (true) {

	                                System.out.print("Enter new service date (yyyy-MM-dd): ");
	                                serviceDate = sc.nextLine();

	                                if (!validation.isNotEmpty(serviceDate)) {
	                                    System.out.println("Service date cannot be empty.");
	                                }
	                                else if (!validation.isValidDate(serviceDate)) {
	                                    System.out.println(
	                                            "Enter date in yyyy-MM-dd format."
	                                    );
	                                }
	                                else {
	                                    break;
	                                }
	                            }

	                            String dateSql =
	                                    "UPDATE service_records SET service_date = ? " +
	                                    "WHERE service_id = ?";

	                            PreparedStatement datePs =
	                                    con.prepareStatement(dateSql);

	                            datePs.setDate(
	                                    1,
	                                    java.sql.Date.valueOf(serviceDate)
	                            );

	                            datePs.setInt(2, serviceId);

	                            int dateRows = datePs.executeUpdate();

	                            if (dateRows > 0) {
	                                System.out.println("Service date updated successfully!");
	                            }
	                            else {
	                                System.out.println("Service record not found.");
	                            }

	                            break;

	                        case 2:

	                            System.out.println();
	                            System.out.println("========================================");
	                            System.out.println("        AVAILABLE SERVICES");
	                            System.out.println("========================================");
	                            System.out.println("1. Car Wash          - ₹500");
	                            System.out.println("2. Oil Change        - ₹1000");
	                            System.out.println("3. General Service   - ₹2500");
	                            System.out.println("4. Brake Service     - ₹2000");
	                            System.out.println("5. Engine Service    - ₹5000");
	                            System.out.println("6. Tire Service      - ₹1500");
	                            System.out.println("7. Other Service     - ₹3000");
	                            System.out.println("========================================");

	                            System.out.print("Enter new service choice: ");
	                            int serviceChoice = sc.nextInt();
	                            sc.nextLine();

	                            String serviceType;
	                            double serviceCost;

	                            switch (serviceChoice) {

	                                case 1:
	                                    serviceType = "Car Wash";
	                                    serviceCost = 500;
	                                    break;

	                                case 2:
	                                    serviceType = "Oil Change";
	                                    serviceCost = 1000;
	                                    break;

	                                case 3:
	                                    serviceType = "General Service";
	                                    serviceCost = 2500;
	                                    break;

	                                case 4:
	                                    serviceType = "Brake Service";
	                                    serviceCost = 2000;
	                                    break;

	                                case 5:
	                                    serviceType = "Engine Service";
	                                    serviceCost = 5000;
	                                    break;

	                                case 6:
	                                    serviceType = "Tire Service";
	                                    serviceCost = 1500;
	                                    break;

	                                case 7:
	                                    serviceType = "Other Service";
	                                    serviceCost = 3000;
	                                    break;

	                                default:
	                                    System.out.println("Invalid service choice.");
	                                    return;
	                            }

	                            String typeSql =
	                                    "UPDATE service_records " +
	                                    "SET service_type = ?, service_cost = ? " +
	                                    "WHERE service_id = ?";

	                            PreparedStatement typePs =
	                                    con.prepareStatement(typeSql);

	                            typePs.setString(1, serviceType);
	                            typePs.setDouble(2, serviceCost);
	                            typePs.setInt(3, serviceId);

	                            int typeRows = typePs.executeUpdate();

	                            if (typeRows > 0) {
	                                System.out.println("Service type updated successfully!");
	                                System.out.println("Service cost updated automatically to ₹"
	                                        + serviceCost);
	                            }
	                            else {
	                                System.out.println("Service record not found.");
	                            }

	                            break;

	                        case 3:

	                            String description;

	                            while (true) {

	                                System.out.print("Enter new description: ");
	                                description = sc.nextLine();

	                                if (!validation.isNotEmpty(description)) {
	                                    System.out.println("Description cannot be empty.");
	                                }
	                                else {
	                                    break;
	                                }
	                            }

	                            String descriptionSql =
	                                    "UPDATE service_records SET description = ? " +
	                                    "WHERE service_id = ?";

	                            PreparedStatement descriptionPs =
	                                    con.prepareStatement(descriptionSql);

	                            descriptionPs.setString(1, description);
	                            descriptionPs.setInt(2, serviceId);

	                            int descriptionRows =
	                                    descriptionPs.executeUpdate();

	                            if (descriptionRows > 0) {
	                                System.out.println("Description updated successfully!");
	                            }
	                            else {
	                                System.out.println("Service record not found.");
	                            }

	                            break;

	                        case 4:

	                            String status;

	                            while (true) {

	                                System.out.print(
	                                        "Enter new status (BOOKED/COMPLETED/CANCELLED): "
	                                );

	                                status = sc.nextLine();

	                                if (!validation.isNotEmpty(status)) {
	                                    System.out.println("Status cannot be empty.");
	                                }
	                                else if (!validation.isValidStatus(status)) {
	                                    System.out.println(
	                                            "Status must be BOOKED, COMPLETED or CANCELLED."
	                                    );
	                                }
	                                else {
	                                    break;
	                                }
	                            }

	                            String statusSql =
	                                    "UPDATE service_records SET status = ? " +
	                                    "WHERE service_id = ?";

	                            PreparedStatement statusPs =
	                                    con.prepareStatement(statusSql);

	                            statusPs.setString(1, status.toUpperCase());
	                            statusPs.setInt(2, serviceId);

	                            int statusRows = statusPs.executeUpdate();

	                            if (statusRows > 0) {
	                                System.out.println("Status updated successfully!");
	                            }
	                            else {
	                                System.out.println("Service record not found.");
	                            }

	                            break;

	                        case 5:

	                            System.out.println("Update cancelled.");
	                            break;

	                        default:

	                            System.out.println("Invalid choice.");
	                    }

	                } catch (IllegalArgumentException e) {

	                    System.out.println(
	                            "Invalid date. Please enter a valid date."
	                    );

	                } catch (Exception e) {

	                    e.printStackTrace();
	                }
	            
	            }
	            public static void deleteService(Connection con, Scanner sc) {

	                try {

	                    System.out.print("Enter service ID: ");
	                    int serviceId = sc.nextInt();
	                    sc.nextLine();

	                    String sql =
	                            "DELETE FROM service_records WHERE service_id = ?";

	                    PreparedStatement ps =
	                            con.prepareStatement(sql);

	                    ps.setInt(1, serviceId);

	                    int rows = ps.executeUpdate();

	                    if (rows > 0) {
	                        System.out.println("Service record deleted successfully!");
	                    }
	                    else {
	                        System.out.println("Service record not found.");
	                    }

	                } catch (Exception e) {

	                    e.printStackTrace();
	                }
	            }
	            public static void batchAddServiceRecords(Connection con, Scanner sc) {

	                try {

	                    System.out.print("Enter number of service records to add: ");
	                    int count = sc.nextInt();
	                    sc.nextLine();

	                    if (count <= 0) {
	                        System.out.println("Number of records should be greater than 0.");
	                        return;
	                    }

	                    String vehicleCheckSql =
	                            "SELECT vehicle_id FROM vehicles WHERE vehicle_id = ?";

	                    String sql =
	                            "INSERT INTO service_records " +
	                            "(vehicle_id, service_date, service_type, description, service_cost) " +
	                            "VALUES (?, ?, ?, ?, ?)";

	                    PreparedStatement vehicleCheckPs =
	                            con.prepareStatement(vehicleCheckSql);

	                    PreparedStatement ps =
	                            con.prepareStatement(sql);

	                    con.setAutoCommit(false);

	                    for (int i = 1; i <= count; i++) {

	                        System.out.println();
	                        System.out.println("========================================");
	                        System.out.println("Service Record " + i);
	                        System.out.println("========================================");

	                        System.out.print("Enter vehicle ID: ");
	                        int vehicleId = sc.nextInt();
	                        sc.nextLine();

	                        vehicleCheckPs.setInt(1, vehicleId);

	                        ResultSet vehicleRs =
	                                vehicleCheckPs.executeQuery();

	                        if (!vehicleRs.next()) {

	                            System.out.println("Vehicle not found.");
	                            System.out.println("Batch operation cancelled.");

	                            con.rollback();
	                            return;
	                        }

	                        System.out.println();
	                        System.out.println("AVAILABLE SERVICES");
	                        System.out.println("1. Car Wash          - ₹500");
	                        System.out.println("2. Oil Change        - ₹1000");
	                        System.out.println("3. General Service   - ₹2500");
	                        System.out.println("4. Brake Service     - ₹2000");
	                        System.out.println("5. Engine Service    - ₹5000");
	                        System.out.println("6. Tire Service      - ₹1500");
	                        System.out.println("7. Other Service     - ₹3000");

	                        System.out.print("Enter service choice: ");
	                        int serviceChoice = sc.nextInt();
	                        sc.nextLine();

	                        String serviceType;
	                        double serviceCost;

	                        switch (serviceChoice) {

	                            case 1:
	                                serviceType = "Car Wash";
	                                serviceCost = 500;
	                                break;

	                            case 2:
	                                serviceType = "Oil Change";
	                                serviceCost = 1000;
	                                break;

	                            case 3:
	                                serviceType = "General Service";
	                                serviceCost = 2500;
	                                break;

	                            case 4:
	                                serviceType = "Brake Service";
	                                serviceCost = 2000;
	                                break;

	                            case 5:
	                                serviceType = "Engine Service";
	                                serviceCost = 5000;
	                                break;

	                            case 6:
	                                serviceType = "Tire Service";
	                                serviceCost = 1500;
	                                break;

	                            case 7:
	                                serviceType = "Other Service";
	                                serviceCost = 3000;
	                                break;

	                            default:
	                                System.out.println("Invalid service choice.");
	                                con.rollback();
	                                return;
	                        }

	                        String serviceDate;

	                        while (true) {

	                            System.out.print("Enter service date (yyyy-MM-dd): ");
	                            serviceDate = sc.nextLine();

	                            if (!validation.isNotEmpty(serviceDate)) {
	                                System.out.println("Service date cannot be empty.");
	                            }
	                            else if (!validation.isValidDate(serviceDate)) {
	                                System.out.println(
	                                        "Enter date in yyyy-MM-dd format."
	                                );
	                            }
	                            else {
	                                break;
	                            }
	                        }

	                        String description;

	                        while (true) {

	                            System.out.print("Enter service description: ");
	                            description = sc.nextLine();

	                            if (!validation.isNotEmpty(description)) {
	                                System.out.println("Description cannot be empty.");
	                            }
	                            else {
	                                break;
	                            }
	                        }

	                        ps.setInt(1, vehicleId);
	                        ps.setDate(
	                                2,
	                                java.sql.Date.valueOf(serviceDate)
	                        );
	                        ps.setString(3, serviceType);
	                        ps.setString(4, description);
	                        ps.setDouble(5, serviceCost);

	                        ps.addBatch();
	                    }

	                    ps.executeBatch();

	                    con.commit();

	                    System.out.println();
	                    System.out.println(
	                            "All service records added successfully!"
	                    );

	                } catch (IllegalArgumentException e) {

	                    try {
	                        con.rollback();
	                    } catch (Exception rollbackException) {
	                        rollbackException.printStackTrace();
	                    }

	                    System.out.println(
	                            "Invalid date. Please enter a valid date."
	                    );

	                } catch (Exception e) {

	                    try {
	                        con.rollback();
	                        System.out.println(
	                                "Batch operation failed. All changes rolled back."
	                        );
	                    } catch (Exception rollbackException) {
	                        rollbackException.printStackTrace();
	                    }

	                    e.printStackTrace();

	                } finally {

	                    try {
	                        con.setAutoCommit(true);
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	 }
	        
	
