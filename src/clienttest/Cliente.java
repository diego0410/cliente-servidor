package clienttest;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Cliente extends JFrame{
private JTextField campoTexto;
private JTextArea areaCharla;
private ObjectOutputStream output;
private ObjectInputStream input;
private String mensaje = "";
private String ipServidor;
private Socket cliente;

public Cliente( String host ){

super( "Cliente" );

ipServidor = host;
campoTexto = new JTextField();
campoTexto.setEditable( false );
campoTexto.addActionListener(
new ActionListener(){
public void actionPerformed( ActionEvent event ){
enviarDatos( event.getActionCommand() );
campoTexto.setText( "" );
}
}
);

add( campoTexto, BorderLayout.SOUTH );

areaCharla = new JTextArea();
add( new JScrollPane( areaCharla ), BorderLayout.CENTER );
setSize( 300, 150 );
setVisible( true );
}

public void correrCliente(){
try{
conectarAservidor();
conseguirFlujo();
procesarConexion();
}catch ( EOFException eofException ) {
mostrarMensaje( "nClient terminated connection" );
}catch ( IOException ioException ){
ioException.printStackTrace();
}finally {
cerrarConexion();
}
}

private void conectarAservidor() throws IOException{
mostrarMensaje( "Esperando conectarse" );

cliente = new Socket( InetAddress.getByName( ipServidor ), 5026 );

mostrarMensaje( "Connected to: " + cliente.getInetAddress().getHostName() );
}

private void conseguirFlujo() throws IOException {
output = new ObjectOutputStream( cliente.getOutputStream() );
output.flush();
input = new ObjectInputStream( cliente.getInputStream() );
mostrarMensaje( "nExiste conexion con Servidorn" );
}

private void procesarConexion() throws IOException{
setTextFieldEditable( true );
do{
try{
mensaje = ( String ) input.readObject();
mostrarMensaje( "n" + mensaje );
}catch ( ClassNotFoundException classNotFoundException ){
mostrarMensaje( "nUnknown object type received" );
}

} while ( !mensaje.equals( "SERVER>>> FINITO" ) );
}

private void cerrarConexion(){
mostrarMensaje( "nCerrando la conexion" );
setTextFieldEditable( false );

try{
output.close();
input.close();
cliente.close();
}
catch ( IOException ioException ){
ioException.printStackTrace();
}
}

private void enviarDatos( String sms )
{
try{
output.writeObject( "CLIENTE>>> " + sms );
output.flush();
mostrarMensaje( "nCLIENTE>>> " + sms );
}
catch ( IOException ioException )
{
areaCharla.append( "nNo se puede enviar el mensaje" );
}
}

private void mostrarMensaje( final String mensajeToDisplay ) {
//SwingUtilities.invokeLater(
// new Runnable()
//{
// public void run()
//{
areaCharla.append( mensajeToDisplay );
//}
//}
//);
}

private void setTextFieldEditable( final boolean editable )
{
SwingUtilities.invokeLater(
new Runnable()
{
public void run() // sets campoTexto’s editability
{
campoTexto.setEditable( editable );
}
}
);
}
}
