import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.jar.JarFile;

import static jdk.jfr.consumer.EventStream.openFile;

public class FrmNotepad extends JFrame implements ActionListener, DocumentListener, CaretListener {

    private JTextArea txtFile = null;
    private String fileName = null;
    private Boolean modified = false;
    private JMenuItem mniNew = null;
    private JMenuItem mniOpen = null;
    private JMenuItem mniSave = null;
    private JMenuItem mniSaveAs = null;
    private JMenuItem mniExit = null;
    private JLabel lblStatus = null;
    private FrmNotepad() {

        setTitle("Notepad");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        initUI();
        populate();

        setVisible(true);
    }

    public void initUI() {

        pnlNorth();
        pnlCenter();
        pnlSouth();

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                exitApp();
            }
        });
    }

    private void pnlNorth() {

        JMenuBar mnbFile = new JMenuBar();

        JMenu mnuFile = new JMenu("File");
        this.mniNew = new JMenuItem("New...");
        this.mniOpen = new JMenuItem("Open...");
        this.mniSave = new JMenuItem("Save");
        this.mniSaveAs = new JMenuItem("Save As...");
        this.mniExit = new JMenuItem("Exit");

        KeyStroke keyStrokeToNew
                = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);

        KeyStroke keyStrokeToOpen
                = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);

        KeyStroke keyStrokeToSave
                = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);

        Icon icnNew = UIManager.getIcon("FileView.fileIcon");
        Icon icnOpen = UIManager.getIcon("FileChooser.upFolderIcon");
        Icon icnSave = UIManager.getIcon("FileView.floppyDriveIcon");

        this.mniNew.setIcon(icnNew);
        this.mniOpen.setIcon(icnOpen);
        this.mniSave.setIcon(icnSave);

        this.mniNew.setAccelerator(keyStrokeToNew);
        this.mniOpen.setAccelerator(keyStrokeToOpen);
        this.mniSave.setAccelerator(keyStrokeToSave);

        mnuFile.add(this.mniNew);
        mnuFile.add(this.mniOpen);
        mnuFile.add(this.mniSave);
        mnuFile.add(this.mniSaveAs);
        mnuFile.addSeparator();
        mnuFile.add(this.mniExit);

        mnbFile.add(mnuFile);

        this.add(mnbFile, BorderLayout.NORTH);

        this.mniNew.addActionListener(this);
        this.mniOpen.addActionListener(this);
        this.mniSave.addActionListener(this);
        this.mniSaveAs.addActionListener(this);
        this.mniExit.addActionListener(this);
    }
    private void pnlCenter() {

        this.txtFile = new JTextArea();
        JScrollPane pnlCenter = new JScrollPane(this.txtFile);

        this.add(pnlCenter, BorderLayout.CENTER);

        this.txtFile.getDocument().addDocumentListener(this);
        this.txtFile.addCaretListener(this);
    }
    private void pnlSouth() {

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.lblStatus = new JLabel("Line: 1 - Column: 1");
        pnlSouth.add(this.lblStatus);

        this.add(pnlSouth, BorderLayout.SOUTH);
    }

    public void populate() {

        this.txtFile.setText("");
        this.setTitle(this.fileName == null? "[Untitled]":this.fileName);
        if(this.fileName == null) return;
        try {

            BufferedReader br = new BufferedReader(new FileReader(this.fileName));
            String line;
            while((line = br.readLine()) != null)
                this.txtFile.append(line + "\n");

            modified = false;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == this.mniNew) newFile();
        if(e.getSource() == this.mniOpen) openFile();
        if(e.getSource() == this.mniSave) saveFile();
        if(e.getSource() == this.mniSaveAs) saveAsFile();
        if(e.getSource() == this.mniExit) exitApp();
    }

    private void exitApp() {

        if(this.modified == false) System.exit(0);
        int choice = JOptionPane.showConfirmDialog(this,
                "File not Saved. Save it now?", "Alert",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if(choice == 0) {

            saveFile();
            System.exit(0);
        }
        if(choice == 1) System.exit(1);
        //this.modified = false;
    }

    private void saveAsFile() {

        JFileChooser fc = new JFileChooser();
        int rc = fc.showSaveDialog(this);
        if(rc != JFileChooser.APPROVE_OPTION) return;
        this.fileName = fc.getSelectedFile().getAbsolutePath();
        saveFile();
        populate();
    }

    private void saveFile() {

        if(this.fileName == null) saveAsFile();
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(this.fileName));
            pw.print(txtFile.getText());
            pw.close();
            this.modified = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openFile() {

        JFileChooser fc = new JFileChooser();
        int rc = fc.showOpenDialog(this);
        if(rc != JFileChooser.APPROVE_OPTION) return;
        this.fileName = fc.getSelectedFile().getAbsolutePath();
        populate();
    }

    private void newFile() {

        if(this.txtFile.getText().compareTo("") != 0) {

            if(this.modified != false) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "File not Saved. Save it now?", "Alert",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                if(choice == 0) saveFile();
                else if(choice == 1) {}
                else if(choice == 2) return;
            }
        }
        this.fileName = null;
        this.txtFile.setText("");
        saveAsFile();
    }

    //DOCUMENT LISTENER

    @Override
    public void insertUpdate(DocumentEvent e) { this.modified = true; }

    @Override
    public void removeUpdate(DocumentEvent e) { this.modified = true; }

    @Override
    public void changedUpdate(DocumentEvent e) { this.modified = true; }


    @Override
    public void caretUpdate(CaretEvent e) {

        JTextArea curr = (JTextArea) e.getSource();
        int lines = 1, columns = 1;

        int caretpos = curr.getCaretPosition();

        try {
            lines = curr.getLineOfOffset(caretpos);
            columns = caretpos - curr.getLineStartOffset(lines);
            lines += 1;
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }

        updateStatus(lines, columns);
    }

    private void updateStatus(int lines, int columns) {

        this.lblStatus.setText("Line: " + lines + " - Column: " + columns);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmNotepad());
    }
}