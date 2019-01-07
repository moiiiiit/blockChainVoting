import java.io.*;
import java.net.*;
import java.util.*;
//@author Charles Jackson
public class Client{
    final PrintWriter block;
    final int maxEntriesPerBlock=500;                                                    //how many votes are in one block
    final int voteSize=51;                                                      //how many Characters is one vote
    final double threshold=.9;                                                  //percent error allowed in the blockchain
    String myIp = "";                                                           //the public ip of the network private network I'm in
    String[] ips = {"", ""};                                                    //the public ip of every private network including my own
    ServerSocket me;                                                            //this is to get connected to other machines and recieve things from them
    ArrayList<Socket> others;                                                   //these are for sending things to other machines

    public static void main(String[] args) throws IOException{
    }
    /**
     * Establishes a connection with all other machines
     * @throws SocketException
     * @throws FileNotFoundException 
     */
    public Client() throws SocketException, FileNotFoundException{
       this(3535);
    }
    /**
     * Establishes a connection with all other machines
     * @param port the port number to be used. Should be 3535 for the first voting machine on the network and increment for other machines
     * @throws SocketException
     * @throws FileNotFoundException 
     */
    public Client(int port) throws SocketException, FileNotFoundException{
        try{
            me = new ServerSocket(port);                                        //this is so other machines can try to establish a connection with me
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        others=new ArrayList<>();
PN:     for(int i = 0; i < ips.length; ++i)                                     //for every private voting network
            for(int j=0;true;++i)                                               //for every voting machine on the network?
                try{
                    if(3535+j != port)                                          //unless the machine is me
                        others.add(new Socket(ips[i], 3535+j));                 //try to establish a connection to a machine
                }catch(Exception ex){
                    System.out.println(ex.getMessage());
                    break PN;
                }
        //me.setSoTimeout(40);
        block = new PrintWriter(new File("block.txt"));
    }
    /**
     * Call this to receive votes from other machines and store them on my block
     * This is a server side function
     */
    public void receiveVotes(){
        for(int i=0;i<others.size(); ++i){                                      //while there are connections
            try{
                Socket incomming = me.accept();                                 //try to get infromation from connections
                DataInputStream din = (DataInputStream) incomming.getInputStream();
                while(din.available() >= voteSize)                              //while a machine is giving votes
                    for(int j = 0; j < voteSize; ++j)                           //get the vote
                        block.append(din.readChar() + "\r\n");                  //and write it to my block
            }catch(SocketTimeoutException tm){
                return;                                                         //exit function if there are no more connections
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }
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
        //possibly add new vote to my block?
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
        Scanner myBlockFile = new Scanner(new File("block.txt"));               //for reading in my block from a file
        String[] myBlock = new String[maxEntriesPerBlock];                               //to store my block
        String[][] blocks =new String[others.size()][maxEntriesPerBlock];                //for storing the blocks for machines other than my own                                                 
        for(int i=0;myBlockFile.hasNextLine();++i)                                       
            myBlock[i]=myBlockFile.nextLine();
        Arrays.sort(myBlock);
        for(Socket oth : others){                                               //send my sorted block to all machines
            DataOutputStream dout=(DataOutputStream) oth.getOutputStream();     //get the connection
            for(int i=0; i<maxEntriesPerBlock; ++i)                                      //for my block
                for(int j=0;j <voteSize; ++j)                                   //for each of my votes
                    dout.writeChar(myBlock[i].charAt(j));                       //send each char
        }
        for(int i=0;i<others.size(); ++i){                                      //recieve sorted blocks from other machines
            try{
                Socket incomming = me.accept();                                 //try to get infromation from connections
                DataInputStream din = (DataInputStream) incomming.getInputStream();
                for(int j=0; j<maxEntriesPerBlock; ++j)                                  //read in one block
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
            for(int j=0;j <maxEntriesPerBlock; ++j)                                      //for every vote in the block
                if(!blocks[i][j].equals(myBlock[j]))                            //if there is a bad vote
                    errorCount+=1;                                              //Bad vote!
            PPE+=errorCount/(double)maxEntriesPerBlock;                                  //keep a sum of the average difference between my block and other blocks
        }
        PPE/=(double)blocks.length;                                             //sum of the averages ÷ number blocks gotten from other machines(AKA number of other machines) = my Personal Percent Error
        double[] PPEs=new double[ips.length];                                   //error in PPE[i] is associated with machine with socket[i] 
        for(Socket oth : others){                                               //send my PPE to all other machines
            DataOutputStream dout=(DataOutputStream) oth.getOutputStream();     //get the connection
            dout.writeDouble(PPE);
        }
        for(int i=0; i < others.size(); ++i){                                   //receive PPE from all other machines
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
        percentError/=(double)(others.size()+1);                                //the percent error for this block
        if(percentError>=threshold){                                            //if 90% of machines are ≥ 90% then add the        
            if(PPE < threshold){                                                //if my PPE is < 90% then my block is good and I should share it and return it
                //send my good block to corrupted machines(AKA all machines)
                for(Socket oth : others){                                       //send my sorted block to all machines
                    DataOutputStream dout=(DataOutputStream) oth.getOutputStream();//get the connection
                    for(int i=0; i<maxEntriesPerBlock; ++i)                              //for my block
                        for(int j=0;j <voteSize; ++j)                           //for each of my votes
                            dout.writeChar(myBlock[i].charAt(j));               //send each char
                }                                                               //return my block
            }else{                                                              //else I need a non-corrupted block
                //receive a < 90% error block from another machine
                for(int i=0; i<others.size();++i){
                    try{
                        Socket incomming=me.accept();                               //get incomming connection
                        DataInputStream dout=(DataInputStream)incomming.getInputStream();//get the connection
                        if(dout.available()!=0)                                     //if a good block was sent
                            for(int j=0; j<maxEntriesPerBlock; ++j)                          //read in the good block
                                for(int k=0;k <voteSize; ++k)                       //read in one vote             
                                    blocks[i][j]+=dout.readChar();
                    }catch(SocketTimeoutException tm){                                  //if no one is responding
                        break;                                                          //continue to the next step
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
}