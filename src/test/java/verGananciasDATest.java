import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import configuration.ConfigXML;
import dataAccess.DataAccess;
import domain.Apuesta;
import domain.Cuenta;
import domain.CuentaAhorro;
import domain.Event;
import domain.Pronostico;
import domain.Question;
import utility.TestUtilityDataAccess;

class verGananciasDATest{

	static DataAccess sut = new DataAccess(ConfigXML.getInstance().getDataBaseOpenMode().equals("initialize"));;
	static TestUtilityDataAccess testDA = new TestUtilityDataAccess(); 

	//Caso en el que le pasamos null 
	@Test
	void test1() {
		CuentaAhorro C0=null;
		assertThrows(NullPointerException.class, ()->sut.verGanancias(C0));
	}
	
	//Caso en el que le pasamos una cuenta que no esta en la base de datos
	@Test
	void test2() {
		Cuenta Jon = new Cuenta("Jon", "1234", false);
		CuentaAhorro C0=new CuentaAhorro(Jon, "CuentaJon");
		assertThrows(IllegalArgumentException.class, ()->sut.verGanancias(C0));
	}
	
	//Usuario sin apuestas
	@Test
	void test3() {
		testDA.open();
		Cuenta Telmo = testDA.anadirUsuario("Telmo", "1234", false);
		CuentaAhorro C3 = testDA.anadirCuentaAhorro(Telmo, "Cuenta 2", 200);
		testDA.close();
		float obtained=sut.verGanancias(C3);
		assertEquals(0, obtained);
		testDA.open();
		testDA.removeUsuario(Telmo);
		testDA.removeCuentaAhorro(C3);
	}
	
	//Usuario con una apuesta ganada y la otra sin cerrar
	@Test
	void test4() throws ParseException {
		//Creamos el usuario y su cuenta de ahorro
		testDA.open();
		Cuenta Telmo = testDA.anadirUsuario("Telmo", "1234", false);
		CuentaAhorro C1 = testDA.anadirCuentaAhorro(Telmo, "Cuenta 1", 100);
		testDA.close();
		
		//Creamos el evento, la pregunta y el pronostico
		Event e1;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date oneDate1 = sdf.parse("05/10/2022");
		String eventText1 = "Event Text1";
		String queryText1 = "Query Text1";
		Float betMinimum1 = 2f;
		float Cuota1 = 1.7f;

		testDA.open();
		e1 = testDA.anadirEventoPreguntaPronostico(eventText1, oneDate1, queryText1, betMinimum1, "Pronostico1", Cuota1);
		testDA.close();
		
		//Creamos las apuestas y las anadimos a la cuenta de ahorro
		
		Question pregunta1=e1.getQuestions().get(0);
		Pronostico pronostico1=pregunta1.getPronosticos().get(0);
		float cantidadApuesta1=10f;
		float cantidadApuesta2=5f;
		
		testDA.open();
		Apuesta apuesta1 = testDA.anadirApuesta(pronostico1, cantidadApuesta1, e1, pregunta1,  Telmo, C1, true, true);
		Apuesta apuesta2 = testDA.anadirApuesta(pronostico1, cantidadApuesta2, e1, pregunta1,  Telmo, C1, false, false);
		testDA.close();
		
		//Calculamos el resultado que deberiamos obtener y lo comparamos con el obtenido
		float obtained = sut.verGanancias(C1);
		float expected = (float) (10*1.7-10);
		assertEquals(expected, obtained);
		
		//Borramos todos los objetos de la BD
		testDA.open();
		testDA.removeCuentaAhorro(C1);
		testDA.removeUsuario(Telmo);
		testDA.removeEvent(e1);
		testDA.removeQuestion(pregunta1);
		testDA.removePronostico(pronostico1);
		testDA.removeApuesta(apuesta1);
		testDA.removeApuesta(apuesta2);
		testDA.close();
	}
	
	//Usuario con una apuesta gada y otra perdida
	@Test
	void test5() throws ParseException {
		//Creamos el usuario y su cuenta de ahorro
		testDA.open();
		Cuenta Pedro = testDA.anadirUsuario("Pedro", "1234", false);
		CuentaAhorro C2 = testDA.anadirCuentaAhorro(Pedro, "Cuenta Principal", 50);
		testDA.close();
		
		//Creamos el evento, la pregunta y el pronostico
		Event e1;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date oneDate1 = sdf.parse("05/10/2022");
		String eventText1 = "Event Text1";
		String queryText1 = "Query Text1";
		Float betMinimum1 = 2f;
		float Cuota1 = 3.0f;

		testDA.open();
		e1 = testDA.anadirEventoPreguntaPronostico(eventText1, oneDate1, queryText1, betMinimum1, "Pronostico1", Cuota1);
		testDA.close();
		
		//Creamos las apuestas y las anadimos a la cuenta de ahorro
		
		Question pregunta1=e1.getQuestions().get(0);
		Pronostico pronostico1=pregunta1.getPronosticos().get(0);
		float cantidadApuesta1=20f;
		float cantidadApuesta2=30f;
		
		testDA.open();
		Apuesta apuesta1 = testDA.anadirApuesta(pronostico1, cantidadApuesta1, e1, pregunta1,  Pedro, C2, true, false);
		Apuesta apuesta2 = testDA.anadirApuesta(pronostico1, cantidadApuesta2, e1, pregunta1,  Pedro, C2, true, true);
		testDA.close();
		
		//Calculamos el resultado que deberiamos obtener y lo comparamos con el obtenido
		float obtained = sut.verGanancias(C2);
		float expected = (float) (-50+30*3);
		assertEquals(expected, obtained);
		
		//Borramos todos los objetos de la BD
		testDA.open();
		testDA.removeCuentaAhorro(C2);
		testDA.removeUsuario(Pedro);
		testDA.removeEvent(e1);
		testDA.removeQuestion(pregunta1);
		testDA.removePronostico(pronostico1);
		testDA.removeApuesta(apuesta1);
		testDA.removeApuesta(apuesta2);
		testDA.close();
	} 
	
	//Usuario con una apuesta ganada
	@Test
	void test6() throws ParseException {
		//Creamos el usuario y su cuenta de ahorro
		testDA.open();
		Cuenta Pedro = testDA.anadirUsuario("Pedro", "1234", false);
		CuentaAhorro C4 = testDA.anadirCuentaAhorro(Pedro, "Cuenta Secundaria", 1000);
		testDA.close();
		
		//Creamos el evento, la pregunta y el pronostico
		Event e1;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date oneDate1 = sdf.parse("05/10/2022");
		String eventText1 = "Event Text1";
		String queryText1 = "Query Text1";
		Float betMinimum1 = 2f;
		float Cuota1 = 2.0f;

		testDA.open();
		e1 = testDA.anadirEventoPreguntaPronostico(eventText1, oneDate1, queryText1, betMinimum1, "Pronostico1", Cuota1);
		testDA.close();
		
		//Creamos las apuestas y las añadimos a la cuenta de ahorro
		
		Question pregunta1=e1.getQuestions().get(0);
		Pronostico pronostico1=pregunta1.getPronosticos().get(0);
		float cantidadApuesta1=20f;
		
		
		testDA.open();
		Apuesta apuesta1 = testDA.anadirApuesta(pronostico1, cantidadApuesta1, e1, pregunta1,  Pedro, C4, true, true);
		testDA.close();
		
		//Calculamos el resultado que deberiamos obtener y lo comparamos con el obtenido
		float obtained = sut.verGanancias(C4);
		float expected = (float) (-20+20*2);
		assertEquals(expected, obtained);
		
		//Borramos todos los objetos de la BD
		testDA.open();
		testDA.removeCuentaAhorro(C4);
		testDA.removeUsuario(Pedro);
		testDA.removeEvent(e1);
		testDA.removeQuestion(pregunta1);
		testDA.removePronostico(pronostico1);
		testDA.removeApuesta(apuesta1);
		testDA.close();
	}

}
