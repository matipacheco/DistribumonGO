import java.io.*;
import java.net.*;
import java.util.*;

class Cliente {
	private String nombre_zona;
	private int puerto;
	private InetAddress ip1, ip2;
	// IP1 = IP Multicast
	// IP2 = IP Peticiones

	public void update(String nombre_zona, String datos_zona) {
		try {
			String[] data 	 = datos_zona.split(" ");
			this.nombre_zona = nombre_zona;
			this.ip1 		 = InetAddress.getByName(data[0]);
			this.ip2 		 = InetAddress.getByName(data[1]);
			this.puerto		 = Integer.parseInt(data[2]);
		}
        catch (IOException e){
        	System.err.println("IOException " + e);
		}
	}


	public static String solicitar_zona(String ip_servidor_central, String nombre_zona){
		byte[] b 	  		  = nombre_zona.getBytes();
		byte[] buffer 		  = new byte[65536];
        
        try {
        	InetAddress servidor  = InetAddress.getByName(ip_servidor_central);
			DatagramSocket socket = new DatagramSocket();
	        DatagramPacket dp     = new DatagramPacket(b , b.length , servidor, 8000);
	        socket.send(dp);

			DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
	        socket.receive(respuesta);

			byte[] data 	  = respuesta.getData();
            String datos_zona = new String(data, 0, respuesta.getLength());

            System.out.println();
			System.out.println("Información recibida exitosamente!");

            return datos_zona;
        }
        catch (IOException e){
        	System.err.println("IOException " + e);
        	return "ERROR";
        }
	}

	//---------------------------------------------------------------------------
	// probablemente haya que cambiar lo que retorna el método de void a String,
	// pero depende de como lo vayas a imlementar
	//---------------------------------------------------------------------------
	public void solicitar(String opcion) {

		byte[] b 	  = opcion.getBytes();
		byte[] buffer = new byte[65536];
        
        try {
        	DatagramSocket socket = new DatagramSocket();
	        DatagramPacket dp     = new DatagramPacket(b , b.length , this.ip2, this.puerto);
	        socket.send(dp);

/*			DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
	        socket.receive(respuesta);*/
        }
        catch (IOException e){
        	System.err.println("IOException " + e);
        }
	}


	public static void main(String[] args) {

		Cliente cliente = new Cliente();
		Scanner sc 		= new Scanner(System.in);
		String input, nombre_zona, ip_servidor_central;

		System.out.println("Ingresar IP Servidor Central (es 10.6.40.194)");
		ip_servidor_central = sc.next();

		System.out.println("Introducir Nombre de Zona a explorar (sin espacios)");
		nombre_zona = sc.next();

		String datos_zona = cliente.solicitar_zona(ip_servidor_central, nombre_zona);
		cliente.update(nombre_zona, datos_zona);

		//---------------------------------------------------------------------------
		// INGRESAR A ZONA
		// (Revisar como es eso de suscribirse al multicast para ingresar a la zona)
		// cliente.ingresar(nombre_zona);
		System.out.println();
		System.out.println("Bienvenido a " + cliente.nombre_zona);
		//---------------------------------------------------------------------------

		while (true) {
			System.out.println();
			System.out.println("Consola");
			System.out.println("(1) Listar Distribumones en Zona");
			System.out.println("(2) Cambiar Zona");
			System.out.println("(3) Capturar Distribumon");
			System.out.println("(4) Listar Distribumones Capturados");
			input = sc.next();

			switch (input) {
				case "1":
					cliente.solicitar(input);
					break;

				case "2":
					System.out.println();
					System.out.println("Introducir Nombre de Zona a explorar (sin espacios)");
					nombre_zona = sc.next();

					datos_zona = cliente.solicitar_zona(ip_servidor_central, nombre_zona);
					cliente.update(nombre_zona, datos_zona);
					// cliente.ingresar(nombre_zona);
					break;

				case "3":
					cliente.solicitar(input);
					break;

				case "4":
					cliente.solicitar(input);
					break;
			} // switch
		} // while
	}
}