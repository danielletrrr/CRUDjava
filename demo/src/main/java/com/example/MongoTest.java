package com.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoTest {
    public static void main(String[] args) {
        // URL de conexão com o MongoDB (ajuste conforme necessário, exemplo local)
        String uri = "mongodb+srv://danielletrajano16:tnentL592AOnV9mZ@testcrud.27iby.mongodb.net/?retryWrites=true&w=majority&appName=TestCrud";

        // Criar o cliente MongoDB
        MongoClient mongoClient = MongoClients.create(uri);

        // Testar a conexão acessando uma base de dados (pode ser qualquer banco de dados)
        try {
            MongoDatabase database = mongoClient.getDatabase("admin");
            System.out.println("Conexão bem-sucedida com o MongoDB!");

            // Testar uma simples operação: Listar coleções
            for (String collectionName : database.listCollectionNames()) {
                System.out.println("Coleção encontrada: " + collectionName);
            }
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao MongoDB: " + e.getMessage());
        } finally {
            mongoClient.close();
        }
    }
}
