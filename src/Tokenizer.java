/**
 * 
 */

import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.OffsetLimitReachedException;

import org.eclipse.cdt.internal.core.parser.scanner.Token;
import org.eclipse.cdt.internal.core.parser.scanner.Lexer;
import org.eclipse.cdt.internal.core.parser.scanner.Lexer.LexerOptions;
import org.eclipse.cdt.internal.core.parser.scanner.ILexerLog;

import com.google.common.collect.TreeMultiset;

/**
 * @author Christhian
 *
 */
public final class Tokenizer{
	private final Lexer fRootLexer;
	private final List<Token> fTokenList = new LinkedList<Token>();
	private final TreeMultiset<char[]> nGramSet = TreeMultiset.create(new Comparazer());
	
	public static final int lNGRAM = Ngram.NGRAM - 1;
	public static final int lTOKEN = IToken.tEND_OF_INPUT;
	
	private static final Set<Integer> tList = new TreeSet<Integer>();
	
	/*
	 * Takes numbers in the range [0-143] to compare them.
	 */
	private class Comparazer implements Comparator<char[]>{
		@Override
		public int compare(char[] o1, char[] o2) {
			return  (o1[0]-o2[0]) * 2985984 +
					(o1[1]-o2[1]) * 20736 +
					(o1[2]-o2[2]) * 144 +
					(o1[3]-o2[3]);
		}
	}
	
	/*
	 * For more information about options visit:
	 * https://github.com/NVIDIA/cdt-nsight/blob/master/core/org.eclipse.cdt.core
	 * /parser/org/eclipse/cdt/core/dom/parser/IScannerExtensionConfiguration.java
	 */
	public Tokenizer(char[] code){
        this.fRootLexer = new Lexer(code, new LexerOptions(), ILexerLog.NULL, null);
	}
	
	public Tokenizer(String code){
        this.fRootLexer = new Lexer(code.toCharArray(), new LexerOptions(), ILexerLog.NULL, null);
	}
	
	/*
	 * 
	 */
	public List<Token> getTokenList(){
		if (!this.fTokenList.isEmpty())
			return this.fTokenList;

		Token cToken = this.fRootLexer.currentToken();
		while (cToken.getType() != lTOKEN){
			this.fTokenList.add(cToken);
			try {
				cToken = this.fRootLexer.nextToken();
			} catch (OffsetLimitReachedException e) {
				e.printStackTrace();
			}
		}
		this.fTokenList.add(cToken);
		
		return this.fTokenList;
	}
	
	/*
	 * 
	 */
	public TreeMultiset<char[]> getNgramSet(){
		if (!this.nGramSet.isEmpty())
			return this.nGramSet;
		
		// Negative elements filter
		List<Token> list = this.getTokenList().stream().
				filter(i -> i.getType() > 0).collect(Collectors.toList());
		assert list.size() > Ngram.NGRAM;
		
		for(int i = 0; i < Ngram.NGRAM; i++){
			ListIterator<Token> iter = list.listIterator(i);
			for(int j = i + Ngram.NGRAM; j < list.size(); j = j + Ngram.NGRAM){
				char [] ngram = new char[Ngram.NGRAM];
				for(int k = 0; k < Ngram.NGRAM; k++){
					int type = iter.next().getType();
					assert type < 144;
					ngram[k] = (char) type;
				}
				this.nGramSet.add(ngram);
			}
		}
		return this.nGramSet;
	}
	
	/*
	 *
	 */
	public List<Ngram> getOccurrenceList(){
		return this.getNgramSet().entrySet().stream()
				.map(e -> new Ngram(e.getElement(), e.getCount()))
				.collect(Collectors.toList());
	}

	/*
	 *
	 */
	private int nextToken() throws Exception{
		
		// Ignore errors and line breaks
		while(this.fRootLexer.nextToken().getType() < 0);
		
		// Get token
		int token =  this.fRootLexer.currentToken().getType();
		
		switch (token){
		// STRINGS (UTF16, UTF32) => STRING (L)
			case 5000:
			case 5001:
				token = 131;
				break;
		// CHAR (UTF16, UTF32) => CHAR (L)
			case 5002:
			case 5003:
				token = 133;
				break;
		// MIN and MAX operators ('<?' and '>?') => Not used. 
			case 152:
				token = 138;
				break;
			case 153:
				token = 142;
				break;
		}
		
		// If still greater than 144, maybe it's a problem...
		if (token > 144)
			throw new Exception("token: " + token + " item: " + this.fRootLexer.currentToken().getImage());
		
		return token;
	}

	/*
	 *
	 */
	public Set<Integer> getHashSet() throws Exception{
		Set<Integer> set = new HashSet<Integer>();
		
		// First N-Gram, it is supposed to exist
		int[] ngram = new int[Ngram.NGRAM];
		for(int i = 0; i < Ngram.NGRAM; i++)
			ngram[i] = this.nextToken();
		
		while(ngram[lNGRAM] != lTOKEN){
			set.add(Ngram.hashCode(ngram));
			
			// Move position
			for(int i = 0; i < lNGRAM; i++)
				ngram[i] = ngram[i+1];
			
			// No need to catch OffsetLimitReachedException, never happens
			ngram[lNGRAM] = this.nextToken();
		}
		
		return set;
	}
	
	public Set<Integer> getHashSetO(){
		return this.getNgramSet().elementSet().stream()
		.map(i -> Ngram.hashCode(i)).collect(Collectors.toSet());
	}
	
	// testing
	public static Set<Integer> get_tList(){
		return tList;
	}
}