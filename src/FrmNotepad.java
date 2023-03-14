import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FrmNotepad extends JFrame implements ActionListener, DocumentListener {

    private JTextArea txtFile = null;
    private String fileName = "C:\\_Federico\\Programmazione\\java\\2023-03\\Notepad\\Notepad\\src\\example.txt";
    private Boolean modified = false;
    private JMenuItem mniNew = null;
    private JMenuItem mniOpen = null;
    private JMenuItem mniSave = null;
    private JMenuItem mniSaveAs = null;
    private JMenuItem mniExit = null;
    private FrmNotepad() {

        setTitle("MyNotepad");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        initUI();
        populate();

        setVisible(true);
    }

    public void initUI() {

        //FILE MENU

        JMenuBar mnbFile = new JMenuBar();


        JMenu mnuFile = new JMenu("File");
        this.mniNew = new JMenuItem("New");
        this.mniOpen = new JMenuItem("Open");
        this.mniSave = new JMenuItem("Save");
        this.mniSaveAs = new JMenuItem("Save As");
        this.mniExit = new JMenuItem("Exit");

        mnuFile.add(this.mniNew);
        mnuFile.add(this.mniOpen);
        mnuFile.add(this.mniSave);
        mnuFile.add(this.mniSaveAs);
        mnuFile.addSeparator();
        mnuFile.add(this.mniExit);

        mnbFile.add(mnuFile);

        this.add(mnbFile, BorderLayout.NORTH);



        this.txtFile = new JTextArea();
        JScrollPane pnlCenter = new JScrollPane(this.txtFile);

        this.add(pnlCenter, BorderLayout.CENTER);

        this.txtFile.getDocument().addDocumentListener(this);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.out.println(modified);
                System.exit(0);
            }
        });
    }

    public void populate() {

        try {

            this.txtFile.setText("");
            BufferedReader br = new BufferedReader(new FileReader(fileName));
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

    }

    //DOCUMENT LISTENER

    @Override
    public void insertUpdate(DocumentEvent e) { this.modified = true; }

    @Override
    public void removeUpdate(DocumentEvent e) { this.modified = true; }

    @Override
    public void changedUpdate(DocumentEvent e) { this.modified = true; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmNotepad());
    }

  }