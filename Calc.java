package proj;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Calc implements ActionListener {
	JFrame frame; // Main application window
    JPanel screen; // Panel to hold calculator components
    JTextField text; // Text field to display input/output
    DefaultTableModel tableModel; // Table model to display calculation history
    JTable table; // Table for calculation history
    JScrollPane scrollPane; // Scroll pane for table
    JButton buttonClear, buttonEqu, buttonDiv, buttonMul, buttonSub, buttonAdd, 
            buttonNeg, buttonLog, buttonDeci, buttonRoot, buttonPower, buttonSin, 
            buttonCos, buttonTan, clear, button0, button1, button2, button3, button4, button5, button6, button7, button8, button9; // Calculator buttons
    JComboBox<String> comboBox; // Dropdown for theme selection
    String[] Colors = {"Light", "Dark"}; // Color theme options
    
    final Color LIGHT_BACKGROUND = Color.LIGHT_GRAY;
    final Color DARK_BACKGROUND = Color.DARK_GRAY;
    final Color LIGHT_TEXT = Color.BLACK;
    final Color DARK_TEXT = Color.WHITE;

    //change (/practice to /yourDatabaseName, I used practice)
    String url = "jdbc:mysql://localhost:3306/practice";
    //ur mysql name for user, default is root
    String user = "root";
    //mysql password for the user given above
    String pass = "Kakoo#24";
    Scanner scanner = new Scanner(System.in);

    Connection connection = null;
    Statement statement = null;

    //variable for calculations
    double num1 = Integer.MAX_VALUE, num2 = Integer.MAX_VALUE;
    int sign = 0;
    boolean second = false, reset = false;

    Calc() {
        // Initialize the frame and set properties
        frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 725);
        frame.setLayout(new BorderLayout());

        // Initialize the screen panel and add text field
        screen = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = c.weighty = 1.0;
        c.insets = new Insets(2, 2, 2, 2);

        text = new JTextField(20); // Display for calculations
        text.setHorizontalAlignment(JTextField.RIGHT);
        text.setEditable(false);
        text.setFont(new Font("Arial", Font.BOLD, 24));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 5;
        c.weighty = 0.1; 
        screen.add(text, c);

        c.gridwidth = 1;
        c.weighty = 0.1; 

        // Add calculator buttons to the screen panel
        addButton(screen, buttonSin = new JButton("Sin"), 0, 1, c);
        addButton(screen, buttonCos = new JButton("Cos"), 1, 1, c);
        addButton(screen, buttonTan = new JButton("Tan"), 2, 1, c);
        addButton(screen, buttonClear = new JButton("Clear"), 3, 1, c);

        addButton(screen, buttonLog = new JButton("Log"), 0, 2, c);
        addButton(screen, buttonRoot = new JButton("√"), 1, 2, c);
        addButton(screen, buttonPower = new JButton("^"), 2, 2, c);
        addButton(screen, buttonDiv = new JButton("/"), 3, 2, c);

        addButton(screen, button1 = new JButton("1"), 0, 3, c);
        addButton(screen, button2 = new JButton("2"), 1, 3, c);
        addButton(screen, button3 = new JButton("3"), 2, 3, c);
        addButton(screen, buttonMul = new JButton("x"), 3, 3, c);

        addButton(screen, button4 = new JButton("4"), 0, 4, c);
        addButton(screen, button5 = new JButton("5"), 1, 4, c);
        addButton(screen, button6 = new JButton("6"), 2, 4, c);
        addButton(screen, buttonSub = new JButton("-"), 3, 4, c);

        addButton(screen, button7 = new JButton("7"), 0, 5, c);
        addButton(screen, button8 = new JButton("8"), 1, 5, c);
        addButton(screen, button9 = new JButton("9"), 2, 5, c);
        addButton(screen, buttonAdd = new JButton("+"), 3, 5, c);

        addButton(screen, buttonDeci = new JButton("."), 0, 6, c);
        addButton(screen, button0 = new JButton("0"), 1, 6, c);
        addButton(screen, buttonNeg = new JButton("(±)"), 2, 6, c);
        addButton(screen, buttonEqu = new JButton("="), 3, 6, c);

        // Side panel for theme selection and history table
        JPanel sidePanel = new JPanel(new GridBagLayout());
        GridBagConstraints spc = new GridBagConstraints();
        spc.fill = GridBagConstraints.HORIZONTAL;
        spc.gridx = 0;
        spc.gridy = 0;
        spc.weightx = 1.0;
        spc.weighty = 0.1;

        // Theme selector
        comboBox = new JComboBox<>(Colors);
        comboBox.addActionListener(this);
        spc.gridy = 0;
        spc.gridx = 1;
        sidePanel.add(comboBox, spc);
        
        // Button to clear the database history
        clear = new JButton("Clear Database");
        clear.addActionListener(this);
        spc.gridy = 0;
        spc.gridx = 0;
        sidePanel.add(clear, spc);

        // Set up table to display calculation history
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Number 1");
        tableModel.addColumn("Sign");
        tableModel.addColumn("Number 2");
        tableModel.addColumn("Answer");
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        spc.gridy = 1; 
        spc.gridwidth = GridBagConstraints.REMAINDER; 
        spc.fill = GridBagConstraints.BOTH;
        spc.weighty = 0.6; 
        spc.insets = new Insets(20, 0, 0, 0); 
        sidePanel.add(scrollPane, spc);

        c.gridx = 5;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 7;
        screen.add(sidePanel, c);
        
        // Database connection setup
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load JDBC driver
            connection = DriverManager.getConnection(url, user, pass); // Establish connection
            statement = connection.createStatement();
            System.out.println("Database connected successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading MySQL JDBC driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error with date format: " + e.getMessage());
        }

        // Add the main screen to the frame and make it visible
        frame.add(screen, BorderLayout.CENTER);
        frame.setVisible(true);
        printOldEntries(); // Display previous entries from the database
    }

 // Helper method to add buttons to the panel
    private void addButton(JPanel panel, JButton button, int x, int y, GridBagConstraints c) {
        c.gridx = x;
        c.gridy = y;
        button.addActionListener(this);
        panel.add(button, c);
    }

 // Handle button clicks and other actions
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        try {
        	//buttons to calculate results
            if (source == buttonEqu) {
                num2 = Integer.parseInt(text.getText());
                printAnswer();
                num1 = Integer.MAX_VALUE;
                num2 = Integer.MAX_VALUE;
                sign = 0;
                second = false;
            } else if (source == buttonClear) {
            	//clear input
                text.setText("Cleared");
                num1 = Integer.MAX_VALUE;
                num2 = Integer.MAX_VALUE;
                sign = 0;
                second = false;
                reset = false;
            } else if (source == buttonAdd || source == buttonSub || source == buttonMul || source == buttonDiv || source == buttonPower) {
                num1 = Double.parseDouble(text.getText());
                sign = getOperationSign(source);
                second = true;
            } else if (source == buttonNeg) {
                double curr = Double.parseDouble(text.getText());
                text.setText(String.valueOf(-curr));
                reset = false;
            } else if (source == buttonLog) {
                num1 = Double.parseDouble(text.getText());
                sign = 5;
                printAnswer();
            } else if (source == buttonRoot) {
                num1 = Double.parseDouble(text.getText());
                sign = 6;
                printAnswer();
            } else if (source == buttonDeci) {
                if (text.getText().equals("Cleared") || text.getText().equals("")) {
                    text.setText("0");
                }
                if (!text.getText().contains(".")) {
                    text.setText(text.getText() + ".");
                }
                reset = false;
            } else if (source == buttonPower) {
                num1 = Double.parseDouble(text.getText());
                sign = 7;
                text.setText(((JButton) source).getText());
                second = true;
            } else if (source == buttonSin) {
                num1 = Double.parseDouble(text.getText());
                sign = 8;
                text.setText(((JButton) source).getText());
                printAnswer();
            } else if (source == buttonCos) {
                num1 = Double.parseDouble(text.getText());
                sign = 9;
                text.setText(((JButton) source).getText());
                printAnswer();
            } else if (source == buttonTan) {
                num1 = Double.parseDouble(text.getText());
                sign = 10;
                text.setText(((JButton) source).getText());
                printAnswer();
            } else if (source == comboBox) {
            	//update the theme
                updateTheme((String) comboBox.getSelectedItem());
            } else if (source == clear) {
            	//clear dtaabase
                truncateTable();
            } else {
                if (text.getText().equals("Cleared") || text.getText().equals("Invalid Input") || text.getText().equals("Cannot divide by zero") || text.getText().equals("Cannot log a negative number") || text.getText().equals("Cannot sqrt a negative number")) {
                    text.setText(((JButton) source).getText());
                } else if (second) {
                    text.setText(((JButton) source).getText());
                    second = false;
                } else if (reset) {
                    text.setText(((JButton) source).getText());
                    reset = false;
                } else {
                    text.setText(text.getText() + ((JButton) source).getText());
                }
            }

        } catch (NumberFormatException ex) {
            text.setText("Invalid Input");
            reset = true;
        }
    }

    private int getOperationSign(Object button) {
    	//return sign based on the button clicked
        if (button == buttonAdd) return 4;
        if (button == buttonSub) return 3;
        if (button == buttonMul) return 2;
        if (button == buttonDiv) return 1;
        if (button == buttonPower) return 7;
        return 0;
    }

    private void updateTheme(String theme) {
    	//update UI colors based on the selection theme
        Color background, textColor;
        if (theme.equals("Dark")) {
            background = DARK_BACKGROUND;
            textColor = DARK_TEXT;
        } else {
            background = LIGHT_BACKGROUND;
            textColor = LIGHT_TEXT;
        }

        screen.setBackground(background);
        text.setForeground(textColor);
        text.setBackground(background);
//        clear.setBackground(background);
//        clear.setForeground(textColor);
        table.setBackground(background);
        table.setForeground(textColor);
//        comboBox.setForeground(textColor);
//        comboBox.setBackground(background);
        scrollPane.setForeground(textColor);
        scrollPane.setBackground(background);

        for (Component c : screen.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.setForeground(textColor);
                button.setBackground(background);
            }
        }

    }

    private String calculate() {
    	//perform caclulation based on selected operation
        double answer = 0;
        switch (sign) {
            case 1:
                if (num2 == 0) {
                    return ("Cannot divide by zero");
                } else {
                    answer = num1 / num2;
                }
                break;
            case 2:
                answer = num1 * num2;
                break;
            case 3:
                answer = num1 - num2;
                break;
            case 4:
                answer = num1 + num2;
                break;
            case 5:
                if (num1 <= 0) {
                    return "Cannot log a negative number";
                } else {
                    answer = Math.log10(num1);
                }
                break;
            case 6:
                if (num1 < 0) {
                    return "Cannot sqrt a negative number";
                } else {
                    answer = Math.sqrt(num1);
                }
                break;
            case 7:
                answer = Math.pow(num1, num2);
                break;
            case 8:
                answer = Math.sin(num1 * Math.PI / 180);
                break;
            case 9:
                answer = Math.cos(num1 * Math.PI / 180);
                break;
            case 10:
                answer = Math.tan(num1 * Math.PI / 180);
                break;
            default:
                return "Invalid Input";
        }
        if (answer == (long) answer) {
            return String.format("%d", (long) answer);
        } else {
            return String.format("%s", answer);
        }
    }

    private void printAnswer() {
    	//print answer and add it to the  history
        String answer = calculate();
        text.setText(answer);
        reset = true;
        boolean sec;
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO calc_hist (Number1, sign, Number2, Answer) VALUES (?, ?, ?, ?)");
            pstmt.setFloat(1, (float) num1);
            pstmt.setString(2, signToString(sign));
            if (num2 != Integer.MAX_VALUE) {
                pstmt.setFloat(3, (float) num2);
                sec = true;
            } else {
                pstmt.setString(3, null);
                sec = false;
            }
            pstmt.setFloat(4, Float.parseFloat(answer));
            pstmt.executeUpdate();

            int nextId = tableModel.getRowCount() + 1;
            if (sec) {
                tableModel.addRow(new Object[]{nextId, (float) num1, signToString(sign), (float) num2, (Float.parseFloat(answer))});
            } else {
                tableModel.addRow(new Object[]{nextId, (float) num1, signToString(sign), null, (Float.parseFloat(answer))});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private String signToString(int sign) {
    	//convert operation sign to string representation
        String signer;

        switch (sign) {
            case 1:
                signer = "/";
                break;
            case 2:
                signer = "*";
                break;
            case 3:
                signer = "-";
                break;
            case 4:
                signer = "+";
                break;
            case 5:
                signer = "Log";
                break;
            case 6:
                signer = "√";
                break;
            case 7:
                signer = "^";
                break;
            case 8:
                signer = "Sin";
                break;
            case 9:
                signer = "Cos";
                break;
            case 10:
                signer = "Tan";
                break;
            default:
                signer = "Err";
                break;
        }

        return signer;

    }

    public void truncateTable() {
    	//clear the database history table
        String sql = "TRUNCATE TABLE calc_hist";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            tableModel.setRowCount(0);
            JOptionPane.showMessageDialog(frame, "Database cleared successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to clear database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void printOldEntries() {
    	//retrieve and display previous entries from the database
		ResultSet resultSet = null;
		
        try {
        	String sqlQuery = "SELECT * FROM calc_hist";
	        resultSet = statement.executeQuery(sqlQuery);
	        
	        int number;
	        float one, two, after;
	        String operation;
	        
			while(resultSet.next()) {
				number = resultSet.getInt("id");
                one = resultSet.getFloat("Number1");
                operation = resultSet.getString("sign");
                two = resultSet.getFloat("Number2");
                after = resultSet.getFloat("Answer");

    		    
    		    tableModel.addRow(new Object[] {number, one, operation, two, after});          
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

}

}
