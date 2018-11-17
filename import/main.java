import java.sql.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;


public class main {

	public static void main(String[] argv) {

		// Log FILE
		PrintStream ps = null;
		try {
			ps = new PrintStream("log.txt");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.setOut(ps);
		System.setErr(ps);
		
		System.out.println("-------- StackOverFlow Stack to Postgres ------------");

		try { // Busca el Driver.
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		// Establece la Conexion
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(
				"jdbc:postgresql://127.0.0.1:5432/StackOverflow", "postgres", "pass");
		} catch (SQLException e) {
			System.out.println("No se ha podido establecer la conexion con el servidor.");
			e.printStackTrace();
			return;
		}
		
		System.out.println("Conexion con el Servidor establecida con exito.");
		
		// Creando Base de datos
		// try {
			// String creation_file = new String("creation_scheme.sql");
			// String creation_string = new String(Files.readAllBytes(Paths.get(creation_file)), StandardCharsets.UTF_8);
			// Statement st = conn.createStatement();
			// st.execute(creation_string);
			// st.close();
		// } catch (Exception e) {
			// System.out.println("No se ha podido crear la base de datos.");
			// e.printStackTrace();
			// return;
		// }
		System.out.println("Base de datos Creada con exito.");
		
		// Creando los Procedimientos
		try {
			String import_user_file = new String("import_user.sql");
			String import_badge_file = new String("import_badge.sql");
			String import_tag_file = new String("import_tag.sql");
			String import_post_file = new String("import_post.sql");
			String import_vote_file = new String("import_vote.sql");
			String import_comment_file = new String("import_comment.sql");
			String import_postlink_file = new String("import_postlink.sql");
			
			String import_user = new String(Files.readAllBytes(Paths.get(import_user_file)), StandardCharsets.UTF_8);
			String import_badge = new String(Files.readAllBytes(Paths.get(import_badge_file)), StandardCharsets.UTF_8);
			String import_tag = new String(Files.readAllBytes(Paths.get(import_tag_file)), StandardCharsets.UTF_8);
			String import_post = new String(Files.readAllBytes(Paths.get(import_post_file)), StandardCharsets.UTF_8);
			String import_vote = new String(Files.readAllBytes(Paths.get(import_vote_file)), StandardCharsets.UTF_8);
			String import_comment = new String(Files.readAllBytes(Paths.get(import_comment_file)), StandardCharsets.UTF_8);
			String import_postlink = new String(Files.readAllBytes(Paths.get(import_postlink_file)), StandardCharsets.UTF_8);
			
			Statement st = conn.createStatement();
			st.execute(import_user);
			System.out.println("Procedimiento import_user() creado con exito.");
			st.execute(import_badge);
			System.out.println("Procedimiento import_badge() creado con exito.");
			st.execute(import_tag);
			System.out.println("Procedimiento import_tag() creado con exito.");
			st.execute(import_post);
			System.out.println("Procedimiento import_post() creado con exito.");
			st.execute(import_vote);
			System.out.println("Procedimiento import_vote() creado con exito.");
			st.execute(import_comment);
			System.out.println("Procedimiento import_comment() creado con exito.");
			st.execute(import_postlink);
			System.out.println("Procedimiento import_postlink() creado con exito.");
			st.close();
		} catch (Exception e) {
			System.out.println("No se han podido crear los procedimientos.");
			e.printStackTrace();
		}
		
		// Leyendo Archivos a la Base de datos
		// read_file("stackexchange/Users.xml", "import_user", conn);
		System.out.println("Base de datos User.xml creada con exito.");
		read_file("stackexchange/Badges.xml", "import_badge", conn);
		System.out.println("Base de datos Badges.xml creada con exito.");
		//read_file("stackexchange/Tags.xml", "import_tag", conn);
		System.out.println("Base de datos Tags.xml creada con exito.");
		read_file("stackexchange/Posts.xml", "import_post", conn);
		System.out.println("Base de datos Post.xml creada con exito.");
		//read_file("stackexchange/Votes.xml", "import_vote", conn);
		System.out.println("Base de datos Votes.xml creada con exito.");
		read_file("stackexchange/Comments.xml", "import_comment", conn);
		System.out.println("Base de datos Comments.xml creada con exito.");
		read_file("stackexchange/PostLinks.xml", "import_postlink", conn);
		System.out.println("Base de datos PostLinks.xml creada con exito.");
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ps.close();
	}
	
	private static void read_file(String file_name, String script_name, Connection conn){
		String line = null;
		String next_line;
		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		try {
			fis = new FileInputStream(file_name);
			isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			br = new BufferedReader(isr);
		} catch (IOException e) {
			System.out.println("Archivo no encontrado.");
			e.printStackTrace();
			return;
		}
		
		try {
			line = br.readLine();
			line = br.readLine();
			line = br.readLine();
			while ((next_line = br.readLine()) != null) {
				try {
					CallableStatement proc = conn.prepareCall("{ call "+script_name+"( ? ) }");
					proc.setString(1, line);
					proc.execute();
					proc.close();
				} catch (SQLException e) {
					System.out.println("Linea: "+ line);
					e.printStackTrace();
				}
				line = next_line;
			}
		} catch (IOException e){
			e.printStackTrace();
			return;
		}

	}

}
