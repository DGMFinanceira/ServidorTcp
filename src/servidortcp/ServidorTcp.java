package servidortcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ServidorTcp extends Thread {

    private static ServerSocket serverSocket;
    private Socket conexao;
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private static ArrayList<BufferedWriter> clientes;
    private String nome;

    public ServidorTcp(Socket conexao) {
        this.conexao = conexao;
        try {
            inputStream = conexao.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String mensagem;
            OutputStream outputStream = this.conexao.getOutputStream();
            Writer outputStreamWrite = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWrite);
            clientes.add(bufferedWriter);
            nome = mensagem = bufferedReader.readLine();
            while (true) {
                if (mensagem == null || mensagem.indexOf("Desconectado") > 0) {
                    clientes.remove(bufferedWriter);
                } else {
                    try {
                        mensagem = bufferedReader.readLine();
                        enviarParaTodos(bufferedWriter, mensagem);
                    } catch (IOException ex) {
                        Logger.getLogger(ServidorTcp.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServidorTcp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void enviarParaTodos(BufferedWriter bufferedSaida, String mensagem) throws IOException {
        BufferedWriter bufferedWriter;
        if( mensagem.indexOf("Desconectado")> 0 ){
            clientes.remove(bufferedSaida);
        }
        for (BufferedWriter cliente : clientes) {
            bufferedWriter = (BufferedWriter) cliente;
            if (mensagem == null) {
                cliente.write(nome + " Desconectado \n");
            } else {
                if (!(bufferedSaida == bufferedWriter)) {
                    cliente.write(nome + " : " + mensagem + "\n");
                } else {
                    cliente.write("Eu" + " : " + mensagem + "\n");
                }
            }
            cliente.flush();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServidorTcp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServidorTcp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServidorTcp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServidorTcp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        try {
            //Cria os objetos necessário para instânciar o servidor
            JLabel lblMessage = new JLabel("Porta do Servidor:");
            JTextField txtPorta = new JTextField("12345");
            Object[] texts = {lblMessage, txtPorta};
            JOptionPane.showMessageDialog(null, texts);

            serverSocket = new ServerSocket(Integer.parseInt(txtPorta.getText()));
            clientes = new ArrayList<BufferedWriter>();
            JOptionPane.showMessageDialog(null, "Servidor ativo na porta: "
                    + txtPorta.getText());

            while (true) {
                Socket con = serverSocket.accept();
                Thread t = new ServidorTcp(con);
                t.start();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

}
