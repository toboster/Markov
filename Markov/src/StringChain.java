import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * CS-251-004  11/9/17
 * @author Tony Nguyen
 * Represents the Markov Chain, maps expectes iterator 
 * and generates output based on map.
 * Example table for order 2 chain from input: Quickly, he ran and he ran until
 * he stopped.
 * Prefix                Suffixes and Probability
    NONWORD NONWORD      Quickly, (100%)
    NONWORD              Quickly, he (100%)
    Quickly, he          ran (100%)
    he ran               and (50%), until (50%)
    ran and              he (100%)
    ran until            he (100%)
    until he             stopped. (100%)
    he stopped.          NONWORD (100%)
    stopped. NONWORD     NONWORD (100%)
 */
public class StringChain {

    private final static String nonword = "";
    private final int order;
    private HashMap<Prefixkey, Suffixprob> chain = new HashMap<>();

    /**
     * 
     * @param order
     */
    public StringChain(int order) {
        this.order = order;
    }

    /**
     * generates list of n random chars or words
     * @param n
     * @param rand
     * @return
     */
    public List<String> generate(int n, Random rand) {
        List<String> output = new LinkedList<>();
        // initially filled objects list with nonwords
        Prefixkey currentstate = new Prefixkey(listFillnon());
        String suffix = "";
        int count = 0;
        // printMap();

        output.addAll(currentstate.getList());
        // loops until n words is satisfied, makes sure to exclude non words
        // from total n
        while (count <= n) {
            //obtains value with key object
            Suffixprob sufxobj = chain.get(currentstate);
            suffix = sufxobj.getRandSuffix(rand);

            // sends resulting suffix to output
            output.add(suffix);
            
            if(!(suffix.equals(nonword))){
                count++;               
            }
            // adjust current state using class methods
            currentstate.removeFirst();
            currentstate.add(suffix);

        }

        return output;

    }

    /**
     * given iterator maps chain, uses a list to keep track
     * of prefix and suffix
     * @param itemIter
     */
    public void addItems(Iterator<String> itemIter) {
        
        // new list full of nonwords
        List<String> initList = listFillnon();
        String suffix = "";

        while (itemIter.hasNext()) {

            suffix = itemIter.next();
            // System.out.println(suffix);
            // maps, see put method
            put(initList, suffix);

            // adjust list for next ieration
            initList.remove(0);
            initList.add(suffix);

        }

        // pads the ending
        for (int i = 0; i < this.order; i++) {

            put(initList, nonword);
            initList.remove(0);
            initList.add(nonword);
        }

    }

    /**
     * used by addItems to map given list and suffix
     * @param prefixList
     * @param suffix
     */
    void put(List<String> prefixList, String suffix) {

        List<String> newPrefixList = new LinkedList<>();

        newPrefixList.addAll(prefixList);
        // System.out.println(newPrefixList);

        Prefixkey pfxObj = new Prefixkey(newPrefixList);
        Suffixprob sfxObj = new Suffixprob(suffix);

        // System.err.println(chain.containsKey(pfxObj));
        
        // checks if object doesnt exist
        if (chain.get(pfxObj) == null) {

            chain.put(pfxObj, sfxObj);

            // System.out.println(chain.get(pfxObj));
        } else {
            // duplicate case, add to repeatkey's value
            Suffixprob repeatKeyValue = chain.get(pfxObj);
            // adds suffix to list in value object
            repeatKeyValue.add(suffix);

        }
    }

    /**
     * returns a list that is filled with order number of nonwords
     * 
     * @param
     */
    private List<String> listFillnon() {
        List<String> listOfNonw = new LinkedList<>();

        for (int i = 0; i < this.order; i++) {
            listOfNonw.add(nonword);
        }

        return listOfNonw;

    }

    /**
     * Objects maps contains list of strings that could contain spaces,
     * had to trim strings in respective list to compare,
     * used when overriding equals in Prefixkey class
     * @param s1
     * @param s2
     * @return
     */
    private boolean trimStringEqual(List<String> s1, List<String> s2) {

        Iterator<String> i1 = s1.iterator();
        Iterator<String> i2 = s2.iterator();

        // invalid size, thus these are not equal
        if (s1.size() != s2.size()) {
            return false;
        }

        // if strings are not equal returns early
        while (i1.hasNext() && i2.hasNext()) {
            String temp1 = i1.next();
            String temp2 = i2.next();

            if (!(temp1.trim().equals(temp2.trim()))) {
                return false;
            }

        }

        // if it made it here, all trimmed strings are equal for s1 and s2
        return true;

    }

    /**
     * given to me by Kage, a TA for cs251, used for debugging purposes.
     */
    private void printMap() {
        for (Prefixkey p : this.chain.keySet()) {
            System.out.println("Prefix: ");
            for (String s : p.getList()) {
                System.out.println("\"" + s + "\" ");

            }
            System.out.println("\n   Suffix: ");
            for (String s : this.chain.get(p).getList()) {
                System.out.println("\"" + s + "\" ");
            }
            System.out.println("\n");
        }
    }

    /**
     * Used as a key in map
     * @author Tony
     *
     */
    class Prefixkey {

        private List<String> prefixlist = new LinkedList<>();

        public Prefixkey(List<String> prefixlist) {

            this.prefixlist = prefixlist;
        }

        public Prefixkey() {
            for (int i = 0; i < order; i++) {
                prefixlist.add(nonword);
            }
        }

        private void add(String s) {
            prefixlist.add(s);
        }

        private void removeFirst() {
            prefixlist.remove(0);
        }

        private List<String> getList() {
            return prefixlist;
        }

        /**
        * 
        */
        @Override
        public boolean equals(Object p) {
            if (p == this) {
                return true;
            }
            if (!(p instanceof Prefixkey)) {
                return false;
            }

            Prefixkey other = (Prefixkey) p;
            return trimStringEqual(this.prefixlist, other.prefixlist);

        }

        /**
        * trimmed all the strings and then computed hashCode with new list,
        * possibly could have done it a better a way but could'nt think of 
        * a way for equals and hascode override.
        */
        @Override
        public int hashCode() {
            List<String> temp = new LinkedList<>();
            for (String s : this.prefixlist) {
                temp.add(s.trim());
            }

            return temp.hashCode();
        }

    }

    /**
     * Used as values in a map that contains mapped suffix
     *
     */
    class Suffixprob {
        private List<String> suffixlist = new ArrayList<>();

        public Suffixprob(String s) {
            suffixlist.add(s);
        }

        private void add(String s) {
            suffixlist.add(s);
        }

        private String getRandSuffix(Random rand) {
            // gets random number with upper bound of size, then access array

            int length = suffixlist.size();
            int result = rand.nextInt(length);
            return suffixlist.get(result);

        }

        private List<String> getList() {
            return suffixlist;
        }

    }

}
