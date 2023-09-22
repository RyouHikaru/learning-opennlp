package org.tapioca.nlp;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.StringReader;
import java.util.Scanner;

public class JapaneseKuromoji {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Options:\n(1) Tokenizer\n(2) Analyzer\n(3) Exit\n\nChoice: ");
                String option = scanner.nextLine();

                if (option.equals("3"))
                    break;

                System.out.print("Enter a sentence/phrase: ");
                String inputText = scanner.nextLine();

                if (option.equals("1")) {
                    initiateAnalysis(inputText);
                } else if (option.equals("2")) {
                    initiateAnalysis(inputText);
                }

                System.out.println();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void initiateTokenization(String text) throws Exception {
        // Create a Japanese Tokenizer
        JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, true, JapaneseTokenizer.Mode.NORMAL);

        // Set the text to be tokenized
        tokenizer.setReader(new StringReader(text));

        // Tokenize
        TokenStream tokenStream = tokenizer;
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        // Process and print the tokens
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String token = charTermAttribute.toString();
            System.out.println("Token: " + token);
        }

        tokenStream.close();
        tokenizer.close();
    }

    public static void initiateAnalysis(String text) throws Exception {
        // Create a Japanese Analyzer
        JapaneseAnalyzer analyzer = new JapaneseAnalyzer();

        // Analyze the text
        TokenStream tokenStream = analyzer.tokenStream("content", text);
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute positionIncrementAttribute = tokenStream.addAttribute(PositionIncrementAttribute.class);
        TypeAttribute typeAttribute = tokenStream.addAttribute(TypeAttribute.class);
        PartOfSpeechAttribute partOfSpeechAttribute = tokenStream.addAttribute(PartOfSpeechAttribute.class);

        // Process and print the analysis results
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String token = charTermAttribute.toString();
            int positionIncrement = positionIncrementAttribute.getPositionIncrement();
            String type = typeAttribute.type();
            String pos = partOfSpeechAttribute.getPartOfSpeech();

            System.out.println("Token: " + token);
            System.out.println("Position Increment: " + positionIncrement);
            System.out.println("Type: " + type);
            System.out.println("POS: " + pos);
        }

        tokenStream.close();
        analyzer.close();
    }
}
