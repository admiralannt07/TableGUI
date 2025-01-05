/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.inventoryapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author acer
 */
public class Profil extends javax.swing.JFrame {

    private int userId;

    /**
     * Creates new form profile
     */
    public Profil() {
        initComponents();
        this.userId = userId;
        loadUserDetails(userId);
    }

    public Profil(int userId) {
        initComponents();
        this.userId = userId; // Assign the provided userId to the class variable
        loadUserDetails(userId); // Load user details using the ID

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                Homepage homepage = new Homepage(userId); // Recreate Homepage
                homepage.setVisible(true);
                homepage.setLocationRelativeTo(null);
            }
        });
    }

    private void loadUserDetails(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT username, email FROM users WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        OutputUsername.setText(rs.getString("username"));
                        OutputEmail.setText(rs.getString("email"));
                    } else {
                        JOptionPane.showMessageDialog(this, "User not found in the database.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading user details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnUbahData = new javax.swing.JButton();
        btnUbahPass = new javax.swing.JButton();
        OutputUsername = new javax.swing.JLabel();
        OutputEmail = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Profil");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Poppins SemiBold", 0, 18)); // NOI18N
        jLabel2.setText("Username:");

        jLabel3.setFont(new java.awt.Font("Poppins SemiBold", 0, 18)); // NOI18N
        jLabel3.setText("Email:");

        btnUbahData.setBackground(new java.awt.Color(0, 51, 153));
        btnUbahData.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        btnUbahData.setForeground(new java.awt.Color(255, 255, 255));
        btnUbahData.setText("Ubah Data Diri");
        btnUbahData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahDataActionPerformed(evt);
            }
        });

        btnUbahPass.setBackground(new java.awt.Color(0, 51, 153));
        btnUbahPass.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        btnUbahPass.setForeground(new java.awt.Color(255, 255, 255));
        btnUbahPass.setText("Ubah Password");
        btnUbahPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahPassActionPerformed(evt);
            }
        });

        OutputUsername.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N

        OutputEmail.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(OutputUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(OutputEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(btnUbahData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnUbahPass)))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(OutputUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(OutputEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUbahData)
                    .addComponent(btnUbahPass))
                .addContainerGap(95, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 510, 340));

        jPanel3.setBackground(new java.awt.Color(0, 51, 153));

        jLabel1.setFont(new java.awt.Font("Poppins SemiBold", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Detail Profil");

        jLabel6.setIcon(new javax.swing.ImageIcon("C:\\Users\\acer\\Documents\\NetBeansProjects\\TableGUI2\\InventoryApp\\src\\main\\java\\assets\\welcome\\usersettings.png")); // NOI18N
        jLabel6.setText("jLabel6");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(100, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(112, 112, 112))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(20, 20, 20))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-4, 0, 520, 100));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnUbahDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahDataActionPerformed
        // TODO add your handling code here:
        String newUsername = JOptionPane.showInputDialog(this, "Enter new username (Leave blank to keep current):", "Change Username", JOptionPane.PLAIN_MESSAGE);
        String newEmail = JOptionPane.showInputDialog(this, "Enter new email (Leave blank to keep current):", "Change Email", JOptionPane.PLAIN_MESSAGE);

        if ((newUsername == null || newUsername.trim().isEmpty()) && (newEmail == null || newEmail.trim().isEmpty())) {
            JOptionPane.showMessageDialog(this, "No changes were made.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder query = new StringBuilder("UPDATE users SET ");
            boolean changeUsername = newUsername != null && !newUsername.trim().isEmpty();
            boolean changeEmail = newEmail != null && !newEmail.trim().isEmpty();

            if (changeUsername) {
                query.append("username = ?");
            }
            if (changeEmail) {
                if (changeUsername) {
                    query.append(", ");
                }
                query.append("email = ?");
            }
            query.append(" WHERE id = ?");

            try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
                int index = 1;
                if (changeUsername) {
                    stmt.setString(index++, newUsername.trim());
                }
                if (changeEmail) {
                    stmt.setString(index++, newEmail.trim());
                }
                stmt.setInt(index, userId); // Replace userId with the actual logged-in user's ID.

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "User data updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUserDetails(userId); // Refresh displayed details
                } else {
                    JOptionPane.showMessageDialog(this, "No changes were made.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating user data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUbahDataActionPerformed

    private void btnUbahPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahPassActionPerformed
        // TODO add your handling code here:
        String currentPassword = JOptionPane.showInputDialog(this, "Enter current password:", "Verify Identity", JOptionPane.PLAIN_MESSAGE);

        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password verification cancelled.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT password FROM users WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId); // Replace userId with the actual logged-in user's ID.
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getString("password").equals(currentPassword.trim())) {
                        String newPassword = JOptionPane.showInputDialog(this, "Enter new password:", "Change Password", JOptionPane.PLAIN_MESSAGE);
                        if (newPassword != null && !newPassword.trim().isEmpty()) {
                            String updateQuery = "UPDATE users SET password = ? WHERE id = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                updateStmt.setString(1, newPassword.trim());
                                updateStmt.setInt(2, userId);
                                updateStmt.executeUpdate();
                                JOptionPane.showMessageDialog(this, "Password updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Password change cancelled.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Incorrect password. Verification failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error verifying or updating password: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUbahPassActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Profil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Profil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Profil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Profil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Profil().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel OutputEmail;
    private javax.swing.JLabel OutputUsername;
    private javax.swing.JButton btnUbahData;
    private javax.swing.JButton btnUbahPass;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
