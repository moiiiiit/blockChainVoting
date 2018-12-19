import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;



enum CandidateNames{
    MOHITBHOLE("Mohit Bhole"), JACOBHILL("Jacob Hill"), CARLESJACKSON("Charles Jackson"), ROBERTEHLE("Robert Ehle"), TANNERSEIVART("Tanner Seivart");
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

    public static CandidateNames getCandidateFromId(long identification){
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
    private static JFrame frame = new JFrame("BlockChain Voting");
    private static int counter;
    public final static int maxEntriesPerBlock = 2;
    private static boolean exit = false;


    private void resetValues(){
        nameField.setText("");
        voterIDField.setText("");
        comboBox1.setSelectedIndex(-1);
        comboBox2.setSelectedIndex(-1);
        comboBox3.setSelectedIndex(-1);
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
            try {
                writer.close();
            }catch(Exception closee){

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
                File output = new File("blockChain.txt");
                File input = new File("currentBlock.txt");
                try {
                    copyFileUsingChannel(input, output);
                    counter=0;
                    FileOutputStream writer = new FileOutputStream(input);
                    writer.write(("").getBytes());
                    writer.close();
                }catch (Exception z){
                    return;
                }

                try {
                    CalculateVotes.countVotes("blockChain.txt");
                    System.out.println("Current Standing: ");
                }catch(Exception exc){
                    System.out.println("Calculating Votes failed.");
                }
            }
    }

    private static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destiChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destiChannel = new FileOutputStream(dest,true).getChannel();
            destiChannel.position( destiChannel.size() );
            sourceChannel.transferTo(0, sourceChannel.size(), destiChannel);
        }finally{
            sourceChannel.close();
            destiChannel.close();
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
                if((nameField.getText()).equals("NULL")&&(voterIDField.getText()).equals("000000000000")){
                    try {
                        //PUT TO BLOCKCHAIN
                        File output = new File("blockChain.txt");
                        File input = new File("currentBlock.txt");
                        try {
                            copyFileUsingChannel(input, output);
                            counter=0;
                            FileOutputStream writer = new FileOutputStream(input);
                            writer.write(("").getBytes());
                            writer.close();
                        }catch (Exception z){
                            return;
                        }
                        //
                        System.out.println(CalculateVotes.countVotes("blockChain.txt"));
                        System.out.println("Final Count: ");
                        exit = true;
                        frame.dispose();
                        return;
                    }catch (Exception exc){
                        System.out.println("Calculating Final Votes Failed.");
                        frame.dispose();
                        return;
                    }
                }
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
        frame.setPreferredSize(new Dimension(800, 400));
        frame.setContentPane(new MainGUI().panel1);
        frame.pack();
        counter = 0;
        frame.setVisible(true);
    }
}
