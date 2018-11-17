/**
 * 
 */

public final class Ngram implements Comparable<Ngram>{
	public static final int NGRAM = 4;
	private final char[] ngram;
	private int occurrences;
	private int hashCode;
	
	public Ngram(char[] ngram, int occurrences){
		this.ngram = ngram;
		this.occurrences = occurrences;
		this.hashCode = (ngram[0]-1) * 2985984 +
						(ngram[1]-1) * 20736 +
						(ngram[2]-1) * 144 +
						(ngram[3]-1);
	}
	
	public char[] getNgramImage(){
		return ngram;
	}
	
	public int getNgramOccurrences(){
		return occurrences;
	}
	
	@Override
	public int hashCode(){
		return hashCode;
	}

	@Override
	public int compareTo(Ngram o) {
		return this.hashCode - o.hashCode();
	}
	
	@Override
	public String toString(){
		return '{'+Integer.toString(hashCode)+','+Integer.toString(occurrences)+'}';
	}
	
	public static int hashCode(char[] ngram){
		return (ngram[0]-1) * 2985984 +
			   (ngram[1]-1) * 20736 +
			   (ngram[2]-1) * 144 +
			   (ngram[3]-1);
	}
	
	public static int hashCode(int[] ngram){
		return (ngram[0]-1) * 2985984 +
			   (ngram[1]-1) * 20736 +
			   (ngram[2]-1) * 144 +
			   (ngram[3]-1);
	}
}