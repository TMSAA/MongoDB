package Hospital;

import java.util.Scanner;
import org.bson.Document;
import com.mongodb.client.MongoClients;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.function.Consumer;
import com.mongodb.client.model.Updates;

public class MongoDBManager {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> pacientesCollection;
    private MongoCollection<Document> medicosCollection;
    private Scanner scanner;

    public MongoDBManager(String connectionString, String databaseName) {
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(databaseName);
        this.pacientesCollection = database.getCollection("pacientes");
        this.medicosCollection = database.getCollection("medicos");
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
        System.out.println("Pacientes en la colección:");
        FindIterable<Document> pacientes = pacientesCollection.find();
        pacientes.forEach((Consumer<? super Document>) document -> System.out.println(document.toJson()));
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
        System.out.println("Médicos en la colección:");
        FindIterable<Document> medicos = medicosCollection.find();
        medicos.forEach((Consumer<? super Document>) document -> System.out.println(document.toJson()));
    }
    
    
    private boolean pacienteExiste(String codigoPaciente) {
        return pacientesCollection.countDocuments(Filters.eq("codigo_paciente", codigoPaciente)) > 0;
    }

    private boolean medicoExiste(String codigoMedico) {
        return medicosCollection.countDocuments(Filters.eq("codigo_medico", codigoMedico)) > 0;
    }

    public void close() {
        scanner.close();
        mongoClient.close();
    }

    public static void main(String[] args) {
        MongoDBManager manager = new MongoDBManager("mongodb://localhost:27017", "Hospital");

        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Insertar paciente");
            System.out.println("2. Eliminar paciente");
            System.out.println("3. Actualizar paciente");
            System.out.println("4. Mostrar todos los pacientes");
            System.out.println("5. Buscar pacientes");
            System.out.println("6. Insertar médico");
            System.out.println("7. Eliminar médico");
            System.out.println("8. Actualizar médico");
            System.out.println("9. Mostrar todos los médicos");
            System.out.println("10. Salir");

            int opcion = scanner.nextInt();
            scanner.nextLine(); 

            switch (opcion) {
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
                    manager.displayPacientes();
                    break;
                case 5:
                    manager.insertMedico();
                    break;
                case 6:
                    manager.deleteMedico();
                    break;
                case 7:
                    manager.updateMedico();
                    break;
                case 8:
                    manager.displayMedicos();
                    break;
                case 9:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
            }
        }

        manager.close();
        scanner.close();
    }
}
