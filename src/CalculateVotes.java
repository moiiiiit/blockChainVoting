/**
 * Calculates the number of votes given a fileName input
 * File input expected 12DigitVoterID12DigitCandidateIDRankingNum
 */
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.HashMap;


public class CalculateVotes{
    private String fileName; //Name of the file
    private HashMap<Long,Long> voteCount;

    /**
     *
     * @param fileName  The file name of the file to be read
     * @throws FileNotFoundException Throws exception if file with name is not found.
     */
    public CalculateVotes(String fileName) throws FileNotFoundException
    {
        this.fileName = fileName;
        countVotes();
    }

    /**
     *
     * @param candidateID Candidate to get number of votes for
     * @return return
     */
    public int getVoteCount(int candidateID) throws NoSuchElementException{
        Object value = voteCount.get(candidateID);
        if(value!=null)
            return (Integer)value;
        else
        {
            throw new NoSuchElementException("No candidate with such ID was found");
        }
    }

    /**
     *  Counts votes based on file passed into the initializing
     * @throws FileNotFoundException If file is not found an exception is thrown
     */
    private void countVotes() throws FileNotFoundException
    {
        Scanner input = new Scanner(new File(fileName));
        while(input.hasNextLine()) {
            String line = new EncryptionX().decrypt(input.nextLine());
            //long voterID = Long.parseLong(line.substring(0, 12)); //Don't need voter ID because assuming all voterID's are only used once.
            long candidateID = Long.parseLong(line.substring(12, 24));
            int rank = Integer.parseInt(line.substring(24, 25));

            if(voteCount.get(candidateID)!=null)
                voteCount.put(candidateID,(long)rank);
            else
                voteCount.put(candidateID,voteCount.get(candidateID)+rank);


            candidateID= Long.parseLong(line.substring(25, 37));
            rank = Integer.parseInt(line.substring(37, 38));

            if(voteCount.get(candidateID)!=null)
                voteCount.put(candidateID,(long)rank);
            else
                voteCount.put(candidateID,voteCount.get(candidateID)+rank);



            candidateID= Long.parseLong(line.substring(38, 50));
            rank = Integer.parseInt(line.substring(50, 51));

            if(voteCount.get(candidateID)!=null)
                voteCount.put(candidateID,(long)rank);
            else
                voteCount.put(candidateID,voteCount.get(candidateID)+rank);
        }



    }
}
