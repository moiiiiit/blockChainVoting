import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


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
    private static int numberOfCurrentVotes;
    public final static int maxEntriesPerBlock = 500;
    private final double threshold=.9; 
    private final int voteSize=51;                                              //how many Characters is one vote
    private static boolean exit = false;
    private String myIp = "";                                                   //the public ip of the network private network I'm in
    private String[] ips = {"", ""};                                            //the public ip of every private network including my own
    private ServerSocket me;                                                    //this is to get connected to other machines and recieve things from them
    private ServerSocket receiveBlocks;
    private ArrayList<Socket> others;
    private ArrayList<Socket> transferBlocks; 

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
    private void addToBlock(String str) {                                      //adds data from form to currentblock.txt
            //OPEN THE CURRENTBLOCK FILE TO READ GUI INPUT AND PRINT TO FILE
        try {            
            str = str + "\r\n";
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
        numberOfCurrentVotes++;                                                 //this counter keeps count of the number of entries in currentBlock
        if(numberOfCurrentVotes >= maxEntriesPerBlock) {
            //SAVE TO MAIN FILE(BLOCK-CHAIN) FUNCTION
            try{
                String[] newBlock=getNextBlock();                               //this is the next block
                //FIXY FIXIE MOHITY
            }catch(CorruptedBlockException cbe){
                System.out.println("Votes were lost here. Please revote");
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
            File output = new File("blockChain.txt");   //copy from currentBlock to blockChain if counter reaches maxEntriesPerBlock       
            try {
                copyFileUsingChannel(input, output);
                numberOfCurrentVotes=0;
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
    /**CONSTRUCTOR
     * Establishes connection between all machines
     * 
     * 
     * @throws SocketException
     * @throws FileNotFoundException 
     */
    public MainGUI()throws SocketException, FileNotFoundException{
       this(3535);
    }
    public MainGUI(int port)throws SocketException, FileNotFoundException{
        try{
            me = new ServerSocket(port+3535);                                        //this is so other machines can try to establish a connection with me
            receiveBlocks = new ServerSocket(port+684392);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        others=new ArrayList<>();
        transferBlocks=new ArrayList<>();
PN:     for(int i = 0; i < ips.length; ++i)                                     //for every private voting network
            for(int j=0;true;++i)                                               //for every voting machine on the network?
                try{
                    if(3535+j != port){                                          //unless the machine is me
                        others.add(new Socket(ips[i], 3535+j));                 //try to establish a connection to a machine
                        transferBlocks.add(new Socket(ips[i], 684392+j));
                    }
                }catch(Exception ex){
                    System.out.println(ex.getMessage());
                    continue PN;
                }
        //me.setSoTimeout(40);
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
                            numberOfCurrentVotes=0;
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
                    String vote=EncryptionX.encrypt(voterIDField.getText() + 
                            ((CandidateNames)comboBox1.getSelectedItem()).getiD() + 
                            "1" + ((CandidateNames)comboBox2.getSelectedItem()).getiD() + 
                            "2" + ((CandidateNames)comboBox3.getSelectedItem()).getiD() + 
                            "3");
                    shareVote(vote.toCharArray());
                    addToBlock(vote);
                    
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
    /**
     * Call this function after someone has voted. It will copy the newest vote
     * to all other voting machines. This is a client side function
     *
     * @param vote the most recent vote
     * @throws IllegalArgumentException if the vote isn't right number of characters
     * @author Charles Jackson
     */
    public void shareVote(char[] vote){
        if(vote.length!=voteSize) throw new IllegalArgumentException("Vote must be "+voteSize+" characters");
        for(Socket oth : others){                                               //for all other machines
            if(oth != null){                                                    //if the machine isn't me
                try{
                    DataOutputStream dout = (DataOutputStream) oth.getOutputStream();//get the connection to the machine
                    for(int i=0; i<voteSize; ++i)                               //send the vote
                        dout.writeByte(vote[i]);
                }catch(Exception ex){
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
    /**
     * Call this to receive votes from other machines and store them on my block
     * This is a server side function
     * @author Charles Jackson
     */
    public void receiveVotes(){
        for(int i=0;i<others.size(); ++i){                                      //while there are connections
            try{
                Socket incomming = me.accept();                                 //try to get infromation from connections
                DataInputStream din = (DataInputStream) incomming.getInputStream();
                String vote="";
                while(din.available() >= voteSize)                              //while a machine is giving votes
                    for(int j = 0; j < voteSize; ++j){                          //get the vote
                        vote+=""+din.readChar();                                //and write it to my block
                    }
                addToBlock(vote);
            }catch(SocketTimeoutException tm){
                return;                                                         //exit function if there are no more connections
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }
    /**
     * Call this function after the block capacity has been reached Communicates
     * with other voting machines to decide if the next block has been corrupted
     * and if it should be added.
     *
     * @return the block to be added to this machine's block chain. Each element is a vote
     * @throws CorruptedBlockException if the error threshold was met
     * @author Charles Jackson
     */
    public String[] getNextBlock() throws CorruptedBlockException, FileNotFoundException, IOException{
        Scanner myBlockFile = new Scanner(new File("currentBlock.txt"));        //for reading in my block from a file
        String[] myBlock = new String[maxEntriesPerBlock];                      //to store my block
        String[][] blocks =new String[transferBlocks.size()][maxEntriesPerBlock];       //for storing the blocks for machines other than my own                                                 
        for(int i=0;myBlockFile.hasNextLine();++i)                                       
            myBlock[i]=myBlockFile.nextLine().trim();
        myBlockFile.close();
        Arrays.sort(myBlock);
        for(Socket oth : transferBlocks){                                               //send my sorted block to all machines
            DataOutputStream dout=(DataOutputStream) oth.getOutputStream();     //get the connection
            for(int i=0; i<maxEntriesPerBlock; ++i)                             //for my block
                for(int j=0;j <voteSize; ++j)                                   //for each of my votes
                    dout.writeChar(myBlock[i].charAt(j));                       //send each char
        }
        for(int i=0;i<transferBlocks.size(); ++i){                                      //recieve sorted blocks from other machines
            try{
                Socket incomming = me.accept();                                 //try to get infromation from connections
                DataInputStream din = (DataInputStream) incomming.getInputStream();
                for(int j=0; j<maxEntriesPerBlock; ++j)                         //read in one block
                    for(int k=0;k <voteSize; ++k)                               //read in one vote             
                        blocks[i][j]+=din.readChar();
            }catch(SocketTimeoutException tm){                                  //if no one is responding
                break;                                                          //continue to the next step
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }      
        double PPE=0;                                                           
        for(int i=0; i<blocks.length;++i){                                      //for all other blocks
            int errorCount=0;
            for(int j=0;j <maxEntriesPerBlock; ++j)                             //for every vote in the block
                if(!blocks[i][j].equals(myBlock[j]))                            //if there is a bad vote
                    errorCount+=1;                                              //Bad vote!
            PPE+=errorCount/(double)maxEntriesPerBlock;                         //keep a sum of the average difference between my block and other blocks
        }
        PPE/=(double)blocks.length;                                             //sum of the averages ÷ number blocks gotten from other machines(AKA number of other machines) = my Personal Percent Error
        double[] PPEs=new double[ips.length];                                   //error in PPE[i] is associated with machine with socket[i] 
        for(Socket oth : transferBlocks){                                               //send my PPE to all other machines
            DataOutputStream dout=(DataOutputStream) oth.getOutputStream();     //get the connection
            dout.writeDouble(PPE);
        }
        for(int i=0; i < transferBlocks.size(); ++i){                                   //receive PPE from all other machines
            try{
                Socket incomming=me.accept();                                   //get an incomming connection
                DataInputStream din=(DataInputStream)incomming.getInputStream();//get connection
                PPEs[i]=din.readDouble();                                       //get PPE
                incomming.getOutputStream().flush();
            }catch(SocketTimeoutException tm){                                  //if no one is responding
                break;                                                          //continue to the next step
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }
        double percentError=PPE;
        for(int i=0; i < PPEs.length; ++i)                                      //sum all the PPEs
            percentError+=PPEs[i];                                              
        percentError/=(double)(transferBlocks.size()+1);                                //the percent error for this block
        if(percentError>=threshold){                                            //if 90% of machines are ≥ 90% then add the        
            if(PPE < threshold){                                                //if my PPE is < 90% then my block is good and I should share it and return it
                //send my good block to corrupted machines(AKA all machines)
                for(Socket oth : transferBlocks){                                       //send my sorted block to all machines
                    DataOutputStream dout=(DataOutputStream) oth.getOutputStream();//get the connection
                    for(int i=0; i<maxEntriesPerBlock; ++i)                     //for my block
                        for(int j=0;j <voteSize; ++j)                           //for each of my votes
                            dout.writeChar(myBlock[i].charAt(j));               //send each char
                }                                                               //return my block
            }else{                                                              //else I need a non-corrupted block
                //receive a < 90% error block from another machine
                for(int i=0; i<transferBlocks.size();++i){
                    try{
                        Socket incomming=me.accept();                           //get incomming connection
                        DataInputStream dout=(DataInputStream)incomming.getInputStream();//get the connection
                        if(dout.available()!=0)                                 //if a good block was sent
                            for(int j=0; j<maxEntriesPerBlock; ++j)             //read in the good block
                                for(int k=0;k <voteSize; ++k)                   //read in one vote             
                                    blocks[i][j]+=dout.readChar();
                    }catch(SocketTimeoutException tm){                          //if no one is responding
                        break;                                                  //continue to the next step
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                }                                                               //return the good block                
            }
        }else{                                                                  //else this block is going to be trashed
            myBlock=null;                                                       //trash this block 
            throw new CorruptedBlockException();                                //throw error messege of trashed block    
        }
        return myBlock;        
    }
    public static void main(String[] args) {                                    //driver function
        frame.setPreferredSize(new Dimension(800, 400));
        try{
            frame.setContentPane(new MainGUI(Integer.parseInt(args[0])).panel1);
        }catch(Exception Ex){
            System.out.println(Ex.getMessage());
        }        
        frame.pack();
        numberOfCurrentVotes = 0;
        frame.setVisible(true);
    }
}