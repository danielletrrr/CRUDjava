package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import com.mongodb.client.*;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import java.io.FileInputStream;

public class GerenciadorArtigos {
    private JFrame frame;
    private JTextField idField, nomeField, descricaoField, buscaField;
    private JTextArea outputArea;
    private JButton criarButton, lerButton, atualizarButton, deletarButton, selecionarArquivoButton, uploadButton, buscarButton, baixarButton;
    private File arquivoSelecionado;
    private static MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public GerenciadorArtigos() {
        // Inicializar conexão com o MongoDB
        mongoClient = MongoClients.create(
            MongoClientSettings.builder()
                .applyToClusterSettings(builder -> 
                    builder.hosts(Arrays.asList(
                        new ServerAddress("localhost", 27017),
                        new ServerAddress("localhost", 27018),
                        new ServerAddress("localhost", 27019)
                    ))
                )
                .build()
        );
        database = mongoClient.getDatabase("gerenciadorArtigosDB");
        collection = database.getCollection("receitas");

        // Configuração da interface gráfica
        frame = new JFrame("Gerenciador de Artigos Científicos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        idField = new JTextField(20); // Campo de texto para ID
        nomeField = new JTextField(20); // Campo de texto para nome
        descricaoField = new JTextField(20); // Campo de texto para descrição
        buscaField = new JTextField(20); // Campo de texto para busca
        outputArea = new JTextArea(10, 30); // Área de texto para saída
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea); // Adiciona barra de rolagem à área de saída

        criarButton = new JButton("Criar"); // Botão para criar receita
        lerButton = new JButton("Ler"); // Botão para ler receita
        atualizarButton = new JButton("Atualizar"); // Botão para atualizar receita
        deletarButton = new JButton("Deletar"); // Botão para deletar receita
        selecionarArquivoButton = new JButton("Selecionar Arquivo"); // Botão para selecionar arquivo
        uploadButton = new JButton("Upload"); // Botão para fazer upload do arquivo
        buscarButton = new JButton("Buscar"); // Botão para buscar por palavras-chave
        baixarButton = new JButton("Baixar"); // Botão para baixar arquivos

        // Adiciona componentes ao frame
        gbc.gridx = 0; gbc.gridy = 0;
        frame.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        frame.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        frame.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        frame.add(nomeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        frame.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        frame.add(descricaoField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        frame.add(new JLabel("Buscar:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        frame.add(buscaField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        frame.add(criarButton, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        frame.add(lerButton, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        frame.add(atualizarButton, gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        frame.add(deletarButton, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        frame.add(selecionarArquivoButton, gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        frame.add(uploadButton, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        frame.add(buscarButton, gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        frame.add(baixarButton, gbc);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        frame.add(scrollPane, gbc);

        // Adicionar ActionListeners aos botões
        criarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                criarArtigo(); // Chama o método para criar artigo quando o botão é clicado
            }
        });

        lerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lerArtigo(); // Chama o método para ler receita quando o botão é clicado
            }
        });

        atualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarArtigo(); // Chama o método para atualizar receita quando o botão é clicado
            }
        });

        deletarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletarArtigo(); // Chama o método para deletar receita quando o botão é clicado
            }
        });

        selecionarArquivoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selecionarArquivo(); // Chama o método para selecionar um arquivo quando o botão é clicado
            }
        });

        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fazerUploadArquivo(); // Chama o método para fazer upload do arquivo quando o botão é clicado
                } catch (IOException ex) {
                    ex.printStackTrace();
                    outputArea.setText("Erro ao fazer upload do arquivo.");
                }
            }
        });

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarReceitas(); // Chama o método para buscar receitas por palavras-chave quando o botão é clicado
                            }
                
                            private void buscarReceitas() {
                                // TODO Auto-generated method stub
                                throw new UnsupportedOperationException("Unimplemented method 'buscarReceitas'");
                            }
        });

        baixarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    baixarArquivo(); // Chama o método para baixar o arquivo quando o botão é clicado
                } catch (IOException ex) {
                    ex.printStackTrace();
                    outputArea.setText("Erro ao baixar o arquivo.");
                }
            }
        });

        frame.pack(); // Ajusta o tamanho da janela de acordo com os componentes
        frame.setVisible(true); // Torna a janela visível
    }

    // Método para criar uma nova receita
    private void criarArtigo() {
        String id = idField.getText(); // Obtém o ID do campo de texto
        String nome = nomeField.getText(); // Obtém o nome do campo de texto
        String descricao = descricaoField.getText(); // Obtém a descrição do campo de texto

        Document document = new Document("_id", id) // Cria um novo documento BSON
                .append("nome", nome) // Adiciona o nome ao documento
                .append("descricao", descricao); // Adiciona a descrição ao documento
        collection.insertOne(document); // Insere o documento na coleção MongoDB
        outputArea.setText("Artigo criado: " + document.toJson()); // Exibe o documento criado na área de saída
    }

    // Método para ler uma receita existente
    private void lerArtigo() {
        String id = idField.getText(); // Obtém o ID do campo de texto
        Document query = new Document("_id", id); // Cria uma consulta BSON com o ID
        Document document = collection.find(query).first(); // Encontra o primeiro documento que corresponde à consulta
        if (document != null) {
            outputArea.setText("Artigo encontrada: " + document.toJson()); // Exibe o documento encontrado na área de saída
        } else {
            outputArea.setText("Nenhum artigo encontrada com ID: " + id); // Exibe uma mensagem se nenhum documento for encontrado
        }
    }

    // Método para atualizar uma receita existente
    private void atualizarArtigo() {
        String id = idField.getText(); // Obtém o ID do campo de texto
        String nome = nomeField.getText(); // Obtém o nome do campo de texto
        String descricao = descricaoField.getText(); // Obtém a descrição do campo de texto

        Document query = new Document("_id", id); // Cria uma consulta BSON com o ID
        Document update = new Document("$set", new Document("nome", nome).append("descricao", descricao)); // Cria um documento BSON com os campos a serem atualizados
        collection.updateOne(query, update); // Atualiza o documento na coleção MongoDB
        outputArea.setText("Artigo atualizada com ID: " + id); // Exibe uma mensagem indicando que a receita foi atualizada
    }

    // Método para deletar uma receita
    private void deletarArtigo() {
        String id = idField.getText(); // Obtém o ID do campo de texto
        Document query = new Document("_id", id); // Cria uma consulta BSON com o ID
        collection.deleteOne(query); // Deleta o documento que corresponde à consulta
        outputArea.setText("Artigo deletado com ID: " + id); // Exibe uma mensagem indicando que a receita foi deletada
    }

    // Método para selecionar um arquivo
    private void selecionarArquivo() {
        JFileChooser fileChooser = new JFileChooser(); // Cria um seletor de arquivos
        int result = fileChooser.showOpenDialog(frame); // Exibe o diálogo para selecionar um arquivo
        if (result == JFileChooser.APPROVE_OPTION) {
            arquivoSelecionado = fileChooser.getSelectedFile(); // Obtém o arquivo selecionado
            outputArea.setText("Arquivo selecionado: " + arquivoSelecionado.getName()); // Exibe o nome do arquivo selecionado na área de saída
        }
    }

    // Método para fazer upload de um arquivo
    private void fazerUploadArquivo() throws IOException {
        if (arquivoSelecionado != null) {
            FileInputStream fileInputStream = new FileInputStream(arquivoSelecionado); // Cria um FileInputStream para ler o arquivo
            byte[] fileData = new byte[(int) arquivoSelecionado.length()]; // Cria um array de bytes para armazenar os dados do arquivo
            fileInputStream.read(fileData); // Lê os dados do arquivo
            fileInputStream.close(); // Fecha o FileInputStream

            Document fileDocument = new Document("nomeArquivo", arquivoSelecionado.getName()) // Cria um documento BSON para o arquivo
                    .append("dadosArquivo", fileData); // Adiciona os dados do arquivo ao documento
            collection.insertOne(fileDocument); // Insere o documento do arquivo na coleção MongoDB
            outputArea.setText("Arquivo enviado: " + arquivoSelecionado.getName()); // Exibe uma mensagem indicando que o arquivo foi enviado
        } else {
            outputArea.setText("Nenhum arquivo selecionado."); // Exibe uma mensagem indicando que nenhum arquivo foi selecionado
        }
    }

    // Método para buscar receitas por palavras-chave
    private void buscarArtigos() {
        String keyword = buscaField.getText(); // Obtém a palavra-chave do campo de texto de busca
        Document query = new Document("$text", new Document("$search", keyword)); // Cria uma consulta de texto BSON
        FindIterable<Document> resultados = collection.find(query); // Encontra documentos que correspondem à consulta

        StringBuilder resultadosStr = new StringBuilder();
        for (Document doc : resultados) {
            resultadosStr.append(doc.toJson()).append("\n"); // Adiciona cada documento encontrado à string de resultados
        }
        outputArea.setText(resultadosStr.toString()); // Exibe os resultados na área de saída
    }

    // Método para baixar um arquivo
    private void baixarArquivo() throws IOException {
        String id = idField.getText(); // Obtém o ID do campo de texto
        System.out.println("Tentando baixar o arquivo com ID: " + id); // Mensagem de depuração
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            outputArea.setText("ID inválido: " + id);
            return;
        }
        Document query = new Document("_id", objectId); // Cria uma consulta BSON com o ID
        Document document = collection.find(query).first(); // Encontra o primeiro documento que corresponde à consulta

        if (document != null) {
            System.out.println("Documento encontrado: " + document.toJson()); // Mensagem de depuração
            if (document.containsKey("dadosArquivo")) {
                byte[] fileData = document.get("dadosArquivo", Binary.class).getData(); // Obtém os dados do arquivo do documento
                String nomeArquivo = document.getString("nomeArquivo"); // Obtém o nome do arquivo do documento

                // Adiciona mensagens de depuração
                System.out.println("Arquivo encontrado: " + nomeArquivo);
                System.out.println("Tamanho do arquivo: " + fileData.length);

                // Cria um seletor de arquivos para escolher onde salvar o arquivo
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(nomeArquivo));
                int userSelection = fileChooser.showSaveDialog(frame);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToSave); // Cria um FileOutputStream para salvar o arquivo
                    fileOutputStream.write(fileData); // Escreve os dados do arquivo
                    fileOutputStream.close(); // Fecha o FileOutputStream
                    outputArea.setText("Arquivo baixado: " + fileToSave.getAbsolutePath()); // Exibe uma mensagem indicando que o arquivo foi baixado
                    System.out.println("Arquivo salvo em: " + fileToSave.getAbsolutePath());
                }
            } else {
                outputArea.setText("Documento encontrado, mas não contém dados de arquivo.");
                System.out.println("Documento encontrado, mas não contém dados de arquivo.");
            }
        } else {
            outputArea.setText("Nenhum arquivo encontrado para o ID: " + id); // Exibe uma mensagem indicando que nenhum arquivo foi encontrado
            System.out.println("Nenhum arquivo encontrado para o ID: " + id);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GerenciadorArtigos app = new GerenciadorArtigos(); // Cria e exibe a janela principal

            // Adiciona um shutdown hook para fechar a conexão com o MongoDB ao encerrar o aplicativo
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (mongoClient != null) {
                    mongoClient.close();
                }
            }));
        });
    }
}


