import java.io.*;
import java.net.*;
import java.util.*;

class Cliente {
    private static ArrayList<Distribumon> lista_distribumones = new ArrayList<>();
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
			System.out.println("[Cliente]: Información recibida exitosamente!");

            return datos_zona;
        }
        catch (IOException e){
        	System.err.println("IOException " + e);
        	return "ERROR";
        }
	}

	public static void main(String[] args) {

		final int bufferSize = 1024 * 4;
		Cliente cliente = new Cliente();
		Scanner sc 		= new Scanner(System.in);
		String input, nombre_zona, ip_servidor_central;

		System.out.println("[Cliente]: Ingresar IP Servidor Central (es 10.6.40.194)");
		ip_servidor_central = sc.next();

		System.out.println("[Cliente]: Introducir Nombre de Zona a explorar (sin espacios)");
		nombre_zona = sc.next();

		String datos_zona = cliente.solicitar_zona(ip_servidor_central, nombre_zona);
		cliente.update(nombre_zona, datos_zona);

		try {
			byte [] address = cliente.ip1.getAddress();
			InetAddress grupoMulticast = InetAddress.getByAddress(address);
			MulticastSocket ms = new MulticastSocket(9999); //PUERTO MULTICAST 9999
			ms.joinGroup(grupoMulticast);
			System.out.println("[Cliente]: Bienvenido a " + cliente.nombre_zona);

            byte[] buffer = new byte[bufferSize];
            DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
            ms.receive(respuesta); // Datos llegan de la forma de string "id nivel nombre"
            InetAddress ip_servidor = respuesta.getAddress();
            int puerto_servidor = respuesta.getPort();
            byte[] data = respuesta.getData();
            String datos_distribumon = new String(data, 0, respuesta.getLength());
            String[] Lista_datos_distribumones = datos_distribumon.split(" ");
            Distribumon dist = new Distribumon(Integer.parseInt(Lista_datos_distribumones[0]),Integer.parseInt(Lista_datos_distribumones[1]),Lista_datos_distribumones[2]);
            System.out.println();
            System.out.println("[Cliente]: Aparece nuevo Distribumon!: " + dist.nombre);

			while (true) {

                try{
                    System.out.println();
                    System.out.println("[Cliente]: Consola");
                    System.out.println("[Cliente]: (1) Listar Distribumones en Zona");
                    System.out.println("[Cliente]: (2) Cambiar Zona");
                    System.out.println("[Cliente]: (3) Capturar Distribumon");
                    System.out.println("[Cliente]: (4) Listar Distribumones Capturados");
                    input = sc.next();

                    String opcion;

				switch (input) {
					case "1":
                        try {
                            opcion = "1";
                            byte[] b1 = opcion.getBytes();
                            DatagramSocket socket1 = new DatagramSocket();
                            DatagramPacket dp     = new DatagramPacket(b1 , b1.length , cliente.ip2, cliente.puerto);
                            socket1.send(dp);

							byte[] receiveData = new byte[1024];
							DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
							socket1.receive(receivePacket);
							String modifiedSentence = new String(receivePacket.getData());

							System.out.println("[Cliente]: Lista Distribumones en la zona "+cliente.nombre_zona);
							System.out.println(modifiedSentence);
                            socket1.close();
							}
							catch (IOException e){
								System.err.println("IOException " + e);
							}
						break;

					case "2":
						ms.leaveGroup(grupoMulticast);
						System.out.println("[Cliente]: Introducir Nombre de Zona a explorar (sin espacios)");
						nombre_zona = sc.next();

						datos_zona = cliente.solicitar_zona(ip_servidor_central, nombre_zona);
						cliente.update(nombre_zona, datos_zona);
						address = cliente.ip1.getAddress();
						grupoMulticast = InetAddress.getByAddress(address);
						ms.joinGroup(grupoMulticast);
						break;

					case "3":
                        opcion = "3";
                        byte[] b3 = opcion.getBytes();
                        try {
                            DatagramSocket socket3 = new DatagramSocket();
                            DatagramPacket dp3     = new DatagramPacket(b3, b3.length, cliente.ip2, cliente.puerto);
                            socket3.send(dp3);

                            byte[] b = Integer.toString(dist.id).getBytes();
                            byte[] buffer3 = new byte[65536];

							DatagramPacket dp = new DatagramPacket(b, b.length, cliente.ip2, cliente.puerto);
							socket3.send(dp);

							DatagramPacket respuesta3 = new DatagramPacket(buffer3, buffer3.length);
							socket3.receive(respuesta3);
							lista_distribumones.add(dist);
							System.out.println("[Cliente]: Distribumon "+dist.nombre+" capturado!");

							socket3.close();
						}
						catch (IOException e){
							System.err.println("IOException " + e);
						}
						break;

					case "4":
                        System.out.println("[Cliente]: Lista Distribumones Capturados");
                        if (lista_distribumones.size()<1){
							System.out.println();
                            System.out.println("[Cliente]: No hay distribumones capturados, captura algun distribumon!");
                        }else {
                            for (int i = 0; i < lista_distribumones.size(); i++) {
								System.out.println();
                                System.out.println((i + 1) + ". " + lista_distribumones.get(i).nombre + " nivel: " + lista_distribumones.get(i).nivel);
                            }
                        }
						break;
				} // switch
                } catch (Exception e) {
                    System.out.println("Problemas en recibir Distribumon nuevo desde el Servidor Zona");
                    e.printStackTrace();
                }
			} // while
		}catch (IOException e) {
			System.err.println("IOException " + e);
	}
}
}