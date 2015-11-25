import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.*;
import opennlp.tools.util.Span;

/**
 * @author Francesca Frontini LIP6 - UPMC, Paris/ILC - CNR - Pisa*
 */


public class Main {
	//TODO improve this stemming algorithm
	public static String stemString(String token){
		String[] suffixes = {"ae","am","as","em","es","is","os","on","en","in","um","us","im","a","e","i","o","u"};
		String prefix = "";
		Boolean in = false;
		int i = 0;
		while (i < suffixes.length && !in) {
			if(token.endsWith(suffixes[i])){
			prefix = token.substring(0, token.length() - suffixes[i].length());
			in = true;
			}
			i++;
		}
		
		
		return prefix;
		
	}
	
	public static Span[] statNamedEntityFinder(String[] sentence) {

		InputStream modelIn;
		try {
			modelIn = new FileInputStream("models/lat-ner-2.bin");


				TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
				System.out.println("Loading the model...");
				NameFinderME nameFinder = new NameFinderME(model);

				Span nameSpans[] = nameFinder.find(sentence);

				

				nameFinder.clearAdaptiveData();
				
				System.out.println("Finished...");

				return nameSpans;



		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}

	}
	
	public static Span[] dictNamedEntityFinder(String[] sentence) {

		InputStream modelIn;
		try {
			boolean caseSensitive = false;

			InputStream dictFileIn = new FileInputStream("dicts/dictionary.xml");
			
			//argument case sensitive deprecated, maybe remove
			Dictionary dictIn = new Dictionary(dictFileIn,caseSensitive);

			TokenNameFinder dictPlaceFinder  = new DictionaryNameFinder(dictIn);

			Span  [] nameSpans = dictPlaceFinder.find(sentence);

			return nameSpans;


		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}

	}

	public static Map<String, List<String>> createLinkingIndex() {

		System.out.println("entering: retrieveMentionsURIsFromDico");
		Map<String, List<String>> out = new HashMap<String, List<String>>();
		try {

			CSVReader reader = new CSVReader(new InputStreamReader(
					new FileInputStream("dicts/index_pleiades.csv"), "UTF-8"),'\t', CSVWriter.NO_QUOTE_CHARACTER);
			String [] line;
			while ((line = reader.readNext()) != null) {
				List<String> l = new ArrayList<String>();
					for (int k = 1; k < line.length; k++) {
						l.add(line[k]);
					}
						out.put(line[0], l);				       

			}
			reader.close();
				

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}


	public static void main(String[] args) {
		
		try{
			BufferedReader br = 
	                //READS STANDARD INPUT, FOR THE JAR
					//new BufferedReader(new InputStreamReader(System.in));
					//UNCOMMENT THIS TO TEST FROM ECLIPSE
					new BufferedReader(new FileReader("input.txt"));
	 
			String input;
			
			List<String>  sentence= new ArrayList<String>() ;
	 
			while((input=br.readLine())!=null){
				//System.out.println(input);
				sentence.add(input);
				
			}
			
			List<String>  stemmedSentence= new ArrayList<String>() ;
			 
				
			//String[] sentenceArray = (String[]) sentence.toArray();
			String[] sentenceArray = new 	String[sentence.size()] ;
			for (int i=0; i<sentence.size(); i++) 
			{
				sentenceArray[i]=sentence.get(i);
			}

			String[] stemmedSentenceArray = new 	String[sentence.size()] ;
			for (int i=0; i<sentence.size(); i++) 
			{
				stemmedSentenceArray[i]=stemString(sentence.get(i));
			}
			
			Map<String, List<String>> mentionsWithURIs = createLinkingIndex();

			//System.out.println(tokenizeString("Antium"));
			
			Span nameSpans[] = statNamedEntityFinder(sentenceArray);
			
			Span dictNameSpans [] = dictNamedEntityFinder(stemmedSentenceArray);

			
			
			/*

			for (int i=0; i<sentence.size(); i++) {
				String line = sentenceArray[i] + "\t";

				boolean hasMatchStat = false;
				boolean hasMatchDict = false;

				for (int j=0; j<nameSpans.length; j++) {
					//System.out.println("We are printing...");
					//System.out.println(sentence[i]);
					//System.out.println(nameSpans[j].toString());
					//System.out.println(nameSpans[i].getType());
					//System.out.println(nameSpans[j].getStart());
					//System.out.println(nameSpans[j].getEnd());
					if(nameSpans[j].getStart()==i){
						line += "B-STAT ";
						hasMatchStat = true;
					}
					else if(nameSpans[j].getEnd()==i+1){
						line += "E-STAT ";
						hasMatchStat = true;
					}
					else if(nameSpans[j].contains(i)){
						line += "I-STAT ";
						hasMatchStat = true;
					}
					
				}
				
				for (int w=0; w<dictNameSpans.length; w++) {
					
					
					if(dictNameSpans[w].getStart()==i){
						line += "B-DICT ";
						hasMatchDict = true;
					}
					else if(dictNameSpans[w].getEnd()==i+1){
						line += "E-DICT ";
						hasMatchDict = true;
					}
					else if(dictNameSpans[w].contains(i)){
						line += "I-DICT ";
						hasMatchDict = true;
					}
					
				}

				
				if(hasMatchStat || hasMatchDict){

					if (mentionsWithURIs.containsKey(stemString(sentenceArray[i]))) {
						line += "\t" +stemString(sentenceArray[i]) + "\t" + mentionsWithURIs.get(stemString(sentenceArray[i])).toString();   // no comma

					}

				}
				System.out.println(line);
			}
							*/
			
			System.out.println("Result produced by the STATISTICAL model\n");
			System.out.println("offset\tform\tURIS");
			for (int i=0; i<nameSpans.length; i++) {
				String offset = nameSpans[i].getStart() + " " +nameSpans[i].getEnd();
				//System.out.println(nameSpans[i].getType());
				String mention = "";
				for (int j=nameSpans[i].getStart(); j < nameSpans[i].getEnd(); j++ ){
					mention += stemmedSentenceArray[j];
				}
				offset += "\t" +mention;

				if (mentionsWithURIs.containsKey(mention)) {
					offset +=  "\t" + mentionsWithURIs.get(mention).toString();   // no comma

				}

				System.out.println(offset);

			}
			
			System.out.println("\n\nResult produced by the LOOKUP dictionary\n");
			System.out.println("offset\tform\tURIS");

			for (int i=0; i<dictNameSpans.length; i++) {
				String offset = dictNameSpans[i].getStart() + " " +dictNameSpans[i].getEnd();
				//System.out.println(nameSpans[i].getType());
				String mention = "";
				for (int j=dictNameSpans[i].getStart(); j < dictNameSpans[i].getEnd(); j++ ){
					mention += stemmedSentenceArray[j] + " ";
				}
				mention = mention.trim();
				offset += "\t" +mention;

				if (mentionsWithURIs.containsKey(mention)) {
					offset +=  "\t" + mentionsWithURIs.get(mention).toString();   // no comma

				}

				System.out.println(offset);
				//System.out.println(nameSpans[j].getEnd());
			}

			}catch(IOException io){
				io.printStackTrace();
			}			

		}
	
}




