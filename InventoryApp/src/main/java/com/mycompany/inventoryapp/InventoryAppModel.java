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

        Color bg = new Color(211, 241, 223);
        getContentPane().setBackground(bg);

        populateBrandAndTypeData(); // Populate the brand and type data
        populateInitialCategory(); // Populate CmbKategori with placeholders
        addComboBoxListeners();
        addTableSelectionListener();// Add listeners for dynamic updates

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"No.", "Nama Barang", "Kategori", "Status", "Harga", "Jumlah Stok"},
                0
        );
        OutputTable.setModel(model);

        BtnExport.setToolTipText("Ekspor data ke file CSV untuk disimpan.");
        setupExportButton(BtnExport, OutputTable); // Link button to export functionality
    }

    private void populateBrandAndTypeData() {
        brandMap.put("Laptop", new String[]{"Dell", "HP", "Lenovo", "Asus"});
        brandMap.put("Smartphone", new String[]{"Samsung", "Apple", "Xiaomi", "Realme"});

        typeMap.put("Dell", new String[]{"Inspiron", "XPS", "Alienware"});
        typeMap.put("HP", new String[]{"Pavilion", "Envy", "Omen"});
        typeMap.put("Lenovo", new String[]{"Legion", "Ideapad", "Thinkpad"});
        typeMap.put("Asus", new String[]{"ROG", "Vivobook", "Zenbook"});
        typeMap.put("Samsung", new String[]{"Galaxy S", "Galaxy A", "Galaxy Note"});
        typeMap.put("Apple", new String[]{"iPhone SE", "iPhone 13", "iPhone 14", "iPhone 15", "iPhone 16"});
        typeMap.put("Xiaomi", new String[]{"POCO", "Redmi Note", "Redmi"});
        typeMap.put("Realme", new String[]{"Narzo", "GT", "C Series"});
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
            String price = String.valueOf(OutputTable.getValueAt(selectedRow, 4));
            int stock = (int) OutputTable.getValueAt(selectedRow, 5);

            String[] itemParts = itemName.split(" - ");
            String brand = itemParts[0];
            String type = itemParts.length > 1 ? itemParts[1] : "";

            CmbKategori.setSelectedItem(category);
            updateBrands();
            CmbMerk.setSelectedItem(brand);
            updateTypes();
            CmbTipeBarang.setSelectedItem(type);

            TxtHarga.setText(price);

            if ("Baru".equals(status)) {
                RadioBtnBaru.setSelected(true);
            } else {
                RadioBtnBekas.setSelected(true);
            }

            SpinnerStok.setValue(stock);
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

    private void setupExportButton(JButton btnExport, JTable outputTable) {
        btnExport.addActionListener(e -> exportTableDataToCSV(outputTable));
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(211, 241, 223));

        TitleLabel.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        TitleLabel.setForeground(new java.awt.Color(82, 91, 68));
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
                .addGap(20, 20, 20)
                .addComponent(TitleLabel)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(211, 241, 223));

        KategoriBarang.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        KategoriBarang.setForeground(new java.awt.Color(82, 91, 68));
        KategoriBarang.setText("Kategori Barang:");

        CmbKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbKategoriActionPerformed(evt);
            }
        });

        MerkBarang.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        MerkBarang.setForeground(new java.awt.Color(82, 91, 68));
        MerkBarang.setText("Merk Barang:");

        CmbMerk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbMerkActionPerformed(evt);
            }
        });

        TipeBarang.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        TipeBarang.setForeground(new java.awt.Color(82, 91, 68));
        TipeBarang.setText("Tipe Barang:");

        CmbTipeBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbTipeBarangActionPerformed(evt);
            }
        });

        Harga.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        Harga.setForeground(new java.awt.Color(82, 91, 68));
        Harga.setText("Harga:");

        StatusBarang.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        StatusBarang.setForeground(new java.awt.Color(82, 91, 68));
        StatusBarang.setText("Status Barang:");

        groupButtonStatus.add(RadioBtnBaru);
        RadioBtnBaru.setText("Baru");

        groupButtonStatus.add(RadioBtnBekas);
        RadioBtnBekas.setText("Bekas");

        JumlahStok.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        JumlahStok.setForeground(new java.awt.Color(82, 91, 68));
        JumlahStok.setText("Jumlah Stok:");

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

        jPanel3.setBackground(new java.awt.Color(211, 241, 223));

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

        jPanel4.setBackground(new java.awt.Color(211, 241, 223));

        OutputTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Nama Barang", "Kategori", "Status", "Harga", "Jumlah Stok"
            }
        ));
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

            // Add data to the table
            DefaultTableModel model = (DefaultTableModel) OutputTable.getModel();
            int rowNo = model.getRowCount() + 1; // Auto-increment row number
            model.addRow(new Object[]{
                rowNo,
                namaBarang,
                kategori,
                statusBarang,
                formattedHarga,
                jumlahStok
            });

            // Add data to originalTableData for Reset/Search functionality
            originalTableData.add(new Object[]{
                rowNo,
                namaBarang,
                kategori,
                statusBarang,
                formattedHarga,
                jumlahStok
            });

            // Clear inputs after successful addition
            CmbKategori.setSelectedIndex(0);
            CmbMerk.setSelectedIndex(0);
            CmbTipeBarang.setSelectedIndex(0);
            TxtHarga.setText("");
            RadioBtnBaru.setSelected(true);
            SpinnerStok.setValue(0);

            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!",
                    "Informasi", JOptionPane.INFORMATION_MESSAGE);

            // Check for low stocks
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
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"No.", "Nama Barang", "Kategori", "Status", "Harga", "Jumlah Stok"},
                0
        );

        for (Object[] rowData : originalTableData) { // Restore original data
            model.addRow(rowData);
        }

        OutputTable.setModel(model);
        TxtHarga.setText(""); // Clear the search field
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
            String itemName = brand + " - " + type;

            OutputTable.setValueAt(itemName, selectedRow, 1);
            OutputTable.setValueAt(category, selectedRow, 2);
            OutputTable.setValueAt(status, selectedRow, 3);
            OutputTable.setValueAt(formattedHarga, selectedRow, 4);
            OutputTable.setValueAt(stock, selectedRow, 5);

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

                renumberTableRows(); // Renumber rows after deletion

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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane;
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
