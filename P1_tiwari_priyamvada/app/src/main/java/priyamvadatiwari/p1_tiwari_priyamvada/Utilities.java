package priyamvadatiwari.p1_tiwari_priyamvada;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by priyamvadatiwari on 09/30/15.
 * Summary: This class aims to contain utility methods that can be required by the game
 * Title: Utilities.java
 */
public class Utilities {

    /**
     * Summary:      Fetches a list of the specified number of mutually exclusive random integers
     *               between a starting point and ending point: [start, end)
     * @param length - The length of the expected list of random numbers
     * @param start - The lower bound of the set in which we want to look for random numbers
     * @param end - The upper bound of the set in which we want to look for random numbers
     * @return an ArrayList of Integer type containing 'length' number of random numbers from
     *               the integer set : [start, end)
     */
    public ArrayList<Integer> getUniqueRandomsList(int length, int start, int end)   {
        Random rand = new Random();
        ArrayList<Integer> randList = new ArrayList<Integer>();

        while(randList.size() < length) {
            int r = rand.nextInt(end - start) + start;
            if(!randList.contains(r))    {
                randList.add(r);
            }
        }
        return randList;
    }
}
