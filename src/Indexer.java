/**
 * 
 */
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.text.StringEscapeUtils;
/**
 * @author Christhian
 *
 */
public class Indexer{
	
	public static MinHash minHash = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Log FILE
		PrintStream ps = null;
		try {
			ps = new PrintStream("log.txt");
			System.setOut(ps);
			//System.setErr(ps);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Busca el Driver
		try { 
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			ps.close();
			return;
		}
		
		// Establece la Conexion
		System.out.println("------------ Conectando con el Servidor ------------");
		Connection conn = null;
		Statement st = null;
		ResultSet r = null;
		try {
			conn = DriverManager.getConnection(
				"jdbc:postgresql://127.0.0.1:5432/StackOverflow", "postgres", "pass");
		} catch (SQLException e) {
			System.out.println("No se ha podido establecer la conexion con el servidor.");
			e.printStackTrace();
			ps.close();
			return;
		}
		
		// Obteniendo valores iniciales
		System.out.println("------------ Obteniendo valores iniciales ------------");
		Integer[] coefs0 = null, coefs1 = null;
		try {
			st = conn.createStatement();
			r = st.executeQuery("SELECT * FROM minHash");
			if (r.next()) {
				coefs0 = (Integer[]) r.getArray("coefs0").getArray();
				coefs1 = (Integer[]) r.getArray("coefs1").getArray();
			}
			r.close();
		} catch(SQLException e) {
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			System.out.println("No se ha podido leer la base de datos.");
			e.printStackTrace();
			ps.close();
			return;
		}

		// Calculando firmas
		System.out.println("------------ Calculando Firma ------------");
		if (coefs0 != null && coefs1 != null)
			minHash = new MinHash(coefs0, coefs1);
		else {
			minHash = new MinHash();
			try {
				st = conn.createStatement();
				st.execute("DROP TABLE IF EXISTS minHash");
				st.execute("CREATE TABLE minHash (coefs0 INTEGER[], coefs1 INTEGER[])");
				st.executeUpdate("INSERT INTO minHash VALUES(ARRAY"+
						Arrays.toString(minHash.getCoefs0())+", ARRAY"+
						Arrays.toString(minHash.getCoefs1())+")");
			} catch (SQLException e) {
				try {
					conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				System.out.println("No se ha podido escribir en la base de datos.");
				e.printStackTrace();
				ps.close();
				return;
			}
		}
		try {
			r = st.executeQuery("SELECT id, code FROM CODES");
			Statement upd_st = conn.createStatement();

			Set<Integer> set;
			int[] signature;
			String s;
			while (r.next()) {
				//System.out.println("------------ "+r.getInt("id")+" ------------");
				// FROM XML... Habilitar o deshabilitar dependiendo de la version de POSTGRES!!!
				// s = StringEscapeUtils.unescapeXml(r.getString("code"));
				// FROM HTML
				s = StringEscapeUtils.unescapeHtml4(s);
				// REMOVE PREPROCESOR DIRECTIVES
				s = s.replaceAll("(?m)^\\s*?#.*?$", "");
				
				try {
					set = new Tokenizer(s).getHashSet();
				} catch (Exception e) {
					System.out.println(e.getMessage());
					break;
				}
				
				// check if set is empty
				if (set.isEmpty())
					continue;
				
				// calculate signature
				signature = minHash.signature(set);
				
				// Update signature
				upd_st.executeUpdate("UPDATE codes SET signature = ARRAY" +
									Arrays.toString(signature) +
									" WHERE id = "+r.getInt("id"));
			}
			//test
			//System.out.print(Tokenizer.get_tList());
			r.close();
		} catch (SQLException e) {
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			System.out.println("No se ha podido escribir en la base de datos.");
			e.printStackTrace();
			ps.close();
			return;
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ps.close();
	}
}
