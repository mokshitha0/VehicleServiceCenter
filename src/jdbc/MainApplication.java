package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class MainApplication {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/vehicle_service_db";
        String username = "root";
        String password = "Mokshitha@123";

        try {

            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection only once
            Connection con =
                    DriverManager.getConnection(url, username, password);

            System.out.println("Database connection successful!");

            // Create tables before showing menu
            DatabaseOperations.createTables(con);

            Scanner sc = new Scanner(System.in);

            boolean running = true;

            while (running) {

                System.out.println();
                System.out.println("========================================");
                System.out.println("   VEHICLE SERVICE CENTER MANAGEMENT");
                System.out.println("========================================");
                System.out.println("1. Customer Registration");
                System.out.println("2. Vehicle Registration");
                System.out.println("3. Service Booking");
                System.out.println("4. Service History");
                System.out.println("5. Search Service");
                System.out.println("6. Customer Management");
                System.out.println("7. Vehicle Management");
                System.out.println("8. Service Management");
                System.out.println("9. Exit");
                System.out.println("========================================");

                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    // =========================================
                    // 1. CUSTOMER REGISTRATION
                    // =========================================

                    case 1:

                        boolean customerRunning = true;

                        while (customerRunning) {

                            System.out.println();
                            System.out.println("========================================");
                            System.out.println("      CUSTOMER REGISTRATION");
                            System.out.println("========================================");
                            System.out.println("1. New Customer");
                            System.out.println("2. Existing Customer");
                            System.out.println("3. Back");

                            System.out.print("Enter your choice: ");
                            int customerChoice = sc.nextInt();
                            sc.nextLine();

                            switch (customerChoice) {

                                case 1:

                                    int customerId =
                                            DatabaseOperations.addCustomer(
                                                    con,
                                                    sc
                                            );

                                    if (customerId != -1) {

                                        System.out.println();
                                        System.out.println(
                                                "Do you want to register your vehicle now?"
                                        );
                                        System.out.println("1. Yes");
                                        System.out.println("2. No");

                                        System.out.print(
                                                "Enter your choice: "
                                        );

                                        int vehicleChoice =
                                                sc.nextInt();
                                        sc.nextLine();

                                        if (vehicleChoice == 1) {

                                            DatabaseOperations.addVehicle(
                                                    con,
                                                    sc,
                                                    customerId
                                            );

                                            System.out.println();
                                            System.out.println(
                                                    "Do you want to book a service now?"
                                            );
                                            System.out.println("1. Yes");
                                            System.out.println("2. No");

                                            System.out.print(
                                                    "Enter your choice: "
                                            );

                                            int serviceChoice =
                                                    sc.nextInt();
                                            sc.nextLine();

                                            if (serviceChoice == 1) {

                                                DatabaseOperations.bookService(
                                                        con,
                                                        sc,
                                                        customerId
                                                );
                                            }
                                        }
                                    }

                                    break;

                                case 2:

                                    int existingCustomerId =
                                            DatabaseOperations.findCustomer(
                                                    con,
                                                    sc
                                            );

                                    if (existingCustomerId != -1) {

                                        System.out.println();
                                        System.out.println(
                                                "Existing customer found."
                                        );
                                    }

                                    break;

                                case 3:

                                    customerRunning = false;
                                    break;

                                default:

                                    System.out.println(
                                            "Invalid choice. Please try again."
                                    );
                            }
                        }

                        break;

                    // =========================================
                    // 2. VEHICLE REGISTRATION
                    // =========================================

                    case 2:

                        System.out.println();
                        System.out.println("========================================");
                        System.out.println("       VEHICLE REGISTRATION");
                        System.out.println("========================================");

                        int vehicleCustomerId =
                                DatabaseOperations.findCustomer(
                                        con,
                                        sc
                                );

                        if (vehicleCustomerId != -1) {

                            DatabaseOperations.addVehicle(
                                    con,
                                    sc,
                                    vehicleCustomerId
                            );
                        }

                        break;

                    // =========================================
                    // 3. SERVICE BOOKING
                    // =========================================

                    case 3:

                        System.out.println();
                        System.out.println("========================================");
                        System.out.println("          SERVICE BOOKING");
                        System.out.println("========================================");

                        int serviceCustomerId =
                                DatabaseOperations.findCustomer(
                                        con,
                                        sc
                                );

                        if (serviceCustomerId != -1) {

                            System.out.println();
                            System.out.println("Registered Vehicles:");

                            DatabaseOperations.viewCustomerVehicles(
                                    con,
                                    serviceCustomerId
                            );

                            System.out.println();

                            DatabaseOperations.bookService(
                                    con,
                                    sc,
                                    serviceCustomerId
                            );
                        }

                        break;

                    // =========================================
                    // 4. SERVICE HISTORY
                    // =========================================

                    case 4:

                        System.out.println();
                        System.out.println("========================================");
                        System.out.println("          SERVICE HISTORY");
                        System.out.println("========================================");

                        DatabaseOperations.viewServiceHistory(
                                con
                        );

                        break;

                    // =========================================
                    // 5. SEARCH SERVICE
                    // =========================================

                    case 5:

                        System.out.println();
                        System.out.println("========================================");
                        System.out.println("           SEARCH SERVICE");
                        System.out.println("========================================");

                        DatabaseOperations.searchService(
                                con,
                                sc
                        );

                        break;

                    // =========================================
                    // 6. CUSTOMER MANAGEMENT
                    // =========================================

                    case 6:

                        boolean customerManagementRunning = true;

                        while (customerManagementRunning) {

                            System.out.println();
                            System.out.println("========================================");
                            System.out.println("       CUSTOMER MANAGEMENT");
                            System.out.println("========================================");
                            System.out.println("1. View Customers");
                            System.out.println("2. Update Customer");
                            System.out.println("3. Delete Customer");
                            System.out.println("4. Back");

                            System.out.print("Enter your choice: ");
                            int managementChoice =
                                    sc.nextInt();
                            sc.nextLine();

                            switch (managementChoice) {

                                case 1:

                                    DatabaseOperations.viewCustomers(
                                            con
                                    );

                                    break;

                                case 2:

                                    DatabaseOperations.updateCustomer(
                                            con,
                                            sc
                                    );

                                    break;

                                case 3:

                                    DatabaseOperations.deleteCustomer(
                                            con,
                                            sc
                                    );

                                    break;

                                case 4:

                                    customerManagementRunning = false;
                                    break;

                                default:

                                    System.out.println(
                                            "Invalid choice. Please try again."
                                    );
                            }
                        }

                        break;

                    // =========================================
                    // 7. VEHICLE MANAGEMENT
                    // =========================================

                    case 7:

                        boolean vehicleManagementRunning = true;

                        while (vehicleManagementRunning) {

                            System.out.println();
                            System.out.println("========================================");
                            System.out.println("        VEHICLE MANAGEMENT");
                            System.out.println("========================================");
                            System.out.println("1. View Vehicles");
                            System.out.println("2. Update Vehicle");
                            System.out.println("3. Delete Vehicle");
                            System.out.println("4. Back");

                            System.out.print("Enter your choice: ");
                            int vehicleManagementChoice =
                                    sc.nextInt();
                            sc.nextLine();

                            switch (vehicleManagementChoice) {

                                case 1:

                                    DatabaseOperations.viewVehicles(
                                            con
                                    );

                                    break;

                                case 2:

                                    DatabaseOperations.updateVehicle(
                                            con,
                                            sc
                                    );

                                    break;

                                case 3:

                                    DatabaseOperations.deleteVehicle(
                                            con,
                                            sc
                                    );

                                    break;

                                case 4:

                                    vehicleManagementRunning = false;
                                    break;

                                default:

                                    System.out.println(
                                            "Invalid choice. Please try again."
                                    );
                            }
                        }

                        break;

                    // =========================================
                    // 8. SERVICE MANAGEMENT
                    // =========================================

                    case 8:

                        boolean serviceManagementRunning = true;

                        while (serviceManagementRunning) {

                            System.out.println();
                            System.out.println("========================================");
                            System.out.println("         SERVICE MANAGEMENT");
                            System.out.println("========================================");
                            System.out.println("1. Update Service");
                            System.out.println("2. Delete Service");
                            System.out.println("3. Batch Add Service Records");
                            System.out.println("4. Back");

                            System.out.print("Enter your choice: ");
                            int serviceManagementChoice =
                                    sc.nextInt();
                            sc.nextLine();

                            switch (serviceManagementChoice) {

                                case 1:

                                    DatabaseOperations.updateService(
                                            con,
                                            sc
                                    );

                                    break;

                                case 2:

                                    DatabaseOperations.deleteService(
                                            con,
                                            sc
                                    );

                                    break;

                                case 3:

                                    DatabaseOperations.batchAddServiceRecords(
                                            con,
                                            sc
                                    );

                                    break;

                                case 4:

                                    serviceManagementRunning = false;
                                    break;

                                default:

                                    System.out.println(
                                            "Invalid choice. Please try again."
                                    );
                            }
                        }

                        break;

                    // =========================================
                    // 9. EXIT
                    // =========================================

                    case 9:

                        running = false;

                        System.out.println();
                        System.out.println(
                                "Thank you for using Vehicle Service Center."
                        );

                        break;

                    default:

                        System.out.println(
                                "Invalid choice. Please try again."
                        );
                }
            }

            sc.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}