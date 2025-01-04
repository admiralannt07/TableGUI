/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.inventoryapp;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.TableModel;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author acer
 */
public class InventoryAppModel extends javax.swing.JFrame {

    private HashMap<String, String[]> brandMap = new HashMap<>();
    private HashMap<String, String[]> typeMap = new HashMap<>();
    private ArrayList<Object[]> originalTableData = new ArrayList<>();

    public String formatHarga(int harga) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(harga).replace("Rp", "Rp.").replace(",00", "");
    }

    /**
     * Creates new form InventoryAppModel
     */
    public InventoryAppModel() {
        initComponents();

        Color bg = new Color(255, 255, 255);
        getContentPane().setBackground(bg);

        SpinnerStok.setModel(new javax.swing.SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));

        populateBrandAndTypeData(); // Populate the brand and type data
        populateInitialCategory(); // Populate CmbKategori with placeholders
        addComboBoxListeners();
        addTableSelectionListener();// Add listeners for dynamic updates

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"No.", "Nama Barang", "Kategori", "Status", "Harga", "Jumlah Stok"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Semua sel tidak dapat diedit
                return false;
            }
        };
        OutputTable.setModel(model);

        BtnExport.setToolTipText("Ekspor data ke file CSV untuk disimpan.");
    }

    private void populateBrandAndTypeData() {
        // Data for Brand and Types
        brandMap.put("Laptop", new String[]{"Dell", "HP", "Lenovo", "Asus", "Acer", "MSI", "Apple"});
        brandMap.put("Smartphone", new String[]{"Samsung", "Apple", "Xiaomi", "Realme", "Oppo", "Vivo", "Google Pixel", "OnePlus"});

        // Laptop types
        typeMap.put("Dell", new String[]{"Inspiron 15", "Inspiron 14", "XPS 13", "XPS 15", "Alienware m15", "Alienware x14"});
        typeMap.put("HP", new String[]{"Pavilion 14", "Pavilion 15", "Envy 13", "Envy x360", "Spectre x360", "Omen 15"});
        typeMap.put("Lenovo", new String[]{"ThinkPad X1", "ThinkPad T14", "Legion 5", "Legion 7", "IdeaPad 3", "IdeaPad Slim 7"});
        typeMap.put("Asus", new String[]{"ROG Zephyrus G14", "ROG Strix G15", "TUF Gaming A15", "VivoBook 15", "ZenBook Duo", "ExpertBook B9"});
        typeMap.put("Acer", new String[]{"Aspire 5", "Nitro 5", "Predator Helios 300", "Predator Triton 500", "Swift 3", "Swift X"});
        typeMap.put("MSI", new String[]{"GF63 Thin", "GE76 Raider", "GP66 Leopard", "Creator Z16", "Stealth 15M", "Modern 14", "Titan GT77"});
        typeMap.put("Apple", new String[]{"MacBook Air M1", "MacBook Air M2", "MacBook Pro 13", "MacBook Pro 14", "MacBook Pro 16"});

        // Smartphone types
        typeMap.put("Samsung", new String[]{"Galaxy S23 Ultra", "Galaxy S22", "Galaxy Z Fold 4", "Galaxy A73", "Galaxy M14", "Galaxy S10 Ultra"});
        typeMap.put("Apple", new String[]{"iPhone 13 Mini", "iPhone 14 Pro", "iPhone 14 Pro Max", "iPhone SE 2022", "iPhone XR", "iPhone 12"});
        typeMap.put("Xiaomi", new String[]{"Redmi Note 12", "POCO F5", "13", "13 Ultra", "Mi Mix Fold 2", "Redmi K60"});
        typeMap.put("Realme", new String[]{"Narzo 60", "GT Neo 5", "C55", "11 Pro", "10 Regular", "Narzo 50"});
        typeMap.put("Oppo", new String[]{"Find X5 Pro", "Reno 8", "Reno 9 Pro", "Oppo A96", "Find N2 Flip", "A78"});
        typeMap.put("Vivo", new String[]{"X80 Pro", "Vivo T2 5G", "Y100", "Y16", "V27 Pro", "V25"});
        typeMap.put("Google Pixel", new String[]{"7 Pro", "6a", "Fold", "7a", "5", "4 XL"});
        typeMap.put("OnePlus", new String[]{"11", "10 Pro", "2T", "CE 3 Lite", "Ace", "9R"});
    }

    private void populateInitialCategory() {
        CmbKategori.removeAllItems();
        CmbKategori.addItem("Pilih Kategori"); // Add placeholder
        for (String category : brandMap.keySet()) {
            CmbKategori.addItem(category); // Add actual categories
        }
        CmbKategori.setSelectedIndex(0); // Set placeholder as the default selection
    }

    private void addComboBoxListeners() {
        CmbKategori.addActionListener(e -> updateBrands());
        CmbMerk.addActionListener(e -> updateTypes());
    }

    private void updateBrands() {
        String selectedCategory = (String) CmbKategori.getSelectedItem();
        CmbMerk.removeAllItems();
        CmbMerk.addItem("Pilih Merk"); // Add placeholder

        if (selectedCategory != null && !selectedCategory.equals("Pilih Kategori") && brandMap.containsKey(selectedCategory)) {
            for (String brand : brandMap.get(selectedCategory)) {
                CmbMerk.addItem(brand); // Populate the brand ComboBox
            }
        }
        CmbMerk.setSelectedIndex(0); // Set placeholder as the default selection
    }

    private void updateTypes() {
        String selectedBrand = (String) CmbMerk.getSelectedItem();
        CmbTipeBarang.removeAllItems();
        CmbTipeBarang.addItem("Pilih Tipe Barang"); // Add placeholder

        if (selectedBrand != null && !selectedBrand.equals("Pilih Merk") && typeMap.containsKey(selectedBrand)) {
            for (String type : typeMap.get(selectedBrand)) {
                CmbTipeBarang.addItem(type); // Populate the type ComboBox
            }
        }
        CmbTipeBarang.setSelectedIndex(0); // Set placeholder as the default selection
    }

    private void addTableSelectionListener() {
        OutputTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && OutputTable.getSelectedRow() != -1) {
                loadSelectedRowData();
            }
        });
    }

    private void loadSelectedRowData() {
        int selectedRow = OutputTable.getSelectedRow();

        if (selectedRow != -1) {
            String category = (String) OutputTable.getValueAt(selectedRow, 2);
            String itemName = (String) OutputTable.getValueAt(selectedRow, 1);
            String status = (String) OutputTable.getValueAt(selectedRow, 3);
            String priceWithCurrency = (String) OutputTable.getValueAt(selectedRow, 4);
            int stock = (int) OutputTable.getValueAt(selectedRow, 5);

            // Split itemName into brand and type
            String[] itemParts = itemName.split(" ", 2);
            String brand = itemParts.length > 0 ? itemParts[0] : "";
            String type = itemParts.length > 1 ? itemParts[1] : "";

            // Remove currency and formatting from price
            String price = priceWithCurrency.replaceAll("[^\\d]", "");

            // Set the values in the input fields
            CmbKategori.setSelectedItem(category);
            updateBrands(); // Ensure the brand dropdown is populated
            CmbMerk.setSelectedItem(brand);
            updateTypes(); // Ensure the type dropdown is populated

            // Set the type value after ensuring the dropdown is updated
            SwingUtilities.invokeLater(() -> {
                CmbTipeBarang.setSelectedItem(type);
            });

            TxtHarga.setText(price); // Set only numeric value
            SpinnerStok.setValue(stock);

            if ("Baru".equalsIgnoreCase(status)) {
                RadioBtnBaru.setSelected(true);
            } else {
                RadioBtnBekas.setSelected(true);
            }
        }
    }

    private void renumberTableRows() {
        DefaultTableModel model = (DefaultTableModel) OutputTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0); // Update row number (1-based indexing)
        }
    }

    private void exportTableDataToCSV(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Lokasi untuk Menyimpan File");

        // Set default filename
        fileChooser.setSelectedFile(new java.io.File("InventoryExport_" + java.time.LocalDate.now() + ".csv"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();

            try (FileWriter writer = new FileWriter(fileToSave)) {
                TableModel model = table.getModel();

                // Write table headers
                for (int i = 0; i < model.getColumnCount(); i++) {
                    writer.write(model.getColumnName(i) + ",");
                }
                writer.write("\n");

                // Write table rows
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        writer.write(model.getValueAt(i, j).toString() + ",");
                    }
                    writer.write("\n");
                }

                JOptionPane.showMessageDialog(null, "Data berhasil diekspor ke file:\n" + fileToSave.getAbsolutePath(),
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menyimpan file:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void insertDataToDatabase(String namaBarang, String kategori, String status, int harga, int jumlahStok) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO inventory (nama_barang, kategori, status_barang, harga, jumlah_stok) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, namaBarang);
                stmt.setString(2, kategori);
                stmt.setString(3, status);
                stmt.setInt(4, harga);
                stmt.setInt(5, jumlahStok);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error inserting data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDataFromDatabase() {
        DefaultTableModel model = (DefaultTableModel) OutputTable.getModel();
        model.setRowCount(0); // Clear existing rows
        originalTableData.clear(); // Clear local cache

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM inventory";
            try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Object[] row = {
                        model.getRowCount() + 1, // Row number
                        rs.getString("nama_barang"),
                        rs.getString("kategori"),
                        rs.getString("status_barang"),
                        formatHarga(rs.getInt("harga")),
                        rs.getInt("jumlah_stok")
                    };
                    model.addRow(row);
                    originalTableData.add(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDatabaseRecord(int id, String namaBarang, String kategori, String status, int harga, int jumlahStok) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE inventory SET nama_barang = ?, kategori = ?, status_barang = ?, harga = ?, jumlah_stok = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, namaBarang);
                stmt.setString(2, kategori);
                stmt.setString(3, status);
                stmt.setInt(4, harga);
                stmt.setInt(5, jumlahStok);
                stmt.setInt(6, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating record: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDatabaseRecord(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "DELETE FROM inventory WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting record: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openFrame(JFrame frame) {
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private boolean showConfirmationDialog(String message, String title) {
        int confirm = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
        return confirm == JOptionPane.YES_OPTION;
    }

    private void transitionTo(JFrame targetFrame) {
        dispose(); // or this.setVisible(false);
        openFrame(targetFrame);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupButtonStatus = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        TitleLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        KategoriBarang = new javax.swing.JLabel();
        CmbKategori = new javax.swing.JComboBox<>();
        MerkBarang = new javax.swing.JLabel();
        CmbMerk = new javax.swing.JComboBox<>();
        TipeBarang = new javax.swing.JLabel();
        CmbTipeBarang = new javax.swing.JComboBox<>();
        Harga = new javax.swing.JLabel();
        TxtHarga = new javax.swing.JTextField();
        StatusBarang = new javax.swing.JLabel();
        RadioBtnBaru = new javax.swing.JRadioButton();
        RadioBtnBekas = new javax.swing.JRadioButton();
        JumlahStok = new javax.swing.JLabel();
        SpinnerStok = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        BtnTambah = new javax.swing.JButton();
        BtnPerbarui = new javax.swing.JButton();
        BtnHapus = new javax.swing.JButton();
        BtnExport = new javax.swing.JButton();
        BtnReset = new javax.swing.JButton();
        BtnCari = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        OutputTable = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuKembali = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuTentang = new javax.swing.JMenuItem();
        menuProfil = new javax.swing.JMenuItem();
        menuLogout = new javax.swing.JMenuItem();
        menuKeluar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("InventoryMenu");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        TitleLabel.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        TitleLabel.setForeground(new java.awt.Color(51, 0, 102));
        TitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TitleLabel.setText("ELECTRONIC STORE INVENTORY APP");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(TitleLabel)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        KategoriBarang.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        KategoriBarang.setForeground(new java.awt.Color(51, 0, 102));
        KategoriBarang.setText("Kategori Barang:");

        CmbKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbKategoriActionPerformed(evt);
            }
        });

        MerkBarang.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        MerkBarang.setForeground(new java.awt.Color(51, 0, 102));
        MerkBarang.setText("Merk Barang:");

        CmbMerk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbMerkActionPerformed(evt);
            }
        });

        TipeBarang.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        TipeBarang.setForeground(new java.awt.Color(51, 0, 102));
        TipeBarang.setText("Tipe Barang:");

        CmbTipeBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbTipeBarangActionPerformed(evt);
            }
        });

        Harga.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        Harga.setForeground(new java.awt.Color(51, 0, 102));
        Harga.setText("Harga:");

        StatusBarang.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        StatusBarang.setForeground(new java.awt.Color(51, 0, 102));
        StatusBarang.setText("Status Barang:");

        groupButtonStatus.add(RadioBtnBaru);
        RadioBtnBaru.setText("Baru");

        groupButtonStatus.add(RadioBtnBekas);
        RadioBtnBekas.setText("Bekas");

        JumlahStok.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        JumlahStok.setForeground(new java.awt.Color(51, 0, 102));
        JumlahStok.setText("Jumlah Stok:");

        SpinnerStok.setToolTipText("");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(103, 103, 103)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Harga, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(TipeBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(27, 27, 27)
                            .addComponent(CmbTipeBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(MerkBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(CmbMerk, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(JumlahStok, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(KategoriBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(StatusBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(27, 27, 27)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(RadioBtnBaru, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(SpinnerStok, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(RadioBtnBekas, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(CmbKategori, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TxtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(KategoriBarang)
                    .addComponent(CmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(MerkBarang)
                    .addComponent(CmbMerk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TipeBarang)
                    .addComponent(CmbTipeBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Harga)
                    .addComponent(TxtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StatusBarang)
                    .addComponent(RadioBtnBaru)
                    .addComponent(RadioBtnBekas))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JumlahStok)
                    .addComponent(SpinnerStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        BtnTambah.setText("Tambah");
        BtnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahActionPerformed(evt);
            }
        });

        BtnPerbarui.setText("Perbarui");
        BtnPerbarui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPerbaruiActionPerformed(evt);
            }
        });

        BtnHapus.setText("Hapus");
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });

        BtnExport.setText("Export");
        BtnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnExportActionPerformed(evt);
            }
        });

        BtnReset.setText("Reset");
        BtnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnResetActionPerformed(evt);
            }
        });

        BtnCari.setText("Cari");
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(BtnTambah)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtnPerbarui)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtnCari)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(BtnHapus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtnReset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtnExport)
                .addGap(24, 24, 24))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnTambah)
                    .addComponent(BtnPerbarui)
                    .addComponent(BtnHapus)
                    .addComponent(BtnExport)
                    .addComponent(BtnReset)
                    .addComponent(BtnCari))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));

        OutputTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Nama Barang", "Kategori", "Status", "Harga", "Jumlah Stok"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane.setViewportView(OutputTable);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jMenu1.setText("Pilih Menu");

        menuKembali.setIcon(new javax.swing.ImageIcon("C:\\Users\\acer\\Documents\\NetBeansProjects\\TableGUI2\\InventoryApp\\src\\main\\java\\assets\\Back.png")); // NOI18N
        menuKembali.setText("Kembali");
        menuKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuKembaliActionPerformed(evt);
            }
        });
        jMenu1.add(menuKembali);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Pengaturan");

        menuTentang.setIcon(new javax.swing.ImageIcon("C:\\Users\\acer\\Documents\\NetBeansProjects\\TableGUI2\\InventoryApp\\src\\main\\java\\assets\\About.png")); // NOI18N
        menuTentang.setText("Tentang");
        menuTentang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuTentangActionPerformed(evt);
            }
        });
        jMenu2.add(menuTentang);

        menuProfil.setIcon(new javax.swing.ImageIcon("C:\\Users\\acer\\Documents\\NetBeansProjects\\TableGUI2\\InventoryApp\\src\\main\\java\\assets\\Profile.png")); // NOI18N
        menuProfil.setText("Profil");
        menuProfil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuProfilActionPerformed(evt);
            }
        });
        jMenu2.add(menuProfil);

        menuLogout.setIcon(new javax.swing.ImageIcon("C:\\Users\\acer\\Documents\\NetBeansProjects\\TableGUI2\\InventoryApp\\src\\main\\java\\assets\\Logout.png")); // NOI18N
        menuLogout.setText("Logout");
        menuLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLogoutActionPerformed(evt);
            }
        });
        jMenu2.add(menuLogout);

        menuKeluar.setIcon(new javax.swing.ImageIcon("C:\\Users\\acer\\Documents\\NetBeansProjects\\TableGUI2\\InventoryApp\\src\\main\\java\\assets\\Exit.png")); // NOI18N
        menuKeluar.setText("Keluar");
        menuKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuKeluarActionPerformed(evt);
            }
        });
        jMenu2.add(menuKeluar);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void BtnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambahActionPerformed
        // TODO add your handling code here:
        String kategori = CmbKategori.getSelectedItem().toString();
        String merk = CmbMerk.getSelectedItem().toString();
        String tipe = CmbTipeBarang.getSelectedItem().toString();
        String hargaInput = TxtHarga.getText().trim();
        String statusBarang = RadioBtnBaru.isSelected() ? "Baru" : "Bekas";
        int jumlahStok = (int) SpinnerStok.getValue();

        if (kategori.equals("Pilih Kategori") || merk.equals("Pilih Merk") || tipe.equals("Pilih Tipe")
                || hargaInput.isEmpty() || jumlahStok <= 0) {
            JOptionPane.showMessageDialog(this, "Harap isi semua data dengan benar!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate numeric input for price
        try {
            int harga = Integer.parseInt(hargaInput);
            String formattedHarga = formatHarga(harga);

            // Create item name
            String namaBarang = merk + " " + tipe;

            // Nomor baris berdasarkan total data di originalTableData
            int rowNo = originalTableData.size() + 1;

            // Tambahkan data ke tabel saat ini
            DefaultTableModel model = (DefaultTableModel) OutputTable.getModel();
            model.addRow(new Object[]{
                rowNo,
                namaBarang,
                kategori,
                statusBarang,
                formattedHarga,
                jumlahStok
            });

            // Tambahkan data ke originalTableData
            originalTableData.add(new Object[]{
                rowNo,
                namaBarang,
                kategori,
                statusBarang,
                formattedHarga,
                jumlahStok
            });

            // Clear inputs setelah data berhasil ditambahkan
            CmbKategori.setSelectedIndex(0);
            CmbMerk.setSelectedIndex(0);
            CmbTipeBarang.setSelectedIndex(0);
            TxtHarga.setText("");
            RadioBtnBaru.setSelected(true);
            SpinnerStok.setValue(0);

            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!",
                    "Informasi", JOptionPane.INFORMATION_MESSAGE);

            // Check untuk stok rendah
            if (jumlahStok < 5) {
                JOptionPane.showMessageDialog(this, "Stok rendah untuk " + namaBarang + "! Tambahkan segera!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_BtnTambahActionPerformed

    private void BtnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnResetActionPerformed
        // TODO add your handling code here:
        // Buat model tabel baru dengan isCellEditable selalu false
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"No.", "Nama Barang", "Kategori", "Status", "Harga", "Jumlah Stok"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Selalu tidak dapat diedit
            }
        };

        // Tambahkan data dari originalTableData dengan penomoran ulang
        for (int i = 0; i < originalTableData.size(); i++) {
            Object[] rowData = originalTableData.get(i);
            rowData[0] = i + 1; // Perbarui nomor baris berdasarkan urutan
            model.addRow(rowData);
        }

        OutputTable.setModel(model); // Reset tabel ke kondisi originalTableData
    }//GEN-LAST:event_BtnResetActionPerformed

    private void BtnPerbaruiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPerbaruiActionPerformed
        // TODO add your handling code here:
        int selectedRow = OutputTable.getSelectedRow();

        if (selectedRow != -1) {
            if (!validateInputs()) {
                JOptionPane.showMessageDialog(this, "Harap lengkapi semua input dengan benar!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String category = (String) CmbKategori.getSelectedItem();
            String brand = (String) CmbMerk.getSelectedItem();
            String type = (String) CmbTipeBarang.getSelectedItem();
            String status = RadioBtnBaru.isSelected() ? "Baru" : "Bekas";
            String price = TxtHarga.getText().trim();
            int harga = Integer.parseInt(price);
            String formattedHarga = formatHarga(harga);
            int stock = (int) SpinnerStok.getValue();
            String itemName = brand + " " + type;

            // Update tabel
            OutputTable.setValueAt(itemName, selectedRow, 1);
            OutputTable.setValueAt(category, selectedRow, 2);
            OutputTable.setValueAt(status, selectedRow, 3);
            OutputTable.setValueAt(formattedHarga, selectedRow, 4);
            OutputTable.setValueAt(stock, selectedRow, 5);

            // Update originalTableData untuk mempertahankan perubahan
            Object[] updatedRow = new Object[]{
                selectedRow + 1, // Nomor baris (1-based indexing)
                itemName,
                category,
                status,
                formattedHarga,
                stock
            };
            originalTableData.set(selectedRow, updatedRow);

            // Tampilkan peringatan jika stok rendah
            if (stock < 5) {
                JOptionPane.showMessageDialog(this,
                        "Stok rendah untuk " + itemName + "! Tambahkan segera!",
                        "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
            }

            JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diperbarui terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_BtnPerbaruiActionPerformed

    private void CmbKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbKategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbKategoriActionPerformed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        // TODO add your handling code here:
        int selectedRow = OutputTable.getSelectedRow();

        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Apakah Anda yakin ingin menghapus data ini?",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                DefaultTableModel model = (DefaultTableModel) OutputTable.getModel();
                model.removeRow(selectedRow);

                // Hapus data dari originalTableData
                originalTableData.remove(selectedRow);

                // Perbarui nomor baris di originalTableData
                for (int i = 0; i < originalTableData.size(); i++) {
                    originalTableData.get(i)[0] = i + 1; // Update nomor baris
                }

                // Perbarui tabel
                BtnResetActionPerformed(null);

                JOptionPane.showMessageDialog(
                        this,
                        "Data berhasil dihapus!",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih data yang ingin dihapus terlebih dahulu!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnExportActionPerformed
        // TODO add your handling code here:
        exportTableDataToCSV(OutputTable);
    }//GEN-LAST:event_BtnExportActionPerformed

    private void CmbMerkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbMerkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbMerkActionPerformed

    private void CmbTipeBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbTipeBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbTipeBarangActionPerformed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        // TODO add your handling code here:
        // Prompt the user for search input using JOptionPane
        String searchQuery = JOptionPane.showInputDialog(this,
                "Masukkan kata kunci pencarian (misalnya Nama Barang atau Kategori):",
                "Pencarian Data",
                JOptionPane.QUESTION_MESSAGE);

        // If the user clicks "Cancel" or enters nothing, exit the method
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pencarian dibatalkan atau kata kunci kosong.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Convert the search query to lowercase for case-insensitive comparison
        searchQuery = searchQuery.trim().toLowerCase();

        // Get the table model and clear existing rows
        DefaultTableModel model = (DefaultTableModel) OutputTable.getModel();
        model.setRowCount(0); // Clear the table to show filtered results

        // Iterate through originalTableData to find matching rows
        boolean matchFound = false;
        for (Object[] row : originalTableData) {
            // Check if any column contains the search query
            String namaBarang = row[1].toString().toLowerCase();
            String kategori = row[2].toString().toLowerCase();
            if (namaBarang.contains(searchQuery) || kategori.contains(searchQuery)) {
                model.addRow(row); // Add the matching row to the table
                matchFound = true;
            }
        }

        // If no matches found, display a message
        if (!matchFound) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada data yang cocok dengan kata kunci: " + searchQuery,
                    "Hasil Pencarian",
                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_BtnCariActionPerformed

    private void menuTentangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuTentangActionPerformed
        // TODO add your handling code here:
        openFrame(new Tentang());
    }//GEN-LAST:event_menuTentangActionPerformed

    private void menuKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuKeluarActionPerformed
        // TODO add your handling code here:
        if (showConfirmationDialog("Apakah anda yakin untuk keluar?", "Konfirmasi Keluar")) {
            dispose();
            JOptionPane.showMessageDialog(this, "Anda telah keluar dari program. Terimakasih.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_menuKeluarActionPerformed

    private void menuProfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuProfilActionPerformed
        // TODO add your handling code here:
        openFrame(new Profil());
    }//GEN-LAST:event_menuProfilActionPerformed

    private void menuLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLogoutActionPerformed
        // TODO add your handling code here:
        if (showConfirmationDialog("Apakah anda yakin untuk logout?", "Konfirmasi Logout")) {
            transitionTo(new Login());
        }
    }//GEN-LAST:event_menuLogoutActionPerformed

    private void menuKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuKembaliActionPerformed
        // TODO add your handling code here:
        transitionTo(new Homepage());
    }//GEN-LAST:event_menuKembaliActionPerformed

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
            java.util.logging.Logger.getLogger(InventoryAppModel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InventoryAppModel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InventoryAppModel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InventoryAppModel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InventoryAppModel().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnCari;
    private javax.swing.JButton BtnExport;
    private javax.swing.JButton BtnHapus;
    private javax.swing.JButton BtnPerbarui;
    private javax.swing.JButton BtnReset;
    private javax.swing.JButton BtnTambah;
    private javax.swing.JComboBox<String> CmbKategori;
    private javax.swing.JComboBox<String> CmbMerk;
    private javax.swing.JComboBox<String> CmbTipeBarang;
    private javax.swing.JLabel Harga;
    private javax.swing.JLabel JumlahStok;
    private javax.swing.JLabel KategoriBarang;
    private javax.swing.JLabel MerkBarang;
    private javax.swing.JTable OutputTable;
    private javax.swing.JRadioButton RadioBtnBaru;
    private javax.swing.JRadioButton RadioBtnBekas;
    private javax.swing.JSpinner SpinnerStok;
    private javax.swing.JLabel StatusBarang;
    private javax.swing.JLabel TipeBarang;
    private javax.swing.JLabel TitleLabel;
    private javax.swing.JTextField TxtHarga;
    private javax.swing.ButtonGroup groupButtonStatus;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JMenuItem menuKeluar;
    private javax.swing.JMenuItem menuKembali;
    private javax.swing.JMenuItem menuLogout;
    private javax.swing.JMenuItem menuProfil;
    private javax.swing.JMenuItem menuTentang;
    // End of variables declaration//GEN-END:variables
    private boolean validateInputs() {
        if (CmbKategori.getSelectedIndex() == 0) {
            return false;
        }
        if (CmbMerk.getSelectedIndex() == 0) {
            return false;
        }
        if (CmbTipeBarang.getSelectedIndex() == 0) {
            return false;
        }
        if (TxtHarga.getText().trim().isEmpty()) {
            return false;
        }
        try {
            Integer.valueOf(TxtHarga.getText().trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return (int) SpinnerStok.getValue() >= 0;
    }
}
