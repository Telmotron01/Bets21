package utility;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import domain.Apuesta;
import domain.Cuenta;
import domain.CuentaAhorro;
import domain.Event;
import domain.Pronostico;
import domain.Question;

public class TestUtilityDataAccess {
	protected  EntityManager  db;
	protected  EntityManagerFactory emf;

	ConfigXML  c=ConfigXML.getInstance();


	public TestUtilityDataAccess()  {		
		System.out.println("Creating TestDataAccess instance");

		open();		
	}

	
	public void open(){
		
		System.out.println("Opening TestDataAccess instance ");

		String fileName=c.getDbFilename();
		
		if (c.isDatabaseLocal()) {
			  emf = Persistence.createEntityManagerFactory("objectdb:"+fileName);
			  db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			  properties.put("javax.persistence.jdbc.user", c.getUser());
			  properties.put("javax.persistence.jdbc.password", c.getPassword());

			  emf = Persistence.createEntityManagerFactory("objectdb://"+c.getDatabaseNode()+":"+c.getDatabasePort()+"/"+fileName, properties);

			  db = emf.createEntityManager();
    	   }
		
	}
	public void close(){
		db.close();
		System.out.println("DataBase closed");
	}

	public boolean removeEvent(Event ev) {
		System.out.println(">> DataAccessTest: removeEvent");
		Event e = db.find(Event.class, ev.getEventNumber());
		if (e!=null) {
			db.getTransaction().begin();
			db.remove(e);
			db.getTransaction().commit();
			return true;
		} else 
		return false;
    }
		
		public Event addEventWithQuestion(String desc, Date d, String question, float qty) {
			System.out.println(">> DataAccessTest: addEvent");
			Event ev=null;
				db.getTransaction().begin();
				try {
				    ev=new Event(desc,d);
				    ev.addQuestion(question,  qty);
					db.persist(ev);
					db.getTransaction().commit();
				}
				catch (Exception e){
					e.printStackTrace();
				}
				return ev;
	    }
		
		public Vector<Event> getEvents(Date date) {
			System.out.println(">> DataAccess: getEvents");
			Vector<Event> res = new Vector<Event>();	
			TypedQuery<Event> query = db.createQuery("SELECT ev FROM Event ev WHERE ev.eventDate=?1",Event.class);   
			query.setParameter(1, date);
			List<Event> events = query.getResultList();
		 	 for (Event ev:events){
		 	   System.out.println(ev.toString());		 
			   res.add(ev);
			  }
		 	return res;
		}
		
		public boolean existQuestion(Event event, String question) {
			System.out.println(">> DataAccess: existQuestion=> event= "+event+" question= "+question);
			Event ev = db.find(Event.class, event.getEventNumber());
			return ev.DoesQuestionExists(question);
			
		}
		
		public Cuenta añadirUsuario(String usuario, String password, boolean isAdmin) {
			System.out.println(">> DataAccess: añadirUsuario=> usuario= "+usuario+" contraseña= "+password+" es admin= "+isAdmin);
			Cuenta c=null;
			db.getTransaction().begin();
			try {
				c=new Cuenta(usuario, password, isAdmin);
				db.persist(c);
				db.getTransaction().commit();
				
				} catch(Exception e) {
				e.printStackTrace();
			}
			return c;	
		}
		
		public CuentaAhorro añadirCuentaAhorro(Cuenta usuario, String nombre, float fondos) {
			System.out.println(">> DataAccess: añadirCuentaAhorro=> usuario= "+usuario.getNombre()+" nombre= "+nombre+" fondos= "+fondos);
			CuentaAhorro ca=null;
			db.getTransaction().begin();
			try {
				ca=new CuentaAhorro(usuario, nombre);
				ca.setFondos(fondos);
				db.persist(ca);
				db.getTransaction().commit();
			}catch (Exception e) {
				e.printStackTrace();
			}
			return ca;
		}


//		public Pronostico añadirPronostico(String string, float cuota, String queryText) {
//			System.out.println(">> DataAccess: añadirPronostico=> Pronostico= "+string+" cuota= "+cuota+" pregunta= "+queryText);
//			Question q = db.find(Question.class, queryText);
//			Pronostico p=null;
//			db.getTransaction().commit();
//			try {
//				p=q.addPronostico(string, cuota);
//				db.persist(q);
//				db.persist(p);
//				
//				db.getTransaction().commit();
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//			return null;
//		}


		public Event añadirEventoPreguntaPronostico(String eventText, Date oneDate, String queryText,
				Float betMinimum, String string, float cuota) {
				System.out.println(">> DataAccessTest: addEvent");
				Event ev=null;
				db.getTransaction().begin();
					try {
					    ev=new Event(eventText,oneDate);
					    ev.addQuestion(queryText,  betMinimum);
					    Vector<Question> questions = ev.getQuestions();
					    questions.get(0).addPronostico(string, cuota);
						db.persist(ev);
						db.getTransaction().commit();
					}
					catch (Exception e){
						e.printStackTrace();
					}
					
		    
			return ev;
		}
		
		public Apuesta añadirApuesta(Pronostico p, float cantidadApostada, Event e, Question q, Cuenta usuario, CuentaAhorro c, boolean cerrada, boolean ganada) {
			System.out.println(">> DataAccessTest: addApuesta");
			Apuesta a = null;
			Question q1 =db.find(Question.class, q.getQuestionNumber());
			CuentaAhorro ca = db.find(CuentaAhorro.class, c.getCuentaAhorroNumber());
			db.getTransaction().begin();
			try {
				a=q1.addApuesta(p, cantidadApostada, e, q1, usuario, c);
				a.setCerrada(cerrada);
				a.setGanada(ganada);
				ca.addApuesta(a);
				db.persist(a);
				db.persist(ca);
				db.getTransaction().commit();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return a;
		}


		public void removeUsuario(Cuenta c) {
			System.out.println(">> DataAccessTest: removeUsuario");
			Cuenta c1 = db.find(Cuenta.class, c.getUsuario());
			db.getTransaction().begin();
			db.remove(c1);
			db.getTransaction().commit();
		}


		public void removeCuentaAhorro(CuentaAhorro ca) {
			System.out.println(">> DataAccessTest: removeUsuario");
			CuentaAhorro ca1 = db.find(CuentaAhorro.class, ca.getCuentaAhorroNumber());
			db.getTransaction().begin();
			db.remove(ca1);
			db.getTransaction().commit();
		}


		public void removeQuestion(Question pregunta1) {
			System.out.println(">> DataAccessTest: removePregunta");
			Question pregunta = db.find(Question.class, pregunta1.getQuestionNumber());
			db.getTransaction().begin();
			db.remove(pregunta);
			db.getTransaction().commit();
			
		}


		public void removeApuesta(Apuesta apuesta1) {
			System.out.println(">> DataAccessTest: removeApuesta");
			Apuesta a = db.find(Apuesta.class, apuesta1.getApuestaNumber());
			db.getTransaction().begin();
			db.remove(a);
			db.getTransaction().commit();
			
		}


		public void removePronostico(Pronostico pronostico1) {
			System.out.println(">> DataAccessTest: removePronostico");
			Pronostico p = db.find(Pronostico.class, pronostico1.getPronosticoNumber());
			db.getTransaction().begin();
			db.remove(p);
			db.getTransaction().commit();
			
		}
}

