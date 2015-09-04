/**
 *  Copyright 2015 Jasper Infotech (P) Limited . All Rights Reserved.
 *  JASPER INFOTECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package IntelligentThinker;


/**
 * @version 1.0, 04-Sep-2015
 * @author harish
 */
public class WordWeight {

    private long             TotalCounter;
    private NounCounter      nounCounters;
    private VerbCounter      verbCounters;
    private AdjectiveCounter adjectiveCounters;

    public long getTotalCounter() {
        return TotalCounter;
    }

    public void setTotalCounter(long totalCounter) {
        TotalCounter = totalCounter;
    }

    public NounCounter getNounCounters() {
        return nounCounters;
    }

    public void setNounCounters(NounCounter nounCounters) {
        this.nounCounters = nounCounters;
    }

    public VerbCounter getVerbCounters() {
        return verbCounters;
    }

    public void setVerbCounters(VerbCounter verbCounters) {
        this.verbCounters = verbCounters;
    }

    public AdjectiveCounter getAdjectiveCounters() {
        return adjectiveCounters;
    }

    public void setAdjectiveCounters(AdjectiveCounter adjectiveCounters) {
        this.adjectiveCounters = adjectiveCounters;
    }

}
