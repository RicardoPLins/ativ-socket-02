import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Server {
    public static void main(String[] args) throws Exception {
        System.out.println("== Servidor NFS ==");
        ServerSocket serverSocket = new ServerSocket(6000);

        while (true) {
            Socket socket = serverSocket.accept();
            Thread thread = new Thread(() -> {
                try {
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    DataInputStream dis = new DataInputStream(socket.getInputStream());

                    while (true) {
                        String mensagemRecebida = dis.readUTF();
                        System.out.println(socket.getInetAddress()
                                + ":" + socket.getPort()
                                + "-" + mensagemRecebida);
                        if (mensagemRecebida.startsWith("readdir")) {
                            StringTokenizer strToken = new StringTokenizer(mensagemRecebida, " ");
                            strToken.nextToken();
                            File dir = new File(strToken.nextToken());
                            File[] files = dir.listFiles();
                            StringBuilder resposta = new StringBuilder(); 
                            if (files != null) { 
                                for (File file : files) {
                                    resposta.append(" ").append(file.getName());
                                }
                            } else {
                                resposta.append("O diretório está vazio ou não existe.");
                            }
                            dos.writeUTF(resposta.toString());
                        }
                        if (mensagemRecebida.startsWith("rename")) {
                            StringTokenizer strToken = new StringTokenizer(mensagemRecebida, " ");
                            strToken.nextToken(); 
                            String nomeAntigo = strToken.nextToken(); 
                            String novoNome = strToken.nextToken(); 
                            File arquivoAntigo = new File(nomeAntigo);
                            File arquivoNovo = new File(novoNome);
                            if (arquivoAntigo.exists() && arquivoAntigo.isFile()) { 
                                if (arquivoAntigo.renameTo(arquivoNovo)) {
                                    dos.writeUTF("Arquivo renomeado com sucesso para " + novoNome);
                                } else {
                                    dos.writeUTF("Falha ao renomear o arquivo.");
                                }
                            } else {
                                dos.writeUTF("Arquivo antigo não encontrado ou não é um arquivo regular.");
                            }
                        }
                        if (mensagemRecebida.startsWith("create")) {
                            StringTokenizer strToken = new StringTokenizer(mensagemRecebida, " ");
                            strToken.nextToken(); 
                            String nomeArquivo = strToken.nextToken();
                            File novoArquivo = new File(nomeArquivo);
                            try {
                                if (novoArquivo.createNewFile()) {
                                    dos.writeUTF("Arquivo criado com sucesso.");
                                } else {
                                    dos.writeUTF("O arquivo já existe.");
                                }
                            } catch (IOException e) {
                                dos.writeUTF("Erro ao criar o arquivo: " + e.getMessage());
                            }
                        }
                        if (mensagemRecebida.startsWith("remove")) {
                            StringTokenizer strToken = new StringTokenizer(mensagemRecebida, " ");
                            strToken.nextToken(); 
                            String nomeArquivo = strToken.nextToken(); 
                            File arquivo = new File(nomeArquivo);
                            if (arquivo.exists() && arquivo.isFile()) { 
                                if (arquivo.delete()) {
                                    dos.writeUTF("Arquivo removido com sucesso.");
                                } else {
                                    dos.writeUTF("Falha ao remover o arquivo.");
                                }
                            } else {
                                dos.writeUTF("Arquivo não encontrado ou não é um arquivo regular.");
                            }
                        }                      
                                          
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }
}
