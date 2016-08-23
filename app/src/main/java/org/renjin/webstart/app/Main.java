package org.renjin.webstart.app;

import com.google.common.base.Throwables;
import org.renjin.RenjinVersion;
import org.renjin.script.RenjinScriptEngine;
import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.SEXP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringWriter;

public class Main extends JPanel implements ActionListener {
  private JTextArea inputField;
  private JTextArea outputField;
  private JButton executeButton;
  
  private final static String newline = "\n";

  public Main() {
    super(new GridBagLayout());

    inputField = new JTextArea(5, 20);

    outputField = new JTextArea(5, 20);
    outputField.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(outputField);

    executeButton = new JButton("Execute");
    executeButton.addActionListener(this);
    
    //Add Components to this panel.
    GridBagConstraints c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;

    c.fill = GridBagConstraints.HORIZONTAL;
    add(inputField, c);
    
    add(executeButton, c);

    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1.0;
    c.weighty = 1.0;
    add(scrollPane, c);
  }

  public void actionPerformed(ActionEvent evt) {
    String text = inputField.getText();

    StringWriter output = new StringWriter();
    try {
      RenjinScriptEngine engine = new RenjinScriptEngineFactory().getScriptEngine();
      engine.getContext().setWriter(output);
      engine.getContext().setErrorWriter(output);
      SEXP result = (SEXP)engine.eval(text);
      if(!engine.getRuntimeContext().getSession().isInvisible()) {
        engine.invoke("print").withArgument(result).apply();
      }
    } catch (Exception e) {
      output.append(Throwables.getStackTraceAsString(e));
    }
    
    outputField.setText(output.toString());

  }

  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event dispatch thread.
   */
  private static void createAndShowGUI() {
    //Create and set up the window.
    JFrame frame = new JFrame("Renjin " + RenjinVersion.getVersionName());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Add contents to the window.
    frame.add(new Main());

    //Display the window.
    frame.pack();
    frame.setSize(600, 300);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    //Schedule a job for the event dispatch thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }
}