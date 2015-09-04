/**
 *  Copyright 2015 Jasper Infotech (P) Limited . All Rights Reserved.
 *  JASPER INFOTECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0, 04-Sep-2015
 * @author vikas
 */
public class SentenceDetails {

    List<WordDetails> words = new ArrayList<>();
    
    public List<WordDetails> getWordDetails(){
        return words;
    }
    
    public void addWordDetails(String word, String partOfSpeech){
        WordDetails wordDetails = new WordDetails();
        wordDetails.setPartOfSpeech(partOfSpeech);
        wordDetails.setWord(word);
        words.add(wordDetails);
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(WordDetails word: words){
            sb.append(word.getWord());
            sb.append(":");
            sb.append(word.getPartOfSpeech());
            sb.append(",");
        }
        return sb.toString();
    }
    public class WordDetails {
        private String word;
        private String partOfSpeech;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getPartOfSpeech() {
            return partOfSpeech;
        }

        public void setPartOfSpeech(String partOfSpeech) {
            this.partOfSpeech = partOfSpeech;
        }
    }
}
