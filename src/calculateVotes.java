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
    private static HashMap<Long,Long> voteCount;
    /**
     *  Counts votes based on file passed into the initializing
     * @throws FileNotFoundException If file is not found an exception is thrown
     */
    public static String countVotes(String fileName) throws FileNotFoundException
    {
        Scanner input = new Scanner(new File(fileName));
        while(input.hasNextLine()) {
            String line = new EncryptionX().decrypt(input.nextLine());
            //long voterID = Long.parseLong(line.substring(0, 12)); //Don't need voter ID because assuming all voterID's are only used once.
            long candidateID = Long.parseLong(line.substring(12, 24));
            System.out.println(CandidateNames.getCandidateFromId(candidateID));
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

        String list="";
        for(Long key:voteCount.keySet())
        {
            list=CandidateNames.getCandidateFromId(key)+" "+voteCount.get(key)+"\n";
        }
        return list;




    }
}
