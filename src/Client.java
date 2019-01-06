import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
//@author Charles Jackson
public class Client{
    final PrintWriter block;
    final int blockSize=500;  
    final double threshold=.9;
    String myIp = "";                                                           //the public ip of the network private network i'm in
    String[] ips = {"", ""};                                                    //the public ip of every other private network including my own
    ServerSocket me;                                                            //this is to receive things from all other machines
    ArrayList<Socket> others;                                                   //these are for sending things to other machines

    public static void main(String[] args) throws IOException{
    }
    /**
     * Establishes a connection with all other machines
     * @param port the port number to be used. Should be 3535 for the first voting machine on the network and increment for other machines
     */
    public Client(int port) throws SocketException, FileNotFoundException{
        try{
            me = new ServerSocket(port);                                        //this is so other machines can try to establish a connection with me
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        others=new ArrayList<>();
        for(int i = 0; i < ips.length; ++i){                                    //for every private voting network
            for(int j=0;true;++i)                                               //for ever voting machine on the network?
                try{
                    if(3535+j != port)                                          //unless the machine is me
                        others.add(new Socket(ips[i], 3535+j));                 //try to establish a connection to a machine
                }catch(Exception ex){
                    System.out.println(ex.getMessage());
                    break;
                }
        }
        me.setSoTimeout(40);
        block = new PrintWriter(new File("block.txt"));
    }
    /**
     * Call this to receive votes from other machines and store them on my block
     * This is a server side function
     */
    public void receiveVotes(){
        while(true){                                                            //while there are connections
            try{
                Socket incomming = me.accept();                                 //try to get infromation from connections
                DataInputStream din = (DataInputStream) incomming.getInputStream();
                while(din.available() >= 51)                                    //while a machine is giving votes
                    for(int i = 0; i < 51; ++i)                                 //get the vote
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
     * @throws IllegalArgumentException if the vote isn't formated correctly
     * @author Charles Jackson
     */
    public void shareVote(char[] vote){
        if(vote.length!=51) throw new IllegalArgumentException("Vote must be 51 characters");
        for(Socket oth : others){                                               //for all other machines
            if(oth != null){                                                    //if the machine isn't me
                try{
                    DataOutputStream dout = (DataOutputStream) oth.getOutputStream();//get the connection to the machine
                    for(int i=0; i<51; ++i)                                     //send the vote
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
        Scanner myBlockFile = new Scanner(new File("block.txt"));               //for reading in my block
        String[] myBlock = new String[blockSize];                               //to store my block
        String[][] blocks =new String[others.size()][blockSize];                //for storing the blocks for machines other than my own                                                 
        for(int i=0;myBlockFile.hasNextLine();++i)                                       
            myBlock[i]=myBlockFile.nextLine();
        Arrays.sort(myBlock);
        for(Socket oth : others){                                               //send my sorted block to all machines
            DataOutputStream dout=(DataOutputStream) oth.getOutputStream();     //get the connection
            for(int i=0; i<blockSize; ++i)                                      //for my block
                for(int j=0;j <51; ++j)                                         //for each of my votes
                    dout.writeChar(myBlock[i].charAt(j));                       //send each char
        }
        for(int i=0; i<others.size(); ++i){                                     //recieve sorted blocks from other machines
            DataInputStream dout=(DataInputStream) others.get(i).getInputStream();//get the connection
            for(int j=0; j<blockSize; ++j)                                      //read in one block
                for(int k=0;k <51; ++k)                                         //read in one vote             
                    blocks[i][j]+=dout.readChar();
        }                                                               
        double PPE=0;                                                           
        for(int i=0; i<blocks.length;++i){                                      //for all other blocks
            int errorCount=0;
            for(int j=0;j <blockSize; ++j)                                      //for every vote in the block
                if(!blocks[i][j].equals(myBlock[j]))                            //if there is a bad vote
                    errorCount+=1;                                              //Bad vote!
            PPE+=errorCount/(double)blockSize;                                  //keep a sum of the average difference between my block and other blocks
        }
        PPE/=(double)blocks.length;                                             //sum of the averages ÷ number blocks gotten from other machines(AKA number of other machines) = my Personal Percent Error
        double[] PPEs=new double[ips.length];                                   //error in PPE[i] is associated with machine with socket[i] 
        for(Socket oth : others){                                               //send my PPE to all other machines
            DataOutputStream dout=(DataOutputStream) oth.getOutputStream();     //get the connection
            dout.writeDouble(PPE);
        }
        for(int i=0; i < others.size(); ++i){                                   //receive PPE from all other machines
            DataInputStream din=(DataInputStream)others.get(i).getInputStream();//get connection
            PPEs[i]=din.readDouble();                                           //get PPE
            others.get(i).getOutputStream().flush();
        }
        double percentError=PPE;
        for(int i=0; i < PPEs.length; ++i)                                      //sum all the PPEs
            percentError+=PPEs[i];                                              
        percentError/=(double)(others.size()+1);                                //the percent error for this block
        if(percentError>=threshold){                                            //if 90% of machines are ≥ 90% then add the        
            if(PPE < threshold){                                                //if my PPE is < 90% then my block is good and I should share it and return it
                //give my good block to corrupted machines(AKA all machines)
                for(Socket oth : others){                                       //send my sorted block to all machines
                    DataOutputStream dout=(DataOutputStream) oth.getOutputStream();//get the connection
                    for(int i=0; i<blockSize; ++i)                              //for my block
                        for(int j=0;j <51; ++j)                                 //for each of my votes
                            dout.writeChar(myBlock[i].charAt(j));               //send each char
                }                                                               //return my block
            }else{                                                              //else I need a non-corrupted block
                //get a < 90% error block from another machine
                for(int i=0; i<others.size();++i){
                    DataInputStream dout=(DataInputStream) others.get(i).getInputStream();//get the connection
                    if(dout.available()!=0)                                     //if a good block was sent
                        for(int j=0; j<blockSize; ++j)                          //read in the good block
                            for(int k=0;k <51; ++k)                             //read in one vote             
                                blocks[i][j]+=dout.readChar();
                }                                                               //return the good block                
            }
        }else{                                                                  //else this block is going to be trashed
            myBlock=null;                                                       //trash this block 
            throw new CorruptedBlockException();                                //throw error messege of trashed block    
        }
        return myBlock;        
    }
}
