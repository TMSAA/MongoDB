package Hospital;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.bson.Document;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class MongoDBManager {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> pacientesCollection;
    private MongoCollection<Document> medicosCollection;
    private MongoCollection<Document> plantasCollection;
    private Scanner scanner;

    public MongoDBManager(String connectionString, String databaseName) {
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(databaseName);	
        this.pacientesCollection = database.getCollection("pacientes");
        this.medicosCollection = database.getCollection("medicos");
        this.plantasCollection = database.getCollection("plantas");
        this.scanner = new Scanner(System.in);
    }

    public void insertPaciente() {
        System.out.println("Ingrese los datos del nuevo paciente:");
        System.out.print("Código de paciente: ");
        String codigoPaciente = scanner.nextLine();
        if(!pacienteExiste(codigoPaciente)) {
        	System.out.print("Enfermedad: ");
        	String enfermedad = scanner.nextLine();
        	System.out.print("Código de médico asignado: ");
        	String codigoMedico = scanner.nextLine();
        	System.out.print("Código de planta asignada: ");
        	String codigoPlanta = scanner.nextLine();

        	Document paciente = new Document("codigo_paciente", codigoPaciente)
                                    	.append("enfermedad", enfermedad)
                                    	.append("codigo_medico", codigoMedico)
                                    	.append("codigo_planta", codigoPlanta);

        	pacientesCollection.insertOne(paciente);
        	System.out.println("Paciente insertado correctamente.");
        }else {
        	System.out.println("El paciente ya existe");
        }
    }

    public void deletePaciente() {
        System.out.println("Ingrese el código de paciente para eliminar:");
        String codigoPaciente = scanner.nextLine();

        if (pacienteExiste(codigoPaciente)) {
            pacientesCollection.deleteOne(Filters.eq("codigo_paciente", codigoPaciente));
            System.out.println("Paciente eliminado correctamente.");
        } else {
            System.out.println("El paciente con código " + codigoPaciente + " no existe.");
        }
    }

    public void updatePaciente() {
        System.out.println("Ingrese el código de paciente para actualizar:");
        String codigoPaciente = scanner.nextLine();
        if (pacienteExiste(codigoPaciente)) {
            System.out.println("Ingrese el nuevo código de médico asignado:");
            String nuevoCodigoMedico = scanner.nextLine();
            System.out.println("Ingrese el nuevo código de planta asignada:");
            String nuevoCodigoPlanta = scanner.nextLine();

            pacientesCollection.updateOne(Filters.eq("codigo_paciente", codigoPaciente), 
                                           Updates.set("codigo_medico", nuevoCodigoMedico));
            pacientesCollection.updateOne(Filters.eq("codigo_paciente", codigoPaciente), 
                                           Updates.set("codigo_planta", nuevoCodigoPlanta));
            System.out.println("Paciente actualizado correctamente.");
        } else {
            System.out.println("El paciente con código " + codigoPaciente + " no existe.");
        }
    }

    public void displayPacientes() {
    	System.out.println("Pacinetes en la colecion");
    	for (Document paciente : pacientesCollection.find()) {
            System.out.println(paciente.toJson());
        }
    }
    
    public void findPaciente() {
    	System.out.println("Ingrese el codigo del paciente a buscar");
    	String coidgoPaciente = scanner.nextLine();
    	if (pacienteExiste(coidgoPaciente)) {
    		Document paciente = pacientesCollection.find(Filters.eq("codigo_paciente",coidgoPaciente)).first();
			System.out.println(paciente.toJson());
		} else {
			System.out.println("Paciente no encontrado(no existe o codigo erroneo)");
		}
    }

    public void insertMedico() {
        System.out.println("Ingrese los datos del nuevo médico:");
        System.out.print("Código de médico: ");
        String codigoMedico = scanner.nextLine();
        if(!medicoExiste(codigoMedico)) {
        	System.out.print("Código de planta asignada: ");
        	String codigoPlanta = scanner.nextLine();

        	Document medico = new Document("codigo_medico", codigoMedico)
                                	.append("codigo_planta", codigoPlanta);

        	medicosCollection.insertOne(medico);
        	System.out.println("Médico insertado correctamente.");
        }else {
        	System.out.println("El medico ya existe");
        }
    }

    public void deleteMedico() {
        System.out.println("Ingrese el código de médico para eliminar:");
        String codigoMedico = scanner.nextLine();

        if (medicoExiste(codigoMedico)) {
            medicosCollection.deleteOne(Filters.eq("codigo_medico", codigoMedico));
            System.out.println("Médico eliminado correctamente.");
        } else {
            System.out.println("El médico con código " + codigoMedico + " no existe.");
        }
    }

    public void updateMedico() {
        System.out.println("Ingrese el código de médico para actualizar:");
        String codigoMedico = scanner.nextLine();
        if (medicoExiste(codigoMedico)) {
            System.out.println("Ingrese el nuevo código de planta asignada:");
            String nuevoCodigoPlanta = scanner.nextLine();

            medicosCollection.updateOne(Filters.eq("codigo_medico", codigoMedico), 
                                           Updates.set("codigo_planta", nuevoCodigoPlanta));
            System.out.println("Médico actualizado correctamente.");
        } else {
            System.out.println("El médico con código " + codigoMedico + " no existe.");
        }
    }

    public void displayMedicos() {
    	System.out.println("Medicos en la colecion");
    	for (Document medico : medicosCollection.find()) {
            System.out.println(medico.toJson());
        }
    }
    
    public void findMedico() {
    	System.out.println("Ingrese el codigo del medico a buscar");
    	String codigoMedico = scanner.nextLine();
    	if (medicoExiste(codigoMedico)) {
    		Document medico = medicosCollection.find(Filters.eq("codigo_medico",codigoMedico)).first();
			System.out.println(medico.toJson());
		} else {
			System.out.println("Medico no encontrado(no existe o codigo erroneo)");
		}
    }
    
    public void insertPlanta() {
    	System.out.println("Ingrese los datos de la nueva planta:");
    	System.out.println("Codigo de planta: ");
    	String codigoPlanta = scanner.nextLine();
    	if (!plantaExiste(codigoPlanta)) {
			System.out.println("Patologia:");
			String patologia = scanner.nextLine();
			System.out.println("Ingrese los códigos de los médicos asignados (separados por comas):");
	        String medicosInput = scanner.nextLine();
	        List<String> medicosAsignados = Arrays.asList(medicosInput.split(","));

	        System.out.println("Ingrese los códigos de los pacientes asignados (separados por comas):");
	        String pacientesInput = scanner.nextLine();
	        List<String> pacientesAsignados = Arrays.asList(pacientesInput.split(","));

	        Document nuevaPlanta = new Document("codigo_planta", codigoPlanta)
	                                    .append("patologia", patologia)
	                                    .append("medicos_asignados", medicosAsignados)
	                                    .append("pacientes_asignados", pacientesAsignados);

	        plantasCollection.insertOne(nuevaPlanta);

	        System.out.println("Nueva planta creada correctamente.");
		} else {

		}
    }
    
    public void deletePlanta() {
        System.out.println("Ingrese el código de la planta para eliminar:");
        String codigoPlanta = scanner.nextLine();

        if (plantaExiste(codigoPlanta)) {
            plantasCollection.deleteOne(Filters.eq("codigo_planta", codigoPlanta));
            System.out.println("Planta eliminada correctamente.");
        } else {
            System.out.println("La planta con código " + codigoPlanta + " no existe.");
        }
    }
    
    public void updatePlanta() {
        System.out.println("Ingrese el código de la planta para actualizar:");
        String codigoPlanta = scanner.nextLine();

        if (plantaExiste(codigoPlanta)) {
            System.out.println("Ingrese el nuevo nombre de la patología:");
            String nuevaPatologia = scanner.nextLine();

            System.out.println("Ingrese los nuevos códigos de médicos asignados (separados por coma):");
            String[] nuevosCodigosMedicos = scanner.nextLine().split(",");

            System.out.println("Ingrese los nuevos códigos de pacientes asignados (separados por coma):");
            String[] nuevosCodigosPacientes = scanner.nextLine().split(",");

            plantasCollection.updateOne(Filters.eq("codigo_planta", codigoPlanta),
                Updates.combine(
                    Updates.set("patologia", nuevaPatologia),
                    Updates.set("medicos_asignados", nuevosCodigosMedicos),
                    Updates.set("pacientes_asignados", nuevosCodigosPacientes)
                )
            );

            System.out.println("Planta actualizada correctamente.");
        } else {
            System.out.println("La planta con código " + codigoPlanta + " no existe.");
        }
    }
    
    public void findPlanta() {
    	System.out.println("Ingrese el codigo del la planta a buscar");
    	String codigoPlanta = scanner.nextLine();
    	if (plantaExiste(codigoPlanta)) {
    		Document planta = plantasCollection.find(Filters.eq("codigo_planta",codigoPlanta)).first();
			System.out.println(planta.toJson());
		} else {
			System.out.println("Medico no encontrado(no existe o codigo erroneo)");
		}
    }
    
    public void displayPlantas() {
    	System.out.println("Plantas en la colecion");
        for (Document planta : plantasCollection.find()) {
            System.out.println(planta.toJson());
        }
    }
    private boolean pacienteExiste(String codigoPaciente) {
        return pacientesCollection.countDocuments(Filters.eq("codigo_paciente", codigoPaciente)) > 0;
    }

    private boolean medicoExiste(String codigoMedico) {
        return medicosCollection.countDocuments(Filters.eq("codigo_medico", codigoMedico)) > 0;
    }
    
    private boolean plantaExiste(String codigoPlanta) {
    	return plantasCollection.countDocuments(Filters.eq("codigo_planta",codigoPlanta)) > 0;
    }

    public void close() {
        scanner.close();
        mongoClient.close();
    }
    
    public void insertDocumentsFromJSON() {
    	System.out.println("Ingrese el nombre de la coleccion a la cual quiere ingresar el documento");
    	String collectionName = scanner.nextLine();
        try {
            if (!database.listCollectionNames().into(new ArrayList<>()).contains(collectionName)) {
                System.out.println("La colección especificada no existe.");
                return;
            }
            System.out.println("Ingresa la ruta al archivo:");
            String jsonFilePath = scanner.nextLine();
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            Document document = Document.parse(jsonContent);
            MongoCollection<Document> collection = database.getCollection(collectionName);
            collection.insertOne(document);

            System.out.println("Documentos insertados correctamente en la colección " + collectionName + ".");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo JSON: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        MongoDBManager manager = new MongoDBManager("mongodb://localhost:27017", "Hospital");

        Scanner scanner = new Scanner(System.in);
        boolean continuar = false;
        boolean continuar2 = true;
        while(continuar2) {
        	System.out.println("\nSeleccione una opción:");
        	System.out.println("0. Ingresar datos a una coleccion atraves de un archivo");
        	System.out.println("1. Selecionar colecion para trabjar con ella");
        	System.out.println("2. Salir");
        	
        	int opcion2 = scanner.nextInt();
        	scanner.nextLine();
        	
        	switch(opcion2) {
        	case 0:
        		manager.insertDocumentsFromJSON();
        		break;
        	case 1:
        		continuar = true;
        		break;
        	case 2:
        		continuar2 = false;
        		break;
        	default:
				System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
        	}
        	while (continuar) {
        		System.out.println("\nSeleccione una opción:");/*Cuando ingresas los datos por archvio no verifica si ya existe algun documento igual por lo
        		que puede ver duplicados*/
        		System.out.println("1. Pacientes");
                System.out.println("2. Médicos");
                System.out.println("3. Plantas");
                System.out.println("4. Salir");

                int opcionColeccion = scanner.nextInt();
                scanner.nextLine();

                switch (opcionColeccion) {
                    case 1:
                    	boolean continuar3 = true;
                    	while(continuar3) {
                    		System.out.println("\nSelecione una opción:");
                    		System.out.println("1. Insertar un paciente");
                    		System.out.println("2. Eliminar un paciente");
                    		System.out.println("3. Actualizar un paciente");
                    		System.out.println("4. Bucar un paciente");
                    		System.out.println("5. Mostrar todos los pacientes");
                    		System.out.println("6. Atras");
                    		
                    		int opcion3 = scanner.nextInt();
                    		scanner.nextLine();
                    		
                    		switch(opcion3) {
                    		case 1:
                    			manager.insertPaciente();
                    			break;
                    		case 2:
                    			manager.deletePaciente();
                    			break;
                    		case 3:
                    			manager.updatePaciente();
                    			break;
                    		case 4:
                    			manager.findPaciente();
                    			break;
                    		case 5:
                    			manager.displayPacientes();
                    			break;
                    		case 6:
                    			continuar3 = false;
                    			break;
                    		default:
                				System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                    		}
                    	}
                        break;
                    case 2:
                    	boolean continuar4 = true;
                    	while(continuar4) {
                    		System.out.println("\nSelecione una opción:");
                    		System.out.println("1. Insertar un medico");
                    		System.out.println("2. Eliminar un madico");
                    		System.out.println("3. Actualizar un medico");
                    		System.out.println("4. Bucar un medico");
                    		System.out.println("5. Mostrar todos los medicos");
                    		System.out.println("6. Atras");
                    		
                    		int opcion3 = scanner.nextInt();
                    		scanner.nextLine();
                    		
                    		switch(opcion3) {
                    		case 1:
                    			manager.insertMedico();
                    			break;
                    		case 2:
                    			manager.deleteMedico();
                    			break;
                    		case 3:
                    			manager.updateMedico();
                    			break;
                    		case 4:
                    			manager.findMedico();
                    			break;
                    		case 5:
                    			manager.displayMedicos();
                    			break;
                    		case 6:
                    			continuar4 = false;
                    			break;
                    		default:
                				System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                    		}
                    	}
                        break;
                    case 3:
                    	boolean continuar5 = true;
                    	while(continuar5) {
                    		System.out.println("\nSelecione una opción:");
                    		System.out.println("1. Insertar una planta");
                    		System.out.println("2. Eliminar una planta");
                    		System.out.println("3. Actualizar una planta");
                    		System.out.println("4. Bucar una planta");
                    		System.out.println("5. Mostrar todas la plantas");
                    		System.out.println("6. Atras");
                    		
                    		int opcion3 = scanner.nextInt();
                    		scanner.nextLine();
                    		
                    		switch(opcion3) {
                    		case 1:
                    			manager.insertPlanta();
                    			break;
                    		case 2:
                    			manager.deletePlanta();
                    			break;
                    		case 3:
                    			manager.updatePlanta();
                    			break;
                    		case 4:
                    			manager.findPlanta();
                    			break;
                    		case 5:
                    			manager.displayPlantas();
                    			break;
                    		case 6:
                    			continuar5 = false;
                    			break;
                    		default:
                				System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                    		}
                    	}
                        break;
                    case 4:
                        continuar = false; 
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                }
        	}
        }

        manager.close();
        scanner.close();
    }
}
