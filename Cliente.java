import java.io.*;
import java.net.*;
import java.util.*;

class Cliente {

	public String solicitarInfo(DatagramSocket socket, InetAddress servidor, String nombre){
		byte[] b 	  = nombre.getBytes();
		byte[] buffer = new byte[65536];
        
        try {
	        DatagramPacket  dp = new DatagramPacket(b , b.length , servidor, 8000);
	        socket.send(dp);

			DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
	        socket.receive(respuesta);

			byte[] data 	  = respuesta.getData();
            String datos_zona = new String(data, 0, respuesta.getLength());
            return datos_zona;
        }
        catch (IOException e){
        	System.err.println("IOException " + e);
        	return "ERROR";
        }
	}

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		String input, nombre_zona, ip_servidor_central;

		System.out.println("Ingresar IP Servidor Central (es 10.6.40.194)");
		ip_servidor_central = sc.next();

		System.out.println("Introducir Nombre de Zona a explorar (sin espacios)");
		nombre_zona = sc.next();

		try {
			DatagramSocket socket 		 = new DatagramSocket();
			InetAddress servidor_central = InetAddress.getByName(ip_servidor_central);

			Cliente cliente   = new Cliente();
			String datos_zona = cliente.solicitarInfo(socket, servidor_central, nombre_zona);
			System.out.println(datos_zona);
		}
		catch (IOException e) {
			System.err.println("IOException " + e);
		}

/*		while (true) {
			System.out.println();
			System.out.println("Consola");
			System.out.println("(1) Listar Distribumones en Zona");
			System.out.println("(2) Cambiar Zona");
			System.out.println("(3) Capturar Distribumon");
			System.out.println("(4) Listar Distribumones Capturados");
			input = sc.next();

			switch (input) {
				case "1":
					
					break;
				case "2":
					
					break;
				case "3":
					
					break;
				case "4":
					
					break;
			} // switch
		} // while*/
	}
}