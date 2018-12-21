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


//THIS ENUM CONTAINS ALL CANDIDATE NAMES WITH RANDOMLY GENERATED CANDIDATE IDs WHICH WILL BE PUT INTO THE FILES
enum CandidateNames{
    MOHITBHOLE("Mohit Bhole"), JACOBHILL("Jacob Hill"), CARLESJACKSON("Charles Jackson"), ROBERTEHLE("Robert Ehle"), TANNERSEIVART("Tanner Seivart");   //HARDCODED
    protected String name;              //CANDIDATE NAME JUST CUZ
    private long iD;                    //iD

    CandidateNames(String namex){
        this.name = namex;
        this.iD = (long)(Math.random()*(1000000000000L));
    }

    public long getiD(){
        return this.iD;
    }       //yaknow

    public String getName(){
        return this.name;
    }   //yaknow

    public static CandidateNames getCandidateFromId(long identification){   //this function returns the candidateName enum for a particular candidate id
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


    private void resetValues(){             //dont care about this. this resets values when cancel/submit is clicked
        nameField.setText("");
        voterIDField.setText("");
        comboBox1.setSelectedIndex(-1);
        comboBox2.setSelectedIndex(-1);
        comboBox3.setSelectedIndex(-1);
    }

    /**
     *
     * @param voterID
     * @param first
     * @param second
     * @param third
     * CHARLES BOI YOU MIGHT NEED TO TWEAK THIS FUNCTION
     */
    private void addToBlock(String voterID, CandidateNames first, CandidateNames second, CandidateNames third) {        //adds data from form to currentblock.txt
            //OPEN THE CURRENTBLOCK FILE TO READ GUI INPUT AND PRINT TO FILE
        try {
            String str = voterID + first.getiD() + "1" + second.getiD() + "2" + third.getiD() + "3";
            str = EncryptionX.encrypt(str);
            str = str + "\n";
            byte[] encryptedStr = str.getBytes();
            FileOutputStream writer = new FileOutputStream("currentBlock.txt", true);   //write to file
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


            counter++;                                  //this counter keeps count of the number of entries in currentBlock
            if (counter >= maxEntriesPerBlock) {
                //SAVE TO MAIN FILE(BLOCK-CHAIN) FUNCTION
                File output = new File("blockChain.txt");   //copy from currentBlock to blockChain if counter reaches maxEntriesPerBlock
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

    private static void copyFileUsingChannel(File source, File dest) throws IOException {       //IGNORE it just copies one file to another
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

                    }catch (Exception exc){
                        System.out.println("Calculating Final Votes Failed.");
                    }
                    frame.dispose();
                    return;
                }
                    JOptionPane.showMessageDialog(null, "Thank you for voting. Please close this window and proceed.", "Voting Application", JOptionPane.INFORMATION_MESSAGE);
                    addToBlock(voterIDField.getText(), (CandidateNames)comboBox1.getSelectedItem(), (CandidateNames)comboBox2.getSelectedItem(), (CandidateNames)comboBox3.getSelectedItem());
                    resetValues();
                }
        });
        cancelButton.addActionListener(new ActionListener() {   //cancel button
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Values Cleared. Please proceed if you don't want to vote, or have already voted.", "Voting Application", JOptionPane.INFORMATION_MESSAGE);
                resetValues();
            }
        });
        viewCandidates.addActionListener(new ActionListener() { //view candidates button
            @Override
            public void actionPerformed(ActionEvent e) {
                Candidates dialog = new Candidates();
                dialog.open();
            }
        });
    }

    public static void main(String[] args) {                    //driver function
        frame.setPreferredSize(new Dimension(800, 400));
        frame.setContentPane(new MainGUI().panel1);
        frame.pack();
        counter = 0;
        frame.setVisible(true);
    }
}
