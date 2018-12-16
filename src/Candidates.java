import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Candidates extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel picture1;
    private JLabel picture2;
    private JLabel picture3;
    private JLabel picture4;
    private JLabel picture5;
    private JButton buttonCancel;

    public Candidates() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void open() {
        Candidates dialog = new Candidates();
        dialog.pack();
        dialog.setVisible(true);

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        picture1 = new JLabel(new ImageIcon(new ImageIcon("mohit.png").getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
        picture2 = new JLabel(new ImageIcon(new ImageIcon("jacob.jpg").getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
        picture3 = new JLabel(new ImageIcon(new ImageIcon("charles.jpg").getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
        picture4 = new JLabel(new ImageIcon(new ImageIcon("robert.png").getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
        picture5 = new JLabel(new ImageIcon(new ImageIcon("tanner.png").getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
    }
}
