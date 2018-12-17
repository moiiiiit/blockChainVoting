import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

enum CandidateNames{
    MB("Mohit Bhole"), JH("Jacob Hill"), CJ("Charles Jackson"), RH("Robert Ehle"), TS("Tanner Seivart");
    protected String name;
    private long iD;

    CandidateNames(String namex){
        this.name = namex;
        this.iD = (long)(Math.random()*(1000000000000L));
    }

    public long getiD(){
        return this.iD;
    }

    public String getName(){
        return this.name;
    }

    public CandidateNames getCandidateFromId(long identification){
        for(CandidateNames s : CandidateNames.values()){
            if(s.iD == identification){
                return s;
            }
        }
        return null;
    }
}

public class MainGUI {
    private JTextField voterIDField;
    private JPanel panel1;
    private JTextField nameField;
    private JButton submitVoteButton;
    private JButton cancelButton;
    private JLabel Name;
    private JLabel voterId;
    private JLabel firstPriority;
    private JLabel secondPriority;
    private JLabel thirdPriority;
    private JLabel App;
    private JButton viewCandidates;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JComboBox comboBox3;
    private static int counter;
    public final static int maxEntriesPerBlock = 500;


    private void resetValues(){
        nameField.setText("");
        voterIDField.setText("");
        comboBox1.setSelectedIndex(-1);
        comboBox2.setSelectedIndex(-1);
        comboBox3.setSelectedIndex(-1);
        counter = 0;
    }

    private void addToBlock(String voterID, CandidateNames first, CandidateNames second, CandidateNames third) {
            //OPEN THE CURRENTBLOCK FILE TO READ GUI INPUT AND PRINT TO FILE
        try {
            String str = voterID + first.getiD() + "1" + second.getiD() + "2" + third.getiD() + "3";
            str = EncryptionX.encrypt(str);
            str = str + "\n";
            byte[] encryptedStr = str.getBytes();
            FileOutputStream writer = new FileOutputStream("currentBlock.txt", true);
            try {
                writer.write(encryptedStr);
            }
            catch (IOException e){
                return;
            }
        }
        catch (FileNotFoundException e){
            File file = new File("currentBlock.txt");
            try {
                file.createNewFile();
            }
            catch (IOException ex){
                return;
            }
        }




            counter++;
            if (counter >= maxEntriesPerBlock) {
                //SAVE TO MAIN FILE(BLOCK-CHAIN) FUNCTION

            }
    }

    public MainGUI() {

        for(CandidateNames s : CandidateNames.values()){
            comboBox1.addItem(s);
            comboBox2.addItem(s);
            comboBox3.addItem(s);
        }

        submitVoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                    JOptionPane.showMessageDialog(null, "Thank you for voting. Please close this window and proceed.", "Voting Application", JOptionPane.INFORMATION_MESSAGE);
                    addToBlock(voterIDField.getText(), (CandidateNames)comboBox1.getSelectedItem(), (CandidateNames)comboBox2.getSelectedItem(), (CandidateNames)comboBox3.getSelectedItem());
                    resetValues();
                }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Values Cleared. Please proceed if you don't want to vote, or have already voted.", "Voting Application", JOptionPane.INFORMATION_MESSAGE);
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
        counter = 0;
        frame.setVisible(true);

    }
}
