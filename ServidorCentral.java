import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorCentral {
	private HashMap<String, HashMap<String, String>> info;

	public HashMap<String, String> getInfo(String nombre) {
		return this.info.get(nombre);
	}

	public ServidorCentral(HashMap<String, HashMap<String, String>> hash) {
		this.info = hash;
	}

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		HashMap<String, HashMap<String, String>> info = new HashMap<String, HashMap<String, String>>();

		while (true){
			System.out.println("Agregar servidor de zona? (s/n)");
			String input = sc.next();
			
			if ( input.equals("s")) {
				HashMap<String, String> hash = new HashMap<String, String>();

				System.out.println("Nombre:");
				input = sc.next();
				String nombre = input;
				System.out.println("IP Multicast:");
				input = sc.next();
				hash.put("IP1", input);
				System.out.println("IP Peticiones:");
				input = sc.next();
				hash.put("IP2", input);
				System.out.println("Puerto Peticiones:");
				input = sc.next();
				hash.put("Puerto", input);

				System.out.println();
				info.put(nombre, hash);
			}
			else {
				break;	
			}
		} // while
		
		ServidorCentral servidor = new ServidorCentral(info);
		
		try {
			DatagramSocket server_socket = new DatagramSocket(8000);
			
			byte[] buffer 		   	= new byte[65536];
            DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);

            System.out.println();
            System.out.println("Esperando peticiones.....");

            while (true) {
                server_socket.receive(peticion);
                byte[] data   = peticion.getData();
                String nombre = new String(data, 0, peticion.getLength());

		        for (String item : servidor.info.keySet() ) {
					if (nombre.equals(item)) {
						HashMap<String, String> hash = new HashMap<String, String>(servidor.getInfo(nombre));
						String ip1  	 = hash.get("IP1");
						String ip2  	 = hash.get("IP2");
						String port  	 = hash.get("Puerto");
		                String respuesta = ip1 + " " + ip2 + " " + port;

						System.out.println();
						System.out.println("Respuesta a /" + peticion.getAddress().getHostAddress() + " por " + item);
						System.out.println("Nombre: " + item);
						System.out.println("IP Multicast: " + ip1 + ", IP Peticiones: " + ip2 +  ", Puerto Peticiones: " + port);
		               
	                	DatagramPacket dp = new DatagramPacket(respuesta.getBytes() , respuesta.getBytes().length , peticion.getAddress() , peticion.getPort());
		                server_socket.send(dp);
					}
				}              
            }
		}
		catch (IOException e) {
			System.err.println("IOException " + e);
		}
	}
}