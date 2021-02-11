package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;

import br.ce.wcaquino.entidades.Calculadora;

public class CalculadoraMockTest {
	
	@Mock
	private Calculadora calcMock;
	
	@Spy
	private Calculadora calcSpy;
	
	@Before
	public void setUp() {
		initMocks(this);
	}
	
	@Test
	public void deveMostrarDiferencaEntreMockSpy() {
		when(calcMock.somar(1, 2)).thenReturn(8);
		//when(calcSpy.somar(1, 2)).thenReturn(8);
		doReturn(5).when(calcSpy).somar(1, 2);
		doNothing().when(calcSpy).imprime(); 
		
		System.out.println("Mock: " + calcMock.somar(1, 5));
		System.out.println("Spy: " + calcSpy.somar(1, 5));
		
		System.out.println("Mock");
		calcMock.imprime();
		System.out.println("Spy");
		calcSpy.imprime();
	}

	@Test
	public void teste() {
		Calculadora calc = mock(Calculadora.class);
		
		ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
		when(calc.somar(captor.capture(), captor.capture())).thenReturn(5);
		
		assertEquals(5, calc.somar(1, 10000));
	}
	
}
