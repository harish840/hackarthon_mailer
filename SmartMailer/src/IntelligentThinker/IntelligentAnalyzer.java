/**
 *  Copyright 2015 Jasper Infotech (P) Limited . All Rights Reserved.
 *  JASPER INFOTECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package IntelligentThinker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import Model.MailContent;
import Model.SentenceDetails;
import Model.SentenceDetails.WordDetails;
import NLPParser.Mailer;

import com.hc.es.core.api.AdminServiceImpl;
import com.hc.es.core.api.IndexingServiceImpl;
import com.hc.es.core.api.SearchServiceImpl;
import com.hc.es.core.base.AdminService;
import com.hc.es.core.base.IndexingService;
import com.hc.es.core.base.SearchService;
import com.hc.es.core.utils.Document;

/**
 * @version 1.0, 04-Sep-2015
 * @author harish
 */
public class IntelligentAnalyzer {
    private AdminService    adminService    = null;
    private IndexingService indexingService = null;
    private SearchService   searchService   = null;
    private final String    HOST            = "127.0.0.1";
    private final Integer   PORT            = 9300;
    private final String    HRDB            = "hr-db";
    private final String    HRDBTYPE        = "words";
    private final String    ITDB            = "it-db";
    private final String    ITDBTYPE        = "words";
    private final String    FINANCEDB       = "finance-db";
    private final String    FINANCEDBTYPE   = "words";
    private static IntelligentAnalyzer analyzer;
    
    public static IntelligentAnalyzer getInstance() {
        if (null == analyzer) {
            synchronized (Mailer.class) {
                if (null == analyzer) {
                    analyzer = new IntelligentAnalyzer();
                }
            }
        }
        return analyzer;
    }

    private TransportClient getTransportClient() {
        Settings settings = ImmutableSettings.settingsBuilder().put("client.transport.sniff", true).put("cluster.name", "elasticsearch").build();
        TransportClient transportClient = new TransportClient(settings);
        transportClient.addTransportAddress(new InetSocketTransportAddress(HOST, PORT));
        return transportClient;
    }

    private boolean initHrDb() {
        try {
            if (!adminService.isIndexExist(HRDB, HRDBTYPE)) {
                adminService.createIndex(HRDB, HRDBTYPE, null);
            }
        } catch (Exception e) {
            System.out.println("Exception while initialize hr db");
            return false;
        }
        return true;

    }

    private boolean initItDb() {
        try {
            if (!adminService.isIndexExist(ITDB, ITDBTYPE)) {
                adminService.createIndex(ITDB, ITDBTYPE, null);
            }
        } catch (Exception e) {
            System.out.println("could not initialize it db");
            return false;
        }
        return true;
    }

    private boolean initFinanceDb() {
        try {
            if (!adminService.isIndexExist(FINANCEDB, FINANCEDBTYPE)) {
                adminService.createIndex(FINANCEDB, FINANCEDBTYPE, null);
            }
        } catch (Exception e) {
            System.out.println("Exception while initializing finance db");
            return false;
        }
        return true;
    }

    private boolean initializeScoreDbs() {
        if (initHrDb() && initFinanceDb() && initItDb()) {
            System.out.println("initialized all dbs");
            return true;
        } else {
            System.out.println("could not initialize dbs");
            return false;
        }

    }

    public boolean initES() {
        try {
            TransportClient transportClient = getTransportClient();
            adminService = new AdminServiceImpl(transportClient);
            indexingService = new IndexingServiceImpl(transportClient);
            searchService = new SearchServiceImpl(transportClient);
        } catch (Exception e) {
            System.out.println("could not initialize elastic search");
            return false;
        }
        if (!initializeScoreDbs()) {
            System.out.println("could not initialize dbs.Hence existing");
            System.exit(1);
        }
        return true;
    }

    public String indexMailObject(MailContent mail) {
        int hrCounter = 0;
        int itCounter = 0;
        int financeCounter = 0;
        String emailId = null;
        indexMailDetails(mail);
        indexSentenceDetails(mail);
        for (SentenceDetails sentence : mail.getBody()) {
            for (WordDetails word : sentence.getWordDetails()) {
                long hrDbScore = getDbWOrdScore(word, HRDB, HRDBTYPE);
                long itDbScore = getDbWOrdScore(word, ITDB, ITDBTYPE);
                long financeDbScore = getDbWOrdScore(word, FINANCEDB, FINANCEDBTYPE);
                if ((hrDbScore > itDbScore) && (hrDbScore > financeDbScore)) {
                    indexWordToRespectiveDb(word, HRDB, HRDBTYPE);
                    hrCounter++;
                }
                if ((itDbScore > hrDbScore) && (itDbScore > financeDbScore)) {
                    indexWordToRespectiveDb(word, ITDB, ITDBTYPE);
                    itCounter++;
                }
                if ((financeDbScore > itDbScore) && (financeDbScore > hrDbScore)) {
                    indexWordToRespectiveDb(word, FINANCEDB, FINANCEDBTYPE);
                    itCounter++;
                }
            }
        }

        if ((hrCounter > itCounter) && (hrCounter > financeCounter)) {
            emailId = "hr@snapdeal.com";
        }
        if ((itCounter > financeCounter) && (itCounter > hrCounter)) {
            emailId = "it@snapdeal.com";
        }
        if ((financeCounter > itCounter) && (financeCounter > hrCounter)) {
            emailId = "finance@snapdeal.com";
        }
        return emailId;
    }

    private void processNounWord(WordWeight wordWeight, WordDetails word) {
        NounCounter nounCounter = wordWeight.getNounCounters();
        HashMap<String, Long> determinantMap = nounCounter.getDeterminantMap();
        if (null != determinantMap) {
            for (String determinant : word.getDeterminantsList()) {
                if (determinantMap.containsKey(determinant)) {
                    determinantMap.put(determinant, determinantMap.get(determinant) + 1);//handle null
                } else {
                    determinantMap.put(determinant, 1l);
                }
            }
        }
        nounCounter.setDeterminantMap(determinantMap);
        nounCounter.setCounter(nounCounter.getCounter() + 1);
        wordWeight.setNounCounters(nounCounter);
        wordWeight.setTotalCounter(wordWeight.getTotalCounter() + 1);
    }

    private void processAdjectiveWord(WordWeight wordWeight, WordDetails word) {
        AdjectiveCounter adjectiveCounter = wordWeight.getAdjectiveCounters();
        HashMap<String, Long> determinantMap = adjectiveCounter.getDeterminantMap();
        if (null != determinantMap) {
            for (String determinant : word.getDeterminantsList()) {
                if (determinantMap.containsKey(determinant)) {
                    determinantMap.put(determinant, determinantMap.get(determinant) + 1);
                } else {
                    determinantMap.put(determinant, 1l);
                }
            }
        }
        adjectiveCounter.setDeterminantMap(determinantMap);
        adjectiveCounter.setCounter(adjectiveCounter.getCounter() + 1);
        wordWeight.setAdjectiveCounters(adjectiveCounter);
        wordWeight.setTotalCounter(wordWeight.getTotalCounter() + 1);
    }

    private void processVerbWord(WordWeight wordWeight, WordDetails word) {
        VerbCounter verbCounter = wordWeight.getVerbCounters();
        HashMap<String, Long> determinantMap = verbCounter.getDeterminantMap();
        if (null != determinantMap) {
            for (String determinant : word.getDeterminantsList()) {
                if (determinantMap.containsKey(determinant)) {
                    determinantMap.put(determinant, determinantMap.get(determinant) + 1);
                } else {
                    determinantMap.put(determinant, 1l);
                }
            }
        }
        verbCounter.setDeterminantMap(determinantMap);
        verbCounter.setCounter(verbCounter.getCounter() + 1);
        wordWeight.setVerbCounters(verbCounter);
        wordWeight.setTotalCounter(wordWeight.getTotalCounter() + 1);
    }

    private long getDbWOrdScore(WordDetails word, String department, String departmentType) {
        List<Document> docs = searchService.search(department, departmentType, word.getWord());

        if (null != docs && docs.size() > 0) {
            for (Document document : docs) {
                Map<String, Object> fieldsMap = document.getFields();
                if (((String) fieldsMap.get("word")).equalsIgnoreCase(word.getWord())) {
                    if (fieldsMap.containsKey("score")) {
                        WordWeight wordWeight = (WordWeight) fieldsMap.get("score");
                        if (word.getPartOfSpeech().equalsIgnoreCase("NOUN")) {
                            return wordWeight.getNounCounters().getCounter() / wordWeight.getTotalCounter();
                        }
                        if (word.getPartOfSpeech().equalsIgnoreCase("ADJECTIVE")) {
                            return wordWeight.getAdjectiveCounters().getCounter() / wordWeight.getTotalCounter();
                        }
                        if (word.getPartOfSpeech().equalsIgnoreCase("VERB")) {
                            return wordWeight.getVerbCounters().getCounter() / wordWeight.getTotalCounter();
                        }
                    }
                }
            }
        }

        return 0l;
    }

    private void indexWordToRespectiveDb(WordDetails word, String department, String departmentType) {

        List<Document> docs = searchService.search(department, departmentType, word.getWord());

        if (null != docs && docs.size() > 0) {
            for (Document document : docs) {
                Map<String, Object> fieldsMap = document.getFields();
                if (((String) fieldsMap.get("word")).equalsIgnoreCase(word.getWord())) {
                    if (fieldsMap.containsKey("score")) {
                        WordWeight wordWeight = (WordWeight) fieldsMap.get("score");
                        if (word.getPartOfSpeech().equalsIgnoreCase("NOUN")) {
                            processNounWord(wordWeight, word);
                        }
                        if (word.getPartOfSpeech().equalsIgnoreCase("ADJECTIVE")) {
                            processAdjectiveWord(wordWeight, word);
                        }
                        if (word.getPartOfSpeech().equalsIgnoreCase("VERB")) {
                            processVerbWord(wordWeight, word);
                        }

                        HashMap<String, Object> contentMap = new HashMap<String, Object>();
                        contentMap.put("word", word.getWord());
                        contentMap.put("score", wordWeight);
                        Document doc = new Document(contentMap);

                        try {
                            indexingService.index(department, departmentType, doc, null);
                        } catch (IOException e) {
                            System.out.println("Exception while indexing mail");
                        }
                    }
                }

            }
        } else {
            WordWeight weight = new WordWeight();
            if (word.getPartOfSpeech().equalsIgnoreCase("NOUN")) {
                NounCounter nounCOunter = weight.getNounCounters();
                nounCOunter.setCounter(1l);
                for (String determinantWord : word.getDeterminantsList()) {
                    nounCOunter.getDeterminantMap().put(determinantWord, 1l);
                }
                weight.setNounCounters(nounCOunter);
            }
            if (word.getPartOfSpeech().equalsIgnoreCase("ADJECTIVE")) {
                AdjectiveCounter adjectiveCOunter = weight.getAdjectiveCounters();
                adjectiveCOunter.setCounter(1l);
                for (String determinantWord : word.getDeterminantsList()) {
                    adjectiveCOunter.getDeterminantMap().put(determinantWord, 1l);
                }
                weight.setAdjectiveCounters(adjectiveCOunter);
            }

            if (word.getPartOfSpeech().equalsIgnoreCase("VERB")) {
                VerbCounter verbCOunter = weight.getVerbCounters();
                verbCOunter.setCounter(1l);
                for (String determinantWord : word.getDeterminantsList()) {
                    verbCOunter.getDeterminantMap().put(determinantWord, 1l);
                }
                weight.setVerbCounters(verbCOunter);
            }

            weight.setTotalCounter(1l);
            HashMap<String, Object> contentMap = new HashMap<String, Object>();
            contentMap.put("word", word.getWord().toLowerCase());
            contentMap.put("score", weight);
            Document doc = new Document(contentMap);

            try {
                indexingService.index(HRDB, HRDBTYPE, doc, null);
            } catch (IOException e) {
                System.out.println("Exception while indexing mail");
            }
        }

    }

    public void indexWord(WordDetails word, String id) {
        //TODO: fetch doc and update
        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        contentMap.put("id", id);
        contentMap.put(word.getWord(), word.getPartOfSpeech());
        Document document = new Document(contentMap);
        List<Document> documents = new ArrayList<>();
        documents.add(document);
        try {
            indexingService.index("mail-analyzer", "mail-words", documents, null);
        } catch (IOException e) {
            System.out.println("Exception while indexing mail");
        }

    }

    private boolean indexSentenceDetails(MailContent mail) {
        try {
            for (SentenceDetails sentence : mail.getBody()) {
                for (WordDetails word : sentence.getWordDetails()) {
                    indexWord(word, mail.getUniqueId());
                }
            }
        } catch (Exception e) {
            System.out.println("exception ehile indexing word");
        }

        return true;
    }

    private boolean indexMailDetails(MailContent mail) {

        HashMap<String, Object> contentMap = new HashMap<String, Object>();
        contentMap.put("id", mail.getUniqueId());
        contentMap.put("from", mail.getSender());
        contentMap.put("subject", mail.getSubject().toString());
        Document document = new Document(contentMap);
        List<Document> documents = new ArrayList<>();
        documents.add(document);
        try {
            indexingService.index("mail-analyzer", "mails", documents, null);
        } catch (IOException e) {
            System.out.println("Exception while indexing mail");
        }
        return false;

    }

}
