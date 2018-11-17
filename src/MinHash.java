import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;
/**
 * @author Christhian
 * Based on 
 * https://github.com/tdebatty/java-LSH/blob/master/src/main/java/info/debatty/java/lsh/MinHash.java
 */
public class MinHash {

	private static final int size = 100;
	private int elements = 429981696; // 144^4.
	private int prime = 429981701;
    private int[] hash_coefs0;
    private int[] hash_coefs1;
    
	public MinHash(){
        Random r = new Random();
        this.hash_coefs0 = new int[size];
        this.hash_coefs1 = new int[size];
        
        for (int i = 0; i < size; i++) {
            this.hash_coefs0[i] = r.nextInt(elements); // a
            this.hash_coefs1[i] = r.nextInt(elements); // b
        }
	}
	
	public MinHash(int[] coefs0, int[] coefs1){
		if (coefs0.length != size || coefs0.length != size)
			return;
		
		this.hash_coefs0 = coefs0;
        this.hash_coefs1 = coefs1;
	}
    
	public MinHash(Integer[] coefs0, Integer[] coefs1){
		if (coefs0.length != size || coefs0.length != size)
			return;
		
		this.hash_coefs0 = Arrays.stream(coefs0).mapToInt(i->i).toArray();
        this.hash_coefs1 = Arrays.stream(coefs1).mapToInt(i->i).toArray();
	}
	
	public double jaccardIndex(Set<Integer> a, Set<Integer> b){
		double inter = Sets.intersection(a, b).size();
		double union = Sets.union(a, b).size();

		return inter / union;
	}
	
	public double similarity(int[] a, int[] b){
		int sim = 0;
		for (int i = 0; i < size; i++)
			if (a[i] == b[i])
				sim += 1;
			
		return (double) sim / size;
	}
	
	public int[] signature(Set<Integer> set) {
        int[] sig = new int[size];

        for (int i = 0; i < size; i++)
            sig[i] = Integer.MAX_VALUE;
        
        for (int s: set)
        	for (int i = 0; i < size; i++)
				sig[i] = Math.min(fh(i, s), sig[i]);
		
		return sig;
	}
	
	private int fh(int i, long x) {
        return (int) ((x * hash_coefs0[i] + hash_coefs1[i]) % prime);
    }
	
	public int[] getCoefs0(){
		return hash_coefs0;
	}
	
	public int[] getCoefs1(){
		return hash_coefs1;
	}
}