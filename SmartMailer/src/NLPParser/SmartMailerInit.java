/**
 *  Copyright 2015 Jasper Infotech (P) Limited . All Rights Reserved.
 *  JASPER INFOTECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package NLPParser;

import IntelligentThinker.IntelligentAnalyzer;

/**
 * @version 1.0, 04-Sep-2015
 * @author vikas
 */
public class SmartMailerInit {

    public static void main(String[] args) {
        IntelligentAnalyzer.getInstance().initES();
        Mailer.getInstance().fetchMail();
    }

}
