//Charles Jackson
/**
 * This Exception is to be thrown after miners have finished comparing blocks 
 * and determined that the percent error on this block is greater than the threshold
 * @author Charles Jackson
 */
public class CorruptedBlockException extends Exception{
    public CorruptedBlockException(){
        super("The block was corrupted and should not be added");
    }
    public CorruptedBlockException(String m){
        super(m);
    }
}
