import java.io.*;
import java.net.*;
import java.util.*;

class ServidorZona {
	private ArrayList<Distribumon> lista_distribumones = new ArrayList<>();
	private String nombre_zona;
	private int puerto1, puerto2;
	// Puerto1 = Puerto Multicast
	// Puerto2 = Puerto Peticiones
	private InetAddress ip1, ip2;
	// IP1 = IP Multicast
	// IP2 = IP Peticiones

	public ServidorZona(String nombre_zona, String ip1, String ip2, int puerto1, int puerto2) {
		try {
			this.nombre_zona = nombre_zona;
			this.ip1 		 = InetAddress.getByName(ip1);
			this.ip2 		 = InetAddress.getByName(ip2);
			this.puerto1 	 = puerto1;
			this.puerto2 	 = puerto2;
		}
		catch (IOException e){
			System.err.println("IOException " + e);
		}
	}

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		String nombre_zona, nombre, ip1, ip2, input;
		int puerto1, puerto2, nivel, id_distribumon = 0;

		System.out.println("[Servidor Zona]: Nombre Servidor (sin espacios):");
		nombre_zona = sc.next();
		System.out.println("[Servidor Zona: "+nombre_zona+"] IP Multicast:");
		ip1 = sc.next();
		System.out.println("[Servidor Zona: "+nombre_zona+"] Puerto Multicast: (es 9999):");
		puerto1 = sc.nextInt();
		System.out.println("[Servidor Zona: "+nombre_zona+"] IP Peticiones (es 10.6.40.196):");
		ip2 = sc.next();
		System.out.println("[Servidor Zona: "+nombre_zona+"] Puerto Peticiones:");
		puerto2 = sc.nextInt();

		ServidorZona servidor_zona = new ServidorZona(nombre_zona, ip1, ip2, puerto1, puerto2);

		try {

			while (true) {

				System.out.println();
				System.out.println("[Servidor Zona: "+nombre_zona+"]: Agregar un nuevo Distribumon? (s/n)");
				input = sc.next();

				if (input.equals("s")) {
					System.out.println("[Servidor Zona: "+nombre_zona+"]: Introducir nombre");
					nombre = sc.next();
					System.out.println("[Servidor Zona: "+nombre_zona+"]: Introducir nivel");
					nivel = sc.nextInt();

					Distribumon distribumon = new Distribumon(id_distribumon++, nivel, nombre);
                    servidor_zona.lista_distribumones.add(distribumon);

                    System.out.println("[Servidor Zona: "+nombre_zona+"]: Se ha publicado el Distribumon: " + nombre);
                    System.out.println("******");
                    System.out.println("id: "+distribumon.id);
                    System.out.println("nombre: "+distribumon.nombre);
                    System.out.println("nivel: "+distribumon.nivel);

					try{
						String mensaje = distribumon.id+" "+distribumon.nivel+" "+distribumon.nombre;
						DatagramSocket serverSocket = new DatagramSocket();
						DatagramPacket msgPacket = new DatagramPacket(mensaje.getBytes(),mensaje.getBytes().length, servidor_zona.ip1, servidor_zona.puerto1);
						serverSocket.send(msgPacket);

					}catch (IOException e){
						System.err.println("IOException " + e);
                        System.out.println("Distribumon no pudo ser enviado(error)");
					}
				}

				System.out.println();
				System.out.println("[Servidor Zona: "+nombre_zona+"]: Esperando peticiones.....");

				DatagramSocket server_socket = new DatagramSocket(servidor_zona.puerto2);
				byte[] buffer 		   	= new byte[65536];
				DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);

				server_socket.receive(peticion);
				byte[] data   = peticion.getData();
				String opcion = new String(data, 0, peticion.getLength());

				switch (opcion) {
					case "1":
						System.out.println();
						System.out.println("[Servidor Zona: "+nombre_zona+"]: Se solicitó listar Distribumones en Zona");
						String mensaje_lista = "";
						for (int i=0;i<servidor_zona.lista_distribumones.size();i++){
							mensaje_lista += (i+1)+". nombre: "+servidor_zona.lista_distribumones.get(i).nombre+" nivel: "+servidor_zona.lista_distribumones.get(i).nivel+"\n";
						}
						byte[] datos1 = mensaje_lista.getBytes();
						DatagramPacket dp1 = new DatagramPacket(datos1, datos1.length, peticion.getAddress(), peticion.getPort());
                        server_socket.send(dp1);
						break;

					case "3":
						System.out.println();
						System.out.println("[Servidor Zona: "+nombre_zona+"]: Se solicitó capturar Distribumon");
                        server_socket.receive(peticion);
                        byte[] data3   = peticion.getData();
                        String id_d = new String(data3, 0, peticion.getLength());
                        for (int i=0;i<servidor_zona.lista_distribumones.size();i++){
                            int id_revisado = servidor_zona.lista_distribumones.get(i).id;
                            if (id_revisado == Integer.parseInt(id_d)){
                                servidor_zona.lista_distribumones.remove(i);
                                String mensaje = "capturado"; //Se capturo Distribumon, se avisa al cliente que lo capturo.
                                byte[] datos = mensaje.getBytes();
                                DatagramPacket dp = new DatagramPacket(datos, datos.length, peticion.getAddress(), peticion.getPort());
                                server_socket.send(dp);
                                System.out.println("[Servidor Zona: "+nombre_zona+"]: Distribumon capturado por "+peticion.getAddress());
                            }
                        }
                        /*String mensaje = "Distribumon capturado!"; //Mensaje Multicast a miembros de la zona
                        DatagramSocket serverSocket = new DatagramSocket();
                        DatagramPacket msgPacket = new DatagramPacket(mensaje.getBytes(),mensaje.getBytes().length, servidor_zona.ip1, servidor_zona.puerto1);
                        serverSocket.send(msgPacket);*/
						break;
				}// switch
				server_socket.close();
			}// while
		}
		catch (IOException e) {
			System.err.println("IOException " + e);
		}
	}
}