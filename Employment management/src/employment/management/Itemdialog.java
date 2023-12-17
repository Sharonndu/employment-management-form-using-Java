/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employment.management;

import java.awt.Frame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.util.Objects;
/**
 *
 * @author HP
 */
public class Itemdialog extends JDialog{
    
    
    private JTextField nameField;
    private JComboBox <String> genderDropdown;
    private JTextField phone_noField;
    private JTextField emailField;
    private JComboBox <String> designationDropdown;
    private JTextField salaryField;

    public Itemdialog(Frame parent, String title, boolean modal, int id ) {
        super(parent, title, modal);

        // Set background color to white
        getContentPane().setBackground(Color.WHITE);

        // Initialize components
        nameField = new JTextField(20);
        phone_noField = new JTextField(20);
        emailField = new JTextField(20);
        salaryField = new JTextField(20);

        // Set font
        Font font = new Font("Trebuchet MS", Font.PLAIN, 14);
        nameField.setFont(font);
        phone_noField.setFont(font);
        emailField.setFont(font);
        salaryField.setFont(font);

        String[] gender = {"Male", "Female","Non-binary", "Other"};
        genderDropdown = new JComboBox<>(gender);
        genderDropdown.setFont(font);
        
        String[] designation = {"Manager", "Accountant","Intern","Receptionist", "Janitor"};
        designationDropdown = new JComboBox<>(designation);
        designationDropdown.setFont(font);

        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        updateButton.setFont(font);
        deleteButton.setFont(font);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateItem(id);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteItem(id);
            }
        });

        // Set layout manager (GridLayout for better arrangement)
        setLayout(new GridLayout(6, 2, 10, 10));

        // Add components to the layout
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Gender:"));
        add(genderDropdown);
        add(new JLabel("Phone Number:"));
        add(phone_noField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Designation:"));
        add(designationDropdown);
        add(new JLabel("Salary:"));
        add(salaryField);
        
        add(updateButton);
        add(deleteButton);

        // Fetch and populate item details if vendorItemId is provided
        if (id > 0) {
            fetchAndPopulateItemDetails(id);
        }

        pack();
        setLocationRelativeTo(parent);
    }

    private void fetchAndPopulateItemDetails(int id) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ems_db", "root", "")) {
            String sql = "SELECT name, gender, p_number,email, designation, salary FROM employee_details WHERE emp_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Populate the fields with the fetched data
                        nameField.setText(resultSet.getString("name"));
                        String gender = resultSet.getString("gender");
                        genderDropdown.setSelectedItem(gender);
                        phone_noField.setText(resultSet.getString("p_number"));
                        emailField.setText(resultSet.getString("email"));
                        String designation = resultSet.getString("designation");
                        designationDropdown.setSelectedItem(designation);
                        salaryField.setText(resultSet.getString("salary"));
                        
                    } else {
                        // Handle the case where item details are not found
                        JOptionPane.showMessageDialog(this, "Item details not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateItem(int id) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ems_db", "root", "")) {
            String sql = "UPDATE employee_details SET name = ?, gender = ?, p_number = ?, email = ?, designation = ?, salary = ? WHERE emp_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, nameField.getText());
                preparedStatement.setString(2, Objects.requireNonNull(genderDropdown.getSelectedItem()).toString());
                preparedStatement.setString(3, phone_noField.getText());
                preparedStatement.setString(4, emailField.getText());
                preparedStatement.setString(5, Objects.requireNonNull(designationDropdown.getSelectedItem()).toString());
                preparedStatement.setString(6, salaryField.getText());
                preparedStatement.setInt(7, id);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    dispose(); // Close the dialog after successful update
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update item!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteItem(int vendorItemId) {
        int confirmResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmResult == JOptionPane.YES_OPTION) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ems_db", "root", "")) {
                String sql = "DELETE FROM employee_details WHERE emp_id = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, vendorItemId);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // Close the dialog after successful deletion
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete item!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
