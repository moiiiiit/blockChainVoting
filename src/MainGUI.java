import com.sun.xml.internal.ws.wsdl.parser.MexEntityResolver;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
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
        this.iD = (long) (Math.random() * (1000000000000L));
    }

    public long getiD(){
        return this.iD;
    }       //yaknow

    public String getName(){
        return this.name;
    }   //yaknow

    public static CandidateNames getCandidateFromId(long identification){   //this function returns the candidateName enum for a particular candidate id
        for(CandidateNames s : CandidateNames.values())
            if(s.iD == identification)
                return s;
        return null;
    }
}

public class MainGUI{
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
    public final static int blockSize = 500;
    private final double threshold = .9;
    private final int voteSize = 51;                                              //how many Characters is one vote
    private final int MaxNumberOfMachinesPerNetwork = 10;
    private final int numberOfMachines = 4;
    private static boolean exit = false;
    private String myIp = "70.121.56.92";                                       //the public ip of the network private network I'm in
    private int port;
    private String[] ips = {"70.121.56.92"};                                            //the public ip of every private network including my own
    private DatagramSocket me;                                                  //this is to get connected to other machines and recieve things from them
    private DatagramSocket receiveBlocks;

    private void resetValues(){             //dont care about this. this resets values when cancel/submit is clicked
        nameField.setText("");
        voterIDField.setText("");
        comboBox1.setSelectedIndex(-1);
        comboBox2.setSelectedIndex(-1);
        comboBox3.setSelectedIndex(-1);
    }

    /**
     * adds data from form to currentblock.txt
     * @param voterID
     * @param first
     * @param second
     * @param third CHARLES BOI YOU MIGHT NEED TO TWEAK THIS FUNCTION
     */
    private void addToBlock(String str){
        //OPEN THE CURRENTBLOCK FILE TO READ GUI INPUT AND PRINT TO FILE
        try{
            str = str + "\r\n";
            byte[] encryptedStr = str.getBytes();
            FileOutputStream writer = new FileOutputStream("currentBlock.txt", true);   //write to file
            try{
                writer.write(encryptedStr);
            }catch(IOException e){
                return;
            }
            try{
                writer.close();
            }catch(Exception closee){

            }
        }catch(FileNotFoundException e){
            File file = new File("currentBlock.txt");
            try{
                file.createNewFile();
            }catch(IOException ex){
                return;
            }
        }
        numberOfCurrentVotes++;                                                 //this counter keeps count of the number of entries in currentBlock
        if(numberOfCurrentVotes >= blockSize)
            //SAVE TO MAIN FILE(BLOCK-CHAIN) FUNCTION
            try{
                String[] newBlock = getNextBlock();                             //this is the next block
                for(String vote : newBlock){
                    FileOutputStream writer = new FileOutputStream(new File("blockChain.txt"));
                    writer.write((vote + "\r\n").getBytes());
                    writer.close();
                }
                FileOutputStream writer = new FileOutputStream(new File("currentBlock.txt"));                  //ERASES THE FILE
                writer.write(("").getBytes());
                writer.close();
                numberOfCurrentVotes = 0;
            }catch(CorruptedBlockException cbe){
                System.out.println("Votes were lost here. Please revote");
                File corruptedVotes = new File("corruptedVotes.txt");
                File currentBlock = new File("currentBlock.txt");
                try{
                    copyFileUsingChannel(currentBlock, corruptedVotes);
                    FileOutputStream writer = new FileOutputStream(currentBlock);                                       //ERASES THE FILE
                    writer.write(("").getBytes());
                    writer.close();
                    numberOfCurrentVotes = 0;
                }catch(Exception exce){
                    System.out.println("Copying corrupted votes to file failed.");
                }

                //add
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        /*try {
                CalculateVotes.countVotes("blockChain.txt");
                System.out.println("Current Standing: ");
            }catch(Exception exc){
                System.out.println("Calculating Votes failed.");
            }*/
    }

    private static void copyFileUsingChannel(File source, File dest) throws IOException{       //IGNORE it just copies one file to another
        FileChannel sourceChannel = null;
        FileChannel destiChannel = null;
        try{
            sourceChannel = new FileInputStream(source).getChannel();
            destiChannel = new FileOutputStream(dest, true).getChannel();
            destiChannel.position(destiChannel.size());
            sourceChannel.transferTo(0, sourceChannel.size(), destiChannel);
        }finally{
            sourceChannel.close();
            destiChannel.close();
        }
    }
    /**
     * CONSTRUCTOR Establishes connection between all machines
     *
     *
     * @throws SocketException
     * @throws FileNotFoundException
     */
    public MainGUI() throws SocketException, FileNotFoundException{
        this(3535);
    }
    public MainGUI(int port) throws SocketException, FileNotFoundException{
        try{
            System.out.println("Opening servers");
            this.port = port;
            me = new DatagramSocket(port + 3535);                               //this is so other machines can send to me
            receiveBlocks = new DatagramSocket(port + 4242 );
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        for(CandidateNames s : CandidateNames.values()){
            comboBox1.addItem(s);
            comboBox2.addItem(s);
            comboBox3.addItem(s);
        }

        submitVoteButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if((nameField.getText()).equals("NULL") && (voterIDField.getText()).equals("000000000000")){
                    try{
                        //PUT TO BLOCKCHAIN
                        File output = new File("blockChain.txt");
                        File input = new File("currentBlock.txt");
                        try{
                            copyFileUsingChannel(input, output);
                            numberOfCurrentVotes = 0;
                            FileOutputStream writer = new FileOutputStream(input);
                            writer.write(("").getBytes());
                            writer.close();
                        }catch(Exception z){
                            return;
                        }
                        //
                        System.out.println(CalculateVotes.countVotes("blockChain.txt"));
                        System.out.println("Final Count: ");
                        exit = true;

                    }catch(Exception exc){
                        System.out.println("Calculating Final Votes Failed.");
                    }
                    frame.dispose();
                    return;
                }
                JOptionPane.showMessageDialog(null, "Thank you for voting. Please close this window and proceed.", "Voting Application", JOptionPane.INFORMATION_MESSAGE);
                String vote = EncryptionX.encrypt(voterIDField.getText()
                        + ((CandidateNames) comboBox1.getSelectedItem()).getiD()
                        + "1" + ((CandidateNames) comboBox2.getSelectedItem()).getiD()
                        + "2" + ((CandidateNames) comboBox3.getSelectedItem()).getiD()
                        + "3");
                shareVote(vote);
                addToBlock(vote);

                resetValues();
            }
        });
        cancelButton.addActionListener(new ActionListener(){                    //cancel button
            @Override
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog(null, "Values Cleared. Please proceed if you don't want to vote, or have already voted.", "Voting Application", JOptionPane.INFORMATION_MESSAGE);
                resetValues();
            }
        });
        viewCandidates.addActionListener(new ActionListener(){                  //view candidates button
            @Override
            public void actionPerformed(ActionEvent e){
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
     * @throws IllegalArgumentException if the vote isn't right number of
     * characters
     * @author Charles Jackson
     */
    public void shareVote(String vote){
        if(vote.length() != voteSize)
            throw new IllegalArgumentException("Vote must be " + voteSize + " characters");
        for(int i = 0; i < ips.length; ++i){                                    //for all other machines
            byte[] data = new byte[voteSize];                                   //cast vote to a byte array
            for(int j = 0; j < voteSize; ++j)
                data[j] = (byte) vote.charAt(j);
            try{
                for(int j = 0; j < MaxNumberOfMachinesPerNetwork; ++j)
                    if(j != port)                                               //if it's not me
                        me.send(new DatagramPacket(data, data.length,           //send the vote
                                InetAddress.getByName(ips[i]), 3535 + j));
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }
    /**
     * Call this to receive votes from other machines and store them on my block
     * This is a server side function
     * @author Charles Jackson
     */
    public void receiveVotes(){
        while(true)                                                             //for all machines
            try{                                                                //try to get a vote from them
                DatagramPacket o = new DatagramPacket(new byte[voteSize], voteSize);
                me.receive(o);                                                  //try to get infromation from connections
                byte[] data = o.getData();
                String vote = "";
                for(int j = 0; j < voteSize; ++j)                               //get the vote
                    vote += "" + data[j];                                       //and write it to my block
                addToBlock(vote);
            }catch(SocketTimeoutException tm){
                break;                                                          //exit function if there are no more connections
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
    }
    /**
     * Call this function after the block capacity has been reached Communicates
     * with other voting machines to decide if the next block has been corrupted
     * and if it should be added.
     *
     * @return the block to be added to this machine's block chain. Each element
     * is a vote
     * @throws CorruptedBlockException if the error threshold was met
     * @author Charles Jackson
     */
    public String[] getNextBlock() throws CorruptedBlockException, FileNotFoundException, IOException{
        Scanner myBlockFile = new Scanner(new File("currentBlock.txt"));        //for reading in my block from a file
        String[] myBlock = new String[blockSize];                               //to store my block
        String[][] blocks = new String[ips.length][blockSize];                  //for storing the blocks for machines other than my own [machine][a vote in that machine's block]
        for(int i = 0; myBlockFile.hasNextLine(); ++i)
            myBlock[i] = myBlockFile.nextLine().trim();
        myBlockFile.close();
        Arrays.sort(myBlock);
        byte[] data = new byte[blockSize * voteSize];                           //cast block to a byte array
        for(int i = 0; i < blockSize; ++i)
            for(int j = 0; j < myBlock.length; ++j)
                data[i] = (byte) myBlock[i].charAt(i);
        //send my sorted block to all machines
        for(int i = 0; i < ips.length; i++)                                     //for every network
            try{                                                                //send my sorted block
                for(int j = 0; j < MaxNumberOfMachinesPerNetwork; ++j)
                    if(j != port)                                               //if it's not me
                        me.send(new DatagramPacket(data, data.length,           //send the vote
                                InetAddress.getByName(ips[i]), 4242 + j));
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        //recieve sorted blocks from other machines
        for(int i = 0; i < blocks.length; ++i)
            try{
                DatagramPacket o = new DatagramPacket(                          //to receive blocks from others
                        new byte[blockSize * voteSize], blockSize * voteSize);
                receiveBlocks.receive(o);                                       //receive one block
                for(int j = 0; j < blockSize; ++j)                              //save the block
                    for(int k = 0; k < voteSize; ++k)
                        blocks[i][i] += o.getData()[j * k];
            }catch(SocketTimeoutException tm){                                  //if no one is responding
                continue;                                                       //continue to the next step
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        double PPE = 0;
        for(int i = 0; i < blocks.length; ++i){                                 //for all other blocks
            int errorCount = 0;
            for(int j = 0; j < blockSize; ++j)                                  //for every vote in the block
                if(!blocks[i][j].equals(myBlock[j]))                            //if there is a bad vote
                    errorCount += 1;                                            //Bad vote!
            PPE += errorCount / (double) blockSize;                             //keep a sum of the average difference between my block and other blocks
        }
        PPE /= (double) blocks.length;                                          //sum of the averages ÷ number blocks gotten from other machines(AKA number of other machines) = my Personal Percent Error
        double[] PPEs = new double[ips.length];                                 //error in PPE[i] is associated with machine with socket[i]
        for(int i = 0; i < ips.length; ++i)                                     //send my PPE to all other machines
            for(int j = 0; j < MaxNumberOfMachinesPerNetwork; ++j)
                if(j != port)                                                   //if it's not me
                    me.send(new DatagramPacket(toBytes(PPE), 8, InetAddress.getByName(ips[i]), j + 4242));
        for(int i = 0; i < PPEs.length; ++i)                                    //receive PPE from all other machines
            try{
                DatagramPacket o = new DatagramPacket(new byte[8], 8);
                receiveBlocks.receive(o);
                PPEs[i] = toDouble(o.getData());                                //get PPE
            }catch(SocketTimeoutException tm){                                  //if no one is responding
                break;                                                          //continue to the next step
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        double percentError = PPE;
        for(int i = 0; i < PPEs.length; ++i)                                    //sum all the PPEs
            percentError += PPEs[i];
        percentError /= (double) (numberOfMachines);                            //the percent error for this block
        if(percentError >= threshold)                                           //if 90% of machines are ≥ 90% then add the
            if(PPE < threshold)                                                 //if my PPE is < 90% then my block is good and I should share it and return it
                //send my good block to corrupted machines(AKA all machines)
                for(int i = 0; i < ips.length; i++)                             //for every network
                    try{
                        for(int j = 0; j < MaxNumberOfMachinesPerNetwork; ++j)
                            if(j != port)                                       //if it's not me
                                me.send(new DatagramPacket(data, data.length,   //send the vote
                                        InetAddress.getByName(ips[i]), 4242 + j));
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    } //return my block
            else                                                                //else I need a non-corrupted block
                try{                                                            //receive a < 90% error block from another machine
                    DatagramPacket o = new DatagramPacket(                      //to receive blocks from others
                            new byte[blockSize * voteSize], blockSize * voteSize);
                    receiveBlocks.receive(o);                                   //receive one block
                    for(int i = 0; i < blockSize; ++i)                          //save the block
                        for(int j = 0; j < voteSize; ++j)
                            myBlock[i] += o.getData()[i * j];
                }catch(Exception ex){
                    System.out.println(ex.getMessage());
                } //return the good block
        else{                                                                   //else this block is going to be trashed
            myBlock = null;                                                     //trash this block
            throw new CorruptedBlockException();                                //throw error messege of trashed block
        }
        //flush out the data stream
        for(int i = 0; i < numberOfMachines; ++i)
            try{
                DatagramPacket o = new DatagramPacket( //to receive blocks from others
                        new byte[blockSize * voteSize], blockSize * voteSize);
                receiveBlocks.receive(o);                                       //receive one block
            }catch(SocketTimeoutException tm){                                  //if no one is responding
                continue;                                                       //continue to the next step
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        return myBlock;
    }
    public static byte[] toBytes(double value){
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }

    public static double toDouble(byte[] bytes){
        return ByteBuffer.wrap(bytes).getDouble();
    }
    public static void main(String[] args){                                     //driver function
        frame.setPreferredSize(new Dimension(800, 400));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        try{
            frame.setContentPane(new MainGUI(Integer.parseInt(args[0])).panel1);
        }catch(Exception Ex){
            System.out.println(Ex + ": " + Ex.getMessage());
        }
        frame.pack();
        numberOfCurrentVotes = 0;
        frame.setVisible(true);
    }
}