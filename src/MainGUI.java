import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

enum CandidateNames{
    MOHITBHOLE, JACOBHILL, CHARLESJACKSON, ROBERTEHLE, TANNERSEIVART
}

public class MainGUI {
    private JFormattedTextField voterIDField;
    private JPanel panel1;
    private JFormattedTextField nameField;
    private JButton submitVoteButton;
    private JButton cancelButton;
    private JLabel Name;
    private JLabel voterId;
    private JLabel fisrtPriority;
    private JLabel secondPriority;
    private JLabel thirdPriority;
    private JLabel App;
    private JButton viewCandidates;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JComboBox comboBox3;

    public void resetValues(){
        nameField.setText("");
        voterIDField.setText("");
        comboBox1.setSelectedIndex(-1);
        comboBox2.setSelectedIndex(-1);
        comboBox3.setSelectedIndex(-1);
    }

    public MainGUI() {

        for(CandidateNames s : CandidateNames.values()){
            comboBox1.addItem(s.name());
            comboBox2.addItem(s.name());
            comboBox3.addItem(s.name());
        }

        submitVoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetValues();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetValues();
            }
        });
        viewCandidates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Candidates dialog = new Candidates();
                dialog.open();
            }
        });
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("BlockChain Voting");
        frame.setPreferredSize(new Dimension(800, 400));
        frame.setContentPane(new MainGUI().panel1);
        frame.pack();

        frame.setVisible(true);
    }
}
