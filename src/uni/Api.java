package uni;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.google.gson.Gson;


public class Api {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
		 String url = "jdbc:sqlserver://localhost:1433;" +
	             "databaseName=Api;" +
	             "encrypt=true;" +
	             "trustServerCertificate=true";
        String country = "";

        Scanner sc = new Scanner(System.in);
        int option = 0;
        String username = "";
        String sPassword = "";
        char[] cPassword;


        JTextField usernameField = new JTextField(50);
        JPasswordField passwordField = new JPasswordField(20);

        JPanel loginPanel = new JPanel();
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(Box.createHorizontalStrut(15)); 
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);

          int result = JOptionPane.showConfirmDialog(null, loginPanel, 
                   "Please Enter Username and Password", JOptionPane.OK_CANCEL_OPTION);

          if (result == JOptionPane.OK_OPTION) {
             username = usernameField.getText();
             cPassword = passwordField.getPassword();

             //password = passwordField.getText();

             for(int c = 0; c < cPassword.length; c++)
                 sPassword = sPassword + cPassword[c];
        while (option != 5) {
            System.out.println("Please choose a country:");
            System.out.println("1 - United States");
            System.out.println("2 - Oman");
            System.out.println("3 - India");
            System.out.println("4 - Canada");
            System.out.println("5 - Exit");

            String choice = sc.nextLine();

            try {
                option = Integer.parseInt(choice);
                if (option < 1 || option > 5) {
                    System.out.println("Invalid input. Number must be between 1 and 5.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                continue;
            }

            switch (option) {
                case 1:
                    country = "United+States";
                    break;
                case 2:
                    country = "Oman";
                    break;
                case 3:
                    country = "India";
                    break;
                case 4:
                    country = "Canada";
                    break;
                case 5:
                    System.out.println("Thank you for using our system, Goodbye!");
                    return;
            }

            String apiUrl = "http://universities.hipolabs.com/search?country=" + country;

            try {
            	
                URL url1 = new URL(apiUrl);
                
                HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
                
                conn1.setRequestMethod("GET");
                conn1.setRequestProperty("Accept", "application/json");

                if (conn1.getResponseCode() != 200) {
                    throw new RuntimeException("HTTP error code : " + conn1.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn1.getInputStream())));
                String output;
                StringBuilder json = new StringBuilder();

                while ((output = br.readLine()) != null) {
                    json.append(output);
                }

                Connection conn = DriverManager.getConnection(url, username, sPassword);
                Gson gson = new Gson();
                MyObject[] universities = gson.fromJson(json.toString(), MyObject[].class);
                String insertSql = "INSERT INTO universities (name, domain, website, country, alpha_code) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement statement = conn.prepareStatement(insertSql);
                for (MyObject university : universities) {
                    System.out.println("Name: " + university.getName());
                    System.out.println("Domain: " + university.getDomains());
                    System.out.println("Website: " + university.getWebPages());
                    System.out.println("Country: " + university.getCountry());
                    System.out.println("Alpha Code: " + university.getAlphaTwoCode());
                    statement.setString(1, university.getName());
                    statement.setString(2, university.getDomains().get(0));
                    statement.setString(3, university.getWebPages().get(0));
                    statement.setString(4, university.getCountry());
                    statement.setString(5, university.getAlphaTwoCode());
                    statement.addBatch();
                    System.out.println();
                }
                    statement.executeBatch();
                    statement.close();
                    conn.close();
                    conn1.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            }
    }
}}

