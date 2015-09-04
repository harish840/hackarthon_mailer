package NLPParser;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

/**
 * @version 1.0, 03-Sep-2015
 * @author vikas
 */
public class Parser {

    Parser parser = null;

    public Parser getInstance() {
        if (null == parser) {
            synchronized (this) {
                if (null == parser) {
                    parser = new Parser();
                }
            }
        }
        return parser;
    }

    public void parse() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String text = "I am happy";
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                String pos = token.get(PartOfSpeechAnnotation.class);
                //                String ne = token.get(NamedEntityTagAnnotation.class);
                //                System.out.println("word:" + word + " pos:" + pos + " ne:" + ne);
            }
            //            Tree tree = sentence.get(TreeAnnotation.class);
            //            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
            //            System.out.println(tree);
            //            System.out.println(dependencies);
            String sentiment = sentence.get(SentimentCoreAnnotations.ClassName.class);
            System.out.println(sentiment + "\t" + sentence);
        }
    }

}
