import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;
import dataAccess.DataAccess;
import domain.CuentaAhorro;
import domain.Question;

class verGananciasBLMockTest {
	
	DataAccess dataAccess = Mockito.mock(DataAccess.class);
	BLFacade sut = new BLFacadeImplementation(dataAccess);
	
	CuentaAhorro mockedCuentaAhorro=Mockito.mock(CuentaAhorro.class);
	
	//Caso le pasamos null
	@Test
	void test1() {
		Mockito.when(dataAccess.verGanancias(null)).thenThrow(NullPointerException.class);
		assertThrows(NullPointerException.class, ()->sut.verGanancias(null));
	}
	
	//Caso en el que le pasamos una cuenta que no esta en la base de datos
	@Test
	void test2() {
		Mockito.when(dataAccess.verGanancias(Mockito.any(CuentaAhorro.class))).thenThrow(IllegalArgumentException.class);
		assertThrows(IllegalArgumentException.class, () -> sut.verGanancias(mockedCuentaAhorro));
	}
	
	//Usuario sin apuestas
	@Test
	void test3() {
		float f = 0f;
		Mockito.when(dataAccess.verGanancias(Mockito.any(CuentaAhorro.class))).thenReturn(f);
		
		assertEquals(0f, sut.verGanancias(mockedCuentaAhorro));
		
		Mockito.verify(dataAccess, Mockito.times(1)).verGanancias(Mockito.any(CuentaAhorro.class));
	}
	
	//Usuario con una apuesta ganada y la otra sin cerrar
	@Test
	void test4() {
		float f = 7f;
		Mockito.when(dataAccess.verGanancias(Mockito.any(CuentaAhorro.class))).thenReturn(f);
		assertEquals(7f, sut.verGanancias(mockedCuentaAhorro));
		
		Mockito.verify(dataAccess, Mockito.times(1)).verGanancias(Mockito.any(CuentaAhorro.class));
	}
	
	//Usuario con una apuesta gada y otra perdida
	@Test
	void test5() {
		float f = 40f;
		Mockito.when(dataAccess.verGanancias(Mockito.any(CuentaAhorro.class))).thenReturn(f);
		assertEquals(40f, sut.verGanancias(mockedCuentaAhorro));
		
		Mockito.verify(dataAccess, Mockito.times(1)).verGanancias(Mockito.any(CuentaAhorro.class));
	}
	
	//Usuario con una apuesta ganada
	@Test
	void test6() {
		float f = 20f;
		Mockito.when(dataAccess.verGanancias(Mockito.any(CuentaAhorro.class))).thenReturn(f);
		assertEquals(20f, sut.verGanancias(mockedCuentaAhorro));
		
		Mockito.verify(dataAccess, Mockito.times(1)).verGanancias(Mockito.any(CuentaAhorro.class));
	}
}
