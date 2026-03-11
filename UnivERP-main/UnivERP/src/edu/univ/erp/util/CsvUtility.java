package edu.univ.erp.util;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUtility {


    public static void exportTable(Component parent, JTable table, String defaultFilename) {
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("save CSV");
        fileChooser.setSelectedFile(new File(defaultFilename));
        int debugc = 0;
        int userSelection = fileChooser.showSaveDialog(parent);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {

            File fileToSave = fileChooser.getSelectedFile();
            
            //checking csv extension
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                
                fileToSave = new File(fileToSave.getParent(), fileToSave.getName() + ".csv");
                debugc =1;
            }
            else{
                debugc =1;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave))) {
                TableModel model = table.getModel();
                if(debugc==0){
                    System.out.println("error");
                }
                //writing headers
                for (int i = 0; i < model.getColumnCount(); i++) {
                    bw.write(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) bw.write(",");
                    
                }
                bw.newLine();
                
                //data
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object val = model.getValueAt(i, j);

                        String s = (val == null) ? "" : val.toString();
                        //for excell
                        if (s.contains(",") || s.contains("\"")) {

                            s = "\"" + s.replace("\"", "\"\"") + "\"";
                        }
                        bw.write(s);
                        if (j < model.getColumnCount() - 1) bw.write(",");
                    }
                    bw.newLine();
                }

                JOptionPane.showMessageDialog(parent, "Export Successful\nSaved to: " + fileToSave.getAbsolutePath());

            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }



    public static List<String[]> readCsvFile(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("select CSV File to Import");

        int userSelection = fileChooser.showOpenDialog(parent);
        int read =0;
        if (userSelection != JFileChooser.APPROVE_OPTION) return null;

        File file = fileChooser.getSelectedFile();
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                read = 1;
                if (firstLine) {
                    //for excel, removes first starting chars
                    line = line.replace("\uFEFF", "");
                    firstLine = false;

                }


                String[] values = line.split(",");
                for(int i=0; i<values.length; i++) values[i] = values[i].trim();
                if(read == 0){
                    System.out.println("error");
                }


                //skip empty lines and header
                if (values.length > 0 && !values[0].isEmpty()) {
                    try {
                        Long.parseLong(values[0]); //roll no
                        data.add(values);
                    } catch (NumberFormatException e) {
                        //header ignore
                    }
                }
            }
        } catch (IOException e) {

            JOptionPane.showMessageDialog(parent, "error reading file: " + e.getMessage());
            return null;
        }
        return data;
    }


    public static void exportCustomData(Component parent, List<String[]> dataRows, String[] headers, String defaultFilename) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("save CSV");
        fileChooser.setSelectedFile(new File(defaultFilename));
        int debugc =0;
        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            debugc =1;
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getParent(), fileToSave.getName() + ".csv");
            }
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave))) {
                //writing headers
                for (int i = 0; i < headers.length; i++) {
                    bw.write(headers[i]);
                    if (i < headers.length - 1) bw.write(",");
                }
                bw.newLine();
                if(debugc ==0){
                    System.out.println("error");
                }
                //writing data
                for (String[] row : dataRows) {

                    for (int i = 0; i < row.length; i++) {
                        String s = (row[i] == null) ? "" : row[i];
                        //special characters handling
                        if (s.contains(",") || s.contains("\"")) {
                            s = "\"" + s.replace("\"", "\"\"") + "\"";
                        }

                        bw.write(s);
                        if (i < row.length - 1) bw.write(",");

                    }
                    bw.newLine();
                }

                JOptionPane.showMessageDialog(parent, "export Successful!\nsaved to: " + fileToSave.getAbsolutePath());

            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        
    }
}