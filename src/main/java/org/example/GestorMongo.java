package org.example;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GestorMongo {
    private static Scanner tec = new Scanner(System.in);
    private static boolean programaFuncionando = true;
    private static String op;
    private static String url = "mongodb://localhost";
    private static String nombreDataBase = "biblioteca";
    private static String nombreCollection = "juegos";

    private static boolean opcionesCorrectas = true;

    //bucle principal del programa
    public static void main(String[] args) {
        while(programaFuncionando){
            System.out.println("Bienvenido\n--------------------\n1. Alta\n2. Modificar\n3. Eliminar\n4. Buscar\n0. Salir");
            System.out.print("\nElige una opción:");
            op = tec.nextLine();
            controlaOpciones(op);
        }
    }

    //función que controla la elección que has hecho en las opciones y redirige a la función que
    //realiza lo que quieres hacer.
    public static void controlaOpciones(String op){
        try(MongoClient mongoClient = MongoClients.create(url);){
            MongoDatabase mongoDatabase = mongoClient.getDatabase(nombreDataBase);
            MongoCollection mongoCollection = mongoDatabase.getCollection(nombreCollection);
            switch (op){
                case "1": controlaAlta(mongoCollection); break;
                //case "2": modificar(); break;
                //case "3": eliminar(); break;
                //case "4": buscar(); break;
                case "0": salir(); break;
                default: System.out.println("Opción incorrecta.");
            }
        }catch (MongoException me){}

    }

    //función que rompe el bucle principal y sale del programa
    public static void salir() {
        programaFuncionando = false;
        System.exit(0);
    }

    //función que muestra el sub-menu de alta y te da a elegir
    public static void controlaAlta(MongoCollection mongoCollection) {
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

    //función que obtiene los datos para insertar uno
    public static Juego obtieneDatos(){
        int _id = 0, ano = 0;
        String titulo = "", compania = "", director = "", genero = "";
        double precio = 0;
        while(opcionesCorrectas) {
            System.out.print("\nIntroduce los datos:\nID:");
            _id = tec.nextInt();
            tec.nextLine();
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
        return juego;
    }

    //función que obtiene los datos de para insertar muchos
    public static void obtieneDatosMuchos(MongoCollection mongoCollection){
        List<Document> arrayJuegos = new ArrayList<>();
        boolean insertaOtro = true;
        while(insertaOtro){
            System.out.println("¿Quieres insertar otro más?");
            String op = tec.nextLine();
            if(!op.equals("s")){
                insertaOtro = false;
            }
        }
        //pregunta_S_N(mongoCollection, null, arrayJuegos);
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
                default: System.out.print("Escribe 's' para SI, o 'n' para NO");
            }
        }
        return tomaDecision;
    }

    //función que inserta los datos obtenidos en obtieneDatosUno
    public static void insertaUno(MongoCollection mongoCollection){
        Juego juego = obtieneDatos();
        Document doc = new Document("_id",juego.get_id())
                .append("titulo",juego.getTitulo())
                .append("compania",juego.getCompania())
                .append("director",juego.getDirector())
                .append("genero",juego.getGenero())
                .append("ano",juego.getAno())
                .append("precio",juego.getPrecio());
        mongoCollection.insertOne(doc);
        System.out.println("\nExito al insertar.\n");
    }

    //función que inserta los datos obtenidos en obtieneDatosMuchos
    public static void insertaMuchos(MongoCollection mongoCollection){
        boolean otroMas = true;
        List<Document> arrayJuegos = new ArrayList<>();
        while (otroMas){
            Juego juego = obtieneDatos();
            arrayJuegos.add(creaDocumentJuego(juego));
            System.out.print("¿Quieres añadir otro juego más? s/n: ");
            if(!tec.nextLine().equals("s")){
                otroMas = false;
            }
        }
    }

    public static Document creaDocumentJuego(Juego juego){
        return new Document()
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
                System.out.print("Valor introducido no válido, inserta un número:");
                x = tec.nextLine();
            }
        }
        return numero;
    }

}