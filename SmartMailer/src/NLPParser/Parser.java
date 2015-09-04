package NLPParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import Model.Email;
import Model.MailContent;
import Model.SentenceDetails;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * @version 1.0, 03-Sep-2015
 * @author vikas
 */
public class Parser {

    private static int uniqueId;
    private static Parser parser = null;
    private static StanfordCoreNLP pipeline;
    
    public static Parser getInstance() {
        if (null == parser) {
            synchronized (Parser.class) {
                if (null == parser) {
                    parser = new Parser();
                    parser.init();
                }
            }
        }
        return parser;
    }

    private synchronized int getUniqueId(){
        uniqueId++;
        return uniqueId;
    }
    
    private void init(){
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
         pipeline = new StanfordCoreNLP(props);
    }
    public MailContent parse(Email email) {
        MailContent content = new MailContent();
        content.setDate(email.getFromDate());
        content.setSender(email.getSender());
        //Adding subject
        System.out.println("Parsing subject");
        List<SentenceDetails> subject = new ArrayList<>();
        subject.add(getParsedContent(email.getSubject(), pipeline));
        content.setSubject(subject);
        System.out.println("Parsing body");
        List<SentenceDetails> body = new ArrayList<>();
        body.add(getParsedContent(email.getBody(), pipeline));
        content.setBody(body);
        content.setUniqueId("" + getUniqueId());
        return content;
    }

    private SentenceDetails getParsedContent(String input, StanfordCoreNLP pipeline) {
        SentenceDetails subject = new SentenceDetails();
        Annotation document = new Annotation(input);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                String pos = token.get(PartOfSpeechAnnotation.class);
                System.out.println("Word:" + word + " pos:"+pos);
                subject.addWordDetails(word, pos);
            }
            //            String sentiment = sentence.get(SentimentCoreAnnotations.ClassName.class);
            //            System.out.println(sentiment + "\t" + sentence);
        }
        return subject;
    }

}
