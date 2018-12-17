/**
 * Calculates the number of votes given a fileName input
 * File input expected 12DigitVoterID12DigitCandidateIDRankingNum
 */
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.HashMap;
public class calculateVotes{
    private String fileName; //Name of the file
    private HashMap<Integer,Integer> voteCount;

    /**
     *
     * @param fileName  The file name of which
     * @throws FileNotFoundException Throws exception if file with name is not found.
     */
    public calculateVotes(String fileName) throws FileNotFoundException
    {
        this.fileName = fileName;
        countVotes();
    }

    /**
     *
     * @param candidateID Candidate to get number of votes for
     * @return
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
     *
     * @throws FileNotFoundException
     */
    private void countVotes() throws FileNotFoundException
    {
        Scanner input = new Scanner(new File(fileName));
        String line = new Encryption().decrypt(input.nextLine());
        float voterID = Float.parseFloat(line.substring(0,11));
        float canaditeID = Float.parseFloat(line.substring(11,23));



    }
}
