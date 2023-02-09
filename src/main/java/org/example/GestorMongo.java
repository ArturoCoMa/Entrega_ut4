package org.example;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;

public class GestorMongo {
    private static Scanner tec = new Scanner(System.in);
    private static boolean programaFuncionando = true;
    private static String op;
    private static String url = "mongodb://localhost";
    private static String nombreDataBase = "biblioteca";
    private static String nombreCollection = "juegos";

    private static int contInserciones = 0;


    //bucle principal del programa
    public static void main(String[] args) {
        while(programaFuncionando){
            ejecutaPrograma();
        }
    }

    //funcnion con el menú principal del programa
    public static void ejecutaPrograma(){
        System.out.println("\nMENU PRINCIPAL\n--------------------\n1. Alta\n2. Buscar\n3. Lista\n4. Modificar\n5. Eliminar\n0. Salir");
        System.out.print("\nElige una opción:");
        op = tec.nextLine();
        controlaOpciones(op);
    }

    //función que controla la elección que has hecho en las opciones y redirige a la función que
    //realiza lo que quieres hacer.
    public static void controlaOpciones(String op){
        try(MongoClient mongoClient = MongoClients.create(url);){
            MongoDatabase mongoDatabase = mongoClient.getDatabase(nombreDataBase);
            MongoCollection mongoCollection = mongoDatabase.getCollection(nombreCollection);
            switch (op){
                case "1": menuAlta(mongoCollection); break;
                case "2": menuBuscar(mongoCollection); break;
                case "3": lista(mongoCollection); break;
                case "4": menuModificar(mongoCollection); break;
                case "5": menuEliminar(mongoCollection); break;
                case "0": salir(); break;
                default: System.out.println("Opción incorrecta.");
            }
        }catch (MongoException me){}

    }

    //función que muestra el sub-menu de alta y te da a elegir
    public static void menuAlta(MongoCollection mongoCollection) {
        boolean menuAlta = true;
        while (menuAlta) {
            System.out.println("¿Cuántos juegos vas a registrar?\n1. Uno\n2. Varios\n0. Volver");
            String numJuegos = tec.nextLine();
            switch (numJuegos){
                case "1": insertaUno(mongoCollection); menuAlta = false; break;
                case "2": insertaMuchos(mongoCollection); menuAlta = false; break;
                case "0": menuAlta = false;
                default: System.out.println("Opción incorrecta.");
            }
        }
    }

    //función que inserta uno
    public static void insertaUno(MongoCollection mongoCollection){
        Juego juego = obtieneDatos(mongoCollection);
        Document doc = creaDocumentJuego(juego);
        mongoCollection.insertOne(doc);
        System.out.println("\nExito al insertar.\n");
    }

    //función que inserta varios, contiene un bucle para ir pidiendolos
    public static void insertaMuchos(MongoCollection mongoCollection){

        List<Document> arrayJuegos = new ArrayList<>();
        boolean otroMas = true;
        while (otroMas){
            System.out.println("DATOS");
            Juego juego = obtieneDatos(mongoCollection);
            Document doc = creaDocumentJuego(juego);
            arrayJuegos.add(doc);
            System.out.print("¿Quieres añadir otro juego más? s/n: ");
            if(!tec.nextLine().equals("s")){
                otroMas = false;
                contInserciones++;
            }
        }

        System.out.print("Has creado "+contInserciones+" registros.");
        contInserciones = 0;
        mongoCollection.insertMany(arrayJuegos);
    }


    //función que gestiona el menú de buscar
    public static void menuBuscar(MongoCollection mongoCollection){
        boolean decidiendo = true;
        while(decidiendo){
            System.out.println("Buscar por:\n1. Id\n2. Nombre\n0. Volver");
            String op = tec.nextLine();
            switch (op){
                case "1": imprimeUno(mongoCollection, 1); decidiendo = false; break;
                case "2": imprimeUno(mongoCollection, 2); decidiendo = false; break;
                case "0": decidiendo = false; break;
                default: System.out.println("Opción incorrecta.");
            }
        }
    }

    //función que imprime el juego encontrado por buscaJuego
    public static void imprimeUno(MongoCollection mongoCollection, int op){
        Document doc = buscaJuego(mongoCollection, op);
        if (doc == null){
            System.out.println("Videojuego no encontrado.");
        }else{
            System.out.println("Videojuego encontrado\n" + doc.toJson());
        }
    }

    //funcíon que busca el juego segun el parametro pasado (id o nombre), y devuelve
    //un document con el juego que ha encontrado
    //devuelve null si no encuentra ninguno
    public static Document buscaJuego(MongoCollection mongoCollection, int tipoBusqueda){
        Document doc = null;
        if(tipoBusqueda == 1){
            System.out.print("Introduce el Id del videojuego:");
            int idElegido = compruebaInt(tec.nextLine());
            doc = (Document) mongoCollection.find(eq("_id",idElegido)).first();
        } else if (tipoBusqueda == 2) {
            System.out.print("Introduce el nombre del videojuego:");
            String nombreElegido = tec.nextLine();
            doc = (Document) mongoCollection.find(eq("titulo",nombreElegido)).first();
        }
        return doc;
    }

    //función que muestra una lista de todos los juegos
    public static void lista(MongoCollection mongoCollection){
        FindIterable<Document> iterable = mongoCollection.find(new Document());
        MongoCursor<Document> cursor = iterable.iterator();
        System.out.println("Lista de videojuegos: ");
        while (cursor.hasNext()) {
            System.out.println(cursor.next().toJson());
        }
    }

    //función que controla el sub-menú de modificar, llama la funcion
    //buscaJuego para encontrar el juego que quieres modificar
    //y luego llama al metodo modificarJuego
    public static void menuModificar(MongoCollection mongoCollection){
        System.out.println("Excribe el id del juego que quieres modificar:");
        Document docParaMod = buscaJuego(mongoCollection,1);
        if (docParaMod != null){
            System.out.println("Juego encontrado: "+docParaMod.toJson());
            modificaJuego(mongoCollection, docParaMod);
        }else{
            System.out.println("No existe ningún juego con ese id.");
        }
    }

    //este método controla lo que quieres modificar, dándote a elegir y llamando
    //al metodo modifica
    public static void modificaJuego(MongoCollection mongoCollection, Document docParaMod){
        System.out.println("¿Qué campo quieres modificar?\n1. Título\n2. Director\n3. Compañía\n4. Género\n5. Año\n6. Precio\n0. Cancelar");
        String opcion = tec.nextLine();
        switch (opcion){
            case "1":
                modifica(mongoCollection,docParaMod,"titulo");
                break;
            case "2":
                modifica(mongoCollection,docParaMod,"director");
                break;
            case "3":
                modifica(mongoCollection,docParaMod,"compania");
                break;
            case "4":
                modifica(mongoCollection,docParaMod,"genero");
                break;
            case "5":
                modifica(mongoCollection,docParaMod,"ano");
                break;
            case "6":
                modifica(mongoCollection,docParaMod,"precio");
                break;
            case "0":
                System.out.println("Modificación cancelada");
                break;
            default:System.out.println("Opción incorrecta");
        }
    }

    //función que modifica según los parametros que le hayamos pasado
    public static void modifica(MongoCollection mongoCollection, Document docParaMod, String dato){
        System.out.println("Introduce un nuevo valor:");
        if(dato.equals("ano")){
            int datoNuevo = compruebaInt(tec.nextLine());
            mongoCollection.updateOne(docParaMod,new Document("$set", new Document(dato, datoNuevo)));
        } else if (dato.equals("precio")) {
            double datoNuevo = compruebaDouble(tec.nextLine());
            mongoCollection.updateOne(docParaMod,new Document("$set", new Document(dato, datoNuevo)));
        }else{
            String datoNuevo = datoNuevo = tec.nextLine();
            mongoCollection.updateOne(docParaMod,new Document("$set", new Document(dato, datoNuevo)));
        }
    }

    //función que controla el sub-menú de eliminar
    public static void menuEliminar(MongoCollection mongoCollection){
        boolean decidiendo = true;
        while(decidiendo){
            System.out.println("Busca un juego para eliminarlo por:\n1. Id\n2. Nombre\n0. Volver");
            String op = tec.nextLine();
            switch (op){
                case "1": eliminar(mongoCollection,1); decidiendo = false; break;
                case "2": eliminar(mongoCollection, 2); decidiendo = false; break;
                case "0": decidiendo = false; break;
                default: System.out.println("Opción incorrecta.");
            }
        }
    }

    //funcín que te pregunta si quieres borrar ese juego
    //y lo elimina o no en función de lo que elijas.
    public static void eliminar(MongoCollection mongoCollection, int op){
        Document doc = buscaJuego(mongoCollection,op);
        if(doc != null){
            System.out.println("¿Es este el juego que quieres borrar?\n" + doc.toJson());
            String decision = tec.nextLine();
            if(decision.equals("s")){
                mongoCollection.deleteOne(doc);
                System.out.println("Borrado con exito");
            }else{
                System.out.println("Videojuego no borrado");
            }
        }else{
            System.out.println("No se ha encontrado ningún juego.");
        }
    }

    //función que rompe el bucle principal y sale del programa
    public static void salir() {
        programaFuncionando = false;
        System.exit(0);
    }


    //función que obtiene los datos
    public static Juego obtieneDatos(MongoCollection mongoCollection){
        int _id = 0, ano = 0;
        String titulo = "", compania = "", director = "", genero = "";
        double precio = 0;
        boolean opcionesCorrectas = true;
        while(opcionesCorrectas) {
            System.out.print("\nIntroduce los datos:\n");
            _id = generaId(mongoCollection);
            System.out.print("Título:");
            titulo = tec.nextLine();
            System.out.print("Compañia:");
            compania = tec.nextLine();
            System.out.print("Director:");
            director = tec.nextLine();
            System.out.print("Genero:");
            genero = tec.nextLine();
            System.out.print("Año:");
            ano = compruebaInt(tec.nextLine());
            System.out.print("Precio:");
            precio = compruebaDouble(tec.nextLine());
            //mientras devuelve true, vuelve a pedir los datos
            opcionesCorrectas = pregunta_S_N();
        }
        Juego juego = new Juego(_id, titulo, compania, director, genero, ano, precio);
        System.out.println(juego);
        return juego;
    }

    //función que pregunta al usuario si los datos son correctos, para que se asegure
    public static boolean pregunta_S_N(){
        System.out.print("Revisa los datos que vas a introducir ¿Son correctos? s/n\n");
        String decision = tec.nextLine();
        boolean tomaDecision = true;
        boolean sinDecision = true;
        while (sinDecision){
            switch (decision){
                //cambia tomaDecision a false
                case "s": tomaDecision = false; sinDecision = false; break;
                //no cambia tomaDecision, y por tanto devuelve true
                case "n": System.out.print("Vuelve a introducir los datos."); sinDecision = false; break;
                //no rompe el bucle decision, así que sigue pidiendo o S o N
                default: System.out.print("Escribe 's' para SI, o 'n' para NO"); decision = tec.nextLine();
            }
        }
        return tomaDecision;
    }

    //función que contruye el document pasandole un objeto Juego
    public static Document creaDocumentJuego(Juego juego){
        Document doc = new Document("_id", juego.get_id());
        doc.append("titulo",juego.getTitulo())
                .append("compania",juego.getCompania())
                .append("director",juego.getDirector())
                .append("genero",juego.getGenero())
                .append("ano",juego.getAno())
                .append("precio",juego.getPrecio());
        return doc;
    }


    //función que da robustez y comprueba que la introducción de datos Integer es correcta
    public static int compruebaInt(String x){
        boolean noParseado = true;
        int numero = 0;
        while(noParseado){
            try{
                numero = Integer.parseInt(x);
                noParseado = false;
            }catch(Exception e){
                System.out.print("Valor introducido no válido, inserta un número entero:");
                x = tec.nextLine();
            }
        }
        return numero;
    }

    //función que da robustez y comprueba que la introducción de datos Double es correcta
    public static double compruebaDouble(String x){
        boolean noParseado = true;
        double numero = 0;
        while(noParseado){
            try{
                numero = Double.parseDouble(x);
                noParseado = false;
            }catch(Exception e){
                System.out.print("Valor introducido no válido, inserta un número.");
                x = tec.nextLine();
            }
        }
        return numero;
    }
    //busca el ultimo id mas alto y le suma 1 para crear un id que no exista aun
    public static int generaId(MongoCollection mongoCollection){
        //esta consulta es la que devuelve el document con el id mas alto
        Document doc = (Document) mongoCollection.find().sort(new Document("_id",-1)).first();
        //obtenemos el integer del document
        int maxId = doc.getInteger("_id");
        return maxId+1;
    }
}